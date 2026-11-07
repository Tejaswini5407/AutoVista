/**
 * Curated Automobile Inventory for AutoVista Dealership (Lucknow, India).
 * Includes exact sample inventory mandated by client requirements.
 */

export const NEW_CARS = [
  {
    id: 'new-model-h-zx-2026',
    brand: 'Model H',
    variant: 'ZX Automatic Petrol',
    year: 2026,
    condition: 'New',
    exShowroomPrice: 1245000, // Mandated exact sample
    bodyType: 'Premium Sedan',
    fuelType: 'Petrol',
    transmission: 'Automatic',
    engine: '1.5L i-VTEC Smart Hybrid',
    mileageKm: 0,
    power: '121 PS @ 6,600 rpm',
    torque: '145 Nm @ 4,300 rpm',
    seating: 5,
    colors: ['Radiant Red Metallic', 'Obsidian Black', 'Platinum White Pearl', 'Lunar Silver'],
    heroImage: 'https://images.unsplash.com/photo-1617788138017-80ad40651399?auto=format&fit=crop&w=1200&q=80',
    gallery: [
      'https://images.unsplash.com/photo-1617788138017-80ad40651399?auto=format&fit=crop&w=800&q=80',
      'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?auto=format&fit=crop&w=800&q=80',
      'https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=800&q=80',
    ],
    features: [
      'Panoramic Sunroof',
      'ADAS Level 2 Autonomous Safety',
      '10.2-inch Touchscreen Infotainment',
      'Wireless Apple CarPlay & Android Auto',
      'Ventilated Front Seats',
      '6 Airbags & Electronic Stability Control',
    ],
    tagline: 'Refined craftsmanship meets intelligent hybrid performance.',
    status: 'Available',
    location: 'AutoVista Experience Centre, Gomti Nagar, Lucknow',
  },
  {
    id: 'new-model-x-gt-2026',
    brand: 'Model X',
    variant: 'GT Luxury Turbo Petrol',
    year: 2026,
    condition: 'New',
    exShowroomPrice: 1980000,
    bodyType: 'Executive SUV',
    fuelType: 'Petrol',
    transmission: 'Automatic (DCT)',
    engine: '2.0L Turbocharged Dual VVT',
    mileageKm: 0,
    power: '188 PS @ 5,500 rpm',
    torque: '320 Nm @ 1,500-4,000 rpm',
    seating: 7,
    colors: ['Carbon Black', 'Nardo Grey', 'Imperial Blue'],
    heroImage: 'https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=1200&q=80',
    gallery: [
      'https://images.unsplash.com/photo-1549399542-7e3f8b79c341?auto=format&fit=crop&w=800&q=80',
      'https://images.unsplash.com/photo-1583121274602-3e2820c69888?auto=format&fit=crop&w=800&q=80',
    ],
    features: [
      'All-Wheel Drive (AWD)',
      'Bose 12-Speaker Surround Audio',
      '360-Degree HD Camera with Blind Spot Monitoring',
      'Heated and Memory Seats',
    ],
    tagline: 'Dominate every road with uncompromising power and presence.',
    status: 'Available',
    location: 'AutoVista Experience Centre, Gomti Nagar, Lucknow',
  },
  {
    id: 'new-model-e-ev-2026',
    brand: 'Model E',
    variant: 'Long Range EV Tech Pack',
    year: 2026,
    condition: 'New',
    exShowroomPrice: 2450000,
    bodyType: 'Electric Crossover',
    fuelType: 'Electric',
    transmission: 'Automatic (Direct Drive)',
    engine: 'Permanent Magnet Synchronous Motor (72 kWh)',
    mileageKm: 0,
    power: '215 hp',
    torque: '350 Nm Instant',
    seating: 5,
    colors: ['Emerald Frost', 'Monaco White', 'Cosmic Blue'],
    heroImage: 'https://images.unsplash.com/photo-1563720223185-11003d516935?auto=format&fit=crop&w=1200&q=80',
    gallery: [
      'https://images.unsplash.com/photo-1563720223185-11003d516935?auto=format&fit=crop&w=800&q=80',
    ],
    features: [
      '520 km ARAI Certified Range',
      'Fast DC Charging (10-80% in 28 mins)',
      'Regenerative Braking Paddle Controls',
      'Clean Air Purifier with PM2.5 Monitor',
    ],
    tagline: 'Silent luxury and zero-emission dynamism for modern cities.',
    status: 'Available',
    location: 'AutoVista Experience Centre, Gomti Nagar, Lucknow',
  },
]

