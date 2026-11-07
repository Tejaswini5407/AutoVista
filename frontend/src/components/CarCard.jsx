import { Link } from 'react-router-dom'
import Badge from './Badge.jsx'
import { formatINR, formatMileage, maskRegistrationNumber } from '../utils/formatters.js'
import { useLanguage } from '../hooks/useLanguage.js'

function CarCard({ car, isUsed = false }) {
  const { t } = useLanguage()

  const isBooked = car.status?.toUpperCase() === 'BOOKED'
  const detailUrl = isUsed ? `/used-cars/${car.id}` : `/cars/${car.id}`
  const driveUrl = `/book-test-drive?carId=${car.id}${isUsed ? '&type=used' : ''}`

  const priceLabel = isUsed ? 'Price' : t('exShowroom')
  const priceValue = isUsed
    ? car.price
    : (car.exShowroomPrice || car.variants?.[0]?.exShowroomPrice || 0)

  const heroImg = car.heroImageUrl || car.heroImage || ''
  const displayTitle = car.variantName || car.variant || car.modelName
  const displayBrand = car.brand || 'AutoVista'
  const displayYear = car.year || car.manufacturingYear || 2026

  const rawFuel = (car.fuelType || car.variants?.[0]?.fuelType || '').toUpperCase()
  const displayFuel = rawFuel === 'PETROL' ? t('fuelPetrol')
    : rawFuel === 'DIESEL' ? t('fuelDiesel')
    : rawFuel || 'Petrol'

  const rawTrans = (car.transmission || car.variants?.[0]?.transmission || '').toUpperCase()
  const displayTransmission = rawTrans === 'AUTOMATIC' ? t('transAutomatic')
    : rawTrans === 'MANUAL' ? t('transManual')
    : rawTrans || 'Automatic'

  return (
    <div className={`car-card ${isBooked ? 'car-card-booked' : ''}`}>
      <div className="car-card-media">
        <img
          src={heroImg}
          alt={`${displayBrand} ${displayTitle}`}
          loading="lazy"
          className="car-card-img"
        />
        <div className="car-card-badges">
          <Badge variant={isUsed ? 'secondary' : 'accent'}>
            {displayYear} {isUsed ? 'Pre-Owned' : 'New'}
          </Badge>
          <Badge variant={isBooked ? 'danger' : 'success'}>
            {isBooked ? t('statusBooked') : t('statusAvailable')}
          </Badge>
        </div>
      </div>

      <div className="car-card-content">
        <div className="car-card-header">
          <span className="car-brand-sub">{displayBrand}</span>
          <h3 className="car-title">{displayTitle}</h3>
        </div>

        <div className="car-specs-grid">
          <div className="car-spec-item">
            <span className="spec-label">Fuel</span>
            <span className="spec-value">{displayFuel}</span>
          </div>
          <div className="car-spec-item">
            <span className="spec-label">Transmission</span>
            <span className="spec-value">{displayTransmission}</span>
          </div>
          {isUsed ? (
            <>
              <div className="car-spec-item">
                <span className="spec-label">Odometer</span>
                <span className="spec-value">{formatMileage(car.odometerKm)}</span>
              </div>
              <div className="car-spec-item">
                <span className="spec-label">{t('registrationNo')}</span>
                <span className="spec-value spec-masked">
                  {car.maskedRegistrationNumber || maskRegistrationNumber(car.rawRegistrationNumber)}
                </span>
              </div>
            </>
          ) : (
            <>
              <div className="car-spec-item">
                <span className="spec-label">Body Type</span>
                <span className="spec-value">{car.bodyType}</span>
              </div>
              <div className="car-spec-item">
                <span className="spec-label">Engine</span>
                <span className="spec-value">{car.power || 'Hybrid Assist'}</span>
              </div>
            </>
          )}
        </div>

        <div className="car-card-pricing">
          <div className="price-stack">
            <span className="price-caption">{priceLabel}</span>
            <span className="price-amount">{formatINR(priceValue)}</span>
          </div>
        </div>

        <div className="car-card-actions">
          <Link to={detailUrl} className="btn btn-outline btn-sm">
            {t('viewDetails')}
          </Link>
          <Link
            to={driveUrl}
            className={`btn btn-primary btn-sm ${isBooked ? 'btn-disabled' : ''}`}
            aria-disabled={isBooked}
            onClick={(e) => isBooked && e.preventDefault()}
          >
            {isBooked ? 'Reserved' : t('bookDrive')}
          </Link>
        </div>
      </div>
    </div>
  )
}

export default CarCard
