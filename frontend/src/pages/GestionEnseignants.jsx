import { useMemo, useState } from 'react'
import { Search, Plus, Eye } from 'lucide-react'
import Pagination from '../components/Pagination'
import FicheDetail, { Champ } from '../components/FicheDetail'
import { ENSEIGNANTS, EC_INTITULES } from '../data/exemples'
import '../gestion.css'

const PAGE_SIZE = 5
const norm = (s) => s.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase()
const TOUS_EC = Object.keys(EC_INTITULES).sort()

export default function GestionEnseignants() {
  const [q, setQ] = useState('')
  const [ec, setEc] = useState('')
  const [page, setPage] = useState(1)
  const [fiche, setFiche] = useState(null)

  const filtres = useMemo(() => ENSEIGNANTS.filter((t) =>
    (!q || norm(t.nom + ' ' + t.code).includes(norm(q))) && (!ec || t.ec.includes(ec))), [q, ec])
  const lignes = filtres.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE)
  const set = (fn) => (e) => { fn(e.target.value); setPage(1) }
  const reset = () => { setQ(''); setEc(''); setPage(1) }

  return (
    <>
      <header className="page-head"><h1>Enseignants</h1><p>{filtres.length} enseignant(s) correspondant aux filtres</p></header>

      <section className="box">
        <div className="toolbar toolbar-ens">
          <label className="search"><Search size={16} /><input value={q} onChange={set(setQ)} placeholder="Rechercher par nom ou code" aria-label="Rechercher un enseignant" /></label>
          <select value={ec} onChange={set(setEc)} aria-label="Matière enseignée"><option value="">Toutes les matières</option>
            {TOUS_EC.map((c) => <option key={c} value={c}>{c} – {EC_INTITULES[c]}</option>)}</select>
          <button className="btn small ghost-dark" onClick={reset}>Réinitialiser</button>
          <button className="btn small"><Plus size={16} />Ajouter</button>
        </div>

        {lignes.length === 0 ? <p className="empty">Aucun enseignant ne correspond à ces filtres.</p> : (
          <div className="scroll"><table>
            <thead><tr><th>Code</th><th>Nom</th><th>E-mail</th><th>Matières</th><th /></tr></thead>
            <tbody>{lignes.map((t) => (
              <tr key={t.id}><td><b>{t.code}</b></td><td>{t.nom}</td><td>{t.email}</td><td><span className="pill">{t.ec.length} EC</span></td>
                <td className="end"><button className="btn small ghost-dark" onClick={() => setFiche(t)}><Eye size={16} />Fiche</button></td></tr>
            ))}</tbody></table></div>
        )}
        <Pagination page={page} pageSize={PAGE_SIZE} total={filtres.length} onChange={setPage} />
      </section>

      {fiche && (
        <FicheDetail titre={fiche.nom} sousTitre={fiche.code} onClose={() => setFiche(null)}>
          <h3 className="fiche-h">Identité</h3>
          <dl className="champs"><Champ label="Code enseignant">{fiche.code}</Champ><Champ label="Nom complet">{fiche.nom}</Champ>
            <Champ label="E-mail">{fiche.email}</Champ><Champ label="Téléphone">{fiche.telephone}</Champ></dl>
          <h3 className="fiche-h">Matières affectées ({fiche.ec.length})</h3>
          <ul className="ec-liste">{fiche.ec.map((c) => <li key={c}><b>{c}</b><span>{EC_INTITULES[c]}</span></li>)}</ul>
        </FicheDetail>
      )}
    </>
  )
}
