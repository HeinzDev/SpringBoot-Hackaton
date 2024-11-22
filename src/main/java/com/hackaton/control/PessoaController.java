package com.hackaton.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackaton.model.Bairro;
import com.hackaton.model.Endereco;
import com.hackaton.model.Municipio;
import com.hackaton.model.Pessoa;
import com.hackaton.model.UF;
import com.hackaton.repository.BairroService;
import com.hackaton.repository.EnderecoService;
import com.hackaton.repository.MunicipioService;
import com.hackaton.repository.PessoaRepository;
import com.hackaton.repository.UFService;

import jakarta.transaction.Transactional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@CrossOrigin(origins = "*")
public class PessoaController extends ControllerSupport {

    @Autowired
    private PessoaRepository action;

    @Autowired
    private EnderecoService enderecoService;
    private BairroService bairroService;
    private UFService ufService;
    private MunicipioService municipioService;


    @CrossOrigin(origins = "*")
    @GetMapping("/all")
    public Iterable<Pessoa> getPessoas() {
        return action.findAll();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/pessoa")
    public ResponseEntity<Object> getPessoaByParams(@RequestParam(required = false) String codigoPessoa,
                                                    @RequestParam(required = false) String status,
                                                    @RequestParam(required = false) String login) {
        if (codigoPessoa != null && !isNumeric(codigoPessoa)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O valor inserido para códigoPessoa não é um número válido.", 400));
        }
        if (status != null && !isNumeric(status)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O valor inserido para status não é um número válido.", 400));
        }
    
        Long codigoPessoaNumber = codigoPessoa != null ? Long.parseLong(codigoPessoa) : null;
        Long statusNumber = status != null ? Long.parseLong(status) : null;
    
        List<Pessoa> results = new ArrayList<>();
        if (codigoPessoaNumber != null) results.addAll(action.findByCodigoPessoa(codigoPessoaNumber));
        if (login != null) results.addAll(action.findByLogin(login));
    
        results = results.stream()
                .filter(pessoa -> (statusNumber == null || pessoa.getStatus().equals(statusNumber)) &&
                        (codigoPessoaNumber == null || pessoa.getCodigoPessoa().equals(codigoPessoaNumber)) &&
                        (login == null || pessoa.getLogin().equals(login)))
                .distinct()
                .collect(Collectors.toList());
    
        if (!results.isEmpty()) {
            Pessoa pessoa = results.get(0);
            List<Endereco> enderecos = enderecoService.findByPessoaId(pessoa.getCodigoPessoa());
    
            // Preenchendo os endereços com informações adicionais de Bairro, Município e UF
            List<Map<String, Object>> enderecosCompleto = enderecos.stream().map(endereco -> {
                Map<String, Object> enderecoMap = new HashMap<>();
                enderecoMap.put("codigoEndereco", endereco.getCodigoEndereco());
                enderecoMap.put("codigoPessoa", endereco.getCodigoPessoa());
                enderecoMap.put("nomeRua", endereco.getNomeRua());
                enderecoMap.put("numero", endereco.getNumero());
                enderecoMap.put("complemento", endereco.getComplemento());
                enderecoMap.put("cep", endereco.getCep());
            
                // Buscar o Bairro manualmente
                Bairro bairro = bairroService.findBairroByCodigo(endereco.getCodigoBairro());
                Map<String, Object> bairroMap = new HashMap<>();
                bairroMap.put("codigoBairro", bairro.getCodigoBairro());
                bairroMap.put("codigoMunicipio", bairro.getCodigoMunicipio());
                bairroMap.put("nome", bairro.getNome());
            
                // Buscar o Município manualmente
                Municipio municipio = municipioService.findMunicipioByCodigo(bairro.getCodigoMunicipio());
                Map<String, Object> municipioMap = new HashMap<>();
                municipioMap.put("codigoMunicipio", municipio.getCodigoMunicipio());
                municipioMap.put("codigoUF", municipio.getCodigoUF());
                municipioMap.put("nome", municipio.getNome());
            
                // Buscar o UF manualmente
                UF uf = ufService.findUFByCodigo(municipio.getCodigoUF());
                Map<String, Object> ufMap = new HashMap<>();
                ufMap.put("codigoUF", uf.getCodigoUF());
                ufMap.put("sigla", uf.getSigla());
                ufMap.put("nome", uf.getNome());
            
                municipioMap.put("uf", ufMap); // Associando a UF ao município
                bairroMap.put("municipio", municipioMap); // Associando o município ao bairro
                enderecoMap.put("bairro", bairroMap); // Associando o bairro ao endereço
            
                return enderecoMap;
            }).collect(Collectors.toList());
    
            Map<String, Object> pessoaMap = new HashMap<>();
            pessoaMap.put("codigoPessoa", pessoa.getCodigoPessoa());
            pessoaMap.put("nome", pessoa.getNome());
            pessoaMap.put("sobrenome", pessoa.getSobrenome());
            pessoaMap.put("idade", pessoa.getIdade());
            pessoaMap.put("login", pessoa.getLogin());
            pessoaMap.put("senha", pessoa.getSenha());
            pessoaMap.put("status", pessoa.getStatus());
            pessoaMap.put("enderecos", enderecosCompleto);
    
            return ResponseEntity.ok(Collections.singletonList(pessoaMap));
        }
    
        return ResponseEntity.ok(Collections.emptyList());
    }
    

    @PostMapping("/pessoa")
    public ResponseEntity<Object> createPessoa(@RequestBody Pessoa pessoa) {
        if (pessoa.getNome() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo nome é obrigatório.", 400));
        }
        if (pessoa.getLogin() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo login é obrigatório.", 400));
        }
        if (pessoa.getStatus() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo status é obrigatório.", 400));
        }

        List<Pessoa> loginExists = action.findByLogin(pessoa.getLogin());
        if (!loginExists.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("Já existe uma pessoa com esse login no banco de dados.", 400));
        }

        action.save(pessoa);
        Iterable<Pessoa> allPessoas = action.findAll();
        return ResponseEntity.ok(allPessoas);
    }

