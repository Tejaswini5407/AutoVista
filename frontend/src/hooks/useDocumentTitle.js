import { useEffect } from 'react'

export function useDocumentTitle(title) {
  useEffect(() => {
    const prevTitle = document.title
    document.title = title ? `${title} | AutoVista Lucknow` : 'AutoVista | Premium Automotive Dealership'
    return () => {
      document.title = prevTitle
    }
  }, [title])
}

export default useDocumentTitle
