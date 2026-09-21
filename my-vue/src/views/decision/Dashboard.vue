<template>
    <div class="decision-container">
        <!-- 大屏头部 -->
        <div class="big-screen-header">
            <div class="header-left">
                <div class="logo">
                    <el-icon><Odometer /></el-icon>
                    <span>东软环保公众监督系统</span>
                </div>
            </div>
            <div class="header-center">
                <h1>空气质量数据决策大屏</h1>
                <p>{{ currentTime }}</p>
            </div>
            <div class="header-right">
                <el-tag type="info" size="large">
                    <el-icon><User /></el-icon>
                    {{ userInfo.userName || '决策者' }}
                </el-tag>
                <el-button type="text" @click="handleLogout" style="color: #fff;">
                    <el-icon><SwitchButton /></el-icon>
                </el-button>
            </div>
        </div>

        <!-- Tab切换 -->
        <el-tabs v-model="activeTab" class="decision-tabs" @tab-change="handleTabChange">
            <!-- 数据大屏 -->
            <el-tab-pane label="数据大屏" name="dashboard">
                <div v-if="activeTab === 'dashboard'">
                    <div class="overview-section">
                        <div class="overview-item" v-for="item in overviewData" :key="item.label">
                            <div class="overview-value">{{ item.value }}</div>
                            <div class="overview-label">{{ item.label }}</div>
                            <div class="overview-trend" :class="item.trend > 0 ? 'up' : 'down'">
                                {{ item.trend > 0 ? '↑' : '↓' }} {{ Math.abs(item.trend) }}%
                            </div>
                        </div>
                    </div>

                    <!-- 图表区域 -->
                    <el-row :gutter="20" class="charts-row">
                        <el-col :span="16">
                            <div class="chart-box">
                                <div class="chart-title">各省AQI超标统计</div>
                                <div ref="provinceChartRef" class="chart-container"></div>
                            </div>
                        </el-col>
                        <el-col :span="8">
                            <div class="chart-box">
                                <div class="chart-title">AQI指数分布</div>
                                <div ref="distributionChartRef" class="chart-container"></div>
                            </div>
                        </el-col>
                    </el-row>

                    <el-row :gutter="20" class="charts-row">
                        <el-col :span="12">
                            <div class="chart-box">
                                <div class="chart-title">AQI趋势（近12个月）</div>
                                <div ref="trendChartRef" class="chart-container"></div>
                            </div>
                        </el-col>
                        <el-col :span="12">
                            <el-row :gutter="20">
                                <el-col :span="24">
                                    <div class="chart-box">
                                        <div class="chart-title">实时检测统计</div>
                                        <div class="real-time-stats">
                                            <div class="rt-item" v-for="item in realTimeItems" :key="item.label">
                                                <div class="rt-number" :style="{ color: item.color }">{{ item.value }}</div>
                                                <div class="rt-label">{{ item.label }}</div>
                                            </div>
                                        </div>
                                    </div>
                                </el-col>
                                <el-col :span="24" style="margin-top: 20px;">
                                    <div class="chart-box">
                                        <div class="chart-title">网格覆盖率</div>
                                        <div class="coverage-stats">
                                            <div class="coverage-item">
                                                <span class="coverage-label">省份覆盖率</span>
                                                <el-progress
                                                    :percentage="coverage.provinceCoverage || 0"
                                                    :color="coverage.provinceCoverage >= 80 ? '#67c23a' : '#e6a23c'"
                                                    :stroke-width="20"
                                                />
                                                <span class="coverage-detail">
                                                    {{ coverage.provinceCovered || 0 }} / {{ coverage.provinceTotal || 0 }}
                                                </span>
                                            </div>
                                            <div class="coverage-item" style="margin-top: 16px;">
                                                <span class="coverage-label">城市覆盖率</span>
                                                <el-progress
                                                    :percentage="coverage.cityCoverage || 0"
                                                    :color="coverage.cityCoverage >= 80 ? '#67c23a' : '#e6a23c'"
                                                    :stroke-width="20"
                                                />
                                                <span class="coverage-detail">
                                                    {{ coverage.cityCovered || 0 }} / {{ coverage.cityTotal || 0 }}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </el-col>
                            </el-row>
                        </el-col>
                    </el-row>
                </div>
            </el-tab-pane>

            <!-- 地图视图 -->
            <el-tab-pane label="全国地图" name="map">
                <MapView v-if="activeTab === 'map'" />
            </el-tab-pane>
        </el-tabs>

        <!-- 数据刷新时间 -->
        <div class="refresh-time">
            最后更新时间：{{ lastUpdateTime }}
            <el-button type="text" @click="refreshData" style="margin-left: 12px;">
                <el-icon><Refresh /></el-icon> 刷新数据
            </el-button>
        </div>
        <AiChatDialog />
    </div>
