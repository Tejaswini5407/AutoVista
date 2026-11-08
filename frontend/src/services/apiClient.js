/**
 * AutoVista API Client
 * Configured for Spring Boot backend integration (Phase 2+).
 */

const getDefaultApiBaseUrl = () => {
  if (import.meta.env.VITE_API_BASE_URL) {
    return import.meta.env.VITE_API_BASE_URL
  }
  if (
    typeof window !== 'undefined' &&
    window.location.hostname &&
    window.location.hostname !== 'localhost' &&
    window.location.hostname !== '127.0.0.1'
  ) {
    return 'https://autovista-backend.onrender.com/api'
  }
  return 'http://localhost:8080/api'
}

const API_BASE_URL = getDefaultApiBaseUrl()

export async function apiRequest(endpoint, { method = 'GET', body, headers = {}, timeout = 60000 } = {}) {
  const token = localStorage.getItem('autovista_jwt_token')
  const defaultHeaders = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...headers,
  }

  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), timeout)

  const config = {
    method,
    headers: defaultHeaders,
    signal: controller.signal,
    ...(body ? { body: JSON.stringify(body) } : {}),
  }

  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, config)
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      const error = new Error(errorData.message || `Request failed with status ${response.status}`)
      error.status = response.status
      error.data = errorData
      throw error
    }
    return await response.json()
  } catch (error) {
    if (error.name === 'AbortError') {
      const timeoutError = new Error('Authentication request timed out. Please try again.')
      timeoutError.status = 408
      console.warn(`[AutoVista API] ${method} ${endpoint} timed out after ${timeout}ms`)
      throw timeoutError
    }
    console.warn(`[AutoVista API] ${method} ${endpoint} failed:`, error.message)
    throw error
  } finally {
    clearTimeout(timer)
  }
}

export default apiRequest
