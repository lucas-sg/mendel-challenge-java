package mendel.challenge.java.lucas_sanz_gorostiaga.service;

import java.util.ArrayList;
import java.util.Optional;

import mendel.challenge.java.lucas_sanz_gorostiaga.Db;
import mendel.challenge.java.lucas_sanz_gorostiaga.controller.TransactionBody;
import mendel.challenge.java.lucas_sanz_gorostiaga.model.Transaction;


public class TransactionService {
    public void insertTransaction(TransactionBody transactionBody) {
        Db db = new Db();
        Transaction transaction = createTransactionFromTransactionBody(transactionBody, db);
        insertTransactionInTransactionByTypeMap(transaction, db);
        insertTransactionInTransactionByIdMap(transaction, db);
    }

    private boolean insertTransactionInTransactionByTypeMap(Transaction transaction, Db db) {
        String transactionType = transaction.getType();

        return db.getTransactionsByType()
            .computeIfAbsent(transactionType, t -> new ArrayList<Transaction>())
            .add(transaction);
    }

    private boolean insertTransactionInTransactionByIdMap(Transaction transaction, Db db) {
        Long transactionId = transaction.getId();
        boolean successfullyInserted = db.getTransactionsById().putIfAbsent(transactionId, transaction) == null;
        if (!successfullyInserted) {
            return false;
        }

        updateAscendantTransactions(transaction);

        return true;
    }

    private void updateAscendantTransactions(Transaction transaction) {
        Optional<Transaction> parent = transaction.getParent();
        Double transactionAmount = transaction.getAmount();

        while (parent.isPresent()) {
            Transaction parentValue = parent.get();
            parentValue.setDescendantsSum(parentValue.getDescendantsSum() + transactionAmount);
            parent = parentValue.getParent();
        }
    }

    private Transaction createTransactionFromTransactionBody(TransactionBody transactionBody, Db db) {
        Transaction transaction = new Transaction();
        Optional<Transaction> parent = Optional.ofNullable(db.getTransactionsById().get(transactionBody.parentId));
        transaction.setAmount(transactionBody.amount);
        transaction.setParent(parent);
        transaction.setType(transactionBody.type);

        return transaction;
    }
}
