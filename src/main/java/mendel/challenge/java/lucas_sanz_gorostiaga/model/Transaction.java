package mendel.challenge.java.lucas_sanz_gorostiaga.model;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class Transaction {
    @Id
    private long id;
    private double amount;
    private String type;
    private Transaction parent;
    private Double descendantsSum;
}