import { useEffect, useState } from 'react'
import { Printer } from 'lucide-react'
import api, { getUser, unwrap, asList, gradeText } from '../api'

export default function Transcript() {
  const user = getUser()
  const [rows, setRows] = useState(null)
  const [error, setError] = useState('')
  useEffect(() => {
    api.get(`/student/${user.studentId}/grades`).then((r) => setRows(asList(unwrap(r)))).catch(() => setError('Impossible de charger vos notes.'))
  }, [])
  const scores = (rows || []).map((r) => r.totalScore).filter((n) => typeof n === 'number')
  const avg = scores.length ? scores.reduce((a, b) => a + b, 0) / scores.length : null
  const decision = avg === null ? '—' : avg >= 10 ? 'Admis(e)' : 'Non admis(e)'
  const today = new Date().toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' })

  return (
    <>
      <header className="page-head no-print"><h1>Relevé de notes</h1><p>Document à imprimer ou à enregistrer en PDF.</p></header>
      {error && <p className="error">{error}</p>}
      <button className="btn small no-print" style={{ marginBottom: 18 }} onClick={() => window.print()}><Printer size={16} />Imprimer / Enregistrer en PDF</button>
      <article className="sheet">
        <div className="sheet-head">
          <img src="/logo-estlc.jpg" alt="ESTLC" />
          <div><b>École Supérieure de Transport, de Logistique et de Commerce</b><span>Université d’Ebolowa – Ambam</span></div>
        </div>
        <h2 className="sheet-title">RELEVÉ DE NOTES</h2>
        <p className="sheet-id"><b>{user?.name}</b><br />{user?.email}</p>
        {rows && rows.length === 0 ? <p className="empty">Aucune note publiée pour le moment.</p> : (
          <table><thead><tr><th>Matière</th><th>Cours</th><th>Note /20</th><th>Mention</th></tr></thead>
            <tbody>{(rows || []).map((r) => <tr key={r.id}><td>{r.subjectName}</td><td>{r.courseName}</td><td>{r.totalScore ?? '—'}</td><td>{gradeText(r.grade)}</td></tr>)}</tbody></table>
        )}
        <div className="sheet-sum"><span>Moyenne générale : <b>{avg === null ? '—' : avg.toFixed(2) + ' / 20'}</b></span><span>Décision : <b>{decision}</b></span></div>
        <p className="sheet-foot">Document généré le {today}. Ce relevé est informatif et ne remplace pas le document officiel signé par l’administration.</p>
      </article>
    </>
  )
}
