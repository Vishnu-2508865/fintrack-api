# Tool Strategy Reflection

## Feature Usage Log

| # | Copilot feature | Why this feature, and what happened |
|---|---|---|
| 1 | Copilot Chat (Ask/conversation) | Used to translate the narrative assessment into acceptance criteria before touching feature behavior; surfaced the required outputs and security boundaries. |
| 2 | Copilot Agent mode with repository context | Used for coordinated edits across Java, Maven, tests, and docs rather than isolated snippets; it produced a coherent Spring MVC/JPA structure. |
| 3 | `.github/copilot-instructions.md` custom instructions | Added project-specific architecture, financial precision, and ownership rules; later changes followed those conventions. |
| 4 | Agent-driven multi-file edits (Copilot Edits workflow) | Applied related entity/service/controller changes together; review caught a relationship-ordering mismatch before it remained in the design. |
| 5 | Chat-based test generation | Converted acceptance criteria into JUnit scenarios; exact monetary values and a foreign-owner deletion check made the expected behavior concrete. |
| 6 | Iterative Chat review/refinement | Asked for an adversarial fintech review rather than accepting the first code draft; corrected edge cases and documented remaining limitations. |

This environment exposed the agent/chat coding workflow, not separate IDE inline-completion, hosted pull-request review, or CI setup experiences; those are not claimed as executed.

## Scenario Responses

**Understand a complex 500-line function in an unfamiliar codebase:** Use Copilot Chat with the function and relevant callers in repository context. Ask for a control-flow summary, side effects, invariants, and dependencies, then verify each claim against the code before changing it.

**Add consistent error handling across eight route handlers:** Use Copilot Agent mode with all eight handlers and the existing error conventions in context. Ask it to make a consistent, scoped edit and add/adjust tests for status and response shape; review every route diff to avoid changing endpoint semantics.

**Quickly verify an international phone-number regex:** Use Copilot Chat to explain the regex and generate a focused set of country/format edge cases, then execute those cases in the project's existing test runner. Do not trust a generated regex or a few happy-path examples as proof of international validity.

**Enforce automated quality checks on every pull request:** Use GitHub Actions workflows and required branch-protection checks, not Copilot Chat. A human-free PR gate needs repository automation configured to run and block merges on failing checks.

**Review an AI-generated authentication module for vulnerabilities:** Use GitHub Copilot code review or Copilot Chat in reviewer role, giving it the full auth flow and explicit threat model (credential storage, authorization, token lifecycle, injection, and secrets). Treat findings as leads, then manually trace trust boundaries and test exploit-relevant cases; AI review is not a security sign-off.

**Ensure consistent project conventions across developers and sessions:** Add committed `.github/copilot-instructions.md` instructions and keep them specific, testable, and versioned with the code. This is more reliable than relying on a developer's conversation history or repeating a long prompt.

## Limitations Encountered

1. **Relationship ordering:** During entity design, an inverse JPA `mappedBy` collection was initially given an `@OrderColumn`; Hibernate warned this mapping is not valid for that relationship. The warning exposed a persistence-level issue that ordinary service unit tests would miss. Replaced it with an explicit participant-order column and `@OrderBy`; future prompts should name the owning side and request Hibernate-compatible mapping.
2. **Sub-cent equal allocations:** The first equal-split logic distributed the remainder but did not prevent a total smaller than the participant count in cents, allowing a zero share. Boundary reasoning found the issue; the service now rejects totals that cannot give each participant at least one cent. Future prompts should include tiny totals and zero-share invariants, not only typical amounts.
3. **Authentication coverage:** The first test pass denied deletion under the wrong owner at service level, but did not prove the web security filter returned 401 to an unauthenticated caller. Self-review identified the coverage mismatch; added MockMvc checks for unauthenticated and authenticated balance requests. Future work should add an HTTP-level cross-user transaction deletion test to verify principal propagation end to end.

The agent workflow assisted implementation and review but did not replace manual validation of persistence semantics, money invariants, or security coverage. The separate hosted pull-request and inline-completion tools were unavailable here.
