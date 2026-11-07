import { useState, useEffect } from 'react'
import AuthContext from './AuthContext.js'

import apiRequest from '../services/apiClient.js'

const DEMO_USERS = {
  customer: {
    id: 'user-101',
    name: 'Aarav Sharma',
    email: 'aarav.sharma@example.com',
    role: 'CUSTOMER',
    city: 'Lucknow',
  },
  consultant: {
    id: 'consultant-201',
    name: 'Vikram Singh',
    email: 'vikram.consultant@autovista.in',
    role: 'CONSULTANT',
    branch: 'Lucknow Flagship',
  },
  admin: {
    id: 'admin-301',
    name: 'Pooja Verma',
    email: 'admin@autovista.in',
    role: 'ADMIN',
    dealership: 'AutoVista Lucknow',
  },
}

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

    try {
      const res = await apiRequest('/auth/login', {
        method: 'POST',
        body: { email, password: 'Password@123' },
      })
      const authData = res.data
      localStorage.setItem('autovista_jwt_token', authData.token)
      const loggedUser = {
        id: authData.id,
        name: authData.name,
        email: authData.email,
        role: authData.role,
        phone: authData.phone,
      }
      setUser(loggedUser)
      return loggedUser
    } catch (err) {
      console.warn('Backend login fallback to mock:', err.message)
      const targetUser = DEMO_USERS[roleKey]
      setUser(targetUser)
      return targetUser
    }
  }

  const login = async (email, password = 'Password@123') => {
    try {
      const res = await apiRequest('/auth/login', {
        method: 'POST',
        body: { email, password },
      })
      const authData = res.data
      localStorage.setItem('autovista_jwt_token', authData.token)
      const loggedUser = {
        id: authData.id,
        name: authData.name,
        email: authData.email,
        role: authData.role,
        phone: authData.phone,
      }
      setUser(loggedUser)
      return { success: true, user: loggedUser }
    } catch (err) {
      console.warn('Backend login fallback to mock:', err.message)
      // Fallback for Phase 1 demo login
      if (email.includes('admin')) {
        const u = DEMO_USERS.admin
        setUser(u)
        return { success: true, user: u }
      }
      if (email.includes('consultant')) {
        const u = DEMO_USERS.consultant
        setUser(u)
        return { success: true, user: u }
      }
      const u = {
        id: 'user-' + Date.now(),
        name: email.split('@')[0],
        email,
        role: 'CUSTOMER',
        city: 'Lucknow',
      }
      setUser(u)
      return { success: true, user: u }
    }
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
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export default AuthProvider
