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
        if (transactionRepository.getTransactionsById().get(transactionId) != null) {
            throw new ResourceAlreadyExistsException("Transaction with id " + transactionId + " already exists");
        }

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
        Transaction parent = transaction.getParent();
        Double transactionAmount = transaction.getAmount();

        while (parent != null) {
            Transaction parentValue = parent;
            parentValue.setDescendantsSum(parentValue.getDescendantsSum() + transactionAmount);
            parent = parentValue.getParent();
        }
    }

    private Transaction createTransactionFromTransactionBody(TransactionBody transactionBody, Long transactionId) {
        Optional<Transaction> parent = Optional.empty();
        if (transactionBody.parentId != null) {
            parent = Optional.ofNullable(transactionRepository.getTransactionsById().get(transactionBody.parentId));
        }

        Transaction.TransactionBuilder builder = Transaction.builder()
                .id(transactionId)
                .amount(transactionBody.amount)
                .type(transactionBody.type);
        parent.ifPresent(builder::parent);

        return builder.build();
    }

    public List<Long> getTransactionIdsByType(String type) {
        return Optional.ofNullable(transactionRepository.getTransactionIdsByType().get(type)).orElse(new ArrayList<>());
    }

    public Double getTransactionDescendantsSum(Long transactionId) {
        return Optional.ofNullable(transactionRepository.getTransactionsById().get(transactionId))
            .map(Transaction::getDescendantsSum)
            .orElse(0.0);
    }
}
