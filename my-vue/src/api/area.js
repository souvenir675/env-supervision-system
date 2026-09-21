import request from './index'

/**
 * 获取所有省份
 */
export function getProvinces() {
    return request({
        url: '/area/provinces',
        method: 'get'
    })
}

/**
 * 根据省份获取城市列表
 */
export function getCitiesByProvince(provinceId) {
    return request({
        url: `/area/cities/${provinceId}`,
        method: 'get'
    })
}