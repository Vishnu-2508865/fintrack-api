# Tool Strategy Reflection

## Feature Usage Log

The implementation used the repository-aware GitHub Copilot coding-agent environment. The environment did not expose the VS Code Chat mode selector, inline ghost text, slash commands, hosted code review, or screenshots; those UI features are not presented as having been exercised.

| # | Where used | Copilot capability used | Why this capability | What happened |
|---|---|---|---|---|
| 1 | Whole assessment | Repository/workspace context in the coding-agent task | The requirements referenced an existing Spring Boot project and several connected deliverables; shared repository context kept the changes consistent. | Inspected the Maven project and built on its Java/Spring stack instead of creating a separate application. |
| 2 | Project standards | Repository file creation/editing | Project-wide instructions need to live in a committed, reusable location. | Added `.github/copilot-instructions.md` with the stack, layering, money, security, and testing rules. |
| 3 | Transaction and expense modules | Multi-file coding-agent edits | Entities, repositories, services, controllers, and configuration must agree on DTOs and identity boundaries. | Implemented the feature layers together; review corrected a JPA ordering mismatch. |
| 4 | Test and compile cycles | Terminal/build-tool execution via the coding environment | Running the project's existing Maven commands verifies actual compilation and behavior rather than relying on generated reasoning. | Corrected the Spring Boot 4 test import and verified service plus HTTP security tests. |
| 5 | Review and remediation | Repository inspection and iterative coding-agent refinement | Security and financial invariants cross multiple files and cannot be checked from a single snippet. | Identified and fixed money-rounding and participant ordering edge cases; preserved the unreviewed specimen outside production sources. |
| 6 | Submission packaging | Repository-aware file and archive operations | The guide permits source ZIP delivery, which avoids representing unavailable IDE screenshots as evidence. | Added architecture/flow and test case documentation and packaged source without build output or dependency caches. |

## Scenario Responses

**Understand a complex 500-line function in an unfamiliar codebase:** Use Copilot Chat with the function and relevant callers in repository context. Ask for control flow, side effects, invariants, and dependencies, then verify its account against the source before editing.

**Add consistent error handling across eight route handlers:** Use Copilot Agent mode with all handlers and current error conventions in context. Ask for a consistent scoped change and focused response tests, then inspect each diff for unintended endpoint behavior changes.

**Quickly verify an international phone-number regex:** Use Copilot Chat to explain the expression and generate representative country/format edge cases, then run those cases in the project's existing test runner. A generated regex or a handful of examples is not proof of universal international validity.

**Enforce automated code-quality checks on every pull request:** Configure GitHub Actions and required branch-protection checks rather than relying on a Copilot chat feature. Repository automation must run checks and block merges when they fail.

**Review an AI-generated authentication module for vulnerabilities:** Use Copilot code review or Chat in reviewer role with the complete auth flow and an explicit threat model covering credential storage, authorization, token lifecycle, injection, and secrets. Treat findings as leads and manually trace trust boundaries; AI review is not security sign-off.

**Ensure consistent project conventions across developers and sessions:** Commit `.github/copilot-instructions.md` with specific, testable rules. Versioned project instructions are more reliable than depending on conversation history or each developer repeating the same prompt.

## Limitations Encountered

| # | What was prompted/done | Incorrect, incomplete, or inappropriate output | Detection | Correction | What to do differently |
|---|---|---|---|---|---|
| 1 | Designed ordered JPA expense participants. | An initial inverse `mappedBy` collection used `@OrderColumn`; Hibernate warned that this mapping was invalid for the relationship. | Read the ORM startup warning during test execution; unit-only tests would not reveal it. | Stored participant order on the owning participant entity and used `@OrderBy`. | Specify the owning side and validate entity mappings by starting the real persistence context. |
| 2 | Implemented equal split remainder handling. | A small total could give one participant zero cents despite the deterministic remainder policy. | Reasoned through totals smaller than the participant count in cents. | Reject totals that cannot allocate at least one cent to every participant. | Include tiny totals and zero-share invariants in the initial acceptance criteria. |
| 3 | Added HTTP security tests. | The first ownership test exercised the service but did not establish anonymous HTTP rejection or authenticated principal flow. | Compared test coverage with the API security requirement; service calls bypass the filter chain. | Added MockMvc tests for 401 unauthenticated access, authenticated balance access, and expense-to-balance flow. | Map each security acceptance criterion to a request-level integration test. |

The source-code ZIP path does not require process screenshots. This archive therefore contains no fabricated IDE screenshots or unverified Copilot mode claims.
