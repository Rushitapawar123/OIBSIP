import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import AuthLayout from '../components/AuthLayout'
import { useAuth } from '../context/AuthContext'

const inputClass =
  'w-full rounded-xl border border-slate-300/60 bg-white/60 px-4 py-3 text-sm outline-none transition focus:border-brand focus:ring-2 focus:ring-brand/30 dark:border-white/10 dark:bg-white/5'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const user = await login(form.email, form.password)
      navigate(user.role === 'STUDENT' ? '/student' : '/teacher')
    } catch (err) {
      setError(err.response?.data?.message || 'Could not connect to the server')
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthLayout title="Welcome back! 👋" subtitle="Log in to your account">
      <form onSubmit={submit} className="space-y-4">
        <input name="email" type="email" placeholder="Email" value={form.email}
          onChange={change} required className={inputClass} />
        <input name="password" type="password" placeholder="Password" value={form.password}
          onChange={change} required className={inputClass} />

        {error && (
          <p className="rounded-lg bg-red-500/10 px-3 py-2 text-sm text-red-500">{error}</p>
        )}

        <button disabled={loading}
          className="w-full cursor-pointer rounded-xl bg-gradient-to-r from-brand to-cyan-glow py-3 font-semibold text-white shadow-lg transition hover:scale-[1.02] disabled:opacity-60">
          {loading ? 'Please wait...' : 'Log in'}
        </button>

        <div className="flex justify-between text-sm">
          <Link to="/register" className="text-brand hover:underline">
            Create a new account
          </Link>
        </div>
      </form>
    </AuthLayout>
  )
}