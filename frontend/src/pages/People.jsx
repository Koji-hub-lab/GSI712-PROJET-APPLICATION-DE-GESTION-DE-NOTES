import { useEffect, useState } from 'react'
import { Trash2, Plus } from 'lucide-react'
import api, { unwrap, asList } from '../api'

const T = { students: ['Étudiants', 'un étudiant'], teachers: ['Enseignants', 'un enseignant'] }

export default function People({ kind }) {
  const [rows, setRows] = useState([])
  const [form, setForm] = useState({ name: '', email: '', password: '' })
  const [error, setError] = useState('')
  const load = () => api.get(`/admin/${kind}`).then((r) => setRows(asList(unwrap(r)))).catch(() => setError('Impossible de charger la liste.'))
  useEffect(() => { setRows([]); setError(''); load() }, [kind])

  async function add(e) {
    e.preventDefault(); setError('')
    try { await api.post(`/admin/${kind}`, form); setForm({ name: '', email: '', password: '' }); load() }
    catch (err) { setError(err.response?.data?.error || err.response?.data?.message || 'Ajout impossible. Vérifiez les champs.') }
  }
  async function remove(id) {
    if (!window.confirm('Supprimer définitivement cet enregistrement ?')) return
    try { await api.delete(`/admin/${kind}/${id}`); load() } catch { setError('Suppression impossible.') }
  }
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  return (
    <>
      <header className="page-head"><h1>{T[kind][0]}</h1><p>{rows.length} enregistré(s)</p></header>
      {error && <p className="error">{error}</p>}
      <section className="box">
        <h2>Ajouter {T[kind][1]}</h2>
        <form className="inline-form" onSubmit={add}>
          <input placeholder="Nom complet" value={form.name} onChange={set('name')} required />
          <input type="email" placeholder="Adresse e-mail" value={form.email} onChange={set('email')} required />
          <input type="password" placeholder="Mot de passe" value={form.password} onChange={set('password')} required />
          <button className="btn small"><Plus size={16} />Ajouter</button>
        </form>
      </section>
      <section className="box">
        {rows.length === 0 ? <p className="empty">Aucun enregistrement pour le moment.</p> : (
          <div className="scroll"><table><thead><tr><th>Nom</th><th>E-mail</th><th /></tr></thead>
            <tbody>{rows.map((r) => (
              <tr key={r.id}><td>{r.name || r.username}</td><td>{r.email}</td>
                <td className="end"><button className="icon-btn danger" onClick={() => remove(r.id)} aria-label="Supprimer"><Trash2 size={16} /></button></td></tr>))}</tbody></table></div>
        )}
      </section>
    </>
  )
}
