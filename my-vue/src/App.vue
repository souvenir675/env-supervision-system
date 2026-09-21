<template>
    <div id="app">
        <!-- 导航栏（简单布局，可根据需要扩展） -->
        <div class="app-nav" v-if="showNav">
            <div class="nav-container">
                <div class="nav-logo" @click="goHome">
                    <el-icon><Odometer /></el-icon>
                    <span>东软环保监督</span>
                </div>
                <div class="nav-menu">
                    <el-menu
                        :default-active="activeMenu"
                        mode="horizontal"
                        router
                        :ellipsis="false"
                    >
                        <el-menu-item index="/feedback/submit" v-if="isSupervisor">
                            <el-icon><Edit /></el-icon>
                            提交反馈
                        </el-menu-item>
                        <el-menu-item index="/feedback/history" v-if="isSupervisor">
                            <el-icon><List /></el-icon>
                            反馈历史
                        </el-menu-item>
                        <el-menu-item index="/aqi/standard">
                            <el-icon><DataLine /></el-icon>
                            AQI标准
                        </el-menu-item>
                    </el-menu>
                </div>
                <div class="nav-user" v-if="isLoggedIn">
                    <el-dropdown @command="handleCommand">
                        <span class="user-info">
                            <el-avatar :size="32" icon="User" />
                            <span class="username">{{ userInfo.realName || userInfo.userName || '用户' }}</span>
                            <el-icon><ArrowDown /></el-icon>
                        </span>
                        <template #dropdown>
                            <el-dropdown-menu>
                                <el-dropdown-item command="logout">
                                    <el-icon><SwitchButton /></el-icon>
                                    退出登录
                                </el-dropdown-item>
                            </el-dropdown-menu>
                        </template>
                    </el-dropdown>
                </div>
                <div class="nav-actions" v-else>
                    <el-button type="text" @click="goToRegister">注册</el-button>
                    <el-button type="primary" @click="goToLogin">登录</el-button>
                </div>
            </div>
        </div>

        <!-- 主内容 -->
        <div class="app-main">
            <router-view />
        </div>
    </div>
</template>

<script setup>
import { computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store' 

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn())
const userInfo = computed(() => userStore.userInfo)
const isSupervisor = computed(() => userInfo.value?.userType === 'supervisor')

// 是否显示导航栏
const showNav = computed(() => {
    const noNavRoutes = ['/register', '/login']
    return !noNavRoutes.includes(route.path)
})

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 跳转到注册
const goToRegister = () => {
    router.push('/register')
}

// 跳转到登录
const goToLogin = () => {
    router.push('/login')
}

// 跳转到首页
const goHome = () => {
    if (isSupervisor.value) {
        router.push('/feedback/submit')
    } else {
        router.push('/aqi/standard')
    }
}

// 退出登录
const handleCommand = (command) => {
    if (command === 'logout') {
        ElMessageBox.confirm('确认退出登录吗？', '提示', {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            userStore.logout()
            router.push('/register')
        }).catch(() => {})
    }
}
</script>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

#app {
    min-height: 100vh;
    background: #f5f7fa;
    font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
}

.app-nav {
    position: sticky;
    top: 0;
    z-index: 100;
    background: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.nav-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    align-items: center;
    height: 64px;
}

.nav-logo {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 20px;
    font-weight: 700;
    color: #2e7d32;
    cursor: pointer;
    flex-shrink: 0;
}

.nav-logo .el-icon {
    font-size: 28px;
}

.nav-menu {
    flex: 1;
    margin-left: 40px;
}

.nav-menu :deep(.el-menu) {
    border-bottom: none;
    background: transparent;
}

.nav-menu :deep(.el-menu-item) {
    height: 64px;
    line-height: 64px;
    border-bottom: 3px solid transparent;
}

.nav-menu :deep(.el-menu-item.is-active) {
    color: #2e7d32;
    border-bottom-color: #2e7d32;
}

.nav-menu :deep(.el-menu-item:hover) {
    background: transparent;
    color: #2e7d32;
}

.nav-user {
    flex-shrink: 0;
    margin-left: 20px;
}

.user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 12px;
    border-radius: 20px;
    transition: background 0.3s;
}

.user-info:hover {
    background: #f0f0f0;
}

.username {
    font-size: 14px;
    color: #333;
}

.nav-actions {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-shrink: 0;
}

.app-main {
    min-height: calc(100vh - 64px);
    padding-bottom: 40px;
}

/* 全局样式 */
.el-button--primary {
    background-color: #2e7d32;
    border-color: #2e7d32;
}

.el-button--primary:hover {
    background-color: #388e3c;
    border-color: #388e3c;
}

.el-button--primary.is-disabled {
    background-color: #a5d6a7;
    border-color: #a5d6a7;
}

.el-link.el-link--primary {
    color: #2e7d32;
}

.el-link.el-link--primary:hover {
    color: #388e3c;
}

.el-tag--success {
    background-color: #e8f5e9;
    border-color: #c8e6c9;
    color: #2e7d32;
}
</style>