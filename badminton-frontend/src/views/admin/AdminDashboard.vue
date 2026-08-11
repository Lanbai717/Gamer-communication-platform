<template>
  <div class="admin-dashboard">
    <h2 class="page-title">系统概览</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.userCount }}</div>
          <div class="stat-label">总用户数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.merchantCount }}</div>
          <div class="stat-label">商家总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.postCount }}</div>
          <div class="stat-label">帖子总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ stats.orderCount }}</div>
          <div class="stat-label">订单总数</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 待审核商家提醒 -->
    <el-card style="margin-top: 20px">
      <template #header>待审核商家</template>
      <div class="pending-items">
        <div class="pending-item">
          <span>待审核商家数量：</span>
          <span class="pending-count">{{ pendingMerchantCount }}</span>
          <el-button type="primary" size="small" @click="goToMerchantAudit">前往审核</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSystemStats, getPendingMerchants } from '@/api/admin'

const router = useRouter()
const stats = ref({
  userCount: 0,
  merchantCount: 0,
  postCount: 0,
  orderCount: 0
})

const pendingMerchantCount = ref(0)

const loadStats = async () => {
  try {
    const res = await getSystemStats()
    stats.value = res
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

const loadPendingMerchants = async () => {
  try {
    const merchants = await getPendingMerchants()
    pendingMerchantCount.value = Array.isArray(merchants) ? merchants.length : 0
  } catch (error) {
    console.error('加载待审核商家数据失败', error)
  }
}

const goToMerchantAudit = () => {
  router.push('/admin/merchants')
}

onMounted(() => {
  loadStats()
  loadPendingMerchants()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}
.stat-card {
  text-align: center;
}
.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
}
.stat-label {
  margin-top: 10px;
  color: #909399;
}
.pending-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.pending-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.pending-count {
  font-size: 24px;
  font-weight: bold;
  color: #e6a23c;
  margin-left: 10px;
}
</style>