package com.api.salarioFuncionario.service;

import com.api.salarioFuncionario.dto.FuncionarioDTO;
import com.api.salarioFuncionario.model.FuncionarioEntity;
import com.api.salarioFuncionario.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    public FuncionarioEntity cadastrarFuncionario(FuncionarioEntity funcionarioEntity) {
        return this.funcionarioRepository.saveAndFlush(funcionarioEntity);
    }

    public List<FuncionarioEntity> listarFuncionarios() {
        return this.funcionarioRepository.findAll();
    }

//    public FuncionarioEntity atualizarFuncionario(FuncionarioDTO funcionarioDTO) {
//        Optional<FuncionarioEntity> funcionarioEntity =
//                this.funcionarioRepository.findByCpf(funcionarioDTO.getCpf());
//        if (funcionarioEntity.isPresent()) {
//            FuncionarioEntity funcionarioAtualizado = new FuncionarioEntity();
//            funcionarioAtualizado.setId(funcionarioEntity.get().getId());
//            funcionarioAtualizado.setNome(funcionarioDTO.getNome());
//            funcionarioAtualizado.setCpf(funcionarioDTO.getCpf());
//            funcionarioAtualizado.setDataDeNascimento(funcionarioDTO.getDataDeNascimento());
//            funcionarioAtualizado.setTelefone(funcionarioDTO.getTelefone());
//            funcionarioAtualizado.setEndereco(funcionarioDTO.getEndereco());
//            funcionarioAtualizado.setSalario(funcionarioDTO.getSalario());
//            return this.funcionarioRepository.save(funcionarioAtualizado);
//        }
//        return null;
//    }
// Fora do escopo, devo colocar?

    //    public void deletarFuncionario(Long id) {