</template>

<script setup>
import AiChatDialog from '@/components/AiChatDialog.vue'
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store'
import MapView from './MapView.vue'
import {
    getProvinceStatistics,
    getAqiDistribution,
    getAqiTrend,
    getRealTimeStatistics,
    getCoverageStatistics
} from '@/api/statistics'
import * as echarts from 'echarts'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const currentTime = ref('')
const lastUpdateTime = ref('')
let timer = null

const provinceChartRef = ref(null)
const distributionChartRef = ref(null)
const trendChartRef = ref(null)
let provinceChart = null
let distributionChart = null
let trendChart = null

const realTime = ref({})
const coverage = ref({})
const provinceData = ref([])
const distributionData = ref([])
const trendData = ref([])

const activeTab = ref('dashboard')

// 概览数据
const overviewData = computed(() => [
    { label: '总检测数', value: realTime.value.totalCount || 0, trend: 5 },
    { label: '良好', value: realTime.value.goodCount || 0, trend: 8 },
    { label: '超标', value: realTime.value.exceedCount || 0, trend: -3 },
    { label: '省份覆盖率', value: `${coverage.value.provinceCoverage || 0}%`, trend: 2 }
])

// 实时统计项
const realTimeItems = computed(() => [
    { label: '检测总量', value: realTime.value.totalCount || 0, color: '#409eff' },
    { label: '良好', value: realTime.value.goodCount || 0, color: '#67c23a' },
    { label: '超标', value: realTime.value.exceedCount || 0, color: '#f56c6c' }
])

// 更新时间
const updateTime = () => {
    const now = new Date()
    currentTime.value = now.toLocaleString('zh-CN', { hour12: false })
    lastUpdateTime.value = now.toLocaleString('zh-CN', { hour12: false })
}

// 销毁所有图表实例
const destroyCharts = () => {
    if (provinceChart) {
        provinceChart.dispose()
        provinceChart = null
    }
    if (distributionChart) {
        distributionChart.dispose()
        distributionChart = null
    }
    if (trendChart) {
        trendChart.dispose()
        trendChart = null
    }
}

// 加载数据
const loadData = async () => {
    try {
        const [provinceRes, distributionRes, trendRes, realRes, coverageRes] = await Promise.all([
            getProvinceStatistics({}),
            getAqiDistribution(),
            getAqiTrend(),
            getRealTimeStatistics(),
            getCoverageStatistics()
        ])

        provinceData.value = provinceRes.data || []
        distributionData.value = distributionRes.data || []
        trendData.value = trendRes.data || []
        realTime.value = realRes.data || {}
        coverage.value = coverageRes.data || {}

        updateTime()
        // 只有当前在数据大屏Tab时才渲染图表
        if (activeTab.value === 'dashboard') {
            renderCharts()
        }
    } catch (error) {
        console.error('加载数据失败:', error)
    }
}

// 刷新数据
const refreshData = () => {
    ElMessage.info('正在刷新数据...')
    loadData()
}

// Tab切换处理
const handleTabChange = (name) => {
    console.log('切换到Tab:', name)
    // 多次延迟触发 resize，确保 DOM 渲染完成
    if (name === 'dashboard') {
        destroyCharts()
        nextTick(() => {
            setTimeout(() => {
                renderCharts()
            }, 100)
        })
    }
}

