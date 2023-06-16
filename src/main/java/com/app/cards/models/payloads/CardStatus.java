package com.app.cards.models.payloads;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CardStatus {
    TODO("To DO"),
    INPROG("In Progress"),
    DONE("Done");
    private final String status;
    CardStatus(String status) {
        this.status=status;
    }
    /*
    * Creates the card status
    * */
    @JsonCreator
    public static String getCardStatus(String status){
        return Arrays.stream(CardStatus.values())
                .filter(v->v.status.equals(status))
                .findFirst()
                .map(s->s.status)
                .orElse(TODO.status);
    }
}
