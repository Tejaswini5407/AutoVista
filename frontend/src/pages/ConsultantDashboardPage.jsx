import { useState, useEffect, useCallback } from 'react'
import useDocumentTitle from '../hooks/useDocumentTitle.js'
import { useAuth } from '../hooks/useAuth.js'
import Badge from '../components/Badge.jsx'
import LoadingSpinner from '../components/LoadingSpinner.jsx'
import { formatINR, formatDate } from '../utils/formatters.js'
import {
  getConsultantLeads,
  getConsultantLeadById,
  updateConsultantLeadStatus,
  getConsultantAiSummary,
  generateConsultantAiDraft,
  updateConsultantDraft,
  approveConsultantDraft,
  sendConsultantSimulatedReply,
} from '../services/leadService.js'

const STATUS_OPTIONS = [
  { value: 'ALL', label: 'All Enquiries' },
  { value: 'NEW', label: 'New' },
  { value: 'CONTACTED', label: 'Contacted' },
  { value: 'FOLLOW_UP', label: 'Follow Up' },
  { value: 'TEST_DRIVE_SCHEDULED', label: 'Test Drive Scheduled' },
  { value: 'CONVERTED', label: 'Converted' },
  { value: 'LOST', label: 'Lost' },
]

function ConsultantDashboardPage() {
  useDocumentTitle('Consultant Workspace')
  const { user, loginAsDemo } = useAuth()
  const isAuthorized = user && (user.role === 'CONSULTANT' || user.role === 'ADMIN')

  const [leads, setLeads] = useState([])
  const [loading, setLoading] = useState(isAuthorized)
  const [selectedLeadId, setSelectedLeadId] = useState(null)

  // Filters & Search
  const [statusFilter, setStatusFilter] = useState('ALL')
  const [searchQuery, setSearchQuery] = useState('')

  // Draft & Action States
  const [draftText, setDraftText] = useState('')
  const [isEditingDraft, setIsEditingDraft] = useState(false)
  const [savingDraft, setSavingDraft] = useState(false)
  const [approvingDraft, setApprovingDraft] = useState(false)
  const [sendingChannel, setSendingChannel] = useState(null)
  const [generatingAi, setGeneratingAi] = useState(null) // 'summary' | 'draft' | null

  // Status Update State
  const [newStatusValue, setNewStatusValue] = useState('')
  const [statusNote, setStatusNote] = useState('')
  const [updatingStatus, setUpdatingStatus] = useState(false)

  // Tabs for right panel: 'workspace' | 'audit'
  const [panelTab, setPanelTab] = useState('workspace')
  const [actionSuccess, setActionSuccess] = useState(null)
  const [actionError, setActionError] = useState(null)

  // Fetch leads on mount or reload
  const refreshLeads = useCallback(async (preserveSelectedId = null) => {
    if (!isAuthorized) return
    setActionError(null)
    try {
      const data = await getConsultantLeads()
      setLeads(data)
      if (data.length > 0) {
        const targetId = preserveSelectedId || selectedLeadId || data[0].id
        const found = data.find((l) => l.id === targetId) || data[0]
        setSelectedLeadId(found.id)
        setDraftText(found.aiDraftReply || '')
        setNewStatusValue(found.status)
      } else {
        setSelectedLeadId(null)
        setDraftText('')
      }
    } catch (err) {
      console.error('Failed to load consultant leads:', err)
      setActionError(err.message || 'Failed to retrieve assigned enquiries.')
    } finally {
      setLoading(false)
    }
  }, [isAuthorized, selectedLeadId])

  useEffect(() => {
    if (!isAuthorized) return
    let isMounted = true

    getConsultantLeads()
      .then((data) => {
        if (!isMounted) return
        setLeads(data)
        if (data && data.length > 0) {
          const first = data[0]
          setSelectedLeadId(first.id)
          setDraftText(first.aiDraftReply || '')
          setNewStatusValue(first.status)
        }
      })
      .catch((err) => {
        if (!isMounted) return
        console.error('Failed to load consultant leads:', err)
        setActionError(err.message || 'Failed to retrieve assigned enquiries.')
      })
      .finally(() => {
        if (isMounted) setLoading(false)
      })

    return () => {
      isMounted = false
    }
  }, [isAuthorized])

  const activeLead = leads.find((l) => l.id === selectedLeadId) || null

  const handleSelectLead = (lead) => {
    setSelectedLeadId(lead.id)
    setDraftText(lead.aiDraftReply || '')
    setNewStatusValue(lead.status)
    setIsEditingDraft(false)
    setActionSuccess(null)
    setActionError(null)
  }

  // Save Draft
  const handleSaveDraft = async () => {
    if (!activeLead) return
    setSavingDraft(true)
    setActionError(null)
    setActionSuccess(null)
    try {
      const updated = await updateConsultantDraft(activeLead.id, draftText)
      setLeads((prev) => prev.map((l) => (l.id === updated.id ? updated : l)))
      setIsEditingDraft(false)
      setActionSuccess('✓ Draft reply saved successfully. Approval required before dispatch.')
    } catch (err) {
      console.error('Failed to save draft:', err)
      setActionError(err.message || 'Failed to save draft reply.')
    } finally {
      setSavingDraft(false)
    }
  }

  // Approve Draft
  const handleApproveDraft = async () => {
    if (!activeLead) return
    setApprovingDraft(true)
    setActionError(null)
    setActionSuccess(null)
    try {
      const updated = await approveConsultantDraft(activeLead.id)
      setLeads((prev) => prev.map((l) => (l.id === updated.id ? updated : l)))
      setActionSuccess('✓ Draft explicitly approved for customer transmission by consultant.')
    } catch (err) {
      console.error('Failed to approve draft:', err)
      setActionError(err.message || 'Failed to approve draft reply.')
    } finally {
      setApprovingDraft(false)
    }
  }

  // Generate / Refresh AI Draft
  const handleGenerateDraft = async () => {
    if (!activeLead) return
    setGeneratingAi('draft')
    setActionError(null)
    setActionSuccess(null)
    try {
      const updated = await generateConsultantAiDraft(activeLead.id)
      setLeads((prev) => prev.map((l) => (l.id === updated.id ? updated : l)))
      setDraftText(updated.aiDraftReply || '')
      setIsEditingDraft(false)
      setActionSuccess('✨ Fresh AI draft generated. Please review and approve before sending.')
    } catch (err) {
      console.error('Failed to generate AI draft:', err)
      setActionError(err.message || 'Failed to generate AI draft.')
    } finally {
      setGeneratingAi(null)
    }
  }

  // Refresh AI Buyer Summary
  const handleRefreshSummary = async () => {
    if (!activeLead) return
    setGeneratingAi('summary')
    setActionError(null)
    setActionSuccess(null)
    try {
      const updated = await getConsultantAiSummary(activeLead.id)
      setLeads((prev) => prev.map((l) => (l.id === updated.id ? updated : l)))
      setActionSuccess('✨ AI Buyer Summary refreshed with verified customer parameters.')
    } catch (err) {
      console.error('Failed to refresh summary:', err)
      setActionError(err.message || 'Failed to refresh AI summary.')
    } finally {
      setGeneratingAi(null)
    }
  }

  // Send Simulated Reply
  const handleSendReply = async (channel) => {
    if (!activeLead) return
    if (!activeLead.consultantApproved) {
      setActionError('Consultant approval is mandatory. Please review and approve the draft first.')
      return
    }
    if (activeLead.optOut) {
      setActionError('Customer has opted out of communications. Simulated transmission blocked.')
      return
    }

    setSendingChannel(channel)
    setActionError(null)
    setActionSuccess(null)
    try {
      const channelParam =
        channel === 'WhatsApp'
          ? 'WHATSAPP_SIMULATED'
          : channel === 'Email'
          ? 'EMAIL_SIMULATED'
          : 'SMS_SIMULATED'

      await sendConsultantSimulatedReply(activeLead.id, {
        channel: channelParam,
        messageBody: draftText || activeLead.aiDraftReply,
      })
      // Refresh lead details with latest communication history
      const freshLead = await getConsultantLeadById(activeLead.id)
      setLeads((prev) => prev.map((l) => (l.id === freshLead.id ? freshLead : l)))
      setActionSuccess(
        `✓ Simulated message dispatched via ${channel} and recorded in dealership communication audit.`
      )
    } catch (err) {
      console.error('Failed to send simulated reply:', err)
      setActionError(err.message || 'Failed to dispatch simulated message.')
    } finally {
      setSendingChannel(null)
    }
  }

  // Update Status
  const handleStatusUpdate = async (e) => {
    e.preventDefault()
    if (!activeLead || !newStatusValue || newStatusValue === activeLead.status) return
    setUpdatingStatus(true)
    setActionError(null)
    setActionSuccess(null)
    try {
      await updateConsultantLeadStatus(activeLead.id, {
        status: newStatusValue,
        note: statusNote.trim() || undefined,
      })
      const freshLead = await getConsultantLeadById(activeLead.id)
      setLeads((prev) => prev.map((l) => (l.id === freshLead.id ? freshLead : l)))
      setStatusNote('')
      setActionSuccess(`✓ Lead status updated to "${newStatusValue}". Transition audit logged.`)
    } catch (err) {
      console.error('Failed to update lead status:', err)
      setActionError(err.message || 'Invalid status transition.')
    } finally {
      setUpdatingStatus(false)
    }
  }

  // Filtered Leads
  const filteredLeads = leads.filter((l) => {
    if (statusFilter !== 'ALL' && l.status !== statusFilter) return false
    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase()
      const name = (l.customerName || '').toLowerCase()
      const ref = (l.leadRef || '').toLowerCase()
      const phone = (l.customerPhone || '').toLowerCase()
      const car = (l.carModelName || l.usedCarTitle || '').toLowerCase()
      if (!name.includes(q) && !ref.includes(q) && !phone.includes(q) && !car.includes(q)) {
        return false
      }
    }
    return true
  })

  // Helper for SLA status badge
  const getSlaIndicator = (lead) => {
    if (!lead || !lead.assignedAt) return null
    if (lead.status !== 'NEW') {
      return <span className="lead-sla-tag text-emerald-400">Responded ({lead.status})</span>
    }
    const assignedTime = new Date(lead.assignedAt).getTime()
    const now = new Date().getTime()
    const diffMinutes = Math.floor((now - assignedTime) / (1000 * 60))
    if (diffMinutes < 5) {
      return (
        <span className="lead-sla-tag text-emerald-400 font-semibold">
          ⏱ {5 - diffMinutes}m remaining in SLA
        </span>
      )
    }
    return (
      <span className="lead-sla-tag text-amber-400 font-semibold">
        ⚠ 5m SLA Passed ({diffMinutes}m)
      </span>
    )
  }

  // Not authorized view
  if (!isAuthorized) {
    return (
      <div className="dashboard-page">
        <div className="container py-16 text-center">
          <div className="admin-auth-card max-w-md mx-auto p-8 rounded-2xl bg-neutral-900 border border-neutral-700">
            <span className="text-4xl">💼</span>
            <h2 className="text-2xl font-bold mt-4 text-white">Consultant Authorization Required</h2>
            <p className="text-sm text-neutral-400 mt-2">
              The Sales Consultant Workspace is reserved for active dealership sales advisors.
              Access requires an authenticated consultant session.
            </p>
            <div className="mt-6 flex flex-col gap-3">
              <button
                type="button"
                className="btn btn-primary w-full py-2.5 rounded-xl font-medium"
                onClick={() => loginAsDemo('consultant')}
              >
                Sign In as Vikram Singh (Consultant)
              </button>
              <button
                type="button"
                className="btn btn-outline w-full py-2.5 rounded-xl font-medium"
                onClick={() => loginAsDemo('admin')}
              >
                Sign In as Admin (Full Control)
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
        {/* Top Header */}
        <div className="dashboard-header-bar flex flex-wrap items-center justify-between gap-4 pb-6 border-b border-neutral-800">
          <div>
            <div className="eyebrow-badge">Sales Desk • Gomti Nagar Flagship</div>
            <h1 className="dashboard-title text-3xl font-bold text-white">Consultant Lead Desk</h1>
            <p className="dashboard-subtitle text-sm text-neutral-400 mt-1">
              Logged in as <strong>{user?.fullName || 'Vikram Singh'}</strong> ({user?.email || 'vikram.consultant@autovista.in'})
            </p>
          </div>

          <div className="sla-monitor-card flex items-center gap-4 bg-neutral-900/80 border border-neutral-700/80 rounded-xl px-5 py-3">
            <div className="sla-metric">
              <span className="sla-val text-xl font-bold text-emerald-400">&lt; 5m SLA</span>
              <span className="sla-label text-xs text-neutral-400 block uppercase">Response Commitment</span>
            </div>
            <div className="sla-badge flex items-center gap-2 text-xs font-semibold px-3 py-1 bg-cyan-500/10 border border-cyan-500/30 text-cyan-400 rounded-full">
              <span className="pulse-dot" />
              <span>Round-Robin Active</span>
            </div>
          </div>
        </div>

        {/* Global Action Notifications */}
        {actionSuccess && (
          <div className="alert-box alert-success mt-4 flex items-center justify-between">
            <span>{actionSuccess}</span>
            <button
              type="button"
              className="btn btn-link btn-xs text-emerald-300 font-bold ml-4"
              onClick={() => setActionSuccess(null)}
            >
              ✕
            </button>
          </div>
        )}

        {actionError && (
          <div className="alert-box alert-danger mt-4 flex items-center justify-between">
            <span>⚠ {actionError}</span>
            <button
              type="button"
              className="btn btn-link btn-xs text-red-300 font-bold ml-4"
              onClick={() => setActionError(null)}
            >
              ✕
            </button>
          </div>
        )}

        {/* Dashboard Layout */}
        <div className="dashboard-layout mt-6">
          {/* Left Column: Assigned Leads List */}
          <div className="dashboard-sidebar flex flex-col gap-4">
            <div className="sidebar-header flex items-center justify-between">
              <h3 className="text-base font-bold text-white">
                Assigned Enquiries ({filteredLeads.length})
              </h3>
              <button
                type="button"
                className="btn btn-outline btn-xs"
                onClick={() => refreshLeads(selectedLeadId)}
                title="Refresh leads list"
              >
                ↻ Refresh
              </button>
            </div>

            {/* Search Box */}
            <div>
              <input
                type="text"
                placeholder="Search name, phone, ref..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full px-3 py-2 text-xs bg-neutral-900 border border-neutral-700 rounded-lg text-white placeholder-neutral-500 focus:outline-none focus:border-cyan-500"
              />
            </div>

            {/* Status Filter Pills */}
            <div className="flex flex-wrap gap-1.5">
              {STATUS_OPTIONS.map((opt) => (
                <button
                  key={opt.value}
                  type="button"
                  className={`px-2.5 py-1 text-xs rounded-md font-medium transition ${
                    statusFilter === opt.value
                      ? 'bg-cyan-500 text-black font-semibold'
                      : 'bg-neutral-800 text-neutral-400 hover:text-white hover:bg-neutral-700'
                  }`}
                  onClick={() => setStatusFilter(opt.value)}
                >
                  {opt.label}
                </button>
              ))}
            </div>

            {/* Leads List */}
            {loading ? (
              <div className="py-12 text-center">
                <LoadingSpinner label="Loading assigned leads..." />
              </div>
            ) : filteredLeads.length === 0 ? (
              <div className="p-6 text-center bg-neutral-900/60 border border-neutral-800 rounded-xl text-neutral-400 text-sm">
                No enquiries match your current filters.
              </div>
            ) : (
              <div className="leads-list flex flex-col gap-2 max-h-[680px] overflow-y-auto pr-1">
                {filteredLeads.map((lead) => {
                  const isSelected = selectedLeadId === lead.id
                  const carDisplay =
                    lead.carVariantName
                      ? `${lead.carBrand || ''} ${lead.carModelName || ''} - ${lead.carVariantName}`
                      : lead.usedCarTitle || 'Vehicle Consultation'

                  return (
                    <button
                      key={lead.id}
                      type="button"
                      className={`lead-item-card transition text-left p-3.5 rounded-xl border ${
                        isSelected
                          ? 'border-cyan-400 bg-cyan-950/20'
                          : 'border-neutral-800 bg-neutral-900/80 hover:border-neutral-700'
                      }`}
                      onClick={() => handleSelectLead(lead)}
                    >
                      <div className="lead-item-top flex items-center justify-between mb-1">
                        <strong className="lead-name text-sm font-semibold text-white">
                          {lead.customerName}
                        </strong>
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
                      </div>

                      <p className="lead-car-spec text-xs text-neutral-300 truncate mb-1">
                        {carDisplay}
                      </p>

                      <div className="lead-item-meta flex items-center justify-between text-[11px] text-neutral-400 mt-2 pt-2 border-t border-neutral-800/80">
                        <span className="font-mono text-cyan-400/80">{lead.leadRef}</span>
                        {getSlaIndicator(lead)}
                      </div>
                    </button>
                  )
                })}
              </div>
            )}

            <div className="compliance-note-box bg-neutral-900/70 border border-neutral-800 rounded-xl p-3 text-xs text-neutral-400">
              <strong className="text-neutral-200 block mb-1">Dealership AI Compliance:</strong>
              <p>
                All AI replies must be vetted by the consultant prior to dispatch. Unauthorized discounts or delivery commitments are strictly prohibited.
              </p>
            </div>
          </div>

          {/* Right Column: Lead Detail & AI Workbench */}
          <div className="dashboard-main-panel bg-neutral-900/80 border border-neutral-700/80 rounded-2xl p-6">
            {!activeLead ? (
              <div className="py-24 text-center text-neutral-500">
                <span className="text-4xl block mb-2">📋</span>
                Select an enquiry from the left queue to view buyer dossier and AI first reply.
              </div>
            ) : (
              <div>
                {/* Panel Navigation Tabs */}
                <div className="flex items-center justify-between pb-4 border-b border-neutral-800 mb-6">
                  <div className="flex items-center gap-3">
                    <button
                      type="button"
                      className={`px-4 py-1.5 text-sm font-medium rounded-lg transition ${
                        panelTab === 'workspace'
                          ? 'bg-neutral-800 text-white border border-neutral-700'
                          : 'text-neutral-400 hover:text-white'
                      }`}
                      onClick={() => setPanelTab('workspace')}
                    >
                      Enquiry & AI Workspace
                    </button>
                    <button
                      type="button"
                      className={`px-4 py-1.5 text-sm font-medium rounded-lg transition ${
                        panelTab === 'audit'
                          ? 'bg-neutral-800 text-white border border-neutral-700'
                          : 'text-neutral-400 hover:text-white'
                      }`}
                      onClick={() => setPanelTab('audit')}
                    >
                      Audit Trail & Communications ({activeLead.communications?.length || activeLead.communicationHistory?.length || 0})
                    </button>
                  </div>

                  <span className="font-mono text-xs text-amber-400 bg-amber-500/10 border border-amber-500/30 px-3 py-1 rounded-full">
                    {activeLead.leadRef}
                  </span>
                </div>

                {panelTab === 'workspace' ? (
                  <>
                    {/* Header Dossier */}
                    <div className="lead-detail-header flex flex-wrap items-start justify-between gap-4 mb-6">
                      <div>
                        <h2 className="text-2xl font-bold text-white flex items-center gap-3">
                          {activeLead.customerName}
                          <Badge
                            variant={
                              activeLead.status === 'NEW'
                                ? 'accent'
                                : activeLead.status === 'CONVERTED'
                                ? 'success'
                                : activeLead.status === 'LOST'
                                ? 'danger'
                                : 'default'
                            }
                          >
                            {activeLead.status}
                          </Badge>
                        </h2>
                        <p className="lead-sub-info text-sm text-neutral-400 mt-1">
                          📞 {activeLead.customerPhone} • ✉️ {activeLead.customerEmail} • 📍 {activeLead.customerCity || 'Lucknow'}
                        </p>
                      </div>

                      <div className="flex flex-col items-end gap-1.5">
                        {activeLead.optOut ? (
                          <span className="badge badge-danger">Customer Opted Out</span>
                        ) : activeLead.consentGiven ? (
                          <span className="badge badge-success">Consent Active & Verified</span>
                        ) : (
                          <span className="badge badge-danger">No Explicit Consent</span>
                        )}
                        <span className="text-[11px] text-neutral-500">
                          Assigned: {formatDate(activeLead.assignedAt)}
                        </span>
                      </div>
                    </div>

                    {/* Vehicle & Preference Grid */}
                    <div className="bg-neutral-950/60 border border-neutral-800 rounded-xl p-4 mb-6">
                      <div className="flex flex-wrap items-center justify-between gap-2 pb-3 border-b border-neutral-800/80">
                        <div>
                          <span className="text-xs uppercase text-neutral-500 font-medium">Vehicle of Interest</span>
                          <h4 className="text-base font-semibold text-white">
                            {activeLead.carVariantName
                              ? `${activeLead.carBrand || ''} ${activeLead.carModelName || ''} - ${activeLead.carVariantName}`
                              : activeLead.usedCarTitle || 'General Dealership Enquiry'}
                          </h4>
                        </div>
                        <div className="text-right">
                          <span className="text-xs uppercase text-neutral-500 font-medium">Quoted Dealership Price</span>
                          <div className="text-base font-bold font-mono text-cyan-400">
                            {activeLead.vehiclePriceFormatted || (activeLead.carExShowroomPrice
                              ? formatINR(activeLead.carExShowroomPrice)
                              : activeLead.usedCarPrice
                              ? formatINR(activeLead.usedCarPrice)
                              : 'Standard Catalog')}
                          </div>
                        </div>
                      </div>

                      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 pt-3 text-xs">
                        <div>
                          <span className="text-neutral-500 block">Stated Budget:</span>
                          <span className="text-neutral-200 font-medium">{activeLead.statedBudget || 'Not specified'}</span>
                        </div>
                        <div>
                          <span className="text-neutral-500 block">Purchase Timeline:</span>
                          <span className="text-neutral-200 font-medium">{activeLead.purchaseTimeline || 'Not specified'}</span>
                        </div>
                        <div>
                          <span className="text-neutral-500 block">Preferred Fuel:</span>
                          <span className="text-neutral-200 font-medium">{activeLead.preferredFuel || 'Flexible'}</span>
                        </div>
                        <div>
                          <span className="text-neutral-500 block">Transmission:</span>
                          <span className="text-neutral-200 font-medium">{activeLead.preferredTransmission || 'Flexible'}</span>
                        </div>
                      </div>

                      {activeLead.enquiryMessage && (
                        <div className="mt-3 pt-3 border-t border-neutral-800/80 text-xs">
                          <span className="text-neutral-500 block mb-1">Customer Message:</span>
                          <p className="text-neutral-300 italic bg-neutral-900/60 p-2.5 rounded-lg border border-neutral-800">
                            "{activeLead.enquiryMessage}"
                          </p>
                        </div>
                      )}
                    </div>

                    {/* Status Transition Control */}
                    <form
                      onSubmit={handleStatusUpdate}
                      className="bg-neutral-950/60 border border-neutral-800 rounded-xl p-4 mb-6 flex flex-wrap items-center justify-between gap-4"
                    >
                      <div className="flex items-center gap-3 flex-1 min-w-[280px]">
                        <label className="text-xs font-semibold text-neutral-300 uppercase whitespace-nowrap">
                          Lead Workflow:
                        </label>
                        <select
                          value={newStatusValue}
                          onChange={(e) => setNewStatusValue(e.target.value)}
                          className="bg-neutral-900 border border-neutral-700 text-white text-xs rounded-lg px-3 py-2 focus:outline-none focus:border-cyan-500"
                        >
                          <option value="NEW">NEW</option>
                          <option value="CONTACTED">CONTACTED</option>
                          <option value="FOLLOW_UP">FOLLOW_UP</option>
                          <option value="TEST_DRIVE_SCHEDULED">TEST_DRIVE_SCHEDULED</option>
                          <option value="CONVERTED">CONVERTED</option>
                          <option value="LOST">LOST</option>
                        </select>
                        <input
                          type="text"
                          placeholder="Optional transition note..."
                          value={statusNote}
                          onChange={(e) => setStatusNote(e.target.value)}
                          className="flex-1 bg-neutral-900 border border-neutral-700 text-white text-xs rounded-lg px-3 py-2 placeholder-neutral-500 focus:outline-none focus:border-cyan-500"
                        />
                      </div>
                      <button
                        type="submit"
                        disabled={updatingStatus || newStatusValue === activeLead.status}
                        className="btn btn-outline btn-sm font-medium"
                      >
                        {updatingStatus ? 'Updating...' : 'Update Status'}
                      </button>
                    </form>

                    {/* AI Buyer Summary Card */}
                    <div className="ai-card bg-neutral-950/60 border border-neutral-800 rounded-xl p-5 mb-6">
                      <div className="ai-card-header flex items-center justify-between mb-3">
                        <div className="flex items-center gap-2">
                          <span className="ai-chip text-sm font-bold text-cyan-400">
                            ✨ Gemini AI Buyer Summary
                          </span>
                          <span className="text-[11px] text-neutral-500 bg-neutral-900 px-2 py-0.5 rounded border border-neutral-800">
                            Zero-Hallucination Policy
                          </span>
                        </div>
                        <button
                          type="button"
                          className="btn btn-link btn-xs text-cyan-400 hover:text-cyan-300"
                          disabled={generatingAi === 'summary'}
                          onClick={handleRefreshSummary}
                        >
                          {generatingAi === 'summary' ? 'Refreshing...' : '↻ Refresh Summary'}
                        </button>
                      </div>

                      <div className="bg-neutral-900/80 border border-neutral-800/80 rounded-xl p-4 text-xs text-neutral-300 leading-relaxed font-sans">
                        {(activeLead.aiSummary || activeLead.aiBuyerSummary) ? (
                          <div className="whitespace-pre-line">{activeLead.aiSummary || activeLead.aiBuyerSummary}</div>
                        ) : (
                          <span className="italic text-neutral-500">
                            AI Buyer Summary pending generation. Click Refresh Summary above.
                          </span>
                        )}
                      </div>
                    </div>

                    {/* AI First-Reply Draft Workspace */}
                    <div className="ai-card bg-neutral-950/60 border border-neutral-800 rounded-xl p-5">
                      <div className="ai-card-header flex items-center justify-between mb-3">
                        <div className="flex items-center gap-2">
                          <span className="ai-chip text-sm font-bold text-amber-400">
                            📝 AI First-Reply Draft
                          </span>
                          {activeLead.consultantApproved ? (
                            <span className="badge badge-success text-[10px]">
                              ✓ Consultant Approved
                            </span>
                          ) : (
                            <span className="badge badge-danger text-[10px]">
                              Review & Approval Required
                            </span>
                          )}
                        </div>
                        <div className="flex items-center gap-2">
                          <button
                            type="button"
                            className="btn btn-link btn-xs text-amber-400 hover:text-amber-300"
                            disabled={generatingAi === 'draft'}
                            onClick={handleGenerateDraft}
                          >
                            {generatingAi === 'draft' ? 'Drafting...' : '✨ Regenerate Draft'}
                          </button>
                        </div>
                      </div>

                      {/* Draft Editor / Preview */}
                      <div className="draft-editor-box mt-2">
                        {isEditingDraft ? (
                          <div className="space-y-2">
                            <textarea
                              className="draft-textarea w-full bg-neutral-900 border border-neutral-700 rounded-xl p-3 text-xs text-white leading-relaxed focus:outline-none focus:border-cyan-500"
                              rows={6}
                              value={draftText}
                              onChange={(e) => setDraftText(e.target.value)}
                              placeholder="Enter personalized first response..."
                            />
                            <div className="flex justify-end gap-2">
                              <button
                                type="button"
                                className="btn btn-outline btn-xs"
                                onClick={() => {
                                  setDraftText(activeLead.aiDraftReply || '')
                                  setIsEditingDraft(false)
                                }}
                              >
                                Cancel
                              </button>
                              <button
                                type="button"
                                disabled={savingDraft}
                                className="btn btn-primary btn-xs"
                                onClick={handleSaveDraft}
                              >
                                {savingDraft ? 'Saving...' : 'Save Draft Content'}
                              </button>
                            </div>
                          </div>
                        ) : (
                          <div className="draft-preview-box bg-neutral-900/80 border border-neutral-800 rounded-xl p-4 text-xs text-neutral-300 leading-relaxed font-sans">
                            <p className="whitespace-pre-line">{draftText || activeLead.aiDraftReply || 'No draft reply created.'}</p>
                          </div>
                        )}
                      </div>

                      {/* Approval Toggle & simulated transmission */}
                      <div className="mt-4 pt-4 border-t border-neutral-800/80 flex flex-wrap items-center justify-between gap-4">
                        <div className="flex items-center gap-3">
                          <button
                            type="button"
                            className="btn btn-outline btn-xs"
                            onClick={() => setIsEditingDraft(!isEditingDraft)}
                          >
                            {isEditingDraft ? 'Close Editor' : '✏️ Edit Draft'}
                          </button>

                          <button
                            type="button"
                            disabled={approvingDraft || activeLead.consultantApproved}
                            className={`btn btn-xs ${
                              activeLead.consultantApproved
                                ? 'bg-emerald-600/20 text-emerald-400 border border-emerald-500/30'
                                : 'btn-accent'
                            }`}
                            onClick={handleApproveDraft}
                          >
                            {approvingDraft
                              ? 'Approving...'
                              : activeLead.consultantApproved
                              ? '✓ Draft Approved'
                              : '✓ Approve Draft for Sending'}
                          </button>
                        </div>

                        {/* Simulated send buttons */}
                        <div className="send-buttons-group flex items-center gap-2">
                          <button
                            type="button"
                            disabled={
                              !activeLead.consultantApproved ||
                              activeLead.optOut ||
                              sendingChannel !== null
                            }
                            title={
                              activeLead.optOut
                                ? 'Customer has opted out'
                                : !activeLead.consultantApproved
                                ? 'Approve draft first'
                                : 'Send simulated WhatsApp reply'
                            }
                            className="btn btn-primary btn-sm flex items-center gap-1.5"
                            onClick={() => handleSendReply('WhatsApp')}
                          >
                            <span>💬</span>
                            <span>{sendingChannel === 'WhatsApp' ? 'Sending...' : 'WhatsApp (Mock)'}</span>
                          </button>

                          <button
                            type="button"
                            disabled={
                              !activeLead.consultantApproved ||
                              activeLead.optOut ||
                              sendingChannel !== null
                            }
                            className="btn btn-secondary btn-sm flex items-center gap-1.5"
                            onClick={() => handleSendReply('Email')}
                          >
                            <span>✉️</span>
                            <span>{sendingChannel === 'Email' ? 'Sending...' : 'Email (Mock)'}</span>
                          </button>

                          <button
                            type="button"
                            disabled={
                              !activeLead.consultantApproved ||
                              activeLead.optOut ||
                              sendingChannel !== null
                            }
                            className="btn btn-outline btn-sm flex items-center gap-1.5"
                            onClick={() => handleSendReply('SMS')}
                          >
                            <span>📱</span>
                            <span>{sendingChannel === 'SMS' ? 'Sending...' : 'SMS (Mock)'}</span>
                          </button>
                        </div>
                      </div>

                      {activeLead.optOut && (
                        <p className="text-[11px] text-red-400 mt-2">
                          ⚠ Communication dispatch blocked: Customer exercised explicit opt-out preference.
                        </p>
                      )}
                      {!activeLead.consultantApproved && !activeLead.optOut && (
                        <p className="text-[11px] text-neutral-500 mt-2">
                          Mandatory Rule: Click "Approve Draft" after vetting before dispatching to customer.
                        </p>
                      )}
                    </div>
                  </>
                ) : (
                  /* Audit Trail & Communications Tab */
                  <div className="space-y-6">
                    {/* Communication History */}
                    <div>
                      <h4 className="text-sm font-bold text-white mb-3 flex items-center gap-2">
                        <span>💬</span> Simulated Communications Audit Trail
                      </h4>
                      {(!(activeLead.communications || activeLead.communicationHistory) || (activeLead.communications || activeLead.communicationHistory).length === 0) ? (
                        <div className="p-4 bg-neutral-950/60 border border-neutral-800 rounded-xl text-xs text-neutral-400 text-center">
                          No simulated messages sent for this lead yet.
                        </div>
                      ) : (
                        <div className="space-y-3">
                          {(activeLead.communications || activeLead.communicationHistory).map((comm) => (
                            <div
                              key={comm.id}
                              className="bg-neutral-950/80 border border-neutral-800 rounded-xl p-4 text-xs"
                            >
                              <div className="flex items-center justify-between pb-2 border-b border-neutral-800/80 mb-2">
                                <div className="flex items-center gap-2">
                                  <span className="font-semibold text-cyan-400 uppercase">
                                    {comm.channel}
                                  </span>
                                  <span className="text-neutral-500">•</span>
                                  <span className="text-neutral-300">
                                    Dispatched by {comm.consultantName || 'Sales Consultant'}
                                  </span>
                                </div>
                                <span className="text-neutral-500 font-mono">
                                  {formatDate(comm.sentAt)}
                                </span>
                              </div>
                              <p className="text-neutral-300 whitespace-pre-line leading-relaxed">
                                {comm.approvedContent || comm.messageBody || comm.draftContent}
                              </p>
                              <div className="mt-2 text-[10px] text-emerald-400">
                                Status: {comm.deliveryStatus || comm.simulatedStatus || 'SIMULATED_SENT'}
                              </div>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>

                    {/* Status History */}
                    <div>
                      <h4 className="text-sm font-bold text-white mb-3 flex items-center gap-2">
                        <span>📜</span> Lead Status Transition History
                      </h4>
                      {(!activeLead.statusHistory || activeLead.statusHistory.length === 0) ? (
                        <div className="p-4 bg-neutral-950/60 border border-neutral-800 rounded-xl text-xs text-neutral-400 text-center">
                          No status transitions recorded.
                        </div>
                      ) : (
                        <div className="space-y-2">
                          {activeLead.statusHistory.map((hist) => (
                            <div
                              key={hist.id}
                              className="bg-neutral-950/80 border border-neutral-800 rounded-xl p-3 text-xs flex items-center justify-between"
                            >
                              <div>
                                <span className="text-neutral-400">
                                  {hist.fromStatus || hist.previousStatus || 'INITIAL'} →{' '}
                                  <strong className="text-white">{hist.toStatus || hist.newStatus}</strong>
                                </span>
                                {(hist.notes || hist.note) && (
                                  <span className="text-neutral-400 block mt-0.5 italic">
                                    Note: "{hist.notes || hist.note}"
                                  </span>
                                )}
                              </div>
                              <div className="text-right">
                                <span className="text-neutral-400 block">
                                  {hist.changedByUserName || 'System'}
                                </span>
                                <span className="text-[10px] text-neutral-500 font-mono">
                                  {formatDate(hist.createdAt)}
                                </span>
                              </div>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

export default ConsultantDashboardPage