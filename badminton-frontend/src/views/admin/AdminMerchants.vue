<template>
  <div class="admin-merchants">
    <h2 class="page-title">商家管理</h2>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索商家（登录名/公司名称/联系人）"
        class="search-input"
        clearable
        @clear="searchMerchants"
        @keyup.enter="searchMerchants"
      >
        <template #append>
          <el-button @click="searchMerchants"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
    </div>

    <!-- 商家列表 -->
    <el-table :data="merchants" v-loading="loading" class="merchant-table">
      <el-table-column prop="username" label="登录名" />
      <el-table-column prop="companyName" label="公司名称" />
      <el-table-column prop="contactPerson" label="联系人" />
      <el-table-column prop="contactPhone" label="联系电话" />
      <el-table-column prop="email" label="邮箱" />
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
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <template v-if="row.status === 0">
            <el-button size="small" type="success" @click="handleAudit(row.mid, 1)">通过</el-button>
            <el-button size="small" type="danger" @click="handleAudit(row.mid, 2)">拒绝</el-button>
          </template>
          <template v-else-if="row.status === 1">
            <el-button size="small" type="danger" @click="handleToggle(row.mid, 2)">禁用</el-button>
          </template>
          <template v-else-if="row.status === 2">
            <el-button size="small" type="success" @click="handleToggle(row.mid, 1)">启用</el-button>
          </template>
          <el-button size="small" @click="viewDetail(row)">查看详情</el-button>
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

    <!-- 商家详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="商家详情"
      width="800px"
      class="merchant-detail-dialog"
    >
      <div v-if="currentMerchant">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="公司名称">{{ currentMerchant.companyName }}</el-descriptions-item>
          <el-descriptions-item label="登录名">{{ currentMerchant.username }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ currentMerchant.contactPerson }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentMerchant.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentMerchant.email }}</el-descriptions-item>
          <el-descriptions-item label="地址">{{ currentMerchant.address }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentMerchant.status)">{{ getStatusText(currentMerchant.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(currentMerchant.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="营业时间" :span="2">{{ formatBusinessHours(currentMerchant.businessHours) }}</el-descriptions-item>
        </el-descriptions>

        <!-- 待审核修改内容（仅在待审核状态下显示） -->
        <template v-if="currentMerchant.status === 0 && currentMerchant.isSubmitted === 1 && currentMerchant.pendingData">
          <el-divider content-position="left">待审核修改内容</el-divider>
          <el-descriptions :column="1" border>
            <el-descriptions-item v-if="currentMerchant.pendingData.companyName" label="公司名称">
              <span class="old-value">{{ currentMerchant.companyName }}</span> → 
              <span class="new-value">{{ currentMerchant.pendingData.companyName }}</span>
            </el-descriptions-item>
            <el-descriptions-item v-if="currentMerchant.pendingData.contactPerson" label="联系人">
              <span class="old-value">{{ currentMerchant.contactPerson }}</span> → 
              <span class="new-value">{{ currentMerchant.pendingData.contactPerson }}</span>
            </el-descriptions-item>
            <el-descriptions-item v-if="currentMerchant.pendingData.contactPhone" label="联系电话">
              <span class="old-value">{{ currentMerchant.contactPhone }}</span> → 
              <span class="new-value">{{ currentMerchant.pendingData.contactPhone }}</span>
            </el-descriptions-item>
            <el-descriptions-item v-if="currentMerchant.pendingData.email" label="邮箱">
              <span class="old-value">{{ currentMerchant.email }}</span> → 
              <span class="new-value">{{ currentMerchant.pendingData.email }}</span>
            </el-descriptions-item>
            <el-descriptions-item v-if="currentMerchant.pendingData.address" label="地址">
              <span class="old-value">{{ currentMerchant.address }}</span> → 
              <span class="new-value">{{ currentMerchant.pendingData.address }}</span>
            </el-descriptions-item>
            <el-descriptions-item v-if="currentMerchant.pendingData.businessLicense" label="营业执照">
              <span class="old-value">当前图片</span> → 
              <el-image :src="currentMerchant.pendingData.businessLicense" style="width: 100px; height: 80px; margin-top: 4px;" />
            </el-descriptions-item>
            <el-descriptions-item v-if="currentMerchant.pendingData.businessHours" label="营业时间">
              <span class="old-value">{{ formatBusinessHours(currentMerchant.businessHours) }}</span> → 
              <span class="new-value">{{ formatBusinessHours(currentMerchant.pendingData.businessHours) }}</span>
            </el-descriptions-item>
            <el-descriptions-item v-if="currentMerchant.pendingData.facilities" label="设施">
              <span class="old-value">{{ currentMerchant.facilities || '未设置' }}</span> → 
              <span class="new-value">{{ currentMerchant.pendingData.facilities }}</span>
            </el-descriptions-item>
            <el-descriptions-item v-if="currentMerchant.pendingData.detailIntro" label="详细介绍">
              <span class="old-value">{{ currentMerchant.detailIntro || '未设置' }}</span> → 
              <span class="new-value">{{ currentMerchant.pendingData.detailIntro }}</span>
            </el-descriptions-item>
            <el-descriptions-item v-if="currentMerchant.pendingData.videos" label="视频链接">
              <span class="old-value">{{ currentMerchant.videos || '未设置' }}</span> → 
              <span class="new-value">{{ currentMerchant.pendingData.videos }}</span>
            </el-descriptions-item>
            <el-descriptions-item v-if="currentMerchant.pendingData.rules" label="使用规则">
              <span class="old-value">{{ currentMerchant.rules || '未设置' }}</span> → 
              <span class="new-value">{{ currentMerchant.pendingData.rules }}</span>
            </el-descriptions-item>
            <el-descriptions-item v-if="currentMerchant.pendingData.venuePhotos" label="场馆图片">
              <span class="old-value">当前图片</span> → 
              <div class="photo-preview">
                <div v-for="(url, idx) in currentMerchant.pendingData.venuePhotos.split(',')" :key="idx" class="thumbnail-wrapper" @click="openPreview(currentMerchant.pendingData.venuePhotos.split(','), idx)">
                  <img :src="url" class="thumbnail" />
                </div>
              </div>
            </el-descriptions-item>
          </el-descriptions>
        </template>

        <el-divider content-position="left">场馆详情</el-divider>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="设施">{{ currentMerchant.facilities || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="详细介绍">{{ currentMerchant.detailIntro || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="使用规则">{{ currentMerchant.rules || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="视频链接">
            <div v-if="currentMerchant.videos">
              <a v-for="(url, idx) in currentMerchant.videos.split(',')" :key="idx" :href="url" target="_blank">{{ url }}</a>
            </div>
            <span v-else>未设置</span>
          </el-descriptions-item>
          <el-descriptions-item label="场馆图片">
            <div class="photo-preview" v-if="currentMerchant.venuePhotos">
              <div
                v-for="(url, idx) in currentMerchant.venuePhotos.split(',')"
                :key="idx"
                class="thumbnail-wrapper"
                @click="openPreview(currentMerchant.venuePhotos.split(','), idx)"
              >
                <img :src="url" class="thumbnail" />
              </div>
            </div>
            <span v-else>未设置</span>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">营业执照</el-divider>
        <div class="license-wrapper">
          <el-image
            v-if="currentMerchant.businessLicense"
            :src="currentMerchant.businessLicense"
            fit="contain"
            style="width: 300px; height: 200px; cursor: pointer"
            :preview-src-list="[currentMerchant.businessLicense]"
          />
          <span v-else>未上传</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="图片预览"
      width="auto"
      class="preview-dialog"
      destroy-on-close
    >
      <div class="preview-container">
        <el-carousel
          :initial-index="currentPreviewIndex"
          :autoplay="false"
          :loop="false"
          indicator-position="outside"
          height="500px"
        >
          <el-carousel-item v-for="(url, idx) in previewImages" :key="idx">
            <img :src="url" class="preview-image" />
          </el-carousel-item>
        </el-carousel>
        <div class="image-count">{{ currentPreviewIndex + 1 }} / {{ previewImages.length }}</div>
      </div>
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getMerchantList, auditMerchant, updateMerchantStatus } from '@/api/admin'

const merchants = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const detailVisible = ref(false)
const currentMerchant = ref(null)

const previewVisible = ref(false)
const previewImages = ref([])
const currentPreviewIndex = ref(0)

const loadMerchants = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined
    }
    const res = await getMerchantList(params)
    merchants.value = res.list || []
    total.value = res.total || 0
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const searchMerchants = () => {
  currentPage.value = 1
  loadMerchants()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadMerchants()
}
const handleCurrentChange = (page) => {
  currentPage.value = page
  loadMerchants()
}

const handleAudit = async (mid, status) => {
  const action = status === 1 ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确认${action}该商家？`, '提示', { type: 'warning' })
    await auditMerchant({ mid, status })
    ElMessage.success(`已${action}`)
    await loadMerchants()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(`${action}失败`)
  }
}

const handleToggle = async (mid, status) => {
  const action = status === 1 ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确认${action}该商家？`, '提示', { type: 'warning' })
    await updateMerchantStatus(mid, status)
    ElMessage.success(`${action}成功`)
    await loadMerchants()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error(`${action}失败`)
  }
}

const viewDetail = (merchant) => {
  let pendingData = null
  if (merchant.pendingData) {
    try {
      pendingData = JSON.parse(merchant.pendingData)
    } catch (e) {
      console.error('解析 pendingData 失败', e)
    }
  }
  currentMerchant.value = { ...merchant, pendingData }
  detailVisible.value = true
}

const openPreview = (images, index) => {
  previewImages.value = images
  currentPreviewIndex.value = index
  previewVisible.value = true
}

const formatBusinessHours = (businessHours) => {
  if (!businessHours) return '未设置'
  try {
    const hoursList = JSON.parse(businessHours)
    if (!hoursList.length) return '未设置'
    const dayMap = { 1: '周一', 2: '周二', 3: '周三', 4: '周四', 5: '周五', 6: '周六', 7: '周日' }
    return hoursList.map(item => {
      const startDay = dayMap[item.startDay]
      const endDay = dayMap[item.endDay]
      if (startDay === endDay) return `${startDay} ${item.startTime}-${item.endTime}`
      return `${startDay}到${endDay} ${item.startTime}-${item.endTime}`
    }).join('；')
  } catch (e) {
    return '格式错误'
  }
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}
const getStatusText = (status) => {
  const map = { 0: '待审核', 1: '正常', 2: '禁用' }
  return map[status] || '未知'
}
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

onMounted(() => {
  loadMerchants()
})
</script>

<style scoped>
/* 原有样式保持不变，增加新旧值对比样式 */
.old-value {
  color: #909399;
  text-decoration: line-through;
}
.new-value {
  color: #67c23a;
  font-weight: 500;
}
</style>