/**
 * AutoVista Test Drive Booking Service
 * Connects frontend booking flow and admin SLA management to Spring Boot REST endpoints.
 */

import { apiRequest } from './apiClient'

export const OPERATIONAL_TIME_SLOTS = [
  '10:00 AM - 11:00 AM',
  '11:00 AM - 12:00 PM',
  '02:00 PM - 03:00 PM',
  '03:00 PM - 04:00 PM',
  '04:00 PM - 05:00 PM',
  '05:00 PM - 06:00 PM',
]

/**
 * Retrieve dynamic real-time slot availability for a given showroom and date.
 */
export async function getAvailableSlots(showroomId = 1, date) {
  const query = new URLSearchParams()
  if (showroomId) query.append('showroomId', showroomId)
  if (date) query.append('date', date)

  const res = await apiRequest(`/test-drives/slots?${query.toString()}`)
  return res.data
}

/**
 * Submit customer test drive booking with concurrency-safe duplicate prevention.
 */
export async function createTestDriveBooking(bookingData) {
  const res = await apiRequest('/test-drives', {
    method: 'POST',
    body: bookingData,
  })
  return res.data
}

/**
 * Fetch authenticated customer's own test drive bookings (customer data isolation).
 */
export async function getMyBookings() {
  const res = await apiRequest('/test-drives/my-bookings')
  return res.data || []
}

/**
 * Fetch individual booking details.
 */
export async function getBookingById(id) {
  const res = await apiRequest(`/test-drives/${id}`)
  return res.data
}

/**
 * Cancel a test drive booking and release the slot.
 */
export async function cancelBooking(id, reason = '') {
  const query = reason ? `?reason=${encodeURIComponent(reason)}` : ''
  const res = await apiRequest(`/test-drives/${id}/cancel${query}`, {
    method: 'PATCH',
  })
  return res.data
}

/**
 * Fetch mock notification audit events for a specific booking.
 */
export async function getBookingNotifications(id) {
  const res = await apiRequest(`/test-drives/${id}/notifications`)
  return res.data || []
}

/**
 * Admin: Get all test-drive bookings with optional filters (date, showroom, status).
 */
export async function getAdminBookings(filters = {}) {
  const query = new URLSearchParams()
  if (filters.date) query.append('date', filters.date)
  if (filters.showroomId) query.append('showroomId', filters.showroomId)
  if (filters.status && filters.status !== 'ALL') query.append('status', filters.status)

  const res = await apiRequest(`/admin/bookings?${query.toString()}`)
  return res.data || []
}

/**
 * Admin: Update booking status (CONFIRMED, COMPLETED, CANCELLED, REJECTED).
 */
export async function updateAdminBookingStatus(id, status, notes = '') {
  const res = await apiRequest(`/admin/bookings/${id}/status`, {
    method: 'PATCH',
    body: { status, notes },
  })
  return res.data
}

/**
 * Admin: Fetch all mock notification events across the platform.
 */
export async function getAllAdminNotifications() {
  const res = await apiRequest('/admin/notifications')
  return res.data || []
}
