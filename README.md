# Eazy Bank Microservices

This repository contains the Eazy Bank microservices project. The local CQRS learning setup uses Docker Compose to run Kafka, Kafka UI, MongoDB, and Mongo Express. The Java services are run separately from the IDE or Maven.

## Prerequisites

- Docker Desktop (or another Docker Engine with the Docker Compose plugin) installed and running.
- Internet access the first time you start the setup so Docker can download the container images.
- The ports listed below must be available on your laptop.

You do not need to install Kafka or MongoDB directly on your laptop.

## Start local infrastructure

From the repository root, run:

```bash
docker compose up -d
docker compose ps
```

The first command downloads and starts the containers in the background. Check the status with the second command. Kafka can take a few seconds to become ready; wait for it to finish starting before launching Java services that depend on it.

## Local URLs and connection addresses

| Tool | Address |
| --- | --- |
| Kafka UI | <http://localhost:8090> |
| Mongo Express | <http://localhost:8084> |
| Kafka from Java applications running on your laptop | `localhost:29092` |
| Kafka from another container on the Compose network | `kafka:9092` |
| MongoDB from Java applications running on your laptop | `localhost:27017` |
| MongoDB from another container on the Compose network | `mongodb:27017` |

The Kafka listeners use different addresses because a Java app on your laptop and a container inside Compose reach Kafka through different network paths.

## Stop or reset local infrastructure

Stop the containers and keep their saved data:

```bash
docker compose down
```

Start them again later with `docker compose up -d`.

To stop the containers and delete the saved Kafka and MongoDB data as well:

```bash
docker compose down -v
```

Use `down -v` only when you want a clean reset. These containers are configured for local development and classroom use, not production or exposure to an untrusted network.


### Kafka Payload
```bash
{
"eventId": "d970e787-20b1-48ab-8646-884be305e513",
"source": "CUSTOMER",
"changeType": "UPDATED",
"mobileNumber": "1234567890",
"revision": 2,
"occurredAt": "2026-10-23T09:30:00Z",
"payload": {
"customerId": "customer-123",
"name": "Sam Example",
"email": "sam@example.com",
"mobileNumber": "1234567890",
"activeSw": true
}
}
```
