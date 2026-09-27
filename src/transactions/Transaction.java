package transactions;

import java.time.LocalDateTime;

// Preserved low-effort AI draft for review; not part of the Maven production source tree.
public class Transaction {
    public Long id;
    public String userId;
    public String description;
    public double amount;
    public LocalDateTime createdAt = LocalDateTime.now();
}
