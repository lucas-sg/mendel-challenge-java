package mendel.challenge.java.lucas_sanz_gorostiaga;

import mendel.challenge.java.lucas_sanz_gorostiaga.controller.TransactionBody;

import mendel.challenge.java.lucas_sanz_gorostiaga.model.Transaction;
import mendel.challenge.java.lucas_sanz_gorostiaga.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MainTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TransactionRepository repository;

	@Test
	void putTransaction() {
        // Given
        Long transactionId = 10L;
        String transactionType = "cars";
        Double transactionAmount = 5000.0;
        TransactionBody body = new TransactionBody();
        body.setAmount(transactionAmount);
        body.setType(transactionType);

        // When
        RequestEntity<TransactionBody> requestEntity = new RequestEntity<>(body, HttpMethod.PUT, URI.create("/transactions/" + transactionId));
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);


        // Then
        // ? assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(String.class);

        Transaction transaction = repository.getTransactionsById().get(transactionId);
        assertThat(transaction.getParent()).isEmpty();
        assertThat(transaction.getAmount()).isEqualTo(transactionAmount);
        assertThat(transaction.getType()).isEqualTo(transactionType);
        assertThat(transaction.getId()).isEqualTo(transactionId);
	}
}
