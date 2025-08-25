# libs-async-coms

> Asynchronous Multi-Step Process Orchestration for Micronaut & Java

![Java](https://img.shields.io/badge/Java-17%2B-blue)
![Micronaut](https://img.shields.io/badge/Micronaut-4.x-orange)
![Reactive](https://img.shields.io/badge/Reactive-Yes-success)
![Status](https://img.shields.io/badge/Status-Active-green)

---

## Overview

**libs-async-coms** is a Java library designed to orchestrate **multi-step asynchronous processes** across distributed systems. It’s **protocol-agnostic**, **reactive**, and **scalable**, allowing you to start, control, and monitor long-running processes using **REST**, **MQ**, **WebSockets**, **gRPC**, or any other transport.

This repository is a **GitHub mirror**. The code is **maintained** in our **private GitLab repository**.

---

## Prerequisites

- **Java 17+**
- **Micronaut 4.x**
- **Reactive libraries** (Project Reactor)
- **RabbitMQ**: Required for multi-step process orchestration and notifications.

---

## Features

### 1. Multi-Step Process Orchestration

* Define **complex workflows** split into multiple ordered steps.
* Each step is **asynchronous** and can run on **different nodes**.
* Built-in retry and recovery strategies.

### 2. Protocol-Agnostic Triggering

* Start processes from **REST**, **RabbitMQ**, **Kafka**, **gRPC**, or **WebSockets**.
* Unified API through the `MultiStepProcessAdapter`.

### 3. Real-Time Notifications

* Automatic event notifications to an **orchestrator service**.
* Clients can receive live updates via **WebSockets**, **SSE**, or **gRPC streams**.
* Configurable notification queues.

### 4. Reactive & Scalable

* Built on **Project Reactor** for non-blocking operations.
* Designed for **high concurrency** and **horizontal scaling**.

### 5. Configurable via YAML

```yaml
rabbitmq:
  addresses:
    - xxxxxxx:xxxx
  username: xxxx
  password: xxxx

lda:
  steps:
    enabled: true
    queue: svc-ai-msp
  notification:
    enabled: true
    queue: svc-ai-notif
```

---

## Architecture

```
          ┌──────────────┐
          │   Client     │
          │ REST/gRPC    │
          └───────┬──────┘
                  │
         Ask Process / Subscribe Updates
                  │
                  ▼
      ┌─────────────────────┐
      │ Orchestrator Server │
      │     Start MSP       │
      │ (libs-async-coms)   │
      └───────┬─────────────┘
              │
              │ MQ / gRPC / REST
              ▼
      ┌─────────────────────┐
      │   (service server)  │
      │ Worker Nodes        │
      │ Execute Steps       │
      └───────┬─────────────┘
              │ Notifications
              ▼
      ┌─────────────────────┐
      │ Orchestrator Server │
      │ Sends Updates to    │
      │ WebSocket Clients   │
      └─────────────────────┘
```

---

## Getting Started

### 1. Install the Library

**Maven**

need to build localy the library (not public accessible)

```xml
<dependency>
    <groupId>lda.services.libs</groupId>
    <artifactId>libs-async-coms</artifactId>
    <version>0.1.6</version>
</dependency>
```


### 2. Define a Multi-Step Process

```java
@Slf4j
@RequiredArgsConstructor
@MultiStepProcess(name = "UPSCALE_ASYNC")
public class UpscaleProcess extends AbstractMultiStepProcess<UpscaleProcessData> {

    private final NotificationSenderPort<NotificationIdentifierData> notifications;

    @Step(stepValue = 0)
    public UpscaleProcessData start(UpscaleProcessData data) {
        log.info("Starting upscale for {}", data.getUpscaleId());
        throw new MultiStepProcessStopException(data, "Upscale initiated");
    }

    @Step(stepValue = 1)
    public UpscaleProcessData finish(UpscaleProcessData data) {
        log.info("Upscale completed for {}", data.getUpscaleId());
        notifications.sendNotification("svc-ai-notif", "UPSCALE",
            NotificationIdentifierData.builder()
                .id(data.getUpscaleId())
                .finish(true)
                .build());
        throw new MultiStepProcessStopException(data, "Upscale finished");
    }
}
```

### 3. Launch a Process

```java
@Singleton
@RequiredArgsConstructor
public class AiAsyncAdapter extends MultiStepProcessAdapter<Object> implements AiAsync {

    private final MultiStepProcessClient processClient;

    @Override
    public void runAsyncProcess(Upscale upscale) {
        var data = UpscaleProcessData.builder()
            .upscaleId(upscale.getId())
            .retryCount(0)
            .build();

        this.runAsyncProcess(data, "UPSCALE_ASYNC");
    }
}
```

Call `runAsyncProcess()` from any protocol: REST, MQ, WebSocket, or gRPC.

### 4. Inject Data Mid-Process

```java
@Override
public void injectDataProcess(PicDataMessage picDataMessage) {
    var data = PicProcessData.builder()
        .idAi(picDataMessage.getSessionId())
        .data(picDataMessage.getData())
        .finish("END".equals(picDataMessage.getCmd()))
        .build();

    this.injectAsyncProcess(data, "PIC_ASYNC");
}
```

---

## Notifications

Automatic notification mechanism integrated with queues. Orchestrators can forward updates to clients in real time.

Example event:

```json
{
  "process": "UPSCALE_ASYNC",
  "id": "1234-5678",
  "status": "FINISHED",
  "data": {
    "duration": 4500,
    "result": "s3://bucket/output.png"
  }
}
```

---

## Advantages

* 🚀 High performance — Reactive and non-blocking
* 📡 Protocol-agnostic — REST, MQ, gRPC, WebSockets
* 🔔 Built-in notifications — Real-time updates
* 🧩 Scalable — Multi-node, multi-process ready
* 🛡️ Reliable — Automatic retries and recovery

---

## Notes

> This GitHub repository is **read-only**. Main development is on our private GitLab repository.
> This library requires a running **RabbitMQ** instance to manage asynchronous steps and notifications.

---

## License

Licensed under **Apache 2.0 License**. See [LICENSE](./LICENSE) file for details.
