import { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { Link } from 'react-router-dom'
import DashboardLayout from '../components/DashboardLayout'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'

export const teacherMenu = [
  { to: '/teacher', icon: '🏠', label: 'Home' },
  { to: '/teacher/subjects', icon: '📚', label: 'Subjects' },
  { to: '/teacher/questions', icon: '❓', label: 'Questions' },
  { to: '/teacher/exams', icon: '📝', label: 'Exams' },
]

export default function TeacherDashboard() {
  const { user } = useAuth()
  const [subjects, setSubjects] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/teacher/subjects')
      .then((res) => setSubjects(res.data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [])

  const totalQuestions = subjects.reduce((sum, s) => sum + s.questionCount, 0)

  const stats = [
    { icon: '📚', label: 'Subjects', value: subjects.length },
    { icon: '❓', label: 'Questions', value: totalQuestions },
    { icon: '📝', label: 'Exams', value: 0 },
  ]

  return (
    <DashboardLayout menu={teacherMenu}>
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="rounded-3xl bg-gradient-to-r from-brand to-pink-glow p-6 text-white shadow-xl"
      >
        <h1 className="text-2xl font-bold md:text-3xl">Hello, {user?.fullName}! 👋</h1>
        <p className="mt-1 text-sm opacity-90">Create questions and get your exams ready.</p>
      </motion.div>

      <div className="mt-5 grid grid-cols-3 gap-3 md:gap-5">
        {stats.map((s, i) => (
          <motion.div
            key={s.label}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 * i }}
            whileHover={{ y: -4 }}
            className="glass rounded-2xl p-4 text-center shadow-lg"
          >
            <div className="text-2xl">{s.icon}</div>
            <div className="mt-1 text-xl font-bold md:text-2xl">{loading ? '...' : s.value}</div>
            <div className="text-xs opacity-70">{s.label}</div>
          </motion.div>
        ))}
      </div>

      <div className="mt-5 grid gap-5 md:grid-cols-2">
        <Link to="/teacher/subjects" className="glass rounded-3xl p-5 shadow-lg transition hover:scale-[1.02]">
          <h2 className="font-bold">📚 Manage Subjects</h2>
          <p className="mt-2 text-sm opacity-70">Create, rename or remove subjects.</p>
        </Link>
        <Link to="/teacher/questions" className="glass rounded-3xl p-5 shadow-lg transition hover:scale-[1.02]">
          <h2 className="font-bold">❓ Question Bank</h2>
          <p className="mt-2 text-sm opacity-70">Add MCQs with difficulty and topic.</p>
        </Link>
      </div>
    </DashboardLayout>
  )
}