/**
 * Smoke k6 — golpea TODAS las rutas de server/src/index.js (16 operaciones).
 * Requiere API + MySQL con usuarios seed (Admin / Rosisela, PIN 1234).
 *
 * k6 run k6/api-all-endpoints.js
 * k6 run -e BASE_URL=https://happyjump.sorbits.site k6/api-all-endpoints.js
 */
import http from "k6/http";
import { check } from "k6";

const BASE = __ENV.BASE_URL || "http://localhost:3000";
const ADMIN = __ENV.K6_ADMIN || "Admin";
const WORKER = __ENV.K6_WORKER || "Rosisela";
const PIN = __ENV.K6_PIN || "1234";

export const options = {
  vus: 1,
  iterations: 1,
  thresholds: { checks: ["rate>=0.9"] },
};

function hdrs(token) {
  const h = { "Content-Type": "application/json" };
  if (token) h.Authorization = `Bearer ${token}`;
  return { headers: h };
}

function login(nombre) {
  const res = http.post(
    `${BASE}/auth/login`,
    JSON.stringify({ nombre, pin: PIN }),
    hdrs()
  );
  const ok = check(res, {
    [`POST /auth/login (${nombre})`]: (r) => r.status === 200,
  });
  if (!ok) {
    let hint = res.body;
    try {
      const j = res.json();
      hint = j.error || hint;
    } catch (_) {}
    console.warn(
      `LOGIN FALLÓ ${nombre}: HTTP ${res.status} — ${hint}. ` +
        `Solución: npm run k6:prepare (en server/) o UPDATE usuarios SET active_token=NULL;`
    );
    return null;
  }
  const j = res.json();
  return { token: j.token, id: j.usuario?.id };
}

