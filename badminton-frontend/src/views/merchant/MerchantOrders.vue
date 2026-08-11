<template>
  <div class="merchant-orders">
    <h2 class="page-title">订单管理</h2>

    <!-- 顶部警告 -->
    <div v-if="!isApproved" class="alert">
      <el-alert title="账号未通过审核，无法进行订单管理" type="warning" :closable="false" />
    </div>

    <!-- 以下内容仅在审核通过后显示 -->
    <template v-if="isApproved">
      <div class="order-filter">
        <el-radio-group v-model="statusFilter" @change="handleStatusChange">
          <el-radio-button label="all">全部</el-radio-button>
          <el-radio-button label="0">待支付</el-radio-button>
          <el-radio-button label="1">已支付</el-radio-button>
          <el-radio-button label="2">已取消</el-radio-button>
          <el-radio-button label="3">已完成</el-radio-button>
        </el-radio-group>
      </div>

      <el-table :data="orders" v-loading="loading" class="order-table">
        <el-table-column prop="orderNo" label="订单编号" width="200" />
        <el-table-column prop="nickName" label="用户姓名" width="120">
          <template #default="{ row }">
            {{ row.contactName || '匿名用户' }}
          </template>
        </el-table-column>
        <el-table-column prop="bookDate" label="预订日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.bookDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="timeSlot" label="时间段" width="120" />
        <el-table-column prop="hours" label="小时数" width="80">
          <template #default="{ row }">
            {{ row.hours }}小时
          </template>
        </el-table-column>
        <el-table-column prop="needCoach" label="是否需要陪练" width="120">
          <template #default="{ row }">
            <el-tag :type="isNeedCoach(row.needCoach) ? 'success' : 'info'">
              {{ isNeedCoach(row.needCoach) ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="coachCount" label="陪练人数" width="100">
          <template #default="{ row }">
            {{ isNeedCoach(row.needCoach) ? `${row.coachCount || 0}人` : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="总价" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.totalPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusType(row.status)">
              {{ getOrderStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            
            <template v-if="row.status === 0">
              <el-button size="small" type="success" @click="confirmOrder(row.oid)">确认</el-button>
              <el-button size="small" type="danger" @click="rejectOrder(row.oid)">拒绝</el-button>
            </template>
            <template v-else-if="row.status === 1">
              <el-button size="small" type="primary" @click="completeOrder(row.oid)">完成</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import {
  getMerchantOrders,
  confirmOrder as confirmOrderApi,
  rejectOrder as rejectOrderApi,
  completeOrder as completeOrderApi   // 新增导入
} from '@/api/order'

const userStore = useUserStore()
const isApproved = computed(() => userStore.userInfo?.status === 1)

const orders = ref([])
const loading = ref(false)
const statusFilter = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 加载订单列表（仅在审核通过时调用）
const loadOrders = async () => {
  if (!isApproved.value) return
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      status: statusFilter.value === 'all' ? undefined : parseInt(statusFilter.value)
    }
    const res = await getMerchantOrders(params)
    orders.value = res.list || []
    total.value = res.total || 0
  } catch {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

// 状态筛选变化
const handleStatusChange = () => {
  currentPage.value = 1
  loadOrders()
}

// 分页变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadOrders()
}
const handleCurrentChange = (page) => {
  currentPage.value = page
  loadOrders()
}

// 确认订单
const confirmOrder = async (oid) => {
  try {
    await ElMessageBox.confirm('确认该订单？', '提示', { type: 'warning' })
  } catch {
    return // 用户取消
  }
  try {
    await confirmOrderApi(oid)
    ElMessage.success('确认成功')
    // 确认后订单状态由待支付变为已支付（后端处理），刷新列表
    await loadOrders()
  } catch {
    ElMessage.error('操作失败')
  }
}

// 拒绝订单
const rejectOrder = async (oid) => {
  try {
    await ElMessageBox.confirm('拒绝该订单？', '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    await rejectOrderApi(oid)
    ElMessage.success('已拒绝')
    await loadOrders()
  } catch {
    ElMessage.error('操作失败')
  }
}

// 完成订单（商家标记订单为已完成）
const completeOrder = async (oid) => {
  try {
    await ElMessageBox.confirm('确认该订单已完成？', '提示', { type: 'info' })
  } catch {
    return
  }
  try {
    await completeOrderApi(oid)
    ElMessage.success('订单已完成')
    await loadOrders()
  } catch {
    ElMessage.error('操作失败')
  }
}

// 查看详情（后续可扩展弹窗）
const viewDetail = (oid) => {
  ElMessage.info(`订单详情: ${oid}`)
}

// 辅助函数
const getOrderStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }
  return map[status] || 'default'
}
const getOrderStatusText = (status) => {
  const map = { 0: '待支付', 1: '已支付', 2: '已取消', 3: '已完成' }
  return map[status] || '未知'
}
const formatDate = (date) => date ? new Date(date).toLocaleDateString() : ''
const formatDateTime = (dateTime) => dateTime ? new Date(dateTime).toLocaleString() : ''
const isNeedCoach = (needCoach) => needCoach === true || needCoach === 1 || needCoach === '1' || needCoach === 'true'

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.merchant-orders {
  min-height: 100%;
}
.page-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #303133;
}
.order-filter {
  margin-bottom: 20px;
}
.order-table {
  margin-bottom: 20px;
}
.price {
  font-weight: bold;
  color: #F56C6C;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.alert {
  margin-bottom: 20px;
}
</style>