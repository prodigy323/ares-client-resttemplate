package com.example.aresclientresttemplate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/hero")
public class HeroController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String URL = "http://localhost:8080/heroes";

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Collection<Hero> getHero() {
        final ResponseEntity<Resources<Hero>> heroResponse = restTemplate
                .exchange(URL, HttpMethod.GET, null,
                        new ParameterizedTypeReference<Resources<Hero>>() {
                        });
        Collection<Hero> heroList = heroResponse.getBody().getContent();
        return heroList;
    }

    // TODO: CRUD operations

    // TODO: UI javascript to present data
}
