<template>
  <div class="user-orders">
    <h2 class="page-title">我的订单</h2>
    <div class="order-filter">
      <el-radio-group v-model="statusFilter" @change="loadOrders">
        <el-radio-button label="all">全部</el-radio-button>
        <el-radio-button label="0">待支付</el-radio-button>
        <el-radio-button label="1">已支付</el-radio-button>
        <el-radio-button label="2">已取消</el-radio-button>
        <el-radio-button label="3">已完成</el-radio-button>
      </el-radio-group>
    </div>

    <el-table :data="orders" v-loading="loading" class="order-table">
      <el-table-column prop="orderNo" label="订单编号" width="200" />
      <el-table-column prop="venueName" label="场馆名称" />
      <el-table-column prop="bookDate" label="预订日期" width="120" />
      <el-table-column prop="timeSlot" label="时间段" width="120" />
      <el-table-column prop="hours" label="小时数" width="80" />
      <el-table-column prop="totalPrice" label="总价" width="100">
        <template #default="{ row }">¥{{ row.totalPrice }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getOrderStatusType(row.status)">{{ getOrderStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <!-- <el-button size="small" @click="viewDetail(row.oid)">详情</el-button> -->
          <el-button v-if="row.status === 0" size="small" type="success" @click="handlePay(row.oid)">支付</el-button>
          <el-button v-if="row.status === 0" size="small" type="danger" @click="handleCancel(row.oid)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        @current-change="loadOrders"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserOrders, payOrder, cancelOrder } from '@/api/order'

const orders = ref([])
const loading = ref(false)
const statusFilter = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await getUserOrders({
      status: statusFilter.value === 'all' ? undefined : parseInt(statusFilter.value)
    })
    // 兼容不同返回格式：可能是 { data: [...] } 或直接是数组
    const list = res.data || res
    if (Array.isArray(list)) {
      orders.value = list
      total.value = list.length
    } else {
      orders.value = []
      total.value = 0
    }
  } catch (error) {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

const handlePay = async (oid) => {
  try {
    await payOrder(oid)
    ElMessage.success('支付成功')
    await loadOrders()
  } catch (error) {
    ElMessage.error('支付失败')
  }
}

const handleCancel = async (oid) => {
  try {
    await ElMessageBox.confirm('确认取消该订单？', '提示', { type: 'warning' })
    await cancelOrder(oid)
    ElMessage.success('已取消')
    await loadOrders()
  } catch (error) {
    // 用户取消确认时会进入此处，不做任何提示
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

const viewDetail = (oid) => {
  ElMessage.info(`订单详情: ${oid}`)
}

const getOrderStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }
  return map[status] || 'default'
}
const getOrderStatusText = (status) => {
  const map = { 0: '待支付', 1: '已支付', 2: '已取消', 3: '已完成' }
  return map[status] || '未知'
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.user-orders {
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
</style>