# PR: Add secure shared expenses and remediate transactions

## Summary

Adds authenticated transaction endpoints backed by owner-scoped Spring Data JPA and a shared-expense feature with equal/custom splits, exact currency validation, and net pending balances. Includes focused service tests and project-level Copilot conventions. The low-effort transaction draft is preserved separately as the assessment review specimen.

## AI Tool Disclosure

Copilot Chat/agent interactions were used to scaffold the layered implementation, propose tests, and review refinements. Accepted output includes routine DTO/controller/repository scaffolding after inspection. Human review overrode or corrected choices involving SQL/owner scope, JPA relationship ordering, equal-split rounding, small totals, and debt direction. Estimated contribution: approximately 55% AI-assisted scaffolding and test structure, 45% human-directed requirements, review, corrections, and documentation; this is an estimate, not a line-count measure. The current environment did not provide separate hosted PR-review or inline-completion surfaces.

## Testing and known gaps

The test suite covers the required six cases: three-way equal split, exact custom split, custom-sum rejection, multi-expense netting, one-participant rejection, and cross-user transaction deletion. MockMvc tests also verify unauthenticated requests are rejected and authenticated callers can request balances. Cross-user HTTP-level transaction tests, concurrency/load testing, settlement/payments, pagination, and production database migrations are not included.

## Risk and trade-off

Balances are derived from immutable expense shares and every share remains pending; there is no settlement ledger or payment state. This is deliberate for the requested scope but means balances cannot represent partial repayment. The sample uses H2 and Spring Boot's generated development identity; production must replace both with managed persistence and an approved identity provider.

## Self-review checklist

- [x] User identity comes from Spring Security, not a request field.
- [x] Transaction reads/deletes are owner-scoped; inaccessible IDs return 404.
- [x] Monetary calculations use BigDecimal, explicit scale, exact custom sums, and deterministic cent allocation.
- [x] At least two unique participants are required, and the creator must be among them.
- [x] Expense and transaction persistence use JPA, not raw SQL.
- [x] Invalid requests produce explicit client errors; logs exclude descriptions and amounts.
- [x] Tests cover requested success, rejection, netting, and authorization scenarios.
- [x] Local demo authentication/database are documented as unsuitable production defaults.

## Peer Review Simulation

1. **`SharedExpenseService.equalShares()` —** Please keep the minimum-cent-per-participant guard when changing split logic. Without it, a $0.01 expense split between two people creates a zero share, which makes participant balances misleading; add a regression test for that boundary.
2. **`TransactionController.delete()` —** Please add an HTTP-level test where Alice attempts to delete Bob's transaction and receives 404, then verify Bob's data remains. The service test proves repository scoping, but exercising the controller-to-repository path protects principal propagation from future regressions.
3. **`SharedExpenseService.validateParticipants()` —** Please consider validating participant IDs against the user directory before persistence once identity provisioning exists. Accepting arbitrary strings currently permits a typo to become a permanent counterparty with no account or way to authenticate; this is a product/security boundary that generated code is unlikely to infer.
