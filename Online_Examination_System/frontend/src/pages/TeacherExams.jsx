import { useCallback, useEffect, useState } from 'react'
import { AnimatePresence, motion } from 'framer-motion'
import DashboardLayout from '../components/DashboardLayout'
import { teacherMenu } from './TeacherDashboard'
import api from '../api/axios'

const inputClass =
  'w-full rounded-xl border border-slate-300/60 bg-white/60 px-4 py-3 text-sm outline-none transition focus:border-brand focus:ring-2 focus:ring-brand/30 dark:border-white/10 dark:bg-white/5'

const statusColor = {
  DRAFT: 'bg-slate-500/20 text-slate-500',
  PUBLISHED: 'bg-green-500/15 text-green-500',
  CLOSED: 'bg-red-500/15 text-red-500',
}

const levelColor = {
  EASY: 'text-green-500',
  MEDIUM: 'text-amber-500',
  HARD: 'text-red-500',
}

const TOGGLES = [
  { key: 'shuffleQuestions', label: 'Shuffle questions' },
  { key: 'shuffleOptions', label: 'Shuffle options' },
  { key: 'adaptive', label: 'Adaptive difficulty' },
  { key: 'proctoringEnabled', label: 'Proctoring (tab switch, fullscreen)' },
  { key: 'webcamRequired', label: 'Webcam required' },
  { key: 'certificateEnabled', label: 'Issue certificate' },
]

const emptyForm = () => ({
  title: '',
  description: '',
  subjectId: '',
  durationMinutes: 30,
  passMarks: 0,
  negativeMarking: 0,
  startTime: '',
  endTime: '',
  maxViolations: 3,
  shuffleQuestions: true,
  shuffleOptions: true,
  adaptive: false,
  proctoringEnabled: true,
  webcamRequired: false,
  certificateEnabled: true,
  questionIds: [],
})

// "2026-11-20T10:00:00" -> value usable by <input type="datetime-local">
const toInputDate = (iso) => (iso ? iso.slice(0, 16) : '')
// datetime-local gives "2026-11-20T10:00"; backend wants seconds too
const toApiDate = (v) => (v && v.length === 16 ? `${v}:00` : v)
const pretty = (iso) =>
  iso ? new Date(iso).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' }) : ''

