import request from './index'

/**
 * 监督员注册
 */
export function register(data) {
    return request({
        url: '/supervisor/register',
        method: 'post',
        data
    })
}

/**
 * 统一登录
 */
export function login(data) {
    return request({
        url: '/auth/login',
        method: 'post',
        data
    })
}