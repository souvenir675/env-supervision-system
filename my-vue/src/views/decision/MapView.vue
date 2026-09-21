<template>
    <div class="map-view">
        <!-- 视图切换 -->
        <div class="view-toolbar">
            <div class="toolbar-left">
                <span class="toolbar-label">污染物类型：</span>
                <el-radio-group v-model="pollutantType" size="large" @change="handlePollutantChange">
                    <el-radio-button value="aqi">
                        <el-icon><DataLine /></el-icon>
                        AQI综合超标
                    </el-radio-button>
                    <el-radio-button value="spm">
                        <el-icon><Cloudy /></el-icon>
                        PM2.5超标
                    </el-radio-button>
                    <el-radio-button value="so2">
                        <el-icon><Sunny /></el-icon>
                        SO₂超标
                    </el-radio-button>
                    <el-radio-button value="co">
                        <el-icon><Odometer /></el-icon>
                        CO超标
                    </el-radio-button>
                </el-radio-group>
            </div>
            <div class="toolbar-right">
                <el-button
                    v-if="drillLevel !== 'china'"
                    type="warning"
                    @click="backToChina"
                >
                    <el-icon><Back /></el-icon>
                    返回全国
                </el-button>
                <el-button type="primary" @click="loadData">
                    <el-icon><Refresh /></el-icon>
                    刷新
                </el-button>
            </div>
        </div>

        <!-- 当前层级提示 -->
        <div class="breadcrumb-bar">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <el-link @click="backToChina" :underline="false">
                        <el-icon><Location /></el-icon>
                        全国
                    </el-link>
                </el-breadcrumb-item>
                <el-breadcrumb-item v-if="currentProvinceName">
                    {{ currentProvinceName }}
                </el-breadcrumb-item>
            </el-breadcrumb>
            <div class="legend">
                <span class="legend-label">超标数量：</span>
                <div class="legend-item">
                    <span class="legend-color" style="background: #67c23a;"></span>
                    <span>0</span>
                </div>
                <div class="legend-item">
                    <span class="legend-color" style="background: #e6a23c;"></span>
                    <span>1-2</span>
                </div>
                <div class="legend-item">
                    <span class="legend-color" style="background: #f56c6c;"></span>
                    <span>3-5</span>
                </div>
                <div class="legend-item">
                    <span class="legend-color" style="background: #a02020;"></span>
                    <span>5+</span>
                </div>
            </div>
        </div>

        <!-- 地图容器 -->
        <div class="map-container">
            <div ref="mapChartRef" class="map-chart"></div>

            <!-- 城市下钻弹窗 -->
            <el-dialog
                v-model="cityDialogVisible"
                :title="`${currentProvinceName} - 城市AQI超标分布`"
                width="800px"
                :close-on-click-modal="false"
            >
                <el-table :data="cityList" border stripe max-height="400">
                    <el-table-column prop="cityId" label="城市编号" width="100" align="center" />
                    <el-table-column prop="cityName" label="城市名称" width="120" align="center" />
                    <el-table-column prop="so2ExceedCount" label="SO₂超标" width="110" align="center">
                        <template #default="{ row }">
                            <el-tag v-if="row.so2ExceedCount > 0" type="danger" size="small">
                                {{ row.so2ExceedCount }}
                            </el-tag>
                            <el-tag v-else type="success" size="small">0</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="coExceedCount" label="CO超标" width="110" align="center">
                        <template #default="{ row }">
                            <el-tag v-if="row.coExceedCount > 0" type="warning" size="small">
                                {{ row.coExceedCount }}
                            </el-tag>
                            <el-tag v-else type="success" size="small">0</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="spmExceedCount" label="PM2.5超标" width="120" align="center">
                        <template #default="{ row }">
                            <el-tag v-if="row.spmExceedCount > 0" type="danger" size="small">
                                {{ row.spmExceedCount }}
                            </el-tag>
                            <el-tag v-else type="success" size="small">0</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="aqiExceedCount" label="AQI超标" width="110" align="center">
                        <template #default="{ row }">
                            <el-tag v-if="row.aqiExceedCount > 0" type="danger" size="small">
                                {{ row.aqiExceedCount }}
                            </el-tag>
                            <el-tag v-else type="success" size="small">0</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="120" align="center" fixed="right">
                        <template #default="{ row }">
                            <el-button
                                type="primary"
                                size="small"
                                @click="openMonitorPoints(row)"
                            >
                                查看监测点
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <el-empty v-if="cityList.length === 0" description="该省份暂无数据" />
            </el-dialog>

            <!-- 监测点明细弹窗 -->
            <el-dialog
                v-model="monitorDialogVisible"
                :title="`${currentCityName} - 监测点明细`"
                width="900px"
                :close-on-click-modal="false"
            >
                <el-table :data="monitorPoints" border stripe max-height="450">
                    <el-table-column prop="id" label="编号" width="70" align="center" />
                    <el-table-column prop="address" label="地址" min-width="180" show-overflow-tooltip />
                    <el-table-column prop="aqiId" label="AQI等级" width="130" align="center">
                        <template #default="{ row }">
                            <el-tag
                                :color="row.aqiColor || getAqiColor(row.aqiId)"
                                style="color: #fff; border: none"
                            >
                                {{ getAqiLabel(row.aqiId) }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="so2Value" label="SO₂浓度" width="100" align="center">
                        <template #default="{ row }">
                            {{ row.so2Value }} ug/m³
                        </template>
                    </el-table-column>
                    <el-table-column prop="coValue" label="CO浓度" width="100" align="center">
                        <template #default="{ row }">
                            {{ row.coValue }} ug/m³
                        </template>
                    </el-table-column>
                    <el-table-column prop="spmValue" label="PM2.5浓度" width="110" align="center">
                        <template #default="{ row }">
                            {{ row.spmValue }} ug/m³
                        </template>
                    </el-table-column>
                    <el-table-column prop="gmName" label="网格员" width="100" align="center" />
                    <el-table-column prop="confirmDate" label="确认日期" width="110" align="center" />
                </el-table>
                <el-empty v-if="monitorPoints.length === 0" description="该城市暂无监测数据" />
            </el-dialog>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getProvinceStatistics, getCityStatistics, getMonitorPoints } from '@/api/statistics'
