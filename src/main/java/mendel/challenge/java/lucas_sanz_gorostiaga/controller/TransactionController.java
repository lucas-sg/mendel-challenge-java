package mendel.challenge.java.lucas_sanz_gorostiaga.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mendel.challenge.java.lucas_sanz_gorostiaga.service.TransactionService;


@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<String> putTransaction(@Valid @RequestBody TransactionBody body) {
        boolean successfullyInserted = transactionService.insertTransaction(body);

        if (!successfullyInserted) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    @GetMapping("/types/{type}")
    public List<Long> getTransactionsByType(@PathVariable String type) {
        return transactionService.getTransactionIdsByType(type).orElse(new ArrayList<>());
    }

    @GetMapping("/sum/{transactionId}")
    public ResponseEntity<TransactionDescendantsSumResponse> getDescendantsSum(@PathVariable Long transactionId) {
        Double sum = transactionService.getTransactionDescendantsSum(transactionId);
        TransactionDescendantsSumResponse response = new TransactionDescendantsSumResponse();
        response.setSum(sum);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
