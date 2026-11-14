import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { GraduationCap, Presentation, ShieldCheck, User, Lock, Eye, EyeOff, Users } from 'lucide-react'
import { login, logout, roleGroup, homeFor } from '../api'

const ROLES = [
  { key: 'student', label: 'Étudiant', sub: 'Espace étudiant', Icon: GraduationCap },
  { key: 'teacher', label: 'Enseignant', sub: 'Espace enseignant', Icon: Presentation },
  { key: 'admin', label: 'Administration', sub: 'Espace administratif', Icon: ShieldCheck },
]

export default function Login() {
  const nav = useNavigate()
  const [tab, setTab] = useState('student')
  const [id, setId] = useState('')
  const [pwd, setPwd] = useState('')
  const [show, setShow] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function submit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { role } = await login(id.trim(), pwd)
      if (roleGroup(role) !== tab) {
        logout()
        const l = ROLES.find((r) => r.key === roleGroup(role)).label
        setError(`Ce compte relève de l’espace « ${l} ». Choisissez le bon espace ci-dessus.`)
        return
      }
      nav(homeFor(role))
    } catch (err) {
      console.error('Échec de connexion', err, err.response)
      setError(err.response?.status === 401 ? 'Identifiant ou mot de passe incorrect.'
        : err.response ? `Erreur du serveur (${err.response.status}). Détails dans la console du navigateur (F12).`
        : err.code === 'ERR_NETWORK' ? 'Connexion impossible : backend éteint, ou requête bloquée par le navigateur (CORS).'
        : `${err.message}`)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth">
      <div className="auth-card">
        <section className="hero">
          <div className="brand">
            <div className="logo-pill"><img src="/logo-estlc.jpg" alt="Logo ESTLC" /></div>
            <p className="brand-sub">École Supérieure de Transport,<br />de Logistique et de Commerce</p>
          </div>
          <div className="hero-body">
            <h2>PORTAIL ACADÉMIQUE</h2>
            <div className="rule" />
            <p className="motto">Excellence • Rigueur • Engagement</p>
            <p className="pitch">Votre espace académique unique pour gérer votre parcours, vos résultats et votre avenir.</p>
          </div>
          <ul className="perks">
            <li><ShieldCheck size={26} /><div><b>Sécurisé</b><span>Vos données sont protégées</span></div></li>
            <li><GraduationCap size={26} /><div><b>Officiel</b><span>Plateforme officielle de l’ESTLC</span></div></li>
            <li><Users size={26} /><div><b>Accessible</b><span>Disponible 24h/24 et 7j/7</span></div></li>
          </ul>
        </section>

        <section className="panel">
          <img className="mobile-logo" src="/logo-estlc.jpg" alt="ESTLC" />
          <h1>Bienvenue sur votre portail académique</h1>
          <p className="lead">Connectez-vous pour accéder à votre espace personnel</p>

          <div className="tabs" role="tablist">
            {ROLES.map(({ key, label, sub, Icon }) => (
              <button key={key} type="button" role="tab" aria-selected={tab === key}
                className={'tab' + (tab === key ? ' on' : '')} onClick={() => { setTab(key); setError('') }}>
                <Icon size={26} /><span><b>{label}</b><small>{sub}</small></span>
              </button>
            ))}
          </div>

          <form onSubmit={submit}>
            <label htmlFor="id">Identifiant</label>
            <div className="field"><User size={18} />
              <input id="id" value={id} onChange={(e) => setId(e.target.value)} autoComplete="username"
                placeholder="Entrez votre adresse e-mail ou identifiant" required />
            </div>

            <label htmlFor="pwd">Mot de passe</label>
            <div className="field"><Lock size={18} />
              <input id="pwd" type={show ? 'text' : 'password'} value={pwd} onChange={(e) => setPwd(e.target.value)}
                autoComplete="current-password" placeholder="Entrez votre mot de passe" required />
              <button type="button" className="eye" onClick={() => setShow(!show)} aria-label={show ? 'Masquer le mot de passe' : 'Afficher le mot de passe'}>
                {show ? <Eye size={18} /> : <EyeOff size={18} />}
              </button>
            </div>

            <a className="forgot" href="#oubli" onClick={(e) => e.preventDefault()}>Mot de passe oublié ?</a>
            {error && <p className="error" role="alert">{error}</p>}
            <button className="btn primary" disabled={loading}><Lock size={18} />{loading ? 'Connexion…' : 'Se connecter'}</button>
            <div className="or"><span>ou</span></div>
            <button type="button" className="btn ghost"><Users size={18} />Besoin d’aide ? Contactez l’administration</button>
          </form>
        </section>
      </div>
      <p className="foot">© 2026 ESTLC – Tous droits réservés.<br />Portail académique officiel de l’École Supérieure de Transport, de Logistique et de Commerce.</p>
    </div>
  )
}
