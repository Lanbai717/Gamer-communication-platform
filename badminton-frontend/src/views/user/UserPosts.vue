<template>
  <div class="posts">
    <el-card>
      <template #header>
        <div class="header">
          <strong>论坛帖子</strong>
          <el-button type="primary" @click="goCreate" size="small">发布新帖</el-button>
        </div>
      </template>
      <!-- 删除运动类型筛选框 -->
      <el-table :data="posts" v-loading="loading">
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <router-link :to="`/forum/${row.pid}`">{{ row.title }}</router-link>
          </template>
        </el-table-column>
        <el-table-column prop="authorName" label="作者" width="120" />
        <el-table-column prop="createTime" label="发布时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column prop="likeCount" label="点赞" width="80" />
        <el-table-column prop="commentCount" label="评论" width="80" />
      </el-table>
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        layout="total, prev, pager, next"
        @current-change="loadPosts"
        style="margin-top: 20px"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPostList } from '@/api/post'

const router = useRouter()
const posts = ref([])
const loading = ref(false)
const pagination = reactive({ page: 1, size: 10, total: 0 })

const formatDate = (date) => date ? new Date(date).toLocaleString() : ''

const loadPosts = async () => {
  loading.value = true
  try {
    const res = await getPostList({ page: pagination.page, size: pagination.size })
    posts.value = res.list || res || []
    pagination.total = res.total || posts.value.length
  } catch (error) {
    console.error('加载帖子失败', error)
  } finally {
    loading.value = false
  }
}

const goCreate = () => {
  router.push('/forum/create')
}

onMounted(loadPosts)
</script>