package com.hackaton.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    @Autowired
    private BairroService bairroService;
    @Autowired
    private UFService ufService;

    @Autowired
    private MunicipioService municipioService;

    @CrossOrigin(origins = "*")
    @GetMapping("/pessoa/all")
    public ResponseEntity<Object> getPessoas() {
        List<Pessoa> results = action.findAll();
        results.forEach(p-> p.setEnderecos(Collections.emptyList()));
        return ResponseEntity.ok(results);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/pessoa")
    public ResponseEntity<Object> getPessoaByParams(@RequestParam(required = false) String codigoPessoa,
                                                    @RequestParam(required = false) String status,
                                                    @RequestParam(required = false) String nome,
                                                    @RequestParam(required = false) String login) {
        if (codigoPessoa != null && !isNumeric(codigoPessoa)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O valor inserido para códigoPessoa não é um número válido.", 400));
        }
        if (status != null && !isNumeric(status)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O valor inserido para status não é um número válido.", 400));
        }

        Long codigoPessoaNumber = codigoPessoa != null ? Long.parseLong(codigoPessoa) : null;
        Long statusNumber = status != null ? Long.parseLong(status) : null;

        //Pegar por status manualmente
        if (statusNumber != null && codigoPessoa == null && login == null) {
            List<Pessoa> pessoasComStatus = action.findByStatus(statusNumber);
            List<Map<String, Object>> pessoasSemEnderecos = pessoasComStatus.stream().map(pessoa -> {
                Map<String, Object> pessoaMap = new HashMap<>();
                pessoaMap.put("codigoPessoa", pessoa.getCodigoPessoa());
                pessoaMap.put("nome", pessoa.getNome());
                pessoaMap.put("sobrenome", pessoa.getSobrenome());
                pessoaMap.put("idade", pessoa.getIdade());
                pessoaMap.put("login", pessoa.getLogin());
                pessoaMap.put("senha", pessoa.getSenha());
                pessoaMap.put("status", pessoa.getStatus());
                pessoaMap.put("endereco", Collections.emptyList());
                return pessoaMap;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(pessoasSemEnderecos);
        }

        List<Pessoa> results = new ArrayList<>();
        if (codigoPessoaNumber != null) results.addAll(action.findByCodigoPessoa(codigoPessoaNumber));
        if (nome != null) results.addAll(action.findByNome(nome));
        if (login != null) results.addAll(action.findByLogin(login));
        if (statusNumber != null) results.addAll(action.findByStatus(statusNumber));

        results = results.stream()
                .filter(pessoa -> (statusNumber == null || pessoa.getStatus().equals(statusNumber)) &&
                                    (codigoPessoaNumber == null || pessoa.getCodigoPessoa().equals(codigoPessoaNumber)) &&
                                    (nome == null || pessoa.getNome().equals(nome)) &&
                                    (login == null || pessoa.getLogin().equals(login)))
                                    .distinct()
                                    .collect(Collectors.toList());

        if (!results.isEmpty()) {
            Pessoa pessoa = results.get(0);

            List<Endereco> enderecos = enderecoService.findByPessoaId(pessoa.getCodigoPessoa());

            List<Map<String, Object>> enderecosCompleto = enderecos.stream()
                    .sorted(Comparator.comparingLong(Endereco::getCodigoEndereco))  // Única forma q encontrei de ordenar pelo codigoEndereço
                    .map(endereco -> {
                        Map<String, Object> enderecoMap = new LinkedHashMap<>();  // Sem o LinkedHashmap Aqui ele não fica na ordem
                        enderecoMap.put("codigoEndereco", endereco.getCodigoEndereco());
                        enderecoMap.put("codigoPessoa", endereco.getCodigoPessoa());
                        enderecoMap.put("codigoBairro", endereco.getCodigoBairro());
                        enderecoMap.put("nomeRua", endereco.getNomeRua());
                        enderecoMap.put("numero", endereco.getNumero());
                        enderecoMap.put("complemento", endereco.getComplemento());
                        enderecoMap.put("cep", endereco.getCep());

                        Bairro bairro = bairroService.findBairroByCodigo(endereco.getCodigoBairro());
                        Map<String, Object> bairroMap = new LinkedHashMap<>();
                        bairroMap.put("codigoBairro", bairro.getCodigoBairro());
                        bairroMap.put("codigoMunicipio", bairro.getCodigoMunicipio());
                        bairroMap.put("nome", bairro.getNome());
                        bairroMap.put("status", bairro.getStatus());

                        Municipio municipio = municipioService.findMunicipioByCodigo(bairro.getCodigoMunicipio());
                        Map<String, Object> municipioMap = new LinkedHashMap<>();
                        municipioMap.put("codigoMunicipio", municipio.getCodigoMunicipio());
                        municipioMap.put("codigoUF", municipio.getCodigoUF());
                        municipioMap.put("nome", municipio.getNome());
                        municipioMap.put("status", municipio.getStatus());

                        UF uf = ufService.findUFByCodigo(municipio.getCodigoUF());
                        Map<String, Object> ufMap = new LinkedHashMap<>();
                        ufMap.put("codigoUF", uf.getCodigoUF());
                        ufMap.put("sigla", uf.getSigla());
                        ufMap.put("nome", uf.getNome());
                        ufMap.put("status", uf.getStatus());

                        municipioMap.put("uf", ufMap);
                        bairroMap.put("municipio", municipioMap);
                        enderecoMap.put("bairro", bairroMap);

                        return enderecoMap;
                    }).collect(Collectors.toList());

            Map<String, Object> pessoaMap = new LinkedHashMap<>();
            pessoaMap.put("codigoPessoa", pessoa.getCodigoPessoa());
            pessoaMap.put("nome", pessoa.getNome());
            pessoaMap.put("sobrenome", pessoa.getSobrenome());
            pessoaMap.put("idade", pessoa.getIdade());
            pessoaMap.put("login", pessoa.getLogin());
            pessoaMap.put("senha", pessoa.getSenha());
            pessoaMap.put("status", pessoa.getStatus());
            pessoaMap.put("enderecos", enderecosCompleto);

            return ResponseEntity.ok(pessoaMap);
        }

        return ResponseEntity.ok(Collections.emptyList());
    }

    @PostMapping("/pessoa")
    public ResponseEntity<Object> createPessoa(@RequestBody Pessoa pessoa) {
        //Sem o trycatch não consigo prever todos os erros possíveis devido a muitos campos :/
        try {
            if (pessoa.getNome() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo nome é obrigatório.", 400));
            if (pessoa.getLogin() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo login é obrigatório.", 400));
            if (pessoa.getStatus() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo status é obrigatório.", 400));

            pessoa.setCodigoPessoa(null);

            List<Pessoa> loginExists = action.findByLogin(pessoa.getLogin());
            if (!loginExists.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("Já existe uma pessoa com esse login no banco de dados.", 400));
            }

            action.save(pessoa);
            action.flush(); 

            if (pessoa.getEnderecos() != null) {
                for (Endereco endereco : pessoa.getEnderecos()) {
                    if (endereco.getCodigoPessoa() == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo codigoPessoa é obrigatório nos endereços.", 400));
                    }

                    Bairro bairro = bairroService.findBairroByCodigo(endereco.getCodigoBairro());
                    if (bairro == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("Bairro não encontrado para o código " + endereco.getCodigoBairro(), 400));
                    }

                    endereco.setBairro(bairro);
                    endereco.setPessoa(pessoa);
                    enderecoService.save(endereco);
                }
            }
            return ResponseEntity.ok(pessoa);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Erro interno no servidor: " + e.getMessage(), 500));
        }
    }

    
    @PutMapping("/pessoa")
    public ResponseEntity<Object> editPessoa(@RequestBody Pessoa pessoa) {
        if (pessoa.getCodigoPessoa() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo codigoPessoa é obrigatório", 400));
        if (pessoa.getNome() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo nome é obrigatório", 400));
        if (pessoa.getLogin() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo login é obrigatório", 400));
        if (pessoa.getStatus() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo status é obrigatório", 400));

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

        // Endereços existentes antes da atualização
        List<Endereco> enderecosExistentes = enderecoService.findByPessoaId(targetPessoa.getCodigoPessoa());

        // Lista para armazenar os endereços que foram atualizados ou criados
        List<Endereco> listaAtualizarEnderecos = new ArrayList<>();

        if (pessoa.getEnderecos() != null) {
            for (Endereco endereco : pessoa.getEnderecos()) {
                if (endereco.getCodigoEndereco() == null) {
                    // Criar novo endereço
                    endereco.setPessoa(targetPessoa);
                    Endereco savedEndereco = enderecoService.save(endereco);
                    listaAtualizarEnderecos.add(savedEndereco);
                } else {
                    // Se o Passou código endereço deve procurar Endereços, se existir edita, se não existir Cria um.
                    Endereco existingEndereco = enderecoService.findById(endereco.getCodigoEndereco());
                    if (existingEndereco != null && existingEndereco.getPessoa().getCodigoPessoa().equals(targetPessoa.getCodigoPessoa())) {
                        existingEndereco.setNomeRua(endereco.getNomeRua());
                        existingEndereco.setNumero(endereco.getNumero());
                        existingEndereco.setComplemento(endereco.getComplemento());
                        existingEndereco.setCep(endereco.getCep());
                        existingEndereco.setCodigoBairro(endereco.getCodigoBairro());
                        Endereco savedEndereco = enderecoService.save(existingEndereco);
                        listaAtualizarEnderecos.add(savedEndereco);
                    }
                }
            }
        }

        // Apagar endereços não encontrados na lista de atualização
        for (Endereco existingEndereco : enderecosExistentes) {
            if (listaAtualizarEnderecos.stream().noneMatch(e -> e.getCodigoEndereco().equals(existingEndereco.getCodigoEndereco()))) {
                enderecoService.delete(existingEndereco);
            }
        }

        // Retorna todas as pessoas com os endereços atualizados
        Iterable<Pessoa> allPessoas = action.findAll();
        allPessoas.forEach(p -> p.setEnderecos(Collections.emptyList()));  // Adiciona uma lista vazia de endereços

        return ResponseEntity.ok(allPessoas);
    }

    //Endpoint de teste
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
