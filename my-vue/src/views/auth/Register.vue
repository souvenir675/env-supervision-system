<template>
    <div class="register-container">
        <div class="register-card">
            <!-- 左侧Logo区域 -->
            <div class="register-left">
                <div class="logo-section">
                    <div class="logo-icon">
                        <el-icon><Odometer /></el-icon>
                    </div>
                    <h1 class="logo-title">东软环保</h1>
                    <p class="logo-sub">公众监督系统</p>
                </div>
                <div class="register-info">
                    <p>🌿 共建绿色家园</p>
                    <p>📢 人人都是环保监督员</p>
                </div>
            </div>

            <!-- 右侧注册表单 -->
            <div class="register-right">
                <div class="register-header">
                    <h2>公众监督员注册</h2>
                    <p>加入环保监督，共创美好环境</p>
                </div>

                <el-form
                    ref="registerFormRef"
                    :model="registerForm"
                    :rules="rules"
                    label-width="0"
                    class="register-form"
                    size="large"
                >
                    <el-form-item prop="telId">
                        <el-input
                            v-model="registerForm.telId"
                            placeholder="请输入手机号码"
                            prefix-icon="Phone"
                            maxlength="11"
                            clearable
                        />
                    </el-form-item>

                    <el-form-item prop="password">
                        <el-input
                            v-model="registerForm.password"
                            type="password"
                            placeholder="请设置登录密码（6-20位）"
                            prefix-icon="Lock"
                            show-password
                            clearable
                        />
                    </el-form-item>

                    <el-form-item prop="confirmPassword">
                        <el-input
                            v-model="registerForm.confirmPassword"
                            type="password"
                            placeholder="请确认登录密码"
                            prefix-icon="Lock"
                            show-password
                            clearable
                        />
                    </el-form-item>

                    <el-form-item prop="realName">
                        <el-input
                            v-model="registerForm.realName"
                            placeholder="请输入真实姓名"
                            prefix-icon="User"
                            clearable
                        />
                    </el-form-item>

                    <el-form-item prop="birthday">
                        <el-date-picker
                            v-model="registerForm.birthday"
                            type="date"
                            placeholder="请选择出生日期"
                            format="YYYY-MM-DD"
                            value-format="YYYY-MM-DD"
                            style="width: 100%"
                            prefix-icon="Calendar"
                        />
                    </el-form-item>

                    <el-form-item prop="sex">
                        <el-radio-group v-model="registerForm.sex">
                            <el-radio :value="1">男</el-radio>
                            <el-radio :value="0">女</el-radio>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item>
                        <el-button
                            type="primary"
                            class="register-btn"
                            :loading="loading"
                            @click="handleRegister"
                        >
                            {{ loading ? '注册中...' : '立即注册' }}
                        </el-button>
                    </el-form-item>

                    <div class="register-footer">
                        <span>已有账号？</span>
                        <el-link type="primary" @click="goToLogin">去登录</el-link>
                    </div>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'

const router = useRouter()
const registerFormRef = ref()
const loading = ref(false)

const registerForm = reactive({
    telId: '',
    password: '',
    confirmPassword: '',
    realName: '',
    birthday: '',
    sex: 1
})

// 校验手机号
const validateTelId = (rule, value, callback) => {
    const telRegex = /^1[3-9]\d{9}$/
    if (!value) {
        callback(new Error('请输入手机号码'))
    } else if (!telRegex.test(value)) {
        callback(new Error('请输入正确的手机号码格式'))
    } else {
        callback()
    }
}

// 校验密码
const validatePassword = (rule, value, callback) => {
    if (!value) {
        callback(new Error('请输入密码'))
    } else if (value.length < 6 || value.length > 20) {
        callback(new Error('密码长度应在6-20位之间'))
    } else {
        callback()
    }
}

// 校验确认密码
const validateConfirmPassword = (rule, value, callback) => {
    if (!value) {
        callback(new Error('请确认密码'))
    } else if (value !== registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
    } else {
        callback()
    }
}

