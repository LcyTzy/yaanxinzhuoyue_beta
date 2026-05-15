<template>
  <div class="chat-widget">
    <div class="chat-toggle" @click="toggleChat">
      <el-icon :size="24"><ChatDotRound /></el-icon>
      <span>在线客服</span>
      <el-badge v-if="unread > 0 && !isOpen" :value="unread" class="chat-badge" />
    </div>

    <div v-if="isOpen" class="chat-window">
      <div class="chat-header">
        <span>在线客服</span>
        <el-button link @click="isOpen = false"><el-icon><Close /></el-icon></el-button>
      </div>
      <div class="chat-messages" ref="msgContainer">
        <div v-for="(msg, idx) in messages" :key="idx" :class="['message', msg.from === 'me' ? 'message-me' : 'message-other']">
          <div class="message-content">{{ msg.content }}</div>
          <div class="message-time">{{ msg.time }}</div>
        </div>
        <div v-if="messages.length === 0" class="chat-empty">
          <p>您好，有什么可以帮您的？</p>
        </div>
      </div>
      <div class="chat-input">
        <el-input v-model="inputText" placeholder="输入消息..." @keyup.enter="sendMessage">
          <template #append>
            <el-button @click="sendMessage">发送</el-button>
          </template>
        </el-input>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isOpen = ref(false)
const inputText = ref('')
const messages = ref([])
const unread = ref(0)
const msgContainer = ref(null)
let ws = null

const toggleChat = () => {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    unread.value = 0
    nextTick(() => scrollToBottom())
  }
}

const connect = () => {
  const userId = userStore.userInfo?.id || 'guest'
  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  const host = window.location.host
  ws = new WebSocket(protocol + '//' + host + '/ws/chat/' + userId)

  ws.onmessage = (event) => {
    try {
      const msg = JSON.parse(event.data)
      messages.value.push({
        from: 'other',
        content: msg.content,
        time: new Date().toLocaleTimeString().slice(0, 5)
      })
      if (!isOpen.value) unread.value++
      nextTick(() => scrollToBottom())
    } catch (e) {
      // ignore
    }
  }

  ws.onclose = () => {
    setTimeout(connect, 3000)
  }
}

const sendMessage = () => {
  if (!inputText.value.trim() || !ws || ws.readyState !== WebSocket.OPEN) return
  const msg = { to: 'admin', content: inputText.value.trim() }
  ws.send(JSON.stringify(msg))
  messages.value.push({
    from: 'me',
    content: inputText.value.trim(),
    time: new Date().toLocaleTimeString().slice(0, 5)
  })
  inputText.value = ''
  nextTick(() => scrollToBottom())
}

const scrollToBottom = () => {
  if (msgContainer.value) {
    msgContainer.value.scrollTop = msgContainer.value.scrollHeight
  }
}

onMounted(() => {
  if (userStore.token) connect()
})

onUnmounted(() => {
  if (ws) ws.close()
})
</script>

<style scoped>
.chat-widget {
  position: fixed;
  bottom: 80px;
  right: 20px;
  z-index: 1000;
}

.chat-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #409EFF;
  color: #fff;
  padding: 10px 16px;
  border-radius: 24px;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.4);
  font-size: 14px;
}

.chat-badge {
  position: absolute;
  top: -5px;
  right: -5px;
}

.chat-window {
  position: absolute;
  bottom: 50px;
  right: 0;
  width: 360px;
  height: 480px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #409EFF;
  color: #fff;
  font-size: 15px;
}

.chat-messages {
  flex: 1;
  padding: 12px;
  overflow-y: auto;
  background: #f5f7fa;
}

.message {
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
}

.message-me {
  align-items: flex-end;
}

.message-other {
  align-items: flex-start;
}

.message-content {
  max-width: 75%;
  padding: 8px 12px;
  border-radius: 12px;
  font-size: 14px;
  word-break: break-word;
}

.message-me .message-content {
  background: #409EFF;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message-other .message-content {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-bottom-left-radius: 4px;
}

.message-time {
  font-size: 11px;
  color: #999;
  margin-top: 2px;
}

.chat-empty {
  text-align: center;
  color: #999;
  padding-top: 60px;
}

.chat-input {
  padding: 10px;
  border-top: 1px solid #ebeef5;
}
</style>