// 渲染图表
const renderCharts = async () => {
    await nextTick()

    // 省分组统计
    if (provinceChartRef.value) {
        if (!provinceChart || provinceChart.isDisposed()) {
            provinceChart = echarts.init(provinceChartRef.value)
        }
        const data = provinceData.value
        provinceChart.setOption({
            tooltip: { trigger: 'axis' },
            legend: { data: ['SO₂', 'CO', 'PM2.5', 'AQI'], textStyle: { color: '#fff' } },
            grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
            xAxis: {
                type: 'category',
                data: data.map(d => d.provinceName || '未知'),
                axisLine: { lineStyle: { color: '#666' } },
                axisLabel: { color: '#aaa' }
            },
            yAxis: {
                type: 'value',
                name: '超标数量',
                nameTextStyle: { color: '#aaa' },
                splitLine: { lineStyle: { color: '#333' } },
                axisLabel: { color: '#aaa' }
            },
            series: [
                { name: 'SO₂', type: 'bar', data: data.map(d => d.so2ExceedCount || 0), color: '#f56c6c' },
                { name: 'CO', type: 'bar', data: data.map(d => d.coExceedCount || 0), color: '#e6a23c' },
                { name: 'PM2.5', type: 'bar', data: data.map(d => d.spmExceedCount || 0), color: '#409eff' },
                { name: 'AQI', type: 'bar', data: data.map(d => d.aqiExceedCount || 0), color: '#67c23a' }
            ]
        })
        provinceChart.resize()
    }

    // AQI分布
    if (distributionChartRef.value) {
        if (!distributionChart || distributionChart.isDisposed()) {
            distributionChart = echarts.init(distributionChartRef.value)
        }
        const data = distributionData.value
        const colors = data.map(d => d.color || '#999')
        distributionChart.setOption({
            tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
            legend: {
                orient: 'vertical',
                right: '5%',
                top: 'center',
                textStyle: { color: '#aaa' },
                data: data.map(d => d.aqiExplain || '')
            },
            series: [{
                type: 'pie',
                radius: ['40%', '70%'],
                avoidLabelOverlap: true,
                itemStyle: { borderRadius: 8, borderColor: '#1a1a2e', borderWidth: 2 },
                label: { color: '#aaa', formatter: '{b}\n{d}%' },
                data: data.map(d => ({
                    name: d.aqiExplain || `等级${d.aqiId}`,
                    value: d.count || 0,
                    itemStyle: { color: d.color || '#999' }
                }))
            }]
        })
        distributionChart.resize()
    }

    // 趋势统计
    if (trendChartRef.value) {
        if (!trendChart || trendChart.isDisposed()) {
            trendChart = echarts.init(trendChartRef.value)
        }
        const data = trendData.value
        trendChart.setOption({
            tooltip: { trigger: 'axis' },
            grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
            xAxis: {
                type: 'category',
                data: data.map(d => d.month || ''),
                boundaryGap: false,
                axisLine: { lineStyle: { color: '#666' } },
                axisLabel: { color: '#aaa' }
            },
            yAxis: {
                type: 'value',
                name: '超标数量',
                nameTextStyle: { color: '#aaa' },
                splitLine: { lineStyle: { color: '#333' } },
                axisLabel: { color: '#aaa' }
            },
            series: [{
                type: 'line',
                data: data.map(d => d.exceedCount || 0),
                smooth: true,
                areaStyle: { color: 'rgba(46, 125, 50, 0.3)' },
                lineStyle: { color: '#4caf50', width: 3 },
                itemStyle: { color: '#4caf50' }
            }]
        })
        trendChart.resize()
    }
}

// 窗口自适应
const handleResize = () => {
    provinceChart?.resize()
    distributionChart?.resize()
    trendChart?.resize()
}

// 退出登录
const handleLogout = () => {
    userStore.logout()
    router.push('/login')
}

// 定时刷新（每60秒）
const startAutoRefresh = () => {
    timer = setInterval(() => {
        loadData()
    }, 60000)
}

onMounted(() => {
  updateTime()
  loadData()
  startAutoRefresh()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
    if (timer) {
        clearInterval(timer)
    }
    window.removeEventListener('resize', handleResize)
    provinceChart?.dispose()
    distributionChart?.dispose()
    trendChart?.dispose()
})
</script>

<style scoped>
.decision-container {
    min-height: 100vh;
    background: #0a0e27;
    padding: 20px;
    color: #fff;
}

