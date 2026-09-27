# FinTrack API

## Technology stack

- Java 17 source compatibility (tested with Java 21)
- Spring Boot 4.1, Spring MVC, Spring Security, Spring Data JPA, Jakarta Validation
- H2 in-memory database for local assessment runs; Maven Wrapper

FinTrack contains an owner-scoped transaction API and a shared-expense API. Authenticated identities are represented by the Spring Security principal name. Authentication/user provisioning is a deployment boundary: Spring Boot's generated development user is not an account system and must not be used as production identity management.

## Run locally

```powershell
.\mvnw.cmd spring-boot:run
```

Spring Security prints a randomly generated development password on startup. Supply credentials using `SPRING_SECURITY_USER_NAME` and `SPRING_SECURITY_USER_PASSWORD` for a stable local account. Do not use development credentials in a deployed environment. The H2 database is in-memory and is recreated when the process stops.

## API

All routes require HTTP Basic authentication in this assessment configuration. Amounts are decimal strings/numbers with at most two fractional digits.

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/api/transactions` | Create an owned transaction (`description`, `amount`) |
| `GET` | `/api/transactions` | List only the authenticated user's transactions |
| `DELETE` | `/api/transactions/{id}` | Delete an owned transaction; inaccessible IDs return 404 |
| `DELETE` | `/api/transactions` | Delete only the authenticated user's transactions |
| `POST` | `/api/shared-expenses` | Create an expense with `description`, `totalAmount`, `splitType` (`EQUAL`/`CUSTOM`), and `participants` |
| `GET` | `/api/balances` | List the caller's net pending amount against each counterparty |

The authenticated creator must appear in the participant list. Expenses require at least two distinct participants. Equal splits distribute leftover cents in request order. Custom splits require positive participant amounts that sum exactly to the total. `netAmount` is positive when the caller owes the counterparty and negative when the counterparty owes the caller; zero-net counterparties are omitted.

Example expense request:

```json
{
  "description": "Dinner",
  "totalAmount": 120.00,
  "splitType": "EQUAL",
  "participants": [
    { "userId": "alice" },
    { "userId": "bob" },
    { "userId": "carol" }
  ]
}
```

## Verify

```powershell
.\mvnw.cmd test
```

See [ARCHITECTURE.md](ARCHITECTURE.md) for the architecture and request-flow diagram, [TEST_CASES.md](TEST_CASES.md) for test documentation, [REVIEW.md](REVIEW.md) for transaction findings, and the other assessment artifacts for implementation notes.
