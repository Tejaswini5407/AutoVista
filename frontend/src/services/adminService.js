import apiRequest from './apiClient.js'

/**
 * Admin Service: Role-restricted backend communication for dealership management.
 * All requests require a valid JWT with ROLE_ADMIN.
 */

// 1. Dashboard Stats
export async function getAdminStats() {
  const res = await apiRequest('/admin/stats')
  return res.data
}

// 2. New Car Inventory
export async function getAdminModels() {
  const res = await apiRequest('/admin/cars')
  return res.data
}

export async function createCarModel(data) {
  const res = await apiRequest('/admin/cars', {
    method: 'POST',
    body: data,
  })
  return res.data
}

export async function updateCarModel(id, data) {
  const res = await apiRequest(`/admin/cars/${id}`, {
    method: 'PUT',
    body: data,
  })
  return res.data
}

export async function toggleCarModelStatus(id, active) {
  const res = await apiRequest(`/admin/cars/${id}/status?active=${active}`, {
    method: 'PATCH',
  })
  return res.data
}

export async function createCarVariant(modelId, data) {
  const res = await apiRequest(`/admin/cars/${modelId}/variants`, {
    method: 'POST',
    body: data,
  })
  return res.data
}

export async function updateCarVariant(variantId, data) {
  const res = await apiRequest(`/admin/cars/variants/${variantId}`, {
    method: 'PUT',
    body: data,
  })
  return res.data
}

export async function toggleCarVariantStatus(variantId, available) {
  const res = await apiRequest(`/admin/cars/variants/${variantId}/status?available=${available}`, {
    method: 'PATCH',
  })
  return res.data
}

// 3. Pre-Owned Vehicle Inventory
export async function getAdminUsedCars() {
  const res = await apiRequest('/admin/used-cars')
  return res.data
}

export async function createUsedCar(data) {
  const res = await apiRequest('/admin/used-cars', {
    method: 'POST',
    body: data,
  })
  return res.data
}

export async function updateUsedCar(id, data) {
  const res = await apiRequest(`/admin/used-cars/${id}`, {
    method: 'PUT',
    body: data,
  })
  return res.data
}

export async function updateUsedCarStatus(id, status) {
  const res = await apiRequest(`/admin/used-cars/${id}/status?status=${status}`, {
    method: 'PATCH',
  })
  return res.data
}

// 4. City-Wise Pricing Engine
export async function getAdminPricingRules() {
  const res = await apiRequest('/admin/pricing')
  return res.data
}

export async function createOrUpdatePricingRule(data) {
  const res = await apiRequest('/admin/pricing', {
    method: 'POST',
    body: data,
  })
  return res.data
}

export async function updatePricingRule(id, data) {
  const res = await apiRequest(`/admin/pricing/${id}`, {
    method: 'PUT',
    body: data,
  })
  return res.data
}
