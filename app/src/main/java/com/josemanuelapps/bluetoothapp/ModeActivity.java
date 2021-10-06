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
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

public class ModeActivity extends Activity implements Observer {

    private ValorObservable vo2 = null;
    private Activity mActivity;

    Button btnBack, btnRadarBarrido, btnMas, btnMenos;
    TextView txtModo, txtRecibido, txtPosicion, txtDistancia, txtTemperatura;
    Handler bluetoothIn;

    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static String EXTRA_MODE = "device_mode";

    // String for MAC address
    private static String address;

    private int modoNum;
    private String modoText;

    /**
    TextoObservador to = new TextoObservador(BluetoothApplication.vo);

    public ModeActivity( ValorObservable vo , Activity context)
    {
        this.vo2 = vo;
        this.mActivity = context;
    }
*/
    public ModeActivity() {
        super();
    }

    private ValorObservable vo = null;

    public ModeActivity( ValorObservable vo) {
        this.vo = vo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modo);
        BluetoothApplication.enviar = true;

        //ModeActivity ma = new ModeActivity( BluetoothApplication.vo ,this);
        //BluetoothApplication.vo.addObserver(ma);

        TextoObservador to = new TextoObservador( BluetoothApplication.vo, this);
        BluetoothApplication.vo.addObserver( to );

        BluetoothApplication.modeActivity = this;

        //Link the buttons and textViews to respective views
        btnBack = (Button) findViewById(R.id.buttonBack);
        btnRadarBarrido = (Button) findViewById(R.id.buttonRadarBarrido);
        btnMas = (Button) findViewById(R.id.buttonMas);
        btnMenos = (Button) findViewById(R.id.buttonMenos);
        txtModo = (TextView) findViewById(R.id.textViewMode);
        txtRecibido = (TextView) findViewById(R.id.textRecibido);
        txtPosicion = (TextView) findViewById(R.id.textViewPos2);
        txtDistancia = (TextView) findViewById(R.id.textViewDis2);
        txtTemperatura = (TextView) findViewById(R.id.textViewTemp2);

        Intent intent = getIntent();
        modoNum = intent.getIntExtra(EXTRA_MODE,0);
        switch (modoNum){
            case 1:
                modoText = "Modo Automático";
                btnRadarBarrido.setVisibility(View.GONE);
                btnMas.setVisibility(View.GONE);
                btnMenos.setVisibility(View.GONE);
                break;
            case 2:
                modoText = "Modo Manual";
                btnRadarBarrido.setVisibility(View.VISIBLE);
                btnRadarBarrido.setText("Radar On/Off");
                btnMas.setVisibility(View.VISIBLE);
                btnMenos.setVisibility(View.VISIBLE);
                break;
            case 3:
                modoText = "Modo Esporádico";
                btnRadarBarrido.setVisibility(View.VISIBLE);
                btnRadarBarrido.setText("Barrido");
                btnMas.setVisibility(View.GONE);
                btnMenos.setVisibility(View.GONE);
                break;
            case 4:
                modoText = "Configuración";
                btnRadarBarrido.setVisibility(View.GONE);
                btnMas.setVisibility(View.GONE);
                btnMenos.setVisibility(View.GONE);
                break;
            default:
                modoText = "Modo Manual";
                btnRadarBarrido.setVisibility(View.VISIBLE);
                btnRadarBarrido.setText("Radar On/Off");
                btnMas.setVisibility(View.VISIBLE);
                btnMenos.setVisibility(View.VISIBLE);
                break;
        }
        txtModo.setText(modoText);

        // Set up onClick listeners for buttons to send 1 or 0 to turn on/off LED
        btnBack.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BluetoothApplication.mConnectedThread.write("Back\r");    // Send "1" via Bluetooth
                //Toast.makeText(getBaseContext(), "Atrás", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(ModeActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS, BluetoothApplication.address);
                startActivity(i);
                finish();
            }
        });

        btnRadarBarrido.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //mConnectedThread.write("1\r");    // Send "1" via Bluetooth
                if (modoNum==2){
                    // En el caso de pulsar radar On/Off
                    BluetoothApplication.mConnectedThread.write("RadarOn-Off\r");
                    //Toast.makeText(getBaseContext(), "Radar On/Off", Toast.LENGTH_SHORT).show();
                }else{
                    // En el caso de pulsar barrido
                    BluetoothApplication.mConnectedThread.write("Barrido\r");
                    //Toast.makeText(getBaseContext(), "Barrido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnMas.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BluetoothApplication.mConnectedThread.write("+\r");
                //Toast.makeText(getBaseContext(), "Botón Más", Toast.LENGTH_SHORT).show();
            }
        });

        btnMenos.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BluetoothApplication.mConnectedThread.write("-\r");
                //Toast.makeText(getBaseContext(), "Botón Menos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent

    }

    @Override
    public void onPause()
    {
        super.onPause();
        /**
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }*/
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    /**
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), getResources().getText(R.string.device_no_bluetooth), Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }*/

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), getResources().getText(R.string.connection_failure), Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }

    public void update( Observable obs,Object obj ) {
        if( obs == vo2 ) {
            String cadenaRecibida = String.valueOf(vo2.getValor());
            Log.i("josecernu: ", "ObservadorMode2--> " + cadenaRecibida);
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
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (BluetoothApplication.enviar) {
            BluetoothApplication.mConnectedThread.write("Back\r");    // Send "1" via Bluetooth
        }
        //BluetoothApplication.mConnectedThread.write("Null");
        //Toast.makeText(getBaseContext(), "Atrás", Toast.LENGTH_SHORT).show();

        finish();
        Intent i = new Intent(ModeActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS, BluetoothApplication.address);
        startActivity(i);

    }

}