# Prices API

Servicio Spring Boot que devuelve el precio aplicable a un producto de una marca en una fecha dada.

## Arquitectura

Arquitectura hexagonal (puertos y adaptadores) con DDD:

```
com.company.prices
├── domain
│   ├── model
│   │   └── Price (record inmutable; reglas de vigencia y precedencia)
│   └── exception
│       └── PriceNotFoundException
├── application
│   ├── port.input
│   │   └── GetApplicablePriceUseCase
│   ├── port.output
│   │   └── LoadPricesPort
│   └── service
│       └── GetApplicablePriceService
└── infrastructure
    ├── adapter.input.rest
    │   ├── PriceController
    │   ├── GlobalExceptionHandler
    │   ├── dto
    │   └── mapper
    ├── adapter.output.persistence
    │   ├── PriceJpaEntity
    │   ├── SpringDataPriceRepository
    │   ├── PricePersistenceAdapter
    │   └── PricePersistenceMapper
    └── configuration
        ├── BeanConfiguration
        └── OpenApiConfiguration
```

`HexagonalArchitectureTest` (ArchUnit) verifica que dominio y aplicacion no dependen de Spring ni de infraestructura.

## Stack

Java 21, Spring Boot 3.5, Spring Web, Spring Data JPA, H2, Bean Validation, Actuator,
springdoc-openapi, JUnit 5, Mockito, MockMvc, ArchUnit, JaCoCo, SLF4J/Logback.

## Reglas de negocio

- Un precio aplica si `START_DATE <= applicationDate <= END_DATE` (limites inclusivos).
- Si varios precios aplican, se elige el de mayor `PRIORITY`.
- Desempate (misma prioridad): gana el de mayor `PRICE_LIST` (determinista).
- Sin precio aplicable: HTTP 404.

## Base de datos

H2 en memoria, inicializada al arrancar con `schema.sql` y `data.sql` (4 registros del enunciado).
Consola H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:pricesdb`, usuario `sa`, sin contrasena).

## Ejecucion

```
mvn spring-boot:run
```

## Tests

```
mvn clean verify
```

Informe de cobertura JaCoCo: `target/site/jacoco/index.html`.

## API

`GET /api/v1/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1`

Respuesta 200:

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 2,
  "startDate": "2020-06-14T15:00:00",
  "endDate": "2020-06-14T18:30:00",
  "price": 25.45,
  "currency": "EUR"
}
```

Los errores (400, 404, 405, 500) devuelven un JSON con el detalle del problema (`application/problem+json`):

```json
{
  "type": "about:blank",
  "title": "Price not found",
  "status": 404,
  "detail": "No applicable price found for productId=99999, brandId=1 at 2020-06-14T10:00",
  "instance": "/api/v1/prices",
  "timestamp": "2020-06-14T16:00:00Z"
}
```

## Documentacion y herramientas

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI: http://localhost:8080/v3/api-docs
- Health: http://localhost:8080/actuator/health
- Coleccion Postman: `postman/prices-api.postman_collection.json` (variable `baseUrl`).

