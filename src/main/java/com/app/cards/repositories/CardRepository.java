package com.app.cards.repositories;

import java.sql.Timestamp;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;


import com.app.cards.models.entities.Card;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CardRepository extends ReactiveSortingRepository<Card,Long>, ReactiveQueryByExampleExecutor<Card> {
    Mono<Card>findByName(String name);
    Mono<Card>findByColor(String color);
    Mono<Card>findByCreatedAt(Timestamp createdAt);
    Mono<Card>findByStatus(String status);
    Mono<Void>deleteById(long id);
    Mono<Card>save(Card card);
    Mono<Card>findById(long id);
    Mono<Card>findByIdAndCreatedBy(long id,long creator);



}
