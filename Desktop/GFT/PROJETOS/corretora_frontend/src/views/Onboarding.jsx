import React from 'react'
import useLocalStorage from '../hooks/useLocalStorage'
export default function Onboarding(){
  const [me,setMe]=useLocalStorage('me',{nome:'',email:''})
  const done=!!(me?.nome&&me?.email)
  return (<div className="card">{!done?(<><h2>Bem-vindo! 👋</h2><p>Preencha seus dados para entrar.</p>
    <div className="row"><fieldset style={{flex:1}}><label>Nome</label><input className="input" value={me.nome} onChange={e=>setMe({...me,nome:e.target.value})}/></fieldset>
    <fieldset style={{flex:1}}><label>E-mail</label><input className="input" value={me.email} onChange={e=>setMe({...me,email:e.target.value})}/></fieldset></div>
    <button className="btn success" onClick={()=>{ if(me.nome&&me.email) alert('Bem-vindo, '+me.nome+'!') }}>Entrar</button></>):(<>
    <h2>Pronto! ✅</h2><p>Você já está logado como <strong>{me.nome}</strong> ({me.email}).</p></>)}</div>)
}