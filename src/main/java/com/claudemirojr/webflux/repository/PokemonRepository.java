package com.claudemirojr.webflux.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.claudemirojr.webflux.model.Pokemon;

public interface PokemonRepository extends ReactiveMongoRepository<Pokemon, String>{

}
