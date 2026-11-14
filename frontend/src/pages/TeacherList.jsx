import { useEffect, useState } from 'react'
import api, { getUser, unwrap, asList } from '../api'

const CFG = {
  courses: { title: 'Mes cours', cols: [['Cours', 'name'], ['Étudiants', 'studentsCount'], ['Matières', 'subjectsCount']], empty: 'Aucun cours ne vous est attribué pour le moment.' },
  students: { title: 'Mes étudiants', cols: [['Nom', 'name'], ['E-mail', 'email'], ['Cours', 'courseName']], empty: 'Aucun étudiant inscrit à vos cours pour le moment.' },
}

export default function TeacherList({ kind }) {
  const { title, cols, empty } = CFG[kind]
  const user = getUser()
  const [rows, setRows] = useState(null)
  const [error, setError] = useState('')
  useEffect(() => {
    setRows(null); setError('')
    api.get(`/teacher/${kind}`, { params: { teacherId: user.teacherId } })
      .then((r) => setRows(asList(unwrap(r)))).catch(() => setError('Impossible de charger la liste.'))
  }, [kind])
  return (
    <>
      <header className="page-head"><h1>{title}</h1><p>{rows ? `${rows.length} résultat(s)` : 'Chargement…'}</p></header>
      {error && <p className="error">{error}</p>}
      <section className="box">
        {rows && rows.length === 0 ? <p className="empty">{empty}</p> : (
          <div className="scroll"><table><thead><tr>{cols.map(([h]) => <th key={h}>{h}</th>)}</tr></thead>
            <tbody>{(rows || []).map((r, i) => <tr key={`${r.id}-${i}`}>{cols.map(([h, k]) => <td key={h}>{r[k] ?? '—'}</td>)}</tr>)}</tbody></table></div>
        )}
      </section>
    </>
  )
}
