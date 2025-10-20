
import React from 'react';

const NavLink = ({ id, route, setRoute, label, icon }) => (
  <a
    href={'#' + id}
    className={`nav-link ${route === id ? 'active' : ''}`}
    onClick={(e) => {
      e.preventDefault();
      setRoute(id);
    }}
  >
    {icon}
    <span>{label}</span>
  </a>
);

export default function Sidebar({ route, setRoute }) {
  const navItems = [
    { id: 'imoveis-lista', label: 'Imóveis — Lista', icon: <IconHome /> },
    { id: 'imoveis-kanban', label: 'Imóveis — Kanban', icon: <IconKanban /> },
    { id: 'imoveis-cards', label: 'Imóveis — Cards', icon: <IconCards /> },
    { id: 'calendario', label: 'Calendário', icon: <IconCalendar /> },
    { id: 'relatorio', label: 'Relatório', icon: <IconReport /> },
  ];

  const accountItems = [
    { id: 'minha-conta', label: 'Minha Conta', icon: <IconUser /> },
    { id: 'suporte', label: 'Suporte TI', icon: <IconSupport /> },
    { id: 'onboarding', label: 'Onboarding', icon: <IconOnboarding /> },
  ];

  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <div className="logo">
          <svg width="32" height="32" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="32" height="32" rx="8" fill="#22d3ee"/>
            <path d="M10 14L16 10L22 14V22H10V14Z" fill="white"/>
          </svg>
          <span>Corretora PRO</span>
        </div>
      </div>
      <nav className="nav-group">
        <h6 className="nav-group-title">Imóveis</h6>
        {navItems.map(item => <NavLink key={item.id} {...item} route={route} setRoute={setRoute} />)}
      </nav>
      <nav className="nav-group">
        <h6 className="nav-group-title">Conta</h6>
        {accountItems.map(item => <NavLink key={item.id} {...item} route={route} setRoute={setRoute} />)}
      </nav>
      <div className="sidebar-footer">
        <div className="user-profile">
            <div className="user-avatar">
                <img src="https://i.pravatar.cc/40?img=1" alt="User" />
            </div>
            <div className="user-info">
                <span className="user-name">Usuário</span>
                <span className="user-role">Admin</span>
            </div>
        </div>
      </div>
    </aside>
  );
}

// Placeholder Icons (simple SVGs)
const IconHome = () => <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline></svg>;
const IconKanban = () => <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><line x1="9" y1="3" x2="9" y2="21"></line><line x1="15" y1="3" x2="15" y2="21"></line></svg>;
const IconCards = () => <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect><path d="M2 10h20"></path></svg>;
const IconCalendar = () => <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>;
const IconReport = () => <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21.21 15.89A10 10 0 1 1 8 2.83"></path><path d="M22 12A10 10 0 0 0 12 2v10z"></path></svg>;
const IconUser = () => <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>;
const IconSupport = () => <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10"></circle><path d="m12 16.5-4-4 1.5-1.5 2.5 2.5 5-5L19 10.5z"></path></svg>;
const IconOnboarding = () => <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M5 12h14"></path><path d="m12 5 7 7-7 7"></path></svg>;
