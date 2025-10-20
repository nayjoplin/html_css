import React from 'react'
import { brl } from '../utils/formatBRL'
export default function Report({rentals,properties}){
  const rows=rentals.map(r=>{const p=properties.find(pp=>pp.id===r.propertyId)||{};const dias=Math.max(1,Math.ceil((new Date(r.fim)-new Date(r.inicio))/86400000)+1);const total=(p.diaria||0)*dias*(r.fator||1);return {id:r.id,prop:`${p.tipo} nº ${p.numero}`,inq:r.inquilino,dias,total}})
  const csv=['id,imovel,inquilino,dias,total'].concat(rows.map(x=>`${x.id},"${x.prop}",${x.inq},${x.dias},${x.total}`)).join('\n')
  const json=JSON.stringify(rows,null,2)
  const dl=(data,name,type)=>{const b=new Blob([data],{type});const a=document.createElement('a');a.href=URL.createObjectURL(b);a.download=name;a.click();URL.revokeObjectURL(a.href)}
  const total=rows.reduce((a,b)=>a+b.total,0)
  return (<div className="card"><div className="header"><h2>Relatório — Imóveis Alugados</h2>
    <div className="row"><button className="btn" onClick={()=>dl(csv,'relatorio.csv','text/csv')}>Baixar CSV</button><button className="btn" onClick={()=>dl(json,'relatorio.json','application/json')}>Baixar JSON</button></div></div>
    <table className="table"><thead><tr><th>ID</th><th>Imóvel</th><th>Inquilino</th><th>Dias</th><th>Total</th></tr></thead>
    <tbody>{rows.map(r=>(<tr key={r.id}><td>{r.id}</td><td>{r.prop}</td><td>{r.inq}</td><td>{r.dias}</td><td>{brl(r.total)}</td></tr>))}</tbody>
    <tfoot><tr><td colSpan="4"><strong>Total geral</strong></td><td><strong>{brl(total)}</strong></td></tr></tfoot></table></div>)
}