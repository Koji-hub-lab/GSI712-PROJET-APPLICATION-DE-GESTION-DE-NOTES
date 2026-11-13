import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { LayoutDashboard, BookOpen, FileText, AlertCircle, User, LogOut, GraduationCap } from 'lucide-react';

export default function Sidebar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    navigate('/');
  };

  const navItems = [
    { to: '/student/dashboard', label: 'Tableau de bord', icon: <LayoutDashboard size={20} /> },
    { to: '/student/cursus', label: 'Mon Cursus / UE', icon: <BookOpen size={20} /> },
    { to: '/student/notes', label: 'Notes & Évaluations', icon: <FileText size={20} /> },
    { to: '/student/dettes', label: 'Dettes & Rattrapages', icon: <AlertCircle size={20} /> },
    { to: '/student/profil', label: 'Mon Profil', icon: <User size={20} /> },
  ];

  return (
    <aside className="w-64 bg-slate-900 text-slate-300 flex flex-col h-screen fixed left-0 top-0 border-r border-slate-800">
      {/* Logo / Entête de l'école */}
      <div className="p-6 flex items-center space-x-3 border-b border-slate-800">
        <div className="bg-emerald-600 text-white p-2 rounded-xl shadow-md">
          <GraduationCap size={28} />
        </div>
        <div>
          <h1 className="font-bold text-white text-lg tracking-wide">ESTLC</h1>
          <p className="text-xs text-slate-400">Gestion Académique</p>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-4 py-6 space-y-1.5 overflow-y-auto">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `flex items-center space-x-3 px-4 py-3 rounded-xl text-sm font-medium transition-all ${
                isActive
                  ? 'bg-emerald-600 text-white shadow-lg shadow-emerald-900/30'
                  : 'hover:bg-slate-800 hover:text-white text-slate-400'
              }`
            }
          >
            {item.icon}
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>

      {/* Déconnexion */}
      <div className="p-4 border-t border-slate-800">
        <button
          onClick={handleLogout}
          className="w-full flex items-center space-x-3 px-4 py-3 rounded-xl text-sm font-medium text-rose-400 hover:bg-rose-500/10 transition-colors"
        >
          <LogOut size={20} />
          <span>Déconnexion</span>
        </button>
      </div>
    </aside>
  );
}