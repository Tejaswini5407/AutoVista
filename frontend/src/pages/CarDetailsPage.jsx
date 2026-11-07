import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { getNewCarById, getVariantPricing } from '../services/marketplaceService.js'
import { formatINR, formatDate } from '../utils/formatters.js'
import Badge from '../components/Badge.jsx'
import EnquiryModal from '../components/EnquiryModal.jsx'
import useDocumentTitle from '../hooks/useDocumentTitle.js'
import { useLanguage } from '../hooks/useLanguage.js'

function CarDetailsPage() {
  const { id } = useParams()
  const { t } = useLanguage()

  const [model, setModel] = useState(null)
  const [selectedVariantId, setSelectedVariantId] = useState(null)
  const [pricing, setPricing] = useState(null)
  const [pricingLoading, setPricingLoading] = useState(false)
  const [pricingError, setPricingError] = useState(null)
  const [selectedColor, setSelectedColor] = useState('')
  const [selectedImg, setSelectedImg] = useState('')
  const [showEnquiryModal, setShowEnquiryModal] = useState(false)

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [reloadKey, setReloadKey] = useState(0)

  // Fetch Car Model
  useEffect(() => {
    let ignore = false
    getNewCarById(id)
      .then((data) => {
        if (!ignore) {
          setModel(data)
          if (data?.variants && data.variants.length > 0) {
            const firstVariant = data.variants[0]
            setSelectedVariantId(firstVariant.id)
            if (firstVariant.colors && firstVariant.colors.length > 0) {
              setSelectedColor(firstVariant.colors[0])
            }
          }
          setSelectedImg(data?.heroImageUrl || '')
          setError(null)
        }
      })
      .catch((err) => {
        if (!ignore) {
          console.error('Failed to fetch car model details:', err)
          setError(err.message || 'Vehicle details could not be retrieved.')
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

  // Active Variant
  const activeVariant = model?.variants?.find((v) => v.id === selectedVariantId) || model?.variants?.[0]

  // Fetch Pricing for selected variant in Lucknow
  useEffect(() => {
    if (!activeVariant?.id) return
    let ignore = false

    getVariantPricing(activeVariant.id, 'Lucknow')
      .then((pricingData) => {
        if (!ignore) {
          setPricing(pricingData)
          setPricingError(null)
        }
      })
      .catch((err) => {
        if (!ignore) {
          console.warn(`Pricing not found for variant ${activeVariant.id}:`, err.message)
          setPricing(null)
          setPricingError(err.message || 'On-road pricing is currently being updated for this variant.')
        }
      })
      .finally(() => {
        if (!ignore) {
          setPricingLoading(false)
        }
      })

    return () => {
      ignore = true
    }
  }, [activeVariant?.id])

  useDocumentTitle(
    model ? `${model.brand} ${activeVariant ? activeVariant.variantName : model.modelName}` : 'Car Details'
  )

  const handleRetry = () => {
    setLoading(true)
    setReloadKey((prev) => prev + 1)
  }

  // Loading State
  if (loading) {
    return (
      <div className="container py-20 text-center">
        <div className="loading-spinner mb-4" />
        <h2 className="text-xl font-semibold text-slate-200">Retrieving Vehicle Specifications...</h2>
        <p className="text-slate-400 text-sm mt-1">Connecting to AutoVista PostgreSQL database</p>
      </div>
    )
  }

  // Error State (Network or server failure)
  if (error) {
    return (
      <div className="container py-16 text-center">
        <div className="empty-state-card max-w-lg mx-auto">
          <div className="text-4xl mb-3">⚠️</div>
          <h2 className="text-2xl font-bold text-red-400">Failed to Load Vehicle</h2>
          <p className="text-slate-300 mt-2">{error}</p>
          <div className="flex gap-4 justify-center mt-6">
            <button type="button" className="btn btn-primary" onClick={handleRetry}>
              Retry
            </button>
            <Link to="/cars" className="btn btn-outline">
              Back to Showroom
            </Link>
          </div>
        </div>
      </div>
    )
  }

  // Not Found State
  if (!model) {
    return (
      <div className="container py-16 text-center">
        <div className="empty-state-card max-w-lg mx-auto">
          <div className="text-4xl mb-3">🚗</div>
          <h2 className="text-2xl font-bold text-slate-100">Vehicle Not Found</h2>
          <p className="text-slate-300 mt-2">
            The vehicle model you requested (ID: {id}) is not available in our current catalog.
          </p>
          <Link to="/cars" className="btn btn-primary mt-6">
            Return to New Cars Collection
          </Link>
        </div>
      </div>
    )
  }

  const heroImage = selectedImg || activeVariant?.heroImageUrl || model.heroImageUrl
  const brand = model.brand || 'AutoVista'
  const modelName = model.modelName || ''
  const variantTitle = activeVariant ? activeVariant.variantName : modelName
  const features = activeVariant?.features || []
  const colors = activeVariant?.colors || []

  return (
    <div className="car-details-page">
      <div className="container">
        {/* Breadcrumb Navigation */}
        <nav className="breadcrumb-nav" aria-label="Breadcrumb">
          <Link to="/">Home</Link>
          <span className="breadcrumb-separator">/</span>
          <Link to="/cars">New Cars</Link>
          <span className="breadcrumb-separator">/</span>
          <span className="breadcrumb-current">{brand} {variantTitle}</span>
        </nav>

        <div className="details-layout">
          {/* Left Column: Media & Highlights */}
          <div className="details-media-col">
            <div className="details-hero-frame">
              <img
                src={heroImage}
                alt={`${brand} ${variantTitle}`}
                className="details-main-img"
              />
              <div className="media-overlay-badges">
                <Badge variant="accent">{model.year} Model</Badge>
                <Badge variant="success">Available</Badge>
              </div>
            </div>

            {/* Model Description */}
            {model.description && (
              <div className="card-box mt-6">
                <h3 className="box-title">About {brand} {modelName}</h3>
                <p className="text-slate-300 leading-relaxed text-sm">
                  {model.description}
                </p>
              </div>
            )}

            {/* Features Highlight */}
            {features.length > 0 && (
              <div className="card-box mt-6">
                <h3 className="box-title">Key Equipment & Technology</h3>
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

          {/* Right Column: Specifications, Real Backend Pricing & CTAs */}
          <div className="details-info-col">
            <span className="car-brand-tag">{brand} Flagship</span>
            <h1 className="details-title">{variantTitle}</h1>
            <p className="details-tagline">{activeVariant?.tagline || model.description}</p>

            {/* Variant Switcher (if multiple variants exist) */}
            {model.variants && model.variants.length > 1 && (
              <div className="variant-selection-wrap mt-4 mb-6">
                <label className="section-label">Select Variant:</label>
                <div className="variant-chips-row flex gap-2 flex-wrap">
                  {model.variants.map((v) => (
                    <button
                      key={v.id}
                      type="button"
                      className={`filter-pill ${v.id === selectedVariantId ? 'active' : ''}`}
                      onClick={() => {
                        setSelectedVariantId(v.id)
                        setPricingLoading(true)
                        if (v.colors && v.colors.length > 0) setSelectedColor(v.colors[0])
                        if (v.heroImageUrl) setSelectedImg(v.heroImageUrl)
                      }}
                    >
                      {v.variantName} ({formatINR(v.exShowroomPrice)})
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Real Backend-Calculated Pricing Summary Box */}
            <div className="pricing-breakdown-card">
              <div className="breakdown-header">
                <span className="caption">
                  {pricing?.city ? `${pricing.city} On-Road Pricing` : 'Estimated Lucknow On-Road Pricing'}
                </span>
                <span className="total-price">
                  {pricingLoading
                    ? 'Calculating...'
                    : pricing
                    ? formatINR(pricing.totalOnRoadPrice)
                    : activeVariant?.exShowroomPrice
                    ? formatINR(activeVariant.exShowroomPrice)
                    : 'N/A'}
                </span>
              </div>

              {pricingLoading && (
                <div className="py-6 text-center text-slate-400 text-sm">
                  Fetching official RTO and tax rates from database...
                </div>
              )}

              {!pricingLoading && pricing && (
                <div className="breakdown-table">
                  <div className="breakdown-row">
                    <span>Ex-Showroom Price</span>
                    <span className="font-mono">{formatINR(pricing.exShowroomPrice)}</span>
                  </div>

                  <div className="breakdown-row">
                    <span>
                      Road Tax ({pricing.rtoTaxPercent ? `${pricing.rtoTaxPercent}% UP RTO` : 'RTO Tax'})
                    </span>
                    <span className="font-mono">{formatINR(pricing.rtoTaxAmount)}</span>
                  </div>

                  <div className="breakdown-row">
                    <span>Comprehensive Insurance (1-Yr)</span>
                    <span className="font-mono">{formatINR(pricing.insuranceAmount)}</span>
                  </div>

                  {pricing.accessoriesAmount > 0 && (
                    <div className="breakdown-row">
                      <span>Standard Accessories Package</span>
                      <span className="font-mono">{formatINR(pricing.accessoriesAmount)}</span>
                    </div>
                  )}

                  {pricing.handlingCharges > 0 && (
                    <div className="breakdown-row">
                      <span>Registration & Handling</span>
                      <span className="font-mono">{formatINR(pricing.handlingCharges)}</span>
                    </div>
                  )}

                  {pricing.tcsAmount > 0 && (
                    <div className="breakdown-row">
                      <span>TCS (Tax Collected at Source 1%)</span>
                      <span className="font-mono">{formatINR(pricing.tcsAmount)}</span>
                    </div>
                  )}

                  <div className="breakdown-row row-total">
                    <span>Total On-Road Price ({pricing.city})</span>
                    <span className="font-mono font-bold text-accent">
                      {formatINR(pricing.totalOnRoadPrice)}
                    </span>
                  </div>
                </div>
              )}

              {!pricingLoading && !pricing && pricingError && (
                <div className="p-4 bg-amber-950/40 border border-amber-800/40 rounded-lg text-amber-300 text-xs my-3">
                  ⚠️ {pricingError}
                </div>
              )}

              {/* Price Validity & Legal Disclaimer from Database */}
              {pricing?.priceValidUntil && (
                <div className="text-xs text-slate-400 mt-3 pt-2 border-t border-slate-700/50">
                  🗓️ <strong>Price Quote Valid Until:</strong> {formatDate(pricing.priceValidUntil)}
                </div>
              )}

              <p className="pricing-disclaimer-note mt-2">
                ℹ️ {pricing?.disclaimer || t('priceDisclaimer')}
              </p>
            </div>

            {/* Available Colors */}
            {colors.length > 0 && (
              <div className="color-selection-wrap mt-6">
                <label className="section-label">
                  Showroom Color: <strong>{selectedColor || colors[0]}</strong>
                </label>
                <div className="color-chip-row">
                  {colors.map((c) => (
                    <button
                      key={c}
                      type="button"
                      className={`color-chip ${selectedColor === c ? 'active' : ''}`}
                      onClick={() => setSelectedColor(c)}
                    >
                      {c}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Technical Specifications Grid */}
            <div className="specs-table-card mt-6">
              <h3 className="box-title">Technical Specifications</h3>
              <div className="specs-key-grid">
                <div className="spec-item">
                  <span className="k">Engine</span>
                  <span className="v">{activeVariant?.engineSpecs || 'Standard'}</span>
                </div>
                <div className="spec-item">
                  <span className="k">Power</span>
                  <span className="v">{activeVariant?.powerSpecs || 'Standard'}</span>
                </div>
                <div className="spec-item">
                  <span className="k">Torque</span>
                  <span className="v">{activeVariant?.torqueSpecs || 'Standard'}</span>
                </div>
                <div className="spec-item">
                  <span className="k">Transmission</span>
                  <span className="v">{activeVariant?.transmission || 'Automatic'}</span>
                </div>
                <div className="spec-item">
                  <span className="k">Fuel Type</span>
                  <span className="v">{activeVariant?.fuelType || 'Petrol'}</span>
                </div>
                <div className="spec-item">
                  <span className="k">Seating Capacity</span>
                  <span className="v">
                    {activeVariant?.seatingCapacity ? `${activeVariant.seatingCapacity} Seats` : '5 Seats'}
                  </span>
                </div>
              </div>
            </div>

            {/* Actions Bar */}
            <div className="details-actions-bar mt-6 flex flex-wrap gap-3">
              <Link
                to={`/book-test-drive?carId=${model.id}`}
                className="btn btn-primary btn-lg flex-1"
              >
                Book VIP Test Drive ↗
              </Link>
              <button
                type="button"
                className="btn btn-secondary btn-lg flex-1"
                onClick={() => setShowEnquiryModal(true)}
              >
                💬 Enquire with Concierge
              </button>
              <Link to="/cars" className="btn btn-outline btn-lg">
                Back to Collection
              </Link>
            </div>

            <div className="consent-subnote mt-3">
              {t('consentNotice')}
            </div>
          </div>
        </div>
      </div>

      <EnquiryModal
        isOpen={showEnquiryModal}
        onClose={() => setShowEnquiryModal(false)}
        vehicle={{
          id: activeVariant?.id,
          variantId: activeVariant?.id,
          displayName: `${model?.brand || ''} ${model?.modelName || ''} - ${activeVariant?.name || ''}`,
          modelName: `${model?.brand || ''} ${model?.modelName || ''} - ${activeVariant?.name || ''}`,
          fuelType: activeVariant?.fuelType,
          transmission: activeVariant?.transmission,
          showroomId: model?.showroomId || 1,
        }}
        vehicleType="NEW"
      />
    </div>
  )
}

export default CarDetailsPage
