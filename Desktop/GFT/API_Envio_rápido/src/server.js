require('dotenv').config();
const express = require('express');
const swaggerUi = require('swagger-ui-express');
const YAML = require('yamljs');
const { connectDB } = require('./shared/db');
const envioRoutes = require('./routes/envioRoutes');
const { errorHandler } = require('./utils/errorHandler');

const swaggerDocument = YAML.load(__dirname + '/../swagger.yaml');

const app = express();
app.use(express.json());

app.use('/api/envios', envioRoutes);
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocument));

app.use(errorHandler);

if (process.env.NODE_ENV !== 'test') {
  const port = process.env.PORT || 3000;
  connectDB().then(() => {
    app.listen(port, () => console.log(`API rodando na porta ${port}`));
  }).catch(err => {
    console.error('Erro ao conectar no banco:', err);
    process.exit(1);
  });
}

module.exports = { app };
