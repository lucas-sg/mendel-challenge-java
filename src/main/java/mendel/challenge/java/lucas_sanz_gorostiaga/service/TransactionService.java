package mendel.challenge.java.lucas_sanz_gorostiaga.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mendel.challenge.java.lucas_sanz_gorostiaga.controller.TransactionBody;
import mendel.challenge.java.lucas_sanz_gorostiaga.model.Transaction;
import mendel.challenge.java.lucas_sanz_gorostiaga.repository.TransactionRepository;


@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    public boolean insertTransaction(TransactionBody transactionBody, Long transactionId) {
        Transaction transaction = createTransactionFromTransactionBody(transactionBody, transactionId);

        return insertTransactionInTransactionByTypeMap(transaction)
            && insertTransactionInTransactionByIdMap(transaction);
    }

    private boolean insertTransactionInTransactionByTypeMap(Transaction transaction) {
        String transactionType = transaction.getType();

        return transactionRepository.getTransactionIdsByType()
            .computeIfAbsent(transactionType, t -> new ArrayList<>())
            .add(transaction.getId());
    }

    private boolean insertTransactionInTransactionByIdMap(Transaction transaction) {
        Long transactionId = transaction.getId();
        boolean successfullyInserted = transactionRepository
            .getTransactionsById()
            .putIfAbsent(transactionId, transaction) == null;

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

    private Transaction createTransactionFromTransactionBody(TransactionBody transactionBody, Long transactionId) {
        Transaction transaction = new Transaction();

        if (transactionBody.parentId != null) {
            Optional<Transaction> parent = Optional.ofNullable(transactionRepository.getTransactionsById()
                    .get(transactionBody.parentId));
            transaction.setParent(parent);
        }

        transaction.setId(transactionId);
        transaction.setAmount(transactionBody.amount);
        transaction.setType(transactionBody.type);

        return transaction;
    }

    public Optional<List<Long>> getTransactionIdsByType(String type) {
        return Optional.ofNullable(transactionRepository.getTransactionIdsByType().get(type));
    }

    public Double getTransactionDescendantsSum(Long transactionId) {
        return Optional.ofNullable(transactionRepository.getTransactionsById().get(transactionId))
            .map(Transaction::getDescendantsSum)
            .orElse(0.0);
    }
}
