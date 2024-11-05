package com.hackaton.repository;

import org.springframework.data.repository.CrudRepository;

import com.hackaton.model.Bairro;

public interface BairroRepository extends CrudRepository<Bairro, Long>{
    
}
