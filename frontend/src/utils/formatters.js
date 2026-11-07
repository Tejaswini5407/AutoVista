/**
 * Formatting utilities for AutoVista Dealership Platform.
 * Lucknow, Uttar Pradesh, India
 */

/**
 * Format a number as Indian Rupee (INR) currency.
 * e.g., 1245000 -> ₹12,45,000
 */
export function formatINR(amount) {
  if (amount === undefined || amount === null || isNaN(amount)) {
    return '₹0'
  }
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(amount)
}

/**
 * Masks a vehicle registration number to preserve privacy.
 * Mandatory Requirement #7: Mask vehicle registration numbers.
 * e.g., "UP32 AB 1234" -> "UP32 •• ••••"
 * e.g., "UP-32-AB-1234" -> "UP-32-••-••••"
 */
export function maskRegistrationNumber(regNumber) {
  if (!regNumber || typeof regNumber !== 'string') {
    return 'UP32 •• ••••'
  }

  const clean = regNumber.trim()
  const parts = clean.split(/[\s-]+/)

  if (parts.length >= 4) {
    // Standard format: State (UP) + District (32) + Series (AB) + Unique (1234)
    return `${parts[0]}${parts[1]} •• ••••`
  }

  // Fallback: keep first 4 chars (e.g. UP32), mask the rest
  if (clean.length > 4) {
    const visible = clean.slice(0, 4)
    return `${visible} •• ••••`
  }

  return `${clean} •• ••••`
}

/**
 * Format odometer kilometers reading.
 * e.g., 41200 -> "41,200 km"
 */
export function formatMileage(km) {
  if (km === undefined || km === null || isNaN(km)) {
    return '0 km'
  }
  return `${new Intl.NumberFormat('en-IN').format(km)} km`
}

/**
 * Format date in Indian locale format (e.g., 04 Nov 2026).
 */
export function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('en-IN', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  })
}
