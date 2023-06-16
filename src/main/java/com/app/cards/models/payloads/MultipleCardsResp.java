package com.app.cards.models.payloads;

import com.app.cards.models.entities.Card;
import java.util.List;


public record MultipleCardsResp (List<Card> content,Pagination pagination){}

