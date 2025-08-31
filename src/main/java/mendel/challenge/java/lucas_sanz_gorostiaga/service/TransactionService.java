package mendel.challenge.java.lucas_sanz_gorostiaga.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import mendel.challenge.java.lucas_sanz_gorostiaga.Db;
import mendel.challenge.java.lucas_sanz_gorostiaga.controller.TransactionBody;
import mendel.challenge.java.lucas_sanz_gorostiaga.model.Transaction;


@Service
public class TransactionService {
    private final Db db;

    public TransactionService(Db db) {
        this.db = db;
    }


    public boolean insertTransaction(TransactionBody transactionBody) {
        Transaction transaction = createTransactionFromTransactionBody(transactionBody);

        return insertTransactionInTransactionByTypeMap(transaction)
            && insertTransactionInTransactionByIdMap(transaction);
    }

    private boolean insertTransactionInTransactionByTypeMap(Transaction transaction) {
        String transactionType = transaction.getType();

        return db.getTransactionIdsByType()
            .computeIfAbsent(transactionType, t -> new ArrayList<Long>())
            .add(transaction.getId());
    }

    private boolean insertTransactionInTransactionByIdMap(Transaction transaction) {
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

    private Transaction createTransactionFromTransactionBody(TransactionBody transactionBody) {
        Transaction transaction = new Transaction();
        Optional<Transaction> parent = Optional.ofNullable(db.getTransactionsById().get(transactionBody.parentId));
        transaction.setAmount(transactionBody.amount);
        transaction.setParent(parent);
        transaction.setType(transactionBody.type);

        return transaction;
    }

    public Optional<List<Long>> getTransactionIdsByType(String type) {
        return Optional.ofNullable(db.getTransactionIdsByType().get(type));
    }

    public Double getTransactionDescendantsSum(Long transactionId) {
        return Optional.ofNullable(db.getTransactionsById().get(transactionId))
            .map(Transaction::getDescendantsSum)
            .orElse(0.0);
    }
}
