package com.hackaton.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hackaton.model.Endereco;
import jakarta.persistence.EntityNotFoundException;

@Service
public class EnderecoService {
    
    @Autowired
    private EnderecoRepository enderecoRepository;

    public List<Endereco> findByPessoaId(Long pessoaId) {
        return enderecoRepository.findByCodigoPessoa(pessoaId);
    }

    public Endereco save(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    public Endereco findById(Long id) {
        return enderecoRepository.findById(id).orElseThrow(()->new EntityNotFoundException("endereço não encontrado"));
    }
    public void delete(Endereco endereco) {
        enderecoRepository.deleteById(endereco.getCodigoEndereco());
    }
}

