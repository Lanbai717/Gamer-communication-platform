<template>
  <div class="post-detail">
    <el-card v-loading="loading">
      <template #header>
        <div class="header">
          <h2>{{ post.title }}</h2>
          <div class="meta">
            <span>作者：{{ post.authorName || post.userId }}</span>
            <span>时间：{{ formatDate(post.createTime) }}</span>
            <span>浏览：{{ post.viewCount }}</span>
            <span>点赞：{{ post.likeCount }}</span>
          </div>
        </div>
      </template>
      <div class="content" v-html="post.content"></div>
      <div class="actions">
        <el-button @click="handleLike" :loading="likeLoading">点赞</el-button>
      </div>
    </el-card>

    <!-- 评论区 -->
    <el-card style="margin-top: 20px">
      <template #header><strong>评论</strong></template>
      <el-form ref="commentFormRef" :model="commentForm" :rules="commentRules" label-width="80px">
        <el-form-item label="评论" prop="content">
          <el-input v-model="commentForm.content" type="textarea" :rows="2" placeholder="请输入评论" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitComment" :loading="commentLoading">发表评论</el-button>
        </el-form-item>
      </el-form>
      <div v-for="c in comments" :key="c.cid" class="comment-item">
        <div class="comment-user">{{ c.userName || c.userId }}：</div>
        <div class="comment-content">{{ c.content }}</div>
        <div class="comment-time">{{ formatDate(c.createTime) }}</div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPostDetail, likePost } from '@/api/post'
import { getCommentsByPost, createComment } from '@/api/comment'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const pid = route.params.pid

const post = ref({})
const comments = ref([])
const loading = ref(false)
const likeLoading = ref(false)
const commentLoading = ref(false)

const commentForm = ref({ content: '' })
const commentFormRef = ref(null)  // 必须声明
const commentRules = {
  content: [{ required: true, message: '请输入评论', trigger: 'blur' }]
}

const formatDate = (date) => date ? new Date(date).toLocaleString() : ''

const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await getPostDetail(pid)
    post.value = res
    const commentRes = await getCommentsByPost(pid)
    comments.value = commentRes || []
  } catch (error) {
    ElMessage.error('加载失败')
    router.push('/forum')
  } finally {
    loading.value = false
  }
}

const handleLike = async () => {
  likeLoading.value = true
  try {
    const res = await likePost(pid)
    // 后端返回 { liked: true/false, likeCount: number }
    post.value.likeCount = res.likeCount
    ElMessage.success(res.liked ? '点赞成功' : '已取消点赞')
  } finally {
    likeLoading.value = false
  }
}

const submitComment = async () => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  await commentFormRef.value.validate()
  commentLoading.value = true
  try {
    await createComment({ postId: pid, content: commentForm.value.content })
    ElMessage.success('评论成功')
    commentForm.value.content = ''
    const newComments = await getCommentsByPost(pid)
    comments.value = newComments || []
  } finally {
    commentLoading.value = false
  }
}

onMounted(fetchDetail)
</script>

<style scoped>
.post-detail { max-width: 800px; margin: 0 auto; }
.header h2 { margin-bottom: 10px; }
.meta { color: #999; font-size: 14px; }
.meta span { margin-right: 15px; }
.content { margin: 20px 0; line-height: 1.8; }
.actions { text-align: center; margin: 20px 0; }
.comment-item { border-bottom: 1px solid #eee; padding: 12px 0; }
.comment-user { font-weight: bold; }
.comment-time { font-size: 12px; color: #999; margin-top: 5px; }
</style>