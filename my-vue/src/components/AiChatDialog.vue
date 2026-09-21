<template>
    <div>
        <!-- 悬浮按钮 -->
        <div class="ai-float-btn" @click="dialogVisible = true" title="AI 环保助手">
            <el-icon :size="26"><ChatDotRound /></el-icon>
            <span class="ai-float-text">AI助手</span>
        </div>

        <!-- 对话弹窗 -->
        <el-dialog
            v-model="dialogVisible"
            title="AI 环保助手"
            width="680px"
            :close-on-click-modal="false"
            class="ai-dialog"
            top="6vh"
        >
            <template #header>
                <div class="ai-dialog-header">
                    <div class="ai-header-left">
                        <el-icon :size="22" color="#4caf50"><ChatDotRound /></el-icon>
                        <span class="ai-title">AI 环保助手</span>
                        <el-tag type="success" size="small">DeepSeek</el-tag>
                    </div>
                    <el-button
                        link
                        type="primary"
                        size="small"
                        @click="clearHistory"
                    >
                        <el-icon><Delete /></el-icon>
                        清空对话
                    </el-button>
                </div>
            </template>

            <!-- 消息区 -->
            <div class="ai-message-list" ref="messageListRef">
                <div
                    v-for="(msg, idx) in messages"
                    :key="idx"
                    :class="['ai-message-item', msg.role]"
                >
                    <div class="ai-avatar">
                        <el-icon v-if="msg.role === 'user'"><User /></el-icon>
                        <el-icon v-else color="#4caf50"><Cpu /></el-icon>
                    </div>
                    <div class="ai-message-content">{{ msg.content }}</div>
                </div>
                <div v-if="loading" class="ai-message-item assistant">
                    <div class="ai-avatar">
                        <el-icon color="#4caf50"><Cpu /></el-icon>
                    </div>
                    <div class="ai-message-content typing">
                        正在思考
                        <span class="dot">.</span>
                        <span class="dot">.</span>
                        <span class="dot">.</span>
                    </div>
                </div>
            </div>

            <!-- 快捷问题（首次进入时显示） -->
            <div v-if="messages.length <= 1" class="ai-quick-questions">
                <div class="quick-title">你可以问我：</div>
                <div class="quick-list">
                    <div
                        v-for="(q, idx) in quickQuestions"
                        :key="idx"
                        class="quick-item"
                        @click="sendQuickQuestion(q)"
                    >
                        {{ q }}
                    </div>
                </div>
            </div>

            <!-- 输入区 -->
            <div class="ai-input-area">
                <el-input
                    v-model="userInput"
                    type="textarea"
                    :rows="2"
                    placeholder="请输入你的环保问题，按 Ctrl+Enter 发送"
                    :disabled="loading"
                    resize="none"
                    @keydown.ctrl.enter.prevent="sendMessage"
                />
                <el-button
                    type="primary"
                    :loading="loading"
                    :disabled="!userInput.trim()"
                    @click="sendMessage"
                    class="ai-send-btn"
                >
                    <el-icon><Promotion /></el-icon>
                    发送
                </el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, nextTick, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store'
import { chatWithAgent } from '@/api/ai'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const userType = computed(() => userInfo.value?.userType || 'supervisor')

const dialogVisible = ref(false)
const userInput = ref('')
const loading = ref(false)
const messageListRef = ref(null)

// 不同角色的欢迎语和快捷问题
const roleConfig = {
    supervisor: {
        welcome: '你好！我是环保AI助手 🌿\n你可以用对话方式让我帮你：\n• 提交空气质量反馈（如："我要在保定市莲池区上报，AQI三级，有雾霾"）\n• 查询你的历史反馈记录',
        quickQuestions: [
            '我要提交反馈：河北省保定市莲池区七一路，AQI三级，有轻度雾霾',
            '帮我查一下我的历史反馈',
            'AQI等级怎么划分？'
        ]
    },
    grid: {
        welcome: '你好！我是网格AI助手 🛠️\n你可以说：\n• "帮我查一下我的任务" \n• "给任务1录入数据，SO₂是261，CO是16，PM2.5是103"',
        quickQuestions: [
            '查询我的任务列表',
            '任务1录入数据：SO₂=261，CO=16，PM2.5=103',
            'AQI是怎么计算的？'
        ]
    },
    admin: {
        welcome: '你好！我是管理AI助手 📊\n你可以说：\n• "把反馈16指派出去"（系统会自动选同地区网格员）\n• "把反馈16指派给13800138001"（指定网格员）\n• "查一下未指派的反馈"\n• "给我看省分组统计"',
        quickQuestions: [
            '查询所有未指派的反馈',
            '把反馈16指派出去',
            '查询可工作的网格员',
            '给我看省分组统计'
        ]
    },
    decision: {
    welcome: '你好！我是决策分析AI助手 📊\n你可以这样问我：\n• "河北省上个月PM2.5超标多少天？"\n• "哪个省的SO₂超标最严重？"\n• "对比一下广东省和江苏省的AQI超标情况"\n• "最近有什么异常数据吗？"\n• "给我看最近12个月的AQI趋势"',
    quickQuestions: [
        '河北省上个月PM2.5超标多少天？',
        '哪个省的SO₂超标最严重？',
        '对比广东省和江苏省的AQI超标情况',
        '最近有哪些异常数据？'
    ]
}
}

const config = computed(() => roleConfig[userType.value] || roleConfig.supervisor)

const messages = ref([
    {
        role: 'system',
        content: '你是一个环保知识助手。'
    },
    {
        role: 'assistant',
        content: config.value.welcome
    }
])

const quickQuestions = computed(() => config.value.quickQuestions)

