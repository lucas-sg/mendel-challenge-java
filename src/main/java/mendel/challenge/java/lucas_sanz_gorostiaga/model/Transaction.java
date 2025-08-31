package mendel.challenge.java.lucas_sanz_gorostiaga.model;

import java.util.Optional;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;


@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private double amount;
    private String type;
    private Optional<Long> parentId;
}