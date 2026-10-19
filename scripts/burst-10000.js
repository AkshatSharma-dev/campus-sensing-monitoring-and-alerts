
import http from 'k6/http';
import { check } from 'k6';
import { Counter } from 'k6/metrics';
import { randomUUID } from 'k6/crypto';

const acceptedReadings = new Counter('accepted_readings');

export const options = {
  scenarios: {
    oneSecondBurst: {
      executor: 'constant-arrival-rate',
      rate: 10,
      timeUnit: '1s',
      duration: '1s',
      preAllocatedVUs: 10,
      maxVUs: 100,
      gracefulStop: '2m',
    },
  },
};

export default function () {
  const readings = Array.from({ length: 1000 }, () => ({
    eventId: randomUUID(),
    sensorId: `sensor-${Math.floor(Math.random() * 100)}`,
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

  if (response && response.status === 200) {
    try {
      const body = response.json();
      accepted = Number(body.accepted || 0);
    } catch (error) {
      console.error(`Response JSON parsing failed: ${String(error)}`);
    }
  }

  acceptedReadings.add(accepted);

  check(response, {
    'batch returned HTTP 200': r => r.status === 200,
    'batch accepted 1000 readings': () => accepted === 1000,
  });

  if (accepted !== 1000) {
    console.error(
      `Status=${response.status}; accepted=${accepted}; ` +
      `body=${String(response.body).slice(0, 500)}`
    );
  }
}