export const USED_CARS = [
  {
    id: 'used-model-c-vx-2022',
    brand: 'Model C',
    variant: 'VX Diesel',
    year: 2022,
    condition: 'Certified Pre-Owned',
    price: 845000, // Mandated exact sample: ₹8,45,000
    odometerKm: 41200, // Mandated exact sample: 41,200 km
    rawRegistrationNumber: 'UP32 BK 8921',
    fuelType: 'Diesel',
    transmission: 'Manual',
    ownersCount: 1,
    insuranceValidity: 'Aug 2027 (Comprehensive)',
    certifiedInspectionPoints: 150,
    inspectionPassed: true,
    status: 'Available',
    location: 'AutoVista Gomti Nagar, Lucknow',
    heroImage: 'https://images.unsplash.com/photo-1550355291-bbee04a92027?auto=format&fit=crop&w=1200&q=80',
    features: [
      'Complete Dealership Service History',
      'No Accident Record (Certified Structural Integrity)',
      'Cruise Control & Push Button Start',
      'Touchscreen with Apple CarPlay',
      'Rear Parking Camera with Sensors',
    ],
    tagline: 'Pristine single-owner diesel sedan with exceptional highway mileage.',
  },
  {
    id: 'used-model-s-lx-2021',
    brand: 'Model S',
    variant: 'LX Petrol',
    year: 2021,
    condition: 'Certified Pre-Owned',
    price: 512000, // Mandated exact sample: ₹5,12,000
    odometerKm: 58600, // Mandated exact sample: 58,600 km
    rawRegistrationNumber: 'UP32 DA 4410',
    fuelType: 'Petrol',
    transmission: 'Manual',
    ownersCount: 1,
    insuranceValidity: 'Nov 2026 (Third Party)',
    certifiedInspectionPoints: 150,
    inspectionPassed: true,
    status: 'Booked', // Mandated exact sample: status Booked
    location: 'AutoVista Gomti Nagar, Lucknow',
    heroImage: 'https://images.unsplash.com/photo-1542282088-72c9c27ed0cd?auto=format&fit=crop&w=1200&q=80',
    features: [
      'Verified Odometer Mileage',
      'Original Manufacturer Paintwork',
      'Dual Front Airbags & ABS with EBD',
      'Chilled Automatic Climate Control',
    ],
    tagline: 'Reliable, well-maintained city cruiser currently reserved under deposit.',
  },
  {
    id: 'used-model-v-zx-2023',
    brand: 'Model V',
    variant: 'ZX Luxury Automatic',
    year: 2023,
    condition: 'Certified Pre-Owned',
    price: 1120000,
    odometerKm: 22400,
    rawRegistrationNumber: 'UP32 EZ 1988',
    fuelType: 'Petrol',
    transmission: 'Automatic',
    ownersCount: 1,
    insuranceValidity: 'Jan 2028 (Zero Depreciation)',
    certifiedInspectionPoints: 150,
    inspectionPassed: true,
    status: 'Available',
    location: 'AutoVista Gomti Nagar, Lucknow',
    heroImage: 'https://images.unsplash.com/photo-1502877338535-766e1452684a?auto=format&fit=crop&w=1200&q=80',
    features: [
      'Under Active Factory Warranty',
      '360 Parking Assist',
      'Leatherette Upholstery',
    ],
    tagline: 'Nearly new luxury crossover with pristine cabin and low mileage.',
  },
]

export function getAllNewCars() {
  return [...NEW_CARS]
}

export function getAllUsedCars() {
  return [...USED_CARS]
}

export function getNewCarById(id) {
  return NEW_CARS.find((car) => car.id === id) || null
}

export function getUsedCarById(id) {
  return USED_CARS.find((car) => car.id === id) || null
}

export function getCarById(id) {
  return getNewCarById(id) || getUsedCarById(id)
}
