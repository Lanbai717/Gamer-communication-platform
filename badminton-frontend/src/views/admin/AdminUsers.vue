<template>
  <div class="admin-users">
    <h2 class="page-title">用户管理</h2>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户（用户名/昵称/邮箱/手机号）"
        class="search-input"
        clearable
        @clear="searchUsers"
        @keyup.enter="searchUsers"
      >
        <template #append>
          <el-button @click="searchUsers"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
    </div>

    <!-- 用户列表 -->
    <el-table :data="users" v-loading="loading" class="user-table">
      <el-table-column prop="uid" label="用户ID" width="80" v-if="false" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="phone" label="手机号" />
      <!-- 技术水平列：显示映射后的文字 -->
      <el-table-column label="技术水平" width="130">
        <template #default="{ row }">
          {{ getSkillLevelText(row.skillLevel) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="viewUserDetail(row.uid)">查看详情</el-button>
          <el-button
            size="small"
            type="danger"
            :loading="statusLoading === row.uid"
            @click="toggleUserStatus(row.uid, row.status)"
          >
            {{ row.status === 0 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
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

    <!-- 用户详情弹窗 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="500px">
      <div v-if="currentUser" class="user-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="用户名">
            {{ currentUser.username }}
          </el-descriptions-item>
          <el-descriptions-item label="昵称">
            {{ currentUser.nickname || '未设置' }}
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">
            {{ currentUser.email || '未设置' }}
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            {{ currentUser.phone || '未设置' }}
          </el-descriptions-item>
          <el-descriptions-item label="技术水平">
            {{ getSkillLevelText(currentUser.skillLevel) }}
          </el-descriptions-item>
          <el-descriptions-item label="性别">
            {{ currentUser.gender || '未设置' }}
          </el-descriptions-item>
          <el-descriptions-item label="生日">
            {{ currentUser.birthday || '未设置' }}
          </el-descriptions-item>
          <el-descriptions-item label="个性签名">
            {{ currentUser.signature || '未设置' }}
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">
            {{ formatDateTime(currentUser.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentUser.status)">
              {{ getStatusText(currentUser.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getUserList, updateUserStatus, getUserDetail } from '@/api/admin'

// 数据
const users = ref([])
const loading = ref(false)
const statusLoading = ref(null)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 详情弹窗
const detailVisible = ref(false)
const currentUser = ref(null)

// 技术水平映射
const skillLevelMap = {
  '0': '国际级运动健将',
  '1': '运动健将',
  '2': '一级运动员',
  '3': '二级运动员',
  '4': '三级运动员',
  '5': '业余高级',
  '6': '业余中级',
  '7': '业余初级'
}

// 将技术水平数字/字符串转为文字
const getSkillLevelText = (level) => {
  if (level === undefined || level === null || level === '') return '未设置'
  const key = String(level)
  return skillLevelMap[key] || '未知'
}

// 加载用户列表
const loadUsers = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined
    }
    const res = await getUserList(params)
    users.value = res.list || []
    total.value = res.total || 0
  } catch (error) {
    console.error('加载用户列表失败', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const searchUsers = () => {
  currentPage.value = 1
  loadUsers()
}

// 分页
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadUsers()
}
const handleCurrentChange = (page) => {
  currentPage.value = page
  loadUsers()
}

// 切换用户状态
const toggleUserStatus = async (uid, currentStatus) => {
  const newStatus = currentStatus === 0 ? 1 : 0
  const action = newStatus === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
      type: 'warning'
    })
    statusLoading.value = uid
    await updateUserStatus(uid, newStatus)
    ElMessage.success(`${action}成功`)
    await loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  } finally {
    statusLoading.value = null
  }
}

// 查看详情
const viewUserDetail = async (uid) => {
  try {
    const res = await getUserDetail(uid)
    currentUser.value = res
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取用户详情失败')
  }
}

// 辅助函数
const getStatusType = (status) => status === 0 ? 'success' : 'danger'
const getStatusText = (status) => status === 0 ? '正常' : '禁用'
const formatDateTime = (dateTime) => dateTime ? new Date(dateTime).toLocaleString('zh-CN') : ''

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-users {
  min-height: 100%;
}
.page-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #303133;
}
.search-bar {
  margin-bottom: 20px;
}
.search-input {
  width: 300px;
}
.user-table {
  margin-bottom: 20px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.user-detail {
  padding: 0 10px;
}
</style>