import React from 'react'
import { eachDayOfInterval, startOfMonth, endOfMonth, format } from 'date-fns'
import { ptBR } from 'date-fns/locale'
function inRange(date,start,end){const d=new Date(date).setHours(0,0,0,0);const s=new Date(start).setHours(0,0,0,0);const e=new Date(end).setHours(0,0,0,0);return d>=s&&d<=e}
export default function CalendarView({rentals,properties}){
  const [refDate,setRefDate]=React.useState(new Date())
  const days=eachDayOfInterval({start:startOfMonth(refDate),end:endOfMonth(refDate)})
  const eventsByDay=days.map(day=>({day,list:rentals.filter(r=>inRange(day,r.inicio,r.fim)).map(r=>{const p=properties.find(pp=>pp.id===r.propertyId)||{};return `${p.tipo} ${p.numero} (${r.inquilino})`})}))
  return (<div className="card"><div className="header"><h2>Calendário — {format(refDate,"LLLL 'de' yyyy",{locale:ptBR})}</h2>
    <div className="row"><button className="btn" onClick={()=>setRefDate(d=>new Date(d.getFullYear(),d.getMonth()-1,1))}>◀</button>
    <button className="btn" onClick={()=>setRefDate(d=>new Date(d.getFullYear(),d.getMonth()+1,1))}>▶</button></div></div>
    <div className="calendar">{['Dom','Seg','Ter','Qua','Qui','Sex','Sáb'].map(w=><div key={w} className="small" style={{textAlign:'center',opacity:.7}}>{w}</div>)}
      {eventsByDay.map(({day,list})=>(<div className="day" key={day.toISOString()}><div className="d">{format(day,'d')}</div><div>{list.map((t,i)=><div key={i} className="tag">{t}</div>)}</div></div>))}
    </div></div>)
}