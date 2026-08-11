<template>
  <div class="merchant-detail">
    <!-- 商家信息卡片 -->
    <div class="merchant-info-card">
      <h2>{{ merchant.companyName }}</h2>
      <div class="info-row"><el-icon><Location /></el-icon> {{ merchant.address }}</div>
      <div class="info-row"><el-icon><Phone /></el-icon> {{ merchant.contactPhone }}</div>
      <div class="info-row" v-if="merchant.businessHours">
        <el-icon><Timer /></el-icon>
        <span>{{ formatBusinessHours(merchant.businessHours) }}</span>
      </div>
    </div>

    <h3 class="subtitle">场地列表</h3>
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="8" v-for="venue in sortedVenues" :key="venue.vid" class="venue-item">
        <el-card shadow="hover" class="venue-card">
          <div class="venue-header">
            <h4>{{ venue.code }}</h4>
            <el-tag :type="venue.status === 0 ? 'success' : 'danger'" size="small">
              {{ venue.status === 0 ? '可预订' : '暂停' }}
            </el-tag>
          </div>
          <div class="venue-photo" v-if="venue.photos">
            <img :src="venue.photos.split(',')[0]" />
          </div>
          <p>{{ venue.description || '暂无描述' }}</p>
          <p class="price">¥{{ venue.pricePerHour }}/小时</p>
          <div class="card-actions">
            <el-button size="small" @click="showVenueDetail(venue)">查看详情</el-button>
            <el-button size="small" type="primary" @click="bookVenue(venue)" :disabled="venue.status !== 0">立即预订</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 场地详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="场地详情" width="600px">
      <div v-if="currentVenue">
        <div class="detail-photo" v-if="currentVenue.photos">
          <el-image :src="currentVenue.photos.split(',')[0]" fit="cover" style="width: 100%; max-height: 300px;" />
        </div>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="场地编号">{{ currentVenue.code }}</el-descriptions-item>
          <el-descriptions-item label="价格">¥{{ currentVenue.pricePerHour }}/小时</el-descriptions-item>
          <el-descriptions-item label="描述">{{ currentVenue.description || '暂无' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentVenue.status === 0 ? 'success' : 'danger'">
              {{ currentVenue.status === 0 ? '可预订' : '暂停' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="currentVenue.photos && currentVenue.photos.split(',').length > 1" class="detail-photos">
          <div class="photos-title">其他图片</div>
          <div class="photos-list">
            <el-image v-for="(url, idx) in currentVenue.photos.split(',')" :key="idx" :src="url" fit="cover" style="width: 80px; height: 80px; margin-right: 8px;" />
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="bookFromDetail">立即预订</el-button>
      </template>
    </el-dialog>

    <!-- 预订对话框 -->
    <el-dialog v-model="dialogVisible" title="预订场地" width="600px">
      <el-form :model="bookingForm" :rules="bookingRules" ref="bookingFormRef" label-width="100px">
        <el-form-item label="场地编号">
          <span>{{ selectedVenue?.code }}</span>
        </el-form-item>
        <el-form-item label="预订日期" prop="bookDate">
          <el-date-picker
            v-model="bookingForm.bookDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
            @change="loadAvailableSlots"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="预订小时数" prop="hours">
          <el-input-number v-model="bookingForm.hours" :min="1" :max="4" @change="handleBookingHoursChange" />
        </el-form-item>
        <el-form-item label="时间段" prop="timeSlot">
          <el-select v-model="bookingForm.timeSlot" placeholder="请选择时间段" :loading="slotLoading" style="width: 100%">
            <el-option
              v-for="slot in availableSlots"
              :key="slot.startTime + '-' + slot.endTime"
              :label="slot.startTime + ' - ' + slot.endTime"
              :value="slot.startTime + '-' + slot.endTime"
              :disabled="!slot.available"
            />
          </el-select>
          <div v-if="availableSlots.length === 0 && bookingForm.bookDate" class="form-tip">暂无可用时段</div>
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="bookingForm.contactName" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="bookingForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="总价">
          <span class="total-price">¥{{ calculateTotalPrice() }}</span>
        </el-form-item>
        <el-form-item label="是否需要陪练" prop="coachCount">
          <div class="coach-field">
            <el-radio-group v-model="bookingForm.needCoach" @change="handleNeedCoachChange">
              <el-radio :value="false">否</el-radio>
              <el-radio :value="true">是</el-radio>
            </el-radio-group>
            <el-input-number
              v-if="bookingForm.needCoach"
              v-model="bookingForm.coachCount"
              :min="1"
              :max="10"
              controls-position="right"
              placeholder="陪练人数"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBooking" :loading="bookingLoading">提交预订</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Location, Phone, Timer } from '@element-plus/icons-vue'
import { getMerchantDetail } from '@/api/merchant'
import { getAvailableSlots } from '@/api/order'   // 只导入 getAvailableSlots，不再导入 createOrder
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const mid = route.params.mid

const merchant = ref({})
const venues = ref([])
const loading = ref(false)
const detailDialogVisible = ref(false)
const currentVenue = ref(null)

// 预订相关
const dialogVisible = ref(false)
const selectedVenue = ref(null)
const bookingLoading = ref(false)
const slotLoading = ref(false)
const availableSlots = ref([])
const bookingFormRef = ref(null)

const bookingForm = ref({
  venueId: null,
  bookDate: '',
  timeSlot: '',
  hours: 2,
  needCoach: false,
  coachCount: null,
  contactName: '',
  contactPhone: ''
})

const disabledDate = (time) => {
  // 只能选择今天及以后的日期，不能选择今天之前的日期
  return time.getTime() < Date.now() - 8.64e7
}

const validateCoachCount = (_rule, value, callback) => {
  if (!bookingForm.value.needCoach) {
    callback()
    return
  }
  if (!value || value < 1) {
    callback(new Error('请填写陪练人数（至少1人）'))
    return
  }
  callback()
}

const bookingRules = {
  bookDate: [{ required: true, message: '请选择预订日期', trigger: 'change' }],
  timeSlot: [{ required: true, message: '请选择时间段', trigger: 'change' }],
  hours: [{ required: true, message: '请输入预订小时数', trigger: 'blur' }],
  coachCount: [{ validator: validateCoachCount, trigger: 'change' }],
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 格式化营业时间
const formatBusinessHours = (businessHoursStr) => {
  if (!businessHoursStr) return '未设置'
  try {
    const hoursList = JSON.parse(businessHoursStr)
    if (!hoursList.length) return '未设置'
    const dayMap = { 1: '周一', 2: '周二', 3: '周三', 4: '周四', 5: '周五', 6: '周六', 7: '周日' }
    return hoursList.map(item => {
      const startDay = dayMap[item.startDay]
      const endDay = dayMap[item.endDay]
      if (startDay === endDay) {
        return `${startDay} ${item.startTime}-${item.endTime}`
      } else {
        return `${startDay}至${endDay} ${item.startTime}-${item.endTime}`
      }
    }).join('；')
  } catch {
    return '格式错误'
  }
}

// 场地排序
const sortedVenues = computed(() => {
  return [...venues.value].sort((a, b) => {
    return a.code.localeCompare(b.code, undefined, { numeric: true, sensitivity: 'base' })
  })
})

const timeToMinutes = (time) => {
  const [hour = 0, minute = 0] = String(time || '').split(':').map(Number)
  return hour * 60 + minute
}

const minutesToTime = (minutes) => {
  const hour = Math.floor(minutes / 60)
  const minute = minutes % 60
  return `${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`
}

const getWeekday = (date) => {
  const day = new Date(`${date}T00:00:00`).getDay()
  return day === 0 ? 7 : day
}

const isDayInRange = (day, startDay, endDay) => {
  if (startDay <= endDay) return day >= startDay && day <= endDay
  return day >= startDay || day <= endDay
}

const getBusinessRangeByDate = () => {
  if (!merchant.value.businessHours || !bookingForm.value.bookDate) return null

  try {
    const weekday = getWeekday(bookingForm.value.bookDate)
    const hoursList = JSON.parse(merchant.value.businessHours)
    const matched = hoursList.find(item => isDayInRange(weekday, Number(item.startDay), Number(item.endDay)))

    if (!matched) return null

    return {
      start: timeToMinutes(matched.startTime),
      end: timeToMinutes(matched.endTime)
    }
  } catch {
    return null
  }
}

const getSlotRange = (slots) => {
  if (!slots.length) return { start: 8 * 60, end: 22 * 60 }

  return {
    start: Math.min(...slots.map(slot => timeToMinutes(slot.startTime))),
    end: Math.max(...slots.map(slot => timeToMinutes(slot.endTime)))
  }
}

const isRangeAvailable = (start, end, sourceSlots) => {
  return !sourceSlots.some(slot => {
    if (slot.available !== false) return false

    const slotStart = timeToMinutes(slot.startTime)
    const slotEnd = timeToMinutes(slot.endTime)
    return start < slotEnd && slotStart < end
  })
}

const buildSlotsByBookingHours = (sourceSlots) => {
  const hours = Number(bookingForm.value.hours) || 1
  const duration = hours * 60
  const range = getBusinessRangeByDate() || getSlotRange(sourceSlots)
  const slots = []
  const step = 30   // 步长 30 分钟，支持非整点开始

  for (let start = range.start; start + duration <= range.end; start += step) {
    const end = start + duration
    slots.push({
      startTime: minutesToTime(start),
      endTime: minutesToTime(end),
      available: isRangeAvailable(start, end, sourceSlots)
    })
  }
  return slots
}

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getMerchantDetail(mid)
    merchant.value = res.merchant
    venues.value = res.venues || []
  } catch {
    ElMessage.error('加载商家信息失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

// 显示场地详情弹窗
const showVenueDetail = (venue) => {
  currentVenue.value = venue
  detailDialogVisible.value = true
}

// 从详情弹窗直接预订
const bookFromDetail = () => {
  detailDialogVisible.value = false
  bookVenue(currentVenue.value)
}

const bookVenue = (venue) => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  selectedVenue.value = venue
  bookingForm.value.venueId = venue.vid
  bookingForm.value.bookDate = ''
  bookingForm.value.timeSlot = ''
  bookingForm.value.hours = 2
  bookingForm.value.needCoach = false
  bookingForm.value.coachCount = null
  bookingForm.value.contactName = userStore.userInfo?.nickname || ''
  bookingForm.value.contactPhone = userStore.userInfo?.phone || ''
  availableSlots.value = []
  dialogVisible.value = true
}

const loadAvailableSlots = async () => {
  bookingForm.value.timeSlot = ''
  availableSlots.value = []
  if (!bookingForm.value.bookDate) return
  slotLoading.value = true
  try {
    const res = await getAvailableSlots({
      venueId: selectedVenue.value.vid,
      date: bookingForm.value.bookDate,
      hours: bookingForm.value.hours
    })
    console.log('可用时段接口返回:', res)
    let slots = buildSlotsByBookingHours(res || [])

    // ✅ 如果选择的是今天，过滤掉已经过去的时间段
    const today = new Date().toISOString().slice(0, 10)  
    if (bookingForm.value.bookDate === today) {
      const now = new Date()
      const currentHour = now.getHours()
      const currentMinute = now.getMinutes()
      slots = slots.filter(slot => {
        const [hour, minute] = slot.startTime.split(':').map(Number)
        // 保留开始时间晚于当前时刻的时段
        return hour > currentHour || (hour === currentHour && minute > currentMinute)
      })
    }

    availableSlots.value = slots
  } catch {
    ElMessage.error('获取可用时间段失败')
  } finally {
    slotLoading.value = false
  }
}

const handleBookingHoursChange = () => {
  loadAvailableSlots()
}

const handleNeedCoachChange = () => {
  bookingForm.value.coachCount = null
  bookingFormRef.value?.clearValidate('coachCount')
}

const calculateTotalPrice = () => {
  if (!selectedVenue.value || !bookingForm.value.hours) return 0
  return (selectedVenue.value.pricePerHour * bookingForm.value.hours).toFixed(2)
}

// ========== 修改点：本地定义 createOrder，确保携带 token ==========
const createOrderRequest = (data) => {
  const token = userStore.token   // 从 store 获取 token
  if (!token) {
    throw new Error('未登录，请重新登录')
  }
  return request({
    url: '/user/order/create',
    method: 'post',
    data: data,
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
}

const submitBooking = async () => {
  // 手动校验陪练人数（若需要陪练但未填人数）
  if (bookingForm.value.needCoach && (!bookingForm.value.coachCount || bookingForm.value.coachCount < 1)) {
    ElMessage.warning('请填写陪练人数')
    return
  }

  await bookingFormRef.value.validate()
  bookingLoading.value = true
  try {
    await createOrderRequest({
      venueId: bookingForm.value.venueId,
      bookDate: bookingForm.value.bookDate,
      timeSlot: bookingForm.value.timeSlot,
      hours: bookingForm.value.hours,
      needCoach: bookingForm.value.needCoach,
      coachCount: bookingForm.value.needCoach ? bookingForm.value.coachCount : 0,
      contactName: bookingForm.value.contactName,
      contactPhone: bookingForm.value.contactPhone
    })
    ElMessage.success('预订成功')
    dialogVisible.value = false
    // 重置表单
    bookingForm.value = {
      venueId: null,
      bookDate: '',
      timeSlot: '',
      hours: 2,
      needCoach: false,
      coachCount: null,
      contactName: '',
      contactPhone: ''
    }
    selectedVenue.value = null
    availableSlots.value = []
  } catch (error) {
    console.error('预订失败:', error)
    ElMessage.error('预订失败：' + (error.response?.data?.message || error.message || '未知错误'))
  } finally {
    bookingLoading.value = false
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.merchant-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.merchant-info-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}
.merchant-info-card h2 {
  margin: 0 0 16px 0;
  font-size: 24px;
}
.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  color: #606266;
  font-size: 14px;
}
.subtitle {
  font-size: 20px;
  font-weight: 500;
  margin-bottom: 20px;
  padding-left: 8px;
  border-left: 4px solid #409eff;
}
.venue-item {
  margin-bottom: 20px;
}
.venue-card {
  height: 100%;
  border-radius: 12px;
  transition: transform 0.2s;
}
.venue-card:hover {
  transform: translateY(-4px);
}
.venue-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.venue-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}
.venue-photo {
  height: 150px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 12px;
}
.venue-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
  margin: 12px 0;
}
.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 12px;
}
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
.coach-field {
  display: flex;
  align-items: center;
  gap: 18px;
}
.coach-field .el-input-number {
  width: 140px;
}
.detail-photo {
  margin-bottom: 15px;
}
.detail-photos {
  margin-top: 15px;
}
.photos-title {
  font-weight: 500;
  margin-bottom: 8px;
}
.photos-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
</style>