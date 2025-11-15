const axios = require('axios');

async function consultarCep(cep) {
  if (!cep) return null;
  const limpo = String(cep).replace(/\D/g, '');
  if (limpo.length !== 8) return null;

  const url = `https://viacep.com.br/ws/${limpo}/json/`;
  const { data } = await axios.get(url);

  if (data.erro) return null;

  return {
    cep: data.cep,
    logradouro: data.logradouro || null,
    bairro: data.bairro || null,
    localidade: data.localidade || null,
    uf: data.uf || null
  };
}

module.exports = { consultarCep };
