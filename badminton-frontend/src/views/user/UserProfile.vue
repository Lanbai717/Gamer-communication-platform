<template>
  <div class="user-profile">
    <h2 class="page-title">个人资料</h2>
    <el-card class="profile-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
          <div class="header-buttons">
            <el-button type="warning" size="small" plain @click="goToRequests">
              好友请求
              <el-badge :value="pendingCount" :hidden="pendingCount === 0" class="badge-inline" />
            </el-button>
            <el-button type="primary" size="small" @click="editProfile">编辑资料</el-button>
          </div>
        </div>
      </template>
      <div class="profile-content">
        <div class="avatar-section">
          <el-avatar :size="100" :src="userInfo.avatar || defaultAvatar" class="avatar"></el-avatar>
          <h3>{{ userInfo.nickname || userInfo.username }}</h3>
          <p class="skill-level">{{ getSkillLevelText(userInfo.skillLevel) }}</p>
          <p class="location">{{ userInfo.province }}{{ userInfo.city }}{{ userInfo.district || '未设置地区' }}</p>
        </div>
        <el-form :model="userInfo" label-width="100px" class="info-form">
          <el-form-item label="用户名">
            <span>{{ userInfo.username }}</span>
          </el-form-item>
          <el-form-item label="昵称">
            <span>{{ userInfo.nickname || '未设置' }}</span>
          </el-form-item>
          <el-form-item label="手机号">
            <span>{{ userInfo.phone || '未设置' }}</span>
          </el-form-item>
          <el-form-item label="邮箱">
            <span>{{ userInfo.email || '未设置' }}</span>
          </el-form-item>
          <el-form-item label="性别">
            <span>{{ userInfo.gender || '未设置' }}</span>
          </el-form-item>
          <el-form-item label="生日">
            <span>{{ userInfo.birthday || '未设置' }}</span>
          </el-form-item>
          <el-form-item label="羽毛球等级">
            <span>{{ getSkillLevelText(userInfo.skillLevel) }}</span>
          </el-form-item>
          <el-form-item label="个性签名">
            <span>{{ userInfo.signature || '未设置' }}</span>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 编辑资料对话框（保持不变） -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑个人资料"
      width="580px"
    >
      <el-form :model="editForm" :rules="rules" ref="editFormRef" label-width="100px">
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="editForm.nickname"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email"></el-input>
        </el-form-item>
        <el-form-item label="羽毛球等级" prop="skillLevel">
          <el-select v-model="editForm.skillLevel" placeholder="请选择等级">
            <el-option
              v-for="item in levelOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="editForm.gender" placeholder="请选择">
            <el-option label="男" value="男"></el-option>
            <el-option label="女" value="女"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="生日" prop="birthday">
          <el-date-picker
            v-model="editForm.birthday"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="所在地区" prop="location">
          <el-cascader
            v-model="locationArr"
            :options="regionOptions"
            placeholder="请选择省/市/区"
            style="width: 100%"
            @change="handleRegionChange"
          ></el-cascader>
        </el-form-item>
        <el-form-item label="个性签名" prop="signature">
          <el-input v-model="editForm.signature" type="textarea" rows="3"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveProfile" :loading="saveLoading">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getUserInfo, updateUserInfo } from '@/api/user'
import { regionData } from 'element-china-area-data'
import request from '@/utils/request'

