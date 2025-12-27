package com.delivery.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA
public class Email {

    // Regex mais robusto para garantir usuario@dominio.tld
    private static final String REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    @Column(name = "email", unique = true)
    private String address;

    public Email(String address) {
        if (address == null || !isValid(address)) {
            throw new IllegalArgumentException("E-mail inválido");
        }
        this.address = address.toLowerCase();
    }

    private boolean isValid(String email) {
        return PATTERN.matcher(email).matches();
    }

    @Override
    public String toString() {
        return address;
    }
}