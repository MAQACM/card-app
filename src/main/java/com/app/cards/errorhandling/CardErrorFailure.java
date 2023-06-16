package com.app.cards.errorhandling;

public class CardErrorFailure extends RuntimeException {
    private final String code;

    public CardErrorFailure(String message) {
        super(message);
        this.code = "500";
    }

    public String getCode() {
        return this.code;
    }


}