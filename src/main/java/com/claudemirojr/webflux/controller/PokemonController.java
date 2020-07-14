package com.claudemirojr.webflux.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.claudemirojr.webflux.model.Pokemon;
import com.claudemirojr.webflux.repository.PokemonRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@RestController
@RequestMapping("/v1/pokemons")
public class PokemonController {

	@Autowired
	private PokemonRepository _repository;

	@GetMapping
	private Flux<Pokemon> findAll() {
		return _repository.findAll();
	}

	@GetMapping("/{id}")
	private Mono<ResponseEntity<Pokemon>> findById(@PathVariable String id) {
		return _repository.findById(id).map(pokemon -> ResponseEntity.ok(pokemon))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	
	@GetMapping(value = "/eventos", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	private Flux<Tuple2<Long, Pokemon>>   eventos() {
		Flux<Long> interval = Flux.interval(Duration.ofSeconds(2));
		
		Flux<Pokemon> pokemons = _repository.findAll();
		
		return Flux.zip(interval, pokemons);
	}	

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	private Mono<Pokemon> save(@RequestBody Pokemon pokemon) {
		return _repository.save(pokemon);
	}

	@PutMapping("/{id}")
	private Mono<ResponseEntity<Pokemon>> update(@RequestBody Pokemon pokemon, @PathVariable String id) {
		return _repository.findById(id).flatMap(existePokemon -> {
			existePokemon.setNome(pokemon.getNome());
			existePokemon.setCategoria(pokemon.getCategoria());
			existePokemon.setHabilidade(pokemon.getHabilidade());
			existePokemon.setPeso(pokemon.getPeso());

			return _repository.save(existePokemon);
		}).map(updatePodemon -> ResponseEntity.ok(updatePodemon)).defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
		return _repository.findById(id)
				.flatMap(existingPokemon -> _repository.delete(existingPokemon)
						.then(Mono.just(ResponseEntity.noContent().<Void>build())))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	
	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAll() {
        return _repository.deleteAll();
    }	
}