    @PutMapping("/pessoa")
    public ResponseEntity<Object> editPessoa(@RequestBody Pessoa pessoa) {
        if (pessoa.getCodigoPessoa() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo codigoPessoa é obrigatório", 400));
        }
        if (pessoa.getNome() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo nome é obrigatório", 400));
        }
        if (pessoa.getLogin() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo login é obrigatório", 400));
        }
        if (pessoa.getStatus() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo status é obrigatório", 400));
        }

        List<Pessoa> targetPessoaList = action.findByCodigoPessoa(pessoa.getCodigoPessoa());
        if (targetPessoaList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse("Não foi encontrado nenhum Pessoa com esse código.", 404));
        }

        Pessoa targetPessoa = targetPessoaList.get(0);

        List<Pessoa> loginExists = action.findByLogin(pessoa.getLogin());
        if (!loginExists.isEmpty() && !loginExists.get(0).getCodigoPessoa().equals(targetPessoa.getCodigoPessoa())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("Já existe uma pessoa com esse login no banco de dados.", 400));
        }

        targetPessoa.setNome(pessoa.getNome());
        targetPessoa.setSobrenome(pessoa.getSobrenome());
        targetPessoa.setIdade(pessoa.getIdade());
        targetPessoa.setLogin(pessoa.getLogin());
        targetPessoa.setSenha(pessoa.getSenha());
        targetPessoa.setStatus(pessoa.getStatus());
        action.save(targetPessoa);

        Iterable<Pessoa> allPessoas = action.findAll();
        return ResponseEntity.ok(allPessoas);
    }

    @DeleteMapping("/pessoa")
    public ResponseEntity<Object> deletePessoa(@RequestParam(required = false) String code) {
        if (code == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo code é obrigatório.", 400));
        }
        if (code != null && !isNumeric(code)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("Insira um valor válido para code.", 400));
        }
        Long codigoPessoaNumber = Long.parseLong(code);

        List<Pessoa> targetPessoaList = action.findByCodigoPessoa(codigoPessoaNumber);
        if (targetPessoaList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse("Não foi encontrado nenhum Pessoa com esse código.", 404));
        }

        Pessoa targetPessoa = targetPessoaList.get(0);
        targetPessoa.setStatus(2L);
        action.save(targetPessoa);
        return ResponseEntity.ok(targetPessoa);
    }

    @DeleteMapping("/pessoa/DELETE/{codigoPessoa}")
    @Transactional
    public ResponseEntity<Object> deleteHiddenPessoa(@PathVariable Long codigoPessoa) {
        if (codigoPessoa == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O parametro codigoPessoa é obrigatório na URL", 400));
        }

        List<Pessoa> targetPessoaList = action.findByCodigoPessoa(codigoPessoa);
        if (!targetPessoaList.isEmpty()) {
            action.deleteByCodigoPessoa(codigoPessoa);
            return ResponseEntity.status(HttpStatus.OK).body(targetPessoaList.get(0));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse("Not found", 404));
        }
    }
}
