package com.delivery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"user"})
@Entity
@Table(name = "establishments")
public class Establishment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do estabelecimento e obrigatorio")
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
