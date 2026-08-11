<template>
  <div class="user-activities">
    <section class="activities-hero">
      <div>
        <span class="eyebrow">MATCH BOARD</span>
        <h2 class="page-title">约球活动</h2>
        <p class="page-subtitle">发现合适的球局，或发起一场有节奏、有水平、有搭子的羽毛球活动。</p>
      </div>
      <div class="hero-metrics">
        <div>
          <strong>{{ total || activities.length }}</strong>
          <span>全部活动</span>
        </div>
        <div>
          <strong>{{ myCreatedActivities.length }}</strong>
          <span>我创建的</span>
        </div>
        <div>
          <strong>{{ myJoinedActivities.length }}</strong>
          <span>我参与的</span>
        </div>
      </div>
    </section>
    
    <el-tabs v-model="activeTab" class="custom-tabs">
      <!-- ===== 全部活动 ===== -->
      <el-tab-pane label="全部活动" name="all">
        <div class="activity-section">
          <div class="search-bar">
            <el-input v-model="searchKeyword" placeholder="搜索活动名称、地点或描述" class="search-input">
              <template #append>
                <el-button @click="searchActivities"><el-icon><Search /></el-icon></el-button>
              </template>
            </el-input>
            <el-button type="primary" round @click="createActivity">创建活动</el-button>
          </div>
          
          <div class="activity-grid">
          <el-card v-for="activity in activities" :key="activity.aid" class="activity-card" shadow="never">
            <template #header>
              <div class="card-header">
                <div>
                  <span class="card-kicker">OPEN PLAY</span>
                  <h3>{{ activity.title }}</h3>
                </div>
                <el-tag :type="getActivityStatusType(activity)">{{ getActivityStatusText(activity) }}</el-tag>
              </div>
            </template>
            <div class="card-body">
              <p><el-icon><Calendar /></el-icon><span>{{ formatDateTime(activity.startTime) }} - {{ formatDateTime(activity.endTime) }}</span></p>
              <p><el-icon><Location /></el-icon><span>{{ activity.location }}</span></p>
              <p><el-icon><User /></el-icon><span>{{ activity.currentPlayers }}/{{ activity.maxPlayers }} 人</span></p>
              <p class="description">{{ activity.description }}</p>
              <div class="card-actions">
                <el-button size="small" round @click="openDetailDialog(activity.aid)">查看详情</el-button>
                <el-button 
                  size="small" 
                  type="primary" 
                  round 
                  @click="joinActivity(activity.aid)" 
                  v-if="!isActivityEnded(activity) && !isActivityFull(activity)"
                >立即报名</el-button>
              </div>
            </div>
          </el-card>
          </div>
          <div v-if="activities.length === 0" class="empty-state">
            <strong>暂无活动</strong>
            <span>换个关键词试试，或创建第一场约球活动。</span>
          </div>
          
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
        </div>
      </el-tab-pane>
      
      <!-- ===== 我创建的 ===== -->
      <el-tab-pane label="我创建的" name="created">
        <div class="my-activities">
          <div class="activity-grid">
          <el-card v-for="activity in myCreatedActivities" :key="activity.aid" class="activity-card" shadow="never">
            <template #header>
              <div class="card-header">
                <div>
                  <span class="card-kicker">HOSTED BY ME</span>
                  <h3>{{ activity.title }}</h3>
                </div>
                <el-tag :type="getActivityStatusType(activity)">{{ getActivityStatusText(activity) }}</el-tag>
              </div>
            </template>
            <div class="card-body">
              <p><el-icon><Calendar /></el-icon><span>{{ formatDateTime(activity.startTime) }} - {{ formatDateTime(activity.endTime) }}</span></p>
              <p><el-icon><Location /></el-icon><span>{{ activity.location }}</span></p>
              <p><el-icon><User /></el-icon><span>{{ activity.currentPlayers }}/{{ activity.maxPlayers }} 人</span></p>
              <div class="card-actions">
                <el-button size="small" round @click="openDetailDialog(activity.aid)">查看详情</el-button>
                <el-button 
                  size="small" 
                  type="primary" 
                  round 
                  @click="editActivity(activity.aid)" 
                  v-if="!isActivityEnded(activity)"
                >编辑</el-button>
              </div>
            </div>
          </el-card>
          </div>
          <div v-if="myCreatedActivities.length === 0" class="empty-state">
            <strong>还没有创建活动</strong>
            <span>发起一场球局，让附近球友加入你。</span>
          </div>
        </div>
      </el-tab-pane>
      
      <!-- ===== 我参与的 ===== -->
      <el-tab-pane label="我参与的" name="joined">
        <div class="my-activities">
          <div class="activity-grid">
          <el-card v-for="activity in myJoinedActivities" :key="activity.aid" class="activity-card" shadow="never">
            <template #header>
              <div class="card-header">
                <div>
                  <span class="card-kicker">JOINED</span>
                  <h3>{{ activity.title }}</h3>
                </div>
                <el-tag :type="getActivityStatusType(activity)">{{ getActivityStatusText(activity) }}</el-tag>
              </div>
            </template>
            <div class="card-body">
              <p><el-icon><Calendar /></el-icon><span>{{ formatDateTime(activity.startTime) }} - {{ formatDateTime(activity.endTime) }}</span></p>
              <p><el-icon><Location /></el-icon><span>{{ activity.location }}</span></p>
              <p><el-icon><User /></el-icon><span>{{ activity.currentPlayers }}/{{ activity.maxPlayers }} 人</span></p>
              <div class="card-actions">
                <el-button size="small" round @click="openDetailDialog(activity.aid)">查看详情</el-button>
                <el-button 
                  size="small" 
                  type="danger" 
                  round 
                  @click="cancelJoinActivity(activity.aid)" 
                  v-if="!isActivityEnded(activity) && activity.status === 0"
                >取消报名</el-button>
              </div>
            </div>
          </el-card>
          </div>
          <div v-if="myJoinedActivities.length === 0" class="empty-state">
            <strong>还没有参与活动</strong>
            <span>去全部活动里寻找一场合适的比赛。</span>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- ===== 创建/编辑活动对话框 ===== -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingActivityId ? '编辑活动' : '创建活动'"
      width="600px"
      class="custom-dialog"
    >
      <el-form :model="activityForm" :rules="rules" ref="activityForm" label-width="100px">
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="activityForm.title"></el-input>
        </el-form-item>
        <el-form-item label="活动地点" prop="location">
          <el-input v-model="activityForm.location"></el-input>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker 
            v-model="activityForm.startTime" 
            type="datetime" 
            placeholder="选择开始时间" 
            format="YYYY-MM-DD HH:mm" 
            value-format="YYYY-MM-DD HH:mm:ss"
            :disabled-date="disabledPastDate"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker 
            v-model="activityForm.endTime" 
            type="datetime" 
            placeholder="选择结束时间" 
            format="YYYY-MM-DD HH:mm" 
            value-format="YYYY-MM-DD HH:mm:ss"
            :disabled-date="disabledPastDate"
          />
        </el-form-item>
        <el-form-item label="总人数" prop="maxPlayers">
          <el-input v-model.number="activityForm.maxPlayers" type="number"></el-input>
        </el-form-item>
        <el-form-item label="水平要求" prop="levelRequire">
          <el-select v-model="activityForm.levelRequire" placeholder="请选择">
            <el-option label="不限" value="不限"></el-option>
            <el-option label="国际级运动健将" value="国际级运动健将"></el-option>
            <el-option label="运动健将" value="运动健将"></el-option>
            <el-option label="一级运动员" value="一级运动员"></el-option>
            <el-option label="二级运动员" value="二级运动员"></el-option>
            <el-option label="三级运动员" value="三级运动员"></el-option>
            <el-option label="业余高级" value="业余高级"></el-option>
            <el-option label="业余中级" value="业余中级"></el-option>
            <el-option label="业余初级" value="业余初级"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input v-model="activityForm.description" type="textarea" rows="4"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveActivity" :loading="saveLoading">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- ===== 活动详情弹窗 ===== -->
    <el-dialog v-model="detailVisible" title="活动详情" width="580px" class="custom-dialog">
      <div v-if="detail" class="detail-container">
        <div class="detail-header">
          <h2>{{ detail.title }}</h2>
          <el-tag :type="getActivityStatusType(detail)">{{ getActivityStatusText(detail) }}</el-tag>
        </div>
        <div class="detail-info">
          <p><el-icon><Calendar /></el-icon> {{ detail.startTime }} 至 {{ detail.endTime }}</p>
          <p><el-icon><Location /></el-icon> {{ detail.location }}</p>
          <p><el-icon><User /></el-icon> {{ detail.currentPlayers }}/{{ detail.maxPlayers }} 人</p>
          <p v-if="detail.levelRequire"><el-icon><Medal /></el-icon> 水平要求：{{ detail.levelRequire }}</p>
          <p class="desc">{{ detail.description }}</p>
        </div>
        <div class="creator-info">
          <span>发起人：</span>
          <el-avatar :size="32" :src="detail.creatorAvatar" />
          <span>{{ detail.creatorNickname }}</span>
        </div>
        <div class="participants">
          <h4>已报名 ({{ detail.participants ? detail.participants.length : 0 }})</h4>
          <ul v-if="detail.participants && detail.participants.length">
            <li v-for="p in detail.participants" :key="p.uid" class="participant-item">
              <el-avatar :size="28" :src="p.avatar" />
              <span>{{ p.nickname }}</span>
            </li>
          </ul>
          <el-empty v-else description="暂无报名" />
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { Search, Calendar, Location, User, Medal } from '@element-plus/icons-vue'
import { activityAPI } from '../../services/api'

