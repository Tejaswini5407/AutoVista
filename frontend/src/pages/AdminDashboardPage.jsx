import { useState, useEffect } from 'react'
import useDocumentTitle from '../hooks/useDocumentTitle.js'
import { useAuth } from '../hooks/useAuth.js'
import {
  getAdminStats,
  getAdminModels,
  createCarModel,
  toggleCarModelStatus,
  createCarVariant,
  toggleCarVariantStatus,
  getAdminUsedCars,
  createUsedCar,
  updateUsedCarStatus,
  getAdminPricingRules,
  createOrUpdatePricingRule,
} from '../services/adminService.js'
import {
  getAdminBookings,
  updateAdminBookingStatus,
  getBookingNotifications,
} from '../services/bookingService.js'
import {
  getAdminLeads,
  reassignAdminLead,
  getAdminLeadHistory,
  getAdminLeadCommunications,
  getAdminLeadOverview,
} from '../services/leadService.js'
import { formatINR } from '../utils/formatters.js'
import Badge from '../components/Badge.jsx'

function AdminDashboardPage() {
  useDocumentTitle('Dealership Administration Console')
  const { user, loginAsDemo } = useAuth()
  const isAdmin = user?.role === 'ADMIN'

  const [activeTab, setActiveTab] = useState('inventory') // 'inventory' | 'used' | 'pricing' | 'bookings'
  const [stats, setStats] = useState(null)
  const [models, setModels] = useState([])
  const [usedCars, setUsedCars] = useState([])
  const [pricingRules, setPricingRules] = useState([])
  const [loading, setLoading] = useState(isAdmin)
  const [error, setError] = useState(null)
  const [successMsg, setSuccessMsg] = useState(null)
  const [actionLoading, setActionLoading] = useState(false)

  // Modals state
  const [showModelModal, setShowModelModal] = useState(false)
  const [showVariantModal, setShowVariantModal] = useState(false)
  const [selectedModelForVariant, setSelectedModelForVariant] = useState(null)
  const [showUsedCarModal, setShowUsedCarModal] = useState(false)
  const [showPricingModal, setShowPricingModal] = useState(false)

  // Form states
  const [modelForm, setModelForm] = useState({
    brand: '',
    modelName: '',
    bodyType: 'Premium Sedan',
    year: new Date().getFullYear(),
    description: '',
    heroImageUrl: '',
    isActive: true,
  })

  const [variantForm, setVariantForm] = useState({
    variantName: '',
    fuelType: 'PETROL',
    transmission: 'AUTOMATIC',
    engineSpecs: '',
    powerSpecs: '',
    torqueSpecs: '',
    seatingCapacity: 5,
    exShowroomPrice: '',
    tagline: '',
    features: 'Panoramic Sunroof, ADAS Level 2, Ventilated Seats',
    colors: 'Platinum White, Obsidian Black, Celestial Blue',
    isAvailable: true,
  })

  const [usedCarForm, setUsedCarForm] = useState({
    showroomId: 1,
    brand: '',
    modelName: '',
    variantName: '',
    manufacturingYear: 2023,
    price: '',
    odometerKm: '',
    rawRegistrationNumber: '',
    fuelType: 'PETROL',
    transmission: 'AUTOMATIC',
    ownersCount: 1,
    insuranceValidity: 'Comprehensive valid till Dec 2026',
    inspectionPointsPassed: 150,
    status: 'AVAILABLE',
    heroImageUrl: '',
    features: 'Auto Climate Control, Touchscreen Infotainment, Rear Camera',
    tagline: 'Single owner, Lucknow registered, dealership certified',
  })

  const [pricingForm, setPricingForm] = useState({
    carVariantId: '',
    city: 'Lucknow',
    state: 'Uttar Pradesh',
    rtoTaxPercent: 10.0,
    insuranceAmount: 43575,
    accessoriesAmount: 25000,
    handlingCharges: 5000,
    priceValidUntil: '2026-12-31',
    disclaimer:
      'Ex-showroom price is indicative. Mandatory charges include RTO registration, road tax, and insurance. TCS (1%) applies if ex-showroom exceeds ₹10,00,000 as per Section 206C(1F) of IT Act. Prices are subject to change without prior notice.',
  })

  // Phase 5 Test Drive Bookings Dynamic State
  const [bookings, setBookings] = useState([])
  const [bookingFilters, setBookingFilters] = useState({
    date: '',
    showroomId: '',
    status: 'ALL',
  })
  const [selectedBookingForNotifications, setSelectedBookingForNotifications] = useState(null)
  const [bookingNotifications, setBookingNotifications] = useState([])
  const [loadingNotifications, setLoadingNotifications] = useState(false)
  const [statusModalBooking, setStatusModalBooking] = useState(null)
  const [statusModalNewStatus, setStatusModalNewStatus] = useState('COMPLETED')
  const [statusModalNotes, setStatusModalNotes] = useState('')

  // Phase 6 Lead Management State
  const [leads, setLeads] = useState([])
  const [leadOverview, setLeadOverview] = useState(null)
  const [leadFilters, setLeadFilters] = useState({
    status: 'ALL',
    consultantId: '',
    unassignedOnly: false,
    search: '',
  })
  const [selectedLeadForReassign, setSelectedLeadForReassign] = useState(null)
  const [reassignConsultantId, setReassignConsultantId] = useState('')
  const [reassignNotes, setReassignNotes] = useState('')
  const [reassignLoading, setReassignLoading] = useState(false)

  const [selectedLeadForAudit, setSelectedLeadForAudit] = useState(null)
  const [auditHistory, setAuditHistory] = useState([])
  const [auditComms, setAuditComms] = useState([])
  const [loadingAudit, setLoadingAudit] = useState(false)

  // Data fetching effect
  useEffect(() => {
    if (!isAdmin) return

    let isMounted = true

    async function loadAdminData() {
      try {
        const [statsData, modelsData, usedData, pricingData, bookingsData, leadsData, overviewData] = await Promise.all([
          getAdminStats().catch((err) => {
            console.warn('Stats load warning:', err.message)
            return null
          }),
          getAdminModels().catch((err) => {
            console.warn('Models load warning:', err.message)
            return []
          }),
          getAdminUsedCars().catch((err) => {
            console.warn('Used cars load warning:', err.message)
            return []
          }),
          getAdminPricingRules().catch((err) => {
            console.warn('Pricing rules load warning:', err.message)
            return []
          }),
          getAdminBookings().catch((err) => {
            console.warn('Bookings load warning:', err.message)
            return []
          }),
          getAdminLeads().catch((err) => {
            console.warn('Leads load warning:', err.message)
            return []
          }),
          getAdminLeadOverview().catch((err) => {
            console.warn('Lead overview warning:', err.message)
            return null
          }),
        ])

        if (isMounted) {
          if (statsData) setStats(statsData)
          if (modelsData) setModels(modelsData)
          if (usedData) setUsedCars(usedData)
          if (pricingData) setPricingRules(pricingData)
          if (bookingsData) setBookings(bookingsData)
          if (leadsData) setLeads(leadsData)
          if (overviewData) setLeadOverview(overviewData)
          setError(null)
        }
      } catch (err) {
        if (isMounted) setError(err.message || 'Failed to load dealership console data')
      } finally {
        if (isMounted) setLoading(false)
      }
    }

    loadAdminData()

    return () => {
      isMounted = false
    }
  }, [isAdmin])

  // Refresh helper
  const refreshData = async () => {
    try {
      const [statsData, modelsData, usedData, pricingData, bookingsData, leadsData, overviewData] = await Promise.all([
        getAdminStats(),
        getAdminModels(),
        getAdminUsedCars(),
        getAdminPricingRules(),
        getAdminBookings(bookingFilters),
        getAdminLeads(leadFilters),
        getAdminLeadOverview(),
      ])
      setStats(statsData)
      setModels(modelsData)
      setUsedCars(usedData)
      setPricingRules(pricingData)
      setBookings(bookingsData)
      setLeads(leadsData)
      setLeadOverview(overviewData)
    } catch (err) {
      console.warn('Error refreshing admin data:', err.message)
    }
  }

  // Booking filtering handlers
  const handleBookingFilterChange = async (key, val) => {
    const nextFilters = { ...bookingFilters, [key]: val }
    setBookingFilters(nextFilters)
    setActionLoading(true)
    try {
      const data = await getAdminBookings(nextFilters)
      setBookings(data)
    } catch (err) {
      setError(err.message || 'Failed to filter bookings')
    } finally {
      setActionLoading(false)
    }
  }

  const handleResetBookingFilters = async () => {
    const reset = { date: '', showroomId: '', status: 'ALL' }
    setBookingFilters(reset)
    setActionLoading(true)
    try {
      const data = await getAdminBookings(reset)
      setBookings(data)
    } catch (err) {
      setError(err.message || 'Failed to reset booking filters')
    } finally {
      setActionLoading(false)
    }
  }

  // Booking status update handler
  const handleUpdateBookingStatus = async (bookingId, newStatus, notes = '') => {
    setActionLoading(true)
    try {
      const updated = await updateAdminBookingStatus(bookingId, newStatus, notes)
      setSuccessMsg(`Booking ${updated.bookingRef} status updated to ${newStatus}. Multi-channel notification recorded.`)
      setStatusModalBooking(null)
      setStatusModalNotes('')
      await refreshData()
    } catch (err) {
      setError(err.message || 'Failed to update booking status')
    } finally {
      setActionLoading(false)
    }
  }

  // Open notification audit modal
  const handleOpenNotificationsModal = async (booking) => {
    setSelectedBookingForNotifications(booking)
    setLoadingNotifications(true)
    try {
      const notifs = await getBookingNotifications(booking.id)
      setBookingNotifications(notifs)
    } catch (err) {
      console.warn('Failed to load notifications:', err.message)
      setBookingNotifications(booking.notifications || [])
    } finally {
      setLoadingNotifications(false)
    }
  }

  // Lead Filtering Handlers (Phase 6)
  const handleLeadFilterChange = async (key, val) => {
    const nextFilters = { ...leadFilters, [key]: val }
    setLeadFilters(nextFilters)
    setActionLoading(true)
    try {
      const data = await getAdminLeads(nextFilters)
      setLeads(data)
    } catch (err) {
      setError(err.message || 'Failed to filter dealership leads')
    } finally {
      setActionLoading(false)
    }
  }

  const handleResetLeadFilters = async () => {
    const reset = { status: 'ALL', consultantId: '', unassignedOnly: false, search: '' }
    setLeadFilters(reset)
    setActionLoading(true)
    try {
      const data = await getAdminLeads(reset)
      setLeads(data)
    } catch (err) {
      setError(err.message || 'Failed to reset lead filters')
    } finally {
      setActionLoading(false)
    }
  }

  const handleOpenReassignModal = (lead) => {
    setSelectedLeadForReassign(lead)
    setReassignConsultantId(lead.lastAssignedConsultantId || '')
    setReassignNotes('')
  }

  const handleExecuteReassign = async (e) => {
    e.preventDefault()
    if (!selectedLeadForReassign || !reassignConsultantId) return
    setReassignLoading(true)
    try {
      const updated = await reassignAdminLead(
        selectedLeadForReassign.id,
        reassignConsultantId,
        reassignNotes
      )
      setSuccessMsg(
        `Enquiry ${updated.leadRef} reassigned to ${
          updated.assignedConsultantName || 'new consultant'
        }. SLA reset to 5 minutes.`
      )
      setSelectedLeadForReassign(null)
      setReassignNotes('')
      await refreshData()
    } catch (err) {
      setError(err.message || 'Failed to reassign lead')
    } finally {
      setReassignLoading(false)
    }
  }

  const handleOpenLeadAuditModal = async (lead) => {
    setSelectedLeadForAudit(lead)
    setLoadingAudit(true)
    try {
      const [history, comms] = await Promise.all([
        getAdminLeadHistory(lead.id).catch(() => lead.statusHistory || []),
        getAdminLeadCommunications(lead.id).catch(() => lead.communications || lead.communicationHistory || []),
      ])
      setAuditHistory(history)
      setAuditComms(comms)
    } catch (err) {
      console.warn('Failed to load lead audit details:', err.message)
      setAuditHistory(lead.statusHistory || [])
      setAuditComms(lead.communications || lead.communicationHistory || [])
    } finally {
      setLoadingAudit(false)
    }
  }

  // Model actions
  const handleToggleModel = async (id, currentActive) => {
    setActionLoading(true)
    try {
      await toggleCarModelStatus(id, !currentActive)
      setSuccessMsg(`Car model ${!currentActive ? 'activated' : 'deactivated'} successfully.`)
      await refreshData()
    } catch (err) {
      setError(err.message || 'Failed to update model status')
    } finally {
      setActionLoading(false)
    }
  }

  const handleSaveModel = async (e) => {
    e.preventDefault()
    setActionLoading(true)
    try {
      await createCarModel({
        ...modelForm,
        year: parseInt(modelForm.year, 10),
      })
      setSuccessMsg(`Car model "${modelForm.modelName}" created successfully!`)
      setShowModelModal(false)
      setModelForm({
        brand: '',
        modelName: '',
        bodyType: 'Premium Sedan',
        year: new Date().getFullYear(),
        description: '',
        heroImageUrl: '',
        isActive: true,
      })
      await refreshData()
    } catch (err) {
      setError(err.message || 'Failed to create car model')
    } finally {
      setActionLoading(false)
    }
  }

  // Variant actions
  const handleToggleVariant = async (variantId, currentAvailable) => {
    setActionLoading(true)
    try {
      await toggleCarVariantStatus(variantId, !currentAvailable)
      setSuccessMsg(`Car variant availability updated successfully.`)
      await refreshData()
    } catch (err) {
      setError(err.message || 'Failed to update variant availability')
    } finally {
      setActionLoading(false)
    }
  }

  const handleSaveVariant = async (e) => {
    e.preventDefault()
    if (!selectedModelForVariant) return
    setActionLoading(true)
    try {
      await createCarVariant(selectedModelForVariant.id, {
        ...variantForm,
        seatingCapacity: parseInt(variantForm.seatingCapacity, 10),
        exShowroomPrice: parseFloat(variantForm.exShowroomPrice),
      })
      setSuccessMsg(`Variant "${variantForm.variantName}" added to ${selectedModelForVariant.modelName}!`)
      setShowVariantModal(false)
      setSelectedModelForVariant(null)
      setVariantForm({
        variantName: '',
        fuelType: 'PETROL',
        transmission: 'AUTOMATIC',
        engineSpecs: '',
        powerSpecs: '',
        torqueSpecs: '',
        seatingCapacity: 5,
        exShowroomPrice: '',
        tagline: '',
        features: 'Panoramic Sunroof, ADAS Level 2, Ventilated Seats',
        colors: 'Platinum White, Obsidian Black, Celestial Blue',
        isAvailable: true,
      })
      await refreshData()
    } catch (err) {
      setError(err.message || 'Failed to create variant')
    } finally {
      setActionLoading(false)
    }
  }

  // Used Car actions
  const handleUsedCarStatusChange = async (id, newStatus) => {
    setActionLoading(true)
    try {
      await updateUsedCarStatus(id, newStatus)
      setSuccessMsg(`Vehicle status updated to ${newStatus}. Masked registration strictly preserved.`)
      await refreshData()
    } catch (err) {
      setError(err.message || 'Failed to update vehicle status')
    } finally {
      setActionLoading(false)
    }
  }

  const handleSaveUsedCar = async (e) => {
    e.preventDefault()
    setActionLoading(true)
    try {
      await createUsedCar({
        ...usedCarForm,
        manufacturingYear: parseInt(usedCarForm.manufacturingYear, 10),
        price: parseFloat(usedCarForm.price),
        odometerKm: parseInt(usedCarForm.odometerKm, 10),
        ownersCount: parseInt(usedCarForm.ownersCount, 10),
        inspectionPointsPassed: parseInt(usedCarForm.inspectionPointsPassed, 10),
      })
      setSuccessMsg(`Pre-owned car added to inventory with masked registration.`)
      setShowUsedCarModal(false)
      setUsedCarForm({
        showroomId: 1,
        brand: '',
        modelName: '',
        variantName: '',
        manufacturingYear: 2023,
        price: '',
        odometerKm: '',
        rawRegistrationNumber: '',
        fuelType: 'PETROL',
        transmission: 'AUTOMATIC',
        ownersCount: 1,
        insuranceValidity: 'Comprehensive valid till Dec 2026',
        inspectionPointsPassed: 150,
        status: 'AVAILABLE',
        heroImageUrl: '',
        features: 'Auto Climate Control, Touchscreen Infotainment, Rear Camera',
        tagline: 'Single owner, Lucknow registered, dealership certified',
      })
      await refreshData()
    } catch (err) {
      setError(err.message || 'Failed to add pre-owned vehicle')
    } finally {
      setActionLoading(false)
    }
  }

  // City-Wise Pricing actions
  const handleSavePricing = async (e) => {
    e.preventDefault()
    setActionLoading(true)
    try {
      await createOrUpdatePricingRule({
        ...pricingForm,
        carVariantId: parseInt(pricingForm.carVariantId, 10),
        rtoTaxPercent: parseFloat(pricingForm.rtoTaxPercent),
        insuranceAmount: parseFloat(pricingForm.insuranceAmount),
        accessoriesAmount: parseFloat(pricingForm.accessoriesAmount),
        handlingCharges: parseFloat(pricingForm.handlingCharges),
      })
      setSuccessMsg(
        `Pricing rule for ${pricingForm.city} calculated and saved! Customer-facing APIs immediately reflect this matrix.`
      )
      setShowPricingModal(false)
      await refreshData()
    } catch (err) {
      setError(err.message || 'Failed to save pricing configuration')
    } finally {
      setActionLoading(false)
    }
  }

  // Fallback for non-admin user
  if (!isAdmin) {
    return (
      <div className="dashboard-page">
        <div className="container py-8">
          <div className="empty-state-card" style={{ maxWidth: '640px', margin: '4rem auto' }}>
            <span className="error-badge">ADMIN ACCESS RESTRICTED</span>
            <h2 className="mt-4">Administrator Authorization Required</h2>
            <p className="error-description mt-2">
              The AutoVista Management Console is reserved for dealership executive administrators.
              Access to inventory mutation, registration databases, and RTO tax calculations requires{' '}
              <code className="text-accent">ROLE_ADMIN</code>.
            </p>
            <div className="mt-6 flex gap-4 justify-center">
              <button
                type="button"
                className="btn btn-accent btn-md"
                onClick={() => loginAsDemo('admin')}
              >
                👑 Sign In as Dealership Admin (admin@autovista.in)
              </button>
            </div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="dashboard-page">
      <div className="container py-6">
        {/* Header Bar */}
        <div className="dashboard-header-bar">
          <div>
            <div className="eyebrow-badge">Executive Control • AutoVista Lucknow</div>
            <h1 className="dashboard-title">Dealership Management Console</h1>
            <p className="dashboard-subtitle">
              Configure live showroom inventory, execute city-specific RTO tax formulas, and manage
              pre-owned vehicle certifications in compliance with Section 206C(1F) of IT Act.
            </p>
          </div>

          <div className="admin-status-pill">
            <span className="pulse-dot" />
            <span>PostgreSQL 17 Live Engine</span>
          </div>
        </div>

        {/* Notifications */}
        {successMsg && (
          <div className="alert-box alert-success mt-4 flex justify-between items-center">
            <span>✓ {successMsg}</span>
            <button
              type="button"
              className="btn btn-link btn-xs"
              onClick={() => setSuccessMsg(null)}
            >
              ✕
            </button>
          </div>
        )}

        {error && (
          <div className="alert-box alert-danger mt-4 flex justify-between items-center">
            <span>⚠ {error}</span>
            <button
              type="button"
              className="btn btn-link btn-xs"
              onClick={() => setError(null)}
            >
              ✕
            </button>
          </div>
        )}

        {/* Quick KPI Cards */}
        <div className="admin-kpi-grid mt-6">
          <div className="kpi-card">
            <span className="kpi-label">New Showroom Inventory</span>
            <span className="kpi-val">{stats?.totalNewModels ?? models.length} Models</span>
            <span className="kpi-sub">
              {stats?.totalNewVariants ?? 0} Active Variants configured
            </span>
          </div>
          <div className="kpi-card">
            <span className="kpi-label">Certified Pre-Owned</span>
            <span className="kpi-val">{stats?.totalUsedCars ?? usedCars.length} Vehicles</span>
            <span className="kpi-sub">
              {stats?.usedAvailable ?? 0} Available • {stats?.usedBooked ?? 0} Booked •{' '}
              {stats?.usedSold ?? 0} Sold
            </span>
          </div>
          <div className="kpi-card">
            <span className="kpi-label">Pricing Matrix</span>
            <span className="kpi-val">
              {stats?.totalPricingRules ?? pricingRules.length} Rules Active
            </span>
            <span className="kpi-sub">Database-managed RTO & TCS formulas</span>
          </div>
          <div className="kpi-card">
            <span className="kpi-label">Test Drive Bookings</span>
            <span className="kpi-val">{stats?.totalBookings ?? bookings.length} Total</span>
            <span className="kpi-sub">
              {stats?.confirmedBookings ?? 0} Confirmed • Duplicate-Free
            </span>
          </div>
          <div className="kpi-card">
            <span className="kpi-label">Customer Enquiries</span>
            <span className="kpi-val">{leadOverview?.totalLeads ?? leads.length} Total</span>
            <span className="kpi-sub">
              {leadOverview?.newLeads ?? 0} New • {leadOverview?.convertedLeads ?? 0} Converted • {leadOverview?.unassignedLeads ?? 0} Unassigned
            </span>
          </div>
        </div>

        {/* Tab Navigation */}
        <div className="admin-tabs mt-8">
          <button
            type="button"
            className={`admin-tab-btn ${activeTab === 'inventory' ? 'active' : ''}`}
            onClick={() => setActiveTab('inventory')}
          >
            🚗 New Car Models & Variants ({models.length})
          </button>
          <button
            type="button"
            className={`admin-tab-btn ${activeTab === 'used' ? 'active' : ''}`}
            onClick={() => setActiveTab('used')}
          >
            🚙 Certified Pre-Owned ({usedCars.length})
          </button>
          <button
            type="button"
            className={`admin-tab-btn ${activeTab === 'pricing' ? 'active' : ''}`}
            onClick={() => setActiveTab('pricing')}
          >
            📊 City-Wise Pricing Engine ({pricingRules.length})
          </button>
          <button
            type="button"
            className={`admin-tab-btn ${activeTab === 'bookings' ? 'active' : ''}`}
            onClick={() => setActiveTab('bookings')}
          >
            📅 Bookings & SLA Engine ({bookings.length})
          </button>
          <button
            type="button"
            className={`admin-tab-btn ${activeTab === 'leads' ? 'active' : ''}`}
            onClick={() => setActiveTab('leads')}
          >
            📋 Enquiries & AI Leads ({leads.length})
          </button>
        </div>

        {/* Loading state */}
        {loading && (
          <div className="loading-state py-8">
            <div className="loading-spinner" />
            <p>Loading real dealership database records...</p>
          </div>
        )}

        {/* Tab 1: New Car Models & Variants */}
        {!loading && activeTab === 'inventory' && (
          <div className="admin-table-container mt-4">
            <div className="admin-action-bar">
              <div>
                <h3>New Car Inventory Catalog</h3>
                <span className="table-caption">
                  Active models and variants are immediately discoverable on public customer pages.
                </span>
              </div>
              <button
                type="button"
                className="btn btn-primary btn-sm"
                onClick={() => setShowModelModal(true)}
                disabled={actionLoading}
              >
                + Add New Car Model
              </button>
            </div>

            <div className="responsive-table-scroll">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>Model & Brand</th>
                    <th>Body Type</th>
                    <th>Year</th>
                    <th>Variants Available</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {models.map((m) => (
                    <tr key={m.id}>
                      <td>
                        <strong>{m.brand} {m.modelName}</strong>
                      </td>
                      <td>{m.bodyType}</td>
                      <td>{m.year}</td>
                      <td>
                        <div className="flex flex-col gap-1">
                          {m.variants && m.variants.length > 0 ? (
                            m.variants.map((v) => (
                              <div
                                key={v.id}
                                className="flex items-center justify-between text-xs font-mono py-1 px-2 rounded bg-surface-raised mb-1"
                              >
                                <span>
                                  <strong>{v.variantName}</strong> ({v.fuelType}/{v.transmission}) —{' '}
                                  {formatINR(v.exShowroomPrice)}
                                </span>
                                <button
                                  type="button"
                                  className={`btn btn-xs ${v.isAvailable ? 'btn-outline' : 'btn-secondary'}`}
                                  onClick={() => handleToggleVariant(v.id, v.isAvailable)}
                                  disabled={actionLoading}
                                >
                                  {v.isAvailable ? 'Deactivate' : 'Activate'}
                                </button>
                              </div>
                            ))
                          ) : (
                            <span className="text-muted text-xs">No variants configured yet</span>
                          )}
                        </div>
                      </td>
                      <td>
                        <Badge variant={m.isActive ? 'success' : 'secondary'}>
                          {m.isActive ? 'ACTIVE' : 'INACTIVE'}
                        </Badge>
                      </td>
                      <td>
                        <div className="flex gap-2">
                          <button
                            type="button"
                            className="btn btn-outline btn-xs"
                            onClick={() => {
                              setSelectedModelForVariant(m)
                              setShowVariantModal(true)
                            }}
                            disabled={actionLoading}
                          >
                            + Variant
                          </button>
                          <button
                            type="button"
                            className={`btn btn-xs ${m.isActive ? 'btn-secondary' : 'btn-accent'}`}
                            onClick={() => handleToggleModel(m.id, m.isActive)}
                            disabled={actionLoading}
                          >
                            {m.isActive ? 'Deactivate' : 'Activate'}
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                  {models.length === 0 && (
                    <tr>
                      <td colSpan="6" className="text-center py-6 text-muted">
                        No car models in database. Click "+ Add New Car Model" to seed inventory.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Tab 2: Certified Pre-Owned */}
        {!loading && activeTab === 'used' && (
          <div className="admin-table-container mt-4">
            <div className="admin-action-bar">
              <div>
                <h3>Certified Pre-Owned Fleet Management</h3>
                <span className="table-caption">
                  Raw registration numbers are visible exclusively to authorized administrators.
                  Customer endpoints strictly return masked numbers per Requirement #7.
                </span>
              </div>
              <button
                type="button"
                className="btn btn-primary btn-sm"
                onClick={() => setShowUsedCarModal(true)}
                disabled={actionLoading}
              >
                + Add Pre-Owned Car
              </button>
            </div>

            <div className="responsive-table-scroll">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>Vehicle</th>
                    <th>Year & Fuel</th>
                    <th>Admin Plate (Raw)</th>
                    <th>Customer Plate (Masked)</th>
                    <th>Odometer</th>
                    <th>Price</th>
                    <th>Status</th>
                    <th>Quick Status Update</th>
                  </tr>
                </thead>
                <tbody>
                  {usedCars.map((car) => (
                    <tr key={car.id}>
                      <td>
                        <strong>{car.brand} {car.modelName} {car.variantName}</strong>
                      </td>
                      <td>
                        {car.manufacturingYear} • {car.fuelType} ({car.transmission})
                      </td>
                      <td className="font-mono text-warning">
                        {car.rawRegistrationNumber || 'N/A'}
                      </td>
                      <td className="font-mono text-accent">
                        {car.maskedRegistrationNumber}
                      </td>
                      <td>{car.odometerKm?.toLocaleString('en-IN')} km</td>
                      <td className="font-mono">{formatINR(car.price)}</td>
                      <td>
                        <Badge
                          variant={
                            car.status === 'AVAILABLE'
                              ? 'success'
                              : car.status === 'BOOKED'
                              ? 'warning'
                              : 'danger'
                          }
                        >
                          {car.status}
                        </Badge>
                      </td>
                      <td>
                        <select
                          className="form-input text-xs py-1 px-2"
                          value={car.status}
                          onChange={(e) => handleUsedCarStatusChange(car.id, e.target.value)}
                          disabled={actionLoading}
                          style={{ minWidth: '120px' }}
                        >
                          <option value="AVAILABLE">AVAILABLE</option>
                          <option value="BOOKED">BOOKED</option>
                          <option value="SOLD">SOLD</option>
                        </select>
                      </td>
                    </tr>
                  ))}
                  {usedCars.length === 0 && (
                    <tr>
                      <td colSpan="8" className="text-center py-6 text-muted">
                        No pre-owned vehicles found in database.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Tab 3: City-Wise Pricing Engine */}
        {!loading && activeTab === 'pricing' && (
          <div className="admin-table-container mt-4">
            <div className="admin-action-bar">
              <div>
                <h3>City-Wise On-Road Pricing Matrix (PostgreSQL Engine)</h3>
                <span className="table-caption">
                  Mandatory Requirements #1 & #2: Backend computes road tax, insurance, standard
                  accessories, handling, TCS (1% for &gt; ₹10L), and total on-road price.
                </span>
              </div>
              <button
                type="button"
                className="btn btn-primary btn-sm"
                onClick={() => {
                  if (models.length > 0 && models[0].variants?.length > 0) {
                    setPricingForm((prev) => ({
                      ...prev,
                      carVariantId: models[0].variants[0].id,
                    }))
                  }
                  setShowPricingModal(true)
                }}
                disabled={actionLoading}
              >
                + Configure City Rule
              </button>
            </div>

            <div className="responsive-table-scroll">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>Jurisdiction</th>
                    <th>Vehicle Variant</th>
                    <th>Ex-Showroom</th>
                    <th>RTO Tax</th>
                    <th>Insurance</th>
                    <th>Accessories</th>
                    <th>TCS (1% &gt; ₹10L)</th>
                    <th>Total On-Road Price</th>
                    <th>Valid Until</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {pricingRules.map((rule) => (
                    <tr key={rule.id}>
                      <td>
                        <strong>{rule.city}</strong> ({rule.state})
                      </td>
                      <td>{rule.variantName || `Variant #${rule.carVariantId}`}</td>
                      <td className="font-mono">{formatINR(rule.exShowroomPrice)}</td>
                      <td className="font-mono">
                        {rule.rtoTaxPercent}% ({formatINR(rule.rtoTaxAmount)})
                      </td>
                      <td className="font-mono">{formatINR(rule.insuranceAmount)}</td>
                      <td className="font-mono">{formatINR(rule.accessoriesAmount)}</td>
                      <td className="font-mono text-warning">
                        {rule.tcsAmount && Number(rule.tcsAmount) > 0
                          ? formatINR(rule.tcsAmount)
                          : '₹0 (Exempt)'}
                      </td>
                      <td className="font-mono text-accent">
                        <strong>{formatINR(rule.totalOnRoadPrice)}</strong>
                      </td>
                      <td>{rule.priceValidUntil}</td>
                      <td>
                        <button
                          type="button"
                          className="btn btn-outline btn-xs"
                          onClick={() => {
                            setPricingForm({
                              carVariantId: rule.carVariantId,
                              city: rule.city,
                              state: rule.state,
                              rtoTaxPercent: rule.rtoTaxPercent,
                              insuranceAmount: rule.insuranceAmount,
                              accessoriesAmount: rule.accessoriesAmount,
                              handlingCharges: rule.handlingCharges || 0,
                              priceValidUntil: rule.priceValidUntil,
                              disclaimer: rule.disclaimer,
                            })
                            setShowPricingModal(true)
                          }}
                          disabled={actionLoading}
                        >
                          Edit / Recalculate
                        </button>
                      </td>
                    </tr>
                  ))}
                  {pricingRules.length === 0 && (
                    <tr>
                      <td colSpan="10" className="text-center py-6 text-muted">
                        No city pricing matrices saved in PostgreSQL. Click "+ Configure City Rule".
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Tab 4: Bookings & SLA Engine (Phase 5 Live) */}
        {!loading && activeTab === 'bookings' && (
          <div className="admin-table-container mt-4">
            <div className="table-header-wrap">
              <div>
                <h3>VIP Test Drive Bookings & Lead Allocation Pipeline</h3>
                <span className="table-caption">
                  Automated PostgreSQL constraint <code>uq_test_drive_duplicate_slot</code> guarantees 100% duplicate-free scheduling. Live records.
                </span>
              </div>
            </div>

            {/* Filter Bar */}
            <div className="flex flex-wrap gap-3 items-end p-4 mb-4 rounded-xl bg-surface-raised border border-subtle">
              <div className="form-group mb-0">
                <label className="form-label text-xs">Filter by Date</label>
                <input
                  type="date"
                  className="form-input form-input-sm"
                  value={bookingFilters.date}
                  onChange={(e) => handleBookingFilterChange('date', e.target.value)}
                />
              </div>

              <div className="form-group mb-0">
                <label className="form-label text-xs">Filter by Showroom</label>
                <select
                  className="form-select form-select-sm"
                  value={bookingFilters.showroomId}
                  onChange={(e) => handleBookingFilterChange('showroomId', e.target.value)}
                >
                  <option value="">All Showrooms</option>
                  <option value="1">AutoVista Gomti Nagar Flagship</option>
                </select>
              </div>

              <div className="form-group mb-0">
                <label className="form-label text-xs">Filter by Status</label>
                <select
                  className="form-select form-select-sm"
                  value={bookingFilters.status}
                  onChange={(e) => handleBookingFilterChange('status', e.target.value)}
                >
                  <option value="ALL">All Statuses</option>
                  <option value="CONFIRMED">CONFIRMED (Slot Locked)</option>
                  <option value="COMPLETED">COMPLETED</option>
                  <option value="CANCELLED">CANCELLED (Slot Released)</option>
                  <option value="REJECTED">REJECTED (Slot Released)</option>
                </select>
              </div>

              <button
                type="button"
                className="btn btn-secondary btn-sm"
                onClick={handleResetBookingFilters}
                disabled={actionLoading}
              >
                Reset Filters
              </button>

              <button
                type="button"
                className="btn btn-outline btn-sm"
                onClick={refreshData}
                disabled={actionLoading}
                style={{ marginLeft: 'auto' }}
              >
                🔄 Refresh Live Records
              </button>
            </div>

            <div className="responsive-table-scroll mt-4">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>Booking Ref</th>
                    <th>Customer Name</th>
                    <th>Vehicle Requested</th>
                    <th>Date & Slot</th>
                    <th>Format / Mode</th>
                    <th>Status</th>
                    <th>Actions & Audit</th>
                  </tr>
                </thead>
                <tbody>
                  {bookings.map((b) => (
                    <tr key={b.id}>
                      <td className="font-mono">
                        <strong className="text-accent">{b.bookingRef}</strong>
                        <div className="text-muted text-xs">
                          {b.createdAt ? new Date(b.createdAt).toLocaleDateString() : ''}
                        </div>
                      </td>
                      <td>
                        <strong>{b.customerName}</strong>
                        <div className="text-muted text-xs">{b.customerPhone}</div>
                        <div className="text-muted text-xs">{b.customerEmail}</div>
                      </td>
                      <td>
                        <div>
                          <strong>
                            {b.vehicleDisplayName ||
                              `${b.vehicleBrand || ''} ${b.vehicleModel || ''} ${b.vehicleVariant || ''}`.trim()}
                          </strong>
                        </div>
                        <Badge
                          variant={b.vehicleType === 'NEW' ? 'primary' : 'warning'}
                          className="text-xs mt-1"
                        >
                          {b.vehicleType === 'NEW' ? 'New Showroom Variant' : 'Certified Pre-Owned'}
                        </Badge>
                      </td>
                      <td>
                        <strong>{b.bookingDate}</strong>
                        <div className="font-mono text-xs text-accent">{b.timeSlot}</div>
                        <div className="text-muted text-xs">
                          {b.showroomName || 'AutoVista Gomti Nagar'}
                        </div>
                      </td>
                      <td>
                        <Badge variant={b.driveMode === 'DOORSTEP' ? 'info' : 'secondary'}>
                          {b.driveMode === 'DOORSTEP' ? '🏠 Doorstep VIP' : '🏢 Showroom Visit'}
                        </Badge>
                        {b.doorstepAddress && (
                          <div
                            className="text-muted text-xs mt-1 max-w-xs truncate"
                            title={b.doorstepAddress}
                          >
                            {b.doorstepAddress}
                          </div>
                        )}
                      </td>
                      <td>
                        {b.status === 'CONFIRMED' && (
                          <Badge variant="success">CONFIRMED (Slot Locked)</Badge>
                        )}
                        {b.status === 'COMPLETED' && <Badge variant="primary">COMPLETED</Badge>}
                        {b.status === 'CANCELLED' && (
                          <Badge variant="secondary">CANCELLED</Badge>
                        )}
                        {b.status === 'REJECTED' && <Badge variant="danger">REJECTED</Badge>}
                      </td>
                      <td>
                        <div className="flex flex-col gap-1">
                          <button
                            type="button"
                            className="btn btn-outline btn-xs"
                            onClick={() => handleOpenNotificationsModal(b)}
                          >
                            ✉ Audit Logs ({b.notifications ? b.notifications.length : 'View'})
                          </button>

                          <div className="flex gap-1 mt-1">
                            {b.status === 'CONFIRMED' && (
                              <>
                                <button
                                  type="button"
                                  className="btn btn-primary btn-xs"
                                  onClick={() => {
                                    setStatusModalBooking(b)
                                    setStatusModalNewStatus('COMPLETED')
                                    setStatusModalNotes('')
                                  }}
                                  disabled={actionLoading}
                                >
                                  Complete
                                </button>
                                <button
                                  type="button"
                                  className="btn btn-outline btn-xs text-danger"
                                  onClick={() => {
                                    setStatusModalBooking(b)
                                    setStatusModalNewStatus('CANCELLED')
                                    setStatusModalNotes('Customer requested cancellation')
                                  }}
                                  disabled={actionLoading}
                                >
                                  Cancel
                                </button>
                              </>
                            )}
                            {(b.status === 'CANCELLED' || b.status === 'REJECTED') && (
                              <button
                                type="button"
                                className="btn btn-outline btn-xs text-accent"
                                onClick={() =>
                                  handleUpdateBookingStatus(
                                    b.id,
                                    'CONFIRMED',
                                    'Re-instated by admin'
                                  )
                                }
                                disabled={actionLoading}
                              >
                                Re-confirm
                              </button>
                            )}
                          </div>
                        </div>
                      </td>
                    </tr>
                  ))}
                  {bookings.length === 0 && (
                    <tr>
                      <td colSpan="7" className="text-center py-8 text-muted">
                        No test drive bookings match the current criteria in PostgreSQL.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* ================= TAB 5: LEAD MANAGEMENT & AI WORKSPACE (PHASE 6) ================= */}
        {activeTab === 'leads' && (
          <div className="admin-tab-content">
            <div className="admin-section-header">
              <div>
                <h2>Dealership Enquiries & AI Lead Pipeline</h2>
                <p className="text-secondary text-sm">
                  Round-robin assignment within 5-min SLA, Gemini AI buyer summary synthesis, and verified customer communication audits.
                </p>
              </div>
              <button
                type="button"
                className="btn btn-outline btn-sm"
                onClick={() => refreshData()}
              >
                ↻ Refresh Live Pipeline
              </button>
            </div>

            {/* Consultant Workloads & SLA Health */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-6">
              {leadOverview?.consultantWorkloads?.map((cw) => (
                <div key={cw.consultantId} className="kpi-card flex items-center justify-between">
                  <div>
                    <span className="kpi-label">Sales Consultant</span>
                    <strong className="text-white block mt-1">{cw.name || cw.consultantName}</strong>
                    <span className="text-xs text-muted font-mono">{cw.email || cw.consultantEmail}</span>
                  </div>
                  <div className="text-right">
                    <span className="text-xs text-muted uppercase block">Active Leads</span>
                    <span className="text-2xl font-bold font-mono text-cyan-400">
                      {cw.assignedLeadsCount ?? cw.activeLeadsCount ?? 0}
                    </span>
                  </div>
                </div>
              ))}

              <div className="kpi-card flex items-center justify-between">
                <div>
                  <span className="kpi-label">Lead Pipeline Health</span>
                  <strong className="text-white block mt-1">
                    {leadOverview?.unassignedLeads > 0 ? (
                      <span className="text-amber-400">⚠️ {leadOverview.unassignedLeads} Unassigned</span>
                    ) : (
                      <span className="text-emerald-400">✓ All Leads Assigned</span>
                    )}
                  </strong>
                  <span className="text-xs text-muted">
                    {leadOverview?.convertedLeads || 0} Converted • {leadOverview?.lostLeads || 0} Lost
                  </span>
                </div>
                <div className="text-right">
                  <span className="text-xs text-muted uppercase block">Total Volume</span>
                  <span className="text-2xl font-bold font-mono text-accent">
                    {leadOverview?.totalLeads || leads.length}
                  </span>
                </div>
              </div>
            </div>

            {/* Filters Bar */}
            <div className="admin-filters-card mt-6">
              <div className="flex flex-wrap gap-4 items-center justify-between">
                <div className="flex flex-wrap gap-3 items-center flex-1">
                  <input
                    type="text"
                    placeholder="Search customer, phone, lead ref..."
                    className="form-input text-xs w-64"
                    value={leadFilters.search}
                    onChange={(e) => handleLeadFilterChange('search', e.target.value)}
                  />

                  <select
                    className="form-select text-xs w-44"
                    value={leadFilters.status}
                    onChange={(e) => handleLeadFilterChange('status', e.target.value)}
                  >
                    <option value="ALL">All Statuses</option>
                    <option value="NEW">NEW</option>
                    <option value="CONTACTED">CONTACTED</option>
                    <option value="FOLLOW_UP">FOLLOW_UP</option>
                    <option value="TEST_DRIVE_SCHEDULED">TEST_DRIVE_SCHEDULED</option>
                    <option value="CONVERTED">CONVERTED</option>
                    <option value="LOST">LOST</option>
                  </select>

                  <select
                    className="form-select text-xs w-52"
                    value={leadFilters.consultantId}
                    onChange={(e) => handleLeadFilterChange('consultantId', e.target.value)}
                  >
                    <option value="">All Consultants</option>
                    {leadOverview?.consultantWorkloads?.map((cw) => (
                      <option key={cw.consultantId} value={cw.consultantId}>
                        {cw.name || cw.consultantName}
                      </option>
                    ))}
                  </select>

                  <label className="flex items-center gap-2 text-xs text-secondary cursor-pointer">
                    <input
                      type="checkbox"
                      checked={leadFilters.unassignedOnly}
                      onChange={(e) => handleLeadFilterChange('unassignedOnly', e.target.checked)}
                      className="rounded border-neutral-700"
                    />
                    <span>Unassigned Only</span>
                  </label>
                </div>

                <button
                  type="button"
                  className="btn btn-outline btn-xs"
                  onClick={handleResetLeadFilters}
                >
                  Reset Filters
                </button>
              </div>
            </div>

            {/* Leads Table */}
            <div className="admin-table-container mt-4">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>Lead Reference</th>
                    <th>Customer Dossier</th>
                    <th>Vehicle Interest</th>
                    <th>Assigned Consultant</th>
                    <th>Status</th>
                    <th>Consent</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {leads.map((lead) => {
                    const carDisplay = lead.carVariantName
                      ? `${lead.carBrand || ''} ${lead.carModelName || ''} - ${lead.carVariantName}`
                      : lead.usedCarTitle || 'Dealership Consultation'
                    const priceDisplay = lead.carExShowroomPrice
                      ? formatINR(lead.carExShowroomPrice)
                      : lead.usedCarPrice
                      ? formatINR(lead.usedCarPrice)
                      : '—'

                    return (
                      <tr key={lead.id}>
                        <td>
                          <span className="font-mono text-xs font-bold text-accent">
                            {lead.leadRef}
                          </span>
                          <span className="text-[11px] text-muted block mt-0.5">
                            {lead.createdAt ? new Date(lead.createdAt).toLocaleDateString() : ''}
                          </span>
                        </td>
                        <td>
                          <strong className="text-white block">{lead.customerName}</strong>
                          <span className="text-xs text-muted block">
                            📞 {lead.customerPhone}
                          </span>
                          <span className="text-[11px] text-muted block">
                            📍 {lead.customerCity || 'Lucknow'}
                          </span>
                        </td>
                        <td>
                          <span className="text-xs text-white block font-medium">
                            {carDisplay}
                          </span>
                          <span className="text-xs font-mono text-cyan-400">
                            {priceDisplay}
                          </span>
                          {lead.statedBudget && (
                            <span className="text-[11px] text-muted block">
                              Budget: {lead.statedBudget}
                            </span>
                          )}
                        </td>
                        <td>
                          {lead.assignedConsultantName ? (
                            <div>
                              <strong className="text-xs text-white block">
                                {lead.assignedConsultantName}
                              </strong>
                              <span className="text-[11px] text-muted font-mono block">
                                {lead.assignedConsultantEmail}
                              </span>
                            </div>
                          ) : (
                            <span className="text-xs font-bold text-amber-400">
                              ⚠️ Unassigned
                            </span>
                          )}
                        </td>
                        <td>
                          <Badge
                            variant={
                              lead.status === 'NEW'
                                ? 'accent'
                                : lead.status === 'CONVERTED'
                                ? 'success'
                                : lead.status === 'LOST'
                                ? 'danger'
                                : 'default'
                            }
                          >
                            {lead.status}
                          </Badge>
                        </td>
                        <td>
                          {lead.optOut ? (
                            <span className="badge badge-danger text-[10px]">Opted Out</span>
                          ) : lead.consentGiven ? (
                            <span className="badge badge-success text-[10px]">Consent Active</span>
                          ) : (
                            <span className="badge badge-subtle text-[10px]">Pending</span>
                          )}
                        </td>
                        <td>
                          <div className="flex items-center gap-2">
                            <button
                              type="button"
                              className="btn btn-outline btn-xs"
                              onClick={() => handleOpenReassignModal(lead)}
                            >
                              Reassign
                            </button>
                            <button
                              type="button"
                              className="btn btn-secondary btn-xs"
                              onClick={() => handleOpenLeadAuditModal(lead)}
                            >
                              Audit Trail
                            </button>
                          </div>
                        </td>
                      </tr>
                    )
                  })}
                  {leads.length === 0 && (
                    <tr>
                      <td colSpan="7" className="text-center py-8 text-muted">
                        No customer enquiries match the selected filters.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* ================= MODAL: ADD CAR MODEL ================= */}
        {showModelModal && (
          <div className="admin-modal-overlay">
            <div className="admin-modal-content">
              <div className="admin-modal-header">
                <h3 className="admin-modal-title">Add New Car Model to Database</h3>
                <button
                  type="button"
                  className="btn btn-link"
                  onClick={() => setShowModelModal(false)}
                >
                  ✕
                </button>
              </div>
              <form onSubmit={handleSaveModel}>
                <div className="admin-form-grid">
                  <div className="form-group">
                    <label className="form-label">Brand</label>
                    <input
                      type="text"
                      className="form-input"
                      value={modelForm.brand}
                      onChange={(e) => setModelForm({ ...modelForm, brand: e.target.value })}
                      placeholder="e.g. AutoVista Flagship"
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Model Name</label>
                    <input
                      type="text"
                      className="form-input"
                      value={modelForm.modelName}
                      onChange={(e) => setModelForm({ ...modelForm, modelName: e.target.value })}
                      placeholder="e.g. Model H"
                      required
                    />
                  </div>
                </div>

                <div className="admin-form-grid mt-4">
                  <div className="form-group">
                    <label className="form-label">Body Type</label>
                    <select
                      className="form-input"
                      value={modelForm.bodyType}
                      onChange={(e) => setModelForm({ ...modelForm, bodyType: e.target.value })}
                    >
                      <option value="Premium Sedan">Premium Sedan</option>
                      <option value="Executive SUV">Executive SUV</option>
                      <option value="Sportback">Sportback</option>
                      <option value="Luxury MPV">Luxury MPV</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Model Year</label>
                    <input
                      type="number"
                      className="form-input"
                      value={modelForm.year}
                      onChange={(e) => setModelForm({ ...modelForm, year: e.target.value })}
                      min="2000"
                      max="2030"
                      required
                    />
                  </div>
                </div>

                <div className="form-group mt-4">
                  <label className="form-label">Hero Image URL</label>
                  <input
                    type="url"
                    className="form-input"
                    value={modelForm.heroImageUrl}
                    onChange={(e) => setModelForm({ ...modelForm, heroImageUrl: e.target.value })}
                    placeholder="https://images.unsplash.com/..."
                  />
                </div>

                <div className="form-group mt-4">
                  <label className="form-label">Executive Description</label>
                  <textarea
                    className="form-input"
                    rows="3"
                    value={modelForm.description}
                    onChange={(e) => setModelForm({ ...modelForm, description: e.target.value })}
                    placeholder="Detailed description of the model design, platform and craftsmanship..."
                  />
                </div>

                <div className="flex justify-end gap-3 mt-6">
                  <button
                    type="button"
                    className="btn btn-secondary btn-sm"
                    onClick={() => setShowModelModal(false)}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="btn btn-primary btn-sm"
                    disabled={actionLoading}
                  >
                    {actionLoading ? 'Saving...' : 'Save Car Model'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* ================= MODAL: ADD VARIANT ================= */}
        {showVariantModal && selectedModelForVariant && (
          <div className="admin-modal-overlay">
            <div className="admin-modal-content">
              <div className="admin-modal-header">
                <h3 className="admin-modal-title">
                  Add Variant to {selectedModelForVariant.brand} {selectedModelForVariant.modelName}
                </h3>
                <button
                  type="button"
                  className="btn btn-link"
                  onClick={() => setShowVariantModal(false)}
                >
                  ✕
                </button>
              </div>
              <form onSubmit={handleSaveVariant}>
                <div className="admin-form-grid">
                  <div className="form-group">
                    <label className="form-label">Variant Name</label>
                    <input
                      type="text"
                      className="form-input"
                      value={variantForm.variantName}
                      onChange={(e) =>
                        setVariantForm({ ...variantForm, variantName: e.target.value })
                      }
                      placeholder="e.g. ZX Automatic Petrol"
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Ex-Showroom Price (₹)</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-input"
                      value={variantForm.exShowroomPrice}
                      onChange={(e) =>
                        setVariantForm({ ...variantForm, exShowroomPrice: e.target.value })
                      }
                      placeholder="e.g. 1245000"
                      required
                    />
                  </div>
                </div>

                <div className="admin-form-grid mt-4">
                  <div className="form-group">
                    <label className="form-label">Fuel Type</label>
                    <select
                      className="form-input"
                      value={variantForm.fuelType}
                      onChange={(e) =>
                        setVariantForm({ ...variantForm, fuelType: e.target.value })
                      }
                    >
                      <option value="PETROL">PETROL</option>
                      <option value="DIESEL">DIESEL</option>
                      <option value="ELECTRIC">ELECTRIC</option>
                      <option value="HYBRID">HYBRID</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Transmission</label>
                    <select
                      className="form-input"
                      value={variantForm.transmission}
                      onChange={(e) =>
                        setVariantForm({ ...variantForm, transmission: e.target.value })
                      }
                    >
                      <option value="AUTOMATIC">AUTOMATIC</option>
                      <option value="MANUAL">MANUAL</option>
                      <option value="CVT">CVT</option>
                      <option value="DCT">DCT</option>
                    </select>
                  </div>
                </div>

                <div className="admin-form-grid mt-4">
                  <div className="form-group">
                    <label className="form-label">Engine Specs</label>
                    <input
                      type="text"
                      className="form-input"
                      value={variantForm.engineSpecs}
                      onChange={(e) =>
                        setVariantForm({ ...variantForm, engineSpecs: e.target.value })
                      }
                      placeholder="e.g. 1.5L i-VTEC DOHC"
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Seating Capacity</label>
                    <input
                      type="number"
                      className="form-input"
                      value={variantForm.seatingCapacity}
                      onChange={(e) =>
                        setVariantForm({ ...variantForm, seatingCapacity: e.target.value })
                      }
                      min="2"
                      max="9"
                    />
                  </div>
                </div>

                <div className="form-group mt-4">
                  <label className="form-label">Marketing Tagline</label>
                  <input
                    type="text"
                    className="form-input"
                    value={variantForm.tagline}
                    onChange={(e) => setVariantForm({ ...variantForm, tagline: e.target.value })}
                    placeholder="e.g. Supreme Elegance with 7-Speed CVT"
                  />
                </div>

                <div className="flex justify-end gap-3 mt-6">
                  <button
                    type="button"
                    className="btn btn-secondary btn-sm"
                    onClick={() => setShowVariantModal(false)}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="btn btn-primary btn-sm"
                    disabled={actionLoading}
                  >
                    {actionLoading ? 'Saving...' : 'Save Variant'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* ================= MODAL: ADD PRE-OWNED CAR ================= */}
        {showUsedCarModal && (
          <div className="admin-modal-overlay">
            <div className="admin-modal-content">
              <div className="admin-modal-header">
                <h3 className="admin-modal-title">Certify Pre-Owned Vehicle (PostgreSQL)</h3>
                <button
                  type="button"
                  className="btn btn-link"
                  onClick={() => setShowUsedCarModal(false)}
                >
                  ✕
                </button>
              </div>
              <form onSubmit={handleSaveUsedCar}>
                <div className="admin-form-grid">
                  <div className="form-group">
                    <label className="form-label">Brand</label>
                    <input
                      type="text"
                      className="form-input"
                      value={usedCarForm.brand}
                      onChange={(e) => setUsedCarForm({ ...usedCarForm, brand: e.target.value })}
                      placeholder="e.g. Honda"
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Model Name</label>
                    <input
                      type="text"
                      className="form-input"
                      value={usedCarForm.modelName}
                      onChange={(e) => setUsedCarForm({ ...usedCarForm, modelName: e.target.value })}
                      placeholder="e.g. City"
                      required
                    />
                  </div>
                </div>

                <div className="admin-form-grid mt-4">
                  <div className="form-group">
                    <label className="form-label">Variant Name</label>
                    <input
                      type="text"
                      className="form-input"
                      value={usedCarForm.variantName}
                      onChange={(e) => setUsedCarForm({ ...usedCarForm, variantName: e.target.value })}
                      placeholder="e.g. VX CVT"
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Year of Manufacture</label>
                    <input
                      type="number"
                      className="form-input"
                      value={usedCarForm.manufacturingYear}
                      onChange={(e) =>
                        setUsedCarForm({ ...usedCarForm, manufacturingYear: e.target.value })
                      }
                      min="2000"
                      max="2026"
                      required
                    />
                  </div>
                </div>

                <div className="admin-form-grid mt-4">
                  <div className="form-group">
                    <label className="form-label">Certified Dealership Price (₹)</label>
                    <input
                      type="number"
                      className="form-input"
                      value={usedCarForm.price}
                      onChange={(e) => setUsedCarForm({ ...usedCarForm, price: e.target.value })}
                      placeholder="e.g. 985000"
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Odometer Reading (km)</label>
                    <input
                      type="number"
                      className="form-input"
                      value={usedCarForm.odometerKm}
                      onChange={(e) => setUsedCarForm({ ...usedCarForm, odometerKm: e.target.value })}
                      placeholder="e.g. 24500"
                      required
                    />
                  </div>
                </div>

                <div className="admin-form-grid mt-4">
                  <div className="form-group">
                    <label className="form-label">
                      Raw Registration Number (Masked on Customer APIs)
                    </label>
                    <input
                      type="text"
                      className="form-input font-mono"
                      value={usedCarForm.rawRegistrationNumber}
                      onChange={(e) =>
                        setUsedCarForm({ ...usedCarForm, rawRegistrationNumber: e.target.value })
                      }
                      placeholder="e.g. UP32 EA 9821"
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Fuel Type</label>
                    <select
                      className="form-input"
                      value={usedCarForm.fuelType}
                      onChange={(e) => setUsedCarForm({ ...usedCarForm, fuelType: e.target.value })}
                    >
                      <option value="PETROL">PETROL</option>
                      <option value="DIESEL">DIESEL</option>
                      <option value="ELECTRIC">ELECTRIC</option>
                      <option value="HYBRID">HYBRID</option>
                    </select>
                  </div>
                </div>

                <div className="flex justify-end gap-3 mt-6">
                  <button
                    type="button"
                    className="btn btn-secondary btn-sm"
                    onClick={() => setShowUsedCarModal(false)}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="btn btn-primary btn-sm"
                    disabled={actionLoading}
                  >
                    {actionLoading ? 'Saving...' : 'Add Pre-Owned Vehicle'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* ================= MODAL: CONFIGURE CITY PRICING ================= */}
        {showPricingModal && (
          <div className="admin-modal-overlay">
            <div className="admin-modal-content">
              <div className="admin-modal-header">
                <h3 className="admin-modal-title">Configure City On-Road Pricing Engine</h3>
                <button
                  type="button"
                  className="btn btn-link"
                  onClick={() => setShowPricingModal(false)}
                >
                  ✕
                </button>
              </div>
              <form onSubmit={handleSavePricing}>
                <div className="admin-form-grid">
                  <div className="form-group">
                    <label className="form-label">Target Vehicle Variant</label>
                    <select
                      className="form-input"
                      value={pricingForm.carVariantId}
                      onChange={(e) =>
                        setPricingForm({ ...pricingForm, carVariantId: e.target.value })
                      }
                      required
                    >
                      <option value="">Select vehicle variant...</option>
                      {models.flatMap((m) =>
                        (m.variants || []).map((v) => (
                          <option key={v.id} value={v.id}>
                            {m.brand} {m.modelName} – {v.variantName} ({formatINR(v.exShowroomPrice)})
                          </option>
                        ))
                      )}
                    </select>
                  </div>

                  <div className="form-group">
                    <label className="form-label">Jurisdiction City (Uttar Pradesh)</label>
                    <select
                      className="form-input"
                      value={pricingForm.city}
                      onChange={(e) => setPricingForm({ ...pricingForm, city: e.target.value })}
                    >
                      <option value="Lucknow">Lucknow</option>
                      <option value="Kanpur">Kanpur</option>
                      <option value="Varanasi">Varanasi</option>
                      <option value="Prayagraj">Prayagraj</option>
                      <option value="Noida">Noida</option>
                      <option value="Agra">Agra</option>
                    </select>
                  </div>
                </div>

                <div className="admin-form-grid mt-4">
                  <div className="form-group">
                    <label className="form-label">RTO Road Tax Rate (%)</label>
                    <input
                      type="number"
                      step="0.1"
                      className="form-input"
                      value={pricingForm.rtoTaxPercent}
                      onChange={(e) =>
                        setPricingForm({ ...pricingForm, rtoTaxPercent: e.target.value })
                      }
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Comprehensive Insurance (₹)</label>
                    <input
                      type="number"
                      step="1"
                      className="form-input"
                      value={pricingForm.insuranceAmount}
                      onChange={(e) =>
                        setPricingForm({ ...pricingForm, insuranceAmount: e.target.value })
                      }
                      required
                    />
                  </div>
                </div>

                <div className="admin-form-grid mt-4">
                  <div className="form-group">
                    <label className="form-label">Standard Accessories Package (₹)</label>
                    <input
                      type="number"
                      step="1"
                      className="form-input"
                      value={pricingForm.accessoriesAmount}
                      onChange={(e) =>
                        setPricingForm({ ...pricingForm, accessoriesAmount: e.target.value })
                      }
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Handling & Logistics Charges (₹)</label>
                    <input
                      type="number"
                      step="1"
                      className="form-input"
                      value={pricingForm.handlingCharges}
                      onChange={(e) =>
                        setPricingForm({ ...pricingForm, handlingCharges: e.target.value })
                      }
                      required
                    />
                  </div>
                </div>

                <div className="form-group mt-4">
                  <label className="form-label">Price Valid Until (Mandatory Requirement #3)</label>
                  <input
                    type="date"
                    className="form-input"
                    value={pricingForm.priceValidUntil}
                    onChange={(e) =>
                      setPricingForm({ ...pricingForm, priceValidUntil: e.target.value })
                    }
                    required
                  />
                </div>

                <div className="form-group mt-4">
                  <label className="form-label">Mandatory Price Disclaimer</label>
                  <textarea
                    className="form-input"
                    rows="2"
                    value={pricingForm.disclaimer}
                    onChange={(e) => setPricingForm({ ...pricingForm, disclaimer: e.target.value })}
                    required
                  />
                </div>

                <div className="flex justify-end gap-3 mt-6">
                  <button
                    type="button"
                    className="btn btn-secondary btn-sm"
                    onClick={() => setShowPricingModal(false)}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="btn btn-primary btn-sm"
                    disabled={actionLoading}
                  >
                    {actionLoading ? 'Calculating...' : 'Recalculate & Save to PostgreSQL'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* ================= MODAL: AUDIT NOTIFICATIONS (Requirement #7) ================= */}
        {selectedBookingForNotifications && (
          <div className="admin-modal-overlay">
            <div className="admin-modal-content" style={{ maxWidth: '780px' }}>
              <div className="admin-modal-header">
                <div>
                  <h3 className="admin-modal-title">
                    Multi-Channel Notification Audit: {selectedBookingForNotifications.bookingRef}
                  </h3>
                  <p className="text-muted text-xs mt-1">
                    Simulated dispatch events (SMS, Email, WhatsApp) persisted in PostgreSQL table <code>mock_notifications</code>.
                  </p>
                </div>
                <button
                  type="button"
                  className="btn btn-link"
                  onClick={() => setSelectedBookingForNotifications(null)}
                >
                  ✕
                </button>
              </div>

              {loadingNotifications ? (
                <div className="loading-state py-6">
                  <div className="loading-spinner" />
                  <p>Fetching notification delivery audits...</p>
                </div>
              ) : (
                <div className="flex flex-col gap-3 max-h-96 overflow-y-auto pr-1">
                  {bookingNotifications.length === 0 ? (
                    <div className="text-center py-6 text-muted">
                      No notifications logged for this booking.
                    </div>
                  ) : (
                    bookingNotifications.map((notif) => (
                      <div
                        key={notif.id}
                        className="p-3 rounded-lg bg-surface-raised border border-subtle flex flex-col gap-2"
                      >
                        <div className="flex items-center justify-between">
                          <div className="flex items-center gap-2">
                            <span className="font-mono text-xs px-2 py-0.5 rounded bg-surface border border-subtle">
                              {notif.channel === 'SMS' && '📱 SMS'}
                              {notif.channel === 'EMAIL' && '📧 EMAIL'}
                              {notif.channel === 'WHATSAPP' && '💬 WHATSAPP'}
                            </span>
                            <Badge variant="success" className="text-xs">
                              {notif.status}
                            </Badge>
                            <span className="text-muted text-xs font-mono">
                              {notif.notificationType}
                            </span>
                          </div>
                          <span className="text-muted text-xs">
                            {notif.sentAt ? new Date(notif.sentAt).toLocaleString() : ''}
                          </span>
                        </div>

                        <div>
                          <strong>{notif.subject}</strong>
                          <div className="text-muted text-xs">
                            To: {notif.recipientName} ({notif.channel === 'EMAIL' ? notif.recipientEmail : notif.recipientPhone})
                          </div>
                        </div>

                        <div className="text-xs font-mono p-2 rounded bg-surface border border-subtle text-secondary whitespace-pre-wrap">
                          {notif.messageBody}
                        </div>
                      </div>
                    ))
                  )}
                </div>
              )}

              <div className="flex justify-end gap-3 mt-6">
                <button
                  type="button"
                  className="btn btn-secondary btn-sm"
                  onClick={() => setSelectedBookingForNotifications(null)}
                >
                  Close
                </button>
              </div>
            </div>
          </div>
        )}

        {/* ================= MODAL: UPDATE BOOKING STATUS ================= */}
        {statusModalBooking && (
          <div className="admin-modal-overlay">
            <div className="admin-modal-content" style={{ maxWidth: '520px' }}>
              <div className="admin-modal-header">
                <div>
                  <h3 className="admin-modal-title">
                    Update Booking Status: {statusModalBooking.bookingRef}
                  </h3>
                  <p className="text-muted text-xs mt-1">
                    Customer: {statusModalBooking.customerName} • {statusModalBooking.bookingDate} {statusModalBooking.timeSlot}
                  </p>
                </div>
                <button
                  type="button"
                  className="btn btn-link"
                  onClick={() => setStatusModalBooking(null)}
                >
                  ✕
                </button>
              </div>

              <div className="flex flex-col gap-4 mt-2">
                <div className="form-group">
                  <label className="form-label">New Status</label>
                  <select
                    className="form-select"
                    value={statusModalNewStatus}
                    onChange={(e) => setStatusModalNewStatus(e.target.value)}
                  >
                    <option value="CONFIRMED">CONFIRMED (Keep Slot Locked)</option>
                    <option value="COMPLETED">COMPLETED (Test Drive Finished)</option>
                    <option value="CANCELLED">CANCELLED (Release Slot for Re-Booking)</option>
                    <option value="REJECTED">REJECTED (Release Slot for Re-Booking)</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Notes / Reason (Recorded in audit)</label>
                  <input
                    type="text"
                    className="form-input"
                    value={statusModalNotes}
                    onChange={(e) => setStatusModalNotes(e.target.value)}
                    placeholder="e.g. VIP test drive completed successfully"
                  />
                </div>
              </div>

              <div className="flex justify-end gap-3 mt-6">
                <button
                  type="button"
                  className="btn btn-secondary btn-sm"
                  onClick={() => setStatusModalBooking(null)}
                  disabled={actionLoading}
                >
                  Cancel
                </button>
                <button
                  type="button"
                  className="btn btn-primary btn-sm"
                  onClick={() =>
                    handleUpdateBookingStatus(
                      statusModalBooking.id,
                      statusModalNewStatus,
                      statusModalNotes
                    )
                  }
                  disabled={actionLoading}
                >
                  {actionLoading ? 'Updating...' : `Confirm Status → ${statusModalNewStatus}`}
                </button>
              </div>
            </div>
          </div>
        )}

        {/* ================= MODAL: REASSIGN LEAD ================= */}
        {selectedLeadForReassign && (
          <div className="admin-modal-overlay">
            <div className="admin-modal-content max-w-md">
              <div className="admin-modal-header">
                <h3 className="admin-modal-title">Reassign Lead to Consultant</h3>
                <button
                  type="button"
                  className="btn btn-link"
                  onClick={() => setSelectedLeadForReassign(null)}
                >
                  ✕
                </button>
              </div>
              <form onSubmit={handleExecuteReassign}>
                <div className="p-3 bg-neutral-900 border border-neutral-800 rounded-lg text-xs mb-4">
                  <div className="flex justify-between">
                    <span className="text-muted">Lead Reference:</span>
                    <span className="font-mono text-accent font-bold">
                      {selectedLeadForReassign.leadRef}
                    </span>
                  </div>
                  <div className="flex justify-between mt-1">
                    <span className="text-muted">Customer:</span>
                    <span className="text-white">{selectedLeadForReassign.customerName}</span>
                  </div>
                  <div className="flex justify-between mt-1">
                    <span className="text-muted">Currently Assigned:</span>
                    <span className="text-white">
                      {selectedLeadForReassign.assignedConsultantName || 'Unassigned'}
                    </span>
                  </div>
                </div>

                <div className="form-group mb-4">
                  <label className="form-label">Select Active Consultant</label>
                  <select
                    className="form-select"
                    value={reassignConsultantId}
                    onChange={(e) => setReassignConsultantId(e.target.value)}
                    required
                  >
                    <option value="">-- Choose Consultant --</option>
                    {leadOverview?.consultantWorkloads?.map((cw) => (
                      <option key={cw.consultantId} value={cw.consultantId}>
                        {cw.name || cw.consultantName} ({cw.assignedLeadsCount ?? cw.activeLeadsCount ?? 0} active leads)
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-group mb-4">
                  <label className="form-label">Reassignment Notes / Reason</label>
                  <input
                    type="text"
                    className="form-input"
                    value={reassignNotes}
                    onChange={(e) => setReassignNotes(e.target.value)}
                    placeholder="e.g. Workload balancing or customer preference"
                  />
                </div>

                <div className="flex justify-end gap-3 mt-6">
                  <button
                    type="button"
                    className="btn btn-secondary btn-sm"
                    onClick={() => setSelectedLeadForReassign(null)}
                    disabled={reassignLoading}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="btn btn-primary btn-sm"
                    disabled={reassignLoading || !reassignConsultantId}
                  >
                    {reassignLoading ? 'Reassigning...' : 'Confirm Reassignment'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* ================= MODAL: LEAD AUDIT & COMMUNICATIONS ================= */}
        {selectedLeadForAudit && (
          <div className="admin-modal-overlay">
            <div className="admin-modal-content max-w-2xl">
              <div className="admin-modal-header">
                <div>
                  <h3 className="admin-modal-title">Dealership Audit: {selectedLeadForAudit.leadRef}</h3>
                  <p className="text-xs text-muted">
                    {selectedLeadForAudit.customerName} • {selectedLeadForAudit.customerPhone} • {selectedLeadForAudit.customerEmail}
                  </p>
                </div>
                <button
                  type="button"
                  className="btn btn-link"
                  onClick={() => setSelectedLeadForAudit(null)}
                >
                  ✕
                </button>
              </div>

              {loadingAudit ? (
                <div className="py-8 text-center text-muted text-sm">
                  Loading lead audit history...
                </div>
              ) : (
                <div className="space-y-6 max-h-[70vh] overflow-y-auto pr-1">
                  {/* AI Buyer Summary */}
                  <div className="p-3 bg-neutral-900 border border-neutral-800 rounded-xl text-xs">
                    <span className="text-cyan-400 font-bold block mb-1">✨ AI Buyer Summary:</span>
                    <p className="text-neutral-300 whitespace-pre-line leading-relaxed">
                      {selectedLeadForAudit.aiSummary || selectedLeadForAudit.aiBuyerSummary || 'No AI summary generated.'}
                    </p>
                  </div>

                  {/* Simulated Communications */}
                  <div>
                    <h4 className="text-xs font-bold uppercase text-muted tracking-wider mb-2">
                      Simulated Communications Dispatched ({auditComms.length})
                    </h4>
                    {auditComms.length === 0 ? (
                      <p className="text-xs text-muted italic">No simulated communications sent.</p>
                    ) : (
                      <div className="space-y-2">
                        {auditComms.map((comm) => (
                          <div key={comm.id} className="p-3 bg-neutral-900 border border-neutral-800 rounded-lg text-xs">
                            <div className="flex justify-between items-center mb-1">
                              <span className="font-bold text-accent">{comm.channel}</span>
                              <span className="text-muted font-mono">{new Date(comm.sentAt).toLocaleString()}</span>
                            </div>
                            <p className="text-neutral-300 whitespace-pre-line">{comm.approvedContent || comm.messageBody || comm.draftContent}</p>
                            <span className="text-[10px] text-emerald-400 block mt-1">
                              Status: {comm.deliveryStatus || comm.simulatedStatus || 'SIMULATED_SENT'} (Dispatched by {comm.consultantName})
                            </span>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>

                  {/* Status History */}
                  <div>
                    <h4 className="text-xs font-bold uppercase text-muted tracking-wider mb-2">
                      Status Transitions ({auditHistory.length})
                    </h4>
                    {auditHistory.length === 0 ? (
                      <p className="text-xs text-muted italic">No status history recorded.</p>
                    ) : (
                      <div className="space-y-2">
                        {auditHistory.map((hist) => (
                          <div key={hist.id} className="p-2.5 bg-neutral-900 border border-neutral-800 rounded-lg text-xs flex justify-between items-center">
                            <div>
                              <span className="text-neutral-300">
                                {hist.fromStatus || hist.previousStatus || 'INITIAL'} → <strong className="text-white">{hist.toStatus || hist.newStatus}</strong>
                              </span>
                              {(hist.notes || hist.note) && (
                                <span className="text-muted block mt-0.5 italic">"{hist.notes || hist.note}"</span>
                              )}
                            </div>
                            <div className="text-right text-[11px] text-muted">
                              <span>{hist.changedByUserName || 'System'}</span>
                              <span className="block font-mono">{new Date(hist.createdAt).toLocaleTimeString()}</span>
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                </div>
              )}

              <div className="flex justify-end mt-4">
                <button
                  type="button"
                  className="btn btn-outline btn-sm"
                  onClick={() => setSelectedLeadForAudit(null)}
                >
                  Close
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default AdminDashboardPage