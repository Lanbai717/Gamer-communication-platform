<template>
  <div class="merchant-profile">
    <h2 class="page-title">商家资料</h2>
    
    <!-- 审核状态提示栏（纯展示，无按钮） -->
    <el-alert
      v-if="showAuditAlert"
      :title="auditAlertTitle"
      :type="auditAlertType"
      :description="auditAlertDesc"
      show-icon
      :closable="false"
      style="margin-bottom: 20px;"
    />

    <el-card class="profile-card" v-loading="loading" shadow="never">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
          <el-button type="primary" plain size="small" @click="editProfile">编辑资料</el-button>
        </div>
      </template>

      <div class="info-grid">
        <div class="info-row">
          <div class="info-label">登录名</div>
          <div class="info-value">{{ merchantInfo.username }}</div>
        </div>
        <div class="info-row">
          <div class="info-label">球馆名称</div>
          <div class="info-value">{{ merchantInfo.companyName || '未设置' }}</div>
        </div>
        <div class="info-row">
          <div class="info-label">联系人</div>
          <div class="info-value">{{ merchantInfo.contactPerson || '未设置' }}</div>
        </div>
        <div class="info-row">
          <div class="info-label">联系电话</div>
          <div class="info-value">{{ merchantInfo.contactPhone || '未设置' }}</div>
        </div>
        <div class="info-row">
          <div class="info-label">邮箱</div>
          <div class="info-value">{{ merchantInfo.email || '未设置' }}</div>
        </div>
        <div class="info-row">
          <div class="info-label">营业时间</div>
          <div class="info-value">
            <div v-if="formattedBusinessHours" class="business-hours-display">
              <div v-for="(item, index) in merchantInfo.businessHoursList" :key="index" class="hours-tag">
                <el-tag type="info" size="small">
                  {{ formatDayRange(item.startDay, item.endDay) }} {{ item.startTime }} - {{ item.endTime }}
                </el-tag>
              </div>
            </div>
            <span v-else class="placeholder">未设置</span>
          </div>
        </div>
        <div class="info-row">
          <div class="info-label">地址</div>
          <div class="info-value">{{ merchantInfo.address || '未设置' }}</div>
        </div>
        <div class="info-row">
          <div class="info-label">营业执照</div>
          <div class="info-value">
            <div class="license-wrapper" @click="openPreview">
              <img
                v-if="merchantInfo.businessLicense"
                :src="merchantInfo.businessLicense"
                class="license-img"
                alt="营业执照"
              />
              <span v-else class="placeholder">未上传</span>
            </div>
          </div>
        </div>
        <div class="info-row">
          <div class="info-label">审核状态</div>
          <div class="info-value">
            <el-tag :type="getAuditStatusType(merchantInfo.auditStatus)" size="small">
              {{ getAuditStatusText(merchantInfo.auditStatus) }}
            </el-tag>
            <span v-if="merchantInfo.auditRemark" class="audit-remark">
              （{{ merchantInfo.auditRemark }}）
            </span>
          </div>
        </div>
        <div class="info-row">
          <div class="info-label">账号状态</div>
          <div class="info-value">
            <el-tag :type="getStatusType(merchantInfo.status)" size="small">{{ getStatusText(merchantInfo.status) }}</el-tag>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 场馆详情卡片 -->
    <el-card class="profile-card" v-loading="loading" shadow="never" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>场馆详情</span>
          <el-button type="primary" plain size="small" @click="editVenueDetail">编辑详情</el-button>
        </div>
      </template>
      <div class="info-grid">
        <div class="info-row">
          <div class="info-label">设施</div>
          <div class="info-value">
            <div v-if="facilityTags.length" class="facility-tags">
              <el-tag v-for="(fac, idx) in facilityTags" :key="idx" size="small" style="margin-right: 8px; margin-bottom: 4px">
                {{ fac }}
              </el-tag>
            </div>
            <span v-else class="placeholder">未设置</span>
          </div>
        </div>
        <div class="info-row">
          <div class="info-label">详细介绍</div>
          <div class="info-value">{{ merchantInfo.detailIntro || '未设置' }}</div>
        </div>
        <div class="info-row">
          <div class="info-label">场馆图片</div>
          <div class="info-value">
            <div class="photo-preview" v-if="merchantInfo.venuePhotos">
              <div
                v-for="(url, idx) in merchantInfo.venuePhotos.split(',')"
                :key="idx"
                class="thumbnail-wrapper"
                @click="openVenuePhotoPreview(merchantInfo.venuePhotos.split(','), idx)"
              >
                <img :src="url" class="thumbnail" />
              </div>
            </div>
            <span v-else class="placeholder">未设置</span>
          </div>
        </div>
        <div class="info-row">
          <!-- <div class="info-label">视频链接</div>
          <div class="info-value">
            <div v-if="merchantInfo.videos" class="video-list">
              <a v-for="(url, idx) in merchantInfo.videos.split(',')" :key="idx" :href="url" target="_blank" class="video-link">{{ url }}</a>
            </div>
            <span v-else class="placeholder">未设置</span>
          </div> -->
        </div>
        <div class="info-row">
          <div class="info-label">使用规则</div>
          <div class="info-value">{{ merchantInfo.rules || '未设置' }}</div>
        </div>
      </div>
    </el-card>

    <!-- 图片预览对话框（营业执照） -->
    <el-dialog
      v-model="previewVisible"
      title="营业执照预览"
      width="auto"
      :before-close="closePreview"
      class="preview-dialog"
      destroy-on-close
    >
      <div class="preview-image-wrapper">
        <img :src="previewUrl" class="preview-image" @load="onImageLoad" v-loading="previewLoading" />
      </div>
    </el-dialog>

    <!-- 场馆图片预览对话框 -->
    <el-dialog
      v-model="venuePhotoPreviewVisible"
      title="场馆图片预览"
      width="auto"
      class="preview-dialog"
      destroy-on-close
    >
      <div class="preview-image-wrapper">
        <img :src="venuePhotoPreviewUrl" class="preview-image" v-loading="venuePhotoPreviewLoading" />
      </div>
    </el-dialog>

    <!-- 编辑商家资料对话框（仅基本信息） -->
    <el-dialog
      v-model="profileDialogVisible"
      title="编辑商家资料"
      width="600px"
      class="edit-dialog"
    >
      <el-form :model="profileEditForm" :rules="profileRules" ref="profileEditForm" label-width="100px">
        <el-form-item label="公司名称" prop="companyName">
          <el-input v-model="profileEditForm.companyName" placeholder="请输入公司名称" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="profileEditForm.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="profileEditForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="profileEditForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="营业时间" prop="businessHoursList">
          <div class="business-hours-editor">
            <div 
              v-for="(item, index) in profileEditForm.businessHoursList" 
              :key="index" 
              class="hours-item"
            >
              <div class="day-range-row">
                <el-select v-model="item.startDay" placeholder="开始星期" style="width: 100px">
                  <el-option 
                    v-for="day in weekDays" 
                    :key="day.value" 
                    :label="day.label" 
                    :value="day.value" 
                  />
                </el-select>
                <span class="range-separator">到</span>
                <el-select v-model="item.endDay" placeholder="结束星期" style="width: 100px">
                  <el-option 
                    v-for="day in weekDays" 
                    :key="day.value" 
                    :label="day.label" 
                    :value="day.value" 
                  />
                </el-select>
              </div>
              <div class="time-range-row">
                <el-time-picker
                  v-model="item.startTime"
                  placeholder="开始时间"
                  format="HH:mm"
                  value-format="HH:mm"
                  style="width: 110px"
                />
                <span class="range-separator">至</span>
                <el-time-picker
                  v-model="item.endTime"
                  placeholder="结束时间"
                  format="HH:mm"
                  value-format="HH:mm"
                  style="width: 110px"
                />
                <el-button 
                  type="danger" 
                  circle 
                  size="small" 
                  @click="removeBusinessHour(index)"
                  :disabled="profileEditForm.businessHoursList.length === 1"
                  class="delete-btn"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
            <el-button 
              type="primary" 
              plain 
              size="small" 
              @click="addBusinessHour"
              class="add-btn"
            >
              <el-icon><Plus /></el-icon> 添加营业时段
            </el-button>
          </div>
          <div class="form-tip">可设置多个营业时段，如：周一到周五 09:00-22:00，周六到周日 10:00-23:00</div>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="profileEditForm.address" type="textarea" rows="3" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="营业执照" prop="businessLicense">
          <el-upload
            class="upload-demo"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :on-remove="handleUploadRemove"
            :file-list="fileList"
            :auto-upload="true"
            :limit="1"
            :before-upload="beforeUpload"
            name="file"
            list-type="picture-card"
          >
            <el-button type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">
                请上传营业执照图片（仅支持jpg/png，不超过10MB）
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="profileDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveProfile" :loading="profileSaveLoading">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑场馆详情对话框（独立） -->
    <el-dialog
      v-model="venueDialogVisible"
      title="编辑场馆详情"
      width="600px"
      class="edit-dialog"
    >
      <el-form :model="venueEditForm" ref="venueEditForm" label-width="100px">
        <el-form-item label="设施">
          <el-input v-model="venueEditForm.facilitiesText" placeholder="多个设施用逗号分隔，如：空调,更衣室,淋浴" />
        </el-form-item>
        <el-form-item label="详细介绍">
          <el-input v-model="venueEditForm.detailIntro" type="textarea" rows="4" placeholder="场馆特色、环境、配套等" />
        </el-form-item>
        <el-form-item label="场馆图片">
          <el-upload
            class="photo-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="handleVenuePhotoSuccess"
            :on-error="handleUploadError"
            :on-remove="handleVenuePhotoRemove"
            :file-list="venuePhotoFileList"
            :before-upload="beforeUpload"
            list-type="picture-card"
            multiple
            :limit="12"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="form-tip">支持jpg/png，每张不超过10MB，最多12张，第一张作为封面</div>
        </el-form-item>
        <!-- <el-form-item label="视频链接">
          <el-input v-model="venueEditForm.videos" placeholder="多个链接用逗号分隔，如：https://xxx.com/v1,https://xxx.com/v2" />
        </el-form-item> -->
        <el-form-item label="使用规则">
          <el-input v-model="venueEditForm.rules" type="textarea" rows="3" placeholder="场地使用须知、退订规则等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="venueDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveVenueDetail" :loading="venueSaveLoading">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getMerchantInfo, updateMerchantInfo } from '@/api/merchant'  // 移除 submitAudit