export default {
  name: 'UserActivities',
  components: { Search, Calendar, Location, User, Medal },
  data() {
    return {
      activeTab: 'all',
      searchKeyword: '',
      activities: [],
      myCreatedActivities: [],
      myJoinedActivities: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      editingActivityId: null,
      saveLoading: false,
      activityForm: {
        title: '',
        location: '',
        startTime: '',
        endTime: '',
        maxPlayers: 8,
        levelRequire: '不限',
        description: ''
      },
      rules: {
        title: [{ required: true, message: '请输入活动标题', trigger: 'blur' }],
        location: [{ required: true, message: '请输入活动地点', trigger: 'blur' }],
        startTime: [
          { required: true, message: '请选择开始时间', trigger: 'change' },
          {
            validator: (rule, value, callback) => {
              if (!value) return callback()
              if (new Date(value) < new Date()) {
                callback(new Error('开始时间不能早于当前时间'))
              } else {
                callback()
              }
            },
            trigger: 'change'
          }
        ],
        endTime: [
          { required: true, message: '请选择结束时间', trigger: 'change' },
          {
            validator: (rule, value, callback) => {
              if (!value) return callback()
              if (!this.activityForm.startTime) {
                callback(new Error('请先选择开始时间'))
              } else if (new Date(value) <= new Date(this.activityForm.startTime)) {
                callback(new Error('结束时间必须晚于开始时间'))
              } else {
                callback()
              }
            },
            trigger: 'change'
          }
        ],
        maxPlayers: [{ required: true, message: '请输入总人数', trigger: 'blur' }]
      },
      detailVisible: false,
      detail: null
    }
  },
  mounted() {
    this.loadActivities()
    this.loadMyActivities()
  },
  methods: {
    async loadActivities() {
      try {
        const response = await activityAPI.getActivityList({
          keyword: this.searchKeyword,
          page: this.currentPage,
          size: this.pageSize
        })
        const sortedData = (response.data || []).sort((a, b) => {
          return new Date(b.createTime) - new Date(a.createTime)
        })
        this.activities = sortedData
        this.total = response.total
      } catch (error) {
        this.$message.error('获取活动列表失败')
      }
    },
    async loadMyActivities() {
      try {
        const createdResponse = await activityAPI.getMyCreatedActivities()
        this.myCreatedActivities = (createdResponse.data || []).sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
        
        const joinedResponse = await activityAPI.getMyJoinedActivities()
        this.myJoinedActivities = (joinedResponse.data || []).sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      } catch (error) {
        this.$message.error('获取我的活动失败')
      }
    },
    searchActivities() {
      this.currentPage = 1
      this.loadActivities()
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.loadActivities()
    },
    handleCurrentChange(current) {
      this.currentPage = current
      this.loadActivities()
    },
    createActivity() {
      this.editingActivityId = null
      this.activityForm = {
        title: '',
        location: '',
        startTime: '',
        endTime: '',
        maxPlayers: 8,
        levelRequire: '不限',
        description: ''
      }
      this.dialogVisible = true
    },
    async editActivity(aid) {
      try {
        const res = await activityAPI.getActivityDetail(aid)
        const detail = res.data !== undefined ? res.data : res
        this.activityForm = {
          title: detail.title,
          location: detail.location,
          startTime: this.formatDateTimeForInput(detail.startTime),
          endTime: this.formatDateTimeForInput(detail.endTime),
          maxPlayers: detail.maxPlayers,
          levelRequire: detail.levelRequire || '不限',
          description: detail.description
        }
        this.editingActivityId = aid
        this.dialogVisible = true
      } catch (e) {
        this.$message.error('获取活动信息失败')
      }
    },
    formatDateTimeForInput(dateStr) {
      if (!dateStr) return ''
      const d = new Date(dateStr)
      if (isNaN(d.getTime())) return ''
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      const hours = String(d.getHours()).padStart(2, '0')
      const minutes = String(d.getMinutes()).padStart(2, '0')
      const seconds = String(d.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    async saveActivity() {
      const valid = await this.$refs.activityForm.validate().catch(() => false)
      if (!valid) {
        this.$message.error('请检查表单')
        return
      }
      this.saveLoading = true
      try {
        if (this.editingActivityId) {
          const updateData = {
            aid: this.editingActivityId,
            title: this.activityForm.title,
            location: this.activityForm.location,
            startTime: this.activityForm.startTime,
            endTime: this.activityForm.endTime,
            maxPlayers: this.activityForm.maxPlayers,
            levelRequire: this.activityForm.levelRequire,
            description: this.activityForm.description
          }
          await activityAPI.updateActivity(updateData)
          this.$message.success('活动更新成功')
        } else {
          await activityAPI.createActivity(this.activityForm)
          this.$message.success('活动创建成功')
        }
        this.dialogVisible = false
        this.loadActivities()
        this.loadMyActivities()
      } catch (error) {
        console.error('保存活动失败', error)
        this.$message.error('操作失败')
      } finally {
        this.saveLoading = false
      }
    },
    // 检查用户是否已有时间冲突的活动（已报名的且未结束的）
    hasTimeConflict(activity) {
      if (!this.myJoinedActivities.length) return false
      const newStart = new Date(activity.startTime).getTime()
      const newEnd = new Date(activity.endTime).getTime()
      return this.myJoinedActivities.some(joined => {
        // 已结束或已取消的活动不视为冲突（因为它们不再占用时间）
        if (joined.status === 2 || joined.status === 3) return false
        const joinedStart = new Date(joined.startTime).getTime()
        const joinedEnd = new Date(joined.endTime).getTime()
        // 判断两个时间段是否有重叠
        return newStart < joinedEnd && newEnd > joinedStart
      })
    },
    async joinActivity(aid) {
      const activity = this.activities.find(a => a.aid === aid) || 
                       this.myCreatedActivities.find(a => a.aid === aid) || 
                       this.myJoinedActivities.find(a => a.aid === aid)
      if (activity && this.isActivityEnded(activity)) {
        this.$message.warning('活动已结束，无法报名')
        return
      }
      if (activity && this.isActivityFull(activity)) {
        this.$message.warning('活动已满员，无法报名')
        return
      }
      // 时间冲突校验
      if (this.hasTimeConflict(activity)) {
        this.$message.warning('您已有其他时间重叠的活动，无法报名')
        return
      }
      try {
        await activityAPI.joinActivity(aid)
        this.$message.success('报名成功')
        this.loadActivities()
        this.loadMyActivities()
      } catch (error) {
        this.$message.error('报名失败')
      }
    },
    async cancelJoinActivity(aid) {
      const activity = this.myJoinedActivities.find(a => a.aid === aid)
      if (activity && this.isActivityEnded(activity)) {
        this.$message.warning('活动已结束，无法取消报名')
        return
      }
      try {
        await activityAPI.cancelActivity(aid)
        this.$message.success('取消报名成功')
        this.loadActivities()
        this.loadMyActivities()
      } catch (error) {
        this.$message.error('取消报名失败')
      }
    },
    async openDetailDialog(aid) {
      try {
        const res = await activityAPI.getActivityDetail(aid)
        const detail = res.data !== undefined ? res.data : res
        this.detail = detail
        this.detailVisible = true
      } catch (e) {
        this.$message.error('获取活动详情失败')
        console.error(e)
      }
    },
    getActivityStatusText(activity) {
      if (activity.status === 2) return '已取消'
      const now = new Date()
      const endTime = new Date(activity.endTime)
      if (now > endTime) return '已结束'
      const current = Number(activity.currentPlayers) || 0
      const max = Number(activity.maxPlayers) || 0
      if (current >= max) return '已满员'
      if (activity.status === 1) return '已满员'
      return '招募中'
    },
    getActivityStatusType(activity) {
      const text = this.getActivityStatusText(activity)
      const map = { '招募中': 'primary', '已满员': 'success', '已取消': 'danger', '已结束': 'info' }
      return map[text] || 'primary'
    },
    formatDateTime(dateTime) {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      return date.toLocaleString('zh-CN')
    },
    disabledPastDate(time) {
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      return time.getTime() < today.getTime()
    },
    isActivityEnded(activity) {
      if (!activity) return true
      if (activity.status === 2 || activity.status === 3) return true
      const now = new Date()
      const endTime = new Date(activity.endTime)
      return now > endTime
    },
    isActivityFull(activity) {
      if (!activity) return false
      const current = Number(activity.currentPlayers) || 0
      const max = Number(activity.maxPlayers) || 0
      return current >= max
    }
  }
}
</script>

<style scoped>
.user-activities {
  position: relative;
  min-height: calc(100vh - 130px);
  padding: 30px 4px 72px;
  overflow: hidden;
  color: #123b35;
}

.user-activities::before,
.user-activities::after {
  content: '';
  position: absolute;
  z-index: 0;
  border-radius: 999px;
  pointer-events: none;
}

.user-activities::before {
  top: 36px;
  right: -130px;
  width: 360px;
  height: 360px;
  background: radial-gradient(circle, rgba(119, 231, 214, 0.24), transparent 68%);
}

.user-activities::after {
  left: -100px;
  bottom: 12%;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(255, 207, 112, 0.19), transparent 64%);
}

.activities-hero,
:deep(.custom-tabs) {
  position: relative;
  z-index: 1;
}

.activities-hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 24px;
  align-items: end;
  margin-bottom: 24px;
  padding: 38px;
  border: 1px solid rgba(33, 145, 130, 0.12);
  border-radius: 34px;
  background:
    linear-gradient(120deg, rgba(255, 255, 255, 0.96), rgba(246, 255, 252, 0.88)),
    radial-gradient(circle at 88% 8%, rgba(96, 221, 203, 0.22), transparent 30%);
  box-shadow: 0 24px 70px rgba(38, 112, 101, 0.12);
}

