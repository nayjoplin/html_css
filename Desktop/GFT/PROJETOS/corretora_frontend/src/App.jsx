import React from 'react'
import Sidebar from './components/Sidebar.jsx'
import PropertiesList from './views/PropertiesList.jsx'
import Kanban from './views/Kanban.jsx'
import Cards from './views/Cards.jsx'
import CalendarView from './views/CalendarView.jsx'
import Report from './views/Report.jsx'
import Account from './views/Account.jsx'
import Support from './views/Support.jsx'
import Onboarding from './views/Onboarding.jsx'
import PropertyForm from './components/PropertyForm.jsx'
import useLocalStorage from './hooks/useLocalStorage.js'
import { seedProperties, seedRentals } from './data/seed.js'

function uid(){ return Math.floor(Math.random()*1e8) }
function discountFactor(days){ if(days>=1095) return 0.85; if(days>=730) return 0.90; if(days>=365) return 0.95; return 1.0; }



export default function App(){
  const [route,setRoute]=React.useState('imoveis-lista')
  const [properties,setProperties]=useLocalStorage('properties',seedProperties)
  const [rentals,setRentals]=useLocalStorage('rentals',seedRentals)
  const [modal,setModal]=React.useState(false)
  const [editing,setEditing]=React.useState(null)

  const addProp=()=>{ setEditing(null); setModal(true) }
  const onSubmitProp=(data)=>{ if(editing){ setProperties(ps=>ps.map(p=>p.id===editing.id?{...editing,...data}:p)) } else { setProperties(ps=>[...ps,{id:uid(),...data,alugado:false}]) } setModal(false) }
  const onDelete=(p)=>{ if(confirm('Confirmar exclusão?')){ setProperties(ps=>ps.filter(x=>x.id!==p.id)); setRentals(rs=>rs.filter(r=>r.propertyId!==p.id)) } }
  const onEdit=(p)=>{ setEditing(p); setModal(true) }
  const onRent=(p)=>{
    const inquilino=prompt('Nome do inquilino:'); if(!inquilino) return;
    const inicio=prompt('Data início (YYYY-MM-DD):', new Date().toISOString().slice(0,10)); if(!inicio) return;
    const fim=prompt('Data fim (YYYY-MM-DD):', new Date(Date.now()+86400000*7).toISOString().slice(0,10)); if(!fim) return;
    const dias=Math.max(1,Math.ceil((new Date(fim)-new Date(inicio))/86400000)+1)
    const fator=discountFactor(dias)
    setRentals(rs=>[...rs,{id:uid(),propertyId:p.id,inquilino,inicio,fim,fator}])
    setProperties(ps=>ps.map(x=>x.id===p.id?{...x,alugado:true}:x))
    alert('Alugado com sucesso!')
  }
  const onFree=(p)=>{ setProperties(ps=>ps.map(x=>x.id===p.id?{...x,alugado:false}:x)) }

  const viewProps={ items:properties,onEdit,onDelete,onRent,onFree }

  return (<div className="container">
    <Sidebar route={route} setRoute={setRoute} />
    <main className="main">
      <div className="header"><h1>Corretora Imobiliária — Frontend</h1><div className="row"><button className="btn success" onClick={addProp}>+ Novo Imóvel</button></div></div>
      {route==='imoveis-lista' && <PropertiesList {...viewProps} />}
      {route==='imoveis-kanban' && <Kanban {...viewProps} />}
      {route==='imoveis-cards' && <Cards {...viewProps} />}
      {route==='calendario' && <CalendarView rentals={rentals} properties={properties} />}
      {route==='relatorio' && <Report rentals={rentals} properties={properties} />}
      {route==='minha-conta' && <Account />}
      {route==='suporte' && <Support />}
      {route==='onboarding' && <Onboarding />}
      {modal && <PropertyForm open={modal} onClose={()=>setModal(false)} onSubmit={onSubmitProp} initial={editing} />}
    </main>
  </div>)
}