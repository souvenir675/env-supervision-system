<template>
    <div class="history-container">
        <!-- 页面头部 -->
        <div class="page-header">
            <div class="header-left">
                <h2>我的反馈历史</h2>
                <p>查看、修改或删除您提交的空气质量监督反馈记录</p>
            </div>
            <div class="header-right">
                <el-button type="primary" @click="goToSubmit">
                    <el-icon><Plus /></el-icon>
                    提交新反馈
                </el-button>
            </div>
        </div>

        <!-- 统计卡片 -->
        <el-row :gutter="20" class="stat-cards">
            <el-col :span="6">
                <el-statistic title="总反馈数" :value="statistics.total">
                    <template #suffix><span style="font-size:14px;color:#999">条</span></template>
                </el-statistic>
            </el-col>
            <el-col :span="6">
                <el-statistic title="未指派" :value="statistics.unassigned" color="#e6a23c">
                    <template #suffix><span style="font-size:14px;color:#999">条</span></template>
                </el-statistic>
            </el-col>
            <el-col :span="6">
                <el-statistic title="已指派" :value="statistics.assigned" color="#409eff">
                    <template #suffix><span style="font-size:14px;color:#999">条</span></template>
                </el-statistic>
            </el-col>
            <el-col :span="6">
                <el-statistic title="已确认" :value="statistics.confirmed" color="#67c23a">
                    <template #suffix><span style="font-size:14px;color:#999">条</span></template>
                </el-statistic>
            </el-col>
        </el-row>

        <!-- 历史列表 -->
        <el-card class="history-card" shadow="hover">
            <template #header>
                <div class="card-header">
                    <span>反馈记录列表</span>
                    <div class="header-actions">
                        <el-input
                            v-model="searchKeyword"
                            placeholder="搜索地址或描述"
                            prefix-icon="Search"
                            clearable
                            style="width: 220px; margin-right: 12px"
                        />
                        <el-select
                            v-model="filterState"
                            placeholder="全部状态"
                            clearable
                            style="width: 140px; margin-right: 12px"
                        >
                            <el-option label="全部" value="" />
                            <el-option label="未指派" value="0" />
                            <el-option label="已指派" value="1" />
                            <el-option label="已确认" value="2" />
                        </el-select>
                        <el-button type="primary" @click="loadHistory">
                            <el-icon><Refresh /></el-icon>
                            刷新
                        </el-button>
                    </div>
                </div>
            </template>

            <el-table :data="filteredList" border stripe v-loading="loading" style="width: 100%">
                <el-table-column prop="afId" label="编号" width="80" align="center" />
                <el-table-column prop="provinceName" label="省份" width="100" />
                <el-table-column prop="cityName" label="城市" width="100" />
                <el-table-column prop="address" label="详细地址" min-width="180" show-overflow-tooltip />
                <el-table-column prop="information" label="反馈描述" min-width="200" show-overflow-tooltip />
                <el-table-column prop="estimatedGrade" label="预估等级" width="120" align="center">
                    <template #default="{ row }">
                        <el-tag
                            :color="getAqiColor(row.estimatedGrade)"
                            style="color: #fff; border: none; min-width: 50px; text-align: center"
                        >
                            {{ getAqiLabel(row.estimatedGrade) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <!-- 实测等级（新增） -->
                <el-table-column prop="measuredGrade" label="实测等级" width="120" align="center">
                    <template #default="{ row }">
                        <el-tag
                            v-if="row.measuredGrade"
                            :color="getAqiColor(row.measuredGrade)"
                            style="color: #fff; border: none; min-width: 50px; text-align: center"
                        >
                            {{ getAqiLabel(row.measuredGrade) }}
                        </el-tag>
                        <span v-else style="color: #999; font-size: 13px;">待检测</span>
                    </template>
                </el-table-column>
                <el-table-column prop="afDate" label="反馈日期" width="120" align="center" />
                <el-table-column prop="state" label="状态" width="100" align="center">
                    <template #default="{ row }">
                        <el-tag :type="getStateType(row.state)">
                            {{ getStateLabel(row.state) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="220" align="center" fixed="right">
                    <template #default="{ row }">
                        <el-button type="text" size="small" @click="viewDetail(row)">查看</el-button>
                        <el-button
                            v-if="row.state === 0"
                            type="primary"
                            size="small"
                            @click="openEditDialog(row)"
                        >
                            修改
                        </el-button>
                        <el-button
                            v-if="row.state === 0"
                            type="danger"
                            size="small"
                            @click="handleDelete(row)"
                        >
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <el-empty v-if="!loading && filteredList.length === 0" description="暂无反馈记录" />
        </el-card>

        <!-- 详情弹窗 -->
        <el-dialog v-model="detailVisible" title="反馈详情" width="600px" :close-on-click-modal="false">
            <div v-if="detailData" class="detail-content">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="反馈编号">{{ detailData.afId }}</el-descriptions-item>
                    <el-descriptions-item label="状态">
                        <el-tag :type="getStateType(detailData.state)">
                            {{ getStateLabel(detailData.state) }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="省份">{{ detailData.provinceName }}</el-descriptions-item>
                    <el-descriptions-item label="城市">{{ detailData.cityName }}</el-descriptions-item>
                    <el-descriptions-item label="详细地址" :span="2">{{ detailData.address }}</el-descriptions-item>
                    <el-descriptions-item label="预估AQI等级" :span="2">
                        <el-tag
                            :color="getAqiColor(detailData.estimatedGrade)"
                            style="color: #fff; border: none"
                        >
                            {{ getAqiLabel(detailData.estimatedGrade) }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="反馈描述" :span="2">{{ detailData.information }}</el-descriptions-item>
                    <el-descriptions-item label="反馈日期">{{ detailData.afDate }}</el-descriptions-item>
                    <el-descriptions-item label="反馈时间">{{ detailData.afTime }}</el-descriptions-item>
                    <el-descriptions-item v-if="detailData.gridMemberName" label="指派网格员">
                        {{ detailData.gridMemberName }}
                    </el-descriptions-item>
                    <el-descriptions-item v-if="detailData.assignDate" label="指派日期">
                        {{ detailData.assignDate }}
                    </el-descriptions-item>
                </el-descriptions>
                <!-- ========== 新增：实测AQI数据展示 ========== -->
                <div v-if="detailData.measuredGrade" class="measured-section">
                    <el-divider content-position="left">
                        <span style="color: #2e7d32; font-weight: 600;">实测AQI数据</span>
                    </el-divider>

                    <el-descriptions :column="2" border>
                        <el-descriptions-item label="实测AQI等级" :span="2">
                            <el-tag
                                :color="getAqiColor(detailData.measuredGrade)"
                                style="color: #fff; border: none; font-size: 14px; padding: 6px 16px;"
                            >
                                {{ getAqiLabel(detailData.measuredGrade) }}
                            </el-tag>
                        </el-descriptions-item>

                        <el-descriptions-item label="SO₂实测浓度">
                            {{ detailData.so2Value }} ug/m³
                            <el-tag
                                v-if="detailData.so2Level"
                                :color="getAqiColor(detailData.so2Level)"
                                size="small"
                                style="color: #fff; border: none; margin-left: 8px;"
                            >
                                {{ getAqiLabel(detailData.so2Level) }}
                            </el-tag>
                        </el-descriptions-item>

                        <el-descriptions-item label="CO实测浓度">
                            {{ detailData.coValue }} ug/m³
                            <el-tag
                                v-if="detailData.coLevel"
                                :color="getAqiColor(detailData.coLevel)"
                                size="small"
                                style="color: #fff; border: none; margin-left: 8px;"
                            >
                                {{ getAqiLabel(detailData.coLevel) }}
                            </el-tag>
                        </el-descriptions-item>

                        <el-descriptions-item label="PM2.5实测浓度">
                            {{ detailData.spmValue }} ug/m³
                            <el-tag
                                v-if="detailData.spmLevel"
                                :color="getAqiColor(detailData.spmLevel)"
                                size="small"
                                style="color: #fff; border: none; margin-left: 8px;"
                            >
                                {{ getAqiLabel(detailData.spmLevel) }}
                            </el-tag>
                        </el-descriptions-item>

                        <el-descriptions-item label="确认时间">
                            {{ detailData.confirmDate }} {{ detailData.confirmTime }}
                        </el-descriptions-item>
                    </el-descriptions>
                </div>
            </div>
            <template #footer>
                <el-button @click="detailVisible = false">关闭</el-button>
            </template>
        </el-dialog>

        <!-- 修改弹窗 -->
        <el-dialog v-model="editVisible" title="修改反馈信息" width="560px" :close-on-click-modal="false">
            <el-form
                ref="editFormRef"
                :model="editForm"
                :rules="editRules"
                label-width="120px"
                label-position="right"
            >
                <el-form-item label="省份" prop="provinceId">
                    <el-select
                        v-model="editForm.provinceId"
                        placeholder="请选择省份"
                        filterable
                        @change="handleEditProvinceChange"
                        style="width: 100%"
                    >
                        <el-option
                            v-for="item in provinceList"
                            :key="item.provinceId"
                            :label="item.provinceName"
                            :value="item.provinceId"
                        />
                    </el-select>
                </el-form-item>

                <el-form-item label="城市" prop="cityId">
                    <el-select
                        v-model="editForm.cityId"
                        placeholder="请选择城市"
                        filterable
                        :disabled="!editForm.provinceId"
                        style="width: 100%"
                    >
                        <el-option
                            v-for="item in editCityList"
                            :key="item.cityId"
                            :label="item.cityName"
                            :value="item.cityId"
                        />
                    </el-select>
                </el-form-item>

                <el-form-item label="详细地址" prop="address">
                    <el-input
                        v-model="editForm.address"
                        placeholder="请输入详细地址"
                        maxlength="200"
                        show-word-limit
                    />
                </el-form-item>

                <el-form-item label="预估AQI等级" prop="estimatedGrade">
                    <el-select v-model="editForm.estimatedGrade" placeholder="请选择AQI等级" style="width: 100%">
                        <el-option
                            v-for="item in aqiList"
                            :key="item.aqiId"
                            :label="`${item.chineseExplain}级 - ${item.aqiExplain}`"
                            :value="item.aqiId"
                        />
                    </el-select>
                </el-form-item>

                <el-form-item label="空气质量描述" prop="information">
                    <el-input
                        v-model="editForm.information"
                        type="textarea"
                        placeholder="请描述空气质量情况"
                        :rows="4"
                        maxlength="400"
                        show-word-limit
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="editVisible = false">取消</el-button>
                <el-button type="primary" :loading="editLoading" @click="handleEditSubmit">
                    {{ editLoading ? '保存中...' : '保存修改' }}
                </el-button>
            </template>
        </el-dialog>
        <AiChatDialog />
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store'
import { getFeedbackHistory } from '@/api/supervisor'
import { getFeedbackDetail, updateFeedback, deleteFeedback } from '@/api/feedback'
import { getAqiList } from '@/api/aqi'
import { getProvinces, getCitiesByProvince } from '@/api/area'
import AiChatDialog from '@/components/AiChatDialog.vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const historyList = ref([])
const aqiList = ref([])
const provinceList = ref([])
const editCityList = ref([])
const searchKeyword = ref('')
const filterState = ref('')
const detailVisible = ref(false)
const detailData = ref(null)

// 修改相关
const editVisible = ref(false)
const editLoading = ref(false)
const editFormRef = ref()
const editForm = reactive({
    afId: null,
    telId: '',
    provinceId: null,
    cityId: null,
    address: '',
    information: '',
    estimatedGrade: null
})

// 统计信息
const statistics = computed(() => {
    const total = historyList.value.length
    const unassigned = historyList.value.filter(item => item.state === 0).length
    const assigned = historyList.value.filter(item => item.state === 1).length
    const confirmed = historyList.value.filter(item => item.state === 2).length
    return { total, unassigned, assigned, confirmed }
})

// 过滤后的列表
const filteredList = computed(() => {
    let list = historyList.value

    if (searchKeyword.value) {
        const keyword = searchKeyword.value.toLowerCase()
        list = list.filter(item =>
            item.address?.toLowerCase().includes(keyword) ||
            item.information?.toLowerCase().includes(keyword)
        )
    }

    if (filterState.value !== '') {
        list = list.filter(item => item.state === parseInt(filterState.value))
    }

    return list
})

// 修改表单校验规则
const editRules = {
    provinceId: [{ required: true, message: '请选择省份', trigger: 'change' }],
    cityId: [{ required: true, message: '请选择城市', trigger: 'change' }],
    address: [
        { required: true, message: '请输入详细地址', trigger: 'blur' },
        { max: 200, message: '地址不能超过200字', trigger: 'blur' }
    ],
    estimatedGrade: [{ required: true, message: '请选择预估AQI等级', trigger: 'change' }],
    information: [
        { required: true, message: '请输入空气质量描述', trigger: 'blur' },
        { max: 400, message: '描述不能超过400字', trigger: 'blur' }
    ]
}

// 加载基础数据（省份、AQI等级）
const loadBaseData = async () => {
    try {
        // 加载省份
        const provinceRes = await getProvinces()
        provinceList.value = provinceRes.data || []
        console.log('省份列表加载成功:', provinceList.value.length, '条')

        // 加载AQI等级
        const aqiRes = await getAqiList()
        aqiList.value = aqiRes.data || []
        console.log('AQI列表加载成功:', aqiList.value.length, '条')
    } catch (error) {
        console.error('加载基础数据失败:', error)
        ElMessage.error('加载基础数据失败')
    }
}

const getAqiLabel = (grade) => {
    const item = aqiList.value.find(a => a.aqiId === grade)
    return item ? `${item.chineseExplain}级-${item.aqiExplain}` : `等级${grade}`
}

const getAqiColor = (grade) => {
    const item = aqiList.value.find(a => a.aqiId === grade)
    return item?.color || '#999'
}

const getStateLabel = (state) => {
    const map = { 0: '未指派', 1: '已指派', 2: '已确认' }
    return map[state] || '未知'
}

const getStateType = (state) => {
    const map = { 0: 'warning', 1: 'primary', 2: 'success' }
    return map[state] || 'info'
}

// 加载历史
const loadHistory = async () => {
    const telId = userStore.userInfo?.userId
    if (!telId) {
        ElMessage.warning('请先登录')
        return
    }

    loading.value = true
    try {
        const { data } = await getFeedbackHistory(telId)
        historyList.value = data || []
    } catch (error) {
        ElMessage.error('加载历史记录失败')
    } finally {
        loading.value = false
    }
}

// 查看详情
const viewDetail = async (row) => {
    try {
        const { data } = await getFeedbackDetail(row.afId)
        detailData.value = data
        detailVisible.value = true
    } catch (error) {
        ElMessage.error('获取详情失败')
    }
}

// 打开修改弹窗
const openEditDialog = async (row) => {
    console.log('打开修改弹窗，行数据:', row)

    // 确保省份列表已加载
    if (provinceList.value.length === 0) {
        await loadBaseData()
    }

    // 加载当前行对应的城市列表
    if (row.provinceId) {
        try {
            const { data } = await getCitiesByProvince(row.provinceId)
            editCityList.value = data || []
            console.log('城市列表加载成功:', editCityList.value.length, '条')
        } catch (error) {
            console.error('加载城市失败:', error)
            editCityList.value = []
        }
    }

    // 填充表单数据 - 使用 nextTick 确保数据绑定正确
    editForm.afId = row.afId
    editForm.telId = userStore.userInfo?.userId || ''
    editForm.provinceId = row.provinceId
    editForm.cityId = row.cityId
    editForm.address = row.address
    editForm.information = row.information
    editForm.estimatedGrade = row.estimatedGrade

    console.log('填充表单:', {
        provinceId: editForm.provinceId,
        cityId: editForm.cityId,
        estimatedGrade: editForm.estimatedGrade
    })

    editVisible.value = true
}

// 修改时省份变更
const handleEditProvinceChange = async (val) => {
    console.log('省份变更:', val)
    editForm.cityId = null
    if (val) {
        try {
            const { data } = await getCitiesByProvince(val)
            editCityList.value = data || []
        } catch (error) {
            console.error('加载城市失败:', error)
            editCityList.value = []
        }
    } else {
        editCityList.value = []
    }
}

// 提交修改
const handleEditSubmit = async () => {
    if (!editFormRef.value) return

    try {
        await editFormRef.value.validate()

        editLoading.value = true
        const { data } = await updateFeedback({
            afId: editForm.afId,
            telId: editForm.telId,
            provinceId: editForm.provinceId,
            cityId: editForm.cityId,
            address: editForm.address,
            information: editForm.information,
            estimatedGrade: editForm.estimatedGrade
        })

        if (data) {
            ElMessage.success('修改成功！')
            editVisible.value = false
            loadHistory()
        }
    } catch (error) {
        if (error.message) {
            ElMessage.error(error.message)
        }
    } finally {
        editLoading.value = false
    }
}

// 删除反馈
const handleDelete = (row) => {
    ElMessageBox.confirm(
        `确认删除编号为 ${row.afId} 的反馈记录吗？删除后不可恢复。`,
        '删除确认',
        {
            confirmButtonText: '确认删除',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            const telId = userStore.userInfo?.userId
            const { data } = await deleteFeedback(row.afId, telId)
            if (data) {
                ElMessage.success('删除成功！')
                loadHistory()
            }
        } catch (error) {
            if (error.message) {
                ElMessage.error(error.message)
            }
        }
    }).catch(() => {})
}

const goToSubmit = () => {
    router.push('/feedback/submit')
}

onMounted(async () => {
    await loadBaseData()
    loadHistory()
})
</script>

<style scoped>
.history-container {
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

.stat-cards {
    margin-bottom: 24px;
}

.stat-cards :deep(.el-statistic) {
    background: #fff;
    padding: 20px 24px;
    border-radius: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.stat-cards :deep(.el-statistic__head) {
    font-size: 14px;
    color: #999;
}

.stat-cards :deep(.el-statistic__value) {
    font-size: 28px;
    font-weight: 700;
}

.history-card {
    border-radius: 16px;
}

.history-card :deep(.el-card__header) {
    padding: 16px 24px;
    border-bottom: 1px solid #f0f0f0;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 600;
    font-size: 16px;
}

.header-actions {
    display: flex;
    align-items: center;
}

.detail-content {
    padding: 10px 0;
}

.detail-content :deep(.el-descriptions__label) {
    width: 120px;
    font-weight: 500;
}
</style>