.eyebrow,
.card-kicker {
  display: inline-flex;
  color: #2aa99a;
  font-size: 12px;
  font-weight: 900;
  letter-spacing: 0.18em;
}

.page-title {
  margin: 12px 0;
  color: #102f2a;
  font-size: clamp(38px, 5vw, 64px);
  font-weight: 900;
  line-height: 0.98;
  letter-spacing: -0.06em;
}

.page-subtitle {
  max-width: 620px;
  margin: 0;
  color: #5f7d77;
  font-size: 16px;
  line-height: 1.9;
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(92px, 1fr));
  gap: 12px;
}

.hero-metrics div {
  min-width: 110px;
  padding: 16px;
  border: 1px solid rgba(33, 145, 130, 0.1);
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.72);
}

.hero-metrics strong,
.hero-metrics span {
  display: block;
}

.hero-metrics strong {
  color: #0d8f82;
  font-size: 30px;
  line-height: 1;
}

.hero-metrics span {
  margin-top: 7px;
  color: #6d8c86;
  font-size: 13px;
}

:deep(.custom-tabs) {
  padding: 24px;
  border: 1px solid rgba(33, 145, 130, 0.1);
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.86);
  box-shadow: 0 20px 56px rgba(38, 112, 101, 0.1);
}

:deep(.el-tabs__header) {
  margin-bottom: 22px;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 0;
}