function futureIso(days) {
  const d = new Date();
  d.setDate(d.getDate() + days);
  const p = (n) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}`;
}

function uniqueHour() {
  const h = 8 + (Date.now() % 10);
  return `${String(h).padStart(2, "0")}:00:00`;
}

export default function () {
  const fecha = futureIso(45);
  const fecha2 = futureIso(46);
  const hora = uniqueHour();
  const hora2 = `${String(9 + (Date.now() % 8)).padStart(2, "0")}:30:00`;

  // --- Públicos (3) ---
  check(http.get(`${BASE}/health`), {
    "GET /health": (r) => r.status === 200,
  });
  check(http.get(`${BASE}/hello`), {
    "GET /hello": (r) => r.status === 200,
  });
  check(http.get(`${BASE}/sumar?a=4&b=5`), {
    "GET /sumar": (r) => r.status === 200 && r.body === "9",
  });

  const admin = login(ADMIN);
  if (!admin) return;

  // --- Cancha (admin) ---
  check(http.get(`${BASE}/reservas-cancha`, hdrs(admin.token)), {
    "GET /reservas-cancha": (r) => r.status === 200,
  });

  const canchaBody = JSON.stringify({
    nombreCliente: "Cliente k6",
    deporte: "Futbol",
    fecha,
    hora,
    montoTotal: 200,
    adelanto: 50,
  });
  const postCancha = http.post(
    `${BASE}/reservas-cancha`,
    canchaBody,
    hdrs(admin.token)
  );
  const canchaOk = check(postCancha, {
    "POST /reservas-cancha": (r) => r.status === 201 || r.status === 409,
  });
  let canchaId = null;
  if (postCancha.status === 201) {
    canchaId = postCancha.json().id;
  } else {
    const list = http.get(`${BASE}/reservas-cancha`, hdrs(admin.token)).json();
    const row = list.find((x) => x.fecha === fecha) || list[0];
    canchaId = row?.id;
  }

  if (canchaId) {
    check(
      http.put(
        `${BASE}/reservas-cancha/${canchaId}/cobrar-saldo`,
        "{}",
        hdrs(admin.token)
      ),
      {
        "PUT /reservas-cancha/:id/cobrar-saldo": (r) =>
          r.status === 200 || r.status === 400,
      }
    );
  }

  const postCancha2 = http.post(
    `${BASE}/reservas-cancha`,
    JSON.stringify({
      nombreCliente: "Cliente k6 cancel",
      deporte: "Voley",
      fecha: fecha2,
      hora: hora2,
      montoTotal: 100,
      adelanto: 0,
    }),
    hdrs(admin.token)
  );
  if (postCancha2.status === 201) {
    const id2 = postCancha2.json().id;
    check(
      http.put(
        `${BASE}/reservas-cancha/${id2}/cancelar`,
        JSON.stringify({ motivo: "Prueba k6 cancelación" }),
        hdrs(admin.token)
      ),
      { "PUT /reservas-cancha/:id/cancelar": (r) => r.status === 200 }
    );
  } else if (canchaId) {
    check(
      http.put(
        `${BASE}/reservas-cancha/${canchaId}/cancelar`,
        JSON.stringify({ motivo: "Prueba k6" }),
        hdrs(admin.token)
      ),
      {
        "PUT /reservas-cancha/:id/cancelar": (r) =>
          r.status === 200 || r.status === 400,
      }
    );
  }

  // --- Reportes admin (2) ---
  check(http.get(`${BASE}/reportes?periodo=semanal`, hdrs(admin.token)), {
    "GET /reportes": (r) => r.status === 200,
  });
  check(
    http.get(`${BASE}/reportes/cancelaciones?periodo=mensual`, hdrs(admin.token)),
    { "GET /reportes/cancelaciones": (r) => r.status === 200 }
  );

  // --- PIN (1) — validación sin cambiar PIN real ---
  check(
    http.put(
      `${BASE}/usuarios/${admin.id}/pin`,
      JSON.stringify({ pinNuevo: "12" }),
      hdrs(admin.token)
    ),
    {
      "PUT /usuarios/:id/pin": (r) => r.status === 400 || r.status === 200,
    }
  );

  check(http.get(`${BASE}/reservas-salones`, hdrs(admin.token)), {
    "GET /reservas-salones": (r) => r.status === 200,
  });

  check(http.post(`${BASE}/auth/logout`, "{}", hdrs(admin.token)), {
    "POST /auth/logout (admin)": (r) => r.status === 200,
  });

  const worker = login(WORKER);
  if (!worker) return;

  const salonRes = http.post(
    `${BASE}/reservas-salones`,
    JSON.stringify({
      nombreCliente: "Evento k6",
      tipoEvento: "Otro",
      zona: "Zona A",
      numeroNinos: 10,
      horaInicio: "14:00:00",
      horaFin: "16:00:00",
      precioTotal: 500,
      adelanto: 100,
      salon: "Salón Principal",
      fecha: futureIso(47),
    }),
    hdrs(worker.token)
  );
  check(salonRes, {
    "POST /reservas-salones": (r) => r.status === 201 || r.status === 409,
  });

  let salonId = null;
  if (salonRes.status === 201) salonId = salonRes.json().id;
  else {
    const sl = http.get(`${BASE}/reservas-salones`, hdrs(worker.token)).json();
    salonId = sl[0]?.id;
  }

  if (salonId) {
    check(
      http.put(
        `${BASE}/reservas-salones/${salonId}/cobrar-saldo`,
        "{}",
        hdrs(worker.token)
      ),
      {
        "PUT /reservas-salones/:id/cobrar-saldo": (r) =>
          r.status === 200 || r.status === 400,
      }
    );
  }

  // Admin puede cancelar salón
  const admin2 = login(ADMIN);
  if (admin2 && salonId) {
    check(
      http.put(
        `${BASE}/reservas-salones/${salonId}/cancelar`,
        JSON.stringify({ motivo: "k6 admin cancel" }),
        hdrs(admin2.token)
      ),
      {
        "PUT /reservas-salones/:id/cancelar (admin)": (r) =>
          r.status === 200 || r.status === 400,
      }
    );
    check(http.post(`${BASE}/auth/logout`, "{}", hdrs(admin2.token)), {
      "POST /auth/logout": (r) => r.status === 200,
    });
  } else {
    check(http.post(`${BASE}/auth/logout`, "{}", hdrs(worker.token)), {
      "POST /auth/logout": (r) => r.status === 200,
    });
  }
}
