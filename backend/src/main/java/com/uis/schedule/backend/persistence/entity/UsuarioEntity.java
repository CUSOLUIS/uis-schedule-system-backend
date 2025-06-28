package com.uis.schedule.backend.persistence.entity;


import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "activo")
    private boolean activo;

    @Column(name = "cuenta_no_expirada")
    private boolean cuentaNoExpirada;

    @Column(name = "cuenta_no_bloqueada")
    private boolean cuentaNoBloqueada;

    @Column(name = "credenciales_no_expiradas")
    private boolean credencialesNoExpiradas;


}