:deep(.el-tabs__nav) {
  gap: 8px;
}

:deep(.el-tabs__item) {
  height: 42px;
  padding: 0 18px !important;
  border-radius: 999px;
  color: #5f7d77;
  font-weight: 800;
  transition: background 0.2s ease, color 0.2s ease;
}

:deep(.el-tabs__item.is-active) {
  color: #0d7368 !important;
  background: rgba(47, 201, 184, 0.14);
}

:deep(.el-tabs__active-bar) {
  display: none;
}

.activity-section,
.my-activities {
  padding: 2px;
}

.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 22px;
  padding: 14px;
  border: 1px solid rgba(33, 145, 130, 0.1);
  border-radius: 24px;
  background: linear-gradient(135deg, rgba(248, 255, 252, 0.86), rgba(255, 255, 255, 0.9));
}

.search-input {
  max-width: 460px;
  flex: 1;
}

:deep(.search-input .el-input__wrapper),
:deep(.search-input .el-input-group__append) {
  border: 0;
  border-radius: 999px;
  background: #fff;
  box-shadow: inset 0 0 0 1px rgba(33, 145, 130, 0.1);
}

.activity-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.activity-card {
  overflow: hidden;
  border: 1px solid rgba(33, 145, 130, 0.1);
  border-radius: 28px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(250, 255, 253, 0.86)),
    radial-gradient(circle at 92% 12%, rgba(47, 201, 184, 0.14), transparent 26%);
  box-shadow: 0 16px 44px rgba(38, 112, 101, 0.09);
  transition:
    transform 0.28s ease,
    box-shadow 0.28s ease,
    border-color 0.28s ease;
}

