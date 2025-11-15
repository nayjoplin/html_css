const express = require('express');
const { create, getById, update, updateAddress, remove } = require('../controllers/shipmentController');
const { calcularFreteMock } = require('../controllers/freteMockController');
const { authMiddleware } = require('../middlewares/authMiddleware');

const router = express.Router();

router.post('/', authMiddleware, create);
router.get('/:id', authMiddleware, getById);
router.put('/:id', authMiddleware, update);
router.patch('/:id/endereco', authMiddleware, updateAddress);
router.delete('/:id', authMiddleware, remove);

router.post('/frete-mock', calcularFreteMock);

module.exports = router;
