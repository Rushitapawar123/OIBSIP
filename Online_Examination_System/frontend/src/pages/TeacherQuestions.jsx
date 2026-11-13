import { useCallback, useEffect, useState } from 'react'
import { AnimatePresence, motion } from 'framer-motion'
import DashboardLayout from '../components/DashboardLayout'
import { teacherMenu } from './TeacherDashboard'
import api from '../api/axios'

const inputClass =
  'w-full rounded-xl border border-slate-300/60 bg-white/60 px-4 py-3 text-sm outline-none transition focus:border-brand focus:ring-2 focus:ring-brand/30 dark:border-white/10 dark:bg-white/5'

const TYPES = [
  { value: 'SINGLE', label: 'Single correct' },
  { value: 'MULTIPLE', label: 'Multiple correct' },
  { value: 'TRUE_FALSE', label: 'True / False' },
]
const LEVELS = ['EASY', 'MEDIUM', 'HARD']

const levelColor = {
  EASY: 'bg-green-500/15 text-green-500',
  MEDIUM: 'bg-amber-500/15 text-amber-500',
  HARD: 'bg-red-500/15 text-red-500',
}

const blankOptions = () => [
  { optionText: '', correct: false },
  { optionText: '', correct: false },
  { optionText: '', correct: false },
  { optionText: '', correct: false },
]

const emptyForm = () => ({
  subjectId: '',
  questionText: '',
  questionType: 'SINGLE',
  difficulty: 'EASY',
  marks: 1,
  topic: '',
  explanation: '',
  options: blankOptions(),
})

