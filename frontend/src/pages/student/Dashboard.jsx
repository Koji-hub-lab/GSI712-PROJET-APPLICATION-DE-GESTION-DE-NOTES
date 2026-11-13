import React from 'react';
import { BookOpen, Award, AlertCircle, CheckCircle2 } from 'lucide-react';

export default function StudentDashboard() {
  return (
    <div className="p-6 space-y-6 bg-gray-50 min-h-screen">
      {/* En-tête de bienvenue */}
      <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">Bienvenue, Koji 👋</h1>
          <p className="text-sm text-gray-500 mt-1">Filière : Génie des Systèmes Intelligents (GSI) | Semestre 5</p>
        </div>
        <div className="bg-emerald-50 text-emerald-700 px-4 py-2 rounded-lg font-semibold text-sm border border-emerald-200">
          Statut : Actif 🟢
        </div>
      </div>

      {/* Cartes statistiques rapides */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="bg-white p-5 rounded-xl shadow-sm border border-gray-100 flex items-center space-x-4">
          <div className="p-3 bg-blue-50 text-blue-600 rounded-lg"><BookOpen size={24} /></div>
          <div>
            <p className="text-sm text-gray-500">Moyenne S5</p>
            <h3 className="text-xl font-bold text-gray-800">14.50 / 20</h3>
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl shadow-sm border border-gray-100 flex items-center space-x-4">
          <div className="p-3 bg-emerald-50 text-emerald-600 rounded-lg"><Award size={24} /></div>
          <div>
            <p className="text-sm text-gray-500">Crédits Validés</p>
            <h3 className="text-xl font-bold text-gray-800">28 / 30</h3>
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl shadow-sm border border-gray-100 flex items-center space-x-4">
          <div className="p-3 bg-amber-50 text-amber-600 rounded-lg"><CheckCircle2 size={24} /></div>
          <div>
            <p className="text-sm text-gray-500">UE Validées</p>
            <h3 className="text-xl font-bold text-gray-800">5 / 6</h3>
          </div>
        </div>

        <div className="bg-white p-5 rounded-xl shadow-sm border border-gray-100 flex items-center space-x-4">
          <div className="p-3 bg-rose-50 text-rose-600 rounded-lg"><AlertCircle size={24} /></div>
          <div>
            <p className="text-sm text-gray-500">Dettes / Rattrapages</p>
            <h3 className="text-xl font-bold text-gray-800">0</h3>
          </div>
        </div>
      </div>

      {/* Dernières notes enregistrées */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <h3 className="text-lg font-bold text-gray-800 mb-4">Dernières Notes Attribuées</h3>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse text-sm">
            <thead>
              <tr className="border-b border-gray-200 text-gray-500 bg-gray-50">
                <th className="py-3 px-4">Code EC</th>
                <th className="py-3 px-4">Intitulé du Cours</th>
                <th className="py-3 px-4">CC</th>
                <th className="py-3 px-4">Examen</th>
                <th className="py-3 px-4">Moyenne</th>
                <th className="py-3 px-4">Statut</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100 text-gray-700">
              <tr>
                <td className="py-3 px-4 font-medium">GSI734</td>
                <td className="py-3 px-4">Systèmes Temps Réel & Embarqués</td>
                <td className="py-3 px-4">15.00</td>
                <td className="py-3 px-4">14.00</td>
                <td className="py-3 px-4 font-bold">14.30</td>
                <td className="py-3 px-4"><span className="px-2 py-1 bg-emerald-100 text-emerald-800 rounded-full text-xs font-semibold">Validé</span></td>
              </tr>
              <tr>
                <td className="py-3 px-4 font-medium">GSI712</td>
                <td className="py-3 px-4">Architecture Distribuée & Middleware</td>
                <td className="py-3 px-4">12.00</td>
                <td className="py-3 px-4">13.50</td>
                <td className="py-3 px-4 font-bold">12.95</td>
                <td className="py-3 px-4"><span className="px-2 py-1 bg-emerald-100 text-emerald-800 rounded-full text-xs font-semibold">Validé</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}