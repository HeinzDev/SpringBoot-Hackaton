package com.hackaton.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hackaton.model.Bairro;

@Service
public class BairroService {
    @Autowired
    private BairroRepository bairroRepository;

    public Bairro findBairroByCodigo(Long codigoBairro) {
        return bairroRepository.findByCodigoBairro(codigoBairro).stream().findFirst().orElse(null);
    }    
}
