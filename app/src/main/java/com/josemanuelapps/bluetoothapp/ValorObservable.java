package com.josemanuelapps.bluetoothapp;

import java.util.Observable;

/**
 * Created by josecernu on 01/05/15.
 */
public class ValorObservable extends Observable{
    private String nValor = null;

    // Constructor al que indicamos el valor actual
    public ValorObservable(String nValor) {
        this.nValor = nValor;
    }

    // Fija el valor que le pasamos y notifica a los observadores que
    // estan pendientes del cambio de estado de los objetos de esta
    // clase, que su etado se ha visto alterado
    public void setValor(String nValor) {
        this.nValor = nValor;

        setChanged();
        notifyObservers();
    }

    // Devuelve el valor actual que tiene el objeto
    public String getValor() {
        return( nValor );
    }
}
