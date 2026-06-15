package com.example.slatkishka.models;

import com.google.firebase.database.PropertyName;

public class BusinessModel {
    private String ime;
    private String kategorija;

    public BusinessModel() {} // Празен конструктор за Firebase!

    public BusinessModel(String ime, String kategorija) {
        this.ime = ime;
        this.kategorija = kategorija;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    @PropertyName("category")
    public String getKategorija() {
        return kategorija;
    }

    @PropertyName("category")
    public void setKategorija(String kategorija) {
        this.kategorija = kategorija;
    }
}
