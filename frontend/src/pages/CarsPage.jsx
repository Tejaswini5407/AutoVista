import { useState, useEffect, useMemo } from 'react'
import { getNewCars, normalizeCarModelForCard } from '../services/marketplaceService.js'
import CarCard from '../components/CarCard.jsx'
import useDocumentTitle from '../hooks/useDocumentTitle.js'
import { useLanguage } from '../hooks/useLanguage.js'

function CarsPage() {
  const { t } = useLanguage()
  useDocumentTitle('New Cars')

  const [models, setModels] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [filterFuel, setFilterFuel] = useState('ALL')
  const [filterPriceMax, setFilterPriceMax] = useState('ALL')
  const [reloadKey, setReloadKey] = useState(0)

  useEffect(() => {
    let ignore = false
    getNewCars()
      .then((data) => {
        if (!ignore) {
          setModels(data || [])
          setError(null)
        }
      })
      .catch((err) => {
        if (!ignore) {
          console.error('Failed to load new car inventory:', err)
          setError(err.message || 'Unable to connect to the AutoVista dealership catalog service.')
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
  }, [reloadKey])

  const handleRetry = () => {
    setLoading(true)
    setReloadKey((prev) => prev + 1)
  }

  // Normalize models for presentation
  const normalizedCars = useMemo(() => {
    return models.map((m) => normalizeCarModelForCard(m))
  }, [models])

  // Filter cars based on fuel type and maximum price
  const filteredCars = useMemo(() => {
    return normalizedCars.filter((car) => {
      // Fuel Type Filter
      if (filterFuel !== 'ALL') {
        const matchesFuel =
          (car.fuelType || '').toUpperCase() === filterFuel ||
          (car.variants && car.variants.some((v) => (v.fuelType || '').toUpperCase() === filterFuel))
        if (!matchesFuel) return false
      }

      // Max Price Filter
      if (filterPriceMax !== 'ALL') {
        const maxPrice = Number(filterPriceMax)
        const carPrice = car.exShowroomPrice || (car.variants?.[0]?.exShowroomPrice || 0)
        if (carPrice > maxPrice) return false
      }

      return true
    })
  }, [normalizedCars, filterFuel, filterPriceMax])

  const handleClearFilters = () => {
    setFilterFuel('ALL')
    setFilterPriceMax('ALL')
  }

  return (
    <div className="catalog-page">
      <div className="catalog-hero">
        <div className="container">
          <span className="eyebrow-badge">2026 Collection</span>
          <h1 className="page-title">{t('featuredNewCars')}</h1>
          <p className="page-subtitle">
            Engineered for effortless distinction. Discover our current inventory ready for immediate Lucknow showroom delivery.
          </p>

          {/* Filter Bar */}
          <div className="filter-bar-container mt-6">
            <div className="filter-pill-group">
              <button
                type="button"
                className={`filter-pill ${filterFuel === 'ALL' ? 'active' : ''}`}
                onClick={() => setFilterFuel('ALL')}
              >
                All Powertrains ({normalizedCars.length})
              </button>
              <button
                type="button"
                className={`filter-pill ${filterFuel === 'PETROL' ? 'active' : ''}`}
                onClick={() => setFilterFuel('PETROL')}
              >
                {t('fuelPetrol')}
              </button>
              <button
                type="button"
                className={`filter-pill ${filterFuel === 'DIESEL' ? 'active' : ''}`}
                onClick={() => setFilterFuel('DIESEL')}
              >
                {t('fuelDiesel')}
              </button>
              <button
                type="button"
                className={`filter-pill ${filterFuel === 'ELECTRIC' ? 'active' : ''}`}
                onClick={() => setFilterFuel('ELECTRIC')}
              >
                Electric EV
              </button>
            </div>

            {/* Price Filter Dropdown */}
            <div className="filter-dropdown-wrap mt-3">
              <label htmlFor="price-filter" className="filter-label mr-2">
                Price Budget:
              </label>
              <select
                id="price-filter"
                className="filter-select"
                value={filterPriceMax}
                onChange={(e) => setFilterPriceMax(e.target.value)}
              >
                <option value="ALL">All Price Ranges</option>
                <option value="1500000">Under ₹15 Lakh</option>
                <option value="2000000">Under ₹20 Lakh</option>
                <option value="3000000">Under ₹30 Lakh</option>
              </select>

              {(filterFuel !== 'ALL' || filterPriceMax !== 'ALL') && (
                <button
                  type="button"
                  className="filter-clear-btn ml-3"
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
              Loading New Car Showroom Inventory...
            </h3>
            <p className="text-slate-400 text-sm mt-1">
              Connecting to PostgreSQL database via AutoVista REST API
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

        {/* Empty State (Filtered out) */}
        {!loading && !error && filteredCars.length === 0 && (
          <div className="empty-state-card text-center py-16">
            <div className="text-4xl mb-3">🔍</div>
            <h2 className="text-2xl font-bold text-slate-100">No Vehicles Match Your Filter</h2>
            <p className="text-slate-400 mt-2">
              We couldn’t find any new cars matching your current powertrain and budget preferences.
            </p>
            <button
              type="button"
              className="btn btn-outline mt-6"
              onClick={handleClearFilters}
            >
              Reset All Filters
            </button>
          </div>
        )}

        {/* Success Grid State */}
        {!loading && !error && filteredCars.length > 0 && (
          <>
            <div className="catalog-status-bar">
              <span className="results-count">
                Showing {filteredCars.length} of {normalizedCars.length} vehicles
              </span>
              <span className="showroom-location-tag">
                📍 Lucknow Experience Centre
              </span>
            </div>

            <div className="cars-grid">
              {filteredCars.map((car) => (
                <CarCard key={car.id} car={car} isUsed={false} />
              ))}
            </div>
          </>
        )}

        {/* Pricing notice */}
        <div className="notice-card mt-8">
          <div className="notice-icon">ℹ️</div>
          <div className="notice-body">
            <strong>On-Road Price Estimation Notice:</strong>
            <p>{t('priceDisclaimer')}</p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default CarsPage