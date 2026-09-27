# Transaction Module Review

## Scope and review method

The assessment supplied only a low-effort prompt, not an existing module. To preserve the requested review trail, `src/transactions/Transaction.java` and `TransactionService.java` are an isolated draft generated from that exact prompt and are intentionally excluded from Maven's production source set. The remediated module is in `src/main/java/com/fintrack/fintrackapi/transactions/`.

Review process: inspect data types, query construction, resource lifecycle, input validation, ownership boundaries, error propagation, transactional behavior, and testability; then compare the findings against a layered JPA implementation. This coding-agent environment helped inspect code paths and organize a remediation checklist; it did not expose an IDE Copilot Chat panel, slash commands, or mode selector. Human judgment was necessary to evaluate monetary precision and the authorization threat model rather than treating compilation as correctness.

## Findings

| # | Location | Category | Severity | What's wrong and fintech impact | How detected | Recommended fix |
|---|---|---|---|---|---|---|
| 1 | `src/transactions/TransactionService.java`, `create` and `getByUser` | Security | Critical | User-controlled values are concatenated into SQL, enabling SQL injection and possible exposure or corruption of financial records. | Manual review — traced request values into SQL statement construction. | Replace raw SQL with Spring Data JPA repository methods. |
| 2 | `src/transactions/TransactionService.java`, `deleteAll` | Security / Bug | Critical | The unscoped delete removes every user's transactions; one user's action can destroy all customer records. | Manual review — checked delete scope against owner-specific access requirements. | Scope the delete by authenticated owner. |
| 3 | `src/transactions/TransactionService.java`, all methods | Performance / Standards | High | JDBC connections, statements, and result sets are never closed, leaking resources under load. | Manual review — traced resource acquisition and disposal. | Use JPA-managed persistence and lifecycle. |
| 4 | `src/transactions/Transaction.java`, `amount`; result mapping | Bug | High | `double` cannot represent decimal currency exactly, allowing financial arithmetic drift. | Manual review — examined amount type and currency arithmetic. | Use `BigDecimal` with explicit precision and scale. |
| 5 | `src/transactions/Transaction.java`, `userId`; `create` | Security | High | The service trusts caller-supplied owner identity, enabling transaction spoofing or cross-user access. | Manual threat modeling — request data is not authenticated identity. | Derive owner from the authenticated principal and scope all access. |
| 6 | `src/transactions/Transaction.java`, fields | Standards / Bug | High | Public mutable fields allow invalid state and accidental owner/amount mutation. | Manual review — checked field visibility and invariants. | Encapsulate state and use controlled construction. |
| 7 | `src/transactions/TransactionService.java`, `create` | Bug | Medium | Blank descriptions, zero/negative amounts, excessive precision, and oversized strings are not rejected. | Manual review — enumerated invalid and boundary inputs. | Add Jakarta Validation and service-level monetary checks. |
| 8 | `src/transactions/TransactionService.java`, `getByUser` | Architecture / Standards | Medium | `SELECT *` and manual row mapping couple business code to schema and leak generic SQL errors. | Manual review — inspected query/mapping and error contract. | Use typed repository/entity mapping and explicit API errors. |
| 9 | `src/transactions/Transaction.java`, `createdAt` | Bug | Medium | `LocalDateTime` has no timezone and is initialized before persistence. | Manual review — considered timestamp semantics across services. | Store and return an `Instant`. |
| 10 | `src/transactions/TransactionService.java`, all methods | Architecture / Standards | Medium | No transaction boundary or structured operational logging exists, complicating atomicity and support. | Manual review — checked transaction and logging behavior. | Add service transaction boundaries and parameterized logs without sensitive values. |
| 11 | `src/transactions/` module | Architecture / Standards | Medium | No controller, API contract, or tests exist, leaving authorization and behavior unverified. | Manual review — compared draft scope with required API and test cases. | Add layered controller, DTOs, exception mapping, and tests. |

## Remediated design

`TransactionEntity` is persisted by `TransactionRepository`; `TransactionService` contains the use cases and is transaction-bound; `TransactionController` derives the owner from the authenticated principal and exposes validated DTOs. A request for another user's transaction is indistinguishable from a missing transaction (404). Bulk delete is explicitly owner-scoped. Spring Security requires authentication for all routes.

## Issues Copilot Introduced That Required Human Judgment

The specimen is a reconstruction of the assessment's low-effort output shape, not a captured Copilot-generated artifact; the environment did not include the requested IDE generation/review screenshots. The following issues are present in the specimen and require human review whether or not an AI reviewer notices them.

- A naive replacement of floating-point amounts with `BigDecimal` is not enough: division still needs a deterministic cent-remainder policy. Equal splits now assign leftover cents in participant order and reject totals too small to give every participant a positive cent.
- A low-effort transaction service offers unscoped `deleteAll`; a developer must recognize this is a cross-customer data-loss vulnerability, not merely a missing convenience parameter.
- Request-supplied `userId` is not proof of identity. Only the authenticated principal is trusted for transaction ownership and the expense creator.
- Persistence annotations must match the relationship ownership model. The participant list stores explicit order data rather than relying on an invalid order-column mapping for an inverse JPA relationship.
