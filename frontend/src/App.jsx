import { BrowserRouter, Route, Routes } from 'react-router-dom'
import './index.css'
import './App.css'

import LanguageProvider from './context/LanguageProvider.jsx'
import AuthProvider from './context/AuthProvider.jsx'
import SiteLayout from './layouts/SiteLayout.jsx'

// Pages
import HomePage from './pages/HomePage.jsx'
import CarsPage from './pages/CarsPage.jsx'
import CarDetailsPage from './pages/CarDetailsPage.jsx'
import UsedCarsPage from './pages/UsedCarsPage.jsx'
import UsedCarDetailsPage from './pages/UsedCarDetailsPage.jsx'
import BookTestDrivePage from './pages/BookTestDrivePage.jsx'
import LoginPage from './pages/LoginPage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import ConsultantDashboardPage from './pages/ConsultantDashboardPage.jsx'
import AdminDashboardPage from './pages/AdminDashboardPage.jsx'
import NotFoundPage from './pages/NotFoundPage.jsx'

function App() {
  return (
    <LanguageProvider>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route element={<SiteLayout />}>
              <Route index element={<HomePage />} />
              <Route path="cars" element={<CarsPage />} />
              <Route path="cars/:id" element={<CarDetailsPage />} />
              <Route path="used-cars" element={<UsedCarsPage />} />
              <Route path="used-cars/:id" element={<UsedCarDetailsPage />} />
              <Route path="book-test-drive" element={<BookTestDrivePage />} />
              <Route path="login" element={<LoginPage />} />
              <Route path="register" element={<RegisterPage />} />
              <Route path="consultant/dashboard" element={<ConsultantDashboardPage />} />
              <Route path="admin/dashboard" element={<AdminDashboardPage />} />
              <Route path="*" element={<NotFoundPage />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </LanguageProvider>
  )
}

export default App
