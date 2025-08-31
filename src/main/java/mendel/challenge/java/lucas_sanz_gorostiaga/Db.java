package mendel.challenge.java.lucas_sanz_gorostiaga;

import java.util.List;
import java.util.Map;

import lombok.Data;
import mendel.challenge.java.lucas_sanz_gorostiaga.model.Transaction;

@Data
public class Db {
    private Map<Long, Transaction> transactionsById;
    private Map<String, List<Long>> transactionIdsByType;
}
