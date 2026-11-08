import { useState, useEffect, useMemo } from 'react'
import { getUsedCars } from '../services/marketplaceService.js'
import CarCard from '../components/CarCard.jsx'
import useDocumentTitle from '../hooks/useDocumentTitle.js'
import { useLanguage } from '../hooks/useLanguage.js'

function UsedCarsPage() {
  const { t } = useLanguage()
  useDocumentTitle('Certified Pre-Owned Cars')

  const [usedCars, setUsedCars] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const [filterStatus, setFilterStatus] = useState('ALL')
  const [filterFuel, setFilterFuel] = useState('ALL')
  const [filterPriceMax, setFilterPriceMax] = useState('ALL')
  const [reloadKey, setReloadKey] = useState(0)

  // Fetch whenever status changes, reload triggered, or on initial mount
  useEffect(() => {
    let ignore = false
    getUsedCars(filterStatus)
      .then((data) => {
        if (!ignore) {
          setUsedCars(data || [])
          setError(null)
        }
      })
      .catch((err) => {
        if (!ignore) {
          console.error('Failed to load certified pre-owned inventory:', err)
          setError(err.message || 'Unable to connect to the AutoVista pre-owned vehicles service.')
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
  }, [filterStatus, reloadKey])

  const handleStatusChange = (newStatus) => {
    setLoading(true)
    setFilterStatus(newStatus)
  }

  const handleRetry = () => {
    setLoading(true)
    setReloadKey((prev) => prev + 1)
  }

  // Filter cars based on fuel type and price
  const filteredCars = useMemo(() => {
    return (usedCars || []).filter((car) => {
      if (!car) return false
      // Fuel filter
      if (filterFuel !== 'ALL') {
        const carFuel = (car.fuelType || '').toUpperCase()
        if (carFuel !== filterFuel) return false
      }

      // Max price filter
      if (filterPriceMax !== 'ALL') {
        const maxPrice = Number(filterPriceMax)
        if (car.price > maxPrice) return false
      }

      return true
    })
  }, [usedCars, filterFuel, filterPriceMax])

  const handleClearFilters = () => {
    setFilterStatus('ALL')
    setFilterFuel('ALL')
    setFilterPriceMax('ALL')
    setLoading(true)
  }

  return (
    <div className="catalog-page">
      <div className="catalog-hero">
        <div className="container">
          <span className="eyebrow-badge">Certified Pre-Owned</span>
          <h1 className="page-title">{t('featuredUsedCars')}</h1>
          <p className="page-subtitle">
            Every pre-owned vehicle at AutoVista undergoes a comprehensive 150-point inspection and complete service history audit.
            Vehicle registration numbers are masked in accordance with our client privacy protocol.
          </p>

          {/* Filter Bar */}
          <div className="filter-bar-container mt-6">
            {/* Status Pills */}
            <div className="filter-pill-group">
              <button
                type="button"
                className={`filter-pill ${filterStatus === 'ALL' ? 'active' : ''}`}
                onClick={() => handleStatusChange('ALL')}
              >
                All Vehicles ({usedCars.length})
              </button>
              <button
                type="button"
                className={`filter-pill ${filterStatus === 'AVAILABLE' ? 'active' : ''}`}
                onClick={() => handleStatusChange('AVAILABLE')}
              >
                Available Now
              </button>
              <button
                type="button"
                className={`filter-pill ${filterStatus === 'BOOKED' ? 'active' : ''}`}
                onClick={() => handleStatusChange('BOOKED')}
              >
                Reserved / Booked
              </button>
            </div>

            {/* Powertrain and Price Filters */}
            <div className="filter-dropdown-wrap mt-3 flex items-center flex-wrap gap-3">
              <div>
                <label htmlFor="used-fuel-filter" className="filter-label mr-2">
                  Fuel:
                </label>
                <select
                  id="used-fuel-filter"
                  className="filter-select"
                  value={filterFuel}
                  onChange={(e) => setFilterFuel(e.target.value)}
                >
                  <option value="ALL">All Fuel Types</option>
                  <option value="PETROL">Petrol</option>
                  <option value="DIESEL">Diesel</option>
                </select>
              </div>

              <div>
                <label htmlFor="used-price-filter" className="filter-label mr-2">
                  Max Budget:
                </label>
                <select
                  id="used-price-filter"
                  className="filter-select"
                  value={filterPriceMax}
                  onChange={(e) => setFilterPriceMax(e.target.value)}
                >
                  <option value="ALL">All Price Ranges</option>
                  <option value="600000">Under ₹6 Lakh</option>
                  <option value="900000">Under ₹9 Lakh</option>
                  <option value="1200000">Under ₹12 Lakh</option>
                </select>
              </div>

              {(filterStatus !== 'ALL' || filterFuel !== 'ALL' || filterPriceMax !== 'ALL') && (
                <button
                  type="button"
                  className="filter-clear-btn"
                  onClick={handleClearFilters}
                >
                  Clear Filters ✕
                </button>
              )}
            </div>
          </div>
        </div>
      </div>

      <div className="container catalog-body">
        {/* Loading State */}
        {loading && (
          <div className="catalog-loading-state text-center py-16">
            <div className="loading-spinner mb-4" />
            <h3 className="text-xl font-semibold text-slate-200">
              Loading Certified Pre-Owned Inventory...
            </h3>
            <p className="text-slate-400 text-sm mt-1">
              Verifying 150-point inspection records from PostgreSQL
            </p>
          </div>
        )}

        {/* Error State with Retry */}
        {!loading && error && (
          <div className="empty-state-card text-center py-12">
            <div className="text-4xl mb-3">⚠️</div>
            <h2 className="text-2xl font-bold text-red-400">Unable to Load Vehicles</h2>
            <p className="text-slate-300 mt-2 max-w-lg mx-auto">{error}</p>
            <button
              type="button"
              className="btn btn-primary mt-6"
              onClick={handleRetry}
            >
              Retry Connection
            </button>
          </div>
        )}

        {/* Empty State */}
        {!loading && !error && filteredCars.length === 0 && (
          <div className="empty-state-card text-center py-16">
            <div className="text-4xl mb-3">🔍</div>
            <h2 className="text-2xl font-bold text-slate-100">No Pre-Owned Vehicles Found</h2>
            <p className="text-slate-400 mt-2">
              No vehicles currently match your selected status and budget filters.
            </p>
            <button
              type="button"
              className="btn btn-outline mt-6"
              onClick={handleClearFilters}
            >
              Reset Filters
            </button>
          </div>
        )}

        {/* Grid State */}
        {!loading && !error && filteredCars.length > 0 && (
          <>
            <div className="catalog-status-bar">
              <span className="results-count">
                Showing {filteredCars.length} of {usedCars.length} certified pre-owned vehicles
              </span>
              <span className="showroom-location-tag">
                🛡️ 150-Point Certified Quality Standard • Gomti Nagar
              </span>
            </div>

            <div className="cars-grid">
              {filteredCars.map((car) => (
                <CarCard key={car.id} car={car} isUsed={true} />
              ))}
            </div>
          </>
        )}

        {/* Certified guarantee banner */}
        <div className="notice-card mt-8">
          <div className="notice-icon">🛡️</div>
          <div className="notice-body">
            <strong>AutoVista Certified Pre-Owned Assurance:</strong>
            <p>
              Includes 1-Year Comprehensive Warranty, zero flood or major structural collision damage guarantee, and verified odometer documentation.
              Physical inspection and test drives available at our Gomti Nagar, Lucknow showroom.
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default UsedCarsPage