.activity-card:hover {
  transform: translateY(-5px);
  border-color: rgba(33, 145, 130, 0.18);
  box-shadow: 0 24px 62px rgba(38, 112, 101, 0.14);
}

:deep(.activity-card .el-card__header) {
  padding: 20px 22px;
  border-bottom: 1px solid rgba(33, 145, 130, 0.1);
  background: rgba(255, 255, 255, 0.52);
}

:deep(.activity-card .el-card__body) {
  padding: 0;
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
}

.card-header h3 {
  margin: 6px 0 0;
  color: #173b36;
  font-size: 20px;
  font-weight: 900;
  line-height: 1.25;
  letter-spacing: -0.02em;
}

.card-body {
  padding: 20px 22px 22px;
}

.card-body p {
  display: flex;
  align-items: flex-start;
  gap: 9px;
  margin: 0 0 12px;
  color: #5f7d77;
  font-size: 14px;
  line-height: 1.6;
}

.card-body p .el-icon {
  flex: 0 0 auto;
  margin-top: 3px;
  color: #1db8a9;
}

.description {
  min-height: 48px;
  margin-top: 14px !important;
  padding: 14px;
  border-radius: 18px;
  color: #6d8c86 !important;
  background: #f4fffb;
}

.card-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 18px;
}

:deep(.el-button) {
  font-weight: 800;
}

