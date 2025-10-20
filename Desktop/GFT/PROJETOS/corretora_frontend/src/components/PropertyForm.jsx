import React from 'react';

export default function PropertyForm({ open, onClose, onSubmit, initial }) {
  const [formData, setFormData] = React.useState(initial || {});

  React.useEffect(() => {
    setFormData(initial || {});
  }, [initial]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name.includes('.')) {
      const [object, key] = name.split('.');
      setFormData(prev => ({ ...prev, [object]: { ...prev[object], [key]: value } }));
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(formData);
  };

  if (!open) return null;

  return (
    <div className="overlay">
      <div className="modal">
        <form onSubmit={handleSubmit}>
          <h2>{initial ? 'Editar Imóvel' : 'Novo Imóvel'}</h2>
          <div className="form-group">
            <label>Tipo</label>
            <input type="text" name="tipo" value={formData.tipo || ''} onChange={handleChange} className="input" />
          </div>
          <div className="form-group">
            <label>Endereço</label>
            <input type="text" name="endereco" value={formData.endereco || ''} onChange={handleChange} className="input" />
          </div>
          <div className="form-group">
            <label>Número</label>
            <input type="text" name="numero" value={formData.numero || ''} onChange={handleChange} className="input" />
          </div>
          <div className="form-group">
            <label>Diária</label>
            <input type="number" name="diaria" value={formData.diaria || ''} onChange={handleChange} className="input" />
          </div>
          <h3>Proprietário</h3>
          <div className="form-group">
            <label>Nome</label>
            <input type="text" name="proprietario.nome" value={formData.proprietario?.nome || ''} onChange={handleChange} className="input" />
          </div>
          <div className="form-group">
            <label>Telefone</label>
            <input type="text" name="proprietario.telefone" value={formData.proprietario?.telefone || ''} onChange={handleChange} className="input" />
          </div>
          <div className="form-group">
            <label>CPF</label>
            <input type="text" name="proprietario.cpf" value={formData.proprietario?.cpf || ''} onChange={handleChange} className="input" />
          </div>
          <div className="button-group">
            <button type="submit" className="btn success">Salvar</button>
            <button type="button" onClick={onClose} className="btn secondary">Cancelar</button>
          </div>
        </form>
      </div>
    </div>
  );
}