import { motion } from 'framer-motion'
import { Link } from 'react-router-dom'
import useTheme from '../hooks/useTheme'

export default function AuthLayout({ title, subtitle, children }) {
  const { dark, toggle } = useTheme()

  return (
    <div className="relative flex min-h-screen items-center justify-center overflow-hidden px-4 py-10">
      <div className="pointer-events-none absolute -top-32 -left-32 h-96 w-96 rounded-full bg-brand/30 blur-3xl" />
      <div className="pointer-events-none absolute -bottom-32 -right-32 h-96 w-96 rounded-full bg-pink-glow/20 blur-3xl" />

      <button
        onClick={toggle}
        className="glass absolute top-5 right-5 z-10 cursor-pointer rounded-full px-4 py-2 text-sm font-medium"
      >
        {dark ? '☀️ Light' : '🌙 Dark'}
      </button>

      <motion.div
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="glass relative z-10 w-full max-w-md rounded-3xl p-7 shadow-2xl sm:p-9"
      >
        <Link
          to="/"
          className="bg-gradient-to-r from-brand to-cyan-glow bg-clip-text text-xl font-extrabold text-transparent"
        >
          ExamPro
        </Link>
        <h1 className="mt-4 text-2xl font-bold">{title}</h1>
        <p className="mt-1 text-sm opacity-70">{subtitle}</p>
        <div className="mt-6">{children}</div>
      </motion.div>
    </div>
  )
}