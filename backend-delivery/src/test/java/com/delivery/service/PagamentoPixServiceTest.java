package com.delivery.service;

import com.google.zxing.WriterException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PagamentoPixServiceTest {

    @InjectMocks
    private PagamentoPixService pagamentoPixService;

    @Test
    void gerarQRCodePix_deveRetornarStringBase64() throws WriterException, IOException {
        Long pedidoId = 123L;
        double valor = 100.50;

        String qrCodeBase64 = pagamentoPixService.gerarQRCodePix(pedidoId, valor);

        assertNotNull(qrCodeBase64);
        assertTrue(qrCodeBase64.length() > 0);
        assertTrue(qrCodeBase64.startsWith("iVBORw0KGgo")); // PNG Base64 starts with this
    }
}
