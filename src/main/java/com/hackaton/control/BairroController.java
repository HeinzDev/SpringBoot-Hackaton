package com.hackaton.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackaton.model.Bairro;
import com.hackaton.model.Municipio;
import com.hackaton.model.UF;
import com.hackaton.repository.BairroRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@SuppressWarnings("unused")
@RestController
@CrossOrigin(origins = "*")
public class BairroController extends ControllerSupport{
    @Autowired
    BairroRepository action;


    @GetMapping("/bairro")
    public ResponseEntity<Object> getBairros(@RequestParam(required=false) String codigoBairro,
                                        @RequestParam(required=false) String codigoMunicipio,
                                        @RequestParam(required=false) String nome,
                                        @RequestParam(required=false) String status) {
        
        if (codigoBairro == null && codigoMunicipio == null && nome == null && status == null){
            return ResponseEntity.ok(action.findAll());
        }

        if (codigoBairro != null && !isNumeric(codigoBairro)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O valor inserido para código bairro não é um número válido.", 400));
        if (codigoMunicipio != null && !isNumeric(codigoMunicipio)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O valor inserido para codigoMunicipio não é um número válido.", 400));
        if (status != null && !isNumeric(status)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O valor inserido para status não é um número válido.", 400));
                         
        Long codigoBairroNumber = codigoBairro !=null ? Long.parseLong(codigoBairro): null;
        Long codigoMunicipioNumber = codigoMunicipio !=null ? Long.parseLong(codigoMunicipio): null;
        Long statusNumber = status !=null ? Long.parseLong(status): null;

        //Querys individuais
        if(codigoMunicipio!= null && codigoBairro == null && nome == null && status == null) {
            return ResponseEntity.ok(action.findByCodigoMunicipio(codigoMunicipioNumber));
        }
        if(status!= null && codigoBairro == null && nome == null && codigoMunicipio == null) {
            return ResponseEntity.ok(action.findByStatus(statusNumber));
        }

        if(nome!= null && codigoBairro == null && codigoMunicipio == null && status == null) {
            return ResponseEntity.ok(action.findByNome(nome));
        }
        if(codigoBairro!=null && codigoMunicipio == null && nome == null && status == null){
            List<Bairro> results = action.findByCodigoBairro(codigoBairroNumber);
            if (results.isEmpty()) return ResponseEntity.ok(Collections.emptyList());
            return ResponseEntity.ok(results.get(0));
        }


        List<Bairro> results = new ArrayList<>();
        if(nome!=null)results.addAll(action.findByNome(nome));
        if(codigoMunicipio!=null)results.addAll(action.findByCodigoMunicipio(codigoMunicipioNumber));
        if(codigoBairro!=null)results.addAll(action.findByCodigoBairro(codigoBairroNumber));
        if(status!=null)results.addAll(action.findByStatus(statusNumber));

        results = results.stream()
                        .filter(bairro ->(nome ==null || bairro.getNome().equals(nome))&&
                                (codigoBairro == null || bairro.getCodigoBairro().equals(codigoBairroNumber))&&
                                (codigoMunicipio == null || bairro.getCodigoMunicipio().equals(codigoMunicipioNumber))&&
                                ( status == null || bairro.getStatus().equals(statusNumber)))
                        .distinct()
                        .collect(Collectors.toList());

        if(!results.isEmpty()) return ResponseEntity.ok(results.get(0));
        
        return ResponseEntity.ok(Collections.emptyList());
    }

    @PostMapping("/bairro")
    public ResponseEntity<Object> createBairro(@RequestBody Bairro bairro){
        if(bairro.getNome() == null)return ResponseEntity.status(400).body(createErrorResponse("O campo nome é obrigatório.", 400));
        if(bairro.getCodigoMunicipio() == null)return ResponseEntity.status(400).body(createErrorResponse("O campo municipio é obrigatório.", 400));
        if(bairro.getStatus() == null)return ResponseEntity.status(400).body(createErrorResponse("O campo status é obrigatório.", 400));

        action.save(bairro);
        
        Iterable<Bairro> allBairros = action.findAll();
        return ResponseEntity.ok(allBairros);
    }
    
    @PutMapping("/bairro")
    public ResponseEntity<Object> editBairro(@RequestBody Bairro bairro) {
        //validação campo x é obrigatorio
        if (bairro.getCodigoBairro() == null)return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo codigoBairro é obrigatório",400));
        if (bairro.getCodigoMunicipio() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo codigoMunicipio é obrigatório", 400));
        if (bairro.getNome() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo nome é obrigatório", 400));
        if (bairro.getStatus() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo status é obrigatório", 400));

        List<Bairro> targetBairroList = action.findByCodigoBairro(bairro.getCodigoBairro());
        if(targetBairroList.isEmpty()) return ResponseEntity.status(400).body(createErrorResponse("Não foi encontrado nenhum Bairro com esse código.", 400));

        Bairro targetBairro = targetBairroList.get(0);

        targetBairro.setNome(bairro.getNome());
        targetBairro.setCodigoMunicipio(bairro.getCodigoMunicipio());
        targetBairro.setStatus(bairro.getStatus());
        action.save(targetBairro);

        Iterable<Bairro> allBairros = action.findAll();
        return ResponseEntity.ok(allBairros);
    }

        @DeleteMapping ("/bairro")
    public ResponseEntity<Object> deleteBairro(@RequestParam(required = false) String code){
        if(code==null) return ResponseEntity.status(400).body(createErrorResponse("O campo code é obrigatório.", 400));
        if(code!=null && !isNumeric(code)) return ResponseEntity.status(400).body(createErrorResponse("Insira um valor numérico", 400));
        Long codeNumber =  Long.parseLong(code);

        Bairro target = action.findByCodigoBairro(codeNumber).get(0);

        target.setStatus(2L);
        action.save(target);
        return ResponseEntity.ok(target);
    }
}
