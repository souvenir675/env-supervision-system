<template>
    <div class="aqi-standard-container">
        <!-- 页面头部 -->
        <div class="page-header">
            <div class="header-left">
                <h2>空气质量指数（AQI）标准</h2>
                <p>了解AQI等级划分、污染物浓度限值及健康建议</p>
            </div>
            <div class="header-right">
                <el-tag size="large" type="info">
                    数据来源：国家环境保护标准 HJ 633-2012
                </el-tag>
            </div>
        </div>

        <!-- AQI等级卡片 -->
        <div class="aqi-cards">
            <el-row :gutter="16">
                <el-col :span="4" v-for="item in aqiList" :key="item.aqiId">
                    <div class="aqi-card" :style="{ background: item.color }">
                        <div class="aqi-grade">{{ item.chineseExplain }}级</div>
                        <div class="aqi-label">{{ item.aqiExplain }}</div>
                        <div class="aqi-id">AQI {{ getAqiRange(item.aqiId) }}</div>
                    </div>
                </el-col>
            </el-row>
        </div>

        <!-- AQI详情表格 -->
        <el-card class="aqi-table-card" shadow="hover">
            <template #header>
                <div class="card-header">
                    <span>空气质量指数（AQI）及对应的污染物项目浓度限值表</span>
                </div>
            </template>

            <el-table :data="aqiList" border stripe style="width: 100%">
                <el-table-column prop="aqiId" label="AQI" width="80" align="center">
                    <template #default="{ row }">
                        <span :style="{ color: row.color, fontWeight: 'bold' }">
                            {{ row.aqiId }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="chineseExplain" label="等级" width="100" align="center">
                    <template #default="{ row }">
                        <el-tag :color="row.color" style="color: #fff; border: none; min-width: 40px">
                            {{ row.chineseExplain }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="aqiExplain" label="描述" width="120" />
                <el-table-column label="二氧化硫 SO₂ (ug/m³)" width="180" align="center">
                    <template #default="{ row }">
                        {{ row.so2Range || '-' }}
                    </template>
                </el-table-column>
                <el-table-column label="一氧化碳 CO (ug/m³)" width="180" align="center">
                    <template #default="{ row }">
                        {{ row.coRange || '-' }}
                    </template>
                </el-table-column>
                <el-table-column label="PM2.5 (ug/m³)" width="180" align="center">
                    <template #default="{ row }">
                        {{ row.spmRange || '-' }}
                    </template>
                </el-table-column>
                <el-table-column prop="healthImpact" label="对健康影响" min-width="200" show-overflow-tooltip />
            </el-table>
        </el-card>

        <!-- 健康建议 -->
        <el-card class="health-card" shadow="hover">
            <template #header>
                <div class="card-header">
                    <span>💡 健康防护建议</span>
                </div>
            </template>
            <el-row :gutter="20">
                <el-col :span="8" v-for="item in aqiList" :key="item.aqiId">
                    <div class="health-item" :style="{ borderLeftColor: item.color }">
                        <div class="health-header">
                            <el-tag :color="item.color" style="color: #fff; border: none">
                                {{ item.chineseExplain }}级 - {{ item.aqiExplain }}
                            </el-tag>
                        </div>
                        <div class="health-content">
                            <p><strong>健康影响：</strong>{{ item.healthImpact }}</p>
                            <p><strong>建议措施：</strong>{{ item.takeSteps }}</p>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </el-card>

        <!-- AQI计算说明 -->
        <el-card class="calc-card" shadow="hover">
            <template #header>
                <div class="card-header">
                    <span>🧮 AQI计算方法</span>
                </div>
            </template>
            <div class="calc-content">
                <p class="calc-formula">
                    AQI = <strong>MAX</strong>（ SO₂<sub>AQI</sub>， CO<sub>AQI</sub>， PM2.5<sub>AQI</sub> ）
                </p>
                <p class="calc-desc">
                    空气质量指数（AQI）根据各项污染物的浓度值，分别计算其空气质量分指数（IAQI），
                    然后取其中的最大值作为最终的AQI。当AQI大于50时，分值最大的污染物即为首要污染物。
                </p>
                <div class="calc-example">
                    <h4>计算示例：</h4>
                    <ul>
                        <li>SO₂实测浓度：200 ug/m³ → IAQI = 3级（轻度污染）</li>
                        <li>CO实测浓度：20 ug/m³ → IAQI = 3级（轻度污染）</li>
                        <li>PM2.5实测浓度：100 ug/m³ → IAQI = 3级（轻度污染）</li>
                        <li style="color: #2e7d32; font-weight: bold;">AQI = MAX(3, 3, 3) = 3级（轻度污染）</li>
                    </ul>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAqiList } from '@/api/aqi'

const aqiList = ref([])

// 获取AQI范围
const getAqiRange = (aqiId) => {
    const ranges = {
        1: '0 ~ 50',
        2: '51 ~ 100',
        3: '101 ~ 150',
        4: '151 ~ 200',
        5: '201 ~ 300',
        6: '301+'
    }
    return ranges[aqiId] || ''
}

// 加载AQI数据
const loadAqiList = async () => {
    try {
        const { data } = await getAqiList()
        aqiList.value = data || []
    } catch (error) {
        console.error('加载AQI数据失败:', error)
    }
}

onMounted(() => {
    loadAqiList()
})
</script>

<style scoped>
.aqi-standard-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 20px 24px;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.header-left h2 {
    font-size: 24px;
    font-weight: 700;
    color: #2e7d32;
    margin: 0 0 6px;
}

.header-left p {
    font-size: 14px;
    color: #999;
    margin: 0;
}

.aqi-cards {
    margin-bottom: 24px;
}

.aqi-card {
    padding: 20px 16px;
    border-radius: 12px;
    text-align: center;
    color: #fff;
    transition: transform 0.3s;
    cursor: default;
}

.aqi-card:hover {
    transform: translateY(-4px);
}

.aqi-grade {
    font-size: 20px;
    font-weight: 700;
}

.aqi-label {
    font-size: 16px;
    margin: 4px 0;
    opacity: 0.95;
}

.aqi-id {
    font-size: 13px;
    opacity: 0.8;
    margin-top: 4px;
}

.aqi-table-card,
.health-card,
.calc-card {
    border-radius: 16px;
    margin-bottom: 24px;
}

.aqi-table-card :deep(.el-card__header),
.health-card :deep(.el-card__header),
.calc-card :deep(.el-card__header) {
    padding: 16px 24px;
    border-bottom: 1px solid #f0f0f0;
}

.card-header {
    font-weight: 600;
    font-size: 16px;
    color: #333;
}

.health-item {
    padding: 16px 20px;
    border-left: 4px solid;
    background: #f8f9fa;
    border-radius: 8px;
    height: 100%;
    margin-bottom: 12px;
}

.health-header {
    margin-bottom: 10px;
}

.health-content p {
    margin: 6px 0;
    font-size: 14px;
    line-height: 1.6;
    color: #555;
}

.health-content strong {
    color: #333;
}

.calc-content {
    padding: 10px 0;
}

.calc-formula {
    font-size: 22px;
    color: #2e7d32;
    text-align: center;
    padding: 20px 0;
    background: #f5f7fa;
    border-radius: 12px;
    margin-bottom: 20px;
}

.calc-desc {
    font-size: 15px;
    color: #666;
    line-height: 1.8;
    padding: 0 10px;
}

.calc-example {
    margin-top: 20px;
    padding: 20px 24px;
    background: #f5f7fa;
    border-radius: 12px;
}

.calc-example h4 {
    margin: 0 0 12px;
    color: #333;
}

.calc-example ul {
    margin: 0;
    padding-left: 20px;
}

.calc-example li {
    margin: 6px 0;
    color: #555;
    line-height: 1.6;
}

/* 响应式适配 */
@media (max-width: 768px) {
    .page-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 12px;
    }

    .aqi-cards .el-col {
        margin-bottom: 12px;
    }

    .health-item {
        margin-bottom: 12px;
    }
}
</style>