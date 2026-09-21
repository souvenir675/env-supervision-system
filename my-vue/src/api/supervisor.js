import request from './index'

/**
 * 获取监督员信息
 */
export function getSupervisorInfo(telId) {
    return request({
        url: `/supervisor/${telId}`,
        method: 'get'
    })
}

/**
 * 提交反馈
 */
export function submitFeedback(data) {
    return request({
        url: '/feedback/submit',
        method: 'post',
        data
    })
}

/**
 * 获取历史反馈列表
 */
export function getFeedbackHistory(telId) {
    return request({
        url: `/feedback/history/${telId}`,
        method: 'get'
    })
}