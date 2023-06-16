package com.app.cards.errorhandling;

public class RecordNotFound extends RuntimeException {
    private final String code;

    public RecordNotFound(String message) {
        super(message);
        this.code = "404";
    }
    public String getCode(){return this.code;};
}
