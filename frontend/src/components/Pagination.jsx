import { ChevronLeft, ChevronRight } from 'lucide-react'

// Pagination simple : page courante, taille de page, total d'éléments.
export default function Pagination({ page, pageSize, total, onChange }) {
  const pages = Math.max(1, Math.ceil(total / pageSize))
  const from = total === 0 ? 0 : (page - 1) * pageSize + 1
  const to = Math.min(page * pageSize, total)
  return (
    <nav className="pager" aria-label="Pagination">
      <span className="pager-info">{from} à {to} sur {total}</span>
      <div className="pager-btns">
        <button className="pg" onClick={() => onChange(page - 1)} disabled={page <= 1} aria-label="Page précédente"><ChevronLeft size={16} /></button>
        {Array.from({ length: pages }, (_, i) => i + 1).map((n) => (
          <button key={n} className={'pg' + (n === page ? ' on' : '')} onClick={() => onChange(n)} aria-current={n === page ? 'page' : undefined}>{n}</button>
        ))}
        <button className="pg" onClick={() => onChange(page + 1)} disabled={page >= pages} aria-label="Page suivante"><ChevronRight size={16} /></button>
      </div>
    </nav>
  )
}
