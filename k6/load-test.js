import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:3000';

export const options = {
  vus: 1,
  duration: '5s',
};

export default function () {
  const res = http.get(`${BASE_URL}/hello`);
  check(res, {
    'status es 200': (r) => r.status === 200,
    'respuesta correcta': (r) => r.body.includes('Good Morning'),
  });
}
