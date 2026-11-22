import { useMemo, useState } from 'react'
import { Search, Plus, Eye } from 'lucide-react'
import Pagination from '../components/Pagination'
import FicheDetail, { Champ } from '../components/FicheDetail'
import { ETUDIANTS } from '../data/exemples'
import '../gestion.css'

const PAGE_SIZE = 5
const norm = (s) => s.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase()
const unique = (k) => [...new Set(ETUDIANTS.map((e) => e[k]))]
const badge = (s) => 'badge ' + (s === 'Actif' ? 'ok' : s === 'En dette' ? 'warn' : 'off')

export default function GestionEtudiants() {
  const [q, setQ] = useState('')
  const [niveau, setNiveau] = useState('')
  const [groupe, setGroupe] = useState('')
  const [statut, setStatut] = useState('')
  const [page, setPage] = useState(1)
  const [fiche, setFiche] = useState(null)

  const filtres = useMemo(() => ETUDIANTS.filter((e) =>
    (!q || norm(e.nom + ' ' + e.matricule).includes(norm(q))) &&
    (!niveau || e.niveau === niveau) && (!groupe || e.groupe === groupe) && (!statut || e.statut === statut)), [q, niveau, groupe, statut])
  const lignes = filtres.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE)
  const set = (fn) => (e) => { fn(e.target.value); setPage(1) }
  const reset = () => { setQ(''); setNiveau(''); setGroupe(''); setStatut(''); setPage(1) }

  return (
    <>
      <header className="page-head"><h1>Étudiants</h1><p>{filtres.length} étudiant(s) correspondant aux filtres</p></header>

      <section className="box">
        <div className="toolbar">
          <label className="search"><Search size={16} /><input value={q} onChange={set(setQ)} placeholder="Rechercher par nom ou matricule" aria-label="Rechercher un étudiant" /></label>
          <select value={niveau} onChange={set(setNiveau)} aria-label="Niveau"><option value="">Tous les niveaux</option>{unique('niveau').map((v) => <option key={v}>{v}</option>)}</select>
          <select value={groupe} onChange={set(setGroupe)} aria-label="Groupe"><option value="">Tous les groupes</option>{unique('groupe').map((v) => <option key={v}>{v}</option>)}</select>
          <select value={statut} onChange={set(setStatut)} aria-label="Statut"><option value="">Tous les statuts</option>{unique('statut').map((v) => <option key={v}>{v}</option>)}</select>
          <button className="btn small ghost-dark" onClick={reset}>Réinitialiser</button>
          <button className="btn small"><Plus size={16} />Ajouter</button>
        </div>

        {lignes.length === 0 ? <p className="empty">Aucun étudiant ne correspond à ces filtres.</p> : (
          <div className="scroll"><table>
            <thead><tr><th>Matricule</th><th>Nom</th><th>Niveau</th><th>Groupe</th><th>Statut</th><th /></tr></thead>
            <tbody>{lignes.map((e) => (
              <tr key={e.id}><td><b>{e.matricule}</b></td><td>{e.nom}</td><td>{e.niveau}</td><td>{e.groupe}</td>
                <td><span className={badge(e.statut)}>{e.statut}</span></td>
                <td className="end"><button className="btn small ghost-dark" onClick={() => setFiche(e)}><Eye size={16} />Fiche</button></td></tr>
            ))}</tbody></table></div>
        )}
        <Pagination page={page} pageSize={PAGE_SIZE} total={filtres.length} onChange={setPage} />
      </section>

      {fiche && (
        <FicheDetail titre={fiche.nom} sousTitre={fiche.matricule} onClose={() => setFiche(null)}>
          <h3 className="fiche-h">Identité</h3>
          <dl className="champs"><Champ label="Matricule">{fiche.matricule}</Champ><Champ label="Nom complet">{fiche.nom}</Champ>
            <Champ label="E-mail">{fiche.email}</Champ><Champ label="Téléphone">{fiche.telephone}</Champ></dl>
          <h3 className="fiche-h">Parcours</h3>
          <dl className="champs"><Champ label="Filière">{fiche.filiere}</Champ><Champ label="Niveau">{fiche.niveau}</Champ>
            <Champ label="Semestre en cours">{fiche.semestre}</Champ><Champ label="Groupe">{fiche.groupe}</Champ>
            <Champ label="Année d'entrée">{String(fiche.anneeEntree)}</Champ>
            <Champ label="Statut"><span className={badge(fiche.statut)}>{fiche.statut}</span></Champ></dl>
        </FicheDetail>
      )}
    </>
  )
}
