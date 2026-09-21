<template>
    <div class="login-container">
        <div class="login-card">
            <!-- 左侧品牌区域 -->
            <div class="login-left">
                <div class="logo-section">
                    <div class="logo-icon">
                        <el-icon><Odometer /></el-icon>
                    </div>
                    <h1 class="logo-title">东软环保</h1>
                    <p class="logo-sub">公众监督系统</p>
                </div>
                <div class="login-features">
                    <div class="feature-item">
                        <el-icon><Monitor /></el-icon>
                        <span>实时空气质量监测</span>
                    </div>
                    <div class="feature-item">
                        <el-icon><User /></el-icon>
                        <span>全民环保监督参与</span>
                    </div>
                    <div class="feature-item">
                        <el-icon><DataAnalysis /></el-icon>
                        <span>数据驱动决策支持</span>
                    </div>
                </div>
            </div>

            <!-- 右侧登录表单 -->
            <div class="login-right">
                <div class="login-header">
                    <h2>欢迎登录</h2>
                    <p>请选择您的角色类型并输入账号信息</p>
                </div>

                <el-form
                    ref="loginFormRef"
                    :model="loginForm"
                    :rules="rules"
                    label-width="0"
                    class="login-form"
                    size="large"
                >
                    <el-form-item prop="userType">
                        <el-radio-group v-model="loginForm.userType" class="user-type-group">
                            <el-radio-button value="supervisor">
                                <el-icon><User /></el-icon>
                                公众监督员
                            </el-radio-button>
                            <el-radio-button value="grid">
                                <el-icon><Management /></el-icon>
                                网格员
                            </el-radio-button>
                            <el-radio-button value="admin">
                                <el-icon><Setting /></el-icon>
                                管理员
                            </el-radio-button>
                            <el-radio-button value="decision">
                                <el-icon><DataBoard /></el-icon>
                                决策者
                            </el-radio-button>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item prop="account">
                        <el-input
                            v-model="loginForm.account"
                            :placeholder="accountPlaceholder"
                            prefix-icon="User"
                            clearable
                        />
                    </el-form-item>

                    <el-form-item prop="password">
                        <el-input
                            v-model="loginForm.password"
                            type="password"
                            placeholder="请输入密码"
                            prefix-icon="Lock"
                            show-password
                            clearable
                            @keyup.enter="handleLogin"
                        />
                    </el-form-item>

                    <el-form-item>
                        <el-button
                            type="primary"
                            class="login-btn"
                            :loading="loading"
                            @click="handleLogin"
                        >
                            {{ loading ? '登录中...' : '登 录' }}
                        </el-button>
                    </el-form-item>

                    <div v-if="errorMessage" class="error-message">
                        <el-alert :title="errorMessage" type="error" show-icon :closable="true" @close="errorMessage = ''" />
                    </div>

                    <div class="login-footer">
                        <span>还没有账号？</span>
                        <el-link type="primary" @click="goToRegister">立即注册</el-link>
                        <span class="divider">|</span>
                        <el-link type="primary" @click="goToAqiStandard">查看AQI标准</el-link>
                    </div>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store'
import { login } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loading = ref(false)
const errorMessage = ref('')

const loginForm = reactive({
    userType: 'supervisor',
    account: '',  // 预填你的账号方便测试
    password: ''   // 预填你的密码
})

const accountPlaceholder = computed(() => {
    const map = {
        supervisor: '请输入手机号码',
        grid: '请输入网格员登录编码',
        admin: '请输入管理员登录编号',
        decision: '请输入决策者登录编号'
    }
    return map[loginForm.userType] || '请输入账号'
})

