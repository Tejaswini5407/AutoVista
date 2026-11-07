function Badge({ children, variant = 'default', size = 'md' }) {
  const variantClass = `badge-${variant}`
  const sizeClass = `badge-${size}`

  return (
    <span className={`badge ${variantClass} ${sizeClass}`}>
      {children}
    </span>
  )
}

export default Badge
