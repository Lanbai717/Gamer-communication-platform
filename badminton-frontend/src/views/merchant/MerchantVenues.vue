<template>
  <div class="merchant-venues">
    <h2 class="page-title">场地管理</h2>

    <!-- 未审核警告 -->
    <div v-if="!isApproved" class="alert">
      <el-alert title="账号未通过审核，无法进行场地管理" type="warning" :closable="false" />
    </div>

    <template v-else>
      <div class="venue-actions">
        <el-button type="primary" @click="openCreateDialog">添加场地</el-button>
      </div>

      <el-table 
        :data="sortedVenues" 
        v-loading="loading" 
        class="venue-table" 
        stripe
        style="width: 100%"
      >
        <el-table-column prop="code" label="场地编号" width="150" />
        <el-table-column prop="pricePerHour" label="价格(元/小时)" width="130">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.pricePerHour }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述/备注" min-width="200" show-overflow-tooltip />
        <el-table-column label="图片" width="140">
          <template #default="{ row }">
            <div class="photo-preview" v-if="row.photos">
              <div 
                class="thumbnail-wrapper"
                @click="openImagePreview(row.photos.split(','))"
              >
                <img 
                  :src="row.photos.split(',')[0]" 
                  class="thumbnail"
                  alt="场地图片"
                />
                <span class="photo-count" v-if="row.photos.split(',').length > 1">
                  +{{ row.photos.split(',').length - 1 }}
                </span>
              </div>
            </div>
            <span v-else class="no-photo">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.vid)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          :total="total"
          @size-change="loadVenues"
          @current-change="loadVenues"
        />
      </div>
    </template>

    <!-- 自定义图片预览对话框 -->
    <el-dialog
      v-model="previewVisible"
      title="场地图片"
      width="800px"
      class="image-preview-dialog"
      :append-to-body="true"
    >
      <div class="preview-container">
        <el-carousel 
          :autoplay="false" 
          :loop="false" 
          indicator-position="outside"
          height="500px"
        >
          <el-carousel-item v-for="(url, idx) in previewImages" :key="idx">
            <img :src="url" class="preview-image" />
          </el-carousel-item>
        </el-carousel>
        <div class="image-count">{{ currentIndex + 1 }} / {{ previewImages.length }}</div>
      </div>
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 创建/编辑场地对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑场地' : '添加场地'"
      width="600px"
      class="venue-dialog"
    >
      <el-form :model="venueForm" :rules="rules" ref="venueFormRef" label-width="100px">
        <el-form-item label="场地编号" prop="code">
          <el-input v-model="venueForm.code" placeholder="如：A001、VIP-01" />
        </el-form-item>
        <el-form-item label="价格(元/小时)" prop="pricePerHour">
          <el-input-number v-model="venueForm.pricePerHour" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述/备注" prop="description">
          <el-input v-model="venueForm.description" type="textarea" rows="3" placeholder="可介绍场地设施、特色、位置等" />
        </el-form-item>
        <el-form-item label="场地图片">
          <el-upload
            class="photo-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :on-remove="handleRemove"
            :file-list="fileList"
            :before-upload="beforeUpload"
            list-type="picture-card"
            multiple
            :limit="9"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="form-tip">支持jpg/png，每张不超过10MB，最多上传9张，第一张作为封面</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveVenue" :loading="submitting">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getMyVenues, addVenue, updateVenue, deleteVenue as deleteVenueApi } from '@/api/venue'

const userStore = useUserStore()
const isApproved = computed(() => userStore.userInfo?.status === 1)

// 上传配置
const uploadUrl = 'http://localhost:8080/upload/image'
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

const venues = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const currentVid = ref(null)
const submitting = ref(false)

// 图片预览
const previewVisible = ref(false)
const previewImages = ref([])
const currentIndex = ref(0)

const venueForm = reactive({
  code: '',
  pricePerHour: null,
  description: '',
  photos: ''
})

