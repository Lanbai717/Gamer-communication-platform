<template>
  <div class="find-partner">
    <h2 class="page-title">为你推荐的球友</h2>

    <!-- 正在加载 -->
    <div v-if="loading" class="status-box">
      <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      <p>正在为你匹配最佳球友…</p>
    </div>

    <!-- 没有推荐数据 -->
    <el-empty
      v-else-if="list.length === 0"
      description="暂无推荐，请先完善个人资料或等待更多用户加入"
    />

    <!-- 推荐列表 -->
    <el-row v-else :gutter="20">
      <el-col v-for="user in list" :key="user.uid" :span="8" :md="6">
        <el-card class="partner-card">
          <div class="card-avatar">
            <el-avatar :size="64" :src="user.avatar || defaultAvatar" />
          </div>
          <h3 class="card-nick">{{ user.nickname }}</h3>
          <p class="card-level">🏸 {{ user.levelText }}</p>
          <p class="card-loc">📍 {{ user.location || '未设置地区' }}</p>
          <p class="card-score">✨ 匹配度 {{ (user.score * 100).toFixed(1) }}%</p>
          <el-button type="primary" size="small" @click="addFriend(user.uid)">
            添加好友
          </el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'

const list = ref([])
const loading = ref(true)

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const loadMatches = async () => {
  loading.value = true
  try {
    const res = await request.get('/user/match/recommend')
    const data = res.data ?? res
    if (Array.isArray(data)) {
      list.value = data
    } else {
      list.value = []
      console.warn('推荐数据格式异常:', res)
    }
  } catch (e) {
    ElMessage.error('获取推荐失败，请稍后再试')
    console.error('推荐接口异常:', e)
  } finally {
    loading.value = false
  }
}

const addFriend = async (toUid) => {
  try {
    // 弹出确认框，可选填写验证消息
    await ElMessageBox.prompt('发送好友申请', '添加好友', {
      confirmButtonText: '发送',
      cancelButtonText: '取消',
      inputPlaceholder: '你好，想和你约球！',
      inputValue: '你好，想和你约球！'
    })
    // 用户点击发送后执行
    await request.post('/user/friend/request', {
      toUid: toUid,
      message: '你好，想和你约球！'  // 可替换为用户输入的内容
    })
    ElMessage.success('好友申请已发送')
  } catch (e) {
    // 用户取消或请求失败
    if (e !== 'cancel' && e?.response?.status !== 401) {
      ElMessage.error(e?.response?.data?.message || '发送失败，请稍后再试')
    }
  }
}

onMounted(loadMatches)
</script>

<style scoped>
.page-title {
  margin-bottom: 24px;
}
.status-box {
  text-align: center;
  padding: 60px 0;
  color: #909399;
}
.partner-card {
  margin-top: 20px;
  text-align: center;
}
.card-avatar {
  margin-bottom: 10px;
}
.card-nick {
  margin: 10px 0 6px;
}
.card-level,
.card-loc,
.card-score {
  margin: 4px 0;
  color: #606266;
}
.card-score {
  color: #e6a23c;
  font-weight: 500;
}
</style>