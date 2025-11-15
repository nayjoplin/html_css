const { shipmentService } = require('../services/shipmentService');
const { asyncHandler } = require('../utils/errorHandler');

const create = asyncHandler(async (req, res) => {
  const resultado = await shipmentService.createShipment(req.body);
  res.status(201).json(resultado);
});

const getById = asyncHandler(async (req, res) => {
  const envio = await shipmentService.getShipmentById(req.params.id);
  res.json(envio);
});

const update = asyncHandler(async (req, res) => {
  const atualizado = await shipmentService.updateShipment(req.params.id, req.body);
  res.json(atualizado);
});

const updateAddress = asyncHandler(async (req, res) => {
  const { endereco } = req.body;
  const atualizado = await shipmentService.updateShipmentAddress(req.params.id, endereco);
  res.json(atualizado);
});

const remove = asyncHandler(async (req, res) => {
  await shipmentService.deleteShipment(req.params.id);
  res.status(204).send();
});

module.exports = {
  create,
  getById,
  update,
  updateAddress,
  remove
};