const rules = {
    userType: [
        { required: true, message: '请选择用户类型', trigger: 'change' }
    ],
    account: [
        { required: true, message: '请输入账号', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
    ]
}

const handleLogin = async () => {
    if (!loginFormRef.value) return

    errorMessage.value = ''

    try {
        await loginFormRef.value.validate()

        loading.value = true
        console.log('🔐 开始登录:', {
            account: loginForm.account,
            userType: loginForm.userType
        })

        const response = await login({
            account: loginForm.account.trim(),
            password: loginForm.password.trim(),
            userType: loginForm.userType
        })

        console.log('📦 登录响应:', response)

        // 检查响应结构
        // 后端返回: { code: 200, message: 'success', data: { userId, userName, userType, token, expireTime } }
        if (response && response.code === 200) {
            const data = response.data
            if (!data) {
                errorMessage.value = '登录成功但未获取到用户信息'
                return
            }
            
            console.log('✅ 登录成功，用户信息:', data)

            // 保存Token
            if (data.token) {
                localStorage.setItem('token', data.token)
                console.log('Token已保存:', data.token.substring(0, 30) + '...')
            } else {
                errorMessage.value = '未获取到Token'
                return
            }

            // 保存用户信息
            const userInfo = {
                userId: data.userId || loginForm.account,
                userName: data.userName || '用户',
                userType: data.userType || loginForm.userType
            }
            localStorage.setItem('userInfo', JSON.stringify(userInfo))
            
            // 更新store
            userStore.setToken(data.token)
            userStore.setUserInfo(userInfo)

            ElMessage.success('登录成功！欢迎回来，' + userInfo.userName)

            // 根据用户类型跳转
            const routeMap = {
                supervisor: '/feedback/submit',
                grid: '/grid/tasks',
                admin: '/admin/dashboard',
                decision: '/decision/dashboard'
            }
            const targetRoute = routeMap[loginForm.userType] || '/'
            console.log('🚀 跳转到:', targetRoute)
            
            setTimeout(() => {
                router.push(targetRoute)
            }, 500)
        } else {
            errorMessage.value = response?.message || '账号或密码错误，请重新输入'
        }
    } catch (error) {
        console.error('❌ 登录异常:', error)
        if (error.response) {
            console.error('响应状态:', error.response.status)
            console.error('响应数据:', error.response.data)
        }
        errorMessage.value = error.message || '登录失败，请检查网络连接'
    } finally {
        loading.value = false
    }
}

const goToRegister = () => {
    router.push('/register')
}

const goToAqiStandard = () => {
    router.push('/aqi/standard')
}
</script>

<style scoped>
.login-container {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 50%, #a5d6a7 100%);
    padding: 20px;
}

.login-card {
    display: flex;
    width: 1000px;
    max-width: 100%;
    min-height: 580px;
    background: #ffffff;
    border-radius: 24px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    overflow: hidden;
}

.login-left {
    flex: 1;
    padding: 50px 40px;
    background: linear-gradient(135deg, #1b5e20, #2e7d32);
    color: #ffffff;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    min-width: 260px;
}

.logo-section {
    text-align: center;
}

.logo-icon {
    width: 80px;
    height: 80px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 20px;
    font-size: 40px;
    color: #fff;
}

.logo-title {
    font-size: 28px;
    font-weight: 700;
    margin: 0 0 5px;
    letter-spacing: 2px;
}

.logo-sub {
    font-size: 14px;
    opacity: 0.8;
    margin: 0;
    letter-spacing: 4px;
}

.login-features {
    padding: 10px 0;
}

.feature-item {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 12px 0;
    font-size: 15px;
    opacity: 0.9;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.feature-item:last-child {
    border-bottom: none;
}

.feature-item .el-icon {
    font-size: 22px;
}

.login-right {
    flex: 1.2;
    padding: 45px 40px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    min-width: 320px;
}

.login-header {
    margin-bottom: 30px;
}

.login-header h2 {
    font-size: 26px;
    font-weight: 700;
    color: #1b5e20;
    margin: 0 0 6px;
}

.login-header p {
    font-size: 14px;
    color: #999;
    margin: 0;
}

.login-form {
    width: 100%;
}

.user-type-group {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    width: 100%;
}

.user-type-group :deep(.el-radio-button) {
    flex: 1;
    min-width: 90px;
}

.user-type-group :deep(.el-radio-button__inner) {
    width: 100%;
    border-radius: 8px !important;
    border: 1px solid #dcdfe6;
    padding: 8px 12px;
    font-size: 13px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
}

.user-type-group :deep(.el-radio-button__inner .el-icon) {
    font-size: 16px;
}

.user-type-group :deep(.is-active .el-radio-button__inner) {
    background: #2e7d32;
    border-color: #2e7d32;
    color: #fff;
}

.login-form :deep(.el-input__wrapper) {
    border-radius: 12px;
    padding: 4px 15px;
    height: 48px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
    box-shadow: 0 2px 12px rgba(46, 125, 50, 0.12);
}

.login-form :deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 2px rgba(46, 125, 50, 0.2);
}

.login-form :deep(.el-form-item) {
    margin-bottom: 20px;
}

.login-btn {
    width: 100%;
    height: 48px;
    border-radius: 12px;
    font-size: 16px;
    font-weight: 600;
    background: linear-gradient(135deg, #1b5e20, #2e7d32);
    border: none;
    transition: all 0.3s;
}

.login-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(46, 125, 50, 0.35);
}

.login-footer {
    text-align: center;
    font-size: 14px;
    color: #999;
    padding-top: 6px;
}

.login-footer .el-link {
    font-size: 14px;
}

.login-footer .divider {
    margin: 0 10px;
    color: #ddd;
}

.error-message {
    margin-bottom: 16px;
}

@media (max-width: 820px) {
    .login-card {
        flex-direction: column;
        min-height: auto;
    }

    .login-left {
        padding: 30px 25px;
        min-width: auto;
    }

    .login-right {
        padding: 30px 25px;
        min-width: auto;
    }

    .logo-icon {
        width: 60px;
        height: 60px;
        font-size: 30px;
    }

    .logo-title {
        font-size: 22px;
    }

    .login-header h2 {
        font-size: 22px;
    }

    .user-type-group :deep(.el-radio-button) {
        min-width: 70px;
    }

    .user-type-group :deep(.el-radio-button__inner) {
        font-size: 12px;
        padding: 6px 8px;
    }
}
</style>