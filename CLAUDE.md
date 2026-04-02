# QueueTable — Contexto del Proyecto

## Qué es
SaaS B2B para restaurantes que unifica cola online, reservas y gestión de mesas en tiempo real. El cliente escanea un QR, entra en la cola desde el móvil. El restaurante gestiona todo desde un panel.

## Estado actual
- **Sprint 1**: COMPLETADO (commit `7036a74` en `main`)
- **Sprint 2**: Pendiente — Cola del cliente (página pública, QR flow, tracking)
- **PRD completo**: `PRD.md` (fuente de verdad de requisitos y arquitectura)
- **Análisis original archivado**: `queue_table_original_brief.md`

## Stack confirmado
- **Backend**: Spring Boot 3.4.4 / Java 21 / Maven
- **Frontend**: React / Next.js (no iniciado)
- **DB**: PostgreSQL via Supabase (managed). Dev local: Docker Compose en puerto 5433
- **Auth**: JWT + refresh tokens (jjwt 0.12.6)
- **Realtime**: WebSockets STOMP + SockJS (staff), SSE/polling (cliente)
- **QR**: ZXing 3.5.3
- **Tests**: JUnit 5 + Testcontainers + MockMvc (22 tests pasando)
- **Migrations**: Flyway (V1-V4)

## Arquitectura backend
Screaming + Hexagonal en `queuetable-backend/`:
```
com.queuetable/
├── auth/          (register, login, refresh — JWT)
├── restaurant/    (CRUD + QR code)
├── staff/         (entity + repository)
├── table/         (CRUD + state transitions: FREE→OCCUPIED→CLEANING→FREE)
├── config/        (RestaurantConfig — timeouts, durations)
└── shared/        (security, exceptions, audit)
```

## Endpoints implementados (Sprint 1)
- `POST /auth/register`, `/auth/login`, `/auth/refresh`
- `GET/PATCH /restaurants/{id}`, `GET /restaurants/{id}/qr`
- `GET/PATCH /restaurants/{id}/config`
- `GET/POST /restaurants/{id}/tables`, `PATCH/DELETE /tables/{id}`, `PATCH /tables/{id}/status`

## Decisiones clave
- MVP = 1 cuenta = 1 restaurante (no multi-tenant)
- Cliente sin cuenta (token UUID anónimo en localStorage)
- Roles ADMIN/STAFF con mismos permisos en MVP
- Sin entidad Visit en MVP (implícita en estados de mesa)
- Sin assignment-engine (staff asigna manualmente con filtros simples)
- Notificaciones solo visuales en web (no SMS/WhatsApp/push)
- Optimistic locking en entidades con estado mutable (version field)
- Rate limiting en endpoints públicos

## Roadmap (7 sprints)
1. ~~Fundación~~ ✅ (auth, restaurant, tables, config, QR, DB)
2. Cola del cliente (página pública, QR flow, tracking, panel básico)
3. Reservas + WebSocket (CRUD reservas, STOMP para staff)
4. Mesas + asignación (gestión completa, walk-in, SSE cliente)
5. Lógica combinada (protección reservas, prioridad, notificaciones)
6. Robustez (cron expiraciones, optimistic locking frontend, rate limiting)
7. Pulido y lanzamiento (UX, E2E tests, deploy pipeline)

## Repos
- Backend: `queuetable-backend/` → https://github.com/DanielRuiz-14/gestorColas.git
- Frontend: Pendiente (repo separado)

## Cómo correr
```bash
cd queuetable-backend
docker compose up -d          # PostgreSQL en puerto 5433
mvn spring-boot:run           # Backend en puerto 8080
mvn test                      # 22 integration tests
```
