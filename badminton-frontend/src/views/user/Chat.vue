<template>
  <div class="chat-container">
    <!-- 好友列表 -->
    <div class="friend-panel">
      <div class="panel-header"><h3>我的好友</h3></div>
      <div class="friend-list">
        <div
          v-for="friend in friends"
          :key="friend.friendUid"
          class="friend-item"
          :class="{ active: selectedFriend?.friendUid === friend.friendUid }"
          @click="selectFriend(friend)"
        >
          <el-avatar :size="40" :src="friend.friendAvatar || defaultAvatar" />
          <div class="friend-info">
            <span class="friend-name">{{ friend.friendNickname }}</span>
            <span class="friend-status">在线</span>
          </div>
        </div>
        <el-empty v-if="friends.length === 0" description="暂无好友" />
      </div>
    </div>

    <!-- 聊天窗口 -->
    <div class="chat-window" v-if="selectedFriend">
      <div class="chat-header">{{ selectedFriend.friendNickname }}</div>
      <div class="chat-messages" ref="messageContainer">
        <div v-if="loadingHistory" class="loading-history">加载历史消息…</div>
        <div
          v-for="msg in messages"
          :key="msg.id"
          class="message-row"
          :class="{ 'message-self': msg.senderUid === currentUid }"
        >
          <template v-if="msg.senderUid !== currentUid">
            <el-avatar :size="32" :src="selectedFriend.friendAvatar || defaultAvatar" class="avatar" />
            <div class="bubble other">
              <div class="content">{{ msg.content }}</div>
              <div class="time">{{ formatTime(msg.createTime) }}</div>
            </div>
          </template>
          <template v-else>
            <div class="bubble self">
              <div class="content">{{ msg.content }}</div>
              <div class="time">{{ formatTime(msg.createTime) }}</div>
            </div>
            <el-avatar :size="32" :src="currentUserAvatar" class="avatar" />
          </template>
        </div>
      </div>
      <div class="chat-input">
        <el-input
          v-model="inputText"
          placeholder="输入消息..."
          @keyup.enter="sendMessage"
          clearable
        >
          <template #append>
            <el-button type="primary" @click="sendMessage" :disabled="!inputText.trim()">发送</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <div class="chat-placeholder" v-else>
      <el-empty description="请选择好友开始聊天" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onBeforeUnmount } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
// ✅ 从 Pinia store 获取当前用户ID，确保准确
const currentUid = userStore.userInfo?.uid || userStore.userInfo?.id
const currentUserAvatar = userStore.userInfo?.avatar || ''
const token = userStore.token || localStorage.getItem('token')

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const friends = ref([])
const selectedFriend = ref(null)
const messages = ref([])
const inputText = ref('')
const loadingHistory = ref(false)
const messageContainer = ref(null)
let ws = null
let tempMsgId = 0

// 好友列表缓存，用于通知时获取昵称
const friendCache = ref({})

const loadFriends = async () => {
  try {
    const res = await request.get('/user/friend/list')
    const list = Array.isArray(res) ? res : (Array.isArray(res.data) ? res.data : [])
    friends.value = list
    // 建立 uid -> friend 映射
    list.forEach(f => {
      friendCache.value[f.friendUid] = f
    })
  } catch (e) {
    ElMessage.error('加载好友列表失败')
  }
}

const selectFriend = async (friend) => {
  if (selectedFriend.value?.friendUid === friend.friendUid) return
  selectedFriend.value = friend
  messages.value = []
  await loadHistory()
  scrollToBottom()
}

const loadHistory = async () => {
  if (!selectedFriend.value) return
  loadingHistory.value = true
  try {
    const res = await request.get(`/user/friend/chat/history/${selectedFriend.value.friendUid}?limit=50`)
    const list = res.data !== undefined ? res.data : res
    messages.value = Array.isArray(list) ? list : []
    await nextTick()
    scrollToBottom()
  } catch (e) {
    ElMessage.error('加载历史消息失败')
  } finally {
    loadingHistory.value = false
  }
}

