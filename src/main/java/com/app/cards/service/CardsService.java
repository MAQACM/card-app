package com.app.cards.service;

import java.util.List;
import java.util.Map;

import com.app.cards.models.entities.Card;
import com.app.cards.models.payloads.CardCreationBody;
import com.app.cards.models.payloads.CardQueryParams;

import com.app.cards.models.payloads.DeleteResponse;
import com.app.cards.models.payloads.MultipleCardsResp;
import reactor.core.publisher.Mono;

public interface CardsService {
    Mono<Card>createCard( CardCreationBody body);
    Mono<MultipleCardsResp>fetchCards(CardQueryParams queryParams);
    Mono<Card>FetchById( long id);
    Mono<DeleteResponse>deleteCard( long id);
    Mono<Card>updateCard(CardCreationBody cardCreationBody);

}
