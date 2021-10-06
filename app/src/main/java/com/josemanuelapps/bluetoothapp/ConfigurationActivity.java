package com.josemanuelapps.bluetoothapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.util.Observable;

public class ConfigurationActivity extends Activity {

    private ValorObservable vo2 = null;
    Button btn1, btn2, btn3, btn4, btnExit;

    // EXTRA string to send on to mainactivity
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // EXTRA string to send on to mainactivity
    public static String EXTRA_CONFIG = "config_id";

    // String for MAC address
    private static String address;

    public ConfigurationActivity() {
        super();
    }

    private ValorObservable vo = null;

    public ConfigurationActivity( ValorObservable vo) {
        this.vo = vo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configuration);
        BluetoothApplication.enviar = true;

        TextoObservador to = new TextoObservador( BluetoothApplication.vo, this);
        BluetoothApplication.vo.addObserver( to );

        //Link the buttons and textViews to respective views
        btn1 = (Button) findViewById(R.id.button01);
        btn2 = (Button) findViewById(R.id.button02);
        btn3 = (Button) findViewById(R.id.button03);
        btn4 = (Button) findViewById(R.id.button04);
        btnExit = (Button) findViewById(R.id.buttonExit);

        btn1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BluetoothApplication.mConnectedThread.write("Config1\r");    // Send "1" via Bluetooth
                //Toast.makeText(getBaseContext(), "Configuración grados", Toast.LENGTH_SHORT).show();

                configGrados();
            }
        });

        btn2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BluetoothApplication.mConnectedThread.write("Config2\r");    // Send "1" via Bluetooth
                //Toast.makeText(getBaseContext(), "Configuración velocidad", Toast.LENGTH_SHORT).show();

                configVelocidad();
            }
        });

        btn3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BluetoothApplication.mConnectedThread.write("Config3\r");    // Send "1" via Bluetooth
                //Toast.makeText(getBaseContext(), "Configuración tiempo", Toast.LENGTH_SHORT).show();

                configTiempo();
            }
        });

        btn4.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BluetoothApplication.mConnectedThread.write("Config4\r");    // Send "1" via Bluetooth
                //Toast.makeText(getBaseContext(), "Configuración umbral detección", Toast.LENGTH_SHORT).show();

                configUmbral();
            }
        });

        btnExit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BluetoothApplication.mConnectedThread.write("Back\r");    // Send "1" via Bluetooth
                //Toast.makeText(getBaseContext(), "Atrás", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(ConfigurationActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS, BluetoothApplication.address);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (BluetoothApplication.enviar) {
            BluetoothApplication.mConnectedThread.write("Back\r");    // Send "1" via Bluetooth
        }
        //Toast.makeText(getBaseContext(), "Atrás", Toast.LENGTH_SHORT).show();
        //BluetoothApplication.mConnectedThread.write("Null");

        Intent i = new Intent(ConfigurationActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS, BluetoothApplication.address);
        startActivity(i);
        finish();
    }

    private void configGrados(){
        Log.i("josecernu","Configuración grados");
        Intent i = new Intent(ConfigurationActivity.this, ConfigActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(EXTRA_CONFIG, 1);
        startActivity(i);
        finish();
    }

    private void configVelocidad(){
        Log.i("josecernu","Configuración velocidad");
        Intent i = new Intent(ConfigurationActivity.this, ConfigActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(EXTRA_CONFIG, 2);
        startActivity(i);
        finish();
    }

    private void configTiempo(){
        Log.i("josecernu","Configuración tiempo");
        Intent i = new Intent(ConfigurationActivity.this, ConfigActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(EXTRA_CONFIG, 3);
        startActivity(i);
        finish();
    }

    private void configUmbral(){
        Log.i("josecernu","Configuración umbral");
        Intent i = new Intent(ConfigurationActivity.this, ConfigActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(EXTRA_CONFIG, 4);
        startActivity(i);
        finish();
    }

    public void update( Observable obs,Object obj ) {
        if( obs == vo2 ) {
            String cadenaRecibida = String.valueOf(vo2.getValor());
            Log.i("josecernu: ", "ObservadorConfiguración--> " + cadenaRecibida);
            if (cadenaRecibida.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
            {
                String subcadena = cadenaRecibida.substring(1,3);
                if (subcadena.equalsIgnoreCase("04")){
                    // Estamos en la pantalla de configuración
                    /**
                    String configOption = cadenaRecibida.substring(4);
                    try {
                        int pantalla = Integer.parseInt(configOption);
                        Log.i("josecernu: ","Pantalla: "+pantalla);
                        switch (pantalla){
                            case 1:
                                configGrados();
                                break;
                            case 2:
                                configVelocidad();
                                break;
                            case 3:
                                configTiempo();
                                break;
                            case 4:
                                configUmbral();
                                break;
                            default:
                                break;
                        }
                    }catch(Exception e){

                    }
                     */
                }
            }
        }
    }

}
