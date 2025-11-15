// Middleware simples apenas para simular autenticação.
// No desafio não há regra específica, então deixamos aberto.

function authMiddleware(req, res, next) {
  // Poderia checar Authorization aqui.
  next();
}

module.exports = { authMiddleware };
