import { useEffect, useState } from 'react'
import { Plus } from 'lucide-react'
import api, { unwrap, asList } from '../api'

const fmt = (d) => (d ? new Date(d).toLocaleDateString('fr-FR') : '—')
const msg = (err) => err.response?.data?.error || err.response?.data?.message || 'Action impossible. Vérifiez les champs.'

export default function Courses() {
  const [rows, setRows] = useState([])
  const [teachers, setTeachers] = useState([])
  const [students, setStudents] = useState([])
  const [form, setForm] = useState({ name: '', startDate: '', endDate: '', teacherId: '' })
  const [enr, setEnr] = useState({ courseId: '', studentId: '' })
  const [error, setError] = useState('')
  const [ok, setOk] = useState('')

  const load = () => api.get('/admin/courses').then((r) => setRows(asList(unwrap(r)))).catch(() => setError('Impossible de charger les cours.'))
  useEffect(() => {
    load()
    api.get('/admin/teachers').then((r) => setTeachers(asList(unwrap(r)))).catch(() => {})
    api.get('/admin/students').then((r) => setStudents(asList(unwrap(r)))).catch(() => {})
  }, [])

  async function create(e) {
    e.preventDefault(); setError(''); setOk('')
    const body = { name: form.name, startDate: form.startDate || null, endDate: form.endDate || null, teacherId: form.teacherId ? Number(form.teacherId) : null }
    try { await api.post('/admin/courses', body); setForm({ name: '', startDate: '', endDate: '', teacherId: '' }); setOk('Cours créé.'); load() }
    catch (err) { setError(msg(err)) }
  }
  async function enroll(e) {
    e.preventDefault(); setError(''); setOk('')
    try { await api.post(`/course/${enr.courseId}/students/${enr.studentId}`); setOk('Étudiant inscrit au cours.'); load() }
    catch (err) { setError(msg(err)) }
  }
  const f = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  return (
    <>
      <header className="page-head"><h1>Cours</h1><p>{rows.length} cours</p></header>
      {error && <p className="error">{error}</p>}
      {ok && <p className="success">{ok}</p>}
      <section className="box">
        <h2>Créer un cours</h2>
        <form className="inline-form five" onSubmit={create}>
          <input placeholder="Intitulé du cours" value={form.name} onChange={f('name')} required />
          <input type="date" aria-label="Date de début" value={form.startDate} onChange={f('startDate')} />
          <input type="date" aria-label="Date de fin" value={form.endDate} onChange={f('endDate')} />
          <select value={form.teacherId} onChange={f('teacherId')}>
            <option value="">Enseignant (optionnel)</option>
            {teachers.map((t) => <option key={t.id} value={t.id}>{t.name}</option>)}
          </select>
          <button className="btn small"><Plus size={16} />Créer</button>
        </form>
      </section>
      <section className="box">
        <h2>Inscrire un étudiant à un cours</h2>
        <form className="inline-form three" onSubmit={enroll}>
          <select value={enr.courseId} onChange={(e) => setEnr({ ...enr, courseId: e.target.value })} required>
            <option value="">Choisir un cours</option>{rows.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
          <select value={enr.studentId} onChange={(e) => setEnr({ ...enr, studentId: e.target.value })} required>
            <option value="">Choisir un étudiant</option>{students.map((s) => <option key={s.id} value={s.id}>{s.name}</option>)}
          </select>
          <button className="btn small">Inscrire</button>
        </form>
      </section>
      <section className="box">
        {rows.length === 0 ? <p className="empty">Aucun cours pour le moment. Créez le premier ci-dessus.</p> : (
          <div className="scroll"><table><thead><tr><th>Cours</th><th>Enseignant</th><th>Début</th><th>Fin</th><th>Étudiants</th></tr></thead>
            <tbody>{rows.map((c) => (
              <tr key={c.id}><td>{c.name}</td><td>{c.teacherName || '—'}</td><td>{fmt(c.startDate)}</td><td>{fmt(c.endDate)}</td><td>{c.enrolledStudentIds?.length ?? '—'}</td></tr>))}</tbody></table></div>
        )}
      </section>
    </>
  )
}
