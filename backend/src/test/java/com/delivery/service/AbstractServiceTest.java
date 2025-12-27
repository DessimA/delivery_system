package com.delivery.service;

import com.delivery.model.User;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractServiceTest {

    @Mock
    protected SecurityService securityService;

    protected void mockAuthenticatedUser(User user) {
        lenient().when(securityService.getAuthenticatedUser()).thenReturn(user);
    }

    protected void mockIsAdmin(User user, boolean isAdmin) {
        lenient().when(securityService.isAdmin(user)).thenReturn(isAdmin);
    }

    protected void mockVerifyRole(String role) {
        // No-op for mock, securityService.verifyRole returns void or throws
    }
}
