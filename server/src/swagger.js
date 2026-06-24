import { readFileSync } from "fs";
import { dirname, join } from "path";
import { fileURLToPath } from "url";
import swaggerUi from "swagger-ui-express";

const __dirname = dirname(fileURLToPath(import.meta.url));
const specPath = join(__dirname, "..", "openapi.json");

function publicBaseUrl(port) {
  const render = process.env.RENDER_EXTERNAL_URL;
  if (render) return render.replace(/\/$/, "");
  const railway = process.env.RAILWAY_PUBLIC_DOMAIN;
  if (railway) return `https://${railway}`;
  return `http://localhost:${port}`;
}

/** Monta Swagger UI (estilo Spring) en /swagger-ui */
export function setupSwagger(app, port) {
  const spec = JSON.parse(readFileSync(specPath, "utf8"));
  const baseUrl = publicBaseUrl(port);
  const isCloud = Boolean(process.env.RENDER_EXTERNAL_URL || process.env.RAILWAY_PUBLIC_DOMAIN);
  spec.servers = [
    {
      url: baseUrl,
      description: isCloud ? "Happy Jump API (cloud)" : "Servidor local Happy Jump",
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
