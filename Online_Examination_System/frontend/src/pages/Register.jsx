import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import AuthLayout from '../components/AuthLayout'
import api from '../api/axios'

const inputClass =
  'w-full rounded-xl border border-slate-300/60 bg-white/60 px-4 py-3 text-sm outline-none transition focus:border-brand focus:ring-2 focus:ring-brand/30 dark:border-white/10 dark:bg-white/5'

export default function Register() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ fullName: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})
  const [loading, setLoading] = useState(false)

  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setFieldErrors({})
    setLoading(true)
    try {
      await api.post('/auth/register', form)
      navigate('/verify-otp', { state: { email: form.email } })
    } catch (err) {
      const data = err.response?.data
      setFieldErrors(data?.errors || {})
      setError(data?.errors ? '' : data?.message || 'Could not connect to the server')
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthLayout title="Create your account 🚀" subtitle="Get started in just a few seconds">
      <form onSubmit={submit} className="space-y-4">
        <div>
          <input name="fullName" placeholder="Full name" value={form.fullName}
            onChange={change} required className={inputClass} />
          {fieldErrors.fullName && <p className="mt-1 text-xs text-red-500">{fieldErrors.fullName}</p>}
        </div>
        <div>
          <input name="email" type="email" placeholder="Email" value={form.email}
            onChange={change} required className={inputClass} />
          {fieldErrors.email && <p className="mt-1 text-xs text-red-500">{fieldErrors.email}</p>}
        </div>
        <div>
          <input name="password" type="password" placeholder="Password (8+ characters, letters and numbers)"
            value={form.password} onChange={change} required className={inputClass} />
          {fieldErrors.password && <p className="mt-1 text-xs text-red-500">{fieldErrors.password}</p>}
        </div>

        {error && (
          <p className="rounded-lg bg-red-500/10 px-3 py-2 text-sm text-red-500">{error}</p>
        )}

        <button disabled={loading}
          className="w-full cursor-pointer rounded-xl bg-gradient-to-r from-brand to-pink-glow py-3 font-semibold text-white shadow-lg transition hover:scale-[1.02] disabled:opacity-60">
          {loading ? 'Please wait...' : 'Register'}
        </button>

        <p className="text-center text-sm">
          Already have an account?{' '}
          <Link to="/login" className="text-brand hover:underline">Log in</Link>
        </p>
      </form>
    </AuthLayout>
  )
}