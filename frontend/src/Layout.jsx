import { NavLink, useNavigate } from 'react-router-dom'
import { LayoutDashboard, Award, GraduationCap, Presentation, BookOpen, Library, Pencil, FileText, LogOut } from 'lucide-react'
import { getUser, logout } from './api'

const NAV = {
  student: [['/student', 'Tableau de bord', LayoutDashboard], ['/student/notes', 'Mes notes', Award], ['/student/releve', 'Relevé de notes', FileText]],
  teacher: [['/teacher', 'Tableau de bord', LayoutDashboard], ['/teacher/courses', 'Mes cours', BookOpen], ['/teacher/students', 'Mes étudiants', GraduationCap], ['/teacher/notes', 'Saisie des notes', Pencil]],
  admin: [['/admin', 'Tableau de bord', LayoutDashboard], ['/admin/students', 'Étudiants', GraduationCap],
          ['/admin/teachers', 'Enseignants', Presentation], ['/admin/courses', 'Cours', BookOpen], ['/admin/subjects', 'Matières', Library]],
}
const LABEL = { student: 'Espace étudiant', teacher: 'Espace enseignant', admin: 'Espace administratif' }

export default function Layout({ group, children }) {
  const nav = useNavigate()
  const user = getUser()
  return (
    <div className="shell">
      <aside className="side">
        <div className="side-logo"><img src="/logo-estlc.jpg" alt="ESTLC" /></div>
        <p className="side-space">{LABEL[group]}</p>
        <nav>
          {NAV[group].map(([to, label, Icon]) => (
            <NavLink key={to} to={to} end className={({ isActive }) => 'nav' + (isActive ? ' on' : '')}><Icon size={19} />{label}</NavLink>
          ))}
        </nav>
        <div className="side-user">
          <div><b>{user?.name || 'Utilisateur'}</b><small>{user?.email}</small></div>
          <button className="icon-btn" onClick={() => { logout(); nav('/') }} aria-label="Se déconnecter" title="Se déconnecter"><LogOut size={18} /></button>
        </div>
      </aside>
      <main className="main">{children}</main>
    </div>
  )
}
