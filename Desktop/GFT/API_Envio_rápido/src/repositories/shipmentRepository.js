const Shipment = require('../models/Shipment');

const shipmentRepository = {
  async create(data) {
    const shipment = await Shipment.create(data);
    return shipment.toObject();
  },

  async findById(id) {
    return Shipment.findById(id).lean();
  },

  async updateById(id, data) {
    return Shipment.findByIdAndUpdate(id, data, { new: true }).lean();
  },

  async deleteById(id) {
    return Shipment.findByIdAndDelete(id).lean();
  }
};

module.exports = { shipmentRepository };
