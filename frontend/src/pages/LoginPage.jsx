import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth.js'
import useDocumentTitle from '../hooks/useDocumentTitle.js'

function LoginPage() {
  useDocumentTitle('Sign In')
  const { login, loginAsDemo } = useAuth()
  const navigate = useNavigate()

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  const handleManualLogin = async (e) => {
    e.preventDefault()
    if (!email || !password) {
      setError('Please provide your email and password.')
      return
    }
    try {
      const res = await login(email, password)
      if (res.user.role === 'ADMIN') {
        navigate('/admin/dashboard')
      } else if (res.user.role === 'CONSULTANT') {
        navigate('/consultant/dashboard')
      } else {
        navigate('/')
      }
    } catch (err) {
      setError(err.message || 'Login failed')
    }
  }

  const handleDemoLogin = async (roleKey) => {
    try {
      const user = await loginAsDemo(roleKey)
      if (user.role === 'ADMIN') {
        navigate('/admin/dashboard')
      } else if (user.role === 'CONSULTANT') {
        navigate('/consultant/dashboard')
      } else {
        navigate('/')
      }
    } catch (err) {
      setError(err.message || 'Demo login failed')
    }
  }

  return (
    <div className="auth-page">
      <div className="container auth-container">
        <div className="auth-card">
          <div className="auth-header">
            <span className="eyebrow-badge">Dealership Portal</span>
            <h1 className="auth-title">Welcome Back</h1>
            <p className="auth-subtitle">
              Sign in to manage test drive bookings, review assigned leads, or configure inventory.
            </p>
          </div>

          {error && <div className="alert-box alert-danger">{error}</div>}

          {/* Quick Evaluator Role Switcher */}
          <div className="evaluator-box">
            <span className="evaluator-label">🚀 Instant Evaluator Quick Login:</span>
            <div className="evaluator-buttons">
              <button
                type="button"
                className="btn btn-outline btn-xs"
                onClick={() => handleDemoLogin('customer')}
              >
                👤 Customer
              </button>
              <button
                type="button"
                className="btn btn-secondary btn-xs"
                onClick={() => handleDemoLogin('consultant')}
              >
                💼 Sales Consultant
              </button>
              <button
                type="button"
                className="btn btn-accent btn-xs"
                onClick={() => handleDemoLogin('admin')}
              >
                👑 Dealership Admin
              </button>
            </div>
          </div>

          <form onSubmit={handleManualLogin} className="auth-form">
            <div className="form-group">
              <label htmlFor="loginEmail" className="form-label">
                Email Address
              </label>
              <input
                id="loginEmail"
                type="email"
                className="form-input"
                placeholder="consultant@autovista.in or user@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="loginPassword" className="form-label">
                Password
              </label>
              <input
                id="loginPassword"
                type="password"
                className="form-input"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <button type="submit" className="btn btn-primary btn-full btn-lg">
              Sign In to AutoVista
            </button>
          </form>

          <div className="auth-footer-text">
            <span>Don't have an account?</span>{' '}
            <Link to="/register" className="auth-link">
              Create customer account
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}

export default LoginPage