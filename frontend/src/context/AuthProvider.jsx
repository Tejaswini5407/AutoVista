import { useState, useEffect } from 'react'
import AuthContext from './AuthContext.js'

import apiRequest from '../services/apiClient.js'

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('autovista_user')
    return saved ? JSON.parse(saved) : null
  })

  useEffect(() => {
    if (user) {
      localStorage.setItem('autovista_user', JSON.stringify(user))
    } else {
      localStorage.removeItem('autovista_user')
      localStorage.removeItem('autovista_jwt_token')
    }
  }, [user])

  const loginAsDemo = async (roleKey) => {
    let email = 'admin@autovista.in'
    if (roleKey === 'consultant') email = 'vikram.consultant@autovista.in'
    if (roleKey === 'customer') email = 'aarav.sharma@example.com'

    const res = await apiRequest('/auth/login', {
      method: 'POST',
      body: { email, password: 'Password@123' },
    })
    const authData = res.data
    const userPayload = authData?.user || authData
    if (authData?.token) {
      localStorage.setItem('autovista_jwt_token', authData.token)
    }
    const loggedUser = {
      id: userPayload.id,
      name: userPayload.fullName || userPayload.name || userPayload.email?.split('@')[0] || 'User',
      fullName: userPayload.fullName || userPayload.name || 'User',
      email: userPayload.email,
      role: userPayload.role,
      phone: userPayload.phone,
      city: userPayload.city,
    }
    setUser(loggedUser)
    return loggedUser
  }

  const login = async (email, password = 'Password@123') => {
    const res = await apiRequest('/auth/login', {
      method: 'POST',
      body: { email, password },
    })
    const authData = res.data
    const userPayload = authData?.user || authData
    if (authData?.token) {
      localStorage.setItem('autovista_jwt_token', authData.token)
    }
    const loggedUser = {
      id: userPayload.id,
      name: userPayload.fullName || userPayload.name || userPayload.email?.split('@')[0] || 'User',
      fullName: userPayload.fullName || userPayload.name || 'User',
      email: userPayload.email,
      role: userPayload.role,
      phone: userPayload.phone,
      city: userPayload.city,
    }
    setUser(loggedUser)
    return { success: true, user: loggedUser }
  }

  const register = async ({ email, password, fullName, phone, city = 'Lucknow', consentGiven = true }) => {
    const res = await apiRequest('/auth/register', {
      method: 'POST',
      body: { email, password, fullName, phone, city, consentGiven },
    })
    const authData = res.data
    const userPayload = authData?.user || authData
    if (authData?.token) {
      localStorage.setItem('autovista_jwt_token', authData.token)
    }
    const loggedUser = {
      id: userPayload.id,
      name: userPayload.fullName || userPayload.name || userPayload.email?.split('@')[0] || 'User',
      fullName: userPayload.fullName || userPayload.name || 'User',
      email: userPayload.email,
      role: userPayload.role || 'CUSTOMER',
      phone: userPayload.phone,
      city: userPayload.city,
    }
    setUser(loggedUser)
    return { success: true, user: loggedUser }
  }

  const logout = () => {
    localStorage.removeItem('autovista_jwt_token')
    localStorage.removeItem('autovista_user')
    setUser(null)
  }

  const isAuthenticated = Boolean(user)
  const isConsultant = user?.role === 'CONSULTANT'
  const isAdmin = user?.role === 'ADMIN'

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated,
        isAdmin,
        isConsultant,
        login,
        loginAsDemo,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export default AuthProvider
