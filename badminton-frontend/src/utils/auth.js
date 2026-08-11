// 判断用户是否具有某角色
export function hasRole(role) {
  const userStore = useUserStore()
  return userStore.role === role
}

// 获取当前用户角色
export function getRole() {
  const userStore = useUserStore()
  return userStore.role
}