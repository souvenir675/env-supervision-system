import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 30000,
    headers: {
        'Content-Type': 'application/json'
    }
})

// 请求拦截器
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        console.log('📤 请求:', config.method.toUpperCase(), config.url, config.data || '')
        return config
    },
    error => {
        console.error('请求错误:', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        const res = response.data
        console.log('📥 响应:', res)
        
        // 如果返回的是错误码
        if (res.code !== undefined && res.code !== 200) {
            if (res.code === 401) {
                ElMessage.error(res.message || '登录已过期，请重新登录')
                localStorage.removeItem('token')
                localStorage.removeItem('userInfo')
                router.push('/login')
                return Promise.reject(new Error(res.message || '未授权'))
            }
            ElMessage.error(res.message || '请求失败')
            return Promise.reject(new Error(res.message || '请求失败'))
        }
        
        // 正常返回数据
        return res
    },
    error => {
        console.error('❌ 响应错误:', error)
        
        if (!error.response) {
            ElMessage.error('无法连接到服务器，请检查后端是否启动')
            return Promise.reject(new Error('网络连接失败'))
        }
        
        const status = error.response.status
        const data = error.response.data
        
        if (status === 401) {
            ElMessage.error('登录已过期，请重新登录')
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
            router.push('/login')
        } else if (status === 403) {
            ElMessage.error('没有权限访问该资源')
        } else if (status === 404) {
            ElMessage.error('请求资源不存在')
        } else if (status === 500) {
            ElMessage.error(data?.message || '服务器内部错误')
        } else {
            ElMessage.error(data?.message || '请求失败')
        }
        return Promise.reject(error)
    }
)

export default request