import { useEffect } from 'react'
import { X } from 'lucide-react'

// Panneau latéral de fiche détaillée. Se ferme avec la touche Échap ou un clic à côté.
export default function FicheDetail({ titre, sousTitre, onClose, children }) {
  useEffect(() => {
    const h = (e) => e.key === 'Escape' && onClose()
    window.addEventListener('keydown', h)
    return () => window.removeEventListener('keydown', h)
  }, [onClose])
  return (
    <div className="fiche-overlay" onClick={onClose}>
      <aside className="fiche" role="dialog" aria-modal="true" aria-label={titre} onClick={(e) => e.stopPropagation()}>
        <header className="fiche-head">
          <div><h2>{titre}</h2>{sousTitre && <p>{sousTitre}</p>}</div>
          <button className="icon-btn dark" onClick={onClose} aria-label="Fermer la fiche"><X size={18} /></button>
        </header>
        {children}
      </aside>
    </div>
  )
}

// Une ligne « libellé : valeur » de la fiche.
export function Champ({ label, children }) {
  return <div className="champ"><dt>{label}</dt><dd>{children || '—'}</dd></div>
}
