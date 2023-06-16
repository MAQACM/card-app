package com.app.cards.entry;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.app.cards.models.payloads.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import com.app.cards.configs.ConfigProperties;
import com.app.cards.models.entities.Card;
import com.app.cards.service.CardsService;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class CardsController {
    private final CardsService cardsService;
    private final ConfigProperties properties;

    public CardsController(CardsService cardsService, ConfigProperties properties) {
        this.cardsService = cardsService;
        this.properties = properties;
    }
    @Operation(summary = "Create Card")
    @PostMapping("/v1/card")
    public Mono<Card> createCard(@Valid @RequestBody CardCreationBody cardCreationBody){
        return this.cardsService.createCard(cardCreationBody);
    }
    @GetMapping("/v1/card")
    @Operation(summary = "Fetch all cards.")
    public Mono<MultipleCardsResp>fetchCards(@RequestParam("name") Optional<String> name, @RequestParam("color") Optional<String> color,
                                      @RequestParam("created") Optional<Instant> created, @RequestParam("status") Optional<CardStatus> status, @RequestParam("limit") Optional<Integer> limit,
                                      @RequestParam("offset") Optional<Integer> offset, @RequestParam("sortBy") Optional<SortBy> sortBy){
        CardQueryParams queryParams=new CardQueryParams(name.orElse(null),limit.orElse(20),offset.orElse(1),color.orElse(null),status.orElse(null),created.orElse(null),sortBy.orElse(SortBy.CREATED_BY));
        return this.cardsService.fetchCards(queryParams);
    }
    @Operation(summary = "FetchCard by Id.")
    @GetMapping("/v1/card/{id}")
    public Mono<Card>FetchById(@PathVariable("id") long id){
        return this.cardsService.FetchById(id);
    }
    @Operation(summary = "Delete card.")
    @DeleteMapping("/v1/card/{id}")
    Mono<DeleteResponse>deleteCard(@PathVariable("id") long id){
        return this.cardsService.deleteCard(id);
    }
    @Operation(summary = "Update Card")
    @PutMapping("/v1/card")
    Mono<Card>updateCard(@Valid @RequestBody CardCreationBody cardCreationBody){
        return this.cardsService.updateCard(cardCreationBody);
    }
}
