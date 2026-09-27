# Architecture
1. The Transaction and Expense modules are separate Spring features sharing authenticated principal IDs and the same JPA database.
2. Transaction records are private to their owner; shared expenses record the creator and each participant's allocated share.
3. Controllers validate request DTOs, derive identity from Spring Security, and delegate all rules to services.
4. Services enforce ownership, participant and amount invariants, and translate stored records to API response DTOs.
5. Spring Data JPA repositories provide persistence; controllers and services do not issue SQL.
6. Expense creation stores the total, split type, ordered participants, and exact two-decimal shares atomically.
7. BalanceService reads expenses involving the caller and nets both debt directions for each counterparty.
8. A positive `netAmount` means the caller owes; a negative value means the other user owes the caller.
9. Jakarta Validation handles request shape, while services enforce cross-field business invariants.
10. A shared exception handler returns explicit 400/404 responses without exposing inaccessible transaction IDs.
11. BigDecimal prevents binary floating-point drift; equal-split remainder cents are assigned in participant order.
12. Owner-scoped repositories and authenticated identity prevent clients from choosing another user's transaction owner.
13. These boundaries support auditable financial rules; production still requires a managed database and identity provider.
