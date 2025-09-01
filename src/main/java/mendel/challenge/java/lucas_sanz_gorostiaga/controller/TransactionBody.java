package mendel.challenge.java.lucas_sanz_gorostiaga.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class TransactionBody {
    @NotNull
    @Min(1)
    public Double amount;

    @Nullable
    public Long parentId;

    @NotNull
    public String type;
}
