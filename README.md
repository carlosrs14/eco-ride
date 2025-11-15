# ECO-RIDE LATAM — Carpooling corporativo
### Proyecto para la materia microservicios

**Resumen**

ECO-RIDE LATAM es una plataforma de carpooling corporativo diseñada para coordinar viajes entre empleados de una misma zona empresarial, reducir costos de transporte, congestión y huella de carbono. Arquitectura basada en 4 microservicios con Spring Boot, Gateway, Keycloak para identidad, y un patrón Saga (event-driven) para garantizar consistencia entre reserva y pago.


## 1. Descripción rápida

- Conductores (empleados) publican viajes con asientos disponibles.
- Pasajeros buscan y reservan asientos.
- El flujo de reserva implica autorización/captura de pago; ante falla se ejecuta compensación automática (cancelación de reserva) mediante una Saga basada en eventos.
- Notificaciones (email/SMS/push) en eventos clave (confirmación, cancelación, recordatorios).

## 2. Arquitectura y componentes

**Microservicios de negocio**

- `trip-service`: CRUD de viajes y gestión de asientos. Publica/consume eventos: `ReservationRequested`, `ReservationConfirmed`, `ReservationCancelled`.
- `passenger-service`: Gestión de perfiles, driver profiles y ratings. Emite `PassengerRated`, consume `TripCompleted`.
- `payment-service`: Manejo de `PaymentIntent`, `Charge`, `Refund`. Responsable de autorizar/capturar y compensa cuando falla.
- `notification-service`: Envía emails/SMS/push a partir de eventos. Modelo outbox opcional para fiabilidad.

**Infra**

- `api-gateway` (Spring Cloud Gateway): predicados, filtros nativos y al menos un filtro custom.
- `config-server`: Config centralizada para perfiles dev/prod.
- `keycloak`: Identity provider (realm `ecoride`, clients `eco-gateway` y `eco-internal`).
- `mensajería`: RabbitMQ.
- `db`: PostgreSQL por servicio.

## 3. Servicios y contratos

### TripService

- `POST /trips` — Crear viaje (ROLE_DRIVER)
- `GET /trips?origin=&destination=&from=&to=` — Listar viajes (ROLE_PASSENGER|DRIVER)
- `POST /trips/{tripId}/reservations` — Reservar asiento (ROLE_PASSENGER) → devuelve `{reservationId}`
- `GET /reservations/{id}` — Obtener reserva

### PassengerService

- `GET /me` — Perfil del sub en token
- `POST /drivers/profile` — Crear/actualizar driver profile
- `POST /ratings` — Publicar rating (post-trip)

### PaymentService

- `POST /payments/intent` — {reservationId, amount} (client_credentials)
- `POST /payments/capture/{intentId}` — Captura (internal)
- `POST /payments/refund/{chargeId}` — Reembolso (internal)

### NotificationService

- `POST /notify` — {templateCode, to, params}


## 4. Eventos y patrón Saga

**Eventos principales**

- `ReservationRequested { reservationId, tripId, passengerId, amount }`
- `PaymentAuthorized { reservationId, paymentIntentId, chargeId }`
- `PaymentFailed { reservationId, reason }`
- `ReservationConfirmed { reservationId }`
- `ReservationCancelled { reservationId, reason }`
- `TripCompleted { tripId }`

## 5. Quickstart local

Como ejecutar:
```bash
git clone https://github.com/carlosrs14/eco-ride.git

cd eco-ride

docker compose up
```
- Los microservicios se levantan con este comando.
- Cada servicio usa Flyway: las migraciones se ejecutan al arrancar.

## 6. Contribuir

1. Crear una rama `feat/<descripción>`.
2. Incluir migraciones DB nuevas en `db/migrations` del servicio correspondiente.
3. Abrir PR hacia `dev` con descripción clara y pruebas.