:deep(.el-button--primary) {
  border: 0 !important;
  background: linear-gradient(135deg, #16b8a7, #67d7c8) !important;
  box-shadow: 0 10px 22px rgba(22, 184, 167, 0.2);
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 14px 28px rgba(22, 184, 167, 0.28);
}

.empty-state {
  display: grid;
  min-height: 190px;
  place-items: center;
  margin-top: 14px;
  border: 1px dashed rgba(33, 145, 130, 0.2);
  border-radius: 26px;
  color: #78918c;
  background: rgba(250, 255, 253, 0.78);
  text-align: center;
}

.empty-state strong,
.empty-state span {
  display: block;
}

.empty-state strong {
  color: #173b36;
  font-size: 18px;
}

.empty-state span {
  margin-top: 8px;
  font-size: 13px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 28px;
}

:deep(.el-pagination) {
  padding: 10px 14px;
  border: 1px solid rgba(33, 145, 130, 0.1);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.82);
}

:deep(.el-pagination button),
:deep(.el-pagination .el-pager li) {
  border-radius: 999px;
}

:deep(.el-pagination .el-pager li.is-active) {
  color: #0d7368;
  background: rgba(47, 201, 184, 0.14);
}

:deep(.custom-dialog .el-dialog) {
  overflow: hidden;
  border: 1px solid rgba(33, 145, 130, 0.12);
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 28px 80px rgba(38, 112, 101, 0.18);
}

