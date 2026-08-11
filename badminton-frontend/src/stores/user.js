import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo')) || null)
  const role = ref(localStorage.getItem('role') || '') // 'user', 'merchant', 'admin'

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  function setRole(r) {
    role.value = r
    localStorage.setItem('role', r)
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    role.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('role')
  }

  const isLoggedIn = () => !!token.value

  return { token, userInfo, role, setToken, setUserInfo, setRole, logout, isLoggedIn }
})