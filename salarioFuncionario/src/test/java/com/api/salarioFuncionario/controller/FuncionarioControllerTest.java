package com.api.salarioFuncionario.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.api.salarioFuncionario.dto.FuncionarioDTO;
import com.api.salarioFuncionario.model.FuncionarioEntity;
import com.api.salarioFuncionario.service.FuncionarioService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.*;


@SpringBootTest
public class FuncionarioControllerTest {
    @Mock
    private FuncionarioService funcionarioService;

    @InjectMocks
    private FuncionarioController funcionarioController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Testando cadastro de funcionario com DTO válido, retornando created")
    public void testGetCadastrarFuncionarioComDtoValido() {
        FuncionarioDTO funcionarioDTO = new FuncionarioDTO();
        funcionarioDTO.setNome("Marco");
        funcionarioDTO.setCpf("123456789");

        ResponseEntity response = funcionarioController.getCadastrarFuncionario(funcionarioDTO);

        verify(funcionarioService, times(1)).cadastrarFuncionario(any(FuncionarioEntity.class));
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    @DisplayName("Testando cadastro de funcionario com DTO inválido, retornando bad request")
    public void testGetCadastrarFuncionarioComDtoInvalido() {
        FuncionarioDTO funcionarioDTO = new FuncionarioDTO();

        Set<ConstraintViolation<FuncionarioDTO>> violations = new HashSet<>();
        violations.add(mock(ConstraintViolation.class));

        ConstraintViolationException exception = new ConstraintViolationException(violations);

        doThrow(exception).when(funcionarioService).cadastrarFuncionario(any(FuncionarioEntity.class));

        ResponseEntity response = funcionarioController.getCadastrarFuncionario(funcionarioDTO);

        verify(funcionarioService, times(1)).cadastrarFuncionario(any(FuncionarioEntity.class));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    @DisplayName("Testando listagem de funcionarios sem funcionários cadastrados, deve retornar NO_CONTENT sem nada no body")
    public void testGetFuncionariosSemFuncionariosCadastrados() {
        List<FuncionarioEntity> emptyList = new ArrayList<>();
        when(funcionarioService.listarFuncionarios()).thenReturn(emptyList);

        ResponseEntity response = funcionarioController.getFuncionarios();

        verify(funcionarioService, times(1)).listarFuncionarios();
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    @DisplayName("Testando listagem de funcionarios com funcionários cadastrados, deve retornar OK e a listagem de funcionários")
    public void testGetFuncionariosComFuncionariosCadastrados() {
        List<FuncionarioEntity> funcionarioList = new ArrayList<>();
        funcionarioList.add(new FuncionarioEntity());
        funcionarioList.add(new FuncionarioEntity());
        when(funcionarioService.listarFuncionarios()).thenReturn(funcionarioList);

        ResponseEntity response = funcionarioController.getFuncionarios();

        verify(funcionarioService, times(1)).listarFuncionarios();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(funcionarioList, response.getBody());
    }


    @Test
    @DisplayName("Testando calculo de salario com funcionario, " +
            "deve retornar OK e os dados a saída esperada com CPF, Novo salario, Reajuste ganho e Percentual")
    public void testGetCalcularNovoSalarioComFuncionarioExistente() {
        String cpf = "1234567890";
        BigDecimal salarioAntigo = new BigDecimal("5000.00");
        BigDecimal novoSalario = new BigDecimal("6000.00");
        BigDecimal reajusteEsperado = new BigDecimal("1000.00");
        BigDecimal percentualEsperado = new BigDecimal("20.0");

        FuncionarioEntity funcionarioEntity = new FuncionarioEntity();
        funcionarioEntity.setCpf(cpf);
        funcionarioEntity.setSalario(salarioAntigo);
        when(funcionarioService.consultarFuncionario(cpf)).thenReturn(Optional.of(funcionarioEntity));
        doAnswer(invocation -> {
            funcionarioEntity.setSalario(novoSalario);
            return null;
        }).when(funcionarioService).calcularNovoSalario(cpf);
        when(funcionarioService.gerarGanhoSalarial(salarioAntigo, novoSalario)).thenReturn(reajusteEsperado);
        when(funcionarioService.gerarPorcentagemDeGanhoSalario(salarioAntigo, novoSalario)).thenReturn(percentualEsperado);

        ResponseEntity<String> response = funcionarioController.getCalcularNovoSalario(cpf);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("CPF: " + cpf + "\n" +
                "Novo salario: " + novoSalario + "\n" +
                "Reajuste ganho: " + reajusteEsperado + "\n" +
                "Percentual: " + percentualEsperado + "\n", response.getBody());

        verify(funcionarioService, times(1)).calcularNovoSalario(cpf);
        verify(funcionarioService, times(1)).gerarGanhoSalarial(salarioAntigo, novoSalario);
        verify(funcionarioService, times(1)).gerarPorcentagemDeGanhoSalario(salarioAntigo, novoSalario);

    }



    @Test
    @DisplayName("Testando calculo de salario sem funcionario existente, " +
            "deve retornar NO_CONTENT e nenhum dado no body")
    public void testGetCalcularNovoSalarioSemFuncionarioExistente() {
        String cpf = "123456789";
        Optional<FuncionarioEntity> optionalFuncionarioEntity = Optional.empty();
        when(funcionarioService.consultarFuncionario(cpf)).thenReturn(optionalFuncionarioEntity);

        ResponseEntity response = funcionarioController.getCalcularNovoSalario(cpf);

        verify(funcionarioService, times(1)).consultarFuncionario(cpf);
        verify(funcionarioService, never()).calcularNovoSalario(cpf);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }


    @Test
    @DisplayName("Teste calculando imposto de funcionário, deve retornar OK e mostrar a saída CPF e Imposto")
    public void testGetCalcularImpostoComFuncionarioExistente() {
        String cpf = "123456789";
        BigDecimal imposto = new BigDecimal("1000");
        FuncionarioEntity funcionario = new FuncionarioEntity();
        funcionario.setCpf(cpf);
        funcionario.setSalario(new BigDecimal("5000"));

        when(funcionarioService.consultarFuncionario(cpf)).thenReturn(Optional.of(funcionario));
        when(funcionarioService.calcularImpostoSobreSalario(cpf)).thenReturn(imposto);
        when(funcionarioService.formataImpostoParaString(funcionario.getSalario(), imposto)).thenReturn("R$ 1.000,00");

        ResponseEntity<String> response = funcionarioController.getCalcularImposto(cpf);

        verify(funcionarioService, times(1)).consultarFuncionario(cpf);
        verify(funcionarioService, times(1)).calcularImpostoSobreSalario(cpf);
        verify(funcionarioService, times(1)).formataImpostoParaString(funcionario.getSalario(), imposto);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("CPF: " + funcionario.getCpf() + "\nImposto: R$ 1.000,00\n", response.getBody());
    }

    @Test
    @DisplayName("Teste calculando imposto de funcionário inexistente, deve retornar NO CONTENT " +
            "e não mostrar dado")
    public void testGetCalcularImpostoSemFuncionarioExistente() {
        String cpf = "123456789";

        when(funcionarioService.consultarFuncionario(cpf)).thenReturn(Optional.empty());

        ResponseEntity<String> response = funcionarioController.getCalcularImposto(cpf);

        verify(funcionarioService, times(1)).consultarFuncionario(cpf);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }


}