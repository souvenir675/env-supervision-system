import request from './index'

/**
 * 分页查询反馈列表
 */
export function getFeedbackList(params) {
    return request({
        url: '/feedback/list',
        method: 'post',
        data: params
    })
}

/**
 * 获取反馈详情
 */
export function getFeedbackDetail(afId) {
    return request({
        url: `/feedback/detail/${afId}`,
        method: 'get'
    })
}

/**
 * 获取未指派的反馈列表
 */
export function getUnassignedList() {
    return request({
        url: '/feedback/unassigned',
        method: 'get'
    })
}

/**
 * 指派网格员
 */
export function assignGridMember(afId, gmId) {
    return request({
        url: '/feedback/assign',
        method: 'post',
        params: { afId, gmId }
    })
}

/**
 * 获取网格员任务列表
 */
export function getTasksByGridMember(gmId) {
    return request({
        url: `/feedback/tasks/${gmId}`,
        method: 'get'
    })
}

/**
 * 确认反馈
 */
export function confirmFeedback(afId) {
    return request({
        url: `/feedback/confirm/${afId}`,
        method: 'post'
    })
}

/**
 * 修改反馈信息
 */
export function updateFeedback(data) {
    return request({
        url: '/feedback/update',
        method: 'put',
        data
    })
}

/**
 * 删除反馈信息
 */
export function deleteFeedback(afId, telId) {
    return request({
        url: `/feedback/delete/${afId}`,
        method: 'delete',
        params: { telId }
    })
}