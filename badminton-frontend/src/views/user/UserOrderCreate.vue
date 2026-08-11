<template>
  <div class="order-create">
    <el-card v-loading="loading">
      <template #header><strong>预约场地</strong></template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="场地">
          <span>{{ venue.name }}（￥{{ venue.pricePerHour }}/小时）</span>
        </el-form-item>
        <el-form-item label="预约日期" prop="bookDate">
          <el-date-picker v-model="form.bookDate" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="小时数" prop="hours">
          <el-input-number v-model="form.hours" :min="1" :max="4" />
        </el-form-item>
        <el-form-item label="时间段" prop="timeSlot">
          <el-select v-model="form.timeSlot" placeholder="选择时间段">
            <el-option v-for="slot in timeSlots" :key="slot" :label="slot" :value="slot" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="form.contactName" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="总价">
          ￥{{ totalPrice }}
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">提交订单</el-button>
          <el-button @click="router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getVenueDetail } from '@/api/venue'
import { createOrder } from '@/api/order'

const route = useRoute()
const router = useRouter()
const vid = route.params.vid

const venue = ref({})
const loading = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const form = reactive({
  bookDate: null,
  timeSlot: '',
  hours: 1,
  contactName: '',
  contactPhone: ''
})

const rules = {
  bookDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  timeSlot: [{ required: true, message: '请选择时间段', trigger: 'change' }],
  hours: [{ required: true, message: '请输入小时数', trigger: 'blur' }],
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }]
}

const OPEN_HOUR = 8
const CLOSE_HOUR = 22

const formatHour = (hour) => `${String(hour).padStart(2, '0')}:00`

const timeSlots = computed(() => {
  const hours = Number(form.hours) || 1
  const slots = []

  for (let start = OPEN_HOUR; start + hours <= CLOSE_HOUR; start += hours) {
    slots.push(`${formatHour(start)}-${formatHour(start + hours)}`)
  }

  return slots
})

watch(() => form.hours, () => {
  form.timeSlot = ''
})

const totalPrice = computed(() => {
  if (!venue.value.pricePerHour) return 0
  return venue.value.pricePerHour * form.hours
})

const fetchVenue = async () => {
  loading.value = true
  try {
    const res = await getVenueDetail(vid)
    venue.value = res
  } catch {
    ElMessage.error('加载场地失败')
    router.push('/venues')
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    await createOrder({
      venueId: vid,
      bookDate: form.bookDate,
      timeSlot: form.timeSlot,
      hours: form.hours,
      contactName: form.contactName,
      contactPhone: form.contactPhone
    })
    ElMessage.success('预约成功，请等待商家确认')
    router.push('/orders')
  } finally {
    submitting.value = false
  }
}

onMounted(fetchVenue)
</script>

<style scoped>
.order-create { max-width: 600px; margin: 0 auto; }
</style>