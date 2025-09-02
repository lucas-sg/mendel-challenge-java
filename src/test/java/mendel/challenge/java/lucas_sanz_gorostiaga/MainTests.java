package mendel.challenge.java.lucas_sanz_gorostiaga;

import mendel.challenge.java.lucas_sanz_gorostiaga.controller.TransactionBody;

import mendel.challenge.java.lucas_sanz_gorostiaga.controller.TransactionDescendantsSumResponse;
import mendel.challenge.java.lucas_sanz_gorostiaga.model.Transaction;
import mendel.challenge.java.lucas_sanz_gorostiaga.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MainTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TransactionRepository repository;

	@Test
	void putTransactionWithoutParent() {
        // Given
        Long transactionId = 10L;
        String transactionType = "cars";
        Double transactionAmount = 5000.0;
        TransactionBody body = TransactionBody.builder()
                .amount(transactionAmount)
                .type(transactionType)
                .build();
        when(repository.getTransactionsById()).thenReturn(new ConcurrentHashMap<>());
        when(repository.getTransactionIdsByType()).thenReturn(new ConcurrentHashMap<>());

        // When
        RequestEntity<TransactionBody> requestEntity = new RequestEntity<>(
                body,
                HttpMethod.PUT,
                URI.create("/transactions/" + transactionId)
        );
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        // Then
        // ? assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(String.class);

        Transaction transaction = repository.getTransactionsById().get(transactionId);
        assertThat(transaction.getParent()).isNull();
        assertThat(transaction.getAmount()).isEqualTo(transactionAmount);
        assertThat(transaction.getType()).isEqualTo(transactionType);
        assertThat(transaction.getId()).isEqualTo(transactionId);
	}

    @Test
    void putTransactionWithParent() {
        // Given
        long transactionId = 2L;
        long parentId = 1L;
        String transactionType = "cars";
        double transactionAmount = 5000.0;
        TransactionBody body = TransactionBody.builder()
                .parentId(parentId)
                .amount(transactionAmount)
                .type(transactionType)
                .build();

        String parentType = "planes";
        Transaction mockedParent = Transaction.builder()
                .id(parentId)
                .descendantsSum(0.0)
                .build();
        Map<Long, Transaction> mockedMap = new ConcurrentHashMap<>(Map.of(parentId, mockedParent));
        when(repository.getTransactionsById()).thenReturn(mockedMap);
        when(repository.getTransactionIdsByType()).thenReturn(new ConcurrentHashMap<>(Map.of(parentType, List.of(parentId))));

        // When
        RequestEntity<TransactionBody> requestEntity = new RequestEntity<>(
                body,
                HttpMethod.PUT,
                URI.create("/transactions/" + transactionId)
        );
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);


        // Then
        // ? assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isInstanceOf(String.class);

        Transaction transaction = repository.getTransactionsById().get(transactionId);
        assertThat(transaction.getParent()).isNotNull();
        assertThat(transaction.getParent().getId()).isEqualTo(parentId);
        assertThat(transaction.getAmount()).isEqualTo(transactionAmount);
        assertThat(transaction.getType()).isEqualTo(transactionType);
        assertThat(transaction.getId()).isEqualTo(transactionId);
    }

    @Test
    void putExistingTransaction() {
        // Given
        long transactionId = 10L;
        String transactionType = "cars";
        Double transactionAmount = 5000.0;
        TransactionBody body = TransactionBody.builder()
                .amount(transactionAmount)
                .type(transactionType)
                .build();

        Transaction mockedTransaction = Transaction.builder().id(transactionId).build();
        Map<Long, Transaction> mockedMap = new ConcurrentHashMap<>(Map.of(transactionId, mockedTransaction));
        when(repository.getTransactionsById()).thenReturn(mockedMap);
        when(repository.getTransactionIdsByType()).thenReturn(new ConcurrentHashMap<>(Map.of(transactionType, List.of(transactionId))));

        // When
        RequestEntity<TransactionBody> requestEntity = new RequestEntity<>(
                body,
                HttpMethod.PUT,
                URI.create("/transactions/" + transactionId)
        );
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void getTransactionIdsByType() {
        // Given
        long transactionId1 = 1L;
        String carsType = "cars";

        long transactionId2 = 2L;
        String planesType = "planes";
        long transactionId3 = 3L;

        Map<String, List<Long>> mockedMap = Map.of(
                carsType, List.of(transactionId1),
                planesType, List.of(transactionId2, transactionId3)
        );

        // When
        when(repository.getTransactionIdsByType()).thenReturn(mockedMap);
        RequestEntity<Void> requestEntity = new RequestEntity<>(
                HttpMethod.GET,
                URI.create("/transactions/types/" + planesType
        ));
        ResponseEntity<List<Long>> response = restTemplate.exchange(
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(transactionId2, transactionId3);
    }

    @Test
    void getTransactionIdsByNonExistentType() {
        // Given
        long transactionId = 1L;
        String carsType = "cars";
        String nonExistentType = "somethingMadeUp";

        Map<String, List<Long>> mockedMap = Map.of(carsType, List.of(transactionId));

        // When
        when(repository.getTransactionIdsByType()).thenReturn(mockedMap);
        RequestEntity<Void> requestEntity = new RequestEntity<>(
                HttpMethod.GET,
                URI.create("/transactions/types/" + nonExistentType
        ));
        ResponseEntity<List<Long>> response = restTemplate.exchange(
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getTransactionDescendantsSum() {
        // Given
        long transactionId = 1L;
        double sum = 15.0;
        Transaction transaction = Transaction.builder()
                .id(transactionId)
                .descendantsSum(sum)
                .build();
        Map<Long, Transaction> mockedMap = Map.of(transactionId, transaction);

        // When
        when(repository.getTransactionsById()).thenReturn(mockedMap);
        RequestEntity<Void> requestEntity = new RequestEntity<>(
                HttpMethod.GET,
                URI.create("/transactions/sum/" + transactionId
        ));
        ResponseEntity<TransactionDescendantsSumResponse> response = restTemplate.exchange(
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSum()).isEqualTo(sum);
    }

    @Test
    void getTransactionDescendantsSumOfNonExistentTransactionId() {
        // Given
        Map<Long, Transaction> mockedMap = Map.of();

        // When
        when(repository.getTransactionsById()).thenReturn(mockedMap);
        RequestEntity<Void> requestEntity = new RequestEntity<>(
                HttpMethod.GET,
                URI.create("/transactions/sum/1" // There aren't any transactions with this ID
                ));
        ResponseEntity<TransactionDescendantsSumResponse> response = restTemplate.exchange(
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSum()).isEqualTo(0.0);
    }

    @TestConfiguration
    static class MockTransactionRepositoryConfig {

        @Bean
        public TransactionRepository transactionRepository() {
            // Create and return a mock instance of the CarRepository
            return mock(TransactionRepository.class);
        }
    }
}
