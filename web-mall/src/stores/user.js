import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('userToken') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  function setToken(t) {
    token.value = t
    localStorage.setItem('userToken', t)
  }

  function setInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('userToken')
    localStorage.removeItem('userInfo')
  }

  return { userInfo, token, setToken, setInfo, logout }
})
