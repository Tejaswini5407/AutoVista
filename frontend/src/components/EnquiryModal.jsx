import { useState } from 'react'
import { useAuth } from '../hooks/useAuth.js'
import { useLanguage } from '../hooks/useLanguage.js'
import { createLead } from '../services/leadService.js'
import LoadingSpinner from './LoadingSpinner.jsx'

function EnquiryModal({ isOpen, onClose, vehicle, vehicleType = 'NEW' }) {
  const { user, loginAsDemo } = useAuth()
  const { t } = useLanguage()

  const [formData, setFormData] = useState({
    customerName: user?.fullName || '',
    customerEmail: user?.email || '',
    customerPhone: user?.phone || '',
    customerCity: user?.city || 'Lucknow',
    enquiryMessage: '',
    statedBudget: '',
    purchaseTimeline: 'Within 2 weeks',
    preferredFuel: vehicle?.fuelType || 'PETROL',
    preferredTransmission: vehicle?.transmission || 'AUTOMATIC',
    consentGiven: false,
    optOut: false,
  })

  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)
  const [successLead, setSuccessLead] = useState(null)

  if (!isOpen) return null

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)

    if (!user) {
      setError('Please sign in or select a demo customer account to submit an enquiry.')
      return
    }

    if (!formData.consentGiven) {
      setError('Explicit consent to be contacted is mandatory before submitting an enquiry.')
      return
    }

    setSubmitting(true)
    try {
      const payload = {
        carVariantId: vehicleType === 'NEW' ? vehicle?.variantId || vehicle?.id : null,
        usedCarId: vehicleType === 'USED' ? vehicle?.id : null,
        showroomId: vehicle?.showroomId || 1,
        customerName: formData.customerName.trim(),
        customerEmail: formData.customerEmail.trim(),
        customerPhone: formData.customerPhone.trim(),
        customerCity: formData.customerCity.trim(),
        enquiryType: 'VEHICLE_ENQUIRY',
        enquiryMessage: formData.enquiryMessage.trim(),
        statedBudget: formData.statedBudget ? formData.statedBudget.trim() : null,
        purchaseTimeline: formData.purchaseTimeline,
        preferredFuel: formData.preferredFuel,
        preferredTransmission: formData.preferredTransmission,
        consentGiven: formData.consentGiven,
        optOut: formData.optOut,
      }

      const response = await createLead(payload)
      setSuccessLead(response)
    } catch (err) {
      console.error('Failed to submit enquiry:', err)
      setError(err.message || 'Failed to submit enquiry. Please check required fields.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="modal-backdrop fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm">
      <div className="modal-card bg-neutral-900 border border-neutral-700 rounded-2xl w-full max-w-xl max-h-[90vh] overflow-y-auto text-neutral-100 shadow-2xl p-6 sm:p-8">
        {/* Header */}
        <div className="flex items-center justify-between pb-4 border-b border-neutral-800">
          <div>
            <h2 className="text-xl font-bold tracking-tight text-white flex items-center gap-2">
              <span className="text-amber-400">✦</span>
              {t('enquiryTitle')}
            </h2>
            <p className="text-sm text-neutral-400 mt-0.5">
              {vehicle?.displayName || vehicle?.modelName || 'Vehicle Consultation'}
            </p>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="text-neutral-400 hover:text-white text-2xl font-light w-8 h-8 flex items-center justify-center rounded-lg hover:bg-neutral-800 transition"
          >
            ✕
          </button>
        </div>

        {/* Success State */}
        {successLead ? (
          <div className="py-6 text-center space-y-4">
            <div className="w-16 h-16 bg-emerald-500/10 border border-emerald-500/30 rounded-full flex items-center justify-center mx-auto text-3xl text-emerald-400">
              ✓
            </div>
            <h3 className="text-lg font-semibold text-white">
              {t('enquirySuccess')}
            </h3>
            <div className="bg-neutral-800/60 border border-neutral-700/60 rounded-xl p-4 text-left space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-neutral-400">Lead Reference:</span>
                <span className="font-mono font-bold text-amber-400">{successLead.leadRef}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-neutral-400">Assigned Consultant:</span>
                <span className="font-medium text-white">{successLead.assignedConsultantName || 'AutoVista Concierge'}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-neutral-400">Response SLA:</span>
                <span className="text-emerald-400 font-medium">Under 5-Minute Priority Window</span>
              </div>
              <div className="flex justify-between">
                <span className="text-neutral-400">Showroom:</span>
                <span className="text-neutral-200">Gomti Nagar Flagship, Lucknow</span>
              </div>
            </div>
            <p className="text-xs text-neutral-400">
              {t('optOutNotice')}
            </p>
            <button
              type="button"
              onClick={onClose}
              className="btn btn-primary w-full py-2.5 rounded-xl font-medium mt-4"
            >
              Done & Return
            </button>
          </div>
        ) : (
          /* Form */
          <form onSubmit={handleSubmit} className="py-4 space-y-4 text-sm">
            {error && (
              <div className="p-3 bg-red-950/40 border border-red-800/60 rounded-xl text-red-200 text-xs flex items-center gap-2">
                <span>⚠</span>
                <span>{error}</span>
              </div>
            )}

            {!user && (
              <div className="p-3.5 bg-amber-950/30 border border-amber-800/40 rounded-xl flex items-center justify-between text-xs text-amber-200">
                <span>Authentication required to submit vehicle enquiry.</span>
                <button
                  type="button"
                  onClick={async () => {
                    try {
                      await loginAsDemo('customer')
                      setFormData((p) => ({
                        ...p,
                        customerName: 'Aarav Sharma (Verified Buyer)',
                        customerEmail: 'aarav.sharma@example.com',
                        customerPhone: '+91 98765 11223',
                      }))
                    } catch (err) {
                      setError(err.message || 'Quick sign-in failed. Please verify connection.')
                    }
                  }}
                  className="px-2.5 py-1 bg-amber-500 hover:bg-amber-400 text-neutral-950 rounded font-medium transition"
                >
                  Quick Sign-In
                </button>
              </div>
            )}

            {/* Customer Details */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-medium text-neutral-400 mb-1">Full Name *</label>
                <input
                  type="text"
                  name="customerName"
                  value={formData.customerName}
                  onChange={handleChange}
                  required
                  className="w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-amber-400"
                  placeholder="e.g. Aarav Sharma"
                />
              </div>
              <div>
                <label className="block text-xs font-medium text-neutral-400 mb-1">Phone Number *</label>
                <input
                  type="tel"
                  name="customerPhone"
                  value={formData.customerPhone}
                  onChange={handleChange}
                  required
                  className="w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-amber-400"
                  placeholder="+91 98765 00000"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-medium text-neutral-400 mb-1">Email Address *</label>
                <input
                  type="email"
                  name="customerEmail"
                  value={formData.customerEmail}
                  onChange={handleChange}
                  required
                  className="w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-amber-400"
                  placeholder="name@example.com"
                />
              </div>
              <div>
                <label className="block text-xs font-medium text-neutral-400 mb-1">City / Region</label>
                <input
                  type="text"
                  name="customerCity"
                  value={formData.customerCity}
                  onChange={handleChange}
                  className="w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-amber-400"
                  placeholder="Lucknow"
                />
              </div>
            </div>

            {/* Requirements & Budget */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-medium text-neutral-400 mb-1">Budget Expectation</label>
                <input
                  type="text"
                  name="statedBudget"
                  value={formData.statedBudget}
                  onChange={handleChange}
                  className="w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-amber-400"
                  placeholder="e.g. ₹12L - ₹15L (Optional)"
                />
              </div>
              <div>
                <label className="block text-xs font-medium text-neutral-400 mb-1">Purchase Timeline</label>
                <select
                  name="purchaseTimeline"
                  value={formData.purchaseTimeline}
                  onChange={handleChange}
                  className="w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-amber-400"
                >
                  <option value="Immediate (within 48 hours)">Immediate (within 48 hours)</option>
                  <option value="Within 2 weeks">Within 2 weeks</option>
                  <option value="Within 1 month">Within 1 month</option>
                  <option value="Exploring & Planning">Exploring & Planning</option>
                </select>
              </div>
            </div>

            {/* Message Area */}
            <div>
              <label className="block text-xs font-medium text-neutral-400 mb-1">Specific Queries or Requirements</label>
              <textarea
                name="enquiryMessage"
                value={formData.enquiryMessage}
                onChange={handleChange}
                rows={3}
                className="w-full bg-neutral-800 border border-neutral-700 rounded-lg p-3 text-white focus:outline-none focus:border-amber-400"
                placeholder="Ask about test-drive availability, Lucknow RTO registration timeline, trade-in exchange, or customized financing..."
              />
            </div>

            {/* Mandatory Explicit Consent Checkbox */}
            <div className="pt-2 border-t border-neutral-800 space-y-2">
              <label className="flex items-start gap-2.5 cursor-pointer text-xs text-neutral-300">
                <input
                  type="checkbox"
                  name="consentGiven"
                  checked={formData.consentGiven}
                  onChange={handleChange}
                  required
                  className="mt-0.5 rounded border-neutral-700 text-amber-500 focus:ring-amber-400"
                />
                <span>
                  <strong className="text-white">Required Consent: </strong>
                  {t('consentCheckbox')}
                </span>
              </label>

              <label className="flex items-center gap-2.5 cursor-pointer text-xs text-neutral-400">
                <input
                  type="checkbox"
                  name="optOut"
                  checked={formData.optOut}
                  onChange={handleChange}
                  className="rounded border-neutral-700 text-amber-500 focus:ring-amber-400"
                />
                <span>Opt out of promotional newsletters (consultant will only assist for this vehicle).</span>
              </label>
            </div>

            {/* Actions */}
            <div className="pt-3 flex gap-3">
              <button
                type="button"
                onClick={onClose}
                className="flex-1 py-2.5 bg-neutral-800 hover:bg-neutral-700 text-neutral-300 rounded-xl font-medium transition"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={submitting || !formData.consentGiven}
                className="flex-1 btn btn-primary py-2.5 rounded-xl font-medium flex items-center justify-center gap-2 disabled:opacity-50"
              >
                {submitting ? (
                  <>
                    <LoadingSpinner size="sm" />
                    <span>Transmitting...</span>
                  </>
                ) : (
                  <span>Submit Enquiry ↗</span>
                )}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  )
}

export default EnquiryModal
