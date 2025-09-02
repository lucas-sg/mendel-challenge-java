package mendel.challenge.java.lucas_sanz_gorostiaga.model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class Transaction {
    private long id;
    private double amount;
    private String type;
    private Transaction parent;
    private Double descendantsSum;
}