const connectWebSocket = () => {
  if (ws && ws.readyState === WebSocket.OPEN) return
  if (ws) ws.close()
  if (!token) return

  const protocol = location.protocol === 'https:' ? 'wss' : 'ws'
  ws = new WebSocket(`${protocol}://localhost:8080/ws/chat/${token}`)
  ws.onopen = () => console.log('✅ WebSocket 已连接')
  ws.onmessage = (event) => {
    try {
      const msg = JSON.parse(event.data)
      console.log('📩 收到消息:', msg)
      
      // 确定对方uid（可能是发送者或接收者）
      const otherUid = msg.senderUid === currentUid ? msg.receiverUid : msg.senderUid

      if (selectedFriend.value && selectedFriend.value.friendUid === otherUid) {
        // 是当前会话，添加到消息列表
        if (!messages.value.some(m => m.id === msg.id)) {
          // 移除可能存在的乐观更新临时消息
          const tempIndex = messages.value.findIndex(
            m => m.id < 0 && m.content === msg.content && m.senderUid === currentUid
          )
          if (tempIndex !== -1) messages.value.splice(tempIndex, 1)
          messages.value.push(msg)
          scrollToBottom()
        }
      } else {
        // 不是当前会话，发出通知
        const friendName = friendCache.value[otherUid]?.friendNickname || '好友'
        ElNotification({
          title: '新消息',
          message: `${friendName}: ${msg.content.substring(0, 20)}${msg.content.length > 20 ? '...' : ''}`,
          type: 'info',
          duration: 5000,
          onClick: () => {
            // 点击通知切换到该好友聊天
            const friend = friendCache.value[otherUid]
            if (friend) selectFriend(friend)
          }
        })
      }
    } catch (e) {
      console.error('消息解析失败', e)
    }
  }
  ws.onclose = () => console.log('⚠️ WebSocket 断开')
  ws.onerror = (err) => console.error('❌ WebSocket 错误', err)
}

const sendMessage = () => {
  if (!inputText.value.trim()) return
  if (!selectedFriend.value) {
    ElMessage.warning('请选择好友')
    return
  }
  if (!ws || ws.readyState !== WebSocket.OPEN) {
    connectWebSocket()
    ElMessage.warning('正在重新连接，请稍后再试')
    return
  }

  // 乐观更新
  const tempMsg = {
    id: --tempMsgId,
    senderUid: currentUid,
    receiverUid: selectedFriend.value.friendUid,
    content: inputText.value.trim(),
    createTime: new Date().toISOString()
  }
  messages.value.push(tempMsg)
  scrollToBottom()

  ws.send(JSON.stringify({
    receiverUid: selectedFriend.value.friendUid,
    content: inputText.value.trim()
  }))
  inputText.value = ''
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageContainer.value) {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight
    }
  })
}

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

onMounted(() => {
  loadFriends()
  connectWebSocket()
})

onBeforeUnmount(() => {
  if (ws) ws.close()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  height: calc(100vh - 60px);
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  background: #fff;
}

/* 左侧好友栏 */
.friend-panel {
  width: 260px;
  border-right: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
}
.panel-header {
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
}
.panel-header h3 { margin: 0; }
.friend-list { flex: 1; overflow-y: auto; }
.friend-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
}
.friend-item:hover { background: #f5f7fa; }
.friend-item.active { background: #ecf5ff; }
.friend-info { margin-left: 10px; }
.friend-name { font-weight: 500; }
.friend-status { font-size: 12px; color: #67c23a; }

/* 右侧聊天区域 */
.chat-window {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.chat-header {
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
  font-weight: 500;
  font-size: 16px;
}

/* 消息滚动区 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #fafafa;
}
.message-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16px;
}
.message-self {
  justify-content: flex-end;
}

.avatar {
  flex-shrink: 0;
}

/* 气泡 */
.bubble {
  max-width: 60%;
  padding: 8px 12px;
  border-radius: 8px;
  word-wrap: break-word;
}
.bubble.other {
  background: #fff;
  margin-left: 8px;
  border: 1px solid #ebeef5;
}
.bubble.self {
  background: #409eff;
  color: #fff;
  margin-right: 8px;
}
.bubble .content {
  margin-bottom: 4px;
}
.bubble .time {
  font-size: 11px;
  color: #c0c4cc;
  text-align: right;
}
.bubble.self .time {
  color: rgba(255,255,255,0.7);
}

/* 输入框 */
.chat-input {
  padding: 12px 16px;
  border-top: 1px solid #ebeef5;
}

/* 占位提示 */
.chat-placeholder {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-history {
  text-align: center;
  color: #999;
  margin-bottom: 10px;
}
</style>