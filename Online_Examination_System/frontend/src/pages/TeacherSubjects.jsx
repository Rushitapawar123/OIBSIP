import { useEffect, useState } from 'react'
import { AnimatePresence, motion } from 'framer-motion'
import DashboardLayout from '../components/DashboardLayout'
import { teacherMenu } from './TeacherDashboard'
import api from '../api/axios'

const inputClass =
  'w-full rounded-xl border border-slate-300/60 bg-white/60 px-4 py-3 text-sm outline-none transition focus:border-brand focus:ring-2 focus:ring-brand/30 dark:border-white/10 dark:bg-white/5'

const emptyForm = { name: '', description: '' }

export default function TeacherSubjects() {
  const [subjects, setSubjects] = useState([])
  const [loading, setLoading] = useState(true)
  const [form, setForm] = useState(emptyForm)
  const [editingId, setEditingId] = useState(null)
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  const load = () =>
    api.get('/teacher/subjects')
      .then((res) => setSubjects(res.data))
      .catch((err) => setError(err.response?.data?.message || 'Could not load subjects'))
      .finally(() => setLoading(false))

  useEffect(() => {
    load()
  }, [])

  const change = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const reset = () => {
    setForm(emptyForm)
    setEditingId(null)
    setError('')
  }

  const submit = async (e) => {
    e.preventDefault()
    setError('')
    setSaving(true)
    try {
      if (editingId) {
        await api.put(`/teacher/subjects/${editingId}`, form)
      } else {
        await api.post('/teacher/subjects', form)
      }
      reset()
      await load()
    } catch (err) {
      const data = err.response?.data
      setError(data?.errors?.name || data?.errors?.description || data?.message || 'Could not save')
    } finally {
      setSaving(false)
    }
  }

  const startEdit = (s) => {
    setEditingId(s.id)
    setForm({ name: s.name, description: s.description || '' })
    setError('')
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const remove = async (s) => {
    if (!window.confirm(`Delete "${s.name}"?`)) return
    setError('')
    try {
      await api.delete(`/teacher/subjects/${s.id}`)
      await load()
    } catch (err) {
      setError(err.response?.data?.message || 'Could not delete')
    }
  }

  return (
    <DashboardLayout menu={teacherMenu}>
      <h1 className="text-2xl font-bold">📚 Subjects</h1>

      <form onSubmit={submit} className="glass mt-5 space-y-3 rounded-3xl p-5 shadow-lg">
        <h2 className="font-semibold">{editingId ? 'Edit subject' : 'New subject'}</h2>
        <input name="name" placeholder="Subject name (e.g. Java)" value={form.name}
          onChange={change} required className={inputClass} />
        <input name="description" placeholder="Short description (optional)" value={form.description}
          onChange={change} className={inputClass} />

        {error && <p className="rounded-lg bg-red-500/10 px-3 py-2 text-sm text-red-500">{error}</p>}

        <div className="flex gap-3">
          <button disabled={saving}
            className="cursor-pointer rounded-xl bg-gradient-to-r from-brand to-cyan-glow px-6 py-2.5 text-sm font-semibold text-white shadow-lg transition hover:scale-105 disabled:opacity-60">
            {saving ? 'Please wait...' : editingId ? 'Update' : 'Add'}
          </button>
          {editingId && (
            <button type="button" onClick={reset}
              className="glass cursor-pointer rounded-xl px-6 py-2.5 text-sm font-medium">
              Cancel
            </button>
          )}
        </div>
      </form>

      <div className="mt-5 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {loading && <p className="text-sm opacity-70">Loading...</p>}
        {!loading && subjects.length === 0 && (
          <p className="text-sm opacity-70">No subjects yet. Add your first one above.</p>
        )}
        <AnimatePresence>
          {subjects.map((s) => (
            <motion.div
              key={s.id}
              layout
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.9 }}
              className="glass rounded-2xl p-5 shadow-lg"
            >
              <h3 className="text-lg font-bold">{s.name}</h3>
              <p className="mt-1 min-h-[2.5rem] text-sm opacity-70">{s.description || 'No description'}</p>
              <p className="mt-2 text-xs font-medium text-brand">{s.questionCount} questions</p>
              <div className="mt-4 flex gap-2">
                <button onClick={() => startEdit(s)}
                  className="glass cursor-pointer rounded-lg px-4 py-1.5 text-xs font-medium">
                  ✏️ Edit
                </button>
                <button onClick={() => remove(s)}
                  className="cursor-pointer rounded-lg bg-red-500/10 px-4 py-1.5 text-xs font-medium text-red-500">
                  🗑️ Delete
                </button>
              </div>
            </motion.div>
          ))}
        </AnimatePresence>
      </div>
    </DashboardLayout>
  )
}