import { Delete, Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const UPLOAD_URL = 'http://localhost:8080/upload/image'

const WEEK_DAYS = [
  { value: 1, label: '周一' },
  { value: 2, label: '周二' },
  { value: 3, label: '周三' },
  { value: 4, label: '周四' },
  { value: 5, label: '周五' },
  { value: 6, label: '周六' },
  { value: 7, label: '周日' }
]

const AUDIT_STATUS = {
  UNFILLED: 0,
  PENDING: 1,
  APPROVED: 2,
  REJECTED: 3
}

export default {
  name: 'MerchantProfile',
  components: { Delete, Plus },
  data() {
    return {
      loading: false,
      profileDialogVisible: false,
      profileSaveLoading: false,
      venueDialogVisible: false,
      venueSaveLoading: false,
      previewVisible: false,
      previewUrl: '',
      previewLoading: false,
      venuePhotoPreviewVisible: false,
      venuePhotoPreviewUrl: '',
      venuePhotoPreviewLoading: false,
      weekDays: WEEK_DAYS,
      uploadUrl: UPLOAD_URL,
      merchantInfo: {
        username: '',
        companyName: '',
        contactPerson: '',
        contactPhone: '',
        email: '',
        businessHours: '',
        businessHoursList: [],
        address: '',
        businessLicense: '',
        status: 1,
        auditStatus: AUDIT_STATUS.UNFILLED,
        auditRemark: '',
        isSubmitted: 0,
        facilities: '',
        detailIntro: '',
        venuePhotos: '',
        videos: '',
        rules: '',
        pendingData: null
      },
      profileEditForm: {
        companyName: '',
        contactPerson: '',
        contactPhone: '',
        email: '',
        businessHoursList: [],
        address: '',
        businessLicense: ''
      },
      venueEditForm: {
        facilitiesText: '',
        detailIntro: '',
        venuePhotos: '',
        videos: '',
        rules: ''
      },
      fileList: [],
      venuePhotoFileList: [],
      profileRules: {
        companyName: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
        contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
        contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        businessHoursList: [{ validator: this.validateBusinessHours, trigger: 'change' }],
        address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
        businessLicense: [{ required: true, message: '请上传营业执照', trigger: 'change' }]
      }
    }
  },
  computed: {
    uploadHeaders() {
      const userStore = useUserStore()
      return {
        Authorization: `Bearer ${userStore.token}`
      }
    },
    formattedBusinessHours() {
      if (!this.merchantInfo.businessHoursList || this.merchantInfo.businessHoursList.length === 0) return ''
      return this.merchantInfo.businessHoursList.map(item => {
        return `${this.formatDayRange(item.startDay, item.endDay)} ${item.startTime}-${item.endTime}`
      }).join('；')
    },
    facilityTags() {
      if (!this.merchantInfo.facilities) return []
      return this.merchantInfo.facilities.split(',').map(f => f.trim()).filter(f => f)
    },
    showAuditAlert() {
      return this.merchantInfo.auditStatus !== AUDIT_STATUS.APPROVED
    },
    auditAlertTitle() {
      if (this.isBasicInfoComplete && this.merchantInfo.auditStatus === AUDIT_STATUS.UNFILLED) {
        return '资料已完善'
      }
      if (!this.isBasicInfoComplete && this.merchantInfo.auditStatus === AUDIT_STATUS.UNFILLED) {
        return '资料未完善'
      }
      switch (this.merchantInfo.auditStatus) {
        case AUDIT_STATUS.PENDING: return '审核中'
        case AUDIT_STATUS.REJECTED: return '审核未通过'
        default: return '资料状态'
      }
    },
    auditAlertType() {
      if (this.isBasicInfoComplete && this.merchantInfo.auditStatus === AUDIT_STATUS.UNFILLED) return 'success'
      if (!this.isBasicInfoComplete && this.merchantInfo.auditStatus === AUDIT_STATUS.UNFILLED) return 'warning'
      switch (this.merchantInfo.auditStatus) {
        case AUDIT_STATUS.PENDING: return 'info'
        case AUDIT_STATUS.REJECTED: return 'error'
        default: return 'info'
      }
    },
    auditAlertDesc() {
      if (this.isBasicInfoComplete && this.merchantInfo.auditStatus === AUDIT_STATUS.UNFILLED) {
        return '您的资料已填写完整，保存后将自动提交审核。审核通过后才能上架场地。'
      }
      if (!this.isBasicInfoComplete && this.merchantInfo.auditStatus === AUDIT_STATUS.UNFILLED) {
        const missingFields = this.getMissingFields()
        return `请完善以下信息后再提交：${missingFields.join('、')}。审核通过后才能上架场地。`
      }
      switch (this.merchantInfo.auditStatus) {
        case AUDIT_STATUS.PENDING: return '您的资料已提交审核，请耐心等待管理员审核，审核期间不能上架新场地。'
        case AUDIT_STATUS.REJECTED: return `您的资料审核未通过${this.merchantInfo.auditRemark ? '：' + this.merchantInfo.auditRemark : ''}，请修改后重新提交。`
        default: return ''
      }
    },
    isBasicInfoComplete() {
      const info = this.merchantInfo
      return !!(info.companyName && info.companyName.trim() !== '' &&
        info.contactPerson && info.contactPerson.trim() !== '' &&
        info.contactPhone && info.contactPhone.trim() !== '' &&
        info.email && info.email.trim() !== '' &&
        info.address && info.address.trim() !== '' &&
        info.businessLicense && info.businessLicense.trim() !== '' &&
        info.businessHoursList && info.businessHoursList.length > 0 &&
        info.businessHoursList.some(h => h.startDay && h.endDay && h.startTime && h.endTime))
    }
  },
  mounted() {
    this.loadMerchantInfo()
  },
  methods: {
    async loadMerchantInfo() {
      this.loading = true
      try {
        const res = await getMerchantInfo()
        let hoursList = []
        if (res.businessHours) {
          try {
            const parsed = JSON.parse(res.businessHours)
            if (Array.isArray(parsed)) hoursList = parsed
          } catch (e) {
            hoursList = this.parseBusinessHoursString(res.businessHours)
          }
        }
        let auditStatus = AUDIT_STATUS.UNFILLED
        if (res.status === 1) auditStatus = AUDIT_STATUS.APPROVED
        else if (res.status === 0 && res.isSubmitted === 1) auditStatus = AUDIT_STATUS.PENDING
        else if (res.status === 2) auditStatus = AUDIT_STATUS.REJECTED
        this.merchantInfo = {
          username: res.username || '',
          companyName: res.companyName || '',
          contactPerson: res.contactPerson || '',
          contactPhone: res.contactPhone || '',
          email: res.email || '',
          address: res.address || '',
          businessHours: res.businessHours || '',
          businessHoursList: hoursList,
          businessLicense: res.businessLicense || '',
          status: res.status !== undefined ? res.status : 1,
          auditStatus: auditStatus,
          auditRemark: res.auditRemark || '',
          isSubmitted: res.isSubmitted || 0,
          facilities: res.facilities || '',
          detailIntro: res.detailIntro || '',
          venuePhotos: res.venuePhotos || '',
          videos: res.videos || '',
          rules: res.rules || '',
          pendingData: res.pendingData || null
        }
      } catch (error) {
        this.$message.error('加载商家信息失败')
      } finally {
        this.loading = false
      }
    },
    getMissingFields() {
      const info = this.merchantInfo
      const missing = []
      if (!info.companyName || info.companyName.trim() === '') missing.push('公司名称')
      if (!info.contactPerson || info.contactPerson.trim() === '') missing.push('联系人')
      if (!info.contactPhone || info.contactPhone.trim() === '') missing.push('联系电话')
      if (!info.email || info.email.trim() === '') missing.push('邮箱')
      if (!info.address || info.address.trim() === '') missing.push('地址')
      if (!info.businessLicense || info.businessLicense.trim() === '') missing.push('营业执照')
      if (!info.businessHoursList || info.businessHoursList.length === 0 ||
          !info.businessHoursList.some(h => h.startDay && h.endDay && h.startTime && h.endTime)) {
        missing.push('营业时间')
      }
      return missing
    },
    editProfile() {
      this.profileEditForm = {
        companyName: this.merchantInfo.companyName || '',
        contactPerson: this.merchantInfo.contactPerson || '',
        contactPhone: this.merchantInfo.contactPhone || '',
        email: this.merchantInfo.email || '',
        businessHoursList: JSON.parse(JSON.stringify(this.merchantInfo.businessHoursList || [])),
        address: this.merchantInfo.address || '',
        businessLicense: this.merchantInfo.businessLicense || ''
      }
      if (!this.profileEditForm.businessHoursList || this.profileEditForm.businessHoursList.length === 0) {
        this.profileEditForm.businessHoursList = [{ startDay: '', endDay: '', startTime: '', endTime: '' }]
      }
      this.fileList = []
      if (this.profileEditForm.businessLicense) {
        this.fileList.push({ name: '营业执照', url: this.profileEditForm.businessLicense })
      }
      this.profileDialogVisible = true
    },
    editVenueDetail() {
      this.venueEditForm = {
        facilitiesText: this.merchantInfo.facilities || '',
        detailIntro: this.merchantInfo.detailIntro || '',
        venuePhotos: this.merchantInfo.venuePhotos || '',
        videos: this.merchantInfo.videos || '',
        rules: this.merchantInfo.rules || ''
      }
      this.venuePhotoFileList = []
      if (this.venueEditForm.venuePhotos) {
        const urls = this.venueEditForm.venuePhotos.split(',')
        urls.forEach((url, idx) => {
          this.venuePhotoFileList.push({ name: `photo_${idx}`, url: url, status: 'success' })
        })
      }
      this.venueDialogVisible = true
    },
    addBusinessHour() {
      this.profileEditForm.businessHoursList.push({ startDay: '', endDay: '', startTime: '', endTime: '' })
    },
    removeBusinessHour(index) {
      this.profileEditForm.businessHoursList.splice(index, 1)
    },
    validateBusinessHours(rule, value, callback) {
      if (!value || value.length === 0) {
        callback(new Error('请至少设置一个营业时段'))
        return
      }
      for (let item of value) {
        if (!item.startDay || !item.endDay || !item.startTime || !item.endTime) {
          callback(new Error('请完善所有营业时间信息'))
          return
        }
        if (item.startDay > item.endDay) callback(new Error('结束星期不能早于开始星期'))
        if (item.startTime >= item.endTime) callback(new Error('结束时间必须晚于开始时间'))
      }
      callback()
    },
    formatDay(dayValue) {
      const day = this.weekDays.find(d => d.value === dayValue)
      return day ? day.label : ''
    },
    formatDayRange(startDay, endDay) {
      const start = this.formatDay(startDay)
      const end = this.formatDay(endDay)
      if (start === end) return start
      return `${start}到${end}`
    },
    parseBusinessHoursString(str) {
      const list = []
      const segments = str.split('；')
      segments.forEach(seg => {
        const match = seg.match(/(周[一二三四五六日])(?:到(周[一二三四五六日]))?\s*(\d{2}:\d{2})-(\d{2}:\d{2})/)
        if (match) {
          const dayMap = { '周一': 1, '周二': 2, '周三': 3, '周四': 4, '周五': 5, '周六': 6, '周日': 7 }
          list.push({
            startDay: dayMap[match[1]],
            endDay: dayMap[match[2]] || dayMap[match[1]],
            startTime: match[3],
            endTime: match[4]
          })
        }
      })
      return list
    },
    handleUploadSuccess(response, file) {
      if (response.code === 200 && response.data) {
        this.profileEditForm.businessLicense = response.data
        this.fileList = [{ name: file.name, url: response.data }]
        this.$message.success('营业执照上传成功')
      } else {
        this.$message.error('上传失败：' + (response.message || '未知错误'))
      }
    },
    handleUploadError() {
      this.$message.error('营业执照上传失败，请重试')
    },
    handleUploadRemove(file) {
      this.profileEditForm.businessLicense = ''
      this.fileList = this.fileList.filter(f => f.url !== file.url)
    },
    beforeUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt10M = file.size / 1024 / 1024 < 10
      if (!isImage) this.$message.error('只能上传图片文件')
      if (!isLt10M) this.$message.error('图片大小不能超过10MB')
      return isImage && isLt10M
    },
    handleVenuePhotoSuccess(response, file, fileList) {
      if (response.code === 200 && response.data) {
        const imageUrl = response.data
        let currentUrls = this.venueEditForm.venuePhotos ? this.venueEditForm.venuePhotos.split(',') : []
        currentUrls.push(imageUrl)
        this.venueEditForm.venuePhotos = currentUrls.join(',')
        const uploadedFile = fileList.find(f => f.uid === file.uid)
        if (uploadedFile && !uploadedFile.url) uploadedFile.url = imageUrl
        this.$message.success('图片上传成功')
      } else {
        this.$message.error('上传失败')
      }
    },
    handleVenuePhotoRemove(file) {
      if (file.url) {
        let currentUrls = this.venueEditForm.venuePhotos ? this.venueEditForm.venuePhotos.split(',') : []
        currentUrls = currentUrls.filter(url => url !== file.url)
        this.venueEditForm.venuePhotos = currentUrls.join(',')
      }
    },
    openPreview() {
      if (!this.merchantInfo.businessLicense) return
      this.previewUrl = this.merchantInfo.businessLicense
      this.previewLoading = true
      this.previewVisible = true
    },
    closePreview() {
      this.previewVisible = false
    },
    openVenuePhotoPreview(images, index) {
      this.venuePhotoPreviewUrl = images[index]
      this.venuePhotoPreviewLoading = true
      this.venuePhotoPreviewVisible = true
    },
    onImageLoad() {
      this.previewLoading = false
    },
    async saveProfile() {
      this.$refs.profileEditForm.validate(async (valid) => {
        if (valid) {
          this.profileSaveLoading = true
          try {
            const submitData = {
              ...this.profileEditForm,
              businessHours: JSON.stringify(this.profileEditForm.businessHoursList),
              facilities: this.merchantInfo.facilities,
              detailIntro: this.merchantInfo.detailIntro,
              venuePhotos: this.merchantInfo.venuePhotos,
              videos: this.merchantInfo.videos,
              rules: this.merchantInfo.rules
            }
            const res = await updateMerchantInfo(submitData)
            // 即使后端返回空响应或code不是200，只要请求成功（没有抛出异常），都视为保存成功
            // 因为后端可能返回的是 null（pending 分支成功但无 data），但实际状态已更新
            await this.loadMerchantInfo()
            this.profileDialogVisible = false
            this.$message.success('已提交审核，请稍后')
          } catch (error) {
            console.error('保存商家资料失败：', error)
            this.$message.error('更新失败，请重试')
          } finally {
            this.profileSaveLoading = false
          }
        } else {
          this.$message.error('请检查表单')
        }
      })
    },
    async saveVenueDetail() {
      this.venueSaveLoading = true
      try {
        const submitData = {
          companyName: this.merchantInfo.companyName,
          contactPerson: this.merchantInfo.contactPerson,
          contactPhone: this.merchantInfo.contactPhone,
          email: this.merchantInfo.email,
          businessHours: this.merchantInfo.businessHours,
          address: this.merchantInfo.address,
          businessLicense: this.merchantInfo.businessLicense,
          facilities: this.venueEditForm.facilitiesText,
          detailIntro: this.venueEditForm.detailIntro,
          venuePhotos: this.venueEditForm.venuePhotos,
          videos: this.venueEditForm.videos,
          rules: this.venueEditForm.rules
        }
        await updateMerchantInfo(submitData)
        await this.loadMerchantInfo()
        this.venueDialogVisible = false
        this.$message.success('场馆详情更新成功')
      } catch (error) {
        console.error('保存场馆详情失败：', error)
        this.$message.error('更新失败，请重试')
      } finally {
        this.venueSaveLoading = false
      }
    },
    getAuditStatusType(status) {
      const map = { 0: 'warning', 1: 'info', 2: 'success', 3: 'danger' }
      return map[status] || 'default'
    },
    getAuditStatusText(status) {
      const map = { 0: '未提交', 1: '审核中', 2: '已通过', 3: '未通过' }
      return map[status] || '未知'
    },
    getStatusType(status) {
      const map = { 0: 'warning', 1: 'success', 2: 'danger' }
      return map[status] || 'default'
    },
    getStatusText(status) {
      const map = { 0: '待审核', 1: '正常', 2: '禁用' }
      return map[status] || '未知'
    }
  }
}
</script>

