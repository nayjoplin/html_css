const { shipmentRepository } = require('../repositories/shipmentRepository');
const { consultarCep } = require('../adaptors/viaCepAdaptor');
const { calcularFrete } = require('../adaptors/freteAdaptor');
const { publicarEnvioCriado } = require('../messaging/queueProducer');

function validarCamposBasicos(payload) {
  const obrigatorios = [
    'nomeRemetente',
    'endereco',
    'cepOrigem',
    'cepDestino',
    'alturaCaixa',
    'larguraCaixa',
    'comprimentoCaixa'
  ];

  for (const campo of obrigatorios) {
    if (payload[campo] == null || payload[campo] === '') {
      const err = new Error(`Campo obrigatório ausente: ${campo}`);
      err.status = 400;
      throw err;
    }
  }

  const medidas = ['alturaCaixa', 'larguraCaixa', 'comprimentoCaixa'];
  for (const m of medidas) {
    const v = Number(payload[m]);
    if (Number.isNaN(v) || v <= 0) {
      const err = new Error(`Valor inválido: ${m} deve ser número > 0`);
      err.status = 400;
      throw err;
    }
  }
}

async function createShipment(payload) {
  validarCamposBasicos(payload);

  const origem = await consultarCep(payload.cepOrigem);
  if (!origem) {
    const err = new Error('CEP de origem inválido.');
    err.status = 400;
    throw err;
  }

  const destino = await consultarCep(payload.cepDestino);
  if (!destino) {
    const err = new Error('CEP de destino inválido.');
    err.status = 400;
    throw err;
  }

  const frete = await calcularFrete(payload.cepOrigem, payload.cepDestino);

  const novoEnvio = await shipmentRepository.create({
    ...payload,
    valorFrete: frete.valorFrete,
    prazoEntrega: frete.prazoEntrega,
    observacaoFrete: frete.observacao
  });

  try {
    await publicarEnvioCriado(novoEnvio);
  } catch (e) {
    console.error('Falha ao publicar mensagem na fila:', e.message);
  }

  return {
    id: novoEnvio._id,
    nomeRemetente: novoEnvio.nomeRemetente,
    cepOrigem: novoEnvio.cepOrigem,
    cepDestino: novoEnvio.cepDestino,
    alturaCaixa: novoEnvio.alturaCaixa,
    larguraCaixa: novoEnvio.larguraCaixa,
    comprimentoCaixa: novoEnvio.comprimentoCaixa,
    valorFrete: novoEnvio.valorFrete,
    prazoEntrega: novoEnvio.prazoEntrega,
    mensagem: 'Cálculo de frete realizado com sucesso'
  };
}

async function getShipmentById(id) {
  const envio = await shipmentRepository.findById(id);
  if (!envio) {
    const err = new Error('Envio não encontrado');
    err.status = 404;
    throw err;
  }
  return envio;
}

async function updateShipment(id, payload) {
  const envio = await shipmentRepository.updateById(id, payload);
  if (!envio) {
    const err = new Error('Envio não encontrado');
    err.status = 404;
    throw err;
  }
  return envio;
}

async function updateShipmentAddress(id, novoEndereco) {
  if (!novoEndereco || novoEndereco.trim() === '') {
    const err = new Error('Endereço não pode ser vazio');
    err.status = 400;
    throw err;
  }

  const envio = await shipmentRepository.updateById(id, { endereco: novoEndereco });
  if (!envio) {
    const err = new Error('Envio não encontrado');
    err.status = 404;
    throw err;
  }
  return envio;
}

async function deleteShipment(id) {
  const envio = await shipmentRepository.deleteById(id);
  if (!envio) {
    const err = new Error('Envio não encontrado');
    err.status = 404;
    throw err;
  }
  return;
}

module.exports = {
  shipmentService: {
    createShipment,
    getShipmentById,
    updateShipment,
    updateShipmentAddress,
    deleteShipment
  }
};
