<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="hero-copy">
        <span class="eyebrow">LIGHT COURT NETWORK</span>
        <h1>一拍即合，即刻开球</h1>
        <p class="subtitle">发现附近球友、加入约球活动、预订优质场馆，让羽毛球从临时起意变成轻松日常。</p>

        <div class="hero-actions">
          <el-button type="primary" size="large" @click="router.push('/activities')">浏览活动</el-button>
          <el-button size="large" plain @click="router.push('/venues')">查找场馆</el-button>
        </div>

        <div class="hero-stats" aria-label="平台能力">
          <div>
            <strong>{{ activities.length }}</strong>
            <span>近期活动</span>
          </div>
          <div>
            <strong>{{ hotMerchants.length }}</strong>
            <span>热门场馆</span>
          </div>
        </div>
      </div>

      <div class="hero-visual" aria-hidden="true">
        <div class="court-card">
          <span class="shuttle shuttle-one"></span>
          <span class="shuttle shuttle-two"></span>
          <div class="court-lines">
            <span></span>
            <span></span>
            <span></span>
          </div>
          <div class="booking-note">
            <small>今日推荐</small>
            <strong>19:00 双打友谊局</strong>
            <span>离你 2.4km · 中级</span>
          </div>
        </div>
      </div>
    </section>

    <section class="feature-strip" aria-label="平台特色">
      <div class="feature-card">
        <span>01</span>
        <strong>智能匹配球友</strong>
        <p>依据水平、地区和活跃度推荐更合拍的球友。</p>
      </div>
      <div class="feature-card">
        <span>02</span>
        <strong>活动快速组队</strong>
        <p>创建、报名、管理活动都集中在一个清爽流程里。</p>
      </div>
      <div class="feature-card">
        <span>03</span>
        <strong>场馆在线预订</strong>
        <p>查看商家与场地详情，直接选择时段发起订单。</p>
      </div>
    </section>

    <el-row :gutter="24" class="home-content">
      <el-col :xs="24" :lg="11">
        <el-card class="home-card activity-panel" shadow="never">
          <template #header>
            <div class="card-header">
              <div>
                <span class="section-kicker">MATCHES</span>
                <strong>最新活动</strong>
              </div>
              <router-link to="/activities">查看全部</router-link>
            </div>
          </template>
          <div class="card-body">
            <router-link v-for="act in activities" :key="act.aid" to="/activities" class="list-item">
              <span class="item-dot"></span>
              <span class="item-main">
                <strong>{{ act.title }}</strong>
                <small>{{ act.location || '场地待定' }}</small>
              </span>
              <span class="time">{{ act.startTime }}</span>
            </router-link>
            <div v-if="activities.length === 0" class="empty-tip">
              <span>暂无活动</span>
              <small>先去发布一场属于你的羽毛球局吧</small>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="13">
        <el-card class="home-card venue-panel" shadow="never">
          <template #header>
            <div class="card-header">
              <div>
                <span class="section-kicker">COURTS</span>
                <strong>热门场馆</strong>
              </div>
              <router-link to="/venues">探索场馆</router-link>
            </div>
          </template>
          <div class="card-body venue-list">
            <div v-for="merchant in hotMerchants" :key="merchant.mid" class="merchant-item" @click="goToMerchant(merchant.mid)">
              <div class="merchant-badge">{{ merchant.companyName?.slice(0, 1) || '馆' }}</div>
              <div class="merchant-info">
                <h3>{{ merchant.companyName }}</h3>
                <p>{{ merchant.address || '地址待完善' }}</p>
                <p class="price">联系电话：{{ merchant.contactPhone || '暂无' }}</p>
              </div>
              <el-button type="primary" size="small" round>查看详情</el-button>
            </div>
            <div v-if="hotMerchants.length === 0" class="empty-tip">
              <span>暂无热门场馆</span>
              <small>审核通过的场馆会展示在这里</small>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getActivityList } from '@/api/activity'
import { getMerchantList } from '@/api/merchant'

const router = useRouter()
const activities = ref([])
const hotMerchants = ref([])

onMounted(async () => {
  try {
    const actRes = await getActivityList({ page: 1, size: 5 })
    activities.value = actRes || []
    const merchantRes = await getMerchantList()
    hotMerchants.value = merchantRes.slice(0, 5)
  } catch (error) {
    console.error('加载首页数据失败', error)
  }
})

const goToMerchant = (mid) => {
  router.push(`/merchant/${mid}`)
}
</script>

<style scoped>
.home-page {
  position: relative;
  min-height: calc(100vh - 130px);
  padding: 30px 4px 72px;
  overflow: hidden;
  color: #123b35;
}

.home-page::before,
.home-page::after {
  content: '';
  position: absolute;
  z-index: 0;
  border-radius: 999px;
  pointer-events: none;
}

.home-page::before {
  top: 18px;
  right: -120px;
  width: 360px;
  height: 360px;
  background: radial-gradient(circle, rgba(119, 231, 214, 0.26), transparent 68%);
}

