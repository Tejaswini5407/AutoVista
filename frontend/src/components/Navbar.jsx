import { useState } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useLanguage } from '../hooks/useLanguage.js'
import { useAuth } from '../hooks/useAuth.js'

function Navbar() {
  const { t, language, toggleLanguage } = useLanguage()
  const { user, isAuthenticated, logout } = useAuth()
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  const closeMenu = () => setMobileMenuOpen(false)

  return (
    <header className="site-header">
      <div className="header-inner">
        {/* Brand Logo */}
        <Link className="brand" to="/" onClick={closeMenu} aria-label="AutoVista Home">
          <div className="brand-logo-badge">
            <span className="brand-initials">AV</span>
          </div>
          <div className="brand-text-group">
            <span className="brand-name">AutoVista</span>
            <span className="brand-location">Lucknow</span>
          </div>
        </Link>

        {/* Desktop Primary Navigation */}
        <nav className="primary-nav" aria-label="Main navigation">
          <NavLink
            to="/cars"
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
          >
            {t('navNewCars')}
          </NavLink>
          <NavLink
            to="/used-cars"
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
          >
            {t('navUsedCars')}
          </NavLink>
          <NavLink
            to="/book-test-drive"
            className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
          >
            {t('navTestDrive')}
          </NavLink>
        </nav>

        {/* Right Header Utilities: Language Switcher & Auth */}
        <div className="header-utilities">
          {/* Language Toggle Button */}
          <button
            type="button"
            className="lang-toggle-btn"
            onClick={toggleLanguage}
            title={`Switch to ${language === 'en' ? 'Hindi' : 'English'}`}
            aria-label="Toggle language"
          >
            <span className={`lang-pill ${language === 'en' ? 'active' : ''}`}>EN</span>
            <span className="lang-divider">/</span>
            <span className={`lang-pill ${language === 'hi' ? 'active' : ''}`}>हिन्दी</span>
          </button>

          {/* User Auth Section */}
          {isAuthenticated ? (
            <div className="user-menu-group">
              {user.role === 'ADMIN' && (
                <Link to="/admin/dashboard" className="btn btn-outline btn-xs" onClick={closeMenu}>
                  Admin
                </Link>
              )}
              {user.role === 'CONSULTANT' && (
                <Link to="/consultant/dashboard" className="btn btn-outline btn-xs" onClick={closeMenu}>
                  Consultant
                </Link>
              )}
              <span className="user-greeting" title={user.email}>
                {user.name.split(' ')[0]}
              </span>
              <button
                type="button"
                className="btn btn-ghost btn-xs"
                onClick={handleLogout}
              >
                {t('navSignOut')}
              </button>
            </div>
          ) : (
            <Link to="/login" className="btn btn-outline btn-sm login-cta">
              {t('navSignIn')}
            </Link>
          )}

          {/* Mobile Menu Hamburger */}
          <button
            type="button"
            className="mobile-menu-trigger"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            aria-label="Toggle menu"
            aria-expanded={mobileMenuOpen}
          >
            <span className={`hamburger-bar ${mobileMenuOpen ? 'open' : ''}`} />
          </button>
        </div>
      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div className="mobile-drawer">
          <nav className="mobile-nav">
            <NavLink to="/cars" onClick={closeMenu} className="mobile-nav-link">
              {t('navNewCars')}
            </NavLink>
            <NavLink to="/used-cars" onClick={closeMenu} className="mobile-nav-link">
              {t('navUsedCars')}
            </NavLink>
            <NavLink to="/book-test-drive" onClick={closeMenu} className="mobile-nav-link">
              {t('navTestDrive')}
            </NavLink>
            {user?.role === 'ADMIN' && (
              <NavLink to="/admin/dashboard" onClick={closeMenu} className="mobile-nav-link">
                Admin Dashboard
              </NavLink>
            )}
            {user?.role === 'CONSULTANT' && (
              <NavLink to="/consultant/dashboard" onClick={closeMenu} className="mobile-nav-link">
                Consultant Dashboard
              </NavLink>
            )}
            <div className="mobile-drawer-footer">
              {isAuthenticated ? (
                <button
                  type="button"
                  className="btn btn-secondary btn-full"
                  onClick={() => {
                    handleLogout()
                    closeMenu()
                  }}
                >
                  {t('navSignOut')} ({user.name})
                </button>
              ) : (
                <Link
                  to="/login"
                  className="btn btn-primary btn-full"
                  onClick={closeMenu}
                >
                  {t('navSignIn')}
                </Link>
              )}
            </div>
          </nav>
        </div>
      )}
    </header>
  )
}

export default Navbar