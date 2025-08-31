package mendel.challenge.java.lucas_sanz_gorostiaga;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Data;
import mendel.challenge.java.lucas_sanz_gorostiaga.model.Transaction;

@Data
public class Db {
    private HashMap<Long, Transaction> transactionsById;
    private HashMap<String, ArrayList<Transaction>> transactionsByType;
}
