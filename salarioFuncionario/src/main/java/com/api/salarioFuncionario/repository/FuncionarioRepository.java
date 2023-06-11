package com.api.salarioFuncionario.repository;

import com.api.salarioFuncionario.model.FuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface FuncionarioRepository extends JpaRepository<FuncionarioEntity,Long>{
    Optional<FuncionarioEntity> findByCpf(String cpf);
}
