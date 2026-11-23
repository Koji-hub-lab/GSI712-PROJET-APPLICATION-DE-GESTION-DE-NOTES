import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Login from './pages/Login'
import Layout from './Layout'
import Dashboard from './pages/Dashboard'
import StudentGrades from './pages/StudentGrades'
import People from './pages/People'
import Courses from './pages/Courses'
import TeacherList from './pages/TeacherList'
import TeacherGrades from './pages/TeacherGrades'
import Subjects from './pages/Subjects'
import Transcript from './pages/Transcript'
import { roleGroup } from './api'

function Guard({ group, children }) {
  const role = localStorage.getItem('role')
  if (!localStorage.getItem('token') || !role) return <Navigate to="/" replace />
  return roleGroup(role) === group ? children : <Navigate to={'/' + roleGroup(role)} replace />
}
const page = (group, el) => <Guard group={group}><Layout group={group}>{el}</Layout></Guard>

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/student" element={page('student', <Dashboard />)} />
        <Route path="/student/notes" element={page('student', <StudentGrades />)} />
        <Route path="/student/releve" element={page('student', <Transcript />)} />
        <Route path="/teacher" element={page('teacher', <Dashboard />)} />
        <Route path="/teacher/courses" element={page('teacher', <TeacherList kind="courses" />)} />
        <Route path="/teacher/students" element={page('teacher', <TeacherList kind="students" />)} />
        <Route path="/teacher/notes" element={page('teacher', <TeacherGrades />)} />
        <Route path="/admin/subjects" element={page('admin', <Subjects />)} />
        <Route path="/admin" element={page('admin', <Dashboard />)} />
        <Route path="/admin/students" element={page('admin', <People kind="students" />)} />
        <Route path="/admin/teachers" element={page('admin', <People kind="teachers" />)} />
        <Route path="/admin/courses" element={page('admin', <Courses />)} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
