<template>
  <div class="friend-requests">
    <h2 class="page-title">好友请求</h2>
    <el-table :data="requests" v-loading="loading">
      <el-table-column prop="fromNickname" label="发送者" width="200" />
      <el-table-column prop="message" label="验证信息" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <template v-if="row.status === 0">
            <el-button size="small" type="success" @click="handleAccept(row.id)">同意</el-button>
            <el-button size="small" type="danger" @click="handleReject(row.id)">拒绝</el-button>
          </template>
          <span v-else>{{ row.status === 1 ? '已同意' : '已拒绝' }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const requests = ref([])
const loading = ref(false)

const loadRequests = async () => {
  loading.value = true
  try {
    const res = await request.get('/user/friend/requests')
    console.log('好友请求原始返回:', res) // 关键调试

    // 兼容多层 data 包裹
    let list = []
    if (Array.isArray(res)) {
      list = res
    } else if (res && Array.isArray(res.data)) {
      list = res.data
    } else if (res && res.data && Array.isArray(res.data.data)) {
      list = res.data.data
    }
    console.log('解析后的请求列表:', list)
    requests.value = list
  } catch (e) {
    ElMessage.error('获取好友请求失败')
  } finally {
    loading.value = false
  }
}

const handleAccept = async (id) => {
  try {
    await request.put(`/user/friend/request/${id}/accept`)
    ElMessage.success('已同意')
    loadRequests()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleReject = async (id) => {
  try {
    await request.put(`/user/friend/request/${id}/reject`)
    ElMessage.success('已拒绝')
    loadRequests()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(loadRequests)
</script>

<style scoped>
.page-title {
  margin-bottom: 20px;
}
</style>