import { readFileSync } from "fs";
import { dirname, join } from "path";
import { fileURLToPath } from "url";
import swaggerUi from "swagger-ui-express";

const __dirname = dirname(fileURLToPath(import.meta.url));
const specPath = join(__dirname, "..", "openapi.json");

/** Monta Swagger UI (estilo Spring) en /swagger-ui */
export function setupSwagger(app, port) {
  const spec = JSON.parse(readFileSync(specPath, "utf8"));
  spec.servers = [
    {
      url: `http://localhost:${port}`,
      description: "Servidor local Happy Jump",
    },
  ];

  app.get("/openapi.json", (_req, res) => {
    res.json(spec);
  });

  app.use(
    "/swagger-ui",
    swaggerUi.serve,
    swaggerUi.setup(spec, {
      customSiteTitle: "Happy Jump API — Swagger",
      swaggerOptions: {
        persistAuthorization: true,
      },
    })
  );

  app.get("/swagger-ui/index.html", (_req, res) => {
    res.redirect("/swagger-ui/");
  });
}
