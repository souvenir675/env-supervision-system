import request from './index'

/**
 * 网格员提交实测AQI数据
 */
export function submitAqiData(data) {
    return request({
        url: '/statistics/submit',
        method: 'post',
        data
    })
}

/**
 * 查询AQI确认数据列表
 */
export function getStatisticsList(params) {
    return request({
        url: '/statistics/list',
        method: 'post',
        data: params || {}
    })
}

/**
 * 查询AQI确认数据详情
 */
export function getStatisticsDetail(id) {
    return request({
        url: `/statistics/detail/${id}`,
        method: 'get'
    })
}

/**
 * 省分组检查统计
 */
export function getProvinceStatistics(params) {
    return request({
        url: '/statistics/province',
        method: 'post',
        data: params || {}
    })
}

/**
 * AQI指数分布统计
 */
export function getAqiDistribution() {
    return request({
        url: '/statistics/distribution',
        method: 'get'
    })
}

/**
 * AQI指数趋势统计
 */
export function getAqiTrend() {
    return request({
        url: '/statistics/trend',
        method: 'get'
    })
}

/**
 * 实时统计
 */
export function getRealTimeStatistics() {
    return request({
        url: '/statistics/realtime',
        method: 'get'
    })
}

/**
 * 网格覆盖率统计
 */
export function getCoverageStatistics() {
    return request({
        url: '/statistics/coverage',
        method: 'get'
    })
}

/**
 * 城市分组统计（按省份）
 */
export function getCityStatistics(provinceId) {
    return request({
        url: `/statistics/city/${provinceId}`,
        method: 'get'
    })
}

/**
 * 查询某城市下的监测点
 */
export function getMonitorPoints(provinceId, cityId) {
    return request({
        url: '/statistics/monitor-points',
        method: 'get',
        params: { provinceId, cityId }
    })
}