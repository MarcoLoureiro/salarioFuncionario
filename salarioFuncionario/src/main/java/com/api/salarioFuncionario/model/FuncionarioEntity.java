package com.api.salarioFuncionario.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Table(name = "tb_funcionario")
public class FuncionarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cpf", unique = true)
    private String cpf;

    @Column(name = "dataDeNascimento")
    private LocalDateTime dataDeNascimento;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "endereco")
    private String endereco;

    @Column(name = "salario")
    private BigDecimal salario;
}
