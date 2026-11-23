import axios from 'axios'

// Le backend Spring Boot autorise déjà toutes les origines (CorsConfig).
const api = axios.create({ baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080' })

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})
api.interceptors.response.use((r) => r, (err) => {
  if (err.response?.status === 401 && !err.config.url.includes('/auth/')) { localStorage.clear(); window.location.href = '/' }
  return Promise.reject(err)
})

export const roleGroup = (r) => (r === 'STUDENT' ? 'student' : r === 'TEACHER' ? 'teacher' : 'admin')
export const homeFor = (r) => '/' + roleGroup(r)
export const logout = () => localStorage.clear()
export const getUser = () => { try { return JSON.parse(localStorage.getItem('user')) } catch { return null } }
// Le backend enveloppe presque tout dans { success, data, error }.
export const unwrap = (res) => res.data?.data ?? res.data
export const asList = (v) => (Array.isArray(v) ? v : [])

// POST /auth/login -> { success, data: { token, user: { id, name, email, role, studentId?, teacherId? } } }
export async function login(email, password) {
  const { data } = await api.post('/auth/login', { email, password })
  const { token, user } = data.data || {}
  if (!token || !user) throw new Error('Réponse inattendue du serveur : ' + JSON.stringify(data).slice(0, 160))
  localStorage.setItem('token', token)
  localStorage.setItem('role', user.role)
  localStorage.setItem('user', JSON.stringify(user))
  return { token, role: user.role, user }
}

// Le backend n’accepte que les lettres A, B, C, D, F pour la mention.
export const letterOf = (n) => (n >= 16 ? 'A' : n >= 14 ? 'B' : n >= 12 ? 'C' : n >= 10 ? 'D' : 'F')
const LABEL = { A: 'Très bien', B: 'Bien', C: 'Assez bien', D: 'Passable', F: 'Insuffisant' }
export const gradeText = (g) => (g ? (LABEL[g] ? `${g} – ${LABEL[g]}` : g) : '—')

export default api
