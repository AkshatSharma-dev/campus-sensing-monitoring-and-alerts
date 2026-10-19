// Simple Node.js-free? This script uses JavaScript only as a test client.
// Run with Node.js 18+ after starting the Spring Boot service.
const baseUrl = process.env.BASE_URL || "http://localhost:8080";

async function main() {
  const count = Number(process.argv[2] || 100);
  const batchSize = Math.min(Number(process.argv[3] || 100), 1000);
  let sent = 0;

  for (let offset = 0; offset < count; offset += batchSize) {
    const size = Math.min(batchSize, count - offset);
    const readings = Array.from({ length: size }, (_, i) => ({
      eventId: crypto.randomUUID(),
      sensorId: `library-floor-${(offset + i) % 10}`,
      type: "temperature",
      value: Number((20 + Math.random() * 20).toFixed(2)),
      timestamp: new Date().toISOString()
    }));

    const response = await fetch(`${baseUrl}/api/readings/batch`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ readings })
    });

    const body = await response.json();
    if (!response.ok) {
      console.error("Batch failed:", response.status, body);
      process.exitCode = 1;
      return;
    }
    sent += body.accepted || 0;
    console.log(`Batch response:`, body);
  }

  console.log(`Simulator complete. Accepted readings reported by API: ${sent}/${count}`);
}

main().catch(error => {
  console.error(error);
  process.exitCode = 1;
});
