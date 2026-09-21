// router/index.js - 补充完整路由

import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    // 认证相关
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/auth/Login.vue'),
        meta: { title: '登录', noAuth: true }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/auth/Register.vue'),
        meta: { title: '监督员注册', noAuth: true }
    },
    // 监督员端
    {
        path: '/feedback/submit',
        name: 'FeedbackSubmit',
        component: () => import('@/views/supervisor/FeedbackSubmit.vue'),
        meta: { title: '提交反馈', requiresAuth: true, userType: 'supervisor' }
    },
    {
        path: '/feedback/history',
        name: 'FeedbackHistory',
        component: () => import('@/views/supervisor/FeedbackHistory.vue'),
        meta: { title: '反馈历史', requiresAuth: true, userType: 'supervisor' }
    },
    // 网格员端
    {
        path: '/grid/tasks',
        name: 'GridTasks',
        component: () => import('@/views/grid/TaskList.vue'),
        meta: { title: '我的任务', requiresAuth: true, userType: 'grid' }
    },
    // 管理员端
    {
        path: '/admin/dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '管理仪表板', requiresAuth: true, userType: 'admin' }
    },
    // 决策者端
    {
        path: '/decision/dashboard',
        name: 'DecisionDashboard',
        component: () => import('@/views/decision/Dashboard.vue'),
        meta: { title: '决策大屏', requiresAuth: true, userType: 'decision' }
    },
    // AQI标准（公开）
    {
        path: '/aqi/standard',
        name: 'AqiStandard',
        component: () => import('@/views/aqi/AqiStandard.vue'),
        meta: { title: 'AQI标准', noAuth: true }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    document.title = to.meta.title ? `东软环保 - ${to.meta.title}` : '东软环保公众监督系统'

    const token = localStorage.getItem('token')
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

    // 需要认证的页面
    if (to.meta.requiresAuth) {
        if (!token) {
            next('/login')
            return
        }
        // 检查用户类型
        if (to.meta.userType && to.meta.userType !== userInfo.userType) {
            next('/login')
            return
        }
    }

    // 已登录用户访问登录/注册页面，跳转到对应首页
    if (token && (to.path === '/login' || to.path === '/register')) {
        const routeMap = {
            supervisor: '/feedback/submit',
            grid: '/grid/tasks',
            admin: '/admin/dashboard',
            decision: '/decision/dashboard'
        }
        next(routeMap[userInfo.userType] || '/')
        return
    }

    next()
})

export default router