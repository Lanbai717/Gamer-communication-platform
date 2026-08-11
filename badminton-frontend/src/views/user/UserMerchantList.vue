<template>
  <div class="merchant-list">
    <section class="venues-hero">
      <div>
        <span class="eyebrow">COURT FINDER</span>
        <h2 class="page-title">羽毛球场地</h2>
        <p class="page-subtitle">用清爽的方式找到合适场馆，查看营业时间、设施配置和可预订场地。</p>
      </div>
      <div class="hero-metrics">
        <div>
          <strong>{{ merchants.length }}</strong>
          <span>入驻场馆</span>
        </div>
        <div>
          <strong>{{ total }}</strong>
          <span>当前结果</span>
        </div>
        <div>
          <strong>{{ Object.values(venueCountMap).reduce((sum, count) => sum + count, 0) }}</strong>
          <span>可选场地</span>
        </div>
      </div>
    </section>

    <div class="search-filter">
      <el-input v-model="searchKeyword" placeholder="搜索场馆名称或地址" class="search-input" clearable @clear="searchMerchants" @keyup.enter="searchMerchants">
        <template #append>
          <el-button @click="searchMerchants"><el-icon><Search /></el-icon></el-button>
        </template>
      </el-input>
      <el-select v-model="sortBy" placeholder="排序方式" class="sort-select" @change="loadMerchants">
        <el-option label="默认排序" value="default"></el-option>
        <el-option label="距离最近" value="distance"></el-option>
        <el-option label="热度最高" value="hot"></el-option>
      </el-select>
    </div>

    <el-row :gutter="24" class="merchant-list-row" v-loading="loading">
      <el-col :xs="24" :md="12" :lg="8" v-for="merchant in filteredMerchants" :key="merchant.mid" class="merchant-item">
        <el-card shadow="never" class="merchant-card" @click="goToDetail(merchant.mid)">
          <div class="merchant-photo" v-if="merchant.venuePhotos">
            <img :src="merchant.venuePhotos.split(',')[0] || defaultImage" alt="场馆图片" />
            <span class="photo-badge">COURT</span>
          </div>
          <div v-else class="merchant-photo photo-placeholder">
            <span>{{ merchant.companyName?.slice(0, 1) || '馆' }}</span>
            <small>BADMINTON</small>
          </div>
          <div class="card-header">
            <div>
              <span class="card-kicker">VENUE</span>
              <h3>{{ merchant.companyName }}</h3>
            </div>
            <el-rate v-model="merchant.rating" disabled size="small" :value="4.5" />
          </div>
          <div class="card-body">
            <div class="merchant-info">
              <p><el-icon><Location /></el-icon><span>{{ merchant.address || '地址待完善' }}</span></p>
              <p><el-icon><Phone /></el-icon><span>{{ merchant.contactPhone || '暂无联系电话' }}</span></p>
              <p><el-icon><Timer /></el-icon><span>{{ formatBusinessHours(merchant.businessHours) }}</span></p>
              <div v-if="merchant.facilities" class="facilities">
                <el-tag v-for="(fac, idx) in merchant.facilities.split(',')" :key="idx" size="small" class="fac-tag">{{ fac }}</el-tag>
              </div>
            </div>
            <div class="card-footer">
              <span class="venue-count">共 {{ getVenueCount(merchant.mid) }} 个场地</span>
              <el-button type="primary" size="small" round>查看详情</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div v-if="!loading && filteredMerchants.length === 0" class="empty-state">
      <strong>没有找到匹配场馆</strong>
      <span>换个关键词或排序方式试试。</span>
    </div>

    <div class="pagination" v-if="total > 0">
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
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Location, Phone, Timer } from '@element-plus/icons-vue'
import { getMerchantList } from '@/api/merchant'
import { getVenueList } from '@/api/venue'

const router = useRouter()
const searchKeyword = ref('')
const sortBy = ref('default')
const merchants = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const defaultImage = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

// 存储每个商家的场地数量（临时）
const venueCountMap = ref({})

