import { Component } from 'react'
import { Link } from 'react-router-dom'

class ErrorBoundary extends Component {
  constructor(props) {
    super(props)
    this.state = { hasError: false, error: null }
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error }
  }

  componentDidCatch(error, errorInfo) {
    console.error('[AutoVista ErrorBoundary]', error, errorInfo)
  }

  handleReset = () => {
    this.setState({ hasError: false, error: null })
    window.location.href = '/'
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="error-boundary-container" role="alert">
          <div className="error-card">
            <div className="error-badge">System Notice</div>
            <h2>Something unexpected occurred</h2>
            <p className="error-description">
              Our showroom interface encountered a temporary glitch. Your data is secure.
            </p>
            {this.state.error && (
              <pre className="error-details">
                {this.state.error.message || 'Unknown error'}
              </pre>
            )}
            <div className="error-actions">
              <button
                type="button"
                className="btn btn-primary"
                onClick={this.handleReset}
              >
                Return to Showroom
              </button>
              <Link to="/" className="btn btn-secondary">
                Back to Homepage
              </Link>
            </div>
          </div>
        </div>
      )
    }

    return this.props.children
  }
}

export default ErrorBoundary
