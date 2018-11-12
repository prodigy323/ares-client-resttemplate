package com.example.aresclientresttemplate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/heroes")
public class HeroController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String BASE_URL = "http://localhost:8080";

    // return all objects
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Collection<Hero> getHeroes() {
        String url = BASE_URL + "/heroes";
        ResponseEntity<Resources<Hero>> responseEntity = restTemplate
                .exchange(url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<Resources<Hero>>() {});
        return responseEntity.getBody().getContent();
    }

/*    @RequestMapping(value = "/{firstName}", method = RequestMethod.GET)
    public Collection<Hero> getHeroByFirstName(@PathVariable("firstName") String firstName) {
        String url = BASE_URL + "/heroes/search/findByFirstName?firstname=" + firstName;
        ResponseEntity<Resources<Hero>> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
                null, new ParameterizedTypeReference<Resources<Hero>>() {});
        return responseEntity.getBody().getContent();
    }*/

    // Return single object by ID
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Hero getHeroById(@PathVariable("id") Long id) {
        String url = BASE_URL + "/heroes/" + id;
        ResponseEntity<Resource<Hero>> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
                null, new ParameterizedTypeReference<Resource<Hero>>() {},
                Collections.emptyMap());
        return responseEntity.getBody().getContent();
    }

    // TODO: POST single object
    // TODO: POST list of objects
    // TODO: PUT update single object
    // TODO: PUT update list of object
    // TODO: DELETE single object
    // TODO: DELETE list of objects
    // TODO: DELETE all objects

    // TODO: UI javascript to present data
}
