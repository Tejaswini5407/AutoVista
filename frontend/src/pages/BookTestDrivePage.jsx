import { useState, useEffect, useMemo, useCallback } from 'react'
import { useSearchParams, Link } from 'react-router-dom'
import { getNewCars, getUsedCars } from '../services/marketplaceService.js'
import {
  getAvailableSlots,
  createTestDriveBooking,
  getMyBookings,
  cancelBooking,
  OPERATIONAL_TIME_SLOTS,
} from '../services/bookingService.js'
import useDocumentTitle from '../hooks/useDocumentTitle.js'
import { useLanguage } from '../hooks/useLanguage.js'
import { useAuth } from '../hooks/useAuth.js'
import { formatINR } from '../utils/formatters.js'
import Badge from '../components/Badge.jsx'

function BookTestDrivePage() {
  const { t } = useLanguage()
  useDocumentTitle(t('navTestDrive') || 'Book VIP Test Drive')
  const { user, isAuthenticated, loginAsDemo } = useAuth()
  const [searchParams] = useSearchParams()

  const queryVariantId = searchParams.get('variantId')
  const queryUsedCarId = searchParams.get('usedCarId')
  const queryCarId = searchParams.get('carId')
  const queryType = searchParams.get('type')

  // Data states
  const [newCars, setNewCars] = useState([])
  const [usedCars, setUsedCars] = useState([])
  const [loadingCatalog, setLoadingCatalog] = useState(true)
  const [myBookings, setMyBookings] = useState([])
  const [loadingMyBookings, setLoadingMyBookings] = useState(false)

  // Selection states
  // vehicleKey: 'new-{variantId}' or 'used-{usedCarId}'
  const [selectedVehicleKey, setSelectedVehicleKey] = useState('')
  const [showroomId] = useState(1) // Default: AutoVista Gomti Nagar Flagship

  // Calculate default booking date: tomorrow
  const getTomorrowDate = () => {
    const d = new Date()
    d.setDate(d.getDate() + 1)
    return d.toISOString().split('T')[0]
  }

  const [bookingDate, setBookingDate] = useState(getTomorrowDate())
  const [selectedSlot, setSelectedSlot] = useState(OPERATIONAL_TIME_SLOTS[0])
  const [driveMode, setDriveMode] = useState('SHOWROOM') // 'SHOWROOM' or 'DOORSTEP'
  const [address, setAddress] = useState('Vibhuti Khand, Gomti Nagar, Lucknow')
  const [customerName, setCustomerName] = useState(() => user?.fullName || user?.name || '')
  const [customerPhone, setCustomerPhone] = useState(() => user?.phone || '+91 98765 43210')
  const [customerEmail, setCustomerEmail] = useState(() => user?.email || '')
  const [consent, setConsent] = useState(true)

  // Slot availability from backend
  const [slotData, setSlotData] = useState({
    operationalSlots: OPERATIONAL_TIME_SLOTS,
    bookedSlots: [],
    availableSlots: OPERATIONAL_TIME_SLOTS,
  })
  const [loadingSlots, setLoadingSlots] = useState(false)

  // Submission & feedback states
  const [submitting, setSubmitting] = useState(false)
  const [duplicateError, setDuplicateError] = useState('')
  const [generalError, setGeneralError] = useState('')
  const [confirmedBooking, setConfirmedBooking] = useState(null)
  const [cancelLoadingId, setCancelLoadingId] = useState(null)
  const [cancelMessage, setCancelMessage] = useState('')

  // 1. Fetch live vehicle catalogs
  useEffect(() => {
    let mounted = true
    async function loadVehicles() {
      try {
        setLoadingCatalog(true)
        const [modelsData, preOwnedData] = await Promise.all([
          getNewCars().catch(() => []),
          getUsedCars('AVAILABLE').catch(() => []),
        ])

        if (!mounted) return
        setNewCars(modelsData)
        setUsedCars(preOwnedData)

        // Pre-select vehicle based on query parameters or fallback
        if (queryVariantId) {
          setSelectedVehicleKey(`new-${queryVariantId}`)
        } else if (queryUsedCarId) {
          setSelectedVehicleKey(`used-${queryUsedCarId}`)
        } else if (queryCarId && (queryType === 'used' || queryType === 'preowned')) {
          setSelectedVehicleKey(`used-${queryCarId}`)
        } else if (queryCarId) {
          const matchedModel = modelsData.find((m) => String(m.id) === String(queryCarId))
          if (matchedModel?.variants?.length > 0) {
            setSelectedVehicleKey(`new-${matchedModel.variants[0].id}`)
          } else {
            setSelectedVehicleKey(`new-${queryCarId}`)
          }
        } else if (modelsData.length > 0 && modelsData[0].variants?.length > 0) {
          setSelectedVehicleKey(`new-${modelsData[0].variants[0].id}`)
        } else if (preOwnedData.length > 0) {
          setSelectedVehicleKey(`used-${preOwnedData[0].id}`)
        }
      } catch (err) {
        if (mounted) {
          setGeneralError('Failed to load vehicle catalog: ' + err.message)
        }
      } finally {
        if (mounted) setLoadingCatalog(false)
      }
    }

    loadVehicles()
    return () => {
      mounted = false
    }
  }, [queryVariantId, queryUsedCarId, queryCarId, queryType])

  // 2. Fetch real-time available time slots for selected showroom and date
  const loadSlots = useCallback(async (date) => {
    try {
      setLoadingSlots(true)
      const data = await getAvailableSlots(showroomId, date)
      setSlotData(data)

      // If currently selected slot is taken, reset to first available
      if (data.bookedSlots?.includes(selectedSlot)) {
        if (data.availableSlots?.length > 0) {
          setSelectedSlot(data.availableSlots[0])
        }
      }
    } catch {
      // Keep existing slots on failure
    } finally {
      setLoadingSlots(false)
    }
  }, [showroomId, selectedSlot])

  useEffect(() => {
    if (!bookingDate) return
    let ignore = false

    getAvailableSlots(showroomId, bookingDate)
      .then((data) => {
        if (!ignore && data) {
          setSlotData(data)
          if (data.bookedSlots?.includes(selectedSlot) && data.availableSlots?.length > 0) {
            setSelectedSlot(data.availableSlots[0])
          }
        }
      })
      .catch(() => {})

    return () => {
      ignore = true
    }
  }, [showroomId, bookingDate, selectedSlot])

  // 3. Fetch customer's own bookings if authenticated
  const loadCustomerBookings = useCallback(async () => {
    if (!isAuthenticated) return
    try {
      setLoadingMyBookings(true)
      const list = await getMyBookings()
      setMyBookings(list)
    } catch {
      // ignore background error
    } finally {
      setLoadingMyBookings(false)
    }
  }, [isAuthenticated])

  useEffect(() => {
    if (!isAuthenticated) return
    let ignore = false

    getMyBookings()
      .then((list) => {
        if (!ignore && list) {
          setMyBookings(list)
        }
      })
      .catch(() => {})

    return () => {
      ignore = true
    }
  }, [isAuthenticated])

  // Unified list of vehicles for dropdown
  const vehicleOptions = useMemo(() => {
    const list = []

    newCars.forEach((model) => {
      (model.variants || []).forEach((variant) => {
        list.push({
          key: `new-${variant.id}`,
          type: 'NEW',
          variantId: variant.id,
          brand: model.brand,
          modelName: model.modelName,
          variantName: variant.variantName,
          year: model.year,
          price: variant.exShowroomPrice,
          fuelType: variant.fuelType,
          transmission: variant.transmission,
          heroImage: model.heroImageUrl,
          displayName: `${model.brand} ${model.modelName} ${variant.variantName} (New ${model.year})`,
          isAvailable: variant.isAvailable !== false,
        })
      })
    })

    usedCars.forEach((car) => {
      list.push({
        key: `used-${car.id}`,
        type: 'USED',
        usedCarId: car.id,
        brand: car.brand,
        modelName: car.modelName,
        variantName: car.variantName,
        year: car.manufacturingYear,
        price: car.price,
        fuelType: car.fuelType,
        transmission: car.transmission,
        heroImage: car.heroImageUrl,
        displayName: `${car.brand} ${car.modelName} ${car.variantName} (${car.manufacturingYear} Pre-Owned • ${car.maskedRegistrationNumber || 'UP32 ••••'})`,
        isAvailable: car.status === 'AVAILABLE',
      })
    })

    return list
  }, [newCars, usedCars])

  // Currently selected vehicle object
  const selectedVehicle = useMemo(() => {
    return vehicleOptions.find((v) => v.key === selectedVehicleKey) || vehicleOptions[0]
  }, [vehicleOptions, selectedVehicleKey])

  // Handle Form Submission
  const handleSubmitBooking = async (e) => {
    e.preventDefault()
    setDuplicateError('')
    setGeneralError('')

    if (!consent) {
      setGeneralError('Mandatory customer consent is required for appointment scheduling.')
      return
    }

    if (!selectedVehicle) {
      setGeneralError('Please select a valid vehicle for your test drive.')
      return
    }

    const payload = {
      showroomId,
      carVariantId: selectedVehicle.type === 'NEW' ? selectedVehicle.variantId : null,
      usedCarId: selectedVehicle.type === 'USED' ? selectedVehicle.usedCarId : null,
      customerName: customerName.trim(),
      customerPhone: customerPhone.trim(),
      customerEmail: customerEmail.trim(),
      bookingDate,
      timeSlot: selectedSlot,
      driveMode,
      doorstepAddress: driveMode === 'DOORSTEP' ? address.trim() : null,
      consentGiven: true,
    }

    try {
      setSubmitting(true)
      const confirmed = await createTestDriveBooking(payload)
      setConfirmedBooking(confirmed)
      // Refresh slot list and customer bookings
      loadSlots(bookingDate)
      loadCustomerBookings()
    } catch (err) {
      // Handle HTTP 409 Conflict (Duplicate Slot Prevention)
      if (err.status === 409 || err.message?.toLowerCase().includes('already booked') || err.message?.toLowerCase().includes('conflict')) {
        setDuplicateError(
          err.message ||
            `Conflict Detected: The time slot (${selectedSlot}) is already reserved for this showroom. Please choose an alternate slot.`
        )
        // Refresh slot availability immediately from backend
        loadSlots(bookingDate)
      } else {
        setGeneralError(err.message || 'Failed to complete test drive reservation. Please try again.')
      }
    } finally {
      setSubmitting(false)
    }
  }

  // Handle Customer Booking Cancellation
  const handleCancelBooking = async (id) => {
    if (!window.confirm('Are you sure you want to cancel this scheduled test drive?')) return
    try {
      setCancelLoadingId(id)
      setCancelMessage('')
      await cancelBooking(id, 'Customer requested cancellation via portal')
      setCancelMessage('Your test drive booking has been cancelled and the time slot is now released.')
      loadCustomerBookings()
      loadSlots(bookingDate)
    } catch (err) {
      setCancelMessage('Failed to cancel booking: ' + err.message)
    } finally {
      setCancelLoadingId(null)
    }
  }

  return (
    <div className="booking-page">
      <div className="container py-8">
        <div className="booking-layout">
          {/* Left Column: Booking Form or Confirmation */}
          <div className="booking-form-col">
            <div className="booking-header">
              <span className="eyebrow-badge">VIP Dealership Experience</span>
              <h1 className="page-title">Schedule a Test Drive</h1>
              <p className="page-subtitle">
                Experience precision handling and tailored luxury at our Gomti Nagar facility or doorstep delivery anywhere in Lucknow.
              </p>
            </div>

            {/* Authentication Alert / Quick Login */}
            {!isAuthenticated && (
              <div className="alert-box alert-warning mb-6">
                <div className="flex flex-col gap-2">
                  <div className="flex items-center gap-2">
                    <span className="text-xl">🔒</span>
                    <strong>Customer Sign-In Required for VIP Appointment</strong>
                  </div>
                  <p className="text-xs text-muted">
                    Test drive bookings are protected by customer authentication to assign dedicated consultants and maintain SLA tracking.
                  </p>
                  <div className="flex gap-2 mt-2">
                    <button
                      type="button"
                      className="btn btn-primary btn-xs"
                      onClick={async () => {
                        try {
                          const loggedIn = await loginAsDemo('customer')
                          if (loggedIn) {
                            setCustomerName(loggedIn.fullName || loggedIn.name || 'Aarav Sharma')
                            setCustomerEmail(loggedIn.email || 'aarav.sharma@example.com')
                            setCustomerPhone(loggedIn.phone || '+91 98765 43210')
                          }
                        } catch (err) {
                          setGeneralError(err.message || 'Quick customer sign-in failed. Please verify connection.')
                        }
                      }}
                    >
                      ⚡ Quick Sign-In as Customer (Aarav Sharma)
                    </button>
                    <Link to="/login" className="btn btn-outline btn-xs">
                      Sign In with Password
                    </Link>
                  </div>
                </div>
              </div>
            )}

            {/* Authenticated Customer Banner */}
            {isAuthenticated && (
              <div className="flex items-center justify-between p-3 rounded-lg bg-surface-raised border border-border-subtle mb-6 text-xs">
                <div>
                  <span className="text-muted">Authenticated Customer: </span>
                  <strong className="text-accent-cyan">{user?.fullName || user?.name}</strong> ({user?.email})
                </div>
                <Badge variant="success">Verified Account</Badge>
              </div>
            )}

            {/* Error alerts */}
            {duplicateError && (
              <div className="alert-box alert-danger mb-6" role="alert">
                <div className="flex items-center gap-2">
                  <span className="text-xl">⚠️</span>
                  <div>
                    <strong>Duplicate Booking Conflict (Requirement #6)</strong>
                    <p className="text-xs mt-1">{duplicateError}</p>
                  </div>
                </div>
              </div>
            )}

            {generalError && (
              <div className="alert-box alert-danger mb-6" role="alert">
                ⚠️ {generalError}
              </div>
            )}

            {/* Confirmation View */}
            {confirmedBooking ? (
              <div className="confirmation-card" role="alert">
                <div className="confirmation-badge">✓ VIP Appointment Confirmed & Scheduled</div>
                <h2>Thank you, {confirmedBooking.customerName || customerName}!</h2>
                <p className="confirmation-desc">
                  Your VIP test drive has been confirmed in our Lucknow dealership engine with unique reference{' '}
                  <strong className="font-mono text-accent-cyan">{confirmedBooking.bookingRef}</strong>.
                </p>

                <div className="confirmation-details-box">
                  <div className="confirm-row">
                    <span>Booking Reference</span>
                    <strong className="font-mono text-accent-cyan">{confirmedBooking.bookingRef}</strong>
                  </div>
                  <div className="confirm-row">
                    <span>Vehicle Requested</span>
                    <strong>{confirmedBooking.vehicleName || selectedVehicle?.displayName}</strong>
                  </div>
                  <div className="confirm-row">
                    <span>Showroom Location</span>
                    <strong>{confirmedBooking.showroomName || 'AutoVista Gomti Nagar Flagship, Lucknow'}</strong>
                  </div>
                  <div className="confirm-row">
                    <span>Date & Time Slot</span>
                    <strong>{confirmedBooking.bookingDate} at {confirmedBooking.timeSlot}</strong>
                  </div>
                  <div className="confirm-row">
                    <span>Experience Mode</span>
                    <strong>
                      {confirmedBooking.driveMode === 'DOORSTEP'
                        ? `Doorstep Delivery (${confirmedBooking.doorstepAddress || address})`
                        : 'Dealership Showroom Visit'}
                    </strong>
                  </div>
                  <div className="confirm-row">
                    <span>Appointment Status</span>
                    <Badge variant="success">{confirmedBooking.status || 'CONFIRMED'}</Badge>
                  </div>
                </div>

                {/* Requirement #7: Mock Notification Confirmation Section */}
                <div className="p-4 rounded-lg bg-surface border border-border-subtle mb-6">
                  <h4 className="text-sm font-semibold mb-2 flex items-center gap-2">
                    <span>📲</span> Simulated Customer Notifications Dispatched (Requirement #7)
                  </h4>
                  <div className="flex flex-col gap-2 text-xs">
                    <div className="flex items-center justify-between py-1 border-b border-border-subtle">
                      <span>SMS Confirmation to {confirmedBooking.customerPhone || customerPhone}</span>
                      <span className="text-success font-mono">DELIVERED (SIMULATED)</span>
                    </div>
                    <div className="flex items-center justify-between py-1 border-b border-border-subtle">
                      <span>Email Itinerary to {confirmedBooking.customerEmail || customerEmail}</span>
                      <span className="text-success font-mono">DELIVERED (SIMULATED)</span>
                    </div>
                    <div className="flex items-center justify-between py-1">
                      <span>WhatsApp Appointment Pass to {confirmedBooking.customerPhone || customerPhone}</span>
                      <span className="text-success font-mono">DELIVERED (SIMULATED)</span>
                    </div>
                  </div>
                </div>

                <div className="confirmation-actions">
                  <button
                    type="button"
                    className="btn btn-outline"
                    onClick={() => {
                      setConfirmedBooking(null)
                      loadSlots(bookingDate)
                    }}
                  >
                    Schedule Another Test Drive
                  </button>
                  <Link to="/" className="btn btn-primary">
                    Return to Dealership Home
                  </Link>
                </div>
              </div>
            ) : (
              /* Booking Form */
              <form className="booking-form" onSubmit={handleSubmitBooking}>
                {/* 1. Vehicle Selector */}
                <div className="form-group">
                  <label htmlFor="vehicleKey" className="form-label flex justify-between">
                    <span>Select Vehicle</span>
                    {loadingCatalog && <span className="text-xs text-muted">Refreshing inventory...</span>}
                  </label>
                  <select
                    id="vehicleKey"
                    name="vehicleKey"
                    className="form-select"
                    value={selectedVehicleKey}
                    onChange={(e) => {
                      setSelectedVehicleKey(e.target.value)
                      setDuplicateError('')
                    }}
                    required
                    disabled={loadingCatalog || vehicleOptions.length === 0}
                  >
                    <optgroup label="New Vehicles (2026 Models)">
                      {vehicleOptions
                        .filter((v) => v.type === 'NEW')
                        .map((v) => (
                          <option key={v.key} value={v.key}>
                            {v.displayName} — {formatINR(v.price)}
                          </option>
                        ))}
                    </optgroup>
                    <optgroup label="Certified Pre-Owned (Ready for Test Drive)">
                      {vehicleOptions
                        .filter((v) => v.type === 'USED')
                        .map((v) => (
                          <option key={v.key} value={v.key}>
                            {v.displayName} — {formatINR(v.price)}
                          </option>
                        ))}
                    </optgroup>
                  </select>
                </div>

                {/* 2. Experience Location Mode */}
                <div className="form-group">
                  <label className="form-label">Experience Location</label>
                  <div className="radio-tile-group">
                    <label className={`radio-tile ${driveMode === 'SHOWROOM' ? 'active' : ''}`}>
                      <input
                        type="radio"
                        name="driveMode"
                        value="SHOWROOM"
                        checked={driveMode === 'SHOWROOM'}
                        onChange={(e) => setDriveMode(e.target.value)}
                      />
                      <span className="tile-title">🏢 Dealership Showroom</span>
                      <span className="tile-desc">Gomti Nagar Flagship, Shaheed Path, Lucknow</span>
                    </label>
                    <label className={`radio-tile ${driveMode === 'DOORSTEP' ? 'active' : ''}`}>
                      <input
                        type="radio"
                        name="driveMode"
                        value="DOORSTEP"
                        checked={driveMode === 'DOORSTEP'}
                        onChange={(e) => setDriveMode(e.target.value)}
                      />
                      <span className="tile-title">🚗 Doorstep Experience</span>
                      <span className="tile-desc">Brought directly to your home or office in Lucknow</span>
                    </label>
                  </div>
                </div>

                {driveMode === 'DOORSTEP' && (
                  <div className="form-group">
                    <label htmlFor="address" className="form-label">
                      Lucknow Delivery Address & Landmark
                    </label>
                    <input
                      type="text"
                      id="address"
                      name="address"
                      className="form-input"
                      value={address}
                      onChange={(e) => setAddress(e.target.value)}
                      required
                      placeholder="e.g. Plot 14, Vibhuti Khand, Gomti Nagar, Lucknow"
                    />
                  </div>
                )}

                {/* 3. Date & Available Slots */}
                <div className="form-row">
                  <div className="form-group flex-1">
                    <label htmlFor="date" className="form-label">
                      Preferred Date
                    </label>
                    <input
                      type="date"
                      id="date"
                      name="date"
                      className="form-input"
                      value={bookingDate}
                      onChange={(e) => {
                        setBookingDate(e.target.value)
                        setDuplicateError('')
                      }}
                      min={new Date().toISOString().split('T')[0]}
                      required
                    />
                  </div>

                  <div className="form-group flex-1">
                    <label htmlFor="timeSlot" className="form-label flex justify-between">
                      <span>Time Slot</span>
                      {loadingSlots && <span className="text-xs text-muted">Checking slots...</span>}
                    </label>
                    <select
                      id="timeSlot"
                      name="timeSlot"
                      className="form-select"
                      value={selectedSlot}
                      onChange={(e) => {
                        setSelectedSlot(e.target.value)
                        setDuplicateError('')
                      }}
                      required
                      disabled={loadingSlots}
                    >
                      {slotData.operationalSlots.map((slot) => {
                        const isBooked = slotData.bookedSlots?.includes(slot)
                        return (
                          <option key={slot} value={slot} disabled={isBooked}>
                            {slot} {isBooked ? '— [RESERVED / UNAVAILABLE]' : '— [AVAILABLE]'}
                          </option>
                        )
                      })}
                    </select>
                  </div>
                </div>

                {/* Real-time slot availability feedback */}
                <div className="text-xs text-muted flex items-center justify-between px-1">
                  <span>
                    Showroom: <strong>AutoVista Gomti Nagar</strong>
                  </span>
                  <span>
                    Available Slots:{' '}
                    <strong className="text-accent-cyan">
                      {slotData.availableSlots?.length || 0} of {slotData.operationalSlots?.length || 6}
                    </strong>
                  </span>
                </div>

                {/* 4. Contact Details */}
                <div className="form-group">
                  <label htmlFor="fullName" className="form-label">
                    Customer Full Name
                  </label>
                  <input
                    type="text"
                    id="fullName"
                    name="fullName"
                    className="form-input"
                    value={customerName}
                    onChange={(e) => setCustomerName(e.target.value)}
                    required
                    placeholder="e.g. Aarav Sharma"
                  />
                </div>

                <div className="form-row">
                  <div className="form-group flex-1">
                    <label htmlFor="phone" className="form-label">
                      Mobile Number (SMS & WhatsApp Updates)
                    </label>
                    <input
                      type="tel"
                      id="phone"
                      name="phone"
                      className="form-input"
                      value={customerPhone}
                      onChange={(e) => setCustomerPhone(e.target.value)}
                      required
                      placeholder="+91 98765 43210"
                    />
                  </div>

                  <div className="form-group flex-1">
                    <label htmlFor="email" className="form-label">
                      Email Address (Booking Pass)
                    </label>
                    <input
                      type="email"
                      id="email"
                      name="email"
                      className="form-input"
                      value={customerEmail}
                      onChange={(e) => setCustomerEmail(e.target.value)}
                      required
                      placeholder="aarav.sharma@example.com"
                    />
                  </div>
                </div>

                {/* Consent & Opt-Out (Requirement #8) */}
                <div className="form-group consent-checkbox-group">
                  <label className="checkbox-label">
                    <input
                      type="checkbox"
                      name="consent"
                      checked={consent}
                      onChange={(e) => setConsent(e.target.checked)}
                      required
                    />
                    <span>
                      I provide my explicit consent to AutoVista Lucknow to process my details, assign a dedicated sales consultant, and contact me via phone/SMS/WhatsApp for this test drive. I acknowledge I can opt out at any time.
                    </span>
                  </label>
                </div>

                <button
                  type="submit"
                  className="btn btn-primary btn-lg btn-full"
                  disabled={submitting || !isAuthenticated}
                >
                  {submitting
                    ? 'Verifying Slot & Scheduling...'
                    : isAuthenticated
                    ? 'Confirm VIP Test Drive Reservation'
                    : 'Sign In to Confirm VIP Reservation'}
                </button>
              </form>
            )}
          </div>

          {/* Right Column: Vehicle Preview & Assurance */}
          <div className="booking-preview-col">
            {selectedVehicle ? (
              <div className="booking-car-card">
                <div className="flex justify-between items-center mb-2">
                  <span className="badge badge-accent">Selected Vehicle</span>
                  <Badge variant={selectedVehicle.type === 'NEW' ? 'primary' : 'secondary'}>
                    {selectedVehicle.type === 'NEW' ? 'New 2026' : 'Certified Pre-Owned'}
                  </Badge>
                </div>

                {selectedVehicle.heroImage && (
                  <img
                    src={selectedVehicle.heroImage}
                    alt={selectedVehicle.displayName}
                    className="preview-img"
                  />
                )}

                <h3 className="preview-title">{selectedVehicle.brand} {selectedVehicle.modelName}</h3>
                <p className="text-sm font-semibold text-accent-cyan">
                  {selectedVehicle.variantName}
                </p>
                <p className="preview-spec text-xs text-muted mb-4">
                  {selectedVehicle.fuelType} • {selectedVehicle.transmission} • {formatINR(selectedVehicle.price)}
                </p>

                <div className="preview-perks">
                  <div className="perk-item">
                    <span>⚡</span>
                    <div>
                      <strong>Guaranteed 5-Min SLA Routing</strong>
                      <p>Your appointment is automatically queued to our on-duty Lucknow sales team.</p>
                    </div>
                  </div>
                  <div className="perk-item">
                    <span>🔒</span>
                    <div>
                      <strong>Duplicate Slot Prevention</strong>
                      <p>Database unique constraint uq_test_drive_duplicate_slot guarantees zero double bookings.</p>
                    </div>
                  </div>
                  <div className="perk-item">
                    <span>📲</span>
                    <div>
                      <strong>Multi-Channel Confirmation</strong>
                      <p>Instant SMS, Email, and WhatsApp simulated notifications with full booking pass.</p>
                    </div>
                  </div>
                </div>
              </div>
            ) : (
              <div className="booking-car-card text-center text-muted py-8">
                <p>Loading vehicle details...</p>
              </div>
            )}
          </div>
        </div>

        {/* Customer's Scheduled Test Drives History Section */}
        {isAuthenticated && (
          <div className="mt-12 pt-8 border-t border-border-subtle">
            <div className="flex items-center justify-between mb-4">
              <div>
                <h3 className="text-xl font-bold">My Scheduled VIP Test Drives</h3>
                <p className="text-xs text-muted">
                  Your active and past test-drive appointments (Customer Data Isolation enforced).
                </p>
              </div>
              <button
                type="button"
                className="btn btn-outline btn-xs"
                onClick={loadCustomerBookings}
                disabled={loadingMyBookings}
              >
                {loadingMyBookings ? 'Refreshing...' : '↻ Refresh History'}
              </button>
            </div>

            {cancelMessage && (
              <div className="alert-box alert-info mb-4 text-xs">
                {cancelMessage}
              </div>
            )}

            {loadingMyBookings ? (
              <div className="text-center py-6 text-muted text-sm">Loading your appointments...</div>
            ) : myBookings.length > 0 ? (
              <div className="responsive-table-scroll bg-card rounded-lg border border-border-subtle">
                <table className="admin-table">
                  <thead>
                    <tr>
                      <th>Booking Ref</th>
                      <th>Vehicle</th>
                      <th>Showroom</th>
                      <th>Date & Slot</th>
                      <th>Mode</th>
                      <th>Status</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {myBookings.map((b) => (
                      <tr key={b.id}>
                        <td className="font-mono text-accent-cyan font-semibold">{b.bookingRef}</td>
                        <td><strong>{b.vehicleName}</strong></td>
                        <td className="text-xs">{b.showroomName}</td>
                        <td>
                          <div className="text-xs font-semibold">{b.bookingDate}</div>
                          <div className="text-xs text-muted">{b.timeSlot}</div>
                        </td>
                        <td className="text-xs">{b.driveMode}</td>
                        <td>
                          <Badge
                            variant={
                              b.status === 'CONFIRMED'
                                ? 'success'
                                : b.status === 'COMPLETED'
                                ? 'primary'
                                : b.status === 'CANCELLED'
                                ? 'secondary'
                                : 'warning'
                            }
                          >
                            {b.status}
                          </Badge>
                        </td>
                        <td>
                          {b.status === 'CONFIRMED' ? (
                            <button
                              type="button"
                              className="btn btn-outline btn-xs text-danger"
                              onClick={() => handleCancelBooking(b.id)}
                              disabled={cancelLoadingId === b.id}
                            >
                              {cancelLoadingId === b.id ? 'Cancelling...' : 'Cancel Slot'}
                            </button>
                          ) : (
                            <span className="text-muted text-xs">—</span>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div className="text-center py-6 text-muted text-sm bg-surface rounded-lg border border-border-subtle">
                You have no scheduled test drives yet. Choose a vehicle and time slot above to reserve your VIP experience.
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  )
}

export default BookTestDrivePage