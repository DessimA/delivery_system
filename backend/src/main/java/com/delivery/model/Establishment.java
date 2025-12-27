package com.delivery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "establishments")
public class Establishment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do estabelecimento é obrigatório")
    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String cnpj;

    private String address;

    private String phone;

    @Builder.Default
    private boolean active = true;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
