import http from 'k6/http';
import { sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:3000';

export const options = {
  stages: [
    { duration: '10s', target: 10 },
    { duration: '10s', target: 30 },
    { duration: '10s', target: 60 },
    { duration: '10s', target: 100 },
    { duration: '10s', target: 0 },
  ],
};

export default function () {
  http.get(`${BASE_URL}/sumar?a=10&b=20`);
  sleep(1);
}
