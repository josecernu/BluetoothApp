package com.josemanuelapps.bluetoothapp;

import android.app.Activity;

/**
 * Created by josecernu on 16/04/15.
 */
public class BluetoothApplication {
    public static ConnectedThread mConnectedThread;
    public static String address;
    public static boolean firstRunning = true;

    public static String posicion = "";
    public static String distancia = "";
    public static String temperatura = "";

    public static ValorObservable vo = new ValorObservable("");
    public static TextoObservador to = new TextoObservador(vo);
    public static Activity modeActivity;
    public static Activity configActivity;
    public static boolean enviar = true;


}
