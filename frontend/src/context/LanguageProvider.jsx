import { useState, useEffect } from 'react'
import LanguageContext from './LanguageContext.js'
import { translations } from '../utils/translations.js'

export function LanguageProvider({ children }) {
  const [language, setLanguage] = useState(() => {
    return localStorage.getItem('autovista_lang') || 'en'
  })

  useEffect(() => {
    localStorage.setItem('autovista_lang', language)
    document.documentElement.lang = language
  }, [language])

  const toggleLanguage = () => {
    setLanguage((prev) => (prev === 'en' ? 'hi' : 'en'))
  }

  const t = (key) => {
    return translations[language]?.[key] || translations.en[key] || key
  }

  return (
    <LanguageContext.Provider value={{ language, setLanguage, toggleLanguage, t }}>
      {children}
    </LanguageContext.Provider>
  )
}

export default LanguageProvider
