import React from 'react'
import { brl } from '../utils/formatBRL'

export default function Kanban({ items, onEdit, onRent, onFree }) {
  const cols = [
    { id: 'd', title: 'Disponíveis', f: i => !i.alugado },
    { id: 'a', title: 'Alugados', f: i => i.alugado }
  ]

  return (
    <div className="kb">
      {cols.map(c => (
        <div className="kb-col" key={c.id}>
          <h4>{c.title}</h4>
          {items.filter(c.f).map(p => (
            <div className="card" key={p.id}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                <h5 style={{ margin: 0 }}>{p.tipo} nº {p.numero}</h5>
                <span className={`badge ${p.alugado ? 'busy' : 'ok'}`}>
                  {p.alugado ? 'Alugado' : 'Disponível'}
                </span>
              </div>
              <div style={{ marginBottom: '12px' }}>
                <div className="small">{p.endereco}</div>
                <div className="small">Diária: {brl(p.diaria)}</div>
              </div>
              <div className="row" style={{ marginTop: 'auto' }}>
                <button className="btn" onClick={() => onEdit(p)}>Editar</button>
                {!p.alugado && <button className="btn success" onClick={() => onRent(p)}>Alugar</button>}
                {p.alugado && <button className="btn warning" onClick={() => onFree(p)}>Disponibilizar</button>}
              </div>
            </div>
          ))}
        </div>
      ))}
    </div>
  )
}