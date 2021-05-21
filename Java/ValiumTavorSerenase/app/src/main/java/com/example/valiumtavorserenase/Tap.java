package com.example.valiumtavorserenase;

public class Tap {
    //singolo valore della sequenza, tempo
    private int valore;
    private long durataTap, momentoTap;

    public int getValore() { //Return valore
        return valore;
    }

    public long getMomentoTap() { //Return momentoTap
        return momentoTap;
    }

    public Tap(int valore, long momentoTap) { //Costruttore, assegno il valore e momento tap
        this.valore = valore;
        this.momentoTap = momentoTap;
    }
}
