package com.delivery.controller;

import com.delivery.dto.PixRequestDTO;
import com.delivery.dto.PixResponseDTO;
import com.delivery.model.User;
import com.delivery.service.PaymentService;
import com.delivery.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentController.class)
public class PaymentControllerTest extends AbstractControllerTest {

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private SecurityService securityService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
    }

    @Test
    @WithMockUser
    void shouldGeneratePix() throws Exception {
        PixRequestDTO request = new PixRequestDTO(1L, BigDecimal.valueOf(100.00));
        PixResponseDTO response = new PixResponseDTO("qr", "copy", "tx123", "2026-12-31T23:59:59", BigDecimal.valueOf(100.00));

        when(securityService.getAuthenticatedUser()).thenReturn(user);
        when(paymentService.createPixPayment(eq(1L), eq(BigDecimal.valueOf(100.00)), any())).thenReturn(response);

        mockMvc.perform(post("/api/payments/pix/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
