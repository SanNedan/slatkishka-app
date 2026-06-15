package com.example.slatkishka.models;

// oвој модел се користи за чување на ПРОИЗВОДИТЕ кои ги нудат локалите во Firebase!

public class ProductModel {
    private String id; // идентификатор на производот
    private String ime;
    private String opis; 
    private double cena;
    private String slika; // во форма на URL за во Firebase
    private String biznis; // бизнис кој го нуди

    public ProductModel() {
    } // празен конструктор по барање на Firebase!

    public ProductModel(String id, String ime, String opis, double cena, String slika, String biznis) {
        this.id = id;
        this.ime = ime;
        this.opis = opis;
        this.cena = cena;
        this.slika = slika;
        this.biznis = biznis;
    }

    // GET и SET методи

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public double getCena() {
        return cena;
    }

    public void setCena(double cena) {
        this.cena = cena;
    }

    public String getSlika() {
        return slika;
    }

    public void setSlika(String slika) {
        this.slika = slika;
    }

    public String getBiznis() {
        return biznis;
    }

    public void setBiznis(String biznis) {
        this.biznis = biznis;
    }
}
