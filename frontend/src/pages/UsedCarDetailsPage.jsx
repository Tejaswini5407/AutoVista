import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { getUsedCarById } from '../services/marketplaceService.js'
import { formatINR, formatMileage, maskRegistrationNumber } from '../utils/formatters.js'
import Badge from '../components/Badge.jsx'
import EnquiryModal from '../components/EnquiryModal.jsx'
import useDocumentTitle from '../hooks/useDocumentTitle.js'
import { useLanguage } from '../hooks/useLanguage.js'

function UsedCarDetailsPage() {
  const { id } = useParams()
  const { t } = useLanguage()

  const [car, setCar] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [reloadKey, setReloadKey] = useState(0)
  const [showEnquiryModal, setShowEnquiryModal] = useState(false)

  useEffect(() => {
    let ignore = false
    getUsedCarById(id)
      .then((data) => {
        if (!ignore) {
          setCar(data)
          setError(null)
        }
      })
      .catch((err) => {
        if (!ignore) {
          console.error('Failed to load used car details:', err)
          setError(err.message || 'Unable to retrieve vehicle certification details.')
        }
      })
      .finally(() => {
        if (!ignore) {
          setLoading(false)
        }
      })

    return () => {
      ignore = true
    }
  }, [id, reloadKey])

  const title = car
    ? `Pre-Owned ${car.brand} ${car.variantName || car.variant || car.modelName}`
    : 'Pre-Owned Car Details'
  useDocumentTitle(title)

  const handleRetry = () => {
    setLoading(true)
    setReloadKey((prev) => prev + 1)
  }

  // Loading State
  if (loading) {
    return (
      <div className="container py-20 text-center">
        <div className="loading-spinner mb-4" />
        <h2 className="text-xl font-semibold text-slate-200">
          Retrieving 150-Point Certified History...
        </h2>
        <p className="text-slate-400 text-sm mt-1">Connecting to AutoVista PostgreSQL database</p>
      </div>
    )
  }

  // Error State with Retry
  if (error) {
    return (
      <div className="container py-16 text-center">
        <div className="empty-state-card max-w-lg mx-auto">
          <div className="text-4xl mb-3">⚠️</div>
          <h2 className="text-2xl font-bold text-red-400">Failed to Load Vehicle</h2>
          <p className="text-slate-300 mt-2">{error}</p>
          <div className="flex gap-4 justify-center mt-6">
            <button type="button" className="btn btn-primary" onClick={handleRetry}>
              Retry Connection
            </button>
            <Link to="/used-cars" className="btn btn-outline">
              Back to Pre-Owned
            </Link>
          </div>
        </div>
      </div>
    )
  }

  // Not Found State
  if (!car) {
    return (
      <div className="container py-16 text-center">
        <div className="empty-state-card max-w-lg mx-auto">
          <div className="text-4xl mb-3">🚗</div>
          <h2 className="text-2xl font-bold text-slate-100">Pre-Owned Vehicle Not Found</h2>
          <p className="text-slate-300 mt-2">
            This pre-owned listing (ID: {id}) may have been acquired by another client or moved.
          </p>
          <Link to="/used-cars" className="btn btn-primary mt-6">
            View All Pre-Owned Vehicles
          </Link>
        </div>
      </div>
    )
  }

  const isBooked = car.status?.toUpperCase() === 'BOOKED'
  const heroImage = car.heroImageUrl || car.heroImage
  const displayBrand = car.brand || 'AutoVista'
  const displayTitle = car.variantName || car.variant || car.modelName
  const year = car.manufacturingYear || car.year || 2022
  const features = car.features || []

  // Ensure registration is ALWAYS masked
  const maskedReg =
    car.maskedRegistrationNumber || maskRegistrationNumber(car.rawRegistrationNumber)

  return (
    <div className="car-details-page">
      <div className="container">
        {/* Breadcrumb Navigation */}
        <nav className="breadcrumb-nav" aria-label="Breadcrumb">
          <Link to="/">Home</Link>
          <span className="breadcrumb-separator">/</span>
          <Link to="/used-cars">Pre-Owned Cars</Link>
          <span className="breadcrumb-separator">/</span>
          <span className="breadcrumb-current">
            {displayBrand} {displayTitle} ({year})
          </span>
        </nav>

        <div className="details-layout">
          {/* Left Column: Media & Inspection Proof */}
          <div className="details-media-col">
            <div className="details-hero-frame">
              <img
                src={heroImage}
                alt={`${displayBrand} ${displayTitle}`}
                className="details-main-img"
              />
              <div className="media-overlay-badges">
                <Badge variant="secondary">Pre-Owned • {year}</Badge>
                <Badge variant={isBooked ? 'danger' : 'success'}>
                  {isBooked ? t('statusBooked') : t('statusAvailable')}
                </Badge>
              </div>
            </div>

            {/* 150-Point Inspection Certificate Card */}
            <div className="card-box mt-6">
              <div className="inspection-header">
                <div className="inspection-badge">🛡️ Certified Quality</div>
                <span className="inspection-status-text">
                  {car.inspectionPointsPassed || 150}-Point Diagnostic Check Passed
                </span>
              </div>
              <p className="inspection-desc">
                Our master technicians completed comprehensive testing across powertrain, braking systems,
                frame geometry, electronics, and interior cosmetics at our Lucknow facility.
              </p>
              <div className="inspection-metrics-row">
                <div className="metric-chip">
                  <span className="metric-val">100%</span>
                  <span className="metric-lbl">Odometer Verified</span>
                </div>
                <div className="metric-chip">
                  <span className="metric-val">0</span>
                  <span className="metric-lbl">Major Collisions</span>
                </div>
                <div className="metric-chip">
                  <span className="metric-val">1-Yr</span>
                  <span className="metric-lbl">Warranty Included</span>
                </div>
              </div>
            </div>

            {/* Vehicle Features */}
            {features.length > 0 && (
              <div className="card-box mt-6">
                <h3 className="box-title">Equipment & Condition Highlights</h3>
                <ul className="features-checklist">
                  {features.map((feature, i) => (
                    <li key={i} className="feature-check-item">
                      <span className="check-icon">✓</span>
                      <span>{feature}</span>
                    </li>
                  ))}
                </ul>
              </div>
            )}
          </div>

          {/* Right Column: Pricing & Verification Data */}
          <div className="details-info-col">
            <div className="condition-pill-row">
              <span className="car-brand-tag">AutoVista Verified Pre-Owned</span>
              {isBooked && (
                <span className="booked-alert-pill">
                  ⚠️ This vehicle is currently reserved under client deposit.
                </span>
              )}
            </div>

            <h1 className="details-title">{displayTitle}</h1>
            <p className="details-tagline">{car.tagline}</p>

            {/* Price Card */}
            <div className="pricing-breakdown-card">
              <div className="breakdown-header">
                <span className="caption">All-Inclusive Dealership Price</span>
                <span className="total-price">{formatINR(car.price)}</span>
              </div>
              <p className="price-subnote">
                Includes transfer of ownership documentation, 150-point refurbishment, and 12-month
                roadside assistance in Uttar Pradesh.
              </p>
            </div>

            {/* History & Registration Table */}
            <div className="specs-table-card">
              <h3 className="box-title">Vehicle Provenance & Specs</h3>
              <div className="specs-key-grid">
                <div className="spec-item">
                  <span className="k">Registration No.</span>
                  <span className="v font-mono text-accent">{maskedReg}</span>
                </div>
                <div className="spec-item">
                  <span className="k">Odometer</span>
                  <span className="v">{formatMileage(car.odometerKm)}</span>
                </div>
                <div className="spec-item">
                  <span className="k">Previous Owners</span>
                  <span className="v">{car.ownersCount || 1} Owner (Individual)</span>
                </div>
                <div className="spec-item">
                  <span className="k">Insurance Status</span>
                  <span className="v">{car.insuranceValidity || 'Comprehensive'}</span>
                </div>
                <div className="spec-item">
                  <span className="k">Fuel Type</span>
                  <span className="v">{car.fuelType}</span>
                </div>
                <div className="spec-item">
                  <span className="k">Transmission</span>
                  <span className="v">{car.transmission}</span>
                </div>
              </div>
            </div>

            {/* Actions Bar */}
            <div className="details-actions-bar flex flex-wrap gap-3">
              {isBooked ? (
                <button type="button" className="btn btn-secondary btn-lg flex-1" disabled>
                  Vehicle Reserved Under Deposit
                </button>
              ) : (
                <Link
                  to={`/book-test-drive?carId=${car.id}&type=used`}
                  className="btn btn-primary btn-lg flex-1"
                >
                  Book Showroom Test Drive ↗
                </Link>
              )}
              <button
                type="button"
                className="btn btn-secondary btn-lg flex-1"
                onClick={() => setShowEnquiryModal(true)}
              >
                💬 Enquire with Concierge
              </button>
              <Link to="/used-cars" className="btn btn-outline btn-lg">
                Back to Pre-Owned
              </Link>
            </div>

            <div className="consent-subnote">
              {t('consentNotice')}
            </div>
          </div>
        </div>
      </div>

      <EnquiryModal
        isOpen={showEnquiryModal}
        onClose={() => setShowEnquiryModal(false)}
        vehicle={{
          id: car.id,
          displayName: displayTitle,
          modelName: displayTitle,
          fuelType: car.fuelType,
          transmission: car.transmission,
          showroomId: car.showroomId || 1,
        }}
        vehicleType="USED"
      />
    </div>
  )
}

export default UsedCarDetailsPage
