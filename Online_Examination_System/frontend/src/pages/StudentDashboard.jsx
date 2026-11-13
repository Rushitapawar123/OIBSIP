import { motion } from 'framer-motion'
import DashboardLayout from '../components/DashboardLayout'
import { useAuth } from '../context/AuthContext'

export const studentMenu = [
  { to: '/student', icon: '🏠', label: 'Home' },
  { to: '/student/exams', icon: '📝', label: 'Exams' },
  { to: '/student/results', icon: '📊', label: 'Results' },
  { to: '/student/leaderboard', icon: '🏆', label: 'Ranks' },
]

const XP_PER_LEVEL = 100

export default function StudentDashboard() {
  const { user } = useAuth()
  const xp = user?.xpPoints ?? 0
  const level = user?.levelNo ?? 1
  const progress = xp % XP_PER_LEVEL
  const toNext = XP_PER_LEVEL - progress

  const stats = [
    { icon: '⭐', label: 'Level', value: level },
    { icon: '✨', label: 'Total XP', value: xp },
    { icon: '🔥', label: 'Streak', value: '1 din' },
  ]

  return (
    <DashboardLayout menu={studentMenu}>
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="rounded-3xl bg-gradient-to-r from-brand to-cyan-glow p-6 text-white shadow-xl"
      >
        <h1 className="text-2xl font-bold md:text-3xl">Wapas aaye, {user?.fullName}! 👋</h1>
        <p className="mt-1 text-sm opacity-90">Aaj kuch naya seekhne ka din hai.</p>

        <div className="mt-5">
          <div className="flex justify-between text-xs font-medium">
            <span>Level {level}</span>
            <span>{toNext} XP aur, phir Level {level + 1}</span>
          </div>
          <div className="mt-2 h-3 overflow-hidden rounded-full bg-white/30">
            <motion.div
              initial={{ width: 0 }}
              animate={{ width: `${progress}%` }}
              transition={{ duration: 1, delay: 0.3 }}
              className="h-full rounded-full bg-white"
            />
          </div>
        </div>
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
            <div className="mt-1 text-xl font-bold md:text-2xl">{s.value}</div>
            <div className="text-xs opacity-70">{s.label}</div>
          </motion.div>
        ))}
      </div>

      <div className="mt-5 grid gap-5 md:grid-cols-2">
        <div className="glass rounded-3xl p-5 shadow-lg">
          <h2 className="font-bold">📅 Upcoming Exams</h2>
          <p className="mt-3 text-sm opacity-70">Abhi koi exam nahi hai. Jaldi hi yahan dikhenge.</p>
        </div>
        <div className="glass rounded-3xl p-5 shadow-lg">
          <h2 className="font-bold">🏅 Badges</h2>
          <p className="mt-3 text-sm opacity-70">Pehla exam dijiye, apna pehla badge jeetiye!</p>
        </div>
      </div>
    </DashboardLayout>
  )
}