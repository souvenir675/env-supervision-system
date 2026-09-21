<template>
    <div class="task-container">
        <!-- 页面头部 -->
        <div class="page-header">
            <div class="header-left">
                <h2>我的任务列表</h2>
                <p>查看指派给您的空气质量检测任务</p>
            </div>
            <div class="header-right">
                <!-- 工作状态切换器 -->
                <div class="status-switcher">
                    <span class="status-label">当前状态：</span>
                    <el-tag
                        :type="getGridMemberStateType(currentState)"
                        size="large"
                        effect="dark"
                        class="status-tag"
                    >
                        {{ getGridMemberStateLabel(currentState) }}
                    </el-tag>
                    <el-button
                        type="primary"
                        size="default"
                        @click="openStateDialog"
                        style="margin-left: 12px"
                    >
                        <el-icon><Switch /></el-icon>
                        修改状态
                    </el-button>
                </div>
                <el-tag type="success" size="large">
                    <el-icon><User /></el-icon>
                    网格员：{{ userInfo.userName || '未登录' }}
                </el-tag>
            </div>
        </div>

        <!-- 统计卡片 -->
        <el-row :gutter="20" class="stat-cards">
            <el-col :span="8">
                <div class="stat-card">
                    <div class="stat-icon" style="background: #e8f5e9; color: #2e7d32;">
                        <el-icon><List /></el-icon>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">{{ statistics.total }}</div>
                        <div class="stat-label">总任务数</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="8">
                <div class="stat-card">
                    <div class="stat-icon" style="background: #fff3e0; color: #e65100;">
                        <el-icon><Clock /></el-icon>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">{{ statistics.pending }}</div>
                        <div class="stat-label">待处理</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="8">
                <div class="stat-card">
                    <div class="stat-icon" style="background: #e3f2fd; color: #1565c0;">
                        <el-icon><CircleCheck /></el-icon>
                    </div>
                    <div class="stat-info">
                        <div class="stat-value">{{ statistics.completed }}</div>
                        <div class="stat-label">已完成</div>
                    </div>
                </div>
            </el-col>
        </el-row>

        <!-- 状态警告提示 -->
        <el-alert
            v-if="currentState !== 0"
            :title="`您当前的工作状态为「${getGridMemberStateLabel(currentState)}」，暂时不会接收到新的任务指派`"
            type="warning"
            show-icon
            :closable="false"
            style="margin-bottom: 20px"
        />

        <!-- 任务列表 -->
        <el-card class="task-card" shadow="hover">
            <template #header>
                <div class="card-header">
                    <span>任务列表</span>
                    <div class="header-actions">
                        <el-input
                            v-model="searchKeyword"
                            placeholder="搜索地址"
                            prefix-icon="Search"
                            clearable
                            style="width: 200px; margin-right: 12px"
                            @input="handleSearch"
                        />
                        <el-select
                            v-model="filterState"
                            placeholder="全部状态"
                            clearable
                            style="width: 140px"
                            @change="handleSearch"
                        >
                            <el-option label="全部" value="" />
                            <el-option label="待处理" value="1" />
                            <el-option label="已完成" value="2" />
                        </el-select>
                        <el-button type="primary" @click="loadTasks">
                            <el-icon><Refresh /></el-icon>
                            刷新
                        </el-button>
                    </div>
                </div>
            </template>

            <el-table :data="filteredList" border stripe v-loading="loading" style="width: 100%">
                <el-table-column prop="afId" label="任务编号" width="100" align="center" />
                <el-table-column prop="provinceName" label="省份" width="100" />
                <el-table-column prop="cityName" label="城市" width="100" />
                <el-table-column prop="address" label="详细地址" min-width="200" show-overflow-tooltip />
                <el-table-column prop="estimatedGrade" label="预估等级" width="140" align="center">
                    <template #default="{ row }">
                        <el-tag
                            :color="getAqiColor(row.estimatedGrade)"
                            style="color: #fff; border: none; min-width: 60px; text-align: center"
                        >
                            {{ getAqiLabel(row.estimatedGrade) }}
                        </el-tag>
                    </template>
                </el-table-column>

                <!-- 新增：实测等级列 -->
                <el-table-column prop="measuredGrade" label="实测等级" width="140" align="center">
                    <template #default="{ row }">
                        <el-tag
                            v-if="row.measuredGrade"
                            :color="getAqiColor(row.measuredGrade)"
                            style="color: #fff; border: none; min-width: 60px; text-align: center"
                        >
                            {{ getAqiLabel(row.measuredGrade) }}
                        </el-tag>
                        <span v-else style="color: #999;">待检测</span>
                    </template>
                </el-table-column>
                <el-table-column prop="information" label="反馈描述" min-width="180" show-overflow-tooltip />
                <el-table-column prop="assignDate" label="指派日期" width="120" align="center" />
                <el-table-column prop="state" label="状态" width="100" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.state === 1 ? 'warning' : 'success'">
                            {{ row.state === 1 ? '待处理' : '已完成' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="160" align="center" fixed="right">
                    <template #default="{ row }">
                        <el-button type="text" size="small" @click="viewDetail(row)">查看详情</el-button>
                        <el-button
                            v-if="row.state === 1"
                            type="primary"
                            size="small"
                            @click="openSubmitData(row)"
                        >
                            录入数据
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <el-empty v-if="!loading && filteredList.length === 0" description="暂无任务" />
        </el-card>

        <!-- 录入数据弹窗 -->
        <el-dialog v-model="submitVisible" title="录入实测AQI数据" width="560px" :close-on-click-modal="false">
            <div v-if="currentTask" class="submit-content">
                <el-descriptions :column="2" border size="small">
                    <el-descriptions-item label="任务编号">{{ currentTask.afId }}</el-descriptions-item>
                    <el-descriptions-item label="地址">{{ currentTask.address }}</el-descriptions-item>
                    <el-descriptions-item label="预估等级">
                        <el-tag
                            :color="getAqiColor(currentTask.estimatedGrade)"
                            style="color: #fff; border: none"
                        >
                            {{ getAqiLabel(currentTask.estimatedGrade) }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="反馈描述">{{ currentTask.information }}</el-descriptions-item>
                </el-descriptions>

                <el-divider>请输入实测数据</el-divider>

                <el-form
                    ref="dataFormRef"
                    :model="dataForm"
                    :rules="dataRules"
                    label-width="160px"
                    label-position="right"
                >
                    <el-form-item label="SO₂实测浓度 (ug/m³)" prop="so2Value">
                        <el-input-number
                            v-model="dataForm.so2Value"
                            :min="0"
                            :max="9999"
                            controls-position="right"
                            style="width: 200px"
                        />
                        <span style="margin-left: 12px; color: #999; font-size: 13px;">
                            参考范围：0 ~ 9999
                        </span>
                    </el-form-item>

                    <el-form-item label="CO实测浓度 (ug/m³)" prop="coValue">
                        <el-input-number
                            v-model="dataForm.coValue"
                            :min="0"
                            :max="9999"
                            controls-position="right"
                            style="width: 200px"
                        />
                        <span style="margin-left: 12px; color: #999; font-size: 13px;">
                            参考范围：0 ~ 9999
                        </span>
                    </el-form-item>

                    <el-form-item label="PM2.5实测浓度 (ug/m³)" prop="spmValue">
                        <el-input-number
                            v-model="dataForm.spmValue"
                            :min="0"
                            :max="9999"
                            controls-position="right"
                            style="width: 200px"
                        />
                        <span style="margin-left: 12px; color: #999; font-size: 13px;">
                            参考范围：0 ~ 9999
                        </span>
                    </el-form-item>
                </el-form>

                <el-divider />

                <div class="calc-result" v-if="calculatedAqi">
                    <span class="label">计算得出的AQI等级：</span>
                    <el-tag
                        :color="getAqiColor(calculatedAqi)"
                        style="color: #fff; border: none; font-size: 16px; padding: 6px 20px;"
                    >
                        {{ getAqiLabel(calculatedAqi) }}
                    </el-tag>
                    <span class="formula" style="margin-left: 12px; color: #999;">
                        AQI = MAX(SO₂, CO, PM2.5)
                    </span>
                </div>
            </div>

            <template #footer>
                <el-button @click="submitVisible = false">取消</el-button>
                <el-button
                    type="primary"
                    :loading="submitting"
                    @click="handleSubmitData"
                >
                    {{ submitting ? '提交中...' : '确认提交' }}
                </el-button>
            </template>
        </el-dialog>

        <!-- 详情弹窗 -->
        <el-dialog v-model="detailVisible" title="任务详情" width="600px">
            <div v-if="detailData" class="detail-content">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="反馈用户">
                        <span style="font-weight: 600; color: #2e7d32;">
                            {{ detailData.supervisorName || '未知用户' }}
                        </span>
                    </el-descriptions-item>
                    <el-descriptions-item label="联系电话">
                        <span style="color: #409eff; font-weight: 600;">
                            {{ detailData.supervisorTel || detailData.telId || '-' }}
                        </span>
                    </el-descriptions-item>
                    <el-descriptions-item label="任务编号">{{ detailData.afId }}</el-descriptions-item>
                    <el-descriptions-item label="状态">
                        <el-tag :type="detailData.state === 1 ? 'warning' : 'success'">
                            {{ detailData.state === 1 ? '待处理' : '已完成' }}
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
                    <el-descriptions-item label="指派日期">{{ detailData.assignDate }}</el-descriptions-item>
                    <el-descriptions-item label="指派时间">{{ detailData.assignTime }}</el-descriptions-item>
                </el-descriptions>

                <!-- 新增：实测数据展示 -->
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

        <!-- 修改状态弹窗（新增） -->
        <el-dialog v-model="stateVisible" title="修改工作状态" width="450px" :close-on-click-modal="false">
            <div class="state-dialog-content">
                <el-alert
                    title="提示"
                    type="info"
                    :closable="false"
                    show-icon
                    style="margin-bottom: 20px"
                >
                    修改工作状态后，管理员将根据您的状态决定是否指派新任务。可工作状态下才会被指派新任务。
                </el-alert>

                <el-form :model="stateForm" label-width="100px">
                    <el-form-item label="当前状态">
                        <el-tag :type="getGridMemberStateType(currentState)" size="large">
                            {{ getGridMemberStateLabel(currentState) }}
                        </el-tag>
                    </el-form-item>

                    <el-form-item label="修改为">
                        <el-radio-group v-model="stateForm.state">
                            <el-radio :value="0" border>
                                <el-icon color="#67c23a"><CircleCheck /></el-icon>
                                可工作
                            </el-radio>
                            <el-radio :value="1" border>
                                <el-icon color="#e6a23c"><Clock /></el-icon>
                                临时抽调
                            </el-radio>
                            <el-radio :value="2" border>
                                <el-icon color="#909399"><Calendar /></el-icon>
                                休假
                            </el-radio>
                            <el-radio :value="3" border>
                                <el-icon color="#f56c6c"><More /></el-icon>
                                其它
                            </el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-form>
            </div>

            <template #footer>
                <el-button @click="stateVisible = false">取消</el-button>
                <el-button
                    type="primary"
                    :loading="stateLoading"
                    :disabled="stateForm.state === currentState"
                    @click="handleUpdateState"
                >
                    {{ stateLoading ? '保存中...' : '确认修改' }}
                </el-button>
            </template>
        </el-dialog>
        <AiChatDialog />
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store'
import { getTasksByGridMember, confirmFeedback } from '@/api/feedback'
import { getAqiList, calculateAqi } from '@/api/aqi'
import { submitAqiData } from '@/api/statistics'
import { getGridMemberDetail, updateGridMemberState } from '@/api/gridmember'
import AiChatDialog from '@/components/AiChatDialog.vue'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const taskList = ref([])
const aqiList = ref([])
const searchKeyword = ref('')
const filterState = ref('')

// 当前网格员状态
const currentState = ref(0)

// 修改状态弹窗
const stateVisible = ref(false)
const stateLoading = ref(false)
const stateForm = reactive({
    state: 0
})

const detailVisible = ref(false)
const detailData = ref(null)

const submitVisible = ref(false)
const submitting = ref(false)
const currentTask = ref(null)
const dataFormRef = ref()
const calculatedAqi = ref(null)

const dataForm = reactive({
    so2Value: null,
    coValue: null,
    spmValue: null
})

// 统计信息
const statistics = computed(() => {
    const total = taskList.value.length
    const pending = taskList.value.filter(item => item.state === 1).length
    const completed = taskList.value.filter(item => item.state === 2).length
    return { total, pending, completed }
})

// 过滤后的列表
const filteredList = computed(() => {
    let list = taskList.value

    if (searchKeyword.value) {
        const keyword = searchKeyword.value.toLowerCase()
        list = list.filter(item =>
            item.address?.toLowerCase().includes(keyword)
        )
    }

    if (filterState.value !== '') {
        list = list.filter(item => item.state === parseInt(filterState.value))
    }

    return list
})

// 数据表单校验规则
const dataRules = {
    so2Value: [
        { required: true, message: '请输入SO₂实测浓度', trigger: 'blur' },
        { type: 'number', min: 0, message: '浓度值不能为负数', trigger: 'blur' }
    ],
    coValue: [
        { required: true, message: '请输入CO实测浓度', trigger: 'blur' },
        { type: 'number', min: 0, message: '浓度值不能为负数', trigger: 'blur' }
    ],
    spmValue: [
        { required: true, message: '请输入PM2.5实测浓度', trigger: 'blur' },
        { type: 'number', min: 0, message: '浓度值不能为负数', trigger: 'blur' }
    ]
}

// 网格员状态相关
const getGridMemberStateLabel = (state) => {
    const map = { 0: '可工作', 1: '临时抽调', 2: '休假', 3: '其它' }
    return map[state] || '未知'
}

const getGridMemberStateType = (state) => {
    const map = { 0: 'success', 1: 'warning', 2: 'info', 3: 'danger' }
    return map[state] || 'info'
}

// 加载网格员状态
const loadGridMemberState = async () => {
    const gmId = userInfo.value?.userId
    if (!gmId) return

    try {
        const { data } = await getGridMemberDetail(gmId)
        if (data) {
            currentState.value = data.state ?? 0
        }
    } catch (error) {
        console.error('加载网格员状态失败:', error)
    }
}

// 打开状态修改弹窗
const openStateDialog = () => {
    stateForm.state = currentState.value
    stateVisible.value = true
}

// 提交状态修改
const handleUpdateState = async () => {
    if (stateForm.state === currentState.value) {
        ElMessage.warning('状态未发生变化')
        return
    }

    const gmId = userInfo.value?.userId
    if (!gmId) {
        ElMessage.error('未获取到网格员信息')
        return
    }

    try {
        stateLoading.value = true
        const { data } = await updateGridMemberState({
            gmId: gmId,
            state: stateForm.state
        })

        if (data) {
            ElMessage.success(`工作状态已修改为「${getGridMemberStateLabel(stateForm.state)}」`)
            currentState.value = stateForm.state   // ⭐ 同步更新
            stateVisible.value = false
        }
    } catch (error) {
        if (error.message) {
            ElMessage.error(error.message)
        }
    } finally {
        stateLoading.value = false
    }
}

// 加载AQI等级
const loadAqiList = async () => {
    try {
        const { data } = await getAqiList()
        aqiList.value = data || []
    } catch (error) {
        console.error('加载AQI等级失败:', error)
    }
}

// 获取AQI等级标签
const getAqiLabel = (grade) => {
    const item = aqiList.value.find(a => a.aqiId === grade)
    return item ? `${item.chineseExplain}级-${item.aqiExplain}` : `等级${grade}`
}

// 获取AQI颜色
const getAqiColor = (grade) => {
    const item = aqiList.value.find(a => a.aqiId === grade)
    return item?.color || '#999'
}

// 加载任务列表
const loadTasks = async () => {
    const gmId = userInfo.value?.userId
    if (!gmId) {
        ElMessage.warning('请先登录')
        return
    }

    loading.value = true
    try {
        const { data } = await getTasksByGridMember(gmId)
        taskList.value = data || []
    } catch (error) {
        ElMessage.error('加载任务失败')
    } finally {
        loading.value = false
    }
}

// 搜索处理
const handleSearch = () => {
    // 计算属性自动更新
}

// 查看详情
const viewDetail = (row) => {
    detailData.value = row
    detailVisible.value = true
}

// 打开录入数据弹窗
const openSubmitData = (row) => {
    currentTask.value = row
    dataForm.so2Value = null
    dataForm.coValue = null
    dataForm.spmValue = null
    calculatedAqi.value = null
    submitVisible.value = true
}

// 监听数据变化自动计算AQI
const watchData = () => {
    // 使用watch监听数据变化
    // 由于在setup中无法直接使用watch，这里在template中通过@change触发
}

const handleCalculate = async () => {
    const { so2Value, coValue, spmValue } = dataForm
    if (so2Value !== null && coValue !== null && spmValue !== null && so2Value !== '' && coValue !== '' && spmValue !== '') {
        try {
            const { data } = await calculateAqi(so2Value, coValue, spmValue)
            calculatedAqi.value = data
        } catch (error) {
            console.error('AQI计算失败:', error)
            calculatedAqi.value = null
        }
    } else {
        calculatedAqi.value = null
    }
}



// 提交实测数据
const handleSubmitData = async () => {
    if (!dataFormRef.value) return
    if (!currentTask.value) return

    try {
        await dataFormRef.value.validate()

        // 计算AQI
        const { so2Value, coValue, spmValue } = dataForm
        let calculated = 3
        try {
            const result = await calculateAqi(so2Value, coValue, spmValue)
            calculated = result.data
        } catch (e) {
            console.error('AQI计算失败:', e)
        }

        await ElMessageBox.confirm(
            `确认提交实测数据？AQI计算结果为：${getAqiLabel(calculated)}`,
            '确认提交',
            { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }
        )

        submitting.value = true
        const { data } = await submitAqiData({
            afId: currentTask.value.afId,
            so2Value: so2Value,
            coValue: coValue,
            spmValue: spmValue,
            gmId: userInfo.value.userId
        })

        if (data) {
            ElMessage.success('数据提交成功！')
            submitVisible.value = false
            loadTasks()
        }
    } catch (error) {
        if (error.message) {
            ElMessage.error(error.message)
        }
    } finally {
        submitting.value = false
    }
}

onMounted(() => {
    loadAqiList()
    loadGridMemberState()
    loadTasks()
})
</script>

<style scoped>
.task-container {
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
    color: #1565c0;
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

.stat-card {
    display: flex;
    align-items: center;
    gap: 16px;
    background: #fff;
    padding: 20px 24px;
    border-radius: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.stat-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    flex-shrink: 0;
}

.stat-info .stat-value {
    font-size: 28px;
    font-weight: 700;
    color: #333;
}

.stat-info .stat-label {
    font-size: 14px;
    color: #999;
}

.task-card {
    border-radius: 16px;
}

.task-card :deep(.el-card__header) {
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

.submit-content {
    padding: 10px 0;
}

.calc-result {
    text-align: center;
    padding: 12px 0;
}

.calc-result .label {
    font-size: 15px;
    color: #666;
}

.detail-content {
    padding: 10px 0;
}

.detail-content :deep(.el-descriptions__label) {
    width: 120px;
    font-weight: 500;
}

.measured-section {
    margin-top: 20px;
    padding: 16px;
    background: #f5f7fa;
    border-radius: 12px;
}

.measured-section :deep(.el-divider__text) {
    background: #f5f7fa;
}
</style>