.decision-tabs {
    background: transparent;
}

.decision-tabs :deep(.el-tabs__header) {
    margin-bottom: 20px;
}

.decision-tabs :deep(.el-tabs__nav-wrap::after) {
    background-color: rgba(255, 255, 255, 0.1);
}

.decision-tabs :deep(.el-tabs__item) {
    color: #aaa;
    font-size: 16px;
}

.decision-tabs :deep(.el-tabs__item.is-active) {
    color: #4caf50;
}

.decision-tabs :deep(.el-tabs__active-bar) {
    background-color: #4caf50;
}

/* 大屏头部 */
.big-screen-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 30px;
    background: linear-gradient(135deg, #0f1535, #1a1a4e);
    border-radius: 16px;
    border: 1px solid rgba(255, 255, 255, 0.08);
    margin-bottom: 20px;
}

.header-left .logo {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 18px;
    font-weight: 700;
    color: #4caf50;
}

.header-left .logo .el-icon {
    font-size: 28px;
}

.header-center {
    text-align: center;
}

.header-center h1 {
    font-size: 26px;
    font-weight: 700;
    background: linear-gradient(90deg, #4caf50, #81c784);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    margin: 0 0 4px;
}

.header-center p {
    font-size: 14px;
    color: #888;
    margin: 0;
}

.header-right {
    display: flex;
    align-items: center;
    gap: 16px;
}

/* 概览区域 */
.overview-section {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 20px;
}

.overview-item {
    background: linear-gradient(135deg, #0f1535, #1a1a4e);
    padding: 20px 24px;
    border-radius: 16px;
    border: 1px solid rgba(255, 255, 255, 0.06);
}

.overview-value {
    font-size: 32px;
    font-weight: 700;
    color: #4caf50;
}

.overview-label {
    font-size: 14px;
    color: #888;
    margin-top: 4px;
}

.overview-trend {
    font-size: 13px;
    margin-top: 6px;
}

.overview-trend.up {
    color: #67c23a;
}

.overview-trend.down {
    color: #f56c6c;
}

/* 图表区域 */
.charts-row {
    margin-bottom: 20px;
}

.chart-box {
    background: linear-gradient(135deg, #0f1535, #1a1a4e);
    border-radius: 16px;
    padding: 20px;
    border: 1px solid rgba(255, 255, 255, 0.06);
    height: 100%;
}

.chart-title {
    font-size: 16px;
    font-weight: 600;
    color: #ccc;
    margin-bottom: 16px;
}

.chart-container {
    height: 280px;
    width: 100%;
}

/* 实时统计 */
.real-time-stats {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
    padding: 10px 0;
}

.rt-item {
    text-align: center;
    padding: 16px;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 12px;
}

.rt-number {
    font-size: 30px;
    font-weight: 700;
}

.rt-label {
    font-size: 14px;
    color: #888;
    margin-top: 4px;
}

/* 覆盖率 */
.coverage-stats {
    padding: 10px 0;
}

.coverage-item {
    padding: 4px 0;
}

.coverage-label {
    font-size: 14px;
    color: #aaa;
}

.coverage-detail {
    font-size: 13px;
    color: #666;
    display: block;
    margin-top: 4px;
}

/* 刷新时间 */
.refresh-time {
    text-align: center;
    padding: 16px;
    color: #666;
    font-size: 13px;
    border-top: 1px solid rgba(255, 255, 255, 0.05);
    margin-top: 10px;
}

.refresh-time .el-button {
    color: #4caf50;
}

/* 进度条样式覆盖 */
:deep(.el-progress-bar__outer) {
    background-color: rgba(255, 255, 255, 0.1);
}

:deep(.el-progress__text) {
    color: #aaa;
}

/* 响应式 */
@media (max-width: 992px) {
    .overview-section {
        grid-template-columns: repeat(2, 1fr);
    }

    .big-screen-header {
        flex-direction: column;
        text-align: center;
        gap: 12px;
    }

    .header-center h1 {
        font-size: 20px;
    }
}

@media (max-width: 576px) {
    .overview-section {
        grid-template-columns: 1fr;
    }

    .real-time-stats {
        grid-template-columns: 1fr;
    }
}
</style>