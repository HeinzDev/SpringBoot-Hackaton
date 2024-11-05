package com.hackaton.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackaton.model.UF;
import com.hackaton.repository.UFRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@CrossOrigin(origins = "*")
public class UFController extends ControllerSupport {
    @Autowired
    private UFRepository action;

    @GetMapping("/uf/all")
    public Iterable<UF> getUF() {
        return action.findAll();
    }

    @GetMapping("/uf")
    public ResponseEntity<Object> getUFByParams(@RequestParam(required = false) String nome,
                                    @RequestParam(required = false) String status,
                                    @RequestParam(required = false) String codigoUF,
                                    @RequestParam(required = false) String sigla) {
        if(codigoUF != null && !isNumeric(codigoUF)) return ResponseEntity.status(0).body(createErrorResponse("O valor inserido para codigoUF não é um número válido", 400));
        if(status != null && !isNumeric(status)) return ResponseEntity.status(400).body(createErrorResponse("O valor inserido para status não é um número válido", 400));
        
        Long codigoUFNumber = codigoUF != null ? Long.parseLong(codigoUF): null;
        Long statusNumber = status != null ? Long.parseLong(status): null;

        if (statusNumber != null && nome == null && codigoUFNumber == null && sigla == null) {
            return ResponseEntity.ok(action.findByStatus(statusNumber));
        }

        List<UF> results = new ArrayList<>();
        if (nome != null) results.addAll(action.findByNome(nome));
        if (codigoUFNumber != null) results.addAll(action.findByCodigoUF(codigoUFNumber));
        if (sigla != null) results.addAll(action.findBySigla(sigla));

        //
        results = results.stream()
                    .filter(uf -> (nome == null || uf.getNome().equals(nome)) &&
                                    (statusNumber == null || uf.getStatus().equals(statusNumber)) &&
                                    (codigoUFNumber == null || uf.getCodigoUF().equals(codigoUFNumber)) &&
                                    (sigla == null || uf.getSigla().equals(sigla)))
                    .distinct()
                    .collect(Collectors.toList());

        if(!results.isEmpty()) {
            return ResponseEntity.ok(results.get(0));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    //POST
    @PostMapping("/uf")
    public ResponseEntity<Object> createUF(@RequestBody UF uf) {
    
        if (uf.getNome() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("Campo nome é obrigatório", 400));
        if (uf.getSigla() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("Campo sigla é obrigatório", 400));
        if (uf.getStatus() == 0) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("Campo status é obrigatório", 400));

    
        // Verificando se já existe:
        List<UF> nameExists = action.findByNome(uf.getNome());
        List<UF> siglaExists = action.findBySigla(uf.getSigla());
    
        if (!nameExists.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse("Já existe uma UF com esse nome no banco de dados.", 409));
        if (!siglaExists.isEmpty()) return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse("Já existe uma UF com essa sigla no banco de dados.", 409));
    
        action.save(uf);
        Iterable<UF> allUFs = action.findAll();
        return ResponseEntity.ok(allUFs);
    }
    

    //PUT
    @PutMapping("/uf")
    public ResponseEntity<Object> editUF(@RequestBody UF uf) {
        if (uf.getCodigoUF() == null)return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("Mensagem: campo codigoUF é obrigatório",400));

        if (uf.getNome() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo nome é obrigatório", 400));
        if (uf.getSigla() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo sigla é obrigatório", 400));
        if (uf.getStatus() == 0) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo status é obrigatório", 400));
    
    
        // Busca o UF existente pelo codigoUF
        List<UF> existingUFList = action.findByCodigoUF(uf.getCodigoUF());
        if (existingUFList.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse("Não foi encontrado nenhuma UF com este código.", 404));
    
        UF existingUF = existingUFList.get(0);
    
        // Verificações se já existem outra UF com o mesmo nome ou sigla
        List<UF> nameExists = action.findByNome(uf.getNome());
        List<UF> siglaExists = action.findBySigla(uf.getSigla());
        if (!nameExists.isEmpty() && !nameExists.get(0).getCodigoUF().equals(existingUF.getCodigoUF())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("Já existe uma UF com esse nome no banco de dados.", 400));
        }

        if (!siglaExists.isEmpty() && !siglaExists.get(0).getCodigoUF().equals(existingUF.getCodigoUF())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("Já existe uma UF com essa sigla no banco de dados.", 400));
        }
    
        // Atualiza os atributos do UF existente
        existingUF.setNome(uf.getNome());
        existingUF.setSigla(uf.getSigla());
        existingUF.setStatus(uf.getStatus());
        action.save(existingUF);
    
        Iterable<UF> allUFs = action.findAll();
        return ResponseEntity.ok(allUFs);
    }

    @DeleteMapping ("/uf")
    public ResponseEntity<Object> deleteUF(@RequestParam(required = false) String code){
        if(code==null) return ResponseEntity.status(400).body(createErrorResponse("O campo code é obrigatório.", 400));
        if(code!=null && !isNumeric(code)) return ResponseEntity.status(400).body(createErrorResponse("Insira um valor numérico", 400));
        Long codeNumber =  Long.parseLong(code);

        UF target = action.findByCodigoUF(codeNumber).get(0);

        target.setStatus(2L);
        action.save(target);
        return ResponseEntity.ok(target);
    }
}