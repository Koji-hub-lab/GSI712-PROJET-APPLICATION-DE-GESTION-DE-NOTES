import { useEffect, useState } from 'react'
import api, { getUser, unwrap, asList, letterOf, gradeText } from '../api'

const msg = (err) => err.response?.data?.error || err.response?.data?.message || 'Enregistrement impossible. L’étudiant est peut-être non affecté à cette matière.'

export default function TeacherGrades() {
  const user = getUser()
  const [courses, setCourses] = useState([])
  const [subjects, setSubjects] = useState([])
  const [students, setStudents] = useState([])
  const [saved, setSaved] = useState({})
  const [scores, setScores] = useState({})
  const [courseId, setCourseId] = useState('')
  const [subjectId, setSubjectId] = useState('')
  const [error, setError] = useState('')
  const [ok, setOk] = useState('')
  const tp = { params: { teacherId: user.teacherId } }

  useEffect(() => { api.get('/teacher/courses', tp).then((r) => setCourses(asList(unwrap(r)))).catch(() => setError('Impossible de charger vos cours.')) }, [])

  async function pickCourse(id) {
    setCourseId(id); setSubjectId(''); setSubjects([]); setStudents([]); setSaved({}); setScores({}); setError(''); setOk('')
    if (!id) return
    const course = courses.find((c) => String(c.id) === id)
    api.get(`/course/${id}/subjects`).then((r) => setSubjects(asList(unwrap(r)))).catch(() => setError('Impossible de charger les matières.'))
    api.get('/teacher/students', tp).then((r) => setStudents(asList(unwrap(r)).filter((s) => s.courseName === course?.name))).catch(() => {})
  }
  async function pickSubject(id) {
    setSubjectId(id); setSaved({}); setScores({}); setError(''); setOk('')
    if (!id) return
    api.get(`/courses/${courseId}/subjects/${id}/grades`, tp).then((r) => {
      const m = {}; asList(unwrap(r)).forEach((g) => { m[g.studentId] = g }); setSaved(m)
    }).catch(() => {})
  }
  // Le backend exige que la matière soit d’abord affectée à l’étudiant (en plus de son inscription au cours).
  async function assignSubject() {
    const r = await api.get(`/teacher/courses/${courseId}/subject-assignments`, tp)
    const d = unwrap(r)
    const list = Array.isArray(d) ? d : d?.students || d?.studentAssignments
      || Object.values(d || {}).find((v) => Array.isArray(v) && v.some((x) => x && 'assignedSubjects' in x)) || []
    const current = {}
    asList(list).forEach((x) => { current[x.studentId] = (x.assignedSubjects || []).map((sub) => sub.id) })
    for (const st of students) {
      const ids = [...new Set([...(current[st.id] || []), Number(subjectId)])]
      await api.put(`/teacher/courses/${courseId}/students/${st.id}/subjects`, { subjectIds: ids }, tp)
    }
  }
  const post = (student, n) => api.post(`/subjects/${subjectId}/students/${student.id}/grade`,
    { courseId: Number(courseId), teacherId: user.teacherId, totalScore: n, grade: letterOf(n) })

  async function save(student) {
    const n = Number(scores[student.id] ?? saved[student.id]?.totalScore)
    if (Number.isNaN(n) || n < 0 || n > 20) { setError('La note doit être comprise entre 0 et 20.'); return }
    setError(''); setOk('')
    let r
    try {
      try { r = await post(student, n) }
      catch (err) {
        if (!/not enrolled/i.test(msg(err))) throw err
        await assignSubject()
        r = await post(student, n)
      }
      setSaved({ ...saved, [student.id]: unwrap(r) }); setOk(`Note enregistrée pour ${student.name}.`)
    } catch (err) { setError(msg(err)) }
  }

  return (
    <>
      <header className="page-head"><h1>Saisie des notes</h1><p>Notes sur 20, la lettre (A à F) est calculée automatiquement : A dès 16, B dès 14, C dès 12, D dès 10, F en dessous.</p></header>
      {error && <p className="error">{error}</p>}
      {ok && <p className="success">{ok}</p>}
      <section className="box">
        <div className="inline-form two">
          <select value={courseId} onChange={(e) => pickCourse(e.target.value)}>
            <option value="">Choisir un cours</option>{courses.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
          <select value={subjectId} onChange={(e) => pickSubject(e.target.value)} disabled={!courseId}>
            <option value="">Choisir une matière</option>{subjects.map((s) => <option key={s.id} value={s.id}>{s.name}</option>)}
          </select>
        </div>
      </section>
      {subjectId && (
        <section className="box">
          {students.length === 0 ? <p className="empty">Aucun étudiant inscrit à ce cours.</p> : (
            <div className="scroll"><table><thead><tr><th>Étudiant</th><th>Note /20</th><th>Mention</th><th /></tr></thead>
              <tbody>{students.map((s) => (
                <tr key={s.id}><td>{s.name}</td>
                  <td><input className="score" type="number" min="0" max="20" step="0.25" value={scores[s.id] ?? saved[s.id]?.totalScore ?? ''}
                    onChange={(e) => setScores({ ...scores, [s.id]: e.target.value })} aria-label={`Note de ${s.name}`} /></td>
                  <td>{saved[s.id]?.grade ? <span className="pill">{gradeText(saved[s.id].grade)}</span> : '—'}</td>
                  <td className="end"><button className="btn small" onClick={() => save(s)}>Enregistrer</button></td></tr>))}</tbody></table></div>
          )}
        </section>
      )}
    </>
  )
}
