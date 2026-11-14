import { useEffect, useState } from 'react'
import api, { getUser, unwrap, asList, gradeText } from '../api'

export default function StudentGrades() {
  const user = getUser()
  const [rows, setRows] = useState(null)
  const [error, setError] = useState('')
  useEffect(() => {
    api.get(`/student/${user.studentId}/grades`).then((r) => setRows(asList(unwrap(r)))).catch(() => setError('Impossible de charger vos notes.'))
  }, [])
  const scores = (rows || []).map((r) => r.totalScore).filter((n) => typeof n === 'number')
  const avg = scores.length ? (scores.reduce((a, b) => a + b, 0) / scores.length).toFixed(2) : null
  return (
    <>
      <header className="page-head"><h1>Mes notes</h1><p>Résultats publiés par vos enseignants.</p></header>
      {error && <p className="error">{error}</p>}
      {avg && <section className="stats"><div className="stat"><span>Moyenne générale</span><b>{avg}</b></div><div className="stat"><span>Matières notées</span><b>{scores.length}</b></div></section>}
      <section className="box">
        {rows && rows.length === 0 ? <p className="empty">Aucune note publiée pour le moment.</p> : (
          <div className="scroll"><table><thead><tr><th>Matière</th><th>Cours</th><th>Note</th><th>Mention</th><th>Date</th></tr></thead>
            <tbody>{(rows || []).map((r) => (
              <tr key={r.id}><td>{r.subjectName}</td><td>{r.courseName}</td><td>{r.totalScore ?? '—'}</td><td><span className="pill">{gradeText(r.grade)}</span></td>
                <td>{r.gradedAt ? new Date(r.gradedAt).toLocaleDateString('fr-FR') : '—'}</td></tr>))}</tbody></table></div>
        )}
      </section>
    </>
  )
}
