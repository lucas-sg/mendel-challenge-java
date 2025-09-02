package mendel.challenge.java.lucas_sanz_gorostiaga.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mendel.challenge.java.lucas_sanz_gorostiaga.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService service;


    @Test
    void putTransactionSuccessfully() throws Exception {
        // Given
        Long transactionId = 1L;
        String transactionType = "cars";
        Double amount = 25.0;
        TransactionBody body = TransactionBody.builder()
                .amount(amount)
                .type(transactionType)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(body);
        when(service.insertTransaction(body, transactionId)).thenReturn(true);

        // When
        mockMvc.perform(
                put("/transactions/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))

                // Then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isString());
    }

    @Test
    void failPuttingTransaction() throws Exception {
        // Given
        Long transactionId = 1L;
        String transactionType = "cars";
        Double amount = 25.0;
        TransactionBody body = TransactionBody.builder()
                .amount(amount)
                .type(transactionType)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(body);
        when(service.insertTransaction(body, transactionId)).thenReturn(false);

        // When
        mockMvc.perform(
                        put("/transactions/{transactionId}", transactionId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody))

                // Then
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getTransactionIdsByType() throws Exception {
        // Given
        String transactionType = "cars";
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(service.getTransactionIdsByType(transactionType)).thenReturn(ids);

        // When
        mockMvc.perform(get("/transactions/types/{transactionType}", transactionType))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value(1L))
                .andExpect(jsonPath("$[1]").value(2L))
                .andExpect(jsonPath("$[2]").value(3L));
    }

    @Test
    void getTransactionsDescendantsSum() throws Exception {
        // Given
        Long transactionId = 1L;
        Double sum = 25.0;
        when(service.getTransactionDescendantsSum(transactionId)).thenReturn(sum);

        // When
        mockMvc.perform(get("/transactions/sum/{transactionId}", transactionId))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(sum));
    }
}
