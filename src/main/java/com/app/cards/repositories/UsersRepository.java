package com.app.cards.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.app.cards.models.entities.Users;

import reactor.core.publisher.Mono;

public interface UsersRepository extends ReactiveCrudRepository<Users,Long> {
    Mono<Users> findByEmail(String email);
}
