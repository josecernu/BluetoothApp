package com.josemanuelapps.bluetoothapp;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by josecernu on 01/05/15.
 */
public class TextoObservador implements Observer {
    private ValorObservable vo = null;
    private Activity activity = null;

    // EXTRA string to send on to mainactivity
    public static String EXTRA_CONFIG = "config_id";

    public TextoObservador( ValorObservable vo ) {
        this.vo = vo;
    }

    public TextoObservador( ValorObservable vo , Activity activity) {
        Log.i("josecernu","Nuevo constructor");
        this.vo = vo;
        this.activity = activity;
    }

    public void update( Observable obs,Object obj ) {
        if( obs == vo ) {
            String cadenaRecibida = String.valueOf(vo.getValor());
            Log.i("josecernu: ", "Observador--> " + cadenaRecibida);

            if (cadenaRecibida.equalsIgnoreCase("Back")){
                activity.finish();
                BluetoothApplication.enviar = false;
                activity.onBackPressed();
            }else {

                if (cadenaRecibida.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                {
                    String subcadena = cadenaRecibida.substring(1, 3);
                    if (subcadena.equalsIgnoreCase("01")) {
                        // Estamos en uno de los 3 modos
                        TextView txtRecibido = (TextView) BluetoothApplication.modeActivity.findViewById(R.id.textRecibido);
                        txtRecibido.setText(activity.getResources().getText(R.string.data_received) + " = " + String.valueOf(vo.getValor()));
                        //int dataLength = cadenaRecibida.length();
                        //txtLongitud.setText(getResources().getText(R.string.string_lenght)+" = " + String.valueOf(dataLength));
                        TextView txtPosicion = (TextView) BluetoothApplication.modeActivity.findViewById(R.id.textViewPos2);
                        TextView txtDistancia = (TextView) BluetoothApplication.modeActivity.findViewById(R.id.textViewDis2);
                        TextView txtTemperatura = (TextView) BluetoothApplication.modeActivity.findViewById(R.id.textViewTemp2);

                        String[] datos = cadenaRecibida.substring(4).split("#");
                        txtPosicion.setText(datos[0]);
                        txtDistancia.setText(datos[1]);
                        txtTemperatura.setText(datos[2]);
                    } else {
                        if (subcadena.equalsIgnoreCase("04")) {
                            String configOption = cadenaRecibida.substring(3);
                            try {
                                int pantalla = Integer.parseInt(configOption);
                                switch (pantalla) {
                                    case 1:
                                        Log.i("josecernu", "Grados config");
                                        configGrados();
                                        break;
                                    case 2:
                                        Log.i("josecernu", "Velocidad config");
                                        configVelocidad();
                                        break;
                                    case 3:
                                        Log.i("josecernu", "Tiempo config");
                                        configTiempo();
                                        break;
                                    case 4:
                                        Log.i("josecernu", "Umbral config");
                                        configUmbral();
                                        break;
                                    default:
                                        break;
                                }
                            } catch (Exception e) {

                            }
                        }else{
                            if (subcadena.equalsIgnoreCase("05")) {
                                Log.i("josecernu","Configuracion interior");
                                String valor = cadenaRecibida.substring(3);
                                Log.i("josecernu", "Valor interno: " + valor);
                                //activity.setContentView(R.layout.activity_config);
                                TextView txtAux = (TextView) BluetoothApplication.configActivity.findViewById(R.id.textViewInfo2);
                                txtAux.setText(valor);
                            }
                        }
                    }
                }
            }

        }
    }

    private void configGrados(){
        Intent i = new Intent(activity, ConfigActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(EXTRA_CONFIG, 1);
        activity.startActivity(i);
        activity.finish();
    }

    private void configVelocidad(){
        Intent i = new Intent(activity, ConfigActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(EXTRA_CONFIG, 2);
        activity.startActivity(i);
        activity.finish();
    }

    private void configTiempo(){
        Intent i = new Intent(activity, ConfigActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(EXTRA_CONFIG, 3);
        activity.startActivity(i);
        activity.finish();
    }

    private void configUmbral(){
        Intent i = new Intent(activity, ConfigActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(EXTRA_CONFIG, 4);
        activity.startActivity(i);
        activity.finish();
    }
}
