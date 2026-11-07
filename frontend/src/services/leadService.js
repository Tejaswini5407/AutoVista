/**
 * AutoVista Lead Management & AI Integration Service
 * Connects customer enquiries, sales consultant workflows, and admin lead management.
 */

import { apiRequest } from './apiClient'

/**
 * Customer creates vehicle enquiry.
 */
export async function createLead(leadData) {
  const res = await apiRequest('/leads', {
    method: 'POST',
    body: leadData,
  })
  return res.data
}

/**
 * Customer fetches their own enquiries.
 */
export async function getMyLeads() {
  const res = await apiRequest('/leads/my-leads')
  return res.data || []
}

/**
 * Customer toggles communication opt-out.
 */
export async function updateLeadOptOut(leadId, optOut = true) {
  const res = await apiRequest(`/leads/${leadId}/opt-out?optOut=${optOut}`, {
    method: 'PATCH',
  })
  return res.data
}

/**
 * Consultant fetches their assigned leads.
 */
export async function getConsultantLeads({ status, search, date } = {}) {
  const query = new URLSearchParams()
  if (status && status !== 'ALL') query.append('status', status)
  if (search) query.append('search', search)
  if (date) query.append('date', date)

  const queryString = query.toString() ? `?${query.toString()}` : ''
  const res = await apiRequest(`/consultant/leads${queryString}`)
  return res.data || []
}

/**
 * Consultant fetches a single assigned lead.
 */
export async function getConsultantLeadById(leadId) {
  const res = await apiRequest(`/consultant/leads/${leadId}`)
  return res.data
}

/**
 * Consultant updates lead status.
 */
export async function updateConsultantLeadStatus(leadId, status, notes = '') {
  const res = await apiRequest(`/consultant/leads/${leadId}/status`, {
    method: 'PATCH',
    body: { status, notes },
  })
  return res.data
}

/**
 * Consultant fetches or generates AI buyer summary.
 */
export async function getConsultantAiSummary(leadId) {
  const res = await apiRequest(`/consultant/leads/${leadId}/ai-summary`)
  return res.data?.summary || ''
}

/**
 * Consultant generates AI first-reply draft.
 */
export async function generateConsultantAiDraft(leadId) {
  const res = await apiRequest(`/consultant/leads/${leadId}/ai-draft`, {
    method: 'POST',
  })
  return res.data
}

/**
 * Consultant edits draft reply.
 */
export async function updateConsultantDraft(leadId, draftReply) {
  const res = await apiRequest(`/consultant/leads/${leadId}/draft`, {
    method: 'PUT',
    body: { draftReply },
  })
  return res.data
}

/**
 * Consultant explicitly approves draft reply.
 */
export async function approveConsultantDraft(leadId) {
  const res = await apiRequest(`/consultant/leads/${leadId}/approve-draft`, {
    method: 'POST',
  })
  return res.data
}

/**
 * Consultant sends simulated communication.
 */
export async function sendConsultantSimulatedReply(leadId, channelParam = 'WHATSAPP_SIMULATED') {
  const channel = typeof channelParam === 'object' ? channelParam.channel : channelParam
  const customMessage = typeof channelParam === 'object' ? channelParam.messageBody : undefined
  const res = await apiRequest(`/consultant/leads/${leadId}/send-reply`, {
    method: 'POST',
    body: { channel, customMessage },
  })
  return res.data
}

/**
 * Admin retrieves all leads with filtering and search.
 */
export async function getAdminLeads({ status, consultantId, search, unassignedOnly, date } = {}) {
  const query = new URLSearchParams()
  if (status && status !== 'ALL') query.append('status', status)
  if (consultantId) query.append('consultantId', consultantId)
  if (search) query.append('search', search)
  if (unassignedOnly) query.append('unassignedOnly', unassignedOnly)
  if (date) query.append('date', date)

  const queryString = query.toString() ? `?${query.toString()}` : ''
  const res = await apiRequest(`/admin/leads${queryString}`)
  return res.data || []
}

/**
 * Admin reassigns lead to a consultant.
 */
export async function reassignAdminLead(leadId, consultantId, notes = '') {
  const res = await apiRequest(`/admin/leads/${leadId}/reassign`, {
    method: 'POST',
    body: { consultantId, notes },
  })
  return res.data
}

/**
 * Admin retrieves lead status history.
 */
export async function getAdminLeadHistory(leadId) {
  const res = await apiRequest(`/admin/leads/${leadId}/history`)
  return res.data || []
}

/**
 * Admin retrieves communication audit records for a lead.
 */
export async function getAdminLeadCommunications(leadId) {
  const res = await apiRequest(`/admin/leads/${leadId}/communications`)
  return res.data || []
}

/**
 * Admin retrieves overview metrics and consultant workloads.
 */
export async function getAdminLeadOverview() {
  const res = await apiRequest('/admin/leads/overview')
  return res.data
}
