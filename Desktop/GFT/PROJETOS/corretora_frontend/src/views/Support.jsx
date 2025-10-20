import React from 'react'
import useLocalStorage from '../hooks/useLocalStorage'
export default function Support(){
  const [tickets,setTickets]=useLocalStorage('tickets',[])
  const [form,setForm]=React.useState({assunto:'',descricao:''})
  const add=()=>{if(!form.assunto)return;setTickets([...tickets,{id:Date.now(),...form,status:'aberto'}]);setForm({assunto:'',descricao:''})}
  const exportCsv=()=>{const csv=['id,assunto,descricao,status'].concat(tickets.map(t=>`${t.id},"${t.assunto}","${t.descricao}",${t.status}`)).join('\n');const b=new Blob([csv],{type:'text/csv'});const a=document.createElement('a');a.href=URL.createObjectURL(b);a.download='tickets.csv';a.click()}
  return (<div className="card"><div className="header"><h2>Suporte TI</h2><div><button className="btn" onClick={exportCsv}>Exportar CSV</button></div></div>
    <div className="row"><fieldset style={{flex:1}}><label>Assunto</label><input className="input" value={form.assunto} onChange={e=>setForm({...form,assunto:e.target.value})}/></fieldset></div>
    <fieldset><label>Descrição</label><textarea className="input" rows="4" value={form.descricao} onChange={e=>setForm({...form,descricao:e.target.value})}></textarea></fieldset>
    <div className="row" style={{justifyContent:'flex-end'}}><button className="btn success" onClick={add}>Abrir chamado</button></div>
    <hr />{tickets.map(t=>(<div className="card" key={t.id}><div style={{display:'flex',justifyContent:'space-between'}}><div><strong>#{t.id}</strong> — {t.assunto}</div><span className="badge">{t.status}</span></div><div className="small">{t.descricao}</div></div>))}
  </div>)
}