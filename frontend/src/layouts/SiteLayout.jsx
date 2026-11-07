import { Outlet } from 'react-router-dom'
import Navbar from '../components/Navbar.jsx'
import Footer from '../components/Footer.jsx'
import ErrorBoundary from '../components/ErrorBoundary.jsx'

function SiteLayout() {
  return (
    <div className="site-shell">
      <Navbar />
      <main className="main-content">
        <ErrorBoundary>
          <Outlet />
        </ErrorBoundary>
      </main>
      <Footer />
    </div>
  )
}

export default SiteLayout