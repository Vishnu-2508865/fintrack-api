# Transaction Module Review

## Scope and review method

The assessment supplied only a low-effort prompt, not an existing module. To preserve the requested review trail, `src/transactions/Transaction.java` and `TransactionService.java` are an isolated draft generated from that exact prompt and are intentionally excluded from Maven's production source set. The remediated module is in `src/main/java/com/fintrack/fintrackapi/transactions/`.

Review process: inspect data types, query construction, resource lifecycle, input validation, ownership boundaries, error propagation, transactional behavior, and testability; then compare the findings against a layered JPA implementation. Copilot-assisted review helped enumerate code paths and propose a remediation checklist. Human judgment was necessary to evaluate monetary precision and the authorization threat model rather than treating compilation as correctness.

## Findings

| Severity | Location | Issue and fintech impact | Detection | Remediation |
|---|---|---|---|---|
| Critical | `src/transactions/TransactionService.java`, `create` and `getByUser` | User-controlled values are concatenated into SQL, enabling SQL injection and possible exposure or corruption of financial records. | Traced request values into SQL and inspected statement construction. | Replaced JDBC/string SQL with Spring Data JPA repository methods. |
| Critical | `src/transactions/TransactionService.java`, `deleteAll` | The unscoped delete removes every user's transactions; there is no caller/owner check. | Compared delete semantics with the user-scoped read path and required authorization invariant. | `deleteAll(ownerId)` deletes only rows for the authenticated owner. |
| High | `src/transactions/TransactionService.java`, all methods | Raw JDBC connections/statements/results are never closed, causing resource leaks under load. | Followed resource acquisition and verified there was no `close`/try-with-resources. | JPA manages connection and statement lifecycles. |
| High | `src/transactions/Transaction.java`, `amount`; result mapping | `double` cannot represent decimal currency exactly, so totals can drift. | Checked the monetary type and considered repeated arithmetic/round trips. | Use `BigDecimal`, precision/scale constraints, and input digit validation. |
| High | `src/transactions/Transaction.java`, `userId`; `create` | The caller supplies arbitrary owner identity, and the service does not establish that the caller may create/read that user's records. | Threat-modeled spoofed user IDs and direct service/controller access. | Controllers derive the owner from `Authentication`; queries and deletes scope by that identity. |
| High | `src/transactions/Transaction.java`, fields | Public mutable fields allow invalid values and accidental owner/amount mutation from any caller. | Inspected visibility and the absence of invariants. | Encapsulated JPA entity with controlled construction and read-only accessors. |
| Medium | `create` | No validation rejects blank descriptions, missing users, zero/negative amounts, excessive precision, or oversized strings. | Enumerated invalid inputs and boundary values. | Jakarta Validation on DTOs plus typed monetary columns. |
| Medium | `getByUser` | `SELECT *` and hand-mapped rows couple the service to schema shape; missing-schema/result errors propagate as generic SQL exceptions. | Inspected query/result mapping and exception contract. | Repository interface plus typed entity/response mapping and explicit API errors. |
| Medium | `createdAt` | `LocalDateTime` has no timezone/offset and is set at object construction, not persistence. | Reviewed time type and entity lifecycle. | Store an `Instant` and return it in the response. |
| Medium | all methods | No service transaction boundary or structured operational logging exists. | Checked the service for transaction and logging conventions. | Added `@Transactional` boundaries and parameterized logs containing IDs only. |
| Medium | module | No controller, request/response contract, or tests exist; callers cannot use the service consistently or verify ownership. | Compared the draft with the requested API and test expectations. | Added REST controller, DTOs, common error mapping, and ownership/money tests. |

## Remediated design

`TransactionEntity` is persisted by `TransactionRepository`; `TransactionService` contains the use cases and is transaction-bound; `TransactionController` derives the owner from the authenticated principal and exposes validated DTOs. A request for another user's transaction is indistinguishable from a missing transaction (404). Bulk delete is explicitly owner-scoped. Spring Security requires authentication for all routes.

## Issues Copilot Introduced That Required Human Judgment

- A naive replacement of floating-point amounts with `BigDecimal` is not enough: division still needs a deterministic cent-remainder policy. Equal splits now assign leftover cents in participant order and reject totals too small to give every participant a positive cent.
- A low-effort transaction service offers unscoped `deleteAll`; a developer must recognize this is a cross-customer data-loss vulnerability, not merely a missing convenience parameter.
- Request-supplied `userId` is not proof of identity. Only the authenticated principal is trusted for transaction ownership and the expense creator.
- Persistence annotations must match the relationship ownership model. The participant list stores explicit order data rather than relying on an invalid order-column mapping for an inverse JPA relationship.
