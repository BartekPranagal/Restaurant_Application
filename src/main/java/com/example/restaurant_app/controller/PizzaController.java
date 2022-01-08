package com.example.restaurant_app.controller;

import com.example.restaurant_app.model.dto.PizzaResponse;
import com.example.restaurant_app.service.PizzaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PizzaController {

    private final PizzaService pizzaService;

    @GetMapping(path = "/menu")
    public List<PizzaResponse> getMenu(){
        return pizzaService.getMenu();
    }

    @GetMapping(path = "/menu/{id}")
    public PizzaResponse getPizzaById(@PathVariable(name = "id") Long pizzaId){
        return pizzaService.getById(pizzaId);
    }

    @GetMapping(path ="/menu/{query}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PizzaResponse> getDynamicPizzaName( @PathVariable(name = "query") String query){
        return pizzaService.getPizzaByQuery(query);
    }
}
