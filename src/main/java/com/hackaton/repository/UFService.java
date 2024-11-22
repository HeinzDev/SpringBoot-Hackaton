package com.hackaton.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hackaton.model.UF;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UFService {
        @Autowired
    private UFRepository ufRepository;

    public UF findUFByCodigo(Long codigoUF) {
        return ufRepository.findById(codigoUF).orElseThrow(() -> new EntityNotFoundException("Bairro não encontrado"));
    }
}