export default {
  name: 'UserProfile',
  data() {
    return {
      loading: false,
      saveLoading: false,
      dialogVisible: false,
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      userInfo: {
        username: '',
        nickname: '',
        phone: '',
        email: '',
        skillLevel: '',
        gender: '',
        birthday: '',
        signature: '',
        province: '',
        city: '',
        district: ''
      },
      editForm: {
        nickname: '',
        phone: '',
        email: '',
        skillLevel: '',
        gender: '',
        birthday: '',
        signature: '',
        province: '',
        city: '',
        district: ''
      },
      locationArr: [],
      regionOptions: regionData,
      levelOptions: [
        { label: '国际级运动健将', value: '0' },
        { label: '运动健将', value: '1' },
        { label: '一级运动员', value: '2' },
        { label: '二级运动员', value: '3' },
        { label: '三级运动员', value: '4' },
        { label: '业余高级', value: '5' },
        { label: '业余中级', value: '6' },
        { label: '业余初级', value: '7' }
      ],
      rules: {
        nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        skillLevel: [{ required: true, message: '请选择羽毛球等级', trigger: 'change' }]
      },
      pendingCount: 0
    }
  },
  mounted() {
    this.fetchUserInfo()
    this.fetchPendingCount()
  },
  methods: {
    async fetchUserInfo() {
      this.loading = true
      try {
        const res = await getUserInfo()
        const userData = res.data || res
        this.userInfo = {
          username: userData.username || '',
          nickname: userData.nickname || '',
          phone: userData.phone || '',
          email: userData.email || '',
          skillLevel: userData.skillLevel || '',
          gender: userData.gender || '',
          birthday: userData.birthday || '',
          signature: userData.signature || '',
          avatar: userData.avatar || '',
          province: userData.province || '',
          city: userData.city || '',
          district: userData.district || ''
        }
      } catch (error) {
        this.$message.error('加载用户信息失败')
      } finally {
        this.loading = false
      }
    },
    editProfile() {
      this.editForm = { ...this.userInfo }
      this.locationArr = [this.editForm.province, this.editForm.city, this.editForm.district]
      this.dialogVisible = true
    },
    handleRegionChange(value) {
      if (value && value.length === 3) {
        const findLabel = (list, code) => {
          for (let item of list) {
            if (item.value === code) return item.label
            if (item.children) {
              const found = findLabel(item.children, code)
              if (found) return found
            }
          }
          return null
        }
        this.editForm.province = findLabel(this.regionOptions, value[0]) || value[0]
        this.editForm.city = findLabel(this.regionOptions, value[1]) || value[1]
        this.editForm.district = findLabel(this.regionOptions, value[2]) || value[2]
      } else {
        this.editForm.province = ''
        this.editForm.city = ''
        this.editForm.district = ''
      }
    },
    async saveProfile() {
      const valid = await this.$refs.editFormRef.validate().catch(() => false)
      if (!valid) {
        this.$message.error('请检查表单')
        return
      }
      this.saveLoading = true
      try {
        await updateUserInfo(this.editForm)
        this.userInfo = { ...this.editForm }
        this.dialogVisible = false
        this.$message.success('资料更新成功')
      } catch (error) {
        this.$message.error('更新失败，请重试')
      } finally {
        this.saveLoading = false
      }
    },
    getSkillLevelText(level) {
      const map = {
        '0': '国际级运动健将',
        '1': '运动健将',
        '2': '一级运动员',
        '3': '二级运动员',
        '4': '三级运动员',
        '5': '业余高级',
        '6': '业余中级',
        '7': '业余初级'
      }
      return map[level] || '未设置'
    },
    async fetchPendingCount() {
      try {
        const res = await request.get('/user/friend/requests')
        const list = res.data || []
        this.pendingCount = list.filter(item => item.status === 0).length
      } catch (e) {
        // 忽略错误
      }
    },
    goToRequests() {
      this.$router.push('/friend-requests')
    }
  }
}
</script>

<style scoped>
.user-profile {
  min-height: 100%;
}
.page-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #303133;
}
.profile-card {
  width: 100%;
  max-width: none;
  box-sizing: border-box;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-buttons {
  display: flex;
  gap: 10px;
  align-items: center;
}
.badge-inline {
  margin-left: -6px;
}
.avatar-section {
  text-align: center;
  margin-bottom: 20px;
}
.avatar {
  margin-bottom: 10px;
}
.skill-level {
  color: #409eff;
  font-weight: bold;
}
.location {
  color: #666;
  margin-top: 5px;
}
</style>