import { getAqiList } from '@/api/aqi'
import * as echarts from 'echarts'

const mapChartRef = ref(null)
let mapChart = null

const pollutantType = ref('aqi')
const drillLevel = ref('china') // china | province
const currentProvinceId = ref(null)
const currentProvinceName = ref('')
const currentCityId = ref(null)
const currentCityName = ref('')

const provinceList = ref([])
const cityList = ref([])
const monitorPoints = ref([])
const aqiList = ref([])

const cityDialogVisible = ref(false)
const monitorDialogVisible = ref(false)

// 省份名称映射（GeoJSON中的名称 -> 系统数据库名称）
const provinceNameMap = {
    '北京市': '北京市', '天津市': '天津市', '上海市': '上海市', '重庆市': '重庆市',
    '河北省': '河北省', '山西省': '山西省', '辽宁省': '辽宁省', '吉林省': '吉林省',
    '黑龙江省': '黑龙江省', '江苏省': '江苏省', '浙江省': '浙江省', '安徽省': '安徽省',
    '福建省': '福建省', '江西省': '江西省', '山东省': '山东省', '河南省': '河南省',
    '湖北省': '湖北省', '湖南省': '湖南省', '广东省': '广东省', '海南省': '海南省',
    '四川省': '四川省', '贵州省': '贵州省', '云南省': '云南省', '陕西省': '陕西省',
    '甘肃省': '甘肃省', '青海省': '青海省', '台湾省': '台湾省',
    '内蒙古自治区': '内蒙古自治区', '广西壮族自治区': '广西壮族自治区',
    '西藏自治区': '西藏自治区', '宁夏回族自治区': '宁夏回族自治区',
    '新疆维吾尔自治区': '新疆维吾尔自治区',
    '香港特别行政区': '香港特别行政区', '澳门特别行政区': '澳门特别行政区'
}

// 省份拼音映射（用于加载 GeoJSON）
const provincePinyinMap = {
    '北京市': 'beijing', '天津市': 'tianjin', '上海市': 'shanghai', '重庆市': 'chongqing',
    '河北省': 'hebei', '山西省': 'shanxi', '辽宁省': 'liaoning', '吉林省': 'jilin',
    '黑龙江省': 'heilongjiang', '江苏省': 'jiangsu', '浙江省': 'zhejiang', '安徽省': 'anhui',
    '福建省': 'fujian', '江西省': 'jiangxi', '山东省': 'shandong', '河南省': 'henan',
    '湖北省': 'hubei', '湖南省': 'hunan', '广东省': 'guangdong', '海南省': 'hainan',
    '四川省': 'sichuan', '贵州省': 'guizhou', '云南省': 'yunnan', '陕西省': 'shaanxi',
    '甘肃省': 'gansu', '青海省': 'qinghai', '台湾省': 'taiwan',
    '内蒙古自治区': 'neimenggu', '广西壮族自治区': 'guangxi',
    '西藏自治区': 'xizang', '宁夏回族自治区': 'ningxia',
    '新疆维吾尔自治区': 'xinjiang'
}