:deep(.custom-dialog .el-dialog__header) {
  padding: 24px 28px 12px;
}

:deep(.custom-dialog .el-dialog__title) {
  color: #173b36;
  font-size: 22px;
  font-weight: 900;
}

:deep(.custom-dialog .el-dialog__body) {
  padding: 16px 28px 8px;
}

:deep(.custom-dialog .el-dialog__footer) {
  padding: 16px 28px 26px;
}

:deep(.custom-dialog .el-input__wrapper),
:deep(.custom-dialog .el-textarea__inner),
:deep(.custom-dialog .el-select__wrapper) {
  border-radius: 14px;
  box-shadow: inset 0 0 0 1px rgba(33, 145, 130, 0.12);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.detail-container {
  padding: 4px 0 10px;
}

.detail-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.detail-header h2 {
  margin: 0;
  color: #173b36;
  font-size: 24px;
  line-height: 1.25;
}

.detail-info {
  display: grid;
  gap: 10px;
}

.detail-info p {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin: 0;
  color: #5f7d77;
  font-size: 14px;
  line-height: 1.7;
}

.detail-info p .el-icon {
  flex: 0 0 auto;
  margin-top: 4px;
  color: #1db8a9;
}

.desc {
  margin-top: 8px !important;
  padding: 14px;
  border-radius: 18px;
  color: #6d8c86 !important;
  background: #f4fffb;
}

.creator-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 20px 0;
  padding: 13px 14px;
  border: 1px solid rgba(33, 145, 130, 0.1);
  border-radius: 18px;
  background: rgba(250, 255, 253, 0.9);
}

.creator-info span {
  color: #173b36;
  font-weight: 800;
}

.participants {
  margin-top: 20px;
}

.participants h4 {
  margin: 0 0 12px;
  color: #173b36;
}

.participants ul {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.participant-item {
  display: flex;
  align-items: center;
  gap: 7px;
  padding: 6px 12px;
  border-radius: 999px;
  background: #f4fffb;
}

.participant-item span {
  color: #5f7d77;
  font-size: 13px;
  font-weight: 800;
}

@media (max-width: 980px) {
  .activities-hero {
    grid-template-columns: 1fr;
    padding: 32px 26px;
  }

  .hero-metrics {
    grid-template-columns: repeat(3, 1fr);
  }

  .activity-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .user-activities {
    padding-top: 12px;
  }

  .activities-hero,
  :deep(.custom-tabs) {
    border-radius: 26px;
  }

  .activities-hero {
    padding: 26px 20px;
  }

  .page-title {
    font-size: 40px;
  }

  .hero-metrics {
    grid-template-columns: 1fr;
  }

  .search-bar {
    align-items: stretch;
    flex-direction: column;
  }

  .search-input {
    max-width: none;
    width: 100%;
  }

  .card-header {
    flex-direction: column;
  }

  :deep(.el-pagination) {
    width: 100%;
    justify-content: center;
    border-radius: 22px;
  }
}
</style>