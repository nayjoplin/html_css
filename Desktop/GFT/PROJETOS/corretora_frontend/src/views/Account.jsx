import React from 'react'
import useLocalStorage from '../hooks/useLocalStorage'
export default function Account(){
  const [me,setMe]=useLocalStorage('me',{nome:'',email:''})
  return (<div className="card"><h2>Minha Conta</h2>
    <div className="row"><fieldset style={{flex:1}}><label>Nome</label><input className="input" value={me.nome} onChange={e=>setMe({...me,nome:e.target.value})}/></fieldset>
    <fieldset style={{flex:1}}><label>E-mail</label><input className="input" value={me.email} onChange={e=>setMe({...me,email:e.target.value})}/></fieldset></div>
    <div className="small" style={{marginTop:8}}>Dados salvos localmente (localStorage).</div></div>)
}