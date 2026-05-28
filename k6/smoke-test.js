import http from 'k6/http';
import { sleep, check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:3000';

export const options = {
  vus: 20,
  duration: '20s',
};

export default function () {
  const res = http.get(`${BASE_URL}/sumar?a=5&b=3`);
  check(res, {
    'status 200': (r) => r.status === 200,
    'resultado correcto': (r) => r.body === '8',
  });
  sleep(1);
}