//        this.funcionarioRepository.deleteById(id);
//    }
// Fora do escopo, devo colocar?
    public Optional<FuncionarioEntity> consultarFuncionario(String cpf) {
        return this.funcionarioRepository.findByCpf(cpf);
    }

    public FuncionarioEntity calcularNovoSalario(String cpf) {
        Optional<FuncionarioEntity> funcionarioEntityOptional = this.funcionarioRepository.findByCpf(cpf);
        if (funcionarioEntityOptional.isPresent()) {
            FuncionarioEntity funcionarioAntigo = funcionarioEntityOptional.get();
            FuncionarioEntity funcionarioComSalarioAtualizado = new FuncionarioEntity();
            funcionarioComSalarioAtualizado.setId(funcionarioAntigo.getId());
            funcionarioComSalarioAtualizado.setNome(funcionarioAntigo.getNome());
            funcionarioComSalarioAtualizado.setCpf(funcionarioAntigo.getCpf());
            funcionarioComSalarioAtualizado.setDataDeNascimento(funcionarioAntigo.getDataDeNascimento());
            funcionarioComSalarioAtualizado.setTelefone(funcionarioAntigo.getTelefone());
            funcionarioComSalarioAtualizado.setEndereco(funcionarioAntigo.getEndereco());
            funcionarioComSalarioAtualizado.setSalario(calcularAumentoSalario(funcionarioAntigo.getSalario()));
            return this.funcionarioRepository.save(funcionarioComSalarioAtualizado);
        }
        return null;
    }


    public BigDecimal calcularImpostoSobreSalario(String cpf) {
        BigDecimal imposto = BigDecimal.ZERO;

        Optional<FuncionarioEntity> funcionarioEntityOptional = this.funcionarioRepository.findByCpf(cpf);
        if (funcionarioEntityOptional.isPresent()) {
            FuncionarioEntity funcionario = funcionarioEntityOptional.get();
            imposto = aplicarImpostoDeRenda(funcionario.getSalario());
        }
        return imposto;
    }


    private BigDecimal calcularAumentoSalario(BigDecimal salario) {

        BigDecimal salarioFinal = BigDecimal.ZERO;

        if (salario.compareTo(BigDecimal.valueOf(400.01)) <= -1) {

            salarioFinal = salario.add(salario.multiply(BigDecimal.valueOf(0.15)));

        } else if (salario.compareTo(BigDecimal.valueOf(800.01)) <= -1
                && salario.compareTo(BigDecimal.valueOf(400.00)) >= 1) {

            salarioFinal = salario.add(salario.multiply(BigDecimal.valueOf(0.12)));

        } else if (salario.compareTo(BigDecimal.valueOf(1200.01)) <= -1
                && salario.compareTo(BigDecimal.valueOf(800.00)) >= 1) {

            salarioFinal = salario.add(salario.multiply(BigDecimal.valueOf(0.10)));

        } else if (salario.compareTo(BigDecimal.valueOf(2000.01)) <= -1
                && salario.compareTo(BigDecimal.valueOf(1200.00)) >= 1) {

            salarioFinal = salario.add(salario.multiply(BigDecimal.valueOf(0.07)));

        } else if (salario.compareTo(BigDecimal.valueOf(2000.00)) >= 1) {

            salarioFinal = salario.add(salario.multiply(BigDecimal.valueOf(0.04)));

        }
        return salarioFinal.setScale(2, RoundingMode.HALF_EVEN);
    }


    public BigDecimal gerarPorcentagemDeGanhoSalario(BigDecimal salarioInicial, BigDecimal salarioAjustado) {

        if (salarioInicial.compareTo(BigDecimal.ZERO) >= 1) {
            BigDecimal diferenca = salarioAjustado.subtract(salarioInicial);
            return (diferenca.multiply(BigDecimal.valueOf(100.00))).divide(salarioInicial, RoundingMode.HALF_EVEN).setScale(2, RoundingMode.HALF_EVEN);
        } else
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
    }

    public BigDecimal gerarGanhoSalarial(BigDecimal salarioInicial, BigDecimal salarioAjustado) {
        return salarioAjustado.subtract(salarioInicial);
    }

    private BigDecimal aplicarImpostoDeRenda(BigDecimal salario) {

        BigDecimal impostoTotal = BigDecimal.ZERO;

        if (salario.compareTo(BigDecimal.valueOf(2000.00)) >= 1 &&
                salario.compareTo(BigDecimal.valueOf(3000.00)) <= -1) {

            BigDecimal valorTaxadoAcimaDaIsencao = salario.subtract(BigDecimal.valueOf(2000.00));
            valorTaxadoAcimaDaIsencao = valorTaxadoAcimaDaIsencao.multiply(BigDecimal.valueOf(0.08));
            impostoTotal = impostoTotal.add(valorTaxadoAcimaDaIsencao);

        } else {
            BigDecimal valorTaxadoAcimaDaIsencao = BigDecimal.valueOf(1000.00);
            valorTaxadoAcimaDaIsencao = valorTaxadoAcimaDaIsencao.multiply(BigDecimal.valueOf(0.08));
            impostoTotal = impostoTotal.add(valorTaxadoAcimaDaIsencao);
        }

        if (salario.compareTo(BigDecimal.valueOf(3000.00)) >= 1 &&
                salario.compareTo(BigDecimal.valueOf(4500.01)) <= -1) {

            BigDecimal valorTaxadoAcimaDaIsencao = salario.subtract(BigDecimal.valueOf(3000.00));
            valorTaxadoAcimaDaIsencao = valorTaxadoAcimaDaIsencao.multiply(BigDecimal.valueOf(0.18));
            impostoTotal = impostoTotal.add(valorTaxadoAcimaDaIsencao);

        } else {
            BigDecimal valorTaxadoAcimaDaIsencao = BigDecimal.valueOf(1500.00);
            valorTaxadoAcimaDaIsencao = valorTaxadoAcimaDaIsencao.multiply(BigDecimal.valueOf(0.18));
            impostoTotal = impostoTotal.add(valorTaxadoAcimaDaIsencao);
        }

        if (salario.compareTo(BigDecimal.valueOf(4500.00)) >= 1) {

            BigDecimal valorTaxadoAcimaDaIsencao = salario.subtract(BigDecimal.valueOf(4500.00));
            valorTaxadoAcimaDaIsencao = valorTaxadoAcimaDaIsencao.multiply(BigDecimal.valueOf(0.28));
            impostoTotal = impostoTotal.add(valorTaxadoAcimaDaIsencao);

        }
        return impostoTotal.setScale(2, RoundingMode.HALF_EVEN);
    }

    public String formataImpostoParaString(BigDecimal salario, BigDecimal imposto) {
        return salario.compareTo(BigDecimal.valueOf(2000.01)) <= -1 ? "Isento" : "R$ " + imposto;
    }

}
