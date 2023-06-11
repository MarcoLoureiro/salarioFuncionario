package com.api.salarioFuncionario.dto;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.stereotype.Component;

import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class FuncionarioDTO {

    @Schema(description = "Nome do funcionário", example = "Marco")
    @Length(min = 1,message = "Nome precisa conter no mínimo 1 caractere")
    private String nome;

    @Schema(description = "CPF do funcionário", example = "12345678900")
    @NotBlank(message = "CPF não pode ser vazio")
    private String cpf;

    @Schema(description = "Data de nascimento", example = "1997-05-28T00:00:00")
    @NotNull
    private LocalDateTime dataDeNascimento;

    @Schema(description = "Telefone do funcionário",example = "91984122827")
    @Length(min = 8)
    private String telefone;

    @Schema(description = "Endereço do sujeito", example = "Rua top")
    @Length(min = 5)
    private String endereco;

    @Schema(description = "Salário do funcionário", example = "2000")
    @NotNull(message = "Salário não pode ser nulo")
    private BigDecimal salario;
}
