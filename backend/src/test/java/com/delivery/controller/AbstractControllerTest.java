package com.delivery.controller;

import com.delivery.config.SecurityConfig;
import com.delivery.security.JwtAuthenticationEntryPoint;
import com.delivery.security.JwtAuthenticationFilter;
import com.delivery.security.JwtTokenProvider;
import com.delivery.security.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
@TestPropertySource(properties = {
    "app.jwt.secret=ZGV2U2VjcmV0S2V5Rm9ySldUU2lnbmluZ1B1cnBvc2VzMTIzNDU2Nzg5MA==",
    "app.jwt.expiration-ms=3600000",
    "frontend.url=http://localhost:5173",
    "app.cors.allowed-origins=http://localhost:5173"
})
public abstract class AbstractControllerTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired(required = false)
    protected MockMvc mockMvc;

    @Autowired(required = false)
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    protected CustomUserDetailsService userDetailsService;

    @MockitoBean
    protected JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
}
