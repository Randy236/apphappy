/**
 * API mínima para pruebas k6 cuando MySQL no está disponible.
 * Mismos endpoints que server/src/index.js (/hello, /sumar).
 */
import express from "express";

const PORT = Number(process.env.PORT || 3000);
const app = express();

app.get("/hello", (req, res) => {
  res.type("text").send("Good Morning");
});

app.get("/sumar", (req, res) => {
  const a = Number(req.query.a);
  const b = Number(req.query.b);
  if (Number.isNaN(a) || Number.isNaN(b)) {
    return res.status(400).type("text").send("error");
  }
  res.type("text").send(String(a + b));
});

app.get("/health", (req, res) => {
  res.json({ ok: true, mode: "k6-demo" });
});

app.listen(PORT, "0.0.0.0", () => {
  console.log(`k6 demo API http://127.0.0.1:${PORT}`);
});
