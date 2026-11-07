import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth.js'
import useDocumentTitle from '../hooks/useDocumentTitle.js'

function RegisterPage() {
  useDocumentTitle('Create Account')
  const { login } = useAuth()
  const navigate = useNavigate()

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    city: 'Lucknow',
    password: '',
    consent: true,
  })

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!formData.consent) {
      alert('Consent is required to create an account.')
      return
    }
    login(formData.email, formData.password)
    navigate('/')
  }

  return (
    <div className="auth-page">
      <div className="container auth-container">
        <div className="auth-card">
          <div className="auth-header">
            <span className="eyebrow-badge">Join AutoVista</span>
            <h1 className="auth-title">Create an Account</h1>
            <p className="auth-subtitle">
              Save your vehicle shortlist, schedule test drives, and receive transparent on-road price quotes for Lucknow.
            </p>
          </div>

          <form onSubmit={handleSubmit} className="auth-form">
            <div className="form-group">
              <label htmlFor="regName" className="form-label">
                Full Name
              </label>
              <input
                id="regName"
                name="name"
                type="text"
                className="form-input"
                placeholder="e.g., Ananya Trivedi"
                value={formData.name}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-row">
              <div className="form-group flex-1">
                <label htmlFor="regEmail" className="form-label">
                  Email Address
                </label>
                <input
                  id="regEmail"
                  name="email"
                  type="email"
                  className="form-input"
                  placeholder="name@example.com"
                  value={formData.email}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group flex-1">
                <label htmlFor="regPhone" className="form-label">
                  Mobile Number
                </label>
                <input
                  id="regPhone"
                  name="phone"
                  type="tel"
                  className="form-input"
                  placeholder="+91 98765 00000"
                  value={formData.phone}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="regCity" className="form-label">
                City / Region
              </label>
              <select
                id="regCity"
                name="city"
                className="form-select"
                value={formData.city}
                onChange={handleChange}
              >
                <option value="Lucknow">Lucknow (Showroom Region)</option>
                <option value="Kanpur">Kanpur</option>
                <option value="Varanasi">Varanasi</option>
                <option value="Prayagraj">Prayagraj</option>
                <option value="Other">Other Uttar Pradesh</option>
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="regPassword" className="form-label">
                Create Password
              </label>
              <input
                id="regPassword"
                name="password"
                type="password"
                className="form-input"
                placeholder="Minimum 8 characters"
                value={formData.password}
                onChange={handleChange}
                required
                minLength={6}
              />
            </div>

            {/* Consent Checkbox (Mandatory Requirement #8) */}
            <div className="form-group consent-checkbox-group">
              <label className="checkbox-label">
                <input
                  type="checkbox"
                  name="consent"
                  checked={formData.consent}
                  onChange={handleChange}
                  required
                />
                <span>
                  I give consent to AutoVista to store my contact details for vehicle enquiries, price alerts, and service communications. I can opt out at any time.
                </span>
              </label>
            </div>

            <button type="submit" className="btn btn-primary btn-full btn-lg">
              Complete Registration
            </button>
          </form>

          <div className="auth-footer-text">
            <span>Already have an account?</span>{' '}
            <Link to="/login" className="auth-link">
              Sign in
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}

export default RegisterPage