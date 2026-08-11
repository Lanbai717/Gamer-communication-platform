export function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getMonth()+1}/${d.getDate()} ${d.getHours()}:${d.getMinutes()}`
}