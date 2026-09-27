# Prompt and AI-Use Record

## Scope and evidence

This package is the source-code submission option from the associate guide. Work was completed through the repository-aware GitHub Copilot coding-agent interface, which does not expose the VS Code Copilot Chat Ask/Edit/Agent selector or provide screenshots of those modes. No IDE screenshots or mode-specific transcripts are included, and this document does not claim that a separate IDE Chat session occurred.

The assessment supplied the following required low-effort prompt:

> Generate a Transaction model and a Transaction service with create, get-by-user, and delete-all functions. Use a database.

The isolated files in `src/transactions/` are a representative low-effort draft for review, produced as part of the coding-agent implementation. They are not preserved output from a captured IDE Copilot generation session. See `REVIEW.md` for the findings and remediation.

## Work instructions reflected in the implementation

The exact historical user prompts in this session were the assessment request and its follow-up to prepare the project against the evaluation guide. The following are concise descriptions of the implementation instructions embodied in that work, not verbatim Copilot Chat prompts or evidence of distinct UI modes:

| # | Instruction focus | Copilot interface evidence available | Prompting technique reflected |
|---|---|---|---|
| 1 | Preserve the requested low-effort transaction draft, review it, and replace it with a layered JPA module. | Coding-agent task context; no IDE mode selector. | Constraint and decomposition |
| 2 | Add a shared-expense model, service, repository, and authenticated API; support equal and custom shares. | Coding-agent workspace edits; no Chat transcript screenshot. | Specificity and decomposition |
| 3 | Reject mismatched custom shares, duplicate/insufficient participants, invalid precision, and creator omission. | Coding-agent task context and implementation review. | Constraint-based prompting |
| 4 | Net debt directions across expenses and document the positive/negative balance convention. | Coding-agent task context; service and API tests added. | Domain-rule specificity |
| 5 | Cover the named split, validation, netting, ownership, and authentication scenarios with JUnit. | Coding-agent edits plus Maven test execution. | Acceptance-criteria enumeration |
| 6 | Add standards, review, architecture, prompt/tool reflection, PR, and submission artifacts. | Repository context and multi-file edits. | Decomposition and iterative refinement |

## Post-Generation Corrections

The coding-agent implementation was reviewed and corrected before packaging:

- Hibernate flagged use of `@OrderColumn` on the inverse `mappedBy` participant collection. Replaced it with a participant-order field and `@OrderBy`.
- Equal splits of very small totals could assign a zero-cent share. Added a guard requiring at least one cent per participant.
- Simplified debt aggregation to mutate the single `PairAmounts` value obtained from the map, avoiding a redundant second lookup.
- An initial security-test import did not match Spring Boot 4's package layout. Updated it to the package exposed by the project's dependency.
- Service-only ownership coverage did not establish that web authentication was configured; added HTTP tests for anonymous rejection, authenticated balance access, and a created expense viewed by another participant.
- This workspace had no visible IDE Copilot Chat output. Therefore, no screenshot, Ask/Edit/Agent mode, slash-command, or verbatim chat-response claim is made.
