import request from './index'

/**
 * 管理员注册网格员
 */
export function registerGridMember(data) {
    return request({
        url: '/gridmember/register',
        method: 'post',
        data
    })
}

/**
 * 网格员修改自己的工作状�?
 */
export function updateGridMemberState(data) {
    return request({
        url: '/gridmember/state',
        method: 'put',
        data
    })
}

/**
 * 查询所有网格员
 */
export function getAllGridMembers(params) {
    return request({
        url: '/gridmember/list',
        method: 'get',
        params
    })
}

/**
 * 删除网格�?
 */
export function deleteGridMember(gmId) {
    return request({
        url: `/gridmember/delete/${gmId}`,
        method: 'delete'
    })
}

/**
 * 获取所有可工作的网格员列表
 */
export function getAvailableList() {
    return request({
        url: '/gridmember/available',
        method: 'get'
    })
}

/**
 * 根据区域获取可工作的网格�?
 */
export function getAvailableByArea(provinceId, cityId) {
    return request({
        url: `/gridmember/available/area?provinceId=${provinceId}&cityId=${cityId}`,
        method: 'get'
    })
}

/**
 * 获取网格员详�?
 */
export function getGridMemberDetail(gmId) {
    return request({
        url: `/gridmember/${gmId}`,
        method: 'get'
    })
}