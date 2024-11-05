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
import org.springframework.web.bind.annotation.RestController;

import com.hackaton.model.Municipio;
import com.hackaton.repository.MunicipioRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin(origins="*")
public class MunicipioController extends ControllerSupport {
    @Autowired
    private MunicipioRepository action;
    

    @GetMapping("/municipio/all")
    public Iterable<Municipio> getAllMunicipios() {
        return action.findAll();
    }
    

    @GetMapping("/municipio")
    public ResponseEntity<Object> getMunicipio(@RequestParam(required=false) String codigoMunicipio,
                                   @RequestParam(required=false) String codigoUF,
                                   @RequestParam(required=false) String nome,
                                   @RequestParam(required=false) String status) {
        
        if (codigoMunicipio != null && !isNumeric(codigoMunicipio)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O valor inserido para código município não é um número válido.", 400));
        if (codigoUF != null && !isNumeric(codigoUF)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O valor inserido para codigoUF não é um número válido.", 400));
        if (status != null && !isNumeric(status)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O valor inserido para status não é um número válido.", 400));
        

        Long codigoMunicipioNumber = codigoMunicipio != null ? Long.parseLong(codigoMunicipio) : null;
        Long codigoUFNumber = codigoUF != null ? Long.parseLong(codigoUF) : null;
        Long statusNumber = status != null ? Long.parseLong(status) : null;

        if(codigoUF!=null && status == null && nome == null && codigoMunicipio ==null) {
            return ResponseEntity.ok(action.findByCodigoUF(codigoUFNumber));
        }

        if(status!=null && codigoUF == null && nome == null && codigoMunicipio == null) {
            return ResponseEntity.ok(action.findByStatus(statusNumber));
        }

        List<Municipio> results = new ArrayList<>();
        if(nome!=null)results.addAll(action.findByNome(nome));
        if(codigoMunicipio!=null)results.addAll(action.findByCodigoMunicipio(codigoMunicipioNumber));

        results = results.stream()
                        .filter(municipio ->(nome ==null || municipio.getNome().equals(nome))&&
                                              (codigoMunicipioNumber == null || municipio.getCodigoMunicipio().equals(codigoMunicipioNumber)))
                        .distinct()
                        .collect(Collectors.toList());

        if(!results.isEmpty()) {
            return ResponseEntity.ok(results.get(0));
        }
        return ResponseEntity.ok(Collections.emptyList());      
    }

    @PostMapping("/municipio")
    public ResponseEntity<Object> createMunicipio(@RequestBody Municipio municipio) {
        if (municipio.getNome() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("Campo nome é obrigatório", 400));
        if (municipio.getCodigoUF() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("Campo codigoUF é obrigatório", 400));
        if (municipio.getStatus() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("Campo status é obrigatório", 400));

        List<Municipio> nameExists = action.findByNome(municipio.getNome());
        if (!nameExists.isEmpty())return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse("Já existe um município com esse nome.", 409));

        action.save(municipio);
        Iterable<Municipio> allMunicipios = action.findAll();
        return ResponseEntity.ok(allMunicipios);
    }

    @PutMapping("/municipio")
    public ResponseEntity<Object> editMunicipio(@RequestBody Municipio municipio) {
        // validação por 4 campos com texto em portugues 
        if (municipio.getCodigoMunicipio() == null)return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo codigoMunicipio é obrigatório",400));
        if (municipio.getCodigoUF() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo codigoUF é obrigatório", 400));
        if (municipio.getNome() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo nome é obrigatório", 400));
        if (municipio.getStatus() == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O campo status é obrigatório", 400));
        

        List<Municipio> targetMunicipioList = action.findByCodigoMunicipio(municipio.getCodigoMunicipio());
        if(targetMunicipioList.isEmpty()) return ResponseEntity.status(404).body(createErrorResponse("Não foi encontrado nenhum município com esse código.", 404));

        Municipio targetMunicipio = targetMunicipioList.get(0);

        targetMunicipio.setNome(municipio.getNome());
        targetMunicipio.setCodigoUF(municipio.getCodigoUF());
        targetMunicipio.setStatus(municipio.getStatus());
        action.save(targetMunicipio);

        Iterable<Municipio> allMunicipios = action.findAll();
        return ResponseEntity.ok(allMunicipios);
        // Lista completa de todos os municipios Iterable<UF> allMunicipios = action.findAll(); return ResponseEntity.ok(allUFs);
    }

    @DeleteMapping ("/municipio")
    public ResponseEntity<Object> deleteMunicipio(@RequestParam(required = false) String code){
        if(code==null) return ResponseEntity.status(400).body(createErrorResponse("O campo code é obrigatório.", 400));
        if(code!=null && !isNumeric(code)) return ResponseEntity.status(400).body(createErrorResponse("Insira um valor numérico", 400));
        Long codeNumber =  Long.parseLong(code);

        Municipio target = action.findByCodigoMunicipio(codeNumber).get(0);

        target.setStatus(2L);
        action.save(target);
        return ResponseEntity.ok(target);
    }
}