.home-page::after {
  left: -90px;
  bottom: 16%;
  width: 280px;
  height: 280px;
  background: radial-gradient(circle, rgba(255, 207, 112, 0.2), transparent 64%);
}

.hero-section,
.feature-strip,
.home-content {
  position: relative;
  z-index: 1;
}

.hero-section {
  display: grid;
  grid-template-columns: minmax(0, 1.08fr) minmax(320px, 0.92fr);
  gap: 34px;
  align-items: center;
  min-height: 430px;
  padding: 54px;
  border: 1px solid rgba(33, 145, 130, 0.12);
  border-radius: 38px;
  background:
    linear-gradient(120deg, rgba(255, 255, 255, 0.96), rgba(246, 255, 252, 0.88)),
    radial-gradient(circle at 84% 14%, rgba(96, 221, 203, 0.22), transparent 30%);
  box-shadow: 0 24px 70px rgba(38, 112, 101, 0.12);
}

.eyebrow,
.section-kicker {
  display: inline-flex;
  color: #2aa99a;
  font-size: 12px;
  font-weight: 900;
  letter-spacing: 0.18em;
}

.hero-copy h1 {
  max-width: 640px;
  margin: 16px 0 18px;
  color: #102f2a;
  font-size: clamp(42px, 6vw, 72px);
  line-height: 1.1;
  letter-spacing: -0.06em;
  white-space: nowrap; 
}

.subtitle {
  max-width: 590px;
  margin: 0;
  color: #5f7d77;
  font-size: 17px;
  line-height: 1.9;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 30px;
}

:deep(.hero-actions .el-button) {
  height: 46px;
  padding-inline: 24px;
  border-radius: 999px;
  font-weight: 800;
}

:deep(.el-button--primary) {
  border: 0 !important;
  background: linear-gradient(135deg, #16b8a7, #67d7c8) !important;
  box-shadow: 0 12px 24px rgba(22, 184, 167, 0.22);
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 16px 30px rgba(22, 184, 167, 0.28);
}

.hero-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 34px;
}

.hero-stats div {
  min-width: 112px;
  padding: 14px 16px;
  border: 1px solid rgba(33, 145, 130, 0.1);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.68);
}

.hero-stats strong,
.hero-stats span {
  display: block;
}

.hero-stats strong {
  color: #0d8f82;
  font-size: 28px;
  line-height: 1;
}

.hero-stats span {
  margin-top: 6px;
  color: #6d8c86;
  font-size: 13px;
}

.hero-visual {
  display: flex;
  justify-content: center;
}

.court-card {
  position: relative;
  width: min(100%, 410px);
  aspect-ratio: 0.86;
  overflow: hidden;
  border: 12px solid rgba(255, 255, 255, 0.82);
  border-radius: 34px;
  background:
    linear-gradient(90deg, rgba(255, 255, 255, 0.46) 1px, transparent 1px),
    linear-gradient(rgba(255, 255, 255, 0.42) 1px, transparent 1px),
    linear-gradient(145deg, #d9fbf4, #fef8ea 58%, #ffffff);
  background-size: 46px 46px, 46px 46px, auto;
  box-shadow: 0 30px 70px rgba(30, 111, 99, 0.18);
}

.court-lines {
  position: absolute;
  inset: 42px;
  border: 2px solid rgba(25, 166, 151, 0.34);
  border-radius: 24px;
}

.court-lines span {
  position: absolute;
  background: rgba(25, 166, 151, 0.28);
}

.court-lines span:nth-child(1) {
  top: 50%;
  left: 0;
  width: 100%;
  height: 2px;
}

.court-lines span:nth-child(2) {
  top: 0;
  left: 50%;
  width: 2px;
  height: 100%;
}

.court-lines span:nth-child(3) {
  top: 18%;
  left: 22%;
  width: 56%;
  height: 2px;
  box-shadow: 0 178px 0 rgba(25, 166, 151, 0.22);
}

.shuttle {
  position: absolute;
  width: 58px;
  height: 58px;
  border-radius: 50% 50% 50% 12px;
  background: linear-gradient(135deg, #ffffff, #e9fbf7);
  box-shadow: 0 12px 26px rgba(22, 119, 108, 0.18);
}

.shuttle::after {
  content: '';
  position: absolute;
  right: 8px;
  bottom: 8px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #f3bd55;
}

.shuttle-one {
  top: 38px;
  right: 44px;
  transform: rotate(18deg);
}

.shuttle-two {
  left: 46px;
  bottom: 96px;
  transform: rotate(-26deg) scale(0.72);
  opacity: 0.72;
}

.booking-note {
  position: absolute;
  right: 24px;
  bottom: 24px;
  left: 24px;
  padding: 18px;
  border: 1px solid rgba(255, 255, 255, 0.74);
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.76);
  backdrop-filter: blur(14px);
  box-shadow: 0 18px 40px rgba(20, 118, 105, 0.14);
}

.booking-note small,
.booking-note strong,
.booking-note span {
  display: block;
}

.booking-note small {
  color: #2aa99a;
  font-weight: 900;
  letter-spacing: 0.08em;
}

.booking-note strong {
  margin: 6px 0;
  font-size: 20px;
}

.booking-note span {
  color: #6d8c86;
  font-size: 13px;
}

.feature-strip {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin: 24px 0;
}

.feature-card {
  padding: 22px;
  border: 1px solid rgba(33, 145, 130, 0.1);
  border-radius: 26px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 12px 36px rgba(40, 122, 111, 0.08);
}

.feature-card span {
  color: #e8a944;
  font-size: 12px;
  font-weight: 900;
}

.feature-card strong {
  display: block;
  margin: 10px 0 8px;
  font-size: 17px;
}

.feature-card p {
  margin: 0;
  color: #708e88;
  font-size: 13px;
  line-height: 1.7;
}

.home-content {
  margin-top: 24px;
}

.home-content :deep(.el-col) {
  margin-bottom: 24px;
}

.home-card {
  height: 100%;
  overflow: hidden;
  border: 1px solid rgba(33, 145, 130, 0.1);
  border-radius: 30px;
  background: rgba(255, 255, 255, 0.86);
  box-shadow: 0 20px 56px rgba(38, 112, 101, 0.1);
  transition:
    transform 0.28s ease,
    box-shadow 0.28s ease;
}

.home-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 26px 70px rgba(38, 112, 101, 0.15);
}

