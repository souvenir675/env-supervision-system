import request from './index'

/**
 * 获取所有AQI等级列表
 */
export function getAqiList() {
    return request({
        url: '/aqi/list',
        method: 'get'
    })
}

/**
 * 获取AQI等级详情
 */
export function getAqiDetail(aqiId) {
    return request({
        url: `/aqi/${aqiId}`,
        method: 'get'
    })
}

/**
 * 计算AQI等级
 */
export function calculateAqi(so2Value, coValue, spmValue) {
    return request({
        url: `/aqi/calculate?so2Value=${so2Value}&coValue=${coValue}&spmValue=${spmValue}`,
        method: 'get'
    })
}