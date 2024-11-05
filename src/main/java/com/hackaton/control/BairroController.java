package com.hackaton.control;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@SuppressWarnings("unused")
@RestController
@CrossOrigin(origins = "*")
public class BairroController {
    @GetMapping("path")
    // public String getMethodName(@RequestParam String param) {
        //validação numero/string com erro em portugues


        //se passar sem parametro listar todos os bairros

        //codigo-> retorna apenas um (ARRAY VAZIO CASO NAO TENHA)
        //codigomunicipio -> lista com todos os bairros do municipio
        //nome -> dentro de uma lista
        //status-> todos
        //não tem nenhum -> array vazio
    // }

    @PostMapping("path")
    public String postMethodName(@RequestBody String entity) {
        //validação string e numeros em portugues
        //validação campo nome/ campo status/ campo codigo municipio é obrigatorio
        //validação nao poder incluir um mesmo bairro pro municipio
        //retornar bairros se der certo
        return entity;
    }
    
    @PutMapping("path/{id}")
    public String putMethodName(@PathVariable String id, @RequestBody String entity) {
        //validação campo x é obrigatorio
        

        //lista completa dos bairros Iterable<Bairro> allBairros = action.findAll(); return ResponseEntity.ok(allBairros);
        return entity;
    }
}
