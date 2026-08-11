<template>
  <div class="post-management">
    <el-card class="main-card">
      <template #header>
        <div class="header">
          <span class="title">帖子管理</span>
          <el-input
            v-model="searchKeyword"
            placeholder="搜索标题或内容"
            clearable
            style="width: 260px"
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch" />
            </template>
          </el-input>
        </div>
      </template>

      <div v-loading="loading" class="post-cards">
        <el-card v-for="post in posts.list" :key="post.pid" class="post-card" shadow="hover">
          <div class="post-header">
            <h3 class="post-title">{{ post.title }}</h3>
            <div class="post-meta">
              <span class="author">作者：{{ post.authorName || post.userId }}</span>
              <span class="time">{{ formatDateTime(post.createTime) }}</span>
            </div>
          </div>
          <div class="post-stats">
            <div class="stat-item">
              <span class="stat-label">浏览</span>
              <span class="stat-value">{{ post.viewCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">点赞</span>
              <span class="stat-value">{{ post.likeCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">评论</span>
              <span class="stat-value">{{ post.commentCount }}</span>
            </div>
          </div>
          <div class="post-actions">
            <el-button size="small" type="primary" plain @click="viewDetail(post.pid)">
              查看详情
            </el-button>
            <el-button
              v-if="post.status === 0"
              size="small"
              type="danger"
              plain
              @click="banPost(post.pid)"
            >
              封禁
            </el-button>
            <el-button
              v-else
              size="small"
              type="success"
              plain
              @click="unbanPost(post.pid)"
            >
              解封
            </el-button>
          </div>
        </el-card>

        <div v-if="posts.list.length === 0 && !loading" class="empty-placeholder">
          暂无数据
        </div>
      </div>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          :total="pagination.total"
          @size-change="loadPosts"
          @current-change="loadPosts"
        />
      </div>
    </el-card>

    <!-- 详情弹窗（同上，保持功能） -->
    <el-dialog v-model="detailVisible" title="帖子详情" width="800px">
      <div v-if="currentPost" class="post-detail">
        <h3>{{ currentPost.title }}</h3>
        <div class="detail-meta">
          <span>作者：{{ currentPost.authorName || currentPost.userId }}</span>
          <span>发布时间：{{ formatDateTime(currentPost.createTime) }}</span>
          <span>浏览量：{{ currentPost.viewCount }}</span>
          <span>点赞数：{{ currentPost.likeCount }}</span>
          <span>评论数：{{ currentPost.commentCount }}</span>
        </div>
        <div class="detail-content" v-html="currentPost.content"></div>
        <el-divider>评论</el-divider>
        <div v-if="comments.length === 0" class="no-comment">暂无评论</div>
        <div v-for="c in comments" :key="c.cid" class="comment-item">
          <div class="comment-user">{{ c.userName || c.userId }}：</div>
          <div class="comment-content">{{ c.content }}</div>
          <div class="comment-time">{{ formatDateTime(c.createTime) }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getPostList, banPost as banPostApi, unbanPost as unbanPostApi, getPostDetail, getCommentsByPost } from '@/api/admin'

const searchKeyword = ref('')
const posts = reactive({ list: [], total: 0 })
const loading = ref(false)
const pagination = reactive({ page: 1, size: 10 })
const detailVisible = ref(false)
const currentPost = ref(null)
const comments = ref([])

const loadPosts = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      keyword: searchKeyword.value || undefined
    }
    const res = await getPostList(params)
    posts.list = res.list || []
    posts.total = res.total || 0
  } catch (error) {
    console.error('加载帖子失败', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadPosts()
}

const viewDetail = async (pid) => {
  try {
    const post = await getPostDetail(pid)
    currentPost.value = post
    const commentRes = await getCommentsByPost(pid)
    comments.value = commentRes || []
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const banPost = async (pid) => {
  await ElMessageBox.confirm('确认封禁此帖子？', '提示', { type: 'warning' })
  try {
    await banPostApi(pid)
    ElMessage.success('封禁成功')
    await loadPosts()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const unbanPost = async (pid) => {
  await ElMessageBox.confirm('确认解封此帖子？', '提示', { type: 'warning' })
  try {
    await unbanPostApi(pid)
    ElMessage.success('解封成功')
    await loadPosts()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.post-management {
  padding: 20px;
}
.main-card {
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header .title {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}
.post-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
  margin: 16px 0;
}
.post-card {
  transition: all 0.2s ease;
  border-radius: 8px;
}
.post-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.post-header {
  margin-bottom: 12px;
}
.post-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.post-meta {
  font-size: 12px;
  color: #909399;
}
.post-meta span {
  margin-right: 12px;
}
.post-stats {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
  padding: 8px 0;
  border-top: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
}
.stat-item {
  flex: 1;
  text-align: center;
}
.stat-label {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}
.stat-value {
  font-size: 16px;
  font-weight: 500;
  color: #409eff;
}
.post-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.empty-placeholder {
  text-align: center;
  padding: 40px 0;
  color: #909399;
  font-size: 14px;
}
.post-detail h3 {
  margin-top: 0;
}
.detail-meta {
  margin-bottom: 16px;
  font-size: 14px;
  color: #606266;
}
.detail-meta span {
  margin-right: 16px;
}
.detail-content {
  line-height: 1.6;
  margin-bottom: 20px;
  white-space: pre-wrap;
}
.comment-item {
  border-bottom: 1px solid #eee;
  padding: 12px 0;
}
.comment-user {
  font-weight: bold;
}
.comment-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
.no-comment {
  text-align: center;
  color: #999;
}
</style>