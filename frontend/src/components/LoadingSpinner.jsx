function LoadingSpinner({ label = 'Loading showroom details...', size = 'md' }) {
  return (
    <div className={`loading-container loading-${size}`} role="status" aria-live="polite">
      <div className="spinner-ring" aria-hidden="true" />
      {label && <p className="loading-label">{label}</p>}
    </div>
  )
}

export default LoadingSpinner
