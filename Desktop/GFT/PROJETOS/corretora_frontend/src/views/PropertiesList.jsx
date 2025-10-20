import React from 'react'
import { brl } from '../utils/formatBRL'

export default function PropertiesList({ items, onEdit, onDelete, onRent, onFree }) {
  return (
    <div className="card">
      <table className="table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tipo</th>
            <th>Endereço</th>
            <th>Número</th>
            <th>Diária</th>
            <th>Status</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {items.map(p => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.tipo}</td>
              <td>{p.endereco}</td>
              <td>{p.numero}</td>
              <td>{brl(p.diaria)}</td>
              <td>
                {p.alugado ? (
                  <span className="badge busy">Alugado</span>
                ) : (
                  <span className="badge ok">Disponível</span>
                )}
              </td>
              <td style={{ display: 'flex', gap: 6 }}>
                <button className="btn" onClick={() => onEdit(p)}>Editar</button>
                {!p.alugado && <button className="btn success" onClick={() => onRent(p)}>Alugar</button>}
                {p.alugado && <button className="btn warning" onClick={() => onFree(p)}>Disponibilizar</button>}
                <button className="btn danger" onClick={() => onDelete(p)}>Excluir</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}