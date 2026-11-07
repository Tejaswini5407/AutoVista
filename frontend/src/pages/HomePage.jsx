import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { getAllNewCars, getAllUsedCars } from '../services/carData.js'
import { getNewCars, getUsedCars, normalizeCarModelForCard } from '../services/marketplaceService.js'
import CarCard from '../components/CarCard.jsx'
import { useLanguage } from '../hooks/useLanguage.js'
import useDocumentTitle from '../hooks/useDocumentTitle.js'

function HomePage() {
  const { t } = useLanguage()
  useDocumentTitle('Home')

  const [newCars, setNewCars] = useState(() => getAllNewCars().slice(0, 3))
  const [usedCars, setUsedCars] = useState(() => getAllUsedCars().slice(0, 3))

  useEffect(() => {
    getNewCars()
      .then((models) => {
        if (models && models.length > 0) {
          setNewCars(models.map(normalizeCarModelForCard).slice(0, 3))
        }
      })
      .catch((err) => console.warn('Home page new cars live fetch:', err.message))

    getUsedCars()
      .then((cars) => {
        if (cars && cars.length > 0) {
          setUsedCars(cars.slice(0, 3))
        }
      })
      .catch((err) => console.warn('Home page used cars live fetch:', err.message))
  }, [])

  const featuredCar = newCars[0] || {}

  return (
    <div className="home-page">
      {/* Hero Section */}
      <section className="home-hero">
        <div className="hero-backdrop" />
        <div className="container hero-container">
          <div className="hero-copy">
            <div className="eyebrow-badge">
              <span className="pulse-dot" />
              <span>{t('heroEyebrow')}</span>
            </div>
            <h1 className="hero-headline">
              {t('heroTitle')}
            </h1>
            <p className="hero-description">
              {t('heroSubtitle')}
            </p>
            <div className="hero-actions">
              <Link className="btn btn-primary btn-lg" to="/cars">
                {t('heroCtaPrimary')} <span className="arrow" aria-hidden="true">→</span>
              </Link>
              <Link className="btn btn-secondary btn-lg" to="/book-test-drive">
                {t('heroCtaSecondary')}
              </Link>
            </div>

            {/* Quick Metrics */}
            <div className="hero-metrics">
              <div className="metric-item">
                <span className="metric-number">100%</span>
                <span className="metric-label">Verified History</span>
              </div>
              <div className="metric-item">
                <span className="metric-number">₹0</span>
                <span className="metric-label">Hidden Surcharges</span>
              </div>
              <div className="metric-item">
                <span className="metric-number">&lt; 5m</span>
                <span className="metric-label">Consultant Response</span>
              </div>
            </div>
          </div>

          <div className="hero-visual">
            <div className="hero-image-frame">
              <img
                src={featuredCar.heroImage}
                alt="AutoVista Flagship Luxury Car"
                className="hero-img"
              />
              <div className="hero-car-overlay">
                <span className="overlay-eyebrow">Dealership Spotlight</span>
                <h3 className="overlay-title">{featuredCar.brand} {featuredCar.variant}</h3>
                <span className="overlay-price">₹12,45,000 ex-showroom</span>
                <Link to={`/cars/${featuredCar.id}`} className="overlay-link">
                  View Specifications ↗
                </Link>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Assurance / Trust Pillars */}
      <section className="section section-assurance">
        <div className="container">
          <div className="section-header text-center">
            <span className="section-eyebrow">{t('highlightTitle')}</span>
            <h2 className="section-title">Designed Around Integrity & Performance</h2>
            <p className="section-subtitle">
              Serving vehicle connoisseurs across Lucknow and Uttar Pradesh with complete price transparency and authenticated certifications.
            </p>
          </div>

          <div className="assurance-grid">
            <div className="assurance-card">
              <div className="assurance-icon">🛡️</div>
              <h3 className="assurance-title">{t('highlight1Title')}</h3>
              <p className="assurance-desc">{t('highlight1Desc')}</p>
              <span className="assurance-badge">Standard Across All Cars</span>
            </div>

            <div className="assurance-card">
              <div className="assurance-icon">📑</div>
              <h3 className="assurance-title">{t('highlight2Title')}</h3>
              <p className="assurance-desc">{t('highlight2Desc')}</p>
              <span className="assurance-badge">Lucknow RTO Certified Rates</span>
            </div>

            <div className="assurance-card">
              <div className="assurance-icon">⚡</div>
              <h3 className="assurance-title">{t('highlight3Title')}</h3>
              <p className="assurance-desc">{t('highlight3Desc')}</p>
              <span className="assurance-badge">Guaranteed Response SLA</span>
            </div>
          </div>
        </div>
      </section>

      {/* Featured New Cars Section */}
      <section className="section section-new-cars">
        <div className="container">
          <div className="section-header-row">
            <div>
              <span className="section-eyebrow">2026 Collection</span>
              <h2 className="section-title">{t('featuredNewCars')}</h2>
            </div>
            <Link to="/cars" className="btn btn-outline">
              View All New Vehicles ({getAllNewCars().length}) ↗
            </Link>
          </div>

          <div className="cars-grid">
            {newCars.map((car) => (
              <CarCard key={car.id} car={car} isUsed={false} />
            ))}
          </div>
        </div>
      </section>

      {/* Pre-Owned Certified Collection */}
      <section className="section section-used-cars">
        <div className="container">
          <div className="section-header-row">
            <div>
              <span className="section-eyebrow">Inspected & Verified</span>
              <h2 className="section-title">{t('featuredUsedCars')}</h2>
              <p className="section-note">
                Vehicle registration numbers are masked for security and owner privacy.
              </p>
            </div>
            <Link to="/used-cars" className="btn btn-outline">
              Browse Pre-Owned Collection ({getAllUsedCars().length}) ↗
            </Link>
          </div>

          <div className="cars-grid">
            {usedCars.map((car) => (
              <CarCard key={car.id} car={car} isUsed={true} />
            ))}
          </div>
        </div>
      </section>

      {/* Test Drive CTA Banner */}
      <section className="section-cta-banner">
        <div className="container">
          <div className="cta-banner-card">
            <div className="cta-content">
              <span className="eyebrow-badge">Experience The Drive</span>
              <h2>Ready to feel the difference behind the wheel?</h2>
              <p>
                Schedule an exclusive test drive from our Gomti Nagar facility or request door-step delivery at your convenience in Lucknow.
              </p>
              <div className="cta-actions">
                <Link to="/book-test-drive" className="btn btn-primary btn-lg">
                  Book Test Drive Slot Now
                </Link>
                <Link to="/cars" className="btn btn-secondary btn-lg">
                  Browse All Models
                </Link>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Price Validity Disclaimer Banner */}
      <div className="disclaimer-banner">
        <div className="container">
          <p className="disclaimer-banner-text">
            ℹ️ {t('priceDisclaimer')}
          </p>
        </div>
      </div>
    </div>
  )
}

export default HomePage