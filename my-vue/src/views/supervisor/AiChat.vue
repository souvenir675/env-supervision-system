<template>
  <div class="ai-chat-container">
    <el-card shadow="hover">
      <template #header>
        <div class="chat-header">
          <span>AI 环保小助手</span>
          <el-tag type="success" size="small">DeepSeek</el-tag>
        </div>
      </template>

      <!-- 消息列表 -->
      <div class="message-list" ref="messageListRef">
        <div v-for="(msg, idx) in messages" :key="idx" :class="['message-item', msg.role]">
          <el-avatar :icon="msg.role === 'user' ? 'User' : 'Cpu'" :size="32" />
          <div class="message-content">{{ msg.content }}</div>
        </div>
        <div v-if="loading" class="message-item assistant">
          <el-avatar :icon="'Cpu'" :size="32" />
          <div class="message-content typing">正在思考中...</div>
        </div>
      </div>

      <!-- 输入区 -->
      <div class="input-area">
        <el-input
          v-model="userInput"
          placeholder="请输入你的环保问题，例如：AQI 和 PM2.5 有什么关系？"
          @keyup.enter="sendMessage"
          :disabled="loading"
        />
        <el-button type="primary" @click="sendMessage" :loading="loading">
          发送
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { chatWithAI } from '@/api/ai' // 按实际路径引入

const messages = ref([
  { role: 'system', content: '你是一个环保知识助手，请基于用户提供的空气质量信息回答问题。' },
  { role: 'assistant', content: '你好！我是环保 AI 助手，有什么可以帮你的？' }
])
const userInput = ref('')
const loading = ref(false)
const messageListRef = ref(null)

const sendMessage = async () => {
  const text = userInput.value.trim()
  if (!text || loading.value) return

  // 1. 添加用户消息
  messages.value.push({ role: 'user', content: text })
  userInput.value = ''
  loading.value = true
  await scrollToBottom()

  try {
    // 2. 调用后端代理接口
    const { data } = await chatWithAI(messages.value)
    
    // 3. 提取 AI 回复（适配 OpenAI 格式）
    const aiReply = data.choices?.[0]?.message?.content || '抱歉，我没有理解你的问题。'
    messages.value.push({ role: 'assistant', content: aiReply })
  } catch (error) {
    ElMessage.error('AI 助手暂时无法回复，请稍后重试')
    console.error(error)
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}

const scrollToBottom = async () => {
  await nextTick()
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}
</script>

<style scoped>
.ai-chat-container { max-width: 700px; margin: 0 auto; }
.chat-header { display: flex; justify-content: space-between; align-items: center; }
.message-list { height: 400px; overflow-y: auto; padding: 16px; background: #f9f9f9; border-radius: 8px; margin-bottom: 16px; }
.message-item { display: flex; gap: 12px; margin-bottom: 16px; }
.message-item.user { flex-direction: row-reverse; }
.message-item.user .message-content { background: #e8f5e9; }
.message-content { padding: 10px 16px; border-radius: 12px; background: #fff; border: 1px solid #eee; max-width: 80%; line-height: 1.6; }
.typing { color: #999; font-style: italic; }
.input-area { display: flex; gap: 12px; }
</style>