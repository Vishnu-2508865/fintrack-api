# Copilot Prompt Record

Prompts below record the implementation sequence used for this assessment. Copilot was used in repository-aware Chat/agent interactions; the tool did not provide a separate hosted PR-review or inline-completion UI. The exact original low-effort transaction prompt is retained before the follow-up prompts.

## Prompt chain

### 1. Create the inherited transaction draft

**Exact prompt:** `Generate a Transaction model and a Transaction service with create, get-by-user, and delete-all functions. Use a database.`

**Feature:** Copilot Chat (generation request). **Technique:** Deliberately underspecified prompt. **Rationale:** The assessment explicitly requires preserving an unreviewed example. The output was placed in `src/transactions/` and reviewed separately rather than compiled as production code.

### 2. Establish project conventions

**Exact prompt:** `Set up .github/copilot-instructions.md for this Java Spring Boot fintech API. Specify layered MVC/JPA architecture, BigDecimal-only money handling, authenticated-principal ownership, request validation, explicit errors, privacy-safe logs, and tests for monetary and authorization boundaries.`

**Feature:** Copilot Chat plus repository custom instructions. **Technique:** Constraint-based and role-based prompting. **Rationale:** Fix the stack and security expectations centrally so subsequent work follows consistent conventions.

### 3. Build the expense domain in layers

**Exact prompt:** `Implement shared expenses in the existing Spring Boot project: use JPA entities/repositories, service-layer validation and calculation, request/response DTOs, and authenticated REST controllers. Support EQUAL and CUSTOM splits; require at least two unique participants including the creator; reject invalid currency amounts; assign equal-split remainder cents deterministically; require custom shares to sum exactly to the total. Do not accept creator identity from the request body.`

**Feature:** Copilot Chat Agent mode with repository context. **Technique:** Decomposition, specificity, and constraints. **Rationale:** Scope the layers and business invariants before generating interconnected API code.

### 4. Add balance netting

**Exact prompt:** `Add a balance service and GET /api/balances. For each expense, treat every non-creator participant's share as owed to the creator. For the authenticated user, aggregate both directions by counterparty, net them, omit zero balances, and document that positive net means the user owes. Preserve exact BigDecimal cents.`

**Feature:** Copilot Chat Agent mode. **Technique:** Rule-based decomposition. **Rationale:** State debt direction explicitly to avoid a plausible but reversed sign or double count.

### 5. Generate boundary tests

**Exact prompt:** `Write focused JUnit tests for three-person equal split, exact custom split, custom total mismatch, netting debts across several expenses, one-participant rejection, and attempting to delete a transaction owned by another user. Assert exact BigDecimal values and verify invalid requests are not persisted.`

**Feature:** Copilot Chat test-generation workflow. **Technique:** Few-shot-by-enumeration and negative-case constraints. **Rationale:** Turn the assessment's acceptance criteria into executable assertions.

### 6. Review and refine

**Exact prompt:** `Review the changed code as a fintech reviewer. Trace principal identity to owner-scoped persistence, check all monetary rounding and validation boundaries, check JPA relationship ownership/order behavior, confirm unauthenticated routes are denied, and identify any tests that only prove a proxy rather than the requested behavior. Fix confirmed defects without changing unrelated files.`

**Feature:** Copilot Chat iterative refinement / code review. **Technique:** Role-based review plus iterative correction. **Rationale:** Explicitly ask for adversarial validation of the sensitive boundaries, not just style.

## Post-Generation Corrections

- The first expense entity draft used an order-column annotation on an inverse `mappedBy` collection, which Hibernate warns is invalid. Replaced it with a persisted participant-order field and `@OrderBy`.
- Equal division can produce zero-cent shares for very small totals. Added a service guard requiring at least one cent per participant.
- The first balance aggregation expression performed a map lookup after mutation setup. Simplified it to retain the single `PairAmounts` reference, making the direction and aggregation path clearer.
- Reviewed monetary handling to ensure all operations remain `BigDecimal`, exact sum comparison uses `compareTo`, and scale overflow fails with a 400-level validation error.
- Kept the AI draft's lack of ownership enforcement, JDBC resource management, and float money in the isolated review specimen only; none of those defects remain in the compiled transaction module.
