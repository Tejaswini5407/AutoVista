import { Link } from 'react-router-dom'
import { useLanguage } from '../hooks/useLanguage.js'

function Footer() {
  const { t } = useLanguage()

  return (
    <footer className="site-footer">
      <div className="footer-top">
        <div className="footer-grid">
          {/* Brand & Address Column */}
          <div className="footer-col footer-brand-col">
            <div className="brand footer-brand">
              <div className="brand-logo-badge">
                <span className="brand-initials">AV</span>
              </div>
              <div className="brand-text-group">
                <span className="brand-name">AutoVista</span>
                <span className="brand-location">Lucknow Showroom</span>
              </div>
            </div>
            <p className="footer-tagline">{t('footerTagline')}</p>
            <address className="footer-address">
              📍 {t('footerAddress')}
            </address>
            <div className="footer-contact-info">
              <span>📞 +91 (0522) 490-8800</span>
              <span>✉️ concierge@autovista-lucknow.in</span>
              <span>⏰ Mon–Sun: 9:30 AM – 8:00 PM IST</span>
            </div>
          </div>

          {/* Quick Links Column */}
          <div className="footer-col">
            <h4 className="footer-heading">Inventory</h4>
            <ul className="footer-links">
              <li><Link to="/cars">New Arrivals 2026</Link></li>
              <li><Link to="/used-cars">Certified Pre-Owned</Link></li>
              <li><Link to="/book-test-drive">VIP Test Drive Booking</Link></li>
              <li><Link to="/cars/new-model-h-zx-2026">Featured Model H ZX</Link></li>
              <li><Link to="/used-cars/used-model-c-vx-2022">Certified Model C VX</Link></li>
            </ul>
          </div>

          {/* Dealership Services Column */}
          <div className="footer-col">
            <h4 className="footer-heading">Dealership Portals</h4>
            <ul className="footer-links">
              <li><Link to="/login">Client & Staff Sign In</Link></li>
              <li><Link to="/register">Create Buyer Account</Link></li>
              <li><Link to="/consultant/dashboard">Sales Consultant Desk</Link></li>
              <li><Link to="/admin/dashboard">Dealership Admin Centre</Link></li>
            </ul>
          </div>

          {/* Disclaimers & Compliance Column */}
          <div className="footer-col footer-compliance-col">
            <h4 className="footer-heading">Statutory & Consent</h4>
            <div className="compliance-box">
              <p className="disclaimer-text">
                <strong>Price Notice:</strong> {t('priceDisclaimer')}
              </p>
              <p className="consent-text">
                {t('consentNotice')}
              </p>
              <div className="opt-out-link-wrap">
                <button
                  type="button"
                  className="text-btn opt-out-btn"
                  onClick={() => alert('You have successfully opted out of marketing communications. Transactional notices regarding existing bookings remain active.')}
                >
                  Manage Communication Consent / Opt Out ↗
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="footer-bottom">
        <div className="footer-bottom-inner">
          <p>{t('footerCopyright')}</p>
          <div className="footer-badges">
            <span className="badge badge-subtle">Lucknow RTO Zone 32</span>
            <span className="badge badge-subtle">150-Point Quality Assured</span>
            <span className="badge badge-subtle">ISO 9001:2026 Certified Facility</span>
          </div>
        </div>
      </div>
    </footer>
  )
}

export default Footer