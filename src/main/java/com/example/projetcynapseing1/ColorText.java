package com.example.projetcynapseing1;

public enum ColorText {
    RESET("\u001B[0m"),
    RED("\u001B[31m"),
    REDBACK("\u001B[48;5;196m"),
    GREEN("\u001b[38;5;46m"),
    GREENBACK("\u001b[48;5;46m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    CYAN("\u001B[36m"),
    MAGENTA("\u001B[35m"),
    BOLD("\u001B[1m"),
    ITALIC("\u001B[3m"),
    UNDERLINE("\u001B[4m"),
    GRAY("\u001b[38;5;244m");

    private final String codeColor;

    ColorText(String codeColor) {
        this.codeColor = codeColor;
    }

    public String getCode() {
        return codeColor;
    }

    @Override
    public String toString() {
        return codeColor;
    }
    
}
