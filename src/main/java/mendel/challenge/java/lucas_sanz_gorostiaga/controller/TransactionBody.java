package mendel.challenge.java.lucas_sanz_gorostiaga.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;


@Data
@Builder
public class TransactionBody {
    @NotNull
    @Min(1)
    public Double amount;

    @Nullable
    @JsonProperty("parent_id")
    public Long parentId;

    @NotNull
    public String type;
}
