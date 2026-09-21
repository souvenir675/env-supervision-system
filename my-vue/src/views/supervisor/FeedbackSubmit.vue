<template>
    <div class="feedback-submit-container">
        <!-- 页面头部 -->
        <div class="page-header">
            <div class="header-left">
                <h2>提交空气质量监督信息</h2>
                <p>请您根据观察到的实际情况，预估所在区域的空气质量等级</p>
            </div>
            <div class="header-right">
                <el-tag type="success" size="large">
                    <el-icon><User /></el-icon>
                    欢迎，{{ userInfo.realName || '监督员' }}
                </el-tag>
            </div>
        </div>

        <!-- 提交表单 -->
        <el-card class="submit-card" shadow="hover">
            <el-form
                ref="submitFormRef"
                :model="submitForm"
                :rules="rules"
                label-width="140px"
                label-position="right"
                size="large"
            >
                <!-- 网格地址选择 -->
                <div class="form-section">
                    <div class="section-title">
                        <el-icon><Location /></el-icon>
                        <span>网格地址信息</span>
                    </div>
                    <el-row :gutter="20">
                        <el-col :span="12">
                            <el-form-item label="省份" prop="provinceId">
                                <el-select
                                    v-model="submitForm.provinceId"
                                    placeholder="请选择省份"
                                    filterable
                                    @change="handleProvinceChange"
                                >
                                    <el-option
                                        v-for="item in provinceList"
                                        :key="item.provinceId"
                                        :label="item.provinceName"
                                        :value="item.provinceId"
                                    />
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="城市" prop="cityId">
                                <el-select
                                    v-model="submitForm.cityId"
                                    placeholder="请选择城市"
                                    filterable
                                    :disabled="!submitForm.provinceId"
                                >
                                    <el-option
                                        v-for="item in cityList"
                                        :key="item.cityId"
                                        :label="item.cityName"
                                        :value="item.cityId"
                                    />
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-form-item label="详细地址" prop="address">
                        <el-input
                            v-model="submitForm.address"
                            placeholder="请输入您观测的具体地址（如：XX路XX号）"
                            maxlength="200"
                            show-word-limit
                            clearable
                        />
                    </el-form-item>
                </div>

                <!-- AQI预估 -->
                <div class="form-section">
                    <div class="section-title">
                        <el-icon><DataAnalysis /></el-icon>
                        <span>空气质量预估</span>
                    </div>

                    <el-form-item label="预估AQI等级" prop="estimatedGrade">
                        <el-radio-group v-model="submitForm.estimatedGrade" size="large">
                            <el-radio-button
                                v-for="item in aqiList"
                                :key="item.aqiId"
                                :value="item.aqiId"
                                :style="{ color: item.color }"
                            >
                                {{ item.chineseExplain }}级 - {{ item.aqiExplain }}
                            </el-radio-button>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item label="空气质量描述" prop="information">
                        <el-input
                            v-model="submitForm.information"
                            type="textarea"
                            placeholder="请描述您观察到的空气质量情况（如：能见度、气味、是否有雾霾等）"
                            :rows="4"
                            maxlength="400"
                            show-word-limit
                            clearable
                        />
                    </el-form-item>
                </div>

                <!-- 提交按钮 -->
                <div class="form-actions">
                    <el-button @click="resetForm">重置</el-button>
                    <el-button
                        type="primary"
                        :loading="submitting"
                        @click="handleSubmit"
                    >
                        <el-icon><Check /></el-icon>
                        {{ submitting ? '提交中...' : '提交反馈' }}
                    </el-button>
                </div>
            </el-form>
        </el-card>

        <!-- AQI等级参考表 -->
        <el-card class="aqi-reference" shadow="hover">
            <template #header>
                <div class="card-header">
                    <span>AQI等级参考表</span>
                    <el-link type="primary" @click="goToAqiStandard">查看详情 →</el-link>
                </div>
            </template>
            <el-table :data="aqiList" border stripe size="small">
                <el-table-column prop="aqiId" label="等级" width="70" align="center">
                    <template #default="{ row }">
                        <el-tag :color="row.color" style="color: #fff; border: none; min-width: 30px; text-align: center">
                            {{ row.chineseExplain }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="aqiExplain" label="描述" width="120" />
                <el-table-column prop="so2Range" label="SO₂ (ug/m³)" width="140" />
                <el-table-column prop="coRange" label="CO (ug/m³)" width="140" />
                <el-table-column prop="spmRange" label="PM2.5 (ug/m³)" width="140" />
                <el-table-column prop="healthImpact" label="健康影响" min-width="200" show-overflow-tooltip />
            </el-table>
        </el-card>
        <AiChatDialog />
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store'
import { getProvinces, getCitiesByProvince } from '@/api/area'
import { getAqiList } from '@/api/aqi'
import { submitFeedback } from '@/api/supervisor'
import AiChatDialog from '@/components/AiChatDialog.vue'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const submitFormRef = ref()
const submitting = ref(false)
const provinceList = ref([])
const cityList = ref([])
const aqiList = ref([])

const submitForm = reactive({
    provinceId: '',
    cityId: '',
    address: '',
    estimatedGrade: '',
    information: ''
})

// 表单校验规则
const rules = {
    provinceId: [
        { required: true, message: '请选择省份', trigger: 'change' }
    ],
    cityId: [
        { required: true, message: '请选择城市', trigger: 'change' }
    ],
    address: [
        { required: true, message: '请输入详细地址', trigger: 'blur' },
        { max: 200, message: '地址不能超过200个字符', trigger: 'blur' }
    ],
    estimatedGrade: [
        { required: true, message: '请选择预估AQI等级', trigger: 'change' }
    ],
    information: [
        { required: true, message: '请输入空气质量描述', trigger: 'blur' },
        { max: 400, message: '描述不能超过400个字符', trigger: 'blur' }
    ]
}

// 加载省份列表
const loadProvinces = async () => {
    try {
        const { data } = await getProvinces()
        provinceList.value = data || []
    } catch (error) {
        console.error('加载省份失败:', error)
    }
}

// 加载AQI等级列表
const loadAqiList = async () => {
    try {
        const { data } = await getAqiList()
        aqiList.value = data || []
    } catch (error) {
        console.error('加载AQI等级失败:', error)
    }
}

// 省份变更时加载城市
const handleProvinceChange = (val) => {
    submitForm.cityId = ''
    if (val) {
        loadCities(val)
    } else {
        cityList.value = []
    }
}

// 加载城市列表
const loadCities = async (provinceId) => {
    try {
        const { data } = await getCitiesByProvince(provinceId)
        cityList.value = data || []
    } catch (error) {
        console.error('加载城市失败:', error)
    }
}

// 提交反馈
const handleSubmit = async () => {
    if (!submitFormRef.value) return

    try {
        await submitFormRef.value.validate()

        const formData = {
            telId: userInfo.value.userId,
            provinceId: submitForm.provinceId,
            cityId: submitForm.cityId,
            address: submitForm.address,
            estimatedGrade: submitForm.estimatedGrade,
            information: submitForm.information
        }

        submitting.value = true
        const { data } = await submitFeedback(formData)

        if (data) {
            ElMessage.success('反馈提交成功！感谢您的参与')
            resetForm()
        }
    } catch (error) {
        if (error.message) {
            ElMessage.error(error.message)
        }
    } finally {
        submitting.value = false
    }
}

// 重置表单
const resetForm = () => {
    if (submitFormRef.value) {
        submitFormRef.value.resetFields()
    }
    submitForm.provinceId = ''
    submitForm.cityId = ''
    submitForm.address = ''
    submitForm.estimatedGrade = ''
    submitForm.information = ''
    cityList.value = []
}

// 跳转到AQI标准页面
const goToAqiStandard = () => {
    router.push('/aqi/standard')
}

onMounted(() => {
    loadProvinces()
    loadAqiList()
})
</script>

<style scoped>
.feedback-submit-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
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

.submit-card {
    border-radius: 16px;
    margin-bottom: 24px;
}

.submit-card :deep(.el-card__body) {
    padding: 30px 35px;
}

.form-section {
    margin-bottom: 28px;
    padding-bottom: 28px;
    border-bottom: 1px solid #f0f0f0;
}

.form-section:last-child {
    border-bottom: none;
    margin-bottom: 0;
    padding-bottom: 0;
}

.section-title {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 20px;
}

.section-title .el-icon {
    font-size: 20px;
    color: #2e7d32;
}

.form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 16px;
    padding-top: 20px;
    border-top: 1px solid #f0f0f0;
}

.form-actions .el-button {
    padding: 12px 40px;
    border-radius: 10px;
    font-size: 15px;
}

.aqi-reference {
    border-radius: 16px;
}

.aqi-reference :deep(.el-card__header) {
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

:deep(.el-select) {
    width: 100%;
}

:deep(.el-radio-button) {
    margin: 4px 0;
}

:deep(.el-radio-button__inner) {
    border-radius: 6px !important;
    border: 1px solid #dcdfe6;
    padding: 8px 16px;
}
</style>