package com.api.salarioFuncionario.controller;


import com.api.salarioFuncionario.dto.FuncionarioDTO;
import com.api.salarioFuncionario.model.FuncionarioEntity;
import com.api.salarioFuncionario.service.FuncionarioService;
//import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api")
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    //    @ApiOperation(value = "Cadastrar Funcionário", notes = "Endpoint para cadastrar um novo funcionário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Funcionário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    @PostMapping("/funcionario")
    public ResponseEntity getCadastrarFuncionario(@Valid @RequestBody FuncionarioDTO funcionarioDTO) {
        try {
            FuncionarioEntity funcionarioNovo = new FuncionarioEntity();
            funcionarioNovo.setNome(funcionarioDTO.getNome());
            funcionarioNovo.setCpf(funcionarioDTO.getCpf());
            funcionarioNovo.setDataDeNascimento(funcionarioDTO.getDataDeNascimento());
            funcionarioNovo.setTelefone(funcionarioDTO.getTelefone());
            funcionarioNovo.setEndereco(funcionarioDTO.getEndereco());
            funcionarioNovo.setSalario(funcionarioDTO.getSalario());
            this.funcionarioService.cadastrarFuncionario(funcionarioNovo);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }


    //    @ApiOperation(value = "Lista funcionários", notes = "Endpoint para listar funcionários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de funcionários"),
            @ApiResponse(responseCode = "204", description = "Sem funcionários cadastrados")
    })
    @GetMapping("/funcionarios")
    public ResponseEntity getFuncionarios() {
        List<FuncionarioEntity> funcionarioEntityList = this.funcionarioService.listarFuncionarios();

        return funcionarioEntityList.isEmpty() ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build() : ResponseEntity.status(HttpStatus.OK).body(funcionarioEntityList);
    }

    //    @ApiOperation(value = "Calcular novo salário do funcionário", notes = "Endpoint para calcular novo salário do funcionário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Salário calculado"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    @PostMapping("/calcularNovoSalario/{cpf}")
    public ResponseEntity<String> getCalcularNovoSalario(@PathVariable("cpf") String cpf) {
        Optional<FuncionarioEntity> optionalFuncionarioEntity = this.funcionarioService.consultarFuncionario(cpf);
        if (optionalFuncionarioEntity.isPresent()) {

            BigDecimal salarioAntigo = optionalFuncionarioEntity.get().getSalario();

            this.funcionarioService.calcularNovoSalario(cpf);
            BigDecimal reajuste = this.funcionarioService.gerarGanhoSalarial(salarioAntigo, optionalFuncionarioEntity.get().getSalario());
            BigDecimal percentual = this.funcionarioService.gerarPorcentagemDeGanhoSalario(salarioAntigo, optionalFuncionarioEntity.get().getSalario());

            optionalFuncionarioEntity = this.funcionarioService.consultarFuncionario(cpf);
            FuncionarioEntity funcionario = optionalFuncionarioEntity.get();
            return ResponseEntity.status(HttpStatus.OK).body(
                    "CPF: " + funcionario.getCpf() + "\n" +
                            "Novo salario: " + funcionario.getSalario() + "\n" +
                            "Reajuste ganho: " + reajuste + "\n" +
                            "Percentual: " + percentual + "\n"
            );
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //    @ApiOperation(value = "Calcular imposto do funcionário ", notes = "Endpoint para calcular imposto sobre o salário do funcionário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imposto calculado"),
            @ApiResponse(responseCode = "204", description = "Funcionário com cpf não encontrado")
    })
    @PostMapping("/calcularImposto/{cpf}")
    public ResponseEntity<String> getCalcularImposto(@PathVariable("cpf") String cpf) {
        Optional<FuncionarioEntity> optionalFuncionarioEntity = this.funcionarioService.consultarFuncionario(cpf);

        if (optionalFuncionarioEntity.isPresent()) {

            BigDecimal imposto = this.funcionarioService.calcularImpostoSobreSalario(cpf);
            FuncionarioEntity funcionario = optionalFuncionarioEntity.get();
            String impostoFormatado = this.funcionarioService.formataImpostoParaString(funcionario.getSalario(), imposto);
            return ResponseEntity.status(HttpStatus.OK).body(
                    "CPF: " + funcionario.getCpf() + "\n" +
                            "Imposto: " + impostoFormatado + "\n"
            );
        } else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
