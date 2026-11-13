import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/Login';
import StudentLayout from './components/StudentLayout';
import StudentDashboard from './pages/student/Dashboard';

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        
        {/* Routes Étudiant avec Sidebar et Header intégrés */}
        <Route element={<StudentLayout />}>
          <Route path="/student/dashboard" element={<StudentDashboard />} />
          {/* Tu pourras ajouter les autres routes ici : /student/cursus, /student/notes, etc. */}
        </Route>
      </Routes>
    </Router>
  );
}