export default function TeacherQuestions() {
  const [subjects, setSubjects] = useState([])
  const [questions, setQuestions] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [filterSubject, setFilterSubject] = useState('')
  const [filterLevel, setFilterLevel] = useState('')
  const [loading, setLoading] = useState(true)

  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState(emptyForm())
  const [editingId, setEditingId] = useState(null)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState('')
  const [listError, setListError] = useState('')

  useEffect(() => {
    api.get('/teacher/subjects').then((res) => setSubjects(res.data)).catch(() => {})
  }, [])

  const load = useCallback(() => {
    setLoading(true)
    const params = { page, size: 10 }
    if (filterSubject) params.subjectId = filterSubject
    if (filterSubject && filterLevel) params.difficulty = filterLevel
    api.get('/teacher/questions', { params })
      .then((res) => {
        setQuestions(res.data.content)
        setTotalPages(res.data.totalPages)
        setTotalElements(res.data.totalElements)
        setListError('')
      })
      .catch((err) => setListError(err.response?.data?.message || 'Could not load questions'))
      .finally(() => setLoading(false))
  }, [page, filterSubject, filterLevel])

  useEffect(() => {
    load()
  }, [load])

  // ---------- filters ----------
  const onSubjectFilter = (value) => {
    setFilterSubject(value)
    if (!value) setFilterLevel('')
    setPage(0)
  }
  const onLevelFilter = (value) => {
    setFilterLevel(value)
    setPage(0)
  }

  // ---------- form helpers ----------
  const setField = (name, value) => setForm((f) => ({ ...f, [name]: value }))

  const changeType = (type) => {
    setForm((f) => {
      let options = f.options.map((o) => ({ ...o, correct: false }))
      if (type === 'TRUE_FALSE') {
        options = [
          { optionText: 'True', correct: false },
          { optionText: 'False', correct: false },
        ]
      } else if (f.questionType === 'TRUE_FALSE') {
        options = blankOptions()
      }
      return { ...f, questionType: type, options }
    })
  }

  const setOptionText = (i, text) =>
    setForm((f) => ({
      ...f,
      options: f.options.map((o, idx) => (idx === i ? { ...o, optionText: text } : o)),
    }))

  const markCorrect = (i) =>
    setForm((f) => ({
      ...f,
      options: f.options.map((o, idx) => {
        if (f.questionType === 'MULTIPLE') {
          return idx === i ? { ...o, correct: !o.correct } : o
        }
        return { ...o, correct: idx === i }
      }),
    }))

  const addOption = () =>
    setForm((f) =>
      f.options.length >= 6 ? f : { ...f, options: [...f.options, { optionText: '', correct: false }] }
    )

  const removeOption = (i) =>
    setForm((f) =>
      f.options.length <= 2 ? f : { ...f, options: f.options.filter((_, idx) => idx !== i) }
    )

  const openNew = () => {
    setEditingId(null)
    setForm({ ...emptyForm(), subjectId: filterSubject || '' })
    setFormError('')
    setShowForm(true)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const openEdit = (q) => {
    setEditingId(q.id)
    setForm({
      subjectId: q.subjectId,
      questionText: q.questionText,
      questionType: q.questionType,
      difficulty: q.difficulty,
      marks: q.marks,
      topic: q.topic || '',
      explanation: q.explanation || '',
      options: q.options.map((o) => ({ optionText: o.optionText, correct: o.correct })),
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

  // ---------- save / delete ----------
  const submit = async (e) => {
    e.preventDefault()
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
        marks: Number(form.marks),
        topic: form.topic.trim() || null,
        explanation: form.explanation.trim() || null,
      }
      if (editingId) {
        await api.put(`/teacher/questions/${editingId}`, body)
      } else {
        await api.post('/teacher/questions', body)
      }
      closeForm()
      setPage(0)
      load()
    } catch (err) {
      const data = err.response?.data
      const fieldMsg = data?.errors ? Object.values(data.errors)[0] : null
      setFormError(fieldMsg || data?.message || 'Could not save the question')
    } finally {
      setSaving(false)
    }
  }

  const remove = async (q) => {
    if (!window.confirm('Delete this question?')) return
    setListError('')
    try {
      await api.delete(`/teacher/questions/${q.id}`)
      if (questions.length === 1 && page > 0) setPage(page - 1)
      else load()
    } catch (err) {
      setListError(err.response?.data?.message || 'Could not delete the question')
    }
  }

  const isMultiple = form.questionType === 'MULTIPLE'
  const isTrueFalse = form.questionType === 'TRUE_FALSE'

  return (
    <DashboardLayout menu={teacherMenu}>
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold">❓ Question Bank</h1>
          <p className="text-xs opacity-60">{totalElements} questions</p>
        </div>
        {!showForm && (
          <button onClick={openNew}
            className="cursor-pointer rounded-xl bg-gradient-to-r from-brand to-cyan-glow px-5 py-2.5 text-sm font-semibold text-white shadow-lg transition hover:scale-105">
            + New question
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
            <h2 className="font-semibold">{editingId ? 'Edit question' : 'New question'}</h2>

            <div className="grid gap-3 sm:grid-cols-2">
              <select value={form.subjectId} onChange={(e) => setField('subjectId', e.target.value)}
                className={inputClass}>
                <option value="">Select subject</option>
                {subjects.map((s) => (
                  <option key={s.id} value={s.id}>{s.name}</option>
                ))}
              </select>
              <input placeholder="Topic (e.g. Java Basics)" value={form.topic}
                onChange={(e) => setField('topic', e.target.value)} className={inputClass} />
              <select value={form.questionType} onChange={(e) => changeType(e.target.value)}
                className={inputClass}>
                {TYPES.map((t) => (
                  <option key={t.value} value={t.value}>{t.label}</option>
                ))}
              </select>
              <select value={form.difficulty} onChange={(e) => setField('difficulty', e.target.value)}
                className={inputClass}>
                {LEVELS.map((l) => (
                  <option key={l} value={l}>{l}</option>
                ))}
              </select>
            </div>

            <textarea rows={3} placeholder="Question text" value={form.questionText}
              onChange={(e) => setField('questionText', e.target.value)} required className={inputClass} />

            <div>
              <p className="mb-2 text-sm font-medium">
                Options{' '}
                <span className="text-xs font-normal opacity-60">
                  ({isMultiple ? 'tick all correct answers' : 'select the correct answer'})
                </span>
              </p>
              <div className="space-y-2">
                {form.options.map((o, i) => (
                  <div key={i} className="flex items-center gap-2">
                    <input
                      type={isMultiple ? 'checkbox' : 'radio'}
                      name="correct"
                      checked={o.correct}
                      onChange={() => markCorrect(i)}
                      className="h-4 w-4 cursor-pointer accent-[#6C63FF]"
                    />
                    <input
                      placeholder={`Option ${i + 1}`}
                      value={o.optionText}
                      onChange={(e) => setOptionText(i, e.target.value)}
                      readOnly={isTrueFalse}
                      required
                      className={inputClass}
                    />
                    {!isTrueFalse && form.options.length > 2 && (
                      <button type="button" onClick={() => removeOption(i)}
                        className="cursor-pointer rounded-lg px-2 text-red-500" aria-label="Remove option">
                        ✕
                      </button>
                    )}
                  </div>
                ))}
              </div>
              {!isTrueFalse && form.options.length < 6 && (
                <button type="button" onClick={addOption}
                  className="mt-2 cursor-pointer text-sm font-medium text-brand hover:underline">
                  + Add option
                </button>
              )}
            </div>

            <div className="grid gap-3 sm:grid-cols-2">
              <input type="number" step="0.25" min="0.25" max="100" placeholder="Marks"
                value={form.marks} onChange={(e) => setField('marks', e.target.value)}
                required className={inputClass} />
            </div>

            <textarea rows={2} placeholder="Explanation shown after the exam (optional)"
              value={form.explanation} onChange={(e) => setField('explanation', e.target.value)}
              className={inputClass} />

            {formError && (
              <p className="rounded-lg bg-red-500/10 px-3 py-2 text-sm text-red-500">{formError}</p>
            )}

            <div className="flex gap-3">
              <button disabled={saving}
                className="cursor-pointer rounded-xl bg-gradient-to-r from-brand to-cyan-glow px-6 py-2.5 text-sm font-semibold text-white shadow-lg transition hover:scale-105 disabled:opacity-60">
                {saving ? 'Saving...' : editingId ? 'Update question' : 'Save question'}
              </button>
              <button type="button" onClick={closeForm}
                className="glass cursor-pointer rounded-xl px-6 py-2.5 text-sm font-medium">
                Cancel
              </button>
            </div>
          </motion.form>
        )}
      </AnimatePresence>

      {/* ---------- filters ---------- */}
      <div className="mt-5 grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
        <select value={filterSubject} onChange={(e) => onSubjectFilter(e.target.value)} className={inputClass}>
          <option value="">All subjects</option>
          {subjects.map((s) => (
            <option key={s.id} value={s.id}>{s.name}</option>
          ))}
        </select>
        <select value={filterLevel} onChange={(e) => onLevelFilter(e.target.value)}
          disabled={!filterSubject} className={`${inputClass} disabled:opacity-50`}>
          <option value="">{filterSubject ? 'All difficulties' : 'Select a subject to filter difficulty'}</option>
          {LEVELS.map((l) => (
            <option key={l} value={l}>{l}</option>
          ))}
        </select>
      </div>

      {listError && (
        <p className="mt-4 rounded-lg bg-red-500/10 px-3 py-2 text-sm text-red-500">{listError}</p>
      )}

      {/* ---------- list ---------- */}
      <div className="mt-5 space-y-4">
        {loading && <p className="text-sm opacity-70">Loading...</p>}
        {!loading && questions.length === 0 && (
          <p className="text-sm opacity-70">No questions found. Click "New question" to add one.</p>
        )}
        {!loading &&
          questions.map((q) => (
            <motion.div key={q.id} layout initial={{ opacity: 0, y: 15 }} animate={{ opacity: 1, y: 0 }}
              className="glass rounded-2xl p-5 shadow-lg">
              <div className="flex flex-wrap items-center gap-2 text-xs">
                <span className={`rounded-full px-3 py-1 font-semibold ${levelColor[q.difficulty]}`}>
                  {q.difficulty}
                </span>
                <span className="rounded-full bg-brand/15 px-3 py-1 font-medium text-brand">{q.subjectName}</span>
                {q.topic && <span className="rounded-full bg-slate-500/15 px-3 py-1">{q.topic}</span>}
                <span className="opacity-60">{q.marks} mark(s)</span>
                <span className="opacity-60">• {q.questionType.replace('_', '/')}</span>
              </div>

              <p className="mt-3 font-semibold whitespace-pre-wrap">{q.questionText}</p>

              <ul className="mt-3 space-y-1.5 text-sm">
                {q.options.map((o) => (
                  <li key={o.id}
                    className={`rounded-lg px-3 py-2 ${o.correct ? 'bg-green-500/15 font-medium text-green-500' : 'bg-slate-500/10'}`}>
                    {o.correct ? '✓ ' : ''}{o.optionText}
                  </li>
                ))}
              </ul>

              <div className="mt-4 flex gap-2">
                <button onClick={() => openEdit(q)}
                  className="glass cursor-pointer rounded-lg px-4 py-1.5 text-xs font-medium">✏️ Edit</button>
                <button onClick={() => remove(q)}
                  className="cursor-pointer rounded-lg bg-red-500/10 px-4 py-1.5 text-xs font-medium text-red-500">
                  🗑️ Delete
                </button>
              </div>
            </motion.div>
          ))}
      </div>

      {/* ---------- pagination ---------- */}
      {totalPages > 1 && (
        <div className="mt-6 flex items-center justify-center gap-4">
          <button disabled={page === 0} onClick={() => setPage(page - 1)}
            className="glass cursor-pointer rounded-xl px-4 py-2 text-sm font-medium disabled:opacity-40">
            ← Previous
          </button>
          <span className="text-sm">Page {page + 1} of {totalPages}</span>
          <button disabled={page + 1 >= totalPages} onClick={() => setPage(page + 1)}
            className="glass cursor-pointer rounded-xl px-4 py-2 text-sm font-medium disabled:opacity-40">
            Next →
          </button>
        </div>
      )}
    </DashboardLayout>
  )
}