export default function TeacherExams() {
  const [exams, setExams] = useState([])
  const [subjects, setSubjects] = useState([])
  const [subjectQuestions, setSubjectQuestions] = useState([])
  const [loading, setLoading] = useState(true)
  const [listError, setListError] = useState('')

  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState(emptyForm())
  const [editingId, setEditingId] = useState(null)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState('')
  const [loadingQuestions, setLoadingQuestions] = useState(false)

  const loadExams = useCallback(() => {
    api.get('/teacher/exams')
      .then((res) => {
        setExams(res.data)
        setListError('')
      })
      .catch((err) => setListError(err.response?.data?.message || 'Could not load exams'))
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    loadExams()
    api.get('/teacher/subjects').then((res) => setSubjects(res.data)).catch(() => {})
  }, [loadExams])

  // load all questions of the chosen subject (for the picker)
  useEffect(() => {
    if (!form.subjectId) {
      setSubjectQuestions([])
      return
    }
    setLoadingQuestions(true)
    api.get('/teacher/questions', { params: { subjectId: form.subjectId, page: 0, size: 50 } })
      .then((res) => setSubjectQuestions(res.data.content.filter((q) => q.approved)))
      .catch(() => setSubjectQuestions([]))
      .finally(() => setLoadingQuestions(false))
  }, [form.subjectId])

  const setField = (name, value) => setForm((f) => ({ ...f, [name]: value }))

  const changeSubject = (value) =>
    setForm((f) => ({ ...f, subjectId: value, questionIds: [] }))

  const toggleQuestion = (id) =>
    setForm((f) => ({
      ...f,
      questionIds: f.questionIds.includes(id)
        ? f.questionIds.filter((x) => x !== id)
        : [...f.questionIds, id],
    }))

  const selectedTotal = subjectQuestions
    .filter((q) => form.questionIds.includes(q.id))
    .reduce((sum, q) => sum + Number(q.marks), 0)

  const openNew = () => {
    setEditingId(null)
    setForm(emptyForm())
    setFormError('')
    setShowForm(true)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const openEdit = (e) => {
    setEditingId(e.id)
    setForm({
      title: e.title,
      description: e.description || '',
      subjectId: e.subjectId,
      durationMinutes: e.durationMinutes,
      passMarks: e.passMarks,
      negativeMarking: e.negativeMarking,
      startTime: toInputDate(e.startTime),
      endTime: toInputDate(e.endTime),
      maxViolations: e.maxViolations,
      shuffleQuestions: e.shuffleQuestions,
      shuffleOptions: e.shuffleOptions,
      adaptive: e.adaptive,
      proctoringEnabled: e.proctoringEnabled,
      webcamRequired: e.webcamRequired,
      certificateEnabled: e.certificateEnabled,
      questionIds: e.questions.map((q) => q.id),
    })
    setFormError('')
    setShowForm(true)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const closeForm = () => {
    setShowForm(false)
    setEditingId(null)
    setFormError('')
  }

  const submit = async (ev) => {
    ev.preventDefault()
    setFormError('')
    if (!form.subjectId) {
      setFormError('Please choose a subject')
      return
    }
    setSaving(true)
    try {
      const body = {
        ...form,
        subjectId: Number(form.subjectId),
        durationMinutes: Number(form.durationMinutes),
        passMarks: Number(form.passMarks),
        negativeMarking: Number(form.negativeMarking),
        maxViolations: Number(form.maxViolations),
        startTime: toApiDate(form.startTime),
        endTime: toApiDate(form.endTime),
        description: form.description.trim() || null,
      }
      if (editingId) {
        await api.put(`/teacher/exams/${editingId}`, body)
      } else {
        await api.post('/teacher/exams', body)
      }
      closeForm()
      loadExams()
    } catch (err) {
      const data = err.response?.data
      const fieldMsg = data?.errors ? Object.values(data.errors)[0] : null
      setFormError(fieldMsg || data?.message || 'Could not save the exam')
    } finally {
      setSaving(false)
    }
  }

  const action = async (exam, kind) => {
    setListError('')
    try {
      if (kind === 'delete') {
        if (!window.confirm(`Delete "${exam.title}"?`)) return
        await api.delete(`/teacher/exams/${exam.id}`)
      } else if (kind === 'publish') {
        if (!window.confirm('Publish this exam? You will not be able to edit it afterwards.')) return
        await api.post(`/teacher/exams/${exam.id}/publish`)
      } else if (kind === 'close') {
        if (!window.confirm('Close this exam now?')) return
        await api.post(`/teacher/exams/${exam.id}/close`)
      }
      loadExams()
    } catch (err) {
      setListError(err.response?.data?.message || 'Action failed')
    }
  }

  return (
    <DashboardLayout menu={teacherMenu}>
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold">📝 Exams</h1>
          <p className="text-xs opacity-60">{exams.length} exams</p>
        </div>
        {!showForm && (
          <button onClick={openNew}
            className="cursor-pointer rounded-xl bg-gradient-to-r from-brand to-cyan-glow px-5 py-2.5 text-sm font-semibold text-white shadow-lg transition hover:scale-105">
            + New exam
          </button>
        )}
      </div>

      {/* ---------- form ---------- */}
      <AnimatePresence>
        {showForm && (
          <motion.form
            onSubmit={submit}
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            className="glass mt-5 space-y-4 rounded-3xl p-5 shadow-lg"
          >
            <h2 className="font-semibold">{editingId ? 'Edit exam' : 'New exam'}</h2>

            <input placeholder="Exam title" value={form.title}
              onChange={(e) => setField('title', e.target.value)} required className={inputClass} />
            <textarea rows={2} placeholder="Description (optional)" value={form.description}
              onChange={(e) => setField('description', e.target.value)} className={inputClass} />

            <div className="grid gap-3 sm:grid-cols-2">
              <select value={form.subjectId} onChange={(e) => changeSubject(e.target.value)}
                required className={inputClass}>
                <option value="">Select subject</option>
                {subjects.map((s) => (
                  <option key={s.id} value={s.id}>{s.name}</option>
                ))}
              </select>
              <label className="text-xs">
                <span className="mb-1 block opacity-70">Duration (minutes)</span>
                <input type="number" min="1" max="600" value={form.durationMinutes}
                  onChange={(e) => setField('durationMinutes', e.target.value)}
                  required className={inputClass} />
              </label>
              <label className="text-xs">
                <span className="mb-1 block opacity-70">Start time</span>
                <input type="datetime-local" value={form.startTime}
                  onChange={(e) => setField('startTime', e.target.value)}
                  required className={inputClass} />
              </label>
              <label className="text-xs">
                <span className="mb-1 block opacity-70">End time</span>
                <input type="datetime-local" value={form.endTime}
                  onChange={(e) => setField('endTime', e.target.value)}
                  required className={inputClass} />
              </label>
              <label className="text-xs">
                <span className="mb-1 block opacity-70">Pass marks</span>
                <input type="number" min="0" step="0.25" value={form.passMarks}
                  onChange={(e) => setField('passMarks', e.target.value)}
                  required className={inputClass} />
              </label>
              <label className="text-xs">
                <span className="mb-1 block opacity-70">Negative marking (per wrong answer)</span>
                <input type="number" min="0" max="10" step="0.25" value={form.negativeMarking}
                  onChange={(e) => setField('negativeMarking', e.target.value)}
                  required className={inputClass} />
              </label>
              <label className="text-xs">
                <span className="mb-1 block opacity-70">Max violations before auto-submit</span>
                <input type="number" min="1" max="20" value={form.maxViolations}
                  onChange={(e) => setField('maxViolations', e.target.value)}
                  required className={inputClass} />
              </label>
            </div>

            <div className="grid gap-2 sm:grid-cols-2">
              {TOGGLES.map((t) => (
                <label key={t.key} className="flex cursor-pointer items-center gap-2 text-sm">
                  <input type="checkbox" checked={form[t.key]}
                    onChange={(e) => setField(t.key, e.target.checked)}
                    className="h-4 w-4 cursor-pointer accent-[#6C63FF]" />
                  {t.label}
                </label>
              ))}
            </div>

            {/* question picker */}
            <div>
              <p className="mb-2 text-sm font-medium">
                Questions{' '}
                <span className="text-xs font-normal opacity-60">
                  ({form.questionIds.length} selected, {selectedTotal} marks)
                </span>
              </p>
              {!form.subjectId && (
                <p className="text-sm opacity-60">Choose a subject to see its questions.</p>
              )}
              {form.subjectId && loadingQuestions && <p className="text-sm opacity-60">Loading...</p>}
              {form.subjectId && !loadingQuestions && subjectQuestions.length === 0 && (
                <p className="text-sm opacity-60">This subject has no approved questions yet.</p>
              )}
              <div className="max-h-72 space-y-2 overflow-y-auto pr-1">
                {subjectQuestions.map((q) => (
                  <label key={q.id}
                    className="glass flex cursor-pointer items-start gap-3 rounded-xl p-3 text-sm">
                    <input type="checkbox" checked={form.questionIds.includes(q.id)}
                      onChange={() => toggleQuestion(q.id)}
                      className="mt-1 h-4 w-4 cursor-pointer accent-[#6C63FF]" />
                    <span className="flex-1">
                      <span className="line-clamp-2">{q.questionText}</span>
                      <span className="mt-1 block text-xs opacity-70">
                        <span className={levelColor[q.difficulty]}>{q.difficulty}</span>
                        {' · '}{q.marks} mark(s)
                        {q.topic ? ` · ${q.topic}` : ''}
                      </span>
                    </span>
                  </label>
                ))}
              </div>
              {subjectQuestions.length >= 50 && (
                <p className="mt-1 text-xs opacity-60">Showing the latest 50 questions of this subject.</p>
              )}
            </div>

            {formError && (
              <p className="rounded-lg bg-red-500/10 px-3 py-2 text-sm text-red-500">{formError}</p>
            )}

            <div className="flex gap-3">
              <button disabled={saving}
                className="cursor-pointer rounded-xl bg-gradient-to-r from-brand to-cyan-glow px-6 py-2.5 text-sm font-semibold text-white shadow-lg transition hover:scale-105 disabled:opacity-60">
                {saving ? 'Saving...' : editingId ? 'Update exam' : 'Save as draft'}
              </button>
              <button type="button" onClick={closeForm}
                className="glass cursor-pointer rounded-xl px-6 py-2.5 text-sm font-medium">
                Cancel
              </button>
            </div>
          </motion.form>
        )}
      </AnimatePresence>

      {listError && (
        <p className="mt-4 rounded-lg bg-red-500/10 px-3 py-2 text-sm text-red-500">{listError}</p>
      )}

      {/* ---------- list ---------- */}
      <div className="mt-5 grid gap-4 lg:grid-cols-2">
        {loading && <p className="text-sm opacity-70">Loading...</p>}
        {!loading && exams.length === 0 && (
          <p className="text-sm opacity-70">No exams yet. Click "New exam" to create one.</p>
        )}
        {exams.map((e) => (
          <motion.div key={e.id} layout initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }}
            className="glass rounded-2xl p-5 shadow-lg">
            <div className="flex items-start justify-between gap-2">
              <h3 className="text-lg font-bold">{e.title}</h3>
              <span className={`rounded-full px-3 py-1 text-xs font-semibold ${statusColor[e.status]}`}>
                {e.status}
              </span>
            </div>
            <p className="mt-1 text-xs font-medium text-brand">{e.subjectName}</p>
            {e.description && <p className="mt-2 text-sm opacity-70">{e.description}</p>}

            <div className="mt-3 grid grid-cols-2 gap-2 text-xs opacity-80">
              <span>⏱️ {e.durationMinutes} min</span>
              <span>❓ {e.questionCount} questions</span>
              <span>🎯 {e.totalMarks} marks (pass {e.passMarks})</span>
              <span>➖ Negative: {e.negativeMarking}</span>
              <span className="col-span-2">🕒 {pretty(e.startTime)} → {pretty(e.endTime)}</span>
            </div>

            <div className="mt-3 flex flex-wrap gap-1.5 text-[11px]">
              {e.shuffleQuestions && <span className="rounded-full bg-brand/15 px-2 py-0.5 text-brand">Shuffled</span>}
              {e.adaptive && <span className="rounded-full bg-brand/15 px-2 py-0.5 text-brand">Adaptive</span>}
              {e.proctoringEnabled && <span className="rounded-full bg-brand/15 px-2 py-0.5 text-brand">Proctored</span>}
              {e.webcamRequired && <span className="rounded-full bg-brand/15 px-2 py-0.5 text-brand">Webcam</span>}
              {e.certificateEnabled && <span className="rounded-full bg-brand/15 px-2 py-0.5 text-brand">Certificate</span>}
            </div>

            <div className="mt-4 flex flex-wrap gap-2">
              {e.status === 'DRAFT' && (
                <>
                  <button onClick={() => action(e, 'publish')}
                    className="cursor-pointer rounded-lg bg-gradient-to-r from-brand to-cyan-glow px-4 py-1.5 text-xs font-semibold text-white">
                    🚀 Publish
                  </button>
                  <button onClick={() => openEdit(e)}
                    className="glass cursor-pointer rounded-lg px-4 py-1.5 text-xs font-medium">✏️ Edit</button>
                  <button onClick={() => action(e, 'delete')}
                    className="cursor-pointer rounded-lg bg-red-500/10 px-4 py-1.5 text-xs font-medium text-red-500">
                    🗑️ Delete
                  </button>
                </>
              )}
              {e.status === 'PUBLISHED' && (
                <button onClick={() => action(e, 'close')}
                  className="cursor-pointer rounded-lg bg-red-500/10 px-4 py-1.5 text-xs font-medium text-red-500">
                  ⛔ Close exam
                </button>
              )}
              {e.status === 'CLOSED' && (
                <span className="text-xs opacity-60">This exam is closed.</span>
              )}
            </div>
          </motion.div>
        ))}
      </div>
    </DashboardLayout>
  )
}