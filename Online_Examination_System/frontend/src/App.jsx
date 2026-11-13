import { Navigate, Route, Routes } from 'react-router-dom'
import Landing from './pages/Landing'
import Login from './pages/Login'
import Register from './pages/Register'
import VerifyOtp from './pages/VerifyOtp'
import StudentDashboard from './pages/StudentDashboard'
import TeacherDashboard from './pages/TeacherDashboard'
import TeacherSubjects from './pages/TeacherSubjects'
import TeacherQuestions from './pages/TeacherQuestions'
import TeacherExams from './pages/TeacherExams'
import { useAuth } from './context/AuthContext'

function Protected({ roles, children }) {
  const { user } = useAuth()
  if (!user) return <Navigate to="/login" replace />
  if (roles && !roles.includes(user.role)) return <Navigate to="/" replace />
  return children
}

const staff = ['ADMIN', 'TEACHER']

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Landing />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/verify-otp" element={<VerifyOtp />} />

      <Route path="/student" element={
        <Protected roles={['STUDENT']}><StudentDashboard /></Protected>} />

      <Route path="/teacher" element={
        <Protected roles={staff}><TeacherDashboard /></Protected>} />
      <Route path="/teacher/subjects" element={
        <Protected roles={staff}><TeacherSubjects /></Protected>} />
      <Route path="/teacher/questions" element={
        <Protected roles={staff}><TeacherQuestions /></Protected>} />
      <Route path="/teacher/exams" element={
        <Protected roles={staff}><TeacherExams /></Protected>} />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}