const filteredMerchants = computed(() => {
  let list = merchants.value
  if (searchKeyword.value) {
    const kw = searchKeyword.value.toLowerCase()
    list = list.filter(m => 
      m.companyName?.toLowerCase().includes(kw) || 
      m.address?.toLowerCase().includes(kw)
    )
  }
  total.value = list.length
  const start = (currentPage.value - 1) * pageSize.value
  return list.slice(start, start + pageSize.value)
})

const loadMerchants = async () => {
  loading.value = true
  try {
    const res = await getMerchantList()
    merchants.value = res || []
    // 获取每个商家的场地数量（可优化为后端直接返回）
    for (const m of merchants.value) {
      const venues = await getVenueList({ merchantId: m.mid })
      venueCountMap.value[m.mid] = venues.length
    }
  } catch (error) {
    ElMessage.error('加载商家列表失败')
  } finally {
    loading.value = false
  }
}

// 添加格式化营业时间的方法
const formatBusinessHours = (businessHours) => {
  if (!businessHours) return '未设置'
  try {
    const hoursList = JSON.parse(businessHours)
    if (!hoursList.length) return '未设置'
    // 将营业时段数组转换为中文文本
    const dayMap = { 1: '周一', 2: '周二', 3: '周三', 4: '周四', 5: '周五', 6: '周六', 7: '周日' }
    return hoursList.map(item => {
      const startDay = dayMap[item.startDay]
      const endDay = dayMap[item.endDay]
      if (startDay === endDay) {
        return `${startDay} ${item.startTime}-${item.endTime}`
      } else {
        return `${startDay}到${endDay} ${item.startTime}-${item.endTime}`
      }
    }).join('；')
  } catch (e) {
    return '格式错误'
  }
}

const getVenueCount = (mid) => venueCountMap.value[mid] || 0

const searchMerchants = () => {
  currentPage.value = 1
  // 已通过 computed 自动过滤
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}
const handleCurrentChange = (page) => {
  currentPage.value = page
}

const goToDetail = (mid) => {
  router.push(`/merchant/${mid}`)
}

onMounted(() => {
  loadMerchants()
})
</script>

<style scoped>
.merchant-list {
  position: relative;
  min-height: calc(100vh - 130px);
  padding: 30px 4px 72px;
  overflow: hidden;
  color: #123b35;
}

.merchant-list::before,
.merchant-list::after {
  content: '';
  position: absolute;
  z-index: 0;
  border-radius: 999px;
  pointer-events: none;
}

.merchant-list::before {
  top: 28px;
  right: -130px;
  width: 360px;
  height: 360px;
  background: radial-gradient(circle, rgba(119, 231, 214, 0.24), transparent 68%);
}

.merchant-list::after {
  left: -90px;
  bottom: 12%;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(255, 207, 112, 0.19), transparent 64%);
}

.venues-hero,
.search-filter,
.merchant-list-row,
.empty-state,
.pagination {
  position: relative;
  z-index: 1;
}

.venues-hero {
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

.search-filter {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 24px;
  padding: 16px;
  border: 1px solid rgba(33, 145, 130, 0.1);
  border-radius: 28px;
  background: linear-gradient(135deg, rgba(248, 255, 252, 0.86), rgba(255, 255, 255, 0.9));
  box-shadow: 0 16px 44px rgba(38, 112, 101, 0.08);
}

.search-input {
  max-width: 460px;
  flex: 1;
}

.sort-select {
  width: 168px;
}

:deep(.search-input .el-input__wrapper),
:deep(.search-input .el-input-group__append),
:deep(.sort-select .el-select__wrapper) {
  border: 0;
  border-radius: 999px;
  background: #fff;
  box-shadow: inset 0 0 0 1px rgba(33, 145, 130, 0.1);
}

.merchant-list-row {
  min-height: 220px;
  margin-bottom: 8px;
}

.merchant-item {
  margin-bottom: 24px;
}

.merchant-card {
  cursor: pointer;
  overflow: hidden;
  border: 1px solid rgba(33, 145, 130, 0.1);
  border-radius: 30px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(250, 255, 253, 0.86)),
    radial-gradient(circle at 92% 12%, rgba(47, 201, 184, 0.14), transparent 26%);
  box-shadow: 0 16px 44px rgba(38, 112, 101, 0.09);
  transition:
    transform 0.28s ease,
    box-shadow 0.28s ease,
    border-color 0.28s ease;
}

