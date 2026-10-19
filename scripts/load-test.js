
import http from 'k6/http';
import { check } from 'k6';
import { Counter } from 'k6/metrics';
import { randomUUID } from 'k6/crypto';

const acceptedReadings = new Counter('accepted_readings');

export const options = {
  scenarios: {
    singleBatch: {
      executor: 'shared-iterations',
      vus: 1,
      iterations: 1,
      maxDuration: '120s',
    },
  },
};

export default function () {
  const readings = Array.from({ length: 10000 }, (_, i) => ({
    eventId: crypto.randomUUID(),
    sensorId: `sensor-${i % 100}`,
    type: 'temperature',
    value: Number((20 + Math.random() * 20).toFixed(2)),
    timestamp: new Date().toISOString(),
  }));

  const response = http.post(
    'http://localhost:8080/api/readings/batch',
    JSON.stringify({ readings }),
    {
      headers: { 'Content-Type': 'application/json' },
      timeout: '110s',
    }
  );

  let accepted = 0;

  const success = check(response, {
    'batch request returned HTTP 200': r => r.status === 200,
    'server accepted all 10000 readings': r => {
      if (r.status !== 200) return false;

      try {
        const body = r.json();
        accepted = body.accepted || 0;
        return accepted === 10000;
      } catch (_) {
        return false;
      }
    },
  });

  acceptedReadings.add(accepted);

  if (!success) {
    console.error(
      `Batch failed or was partially accepted. Status=${response.status}, ` +
      `Body=${String(response.body).slice(0, 1000)}`
    );
  }
}