<style scoped>
/* 页面基础样式保持不变，新增部分样式与原有风格统一 */
.merchant-profile {
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
.profile-card {
  border-radius: 12px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03), 0 1px 6px -1px rgba(0, 0, 0, 0.02), 0 2px 4px 0 rgba(0, 0, 0, 0.02);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  color: #1f2f3d;
}
.info-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.info-row {
  display: flex;
  border-bottom: 1px solid #eef2f6;
  padding: 8px 0;
}
.info-row:last-child {
  border-bottom: none;
}
.info-label {
  width: 100px;
  flex-shrink: 0;
  font-size: 14px;
  color: #5e6e82;
  font-weight: 400;
}
/* 关键修复：flex-1 改为 flex: 1; */
.info-value {
  flex: 1;
  font-size: 14px;
  color: #2c3e50;
  word-break: break-word;
}
.license-wrapper {
  display: inline-block;
  cursor: pointer;
}
.license-img {
  width: 200px;
  height: 150px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  object-fit: cover;
  transition: transform 0.2s, box-shadow 0.2s;
}
.license-img:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.placeholder {
  color: #909399;
}
/* 设施标签 */
.facility-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
/* 场馆图片预览 */
.photo-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.thumbnail-wrapper {
  cursor: pointer;
}
.thumbnail {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  border: 1px solid #e9ecef;
  transition: transform 0.2s;
}
.thumbnail:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}
/* 视频链接 */
.video-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.video-link {
  color: #409eff;
  text-decoration: none;
  word-break: break-all;
}
.video-link:hover {
  text-decoration: underline;
}
/* 审核备注 */
.audit-remark {
  color: #f56c6c;
  font-size: 12px;
  margin-left: 8px;
}
/* 预览对话框样式（复用） */
.preview-dialog :deep(.el-dialog) {
  width: auto;
  max-width: 90vw;
  max-height: 90vh;
}
.preview-dialog :deep(.el-dialog__body) {
  padding: 0;
  text-align: center;
  background: rgba(0, 0, 0, 0.8);
}
.preview-image-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}
.preview-image {
  max-width: 90vw;
  max-height: 85vh;
  object-fit: contain;
}
/* 响应式 */
@media (max-width: 768px) {
  .info-label {
    width: 80px;
    font-size: 12px;
  }
  .info-value {
    font-size: 12px;
  }
  .license-img {
    width: 150px;
    height: 112px;
  }
  .thumbnail {
    width: 60px;
    height: 60px;
  }
}
/* 编辑对话框样式（原有，未改动） */
.edit-dialog :deep(.el-dialog__body) {
  padding: 20px 20px 10px 20px;
}
.edit-dialog :deep(.el-form-item) {
  margin-bottom: 16px;
}
.edit-dialog :deep(.el-input__inner),
.edit-dialog :deep(.el-textarea__inner) {
  border-radius: 6px;
}
.edit-dialog :deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
  line-height: 100px;
}
.edit-dialog :deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}
.dialog-footer {
  text-align: right;
  padding-top: 10px;
}
.business-hours-display {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.hours-tag {
  display: inline-block;
}
.business-hours-editor {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.hours-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: #f5f7fa;
  padding: 12px;
  border-radius: 8px;
}
.day-range-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.time-range-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.range-separator {
  color: #909399;
  font-size: 14px;
  white-space: nowrap;
  padding: 0 4px;
}
.delete-btn {
  margin-left: auto;
}
.add-btn {
  align-self: flex-start;
}
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  line-height: 1.5;
}
</style>