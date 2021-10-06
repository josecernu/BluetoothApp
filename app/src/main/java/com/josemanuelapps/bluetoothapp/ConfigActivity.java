package com.josemanuelapps.bluetoothapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

public class ConfigActivity extends Activity implements Observer {

    private ValorObservable vo2 = null;
    private Activity mActivity;

    Button btnBack, btnPredeterminado, btnMas, btnMenos;
    TextView txtConfig, txtInfo;
    Handler bluetoothIn;

    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static String EXTRA_CONFIG = "config_id";

    // String for MAC address
    private static String address;

    private int modoNum;
    private String modoText;

    public ConfigActivity() {
        super();
    }

    private ValorObservable vo = null;

    public ConfigActivity(ValorObservable vo) {
        this.vo = vo;
    }

    private int min_value = 0;
    private int max_value = 0;
    private int incremento = 5;
    private int predeterminado = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("josecernu","ConfigActivity");

        setContentView(R.layout.activity_config);
        BluetoothApplication.enviar = true;

        //ModeActivity ma = new ModeActivity( BluetoothApplication.vo ,this);
        //BluetoothApplication.vo.addObserver(ma);

        TextoObservador to = new TextoObservador( BluetoothApplication.vo, this);
        BluetoothApplication.vo.addObserver( to );
        BluetoothApplication.configActivity = this;

        //Link the buttons and textViews to respective views
        btnBack = (Button) findViewById(R.id.buttonBack);
        btnPredeterminado = (Button) findViewById(R.id.buttonPredeterminado);
        btnMas = (Button) findViewById(R.id.buttonMas);
        btnMenos = (Button) findViewById(R.id.buttonMenos);
        txtConfig = (TextView) findViewById(R.id.textViewConfig);
        txtInfo = (TextView) findViewById(R.id.textViewInfo2);

        Intent intent = getIntent();
        modoNum = intent.getIntExtra(EXTRA_CONFIG,0);
        switch (modoNum){
            case 1:
                modoText = "Grados";
                min_value = 0;
                max_value = 180;
                incremento = 5;
                predeterminado = 45;
                break;
            case 2:
                modoText = "Velocidad";
                min_value = 0;
                max_value = 2000;
                incremento = 100;
                predeterminado = 500;
                break;
            case 3:
                modoText = "Tiempo de barridos";
                min_value = 0;
                max_value = 3000;
                incremento = 100;
                predeterminado = 500;
                break;
            case 4:
                modoText = "Umbral detección";
                min_value = 0;
                max_value = 250;
                incremento = 5;
                predeterminado = 90;
                break;
            default:
                modoText = "Configuración";
                break;
        }
        txtConfig.setText(modoText);
        txtInfo.setText(predeterminado+"");

        // Set up onClick listeners for buttons to send 1 or 0 to turn on/off LED
        btnBack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BluetoothApplication.mConnectedThread.write("Back\r");    // Send "1" via Bluetooth
                //Toast.makeText(getBaseContext(), "Atrás", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(ConfigActivity.this, ConfigurationActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS, BluetoothApplication.address);
                startActivity(i);
                finish();
            }
        });

        // Hacer switch para valor predeterminado
        btnPredeterminado.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BluetoothApplication.mConnectedThread.write("predeterminado\r");    // Send "1" via Bluetooth
                txtInfo.setText(predeterminado+"");
            }
        });

        btnMas.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BluetoothApplication.mConnectedThread.write("+\r");
                //Toast.makeText(getBaseContext(), "Botón Más", Toast.LENGTH_SHORT).show();

                int valor = Integer.parseInt(txtInfo.getText().toString());
                int nuevoValor = valor + incremento;
                if (nuevoValor <= max_value){
                    txtInfo.setText(nuevoValor+"");
                }
            }
        });

        btnMenos.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BluetoothApplication.mConnectedThread.write("-\r");
                //Toast.makeText(getBaseContext(), "Botón Menos", Toast.LENGTH_SHORT).show();

                int valor = Integer.parseInt(txtInfo.getText().toString());
                int nuevoValor = valor - incremento;
                if (nuevoValor >= min_value){
                    txtInfo.setText(nuevoValor+"");
                }
            }
        });

    }


    public void update( Observable obs,Object obj ) {
        if( obs == vo2 ) {
            String cadenaRecibida = String.valueOf(vo2.getValor());
            Log.i("josecernu: ", "ObservadorMode2--> " + cadenaRecibida);
            /*
            if (cadenaRecibida.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
            {
                String subcadena = cadenaRecibida.substring(1,3);
                if (subcadena.equalsIgnoreCase("01")){
                    // Estamos en uno de los 3 modos
                    TextView txtRecibido = (TextView) mActivity.findViewById(R.id.textRecibido);
                    txtRecibido.setText(getResources().getText(R.string.data_received)+" = " + cadenaRecibida);
                    //int dataLength = cadenaRecibida.length();
                    //txtLongitud.setText(getResources().getText(R.string.string_lenght)+" = " + String.valueOf(dataLength));
                    String[] datos = cadenaRecibida.substring(3).split("#");
                    txtPosicion.setText(datos[0]);
                    txtDistancia.setText(datos[1]);
                    txtTemperatura.setText(datos[2]);
                    Log.i("josecernu: ","Cadena tratada: "+datos);
                }
            }*/
        }
    }

    @Override
    public void onBackPressed() {
        if (BluetoothApplication.enviar) {
            BluetoothApplication.mConnectedThread.write("Back\r");    // Send "1" via Bluetooth
        }
        //Toast.makeText(getBaseContext(), "Atrás", Toast.LENGTH_SHORT).show();
        //BluetoothApplication.mConnectedThread.write("Null");

        finish();
        Intent i = new Intent(ConfigActivity.this, ConfigurationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS, BluetoothApplication.address);
        startActivity(i);

    }

}