// 获取AQI标签
const getAqiLabel = (grade) => {
    const item = aqiList.value.find(a => a.aqiId === grade)
    return item ? `${item.chineseExplain}级-${item.aqiExplain}` : `等级${grade}`
}

const getAqiColor = (grade) => {
    const item = aqiList.value.find(a => a.aqiId === grade)
    return item?.color || '#999'
}

// 加载AQI数据
const loadAqiList = async () => {
    try {
        const { data } = await getAqiList()
        aqiList.value = data || []
    } catch (error) {
        console.error('加载AQI等级失败:', error)
    }
}

// 加载全国数据
const loadChinaData = async () => {
    try {
        const { data } = await getProvinceStatistics({})
        provinceList.value = data || []
        await nextTick()
        renderChinaMap()
    } catch (error) {
        ElMessage.error('加载全国数据失败')
    }
}

// 加载中国地图GeoJSON
const loadChinaGeoJson = async () => {
    try {
        const response = await fetch('/map/china.json')
        return await response.json()
    } catch (error) {
        console.error('加载中国地图失败:', error)
        ElMessage.error('地图数据加载失败，请检查public/map/china.json')
        return null
    }
}

// 加载省份地图GeoJSON
const loadProvinceGeoJson = async (provinceName) => {
    const pinyin = provincePinyinMap[provinceName]
    if (!pinyin) {
        console.error('未找到省份拼音:', provinceName)
        return null
    }
    try {
        const response = await fetch(`/map/${pinyin}.json`)
        return await response.json()
    } catch (error) {
        console.error(`加载${provinceName}地图失败:`, error)
        return null
    }
}

// 根据污染物类型获取值
const getPollutantValue = (item) => {
    switch (pollutantType.value) {
        case 'so2': return item.so2ExceedCount || 0
        case 'co': return item.coExceedCount || 0
        case 'spm': return item.spmExceedCount || 0
        case 'aqi':
        default: return item.aqiExceedCount || 0
    }
}

// 获取污染物名称
const getPollutantName = () => {
    const map = {
        'so2': 'SO₂超标',
        'co': 'CO超标',
        'spm': 'PM2.5超标',
        'aqi': 'AQI综合超标'
    }
    return map[pollutantType.value] || 'AQI超标'
}

// 根据数值获取颜色
const getColorByValue = (value) => {
    if (value === 0) return '#67c23a'
    if (value <= 2) return '#e6a23c'
    if (value <= 5) return '#f56c6c'
    return '#a02020'
}

// 渲染中国地图
const renderChinaMap = async () => {
    const geoJson = await loadChinaGeoJson()
    if (!geoJson) return

    echarts.registerMap('china', geoJson)

    if (!mapChart) {
        mapChart = echarts.init(mapChartRef.value)
    }

    // 构建数据
    const mapData = provinceList.value.map(item => ({
        name: item.provinceName,
        value: getPollutantValue(item),
        provinceId: item.provinceId,
        so2: item.so2ExceedCount || 0,
        co: item.coExceedCount || 0,
        spm: item.spmExceedCount || 0,
        aqi: item.aqiExceedCount || 0
    }))

    const option = {
        tooltip: {
            trigger: 'item',
            backgroundColor: 'rgba(10, 20, 40, 0.9)',
            borderColor: '#4caf50',
            borderWidth: 1,
            textStyle: { color: '#fff' },
            formatter: (params) => {
                if (params.data) {
                    return `
                        <div style="font-weight: bold; color: #4caf50; margin-bottom: 8px;">
                            ${params.name}
                        </div>
                        <div>${getPollutantName()}：<b style="color: #f56c6c;">${params.data.value}</b></div>
                        <div style="font-size: 12px; color: #aaa; margin-top: 6px; border-top: 1px solid #333; padding-top: 6px;">
                            <div>SO₂超标：${params.data.so2}</div>
                            <div>CO超标：${params.data.co}</div>
                            <div>PM2.5超标：${params.data.spm}</div>
                            <div>AQI超标：${params.data.aqi}</div>
                        </div>
                        <div style="font-size: 12px; color: #4caf50; margin-top: 6px;">点击下钻查看城市 →</div>
                    `
                }
                return `<div>${params.name}</div><div style="color:#999;">暂无数据</div>`
            }
        },
        visualMap: {
            min: 0,
            max: 10,
            left: '5%',
            bottom: '8%',
            text: ['高', '低'],
            textStyle: { color: '#fff' },
            calculable: true,
            inRange: {
                color: ['#67c23a', '#95d475', '#e6a23c', '#f56c6c', '#a02020']
            }
        },
        series: [{
            type: 'map',
            map: 'china',
            roam: true,
            zoom: 1.2,
            layoutCenter: ['50%', '50%'],
            layoutSize: '95%',
            label: {
                show: true,
                color: '#fff',
                fontSize: 10
            },
            emphasis: {
                label: { show: true, color: '#fff', fontWeight: 'bold' },
                itemStyle: { areaColor: '#ffd666' }
            },
            itemStyle: {
                areaColor: '#1a3a5c',
                borderColor: '#4caf50',
                borderWidth: 1
            },
            data: mapData
        }]
    }


    mapChart.setOption(option, true)

    // 点击事件
    mapChart.off('click')
    mapChart.on('click', (params) => {
        if (params.data && params.data.provinceId) {
            drillToProvince(params.data.provinceId, params.name)
        } else {
            ElMessage.warning('该省份暂无监测数据')
        }
    })
}

