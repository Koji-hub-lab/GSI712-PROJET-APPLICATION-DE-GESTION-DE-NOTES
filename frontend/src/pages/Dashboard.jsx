import { useEffect, useState } from 'react'
import api, { getUser, roleGroup, unwrap, asList } from '../api'

const LABELS = { totalCourses: 'Cours', totalGrades: 'Notes', totalStudents: 'Étudiants', totalTeachers: 'Enseignants',
  students: 'Étudiants', teachers: 'Enseignants', courses: 'Cours', subjects: 'Matières', enrollments: 'Inscriptions' }

export default function Dashboard() {
  const user = getUser()
  const group = roleGroup(user?.role)
  const [stats, setStats] = useState(null)
  const [courses, setCourses] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    const q = group === 'student' ? { params: { studentId: user.studentId } } : undefined
    api.get(`/${group}/stats`, q).then((r) => setStats(unwrap(r))).catch(() => setError('Impossible de charger les statistiques.'))
    if (group === 'student') api.get('/student/courses', q).then((r) => setCourses(asList(unwrap(r)))).catch(() => {})
  }, [])

  const cards = Object.entries(stats || {}).filter(([, v]) => typeof v === 'number')
  return (
    <>
      <header className="page-head"><h1>Bonjour, {user?.name?.split(' ')[0] || 'bienvenue'}</h1><p>Voici un aperçu de votre espace.</p></header>
      {error && <p className="error">{error}</p>}
      <section className="stats">
        {cards.map(([k, v]) => (<div className="stat" key={k}><span>{LABELS[k] || k}</span><b>{v}</b></div>))}
      </section>
      {group === 'student' && (
        <section className="box">
          <h2>Mes cours</h2>
          {courses.length === 0 ? <p className="empty">Vous n’êtes inscrit à aucun cours pour le moment.</p> : (
            <ul className="list">{courses.map((c, i) => <li key={c.id ?? c.courseId ?? i}>{c.courseName || c.name || `Cours ${i + 1}`}</li>)}</ul>
          )}
        </section>
      )}
    </>
  )
}
