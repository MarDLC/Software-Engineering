package it.univaq.caffeine.catan.model;

public class Token {
    private int number;

    public Token(int number) { this.number = number; }
    public int getNumber() { return number; }

    @Override
    public String toString() { return "[" + number + "]"; }
}
