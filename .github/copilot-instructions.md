# FinTrack Copilot Instructions

## Technology and architecture
- Use Java 17+ with Spring Boot, Spring MVC, Spring Security, Spring Data JPA, Jakarta Validation, and BigDecimal for monetary values.
- Keep feature code in `src/main/java/com/fintrack/fintrackapi/<feature>/` and tests in the matching `src/test/java` package.
- Separate controller/API DTOs, services (business rules), repositories (persistence), and JPA entities. Controllers must not contain persistence or balance logic.
- Use constructor injection and repository interfaces. Do not use raw JDBC, string-built SQL, or expose persistence entities as API responses.
- Put shared API exceptions and response mapping in `common`; keep feature-specific rules in their services.

## Coding conventions
- Use descriptive names, immutable request/response records where suitable, small methods, and explicit types.
- Validate incoming request shape with Jakarta Validation and enforce cross-field/business invariants in the service layer.
- Use `BigDecimal` with a two-decimal currency scale; never use `float` or `double` for money. Define how rounding remainders are assigned.
- Add Javadoc to public service/API methods and keep logs structured with parameterized SLF4J messages.
- Return specific HTTP statuses and stable error responses; never silently swallow an exception.

## Security and privacy
- Derive the acting user from the authenticated Spring Security principal; never accept an owner/creator ID from an untrusted request body.
- Scope every read, update, and delete by the authenticated owner. Return not-found for inaccessible object IDs to avoid leaking their existence.
- Require authentication for all API routes. Do not disable CSRF for browser-cookie sessions; this API uses stateless HTTP Basic for the assessment demo only.
- Never log descriptions, credentials, account data, or monetary details. Do not commit secrets or use insecure production defaults.
- Treat H2 and the generated development identity as local assessment infrastructure; production must use managed credentials, a production database, TLS, and an approved identity provider.

## Testing expectations
- Add focused unit tests for validation, ownership, monetary precision, rounding, and balance direction/netting.
- Include web/security integration coverage for authentication and API status codes when changing controller or security behavior.
- Test positive and negative paths, including duplicate participants, custom shares not totaling the expense, fewer than two participants, and cross-user access.
- Use the existing Maven test setup; do not introduce new test or build frameworks without a requirement.
