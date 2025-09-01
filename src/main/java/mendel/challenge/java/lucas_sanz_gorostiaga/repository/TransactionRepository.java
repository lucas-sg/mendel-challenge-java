package mendel.challenge.java.lucas_sanz_gorostiaga.repository;

import lombok.Getter;
import mendel.challenge.java.lucas_sanz_gorostiaga.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Getter
@Repository
public class TransactionRepository {
    private final Map<Long, Transaction> transactionsById = new ConcurrentHashMap<>();
    private final Map<String, List<Long>> transactionIdsByType = new ConcurrentHashMap<>();
}
