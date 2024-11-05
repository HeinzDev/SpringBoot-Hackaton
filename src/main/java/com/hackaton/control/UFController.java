package com.hackaton.control;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public Object getUFByParams(@RequestParam(required = false) String nome,
                                  @RequestParam(required = false) Integer status,
                                  @RequestParam(required = false) Long codigoUF,
                                  @RequestParam(required = false) String sigla) {
        
        //TODO String to long convert

        // if (nome != null && status != null && codigoUF != null && sigla != null) {
        //     return action.findByNomeAndStatusAndCodigoUFAndSigla(nome, status, codigoUF, sigla);
        // }
        // if(nome != null && status != null && codigoUF != null){
        //     return action.findByNomeAndStatusAndCodigoUF(nome, status, codigoUF);
        // }


        if(nome != null && status != null) return action.findByNomeAndStatus(nome, status);
        else if(nome != null && codigoUF != null) return action.findByNomeAndCodigoUF(nome, codigoUF);    
        else if(status != null && codigoUF != null) return action.findByStatusAndCodigoUF(status, codigoUF);
        
        
         if(nome != null){
            List<UF> result = action.findByNome(nome);
            return result.isEmpty() ? Collections.emptyList() : result.get(0);
        } else if (status != null){
            return action.findByStatus(status);
        } else if(codigoUF != null) {
            List<UF> result = action.findByCodigoUF(codigoUF);
            return result.isEmpty() ? Collections.emptyList() : result.get(0);
        } else if(sigla != null) {
            List<UF> result = action.findBySigla(sigla);
            return result.isEmpty() ? Collections.emptyList() : result.get(0);
        } 
        
        else{
            return Collections.emptyList();
        }
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

    //TODO DELETE(OPCIONAL)
    @DeleteMapping("/uf/{code}")
    public void deleteUF(@PathVariable Long code){
        action.deleteById(code);

        //apagar recursivamente
    }
}