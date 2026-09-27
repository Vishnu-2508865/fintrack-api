package transactions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Preserved low-effort AI draft for review; intentionally not part of the Maven production source tree.
public class TransactionService {
    private static final String URL = "jdbc:h2:mem:fintrack";

    public Transaction create(Transaction transaction) throws Exception {
        Connection connection = DriverManager.getConnection(URL, "sa", "");
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO transactions (user_id, description, amount) VALUES ('"
                + transaction.userId + "', '" + transaction.description + "', " + transaction.amount + ")");
        return transaction;
    }

    public List<Transaction> getByUser(String userId) throws Exception {
        Connection connection = DriverManager.getConnection(URL, "sa", "");
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(
                "SELECT * FROM transactions WHERE user_id = '" + userId + "'");
        List<Transaction> transactions = new ArrayList<>();
        while (result.next()) {
            Transaction transaction = new Transaction();
            transaction.id = result.getLong("id");
            transaction.userId = result.getString("user_id");
            transaction.description = result.getString("description");
            transaction.amount = result.getDouble("amount");
            transactions.add(transaction);
        }
        return transactions;
    }

    public void deleteAll() throws Exception {
        Connection connection = DriverManager.getConnection(URL, "sa", "");
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM transactions");
    }
}
