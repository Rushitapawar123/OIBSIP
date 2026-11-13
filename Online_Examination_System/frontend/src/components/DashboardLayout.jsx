import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import useTheme from '../hooks/useTheme'

export default function DashboardLayout({ menu, children }) {
  const { user, logout } = useAuth()
  const { dark, toggle } = useTheme()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout()
    navigate('/login')
  }

  const linkClass = ({ isActive }) =>
    `flex items-center gap-3 rounded-xl px-4 py-3 text-sm font-medium transition ${
      isActive
        ? 'bg-gradient-to-r from-brand to-cyan-glow text-white shadow-lg'
        : 'hover:bg-brand/10'
    }`

  return (
    <div className="relative min-h-screen overflow-hidden">
      <div className="pointer-events-none absolute -top-32 -left-32 h-96 w-96 rounded-full bg-brand/20 blur-3xl" />
      <div className="pointer-events-none absolute bottom-0 -right-32 h-96 w-96 rounded-full bg-pink-glow/10 blur-3xl" />

      {/* Sidebar: sirf laptop par */}
      <aside className="glass fixed top-0 left-0 z-20 hidden h-full w-64 flex-col p-5 md:flex">
        <span className="bg-gradient-to-r from-brand to-cyan-glow bg-clip-text text-2xl font-extrabold text-transparent">
          ExamPro
        </span>
        <nav className="mt-8 space-y-2">
          {menu.map((m) => (
            <NavLink key={m.to} to={m.to} end className={linkClass}>
              <span>{m.icon}</span>
              {m.label}
            </NavLink>
          ))}
        </nav>
      </aside>

      <div className="relative z-10 md:pl-64">
        {/* Navbar */}
        <header className="flex items-center justify-between px-5 py-4">
          <div>
            <p className="text-xs opacity-60">Namaste</p>
            <p className="font-semibold">{user?.fullName}</p>
          </div>
          <div className="flex items-center gap-2">
            <button onClick={toggle}
              className="glass cursor-pointer rounded-full px-4 py-2 text-sm font-medium">
              {dark ? '☀️' : '🌙'}
            </button>
            <button onClick={handleLogout}
              className="cursor-pointer rounded-full bg-gradient-to-r from-brand to-pink-glow px-4 py-2 text-sm font-semibold text-white">
              Logout
            </button>
          </div>
        </header>

        <main className="px-5 pt-2 pb-28 md:pb-10">{children}</main>
      </div>

      {/* Bottom menu: sirf phone par */}
      <nav className="glass fixed right-0 bottom-0 left-0 z-20 flex justify-around p-2 md:hidden">
        {menu.map((m) => (
          <NavLink key={m.to} to={m.to} end
            className={({ isActive }) =>
              `flex flex-col items-center rounded-xl px-3 py-1 text-xs ${
                isActive ? 'text-brand font-semibold' : 'opacity-70'
              }`}>
            <span className="text-lg">{m.icon}</span>
            {m.label}
          </NavLink>
        ))}
      </nav>
    </div>
  )
}