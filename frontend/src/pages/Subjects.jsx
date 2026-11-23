import { useEffect, useState } from 'react'
import { Plus } from 'lucide-react'
import api, { unwrap, asList } from '../api'

const msg = (err) => err.response?.data?.error || err.response?.data?.message || 'Action impossible. Vérifiez les champs.'

export default function Subjects() {
  const [subjects, setSubjects] = useState([])
  const [teachers, setTeachers] = useState([])
  const [courses, setCourses] = useState([])
  const [form, setForm] = useState({ name: '', teacherId: '' })
  const [link, setLink] = useState({ subjectId: '', courseId: '' })
  const [error, setError] = useState('')
  const [ok, setOk] = useState('')

  const load = () => api.get('/admin/subjects/all').then((r) => setSubjects(asList(unwrap(r)))).catch(() => setError('Impossible de charger les matières.'))
  useEffect(() => {
    load()
    api.get('/admin/teachers').then((r) => setTeachers(asList(unwrap(r)))).catch(() => {})
    api.get('/admin/courses').then((r) => setCourses(asList(unwrap(r)))).catch(() => {})
  }, [])

  async function create(e) {
    e.preventDefault(); setError(''); setOk('')
    try { await api.post('/admin/subjects/create', { name: form.name, teacherId: form.teacherId ? Number(form.teacherId) : null }); setForm({ name: '', teacherId: '' }); setOk('Matière créée.'); load() }
    catch (err) { setError(msg(err)) }
  }
  async function assign(e) {
    e.preventDefault(); setError(''); setOk('')
    try { await api.post(`/admin/courses/${link.courseId}/subjects`, { subjectIds: [Number(link.subjectId)] }); setOk('Matière rattachée au cours.'); load() }
    catch (err) { setError(msg(err)) }
  }

  return (
    <>
      <header className="page-head"><h1>Matières</h1><p>{subjects.length} matière(s)</p></header>
      {error && <p className="error">{error}</p>}
      {ok && <p className="success">{ok}</p>}
      <section className="box">
        <h2>Créer une matière</h2>
        <form className="inline-form three" onSubmit={create}>
          <input placeholder="Intitulé de la matière" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
          <select value={form.teacherId} onChange={(e) => setForm({ ...form, teacherId: e.target.value })}>
            <option value="">Enseignant (optionnel)</option>
            {teachers.map((t) => <option key={t.id} value={t.userId ?? t.id}>{t.name}</option>)}
          </select>
          <button className="btn small"><Plus size={16} />Créer</button>
        </form>
      </section>
      <section className="box">
        <h2>Rattacher une matière à un cours</h2>
        <form className="inline-form three" onSubmit={assign}>
          <select value={link.subjectId} onChange={(e) => setLink({ ...link, subjectId: e.target.value })} required>
            <option value="">Choisir une matière</option>{subjects.map((s) => <option key={s.id} value={s.id}>{s.name}</option>)}
          </select>
          <select value={link.courseId} onChange={(e) => setLink({ ...link, courseId: e.target.value })} required>
            <option value="">Choisir un cours</option>{courses.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
          <button className="btn small">Rattacher</button>
        </form>
      </section>
      <section className="box">
        {subjects.length === 0 ? <p className="empty">Aucune matière pour le moment.</p> : (
          <div className="scroll"><table><thead><tr><th>Matière</th><th>Enseignant</th><th>Cours</th></tr></thead>
            <tbody>{subjects.map((s) => <tr key={s.id}><td>{s.name}</td><td>{s.teacherName || '—'}</td><td>{s.courseName || '—'}</td></tr>)}</tbody></table></div>
        )}
      </section>
    </>
  )
}
