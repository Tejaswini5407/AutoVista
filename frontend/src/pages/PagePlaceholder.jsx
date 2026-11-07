function PagePlaceholder({ eyebrow = 'AutoVista', title, description }) {
  return (
    <section className="placeholder-page">
      <p className="eyebrow">{eyebrow}</p>
      <h1>{title}</h1>
      <p className="placeholder-description">{description}</p>
    </section>
  )
}

export default PagePlaceholder