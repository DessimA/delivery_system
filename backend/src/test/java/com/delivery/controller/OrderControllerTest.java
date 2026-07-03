package com.delivery.controller;

import com.delivery.dto.OrderItemRequestDTO;
import com.delivery.dto.OrderRequestDTO;
import com.delivery.dto.OrderResponseDTO;
import com.delivery.model.OrderStatus;
import com.delivery.model.User;
import java.math.BigDecimal;
import com.delivery.service.OrderService;
import com.delivery.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class)
public class OrderControllerTest extends AbstractControllerTest {

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private SecurityService securityService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
    }

    @Test
    @WithMockUser(username = "user@test.com")
    public void deveCriarPedidoComSucesso() throws Exception {
        OrderRequestDTO pedidoRequest = new OrderRequestDTO("Rua Teste", List.of(new OrderItemRequestDTO(1L, 1), new OrderItemRequestDTO(2L, 1)));

        OrderResponseDTO responseDTO = new OrderResponseDTO(
            1L, 1L, "Rua Teste", BigDecimal.valueOf(5.0), OrderStatus.WAITING_PAYMENT, null, Collections.emptyList(), BigDecimal.valueOf(45.0), null
        );

        when(securityService.getAuthenticatedUser()).thenReturn(user);
        when(orderService.createOrder(any(OrderRequestDTO.class), eq(1L))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedidoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
