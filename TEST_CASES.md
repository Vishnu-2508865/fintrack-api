# Test Case Documentation

Run all tests from the project root:

```powershell
.\mvnw.cmd test
```

| ID | Test | Expected result | Test location |
|---|---|---|---|
| EXP-01 | Split 10.00 equally among three participants | Shares are 3.34, 3.33, and 3.33 in request order; sum is exactly 10.00 | `ExpenseSplittingTests.equalSplitAmongThreeParticipantsDistributesRemainderExactly` |
| EXP-02 | Custom shares of 10.00 and 20.00 for a 30.00 expense | Expense is accepted and persisted shares sum exactly to 30.00 | `ExpenseSplittingTests.customSplitAcceptsAmountsThatMatchTotal` |
| EXP-03 | Custom shares of 10.00 and 19.99 for a 30.00 expense | `InvalidRequestException`; repository save is not called | `ExpenseSplittingTests.customSplitRejectsAmountsThatDoNotMatchTotal` |
| EXP-04 | Net multiple expenses in both directions between Alice and Bob | A single balance remains; Alice is owed 20.00 by Bob (`netAmount` -20.00) | `ExpenseSplittingTests.balancesNetMultipleExpensesBetweenTwoUsers` |
| EXP-05 | Create an expense with one participant | `InvalidRequestException`; repository save is not called | `ExpenseSplittingTests.expenseWithOneParticipantIsRejected` |
| TX-01 | Delete a transaction using a user who does not own it | `ResourceNotFoundException`; no transaction is deleted | `ExpenseSplittingTests.userCannotDeleteAnotherUsersTransaction` |
| SEC-01 | Call balances without authentication | HTTP 401 Unauthorized | `SecurityTests.unauthenticatedRequestIsRejected` |
| SEC-02 | Authenticated user requests balances | HTTP 200 OK | `SecurityTests.authenticatedUserCanRequestOwnBalances` |
| SEC-03 | Alice creates an equal expense with Bob and Bob requests balances | HTTP 201 for creation; Bob sees Alice as payee and owes 5.00 | `SecurityTests.createdExpenseProducesBalanceForAnotherAuthenticatedParticipant` |
| APP-01 | Start the Spring application context | Context loads with JPA repositories and security configuration | `FinTrackApplicationTests.contextLoads` |

The balance convention is intentional: each non-creator participant's share is treated as owed to the expense creator. This assessment does not implement settlements, partial repayment, identity-directory validation, or production database migrations.
