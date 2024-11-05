package com.hackaton.control;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.hackaton.model.Municipio;
import com.hackaton.repository.MunicipioRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@SuppressWarnings("unused")
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
    public Object getMunicipio(@RequestParam(required=false) String codigoMunicipio,
                                   @RequestParam(required=false) String codigoUF,
                                   @RequestParam(required=false) String nome,
                                   @RequestParam(required=false) String status) {
        if (codigoMunicipio != null && !isNumeric(codigoMunicipio)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O valor inserido para código município não é um número válido.", 400));
        if (codigoUF != null && !isNumeric(codigoUF)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O valor inserido para codigoUF não é um número válido.", 400));
        if (status != null && !isNumeric(status)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorResponse("O valor inserido para status não é um número válido.", 400));
        

        Long codigoMunicipioNumber = codigoMunicipio != null ? Long.parseLong(codigoMunicipio) : null;
        Long codigoUFNumber = codigoUF != null ? Long.parseLong(codigoUF) : null;
        Long statusNumber = status != null ? Long.parseLong(status) : null;

        //TODO many params

        // if(codigoMunicipio codigoUF nome status)

        if(codigoMunicipio!=null){
            List<Municipio> result = action.findByCodigoMunicipio(codigoMunicipioNumber);
            return result.isEmpty() ? Collections.emptyList() : result.get(0);
        } else if (codigoUF!=null){
            return action.findByCodigoUF(codigoUFNumber);
        } else if (nome!=null){
            List<Municipio> result = action.findByNome(nome);
            return result.isEmpty() ? Collections.emptyList() : result.get(0);
        } else if (status!=null){
            return action.findByStatus(statusNumber);
        }
        //TODO Nome -> mesmo que só tenha um sempre por lista 
        // status -> lista normal
        
        else{
            return Collections.emptyList();
        }
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

    //Delete maybe
}