const fileList = ref([])  // 用于 el-upload 展示已上传的图片

const rules = {
  code: [{ required: true, message: '请输入场地编号', trigger: 'blur' }],
  pricePerHour: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const venueFormRef = ref(null)

// 按场地编号排序（字符串排序，自然排序）
const sortedVenues = computed(() => {
  return [...venues.value].sort((a, b) => {
    return a.code.localeCompare(b.code, undefined, { numeric: true, sensitivity: 'base' })
  })
})

// 打开图片预览
const openImagePreview = (images) => {
  previewImages.value = images
  currentIndex.value = 0
  previewVisible.value = true
}

const getStatusType = (status) => {
  if (status === 0) return 'success'
  if (status === 1) return 'warning'
  return 'danger'
}
const getStatusText = (status) => {
  if (status === 0) return '正常'
  if (status === 1) return '暂停'
  return '下架'
}

const loadVenues = async () => {
  if (!isApproved.value) {
    venues.value = []
    total.value = 0
    return
  }
  loading.value = true
  try {
    const res = await getMyVenues()
    const all = res || []
    total.value = all.length
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    venues.value = all.slice(start, end)
  } catch (error) {
    ElMessage.error('加载场地失败')
  } finally {
    loading.value = false
  }
}

const openCreateDialog = () => {
  isEdit.value = false
  currentVid.value = null
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = (venue) => {
  isEdit.value = true
  currentVid.value = venue.vid
  venueForm.code = venue.code
  venueForm.pricePerHour = venue.pricePerHour
  venueForm.description = venue.description || ''
  venueForm.photos = venue.photos || ''
  // 构建 fileList
  fileList.value = []
  if (venueForm.photos) {
    const urls = venueForm.photos.split(',')
    urls.forEach((url, idx) => {
      fileList.value.push({
        name: `photo_${idx}`,
        url: url,
        status: 'success'
      })
    })
  }
  dialogVisible.value = true
}

const resetForm = () => {
  venueForm.code = ''
  venueForm.pricePerHour = null
  venueForm.description = ''
  venueForm.photos = ''
  fileList.value = []
  venueFormRef.value?.clearValidate()
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB')
    return false
  }
  return true
}

const handleUploadSuccess = (response, file, fileList) => {
  if (response.code === 200 && response.data) {
    const imageUrl = response.data
    // 更新 venueForm.photos
    let currentUrls = venueForm.photos ? venueForm.photos.split(',') : []
    currentUrls.push(imageUrl)
    venueForm.photos = currentUrls.join(',')
    // 更新 fileList 中的 url
    const uploadedFile = fileList.find(f => f.uid === file.uid)
    if (uploadedFile && !uploadedFile.url) {
      uploadedFile.url = imageUrl
      uploadedFile.status = 'success'
    }
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败：' + (response.message || '未知错误'))
  }
}

const handleUploadError = (error) => {
  console.error('上传失败', error)
  ElMessage.error('图片上传失败，请重试')
}

const handleRemove = (file, fileList) => {
  if (file.url) {
    let currentUrls = venueForm.photos ? venueForm.photos.split(',') : []
    currentUrls = currentUrls.filter(url => url !== file.url)
    venueForm.photos = currentUrls.join(',')
  }
}

const saveVenue = async () => {
  await venueFormRef.value.validate()
  submitting.value = true
  try {
    const submitData = {
      code: venueForm.code,
      pricePerHour: venueForm.pricePerHour,
      description: venueForm.description,
      photos: venueForm.photos
    }
    if (isEdit.value) {
      await updateVenue(currentVid.value, submitData)
      ElMessage.success('更新成功')
    } else {
      await addVenue(submitData)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    await loadVenues()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (vid) => {
  await ElMessageBox.confirm('确认删除该场地？', '提示', { type: 'warning' })
  try {
    await deleteVenueApi(vid)
    ElMessage.success('删除成功')
    await loadVenues()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadVenues()
})
</script>

<style scoped>
.merchant-venues {
  min-height: 100%;
  background-color: #f5f7fa;
  padding: 20px;
}
.page-title {
  font-size: 24px;
  font-weight: 500;
  margin-bottom: 24px;
  color: #303133;
  position: relative;
  padding-left: 12px;
}
.page-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  background: #409eff;
  border-radius: 2px;
}
.alert {
  margin-bottom: 20px;
}
.venue-actions {
  margin-bottom: 20px;
  text-align: right;
}
.venue-actions .el-button {
  border-radius: 20px;
  padding: 8px 20px;
  background: linear-gradient(135deg, #409eff 0%, #36abff 100%);
  border: none;
  color: white;
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}
.venue-actions .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.venue-table {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
.venue-table :deep(.el-table__header) {
  background-color: #f8f9fa;
}
.venue-table :deep(.el-table__header th) {
  font-weight: 600;
  font-size: 14px;
  color: #2c3e50;
  padding: 14px 0;
  border-bottom: 2px solid #e9ecef;
}
.venue-table :deep(.el-table__body td) {
  padding: 12px 0;
  font-size: 14px;
  color: #5a6e8a;
}
.venue-table :deep(.el-table__row) {
  transition: background-color 0.2s;
}
.venue-table :deep(.el-table__row:hover) {
  background-color: #f8f9fa !important;
}
.price-text {
  font-weight: 500;
  color: #f56c6c;
  font-size: 15px;
}

/* 图片预览 */
.photo-preview {
  display: inline-block;
}
.thumbnail-wrapper {
  position: relative;
  display: inline-block;
  cursor: pointer;
}
.thumbnail {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  object-fit: cover;
  transition: transform 0.2s, box-shadow 0.2s;
  border: 1px solid #e9ecef;
}
.thumbnail:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}
.photo-count {
  position: absolute;
  bottom: -6px;
  right: -6px;
  background: #409eff;
  color: white;
  font-size: 10px;
  padding: 0 4px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
  line-height: 16px;
}
.no-photo {
  color: #c0c4cc;
  font-size: 12px;
}

/* 分页 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.pagination :deep(.el-pagination) {
  background: #fff;
  padding: 8px 16px;
  border-radius: 30px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

/* 自定义图片预览对话框 */
.image-preview-dialog :deep(.el-dialog__body) {
  padding: 0;
  background: #000;
}
.preview-container {
  position: relative;
  text-align: center;
}
.preview-image {
  max-width: 100%;
  max-height: 500px;
  object-fit: contain;
}
.image-count {
  position: absolute;
  bottom: 10px;
  right: 20px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
}
.image-preview-dialog :deep(.el-carousel) {
  height: 500px;
}
.image-preview-dialog :deep(.el-carousel__container) {
  height: 100%;
}
.image-preview-dialog :deep(.el-carousel__arrow) {
  background: rgba(0, 0, 0, 0.5);
}
.image-preview-dialog :deep(.el-carousel__arrow:hover) {
  background: rgba(0, 0, 0, 0.7);
}

/* 对话框样式优化 */
.venue-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}
.venue-dialog :deep(.el-dialog__header) {
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  padding: 18px 24px;
  margin: 0;
}
.venue-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}
.venue-dialog :deep(.el-dialog__body) {
  padding: 24px;
}
.venue-dialog :deep(.el-form-item__label) {
  font-weight: 500;
}
.venue-dialog :deep(.el-input__inner),
.venue-dialog :deep(.el-textarea__inner) {
  border-radius: 8px;
}
.venue-dialog :deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
  line-height: 100px;
  background: #f8f9fa;
  border: 1px dashed #dcdfe6;
  transition: all 0.2s;
}
.venue-dialog :deep(.el-upload--picture-card:hover) {
  border-color: #409eff;
  background: #ecf5ff;
}
.venue-dialog :deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
}
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
.dialog-footer {
  text-align: right;
  padding-top: 10px;
}
</style>