// 下钻到省份
const drillToProvince = async (provinceId, provinceName) => {
    currentProvinceId.value = provinceId
    currentProvinceName.value = provinceName

    try {
        const { data } = await getCityStatistics(provinceId)
        cityList.value = data || []
        cityDialogVisible.value = true
    } catch (error) {
        ElMessage.error('加载城市数据失败')
    }
}

// 打开监测点明细
const openMonitorPoints = async (city) => {
    currentCityId.value = city.cityId
    currentCityName.value = city.cityName

    try {
        const { data } = await getMonitorPoints(currentProvinceId.value, city.cityId)
        monitorPoints.value = data || []
        monitorDialogVisible.value = true
    } catch (error) {
        ElMessage.error('加载监测点数据失败')
    }
}

// 返回全国
const backToChina = () => {
    drillLevel.value = 'china'
    currentProvinceId.value = null
    currentProvinceName.value = ''
    cityDialogVisible.value = false
    monitorDialogVisible.value = false
    renderChinaMap()
}

// 污染物切换
const handlePollutantChange = () => {
    if (drillLevel.value === 'china') {
        renderChinaMap()
    }
}

// 刷新数据
const loadData = async () => {
    if (drillLevel.value === 'china') {
        await loadChinaData()
    } else {
        await drillToProvince(currentProvinceId.value, currentProvinceName.value)
    }
}

// 窗口自适应
const handleResize = () => {
    mapChart?.resize()
}

onMounted(async () => {
    await loadAqiList()
    await loadChinaData()
    window.addEventListener('resize', handleResize)
    
    // 延迟调用 resize 确保容器已渲染
    setTimeout(() => {
        mapChart?.resize()
    }, 300)
})

onBeforeUnmount(() => {
    window.removeEventListener('resize', handleResize)
    mapChart?.dispose()
})
</script>

<style scoped>
.map-view {
    background: #0a0e27;
    border-radius: 16px;
    padding: 20px;
    min-height: 750px;
}

.map-container {
    background: #0a0e27;
    border-radius: 12px;
    padding: 10px;
    min-height: 650px;
    position: relative;
}

.map-chart {
    width: 100%;
    height: 650px;    /* 关键：必须明确高度 */
}

.view-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding: 12px 20px;
    background: linear-gradient(135deg, #0f1535, #1a1a4e);
    border-radius: 12px;
    border: 1px solid rgba(255, 255, 255, 0.08);
}

.toolbar-left {
    display: flex;
    align-items: center;
    gap: 12px;
}

.toolbar-label {
    color: #aaa;
    font-size: 14px;
}

.toolbar-right {
    display: flex;
    gap: 12px;
}

.breadcrumb-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 20px;
    background: rgba(15, 21, 53, 0.6);
    border-radius: 10px;
    margin-bottom: 16px;
}

.breadcrumb-bar :deep(.el-breadcrumb__inner) {
    color: #aaa;
}

.breadcrumb-bar :deep(.el-breadcrumb__inner a) {
    color: #4caf50;
}

.breadcrumb-bar :deep(.el-breadcrumb__separator) {
    color: #666;
}

.legend {
    display: flex;
    align-items: center;
    gap: 16px;
}

.legend-label {
    color: #aaa;
    font-size: 13px;
}

.legend-item {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #ccc;
    font-size: 13px;
}

.legend-color {
    width: 16px;
    height: 16px;
    border-radius: 3px;
    display: inline-block;
}

.map-container {
    background: #0a0e27;
    border-radius: 12px;
    padding: 10px;
    min-height: 650px;
}

.map-chart {
    width: 100%;
    height: 650px;
}
</style>