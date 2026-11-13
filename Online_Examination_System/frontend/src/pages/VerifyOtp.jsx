import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import AuthLayout from '../components/AuthLayout'
import api from '../api/axios'

const inputClass =
  'w-full rounded-xl border border-slate-300/60 bg-white/60 px-4 py-3 text-sm outline-none transition focus:border-brand focus:ring-2 focus:ring-brand/30 dark:border-white/10 dark:bg-white/5'

export default function VerifyOtp() {
  const navigate = useNavigate()
  const location = useLocation()
  const [email, setEmail] = useState(location.state?.email || '')
  const [otp, setOtp] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(false)

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { data } = await api.post('/auth/verify-otp', { email, otp })
      setSuccess(data.message)
      setTimeout(() => navigate('/login'), 1500)
    } catch (err) {
      const data = err.response?.data
      setError(data?.errors?.otp || data?.message || 'Could not connect to the server')
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthLayout title="Verify your email ✉️" subtitle="Enter the 6-digit OTP">
      <form onSubmit={submit} className="space-y-4">
        <input type="email" placeholder="Email" value={email}
          onChange={(e) => setEmail(e.target.value)} required className={inputClass} />
        <input
          placeholder="6-digit OTP"
          value={otp}
          onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
          inputMode="numeric"
          required
          className={`${inputClass} text-center text-xl tracking-[0.5em]`}
        />

        {error && <p className="rounded-lg bg-red-500/10 px-3 py-2 text-sm text-red-500">{error}</p>}
        {success && <p className="rounded-lg bg-green-500/10 px-3 py-2 text-sm text-green-500">{success}</p>}

        <button disabled={loading}
          className="w-full cursor-pointer rounded-xl bg-gradient-to-r from-brand to-cyan-glow py-3 font-semibold text-white shadow-lg transition hover:scale-[1.02] disabled:opacity-60">
          {loading ? 'Please wait...' : 'Verify'}
        </button>

        <p className="text-center text-sm">
          <Link to="/login" className="text-brand hover:underline">Back to login</Link>
        </p>
      </form>
    </AuthLayout>
  )
}