.merchant-card:hover {
  transform: translateY(-6px);
  border-color: rgba(33, 145, 130, 0.18);
  box-shadow: 0 24px 62px rgba(38, 112, 101, 0.14);
}

:deep(.merchant-card .el-card__body) {
  padding: 0;
}

.merchant-photo {
  position: relative;
  height: 190px;
  overflow: hidden;
  margin: 14px 14px 0;
  border-radius: 24px;
  background:
    linear-gradient(90deg, rgba(255, 255, 255, 0.46) 1px, transparent 1px),
    linear-gradient(rgba(255, 255, 255, 0.42) 1px, transparent 1px),
    linear-gradient(145deg, #d9fbf4, #fef8ea 58%, #ffffff);
  background-size: 38px 38px, 38px 38px, auto;
}

.merchant-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.merchant-card:hover .merchant-photo img {
  transform: scale(1.05);
}

.photo-badge {
  position: absolute;
  top: 14px;
  left: 14px;
  padding: 6px 10px;
  border-radius: 999px;
  color: #0d7368;
  background: rgba(255, 255, 255, 0.82);
  font-size: 11px;
  font-weight: 900;
  letter-spacing: 0.14em;
  backdrop-filter: blur(10px);
}

.photo-placeholder {
  display: grid;
  place-items: center;
  text-align: center;
}

.photo-placeholder span {
  display: grid;
  width: 78px;
  height: 78px;
  place-items: center;
  border-radius: 28px;
  color: #0f776d;
  background: rgba(255, 255, 255, 0.72);
  font-size: 36px;
  font-weight: 900;
  box-shadow: 0 18px 42px rgba(20, 118, 105, 0.12);
}

.photo-placeholder small {
  margin-top: -34px;
  color: #2aa99a;
  font-size: 11px;
  font-weight: 900;
  letter-spacing: 0.18em;
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  padding: 20px 22px 12px;
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
  padding: 0 22px 22px;
}

.merchant-info p {
  display: flex;
  align-items: flex-start;
  gap: 9px;
  margin: 0 0 11px;
  color: #5f7d77;
  font-size: 14px;
  line-height: 1.6;
}

.merchant-info p .el-icon {
  flex: 0 0 auto;
  margin-top: 3px;
  color: #1db8a9;
}

.merchant-info p span {
  min-width: 0;
}

.facilities {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 14px;
}

.fac-tag {
  border: 0;
  border-radius: 999px;
  color: #0d7368;
  background: rgba(47, 201, 184, 0.14);
  font-weight: 800;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 18px;
  padding-top: 16px;
  border-top: 1px solid rgba(33, 145, 130, 0.1);
}

.venue-count {
  color: #159f91;
  font-size: 12px;
  font-weight: 900;
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
  min-height: 210px;
  place-items: center;
  margin-top: 10px;
  border: 1px dashed rgba(33, 145, 130, 0.2);
  border-radius: 28px;
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
  margin-top: 18px;
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

@media (max-width: 980px) {
  .venues-hero {
    grid-template-columns: 1fr;
    padding: 32px 26px;
  }

  .hero-metrics {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 640px) {
  .merchant-list {
    padding-top: 12px;
  }

  .venues-hero {
    padding: 26px 20px;
    border-radius: 26px;
  }

  .page-title {
    font-size: 40px;
  }

  .hero-metrics {
    grid-template-columns: 1fr;
  }

  .search-filter {
    align-items: stretch;
    flex-direction: column;
  }

  .search-input,
  .sort-select {
    max-width: none;
    width: 100%;
  }

  .card-header,
  .card-footer {
    align-items: flex-start;
    flex-direction: column;
  }

  :deep(.el-pagination) {
    width: 100%;
    justify-content: center;
    border-radius: 22px;
  }
}
</style>