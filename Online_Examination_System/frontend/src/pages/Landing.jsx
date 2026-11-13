import { motion } from 'framer-motion'
import { Link } from 'react-router-dom'
import useTheme from '../hooks/useTheme'

const features = [
  { icon: '🛡️', title: 'Smart Proctoring', text: 'Tab-switch and fullscreen monitoring for fair exams.' },
  { icon: '⏱️', title: 'Server-side Timer', text: 'Tamper-proof timer with automatic submission.' },
  { icon: '🏆', title: 'Leaderboard & XP', text: 'Points, levels, badges and streaks make learning fun.' },
  { icon: '📜', title: 'Verified Certificates', text: 'Certificates you can verify with a QR code.' },
]

export default function Landing() {
  const { dark, toggle } = useTheme()

  return (
    <div className="relative min-h-screen overflow-hidden">
      <div className="pointer-events-none absolute -top-32 -left-32 h-96 w-96 rounded-full bg-brand/30 blur-3xl" />
      <div className="pointer-events-none absolute top-1/3 -right-32 h-96 w-96 rounded-full bg-cyan-glow/20 blur-3xl" />

      <nav className="relative z-10 mx-auto flex max-w-6xl items-center justify-between px-5 py-5">
        <span className="bg-gradient-to-r from-brand to-cyan-glow bg-clip-text text-xl font-extrabold text-transparent">
          ExamPro
        </span>
        <div className="flex items-center gap-3">
          <button onClick={toggle} aria-label="Toggle theme"
            className="glass cursor-pointer rounded-full px-4 py-2 text-sm font-medium">
            {dark ? '☀️ Light' : '🌙 Dark'}
          </button>
          <Link to="/login"
            className="rounded-full bg-gradient-to-r from-brand to-pink-glow px-5 py-2 text-sm font-semibold text-white shadow-lg">
            Log in
          </Link>
        </div>
      </nav>

      <main className="relative z-10 mx-auto max-w-6xl px-5 pt-12 pb-20 text-center md:pt-24">
        <motion.h1
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.7 }}
          className="text-4xl leading-tight font-extrabold md:text-6xl"
        >
          Online Exams,{' '}
          <span className="bg-gradient-to-r from-brand via-cyan-glow to-pink-glow bg-clip-text text-transparent">
            Smart & Fair
          </span>
        </motion.h1>

        <motion.p
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.7, delay: 0.2 }}
          className="mx-auto mt-5 max-w-2xl text-base opacity-80 md:text-lg"
        >
          Practice, take exams and track your progress. All in one place, on phone and laptop.
        </motion.p>

        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.5, delay: 0.4 }}
          className="mt-8 flex flex-col items-center justify-center gap-3 sm:flex-row"
        >
          <Link to="/register"
            className="w-full rounded-full bg-gradient-to-r from-brand to-cyan-glow px-8 py-3 text-center font-semibold text-white shadow-xl transition hover:scale-105 sm:w-auto">
            Get Started
          </Link>
          <Link to="/login"
            className="glass w-full rounded-full px-8 py-3 text-center font-semibold transition hover:scale-105 sm:w-auto">
            I already have an account
          </Link>
        </motion.div>

        <div className="mt-16 grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
          {features.map((f, i) => (
            <motion.div
              key={f.title}
              initial={{ opacity: 0, y: 30 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.5, delay: i * 0.1 }}
              whileHover={{ y: -6 }}
              className="glass rounded-3xl p-6 text-left shadow-lg"
            >
              <div className="text-3xl">{f.icon}</div>
              <h3 className="mt-3 text-lg font-bold">{f.title}</h3>
              <p className="mt-1 text-sm opacity-75">{f.text}</p>
            </motion.div>
          ))}
        </div>
      </main>
    </div>
  )
}