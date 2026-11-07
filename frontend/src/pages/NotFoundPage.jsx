import { Link } from 'react-router-dom'
import useDocumentTitle from '../hooks/useDocumentTitle.js'

function NotFoundPage() {
  useDocumentTitle('Page Not Found')

  return (
    <div className="not-found-page">
      <div className="container py-16 text-center">
        <div className="empty-state-card">
          <span className="eyebrow-badge">404 Error</span>
          <h1 className="not-found-title">Route Not Found</h1>
          <p className="not-found-desc">
            The page you requested does not exist in the AutoVista showroom network.
          </p>
          <div className="not-found-actions mt-6">
            <Link to="/" className="btn btn-primary btn-lg">
              Return to AutoVista Home
            </Link>
            <Link to="/cars" className="btn btn-outline btn-lg">
              Explore New Cars
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}

export default NotFoundPage
