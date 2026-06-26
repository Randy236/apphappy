/**
 * Pruebas de rendimiento Happy Jump API — escenarios smoke, carga y estrés.
 * k6 run k6/performance-api.js
 * k6 run --summary-export k6/results/performance-summary.json k6/performance-api.js
 */
import http from "k6/http";
import { check, sleep } from "k6";

const BASE = __ENV.BASE_URL || "http://localhost:3000";
const ADMIN = __ENV.K6_ADMIN || "Admin";
const PIN = __ENV.K6_PIN || "1234";

export const options = {
  scenarios: {
    smoke: {
      executor: "constant-vus",
      vus: 1,
      duration: "15s",
      exec: "smoke",
      startTime: "0s",
    },
    load: {
      executor: "ramping-vus",
      startVUs: 0,
      stages: [
        { duration: "15s", target: 8 },
        { duration: "30s", target: 15 },
        { duration: "15s", target: 0 },
      ],
      exec: "loadFlow",
      startTime: "18s",
    },
    stress: {
      executor: "ramping-vus",
      startVUs: 0,
      stages: [
        { duration: "15s", target: 15 },
        { duration: "15s", target: 25 },
        { duration: "15s", target: 0 },
      ],
      exec: "stressHealth",
      startTime: "2m",
    },
  },
  thresholds: {
    http_req_failed: ["rate<0.05"],
    http_req_duration: ["p(95)<800"],
    checks: ["rate>=0.90"],
  },
};

function hdrs(token) {
  const h = { "Content-Type": "application/json" };
  if (token) h.Authorization = `Bearer ${token}`;
  return { headers: h };
}

function login() {
  const res = http.post(
    `${BASE}/auth/login`,
    JSON.stringify({ nombre: ADMIN, pin: PIN }),
    hdrs()
  );
  if (res.status !== 200) return null;
  return res.json().token;
}

export function smoke() {
  check(http.get(`${BASE}/health`), {
    "smoke GET /health": (r) => r.status === 200,
  });
  const token = login();
  if (token) {
    check(http.get(`${BASE}/reservas-cancha`, hdrs(token)), {
      "smoke GET /reservas-cancha": (r) => r.status === 200,
    });
    check(http.get(`${BASE}/reportes?periodo=semanal`, hdrs(token)), {
      "smoke GET /reportes": (r) => r.status === 200,
    });
    http.post(`${BASE}/auth/logout`, "{}", hdrs(token));
  }
  sleep(1);
}

export function loadFlow() {
  check(http.get(`${BASE}/health`), {
    "load GET /health": (r) => r.status === 200,
  });
  const token = login();
  if (!token) return;
  check(http.get(`${BASE}/reservas-cancha`, hdrs(token)), {
    "load GET /reservas-cancha": (r) => r.status === 200,
  });
  check(http.get(`${BASE}/reservas-salones`, hdrs(token)), {
    "load GET /reservas-salones": (r) => r.status === 200,
  });
  check(http.get(`${BASE}/reportes?periodo=mensual`, hdrs(token)), {
    "load GET /reportes": (r) => r.status === 200,
  });
  http.post(`${BASE}/auth/logout`, "{}", hdrs(token));
  sleep(0.5);
}

export function stressHealth() {
  check(http.get(`${BASE}/health`), {
    "stress GET /health": (r) => r.status === 200,
  });
  check(http.get(`${BASE}/sumar?a=2&b=3`), {
    "stress GET /sumar": (r) => r.status === 200 && r.body === "5",
  });
  sleep(0.2);
}
