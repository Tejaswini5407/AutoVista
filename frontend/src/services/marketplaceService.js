/**
 * AutoVista Marketplace API Service
 * Centralizes REST communication for new-car and used-car catalogs,
 * car specifications, and city-level on-road pricing calculations.
 */

import { apiRequest } from './apiClient.js'

/**
 * Fetch all active new car models from PostgreSQL backend.
 * Endpoint: GET /api/cars
 * @returns {Promise<Array>} List of car models with nested variants
 */
export async function getNewCars() {
  const response = await apiRequest('/cars')
  return response.data || []
}

/**
 * Fetch a single car model by ID with all its variants.
 * Endpoint: GET /api/cars/{id}
 * @param {string|number} id
 * @returns {Promise<Object>}
 */
export async function getNewCarById(id) {
  const response = await apiRequest(`/cars/${id}`)
  return response.data
}

/**
 * Fetch on-road price calculation breakdown for a specific variant in a city.
 * Endpoint: GET /api/cars/variants/{variantId}/pricing?city={city}
 * @param {number|string} variantId
 * @param {string} city - Defaults to "Lucknow"
 * @returns {Promise<Object>} CityPriceResponse
 */
export async function getVariantPricing(variantId, city = 'Lucknow') {
  const response = await apiRequest(
    `/cars/variants/${variantId}/pricing?city=${encodeURIComponent(city)}`
  )
  return response.data
}

/**
 * Fetch certified used car inventory.
 * Endpoint: GET /api/used-cars?status={status}
 * @param {string} status - 'ALL', 'AVAILABLE', 'BOOKED'
 * @returns {Promise<Array>} List of used cars
 */
export async function getUsedCars(status = 'ALL') {
  const query = status && status !== 'ALL' ? `?status=${encodeURIComponent(status)}` : ''
  const response = await apiRequest(`/used-cars${query}`)
  return response.data || []
}

/**
 * Fetch a single certified pre-owned car by ID.
 * Endpoint: GET /api/used-cars/{id}
 * @param {string|number} id
 * @returns {Promise<Object>}
 */
export async function getUsedCarById(id) {
  const response = await apiRequest(`/used-cars/${id}`)
  return response.data
}

/**
 * Normalize a backend CarModelResponse for display in common components like CarCard.
 * Ensures consistent property names without losing variant details.
 */
export function normalizeCarModelForCard(model) {
  if (!model) return null
  const primaryVariant = model.variants && model.variants.length > 0 ? model.variants[0] : null

  return {
    id: model.id,
    brand: model.brand,
    modelName: model.modelName,
    variant: primaryVariant ? primaryVariant.variantName : model.modelName,
    variantName: primaryVariant ? primaryVariant.variantName : model.modelName,
    year: model.year || 2026,
    bodyType: model.bodyType,
    fuelType: primaryVariant ? primaryVariant.fuelType : 'PETROL',
    transmission: primaryVariant ? primaryVariant.transmission : 'AUTOMATIC',
    engine: primaryVariant ? primaryVariant.engineSpecs : '',
    power: primaryVariant ? primaryVariant.powerSpecs : '',
    torque: primaryVariant ? primaryVariant.torqueSpecs : '',
    exShowroomPrice: primaryVariant ? primaryVariant.exShowroomPrice : 0,
    heroImage: model.heroImageUrl || (primaryVariant ? primaryVariant.heroImageUrl : ''),
    heroImageUrl: model.heroImageUrl || (primaryVariant ? primaryVariant.heroImageUrl : ''),
    status: primaryVariant && primaryVariant.isAvailable ? 'Available' : 'Available',
    features: primaryVariant ? primaryVariant.features : [],
    colors: primaryVariant ? primaryVariant.colors : [],
    variants: model.variants || [],
    description: model.description,
  }
}
