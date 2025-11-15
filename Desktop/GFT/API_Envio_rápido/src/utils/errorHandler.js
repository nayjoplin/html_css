function errorHandler(err, req, res, next) {
  console.error(err);
  const status = err.status || 500;
  const mensagem = err.message || 'Erro interno no servidor';
  res.status(status).json({ mensagem });
}

function asyncHandler(fn) {
  return (req, res, next) => {
    Promise.resolve(fn(req, res, next)).catch(next);
  };
}

module.exports = { errorHandler, asyncHandler };
