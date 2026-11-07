/**
 * AutoVista API Client
 * Configured for Spring Boot backend integration (Phase 2+).
 */

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

export async function apiRequest(endpoint, { method = 'GET', body, headers = {} } = {}) {
  const token = localStorage.getItem('autovista_jwt_token')
  const defaultHeaders = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...headers,
  }

  const config = {
    method,
    headers: defaultHeaders,
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
    console.warn(`[AutoVista API] ${method} ${endpoint} failed:`, error.message)
    throw error
  }
}

export default apiRequest