:deep(.home-card .el-card__header) {
  padding: 22px 24px;
  border-bottom: 1px solid rgba(33, 145, 130, 0.1);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.78), rgba(248, 255, 252, 0.78));
}

:deep(.home-card .el-card__body) {
  padding: 14px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.card-header strong {
  display: block;
  margin-top: 4px;
  font-size: 22px;
  letter-spacing: -0.02em;
}

.card-header a {
  color: #159f91;
  font-size: 13px;
  font-weight: 900;
  text-decoration: none;
}

.card-body {
  display: grid;
  gap: 10px;
}

.list-item {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
  padding: 16px;
  border: 1px solid transparent;
  border-radius: 20px;
  color: inherit;
  text-decoration: none;
  transition:
    background 0.2s ease,
    border-color 0.2s ease,
    transform 0.2s ease;
}

.list-item:hover,
.merchant-item:hover {
  border-color: rgba(33, 145, 130, 0.16);
  background: #f4fffb;
  transform: translateX(4px);
}

.item-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #2fc9b8;
  box-shadow: 0 0 0 7px rgba(47, 201, 184, 0.12);
}

.item-main {
  min-width: 0;
}

.item-main strong {
  display: block;
  overflow: hidden;
  color: #183d38;
  font-size: 15px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-main small,
.time {
  color: #78918c;
  font-size: 12px;
}

.time {
  max-width: 128px;
  text-align: right;
}

.venue-list {
  gap: 12px;
}

.merchant-item {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  gap: 14px;
  align-items: center;
  padding: 16px;
  border: 1px solid transparent;
  border-radius: 22px;
  cursor: pointer;
  transition:
    background 0.2s ease,
    border-color 0.2s ease,
    transform 0.2s ease;
}

.merchant-badge {
  display: grid;
  width: 48px;
  height: 48px;
  place-items: center;
  border-radius: 18px;
  color: #0f776d;
  background: linear-gradient(135deg, #dffbf4, #fff2d6);
  font-size: 20px;
  font-weight: 900;
}

.merchant-info {
  min-width: 0;
}

.merchant-info h3 {
  margin: 0 0 7px;
  overflow: hidden;
  color: #173b36;
  font-size: 17px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.merchant-info p {
  margin: 4px 0;
  overflow: hidden;
  color: #728d88;
  font-size: 13px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.merchant-info .price {
  color: #159f91;
  font-weight: 800;
}

.empty-tip {
  display: grid;
  place-items: center;
  min-height: 150px;
  color: #78918c;
  text-align: center;
}

.empty-tip span {
  color: #173b36;
  font-size: 16px;
  font-weight: 900;
}

.empty-tip small {
  margin-top: 8px;
}

@media (max-width: 980px) {
  .hero-section {
    grid-template-columns: 1fr;
    padding: 36px 28px;
  }

  .hero-visual {
    order: -1;
  }

  .court-card {
    max-width: 360px;
  }

  .feature-strip {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .home-page {
    padding-top: 12px;
  }

  .hero-section {
    padding: 28px 20px;
    border-radius: 28px;
  }

  .hero-copy h1 {
    font-size: 40px;
  }

  .hero-stats div {
    flex: 1;
    min-width: 90px;
  }

  .list-item,
  .merchant-item {
    grid-template-columns: auto minmax(0, 1fr);
  }

  .time,
  .merchant-item :deep(.el-button) {
    grid-column: 2;
    justify-self: start;
    text-align: left;
  }
}
</style>