// 校验姓名
const validateRealName = (rule, value, callback) => {
    if (!value) {
        callback(new Error('请输入真实姓名'))
    } else if (value.length > 20) {
        callback(new Error('姓名不能超过20个字符'))
    } else {
        callback()
    }
}

// 校验出生日期
const validateBirthday = (rule, value, callback) => {
    if (!value) {
        callback(new Error('请选择出生日期'))
    } else {
        callback()
    }
}

const rules = {
    telId: [
        { validator: validateTelId, trigger: 'blur' }
    ],
    password: [
        { validator: validatePassword, trigger: 'blur' }
    ],
    confirmPassword: [
        { validator: validateConfirmPassword, trigger: 'blur' }
    ],
    realName: [
        { validator: validateRealName, trigger: 'blur' }
    ],
    birthday: [
        { validator: validateBirthday, trigger: 'change' }
    ],
    sex: [
        { required: true, message: '请选择性别', trigger: 'change' }
    ]
}

const handleRegister = async () => {
    if (!registerFormRef.value) return

    try {
        await registerFormRef.value.validate()

        loading.value = true
        const { data } = await register({
            telId: registerForm.telId,
            password: registerForm.password,
            realName: registerForm.realName,
            birthday: registerForm.birthday,
            sex: registerForm.sex
        })

        if (data) {
            ElMessage.success('注册成功！请登录使用')
            setTimeout(() => {
                goToLogin()
            }, 1500)
        }
    } catch (error) {
        if (error.message) {
            ElMessage.error(error.message)
        }
    } finally {
        loading.value = false
    }
}

const goToLogin = () => {
    router.push('/login')
}
</script>

<style scoped>
.register-container {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 50%, #a5d6a7 100%);
    padding: 20px;
}

.register-card {
    display: flex;
    width: 1000px;
    max-width: 100%;
    min-height: 600px;
    background: #ffffff;
    border-radius: 24px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    overflow: hidden;
}

.register-left {
    flex: 1;
    padding: 50px 40px;
    background: linear-gradient(135deg, #2e7d32, #43a047);
    color: #ffffff;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    min-width: 280px;
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

.register-info {
    padding: 20px 0;
}

.register-info p {
    font-size: 16px;
    opacity: 0.9;
    margin: 8px 0;
    line-height: 1.8;
}

.register-right {
    flex: 1.2;
    padding: 50px 45px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    min-width: 320px;
}

.register-header {
    margin-bottom: 30px;
}

.register-header h2 {
    font-size: 26px;
    font-weight: 700;
    color: #2e7d32;
    margin: 0 0 6px;
}

.register-header p {
    font-size: 14px;
    color: #999;
    margin: 0;
}

.register-form {
    width: 100%;
}

.register-form :deep(.el-input__wrapper) {
    border-radius: 12px;
    padding: 4px 15px;
    height: 48px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    transition: all 0.3s;
}

.register-form :deep(.el-input__wrapper:hover) {
    box-shadow: 0 2px 12px rgba(46, 125, 50, 0.12);
}

.register-form :deep(.el-input__wrapper.is-focus) {
    box-shadow: 0 0 0 2px rgba(46, 125, 50, 0.2);
}

.register-form :deep(.el-form-item) {
    margin-bottom: 20px;
}

.register-form :deep(.el-radio-group) {
    padding-top: 6px;
}

.register-btn {
    width: 100%;
    height: 48px;
    border-radius: 12px;
    font-size: 16px;
    font-weight: 600;
    background: linear-gradient(135deg, #2e7d32, #43a047);
    border: none;
    transition: all 0.3s;
}

.register-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(46, 125, 50, 0.35);
}

.register-footer {
    text-align: center;
    font-size: 14px;
    color: #999;
    padding-top: 10px;
}

.register-footer .el-link {
    font-size: 14px;
}

/* 响应式适配 */
@media (max-width: 820px) {
    .register-card {
        flex-direction: column;
        min-height: auto;
    }

    .register-left {
        padding: 30px 25px;
        min-width: auto;
    }

    .register-right {
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

    .register-header h2 {
        font-size: 22px;
    }
}
</style>