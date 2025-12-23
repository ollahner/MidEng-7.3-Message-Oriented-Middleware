# Warehouse MOM – Kurz‑Doku (Kafka + Spring Boot)

Kurzbeschreibung: Eine kleine Beispiel‑App. Ein REST‑Producer schreibt Lagerdaten in ein Kafka‑Topic; ein Kafka‑Consumer protokolliert sie und liefert per REST einen Management‑Report.

## Wichtige Dateien
- `src/main/java/com/example/demo/MessagingApplication.java`
- `src/main/java/com/example/demo/MessageProducer.java`
- `src/main/java/com/example/demo/MessageConsumer.java`
- `src/main/resources/application.yml`

## Voraussetzungen
- Java 17+
- Docker (oder Docker Desktop auf Windows)
- Gradle Wrapper (`./gradlew` / `gradlew.bat`)

## Schnellstart: Kafka (Docker)
Führe diese Befehle aus:

```bash
docker pull apache/kafka:4.1.1
docker run -d --name kafka -p 9092:9092 apache/kafka:4.1.1
docker exec -it kafka /bin/bash
bin/kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092
```

Hinweis: Die App verwendet standardmäßig das Topic `warehouse-data`. Entweder mit `quickstart-events` testen oder zusätzlich `warehouse-data` anlegen.

## Anwendung starten
- Windows PowerShell:

  ```powershell
  ./gradlew.bat bootRun
  ```

- macOS/Linux:

  ```bash
  ./gradlew bootRun
  ```

## Konfiguration
- `spring.kafka.bootstrap-servers` steht in `src/main/resources/application.yml` auf `localhost:9092`.
- Wichtig: `@KafkaListener(... groupId = "central-office-group")` in `MessageConsumer` überschreibt `spring.kafka.consumer.group-id` aus der `application.yml`.

## API / Nutzung
### Nachricht senden (Producer)
- Request:

  `GET http://localhost:8080/warehouse/send?data=Hallo`

- Antwort: `SUCCESS` oder `No data provided`

### Management‑Report (Consumer)
- Request:

  `GET http://localhost:8080/management/report`

- Antwort: JSON‑Liste der empfangenen Nachrichten (In‑Memory)

### Beispiel mit curl
```bash
curl "http://localhost:8080/warehouse/send?data=Hallo%20Zentrale"
curl "http://localhost:8080/management/report"
```

## Logging & Speicherung
- Nachrichten werden in einer In‑Memory‑Liste gehalten (keine Persistenz).
- Logs erscheinen standardmäßig in der Konsole.
- Für File‑Logging optional `src/main/resources/logback-spring.xml` hinzufügen.

## Kurzantworten (verkürzt)
### 4 Eigenschaften von Message Oriented Middleware (MOM)
- Asynchronität
- Lose Kopplung
- Zuverlässigkeit (Persistenz möglich)
- Skalierbarkeit

### Transiente vs. synchrone Kommunikation
- Synchron: Sender wartet auf Antwort.
- Transient: Nachrichten sind nicht persistent; gehen bei Ausfall verloren.

### Funktionsweise einer JMS Queue
Point‑to‑Point — jede Nachricht wird genau einem Consumer zugestellt; Speicherung bis zur Bestätigung möglich.

### Funktionsweise eines JMS Topic
Publish/Subscribe — Nachricht wird an alle aktiven Abonnenten verteilt; mit „durable subscriptions“ auch später verfügbar.

### Lose gekoppeltes verteiltes System
Komponenten sind zeitlich und technologisch unabhängig; Kommunikation über einen Broker (z. B. Kafka) ohne direkten Kenntnisstand über Empfänger.

## Troubleshooting (kurz)
- Broker nicht erreichbar → Docker‑Container prüfen, Port 9092 sicherstellen.
- Keine Einträge im Report → Producer‑Request prüfen; Topic existiert/automatische Erstellung erlaubt.
- GroupId‑Konflikte → Die `@KafkaListener`‑Angabe hat Vorrang über `application.yml`.