const sendMessage = async () => {
    const text = userInput.value.trim()
    if (!text || loading.value) return

    messages.value.push({ role: 'user', content: text })
    userInput.value = ''
    loading.value = true
    await scrollToBottom()

    try {
        // 过滤 system 消息（由后端统一注入）
        const sendMessages = messages.value.filter(m => m.role !== 'system')
        const response = await chatWithAgent(
            sendMessages,
            userType.value,
            userInfo.value?.userId     // 新增参数
        )
        
        // 兼容处理
        const data = response.data || response
        const aiReply = data?.content || '处理完成'
        messages.value.push({ role: 'assistant', content: aiReply })
    } catch (error) {
        console.error('AI 对话失败:', error)
        ElMessage.error('AI 助手暂时无法回复，请稍后重试')
    } finally {
        loading.value = false
        await scrollToBottom()
    }
}

// 快捷问题
const sendQuickQuestion = (q) => {
    userInput.value = q
    sendMessage()
}

// 清空对话
const clearHistory = () => {
    messages.value = [
        {
            role: 'system',
            content: '你是一个环保知识助手，帮助公众监督员解答空气质量、AQI、污染物等相关问题。回答要简洁、专业、友好。'
        },
        {
            role: 'assistant',
            content: '对话已清空，有什么可以帮你的吗？'
        }
    ]
}

// 滚动到底部
const scrollToBottom = async () => {
    await nextTick()
    if (messageListRef.value) {
        messageListRef.value.scrollTop = messageListRef.value.scrollHeight
    }
}

// 打开弹窗时滚动到底部
watch(dialogVisible, (val) => {
    if (val) {
        scrollToBottom()
    }
})
</script>

<style scoped>
/* 悬浮按钮 */
.ai-float-btn {
    position: fixed;
    right: 30px;
    bottom: 80px;
    z-index: 2000;
    width: 64px;
    height: 64px;
    border-radius: 50%;
    background: linear-gradient(135deg, #2e7d32, #4caf50);
    color: #fff;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
    transition: all 0.3s;
}

.ai-float-btn:hover {
    transform: translateY(-4px) scale(1.05);
    box-shadow: 0 10px 28px rgba(76, 175, 80, 0.55);
}

.ai-float-text {
    font-size: 10px;
    margin-top: 2px;
    font-weight: 600;
    letter-spacing: 1px;
}

/* 弹窗 */
.ai-dialog :deep(.el-dialog__header) {
    padding: 16px 20px;
    border-bottom: 1px solid #f0f0f0;
    margin-right: 0;
}

.ai-dialog :deep(.el-dialog__body) {
    padding: 0;
}

.ai-dialog-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.ai-header-left {
    display: flex;
    align-items: center;
    gap: 8px;
}

.ai-title {
    font-size: 16px;
    font-weight: 600;
    color: #2e7d32;
}

/* 消息列表 */
.ai-message-list {
    height: 420px;
    overflow-y: auto;
    padding: 20px;
    background: #f7f9fb;
}

.ai-message-list::-webkit-scrollbar {
    width: 6px;
}

.ai-message-list::-webkit-scrollbar-thumb {
    background: #ccc;
    border-radius: 3px;
}

.ai-message-item {
    display: flex;
    gap: 10px;
    margin-bottom: 16px;
}

.ai-message-item.user {
    flex-direction: row-reverse;
}

.ai-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    font-size: 18px;
}

.ai-message-item.user .ai-avatar {
    background: #e8f5e9;
    color: #2e7d32;
}

.ai-message-content {
    max-width: 75%;
    padding: 10px 14px;
    border-radius: 12px;
    background: #fff;
    border: 1px solid #eee;
    line-height: 1.7;
    font-size: 14px;
    color: #333;
    white-space: pre-wrap;
    word-break: break-word;
}

.ai-message-item.user .ai-message-content {
    background: #2e7d32;
    color: #fff;
    border-color: #2e7d32;
}

.ai-message-item.assistant .ai-message-content {
    background: #fff;
}

.typing {
    color: #999;
    font-style: italic;
}

.typing .dot {
    animation: blink 1.4s infinite both;
}

.typing .dot:nth-child(2) {
    animation-delay: 0.2s;
}

.typing .dot:nth-child(3) {
    animation-delay: 0.4s;
}

@keyframes blink {
    0%, 80%, 100% { opacity: 0.2; }
    40% { opacity: 1; }
}

/* 快捷问题 */
.ai-quick-questions {
    padding: 12px 20px;
    background: #fff;
    border-top: 1px solid #f0f0f0;
}

.quick-title {
    font-size: 13px;
    color: #999;
    margin-bottom: 10px;
}

.quick-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}

.quick-item {
    padding: 6px 14px;
    background: #f0f7f0;
    color: #2e7d32;
    border-radius: 16px;
    font-size: 13px;
    cursor: pointer;
    transition: all 0.2s;
}

.quick-item:hover {
    background: #2e7d32;
    color: #fff;
}

/* 输入区 */
.ai-input-area {
    padding: 16px 20px;
    background: #fff;
    border-top: 1px solid #f0f0f0;
    display: flex;
    gap: 12px;
    align-items: flex-end;
}

.ai-input-area :deep(.el-textarea__inner) {
    border-radius: 10px;
    font-size: 14px;
}

.ai-send-btn {
    height: 56px;
    min-width: 90px;
    border-radius: 10px;
    background: linear-gradient(135deg, #2e7d32, #4caf50);
    border: none;
    font-weight: 600;
}

.ai-send-btn:hover {
    background: linear-gradient(135deg, #1b5e20, #388e3c);
}
</style>