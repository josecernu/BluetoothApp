package com.josemanuelapps.bluetoothapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends Activity {

    Button btn1, btn2, btn3, btn4, btnExit;
    Handler bluetoothIn;

    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    //private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // EXTRA string to send on to mainactivity
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // EXTRA string to send on to mainactivity
    public static String EXTRA_MODE = "device_mode";

    // String for MAC address
    private static String address;

    //ModeActivity ma = new ModeActivity( BluetoothApplication.vo );


    ValorObservable vo2 = new ValorObservable("");
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Link the buttons and textViews to respective views
        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);
        btn4 = (Button) findViewById(R.id.button4);
        btnExit = (Button) findViewById(R.id.buttonExit);

        //TextoObservador to = new TextoObservador( BluetoothApplication.vo);
        //BluetoothApplication.vo.addObserver( to );

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("\r\n");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        Log.i("josecernu: ","MainActivity: "+ dataInPrint);
                        BluetoothApplication.vo.setValor(dataInPrint);
                        vo2.setValor(dataInPrint);
                        if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {
                            String subcadena = recDataString.substring(0,3);
                            if (subcadena.equalsIgnoreCase("#00")){
                                Log.i("josecernu: ","Entra por menú principal");
                                String datos = recDataString.substring(3,4);
                                Log.i("josecernu: ","Datos: "+datos);
                                // Estamos en el menú principal
                                //txtString.setText(getResources().getText(R.string.data_received)+" = " + dataInPrint);
                                //int dataLength = dataInPrint.length();                          //get length of data received
                                //txtStringLength.setText(getResources().getText(R.string.string_lenght)+" = " + String.valueOf(dataLength));
                                try {
                                    int pantalla = Integer.parseInt(datos);
                                    Log.i("josecernu: ","Pantalla: "+pantalla);
                                    switch (pantalla){
                                        case 1:
                                            modoAutomatico();
                                            break;
                                        case 2:
                                            modoManual();
                                            break;
                                        case 3:
                                            modoEsporadico();
                                            break;
                                        case 4:
                                            configuracion();
                                            break;
                                        default:
                                            break;
                                    }
                                }catch(Exception e){

                                }
                            }
                        }
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        dataInPrint = " ";
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        btn1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BluetoothApplication.mConnectedThread.write("1\r");    // Send "1" via Bluetooth
                //Toast.makeText(getBaseContext(), "Modo Automático", Toast.LENGTH_SHORT).show();

                modoAutomatico();
            }
        });

        btn2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BluetoothApplication.mConnectedThread.write("2\r");    // Send "1" via Bluetooth
                //Toast.makeText(getBaseContext(), "Modo Manual", Toast.LENGTH_SHORT).show();

                modoManual();
            }
        });

        btn3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BluetoothApplication.mConnectedThread.write("3\r");    // Send "1" via Bluetooth
                //Toast.makeText(getBaseContext(), "Modo Esporádico", Toast.LENGTH_SHORT).show();

                modoEsporadico();
            }
        });

        btn4.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BluetoothApplication.mConnectedThread.write("4\r");    // Send "1" via Bluetooth
                //Toast.makeText(getBaseContext(), "Configuración", Toast.LENGTH_SHORT).show();

                configuracion();
            }
        });

        btnExit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                closeApp();
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        if (BluetoothApplication.firstRunning)
        {
            BluetoothApplication.firstRunning = false;
            //Get MAC address from DeviceListActivity via intent
            Intent intent = getIntent();

            //Get the MAC address from the DeviceListActivty via EXTRA
            address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

            //create device and set the MAC address
            if (address != "1") {
                BluetoothDevice device = btAdapter.getRemoteDevice(address);

                try {
                    btSocket = createBluetoothSocket(device);
                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), getResources().getText(R.string.socket_creation_failed), Toast.LENGTH_LONG).show();
                }
                // Establish the Bluetooth socket connection.
                try {
                    btSocket.connect();
                } catch (IOException e) {
                    try {
                        btSocket.close();
                    } catch (IOException e2) {
                        //insert code to deal with this
                    }
                }
                BluetoothApplication.mConnectedThread = new ConnectedThread(btSocket, bluetoothIn);
                BluetoothApplication.mConnectedThread.start();

            }
        }
        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        //mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //try
        //{
        //    //Don't leave Bluetooth sockets open when leaving activity
        //    btSocket.close();
        //} catch (IOException e2) {
        //    //insert code to deal with this
        //}
    }

    @Override
    public void onBackPressed() {
        // ¿Está seguro que quiere cerrar la aplicación?
        closeApp();
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
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
    }

    private void closeApp(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Salir")
                .setMessage("¿Estás seguro de que quieres cerrar la aplicación?")
                .setNegativeButton(android.R.string.cancel, null)//sin listener
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {//un listener que al pulsar, cierre la aplicacion
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        try
                        {
                            //Don't leave Bluetooth sockets open when leaving activity
                            if (btSocket != null)
                                btSocket.close();
                        } catch (IOException e2) {
                            //insert code to deal with this
                        }
                        //Salir
                        finish();
                        System.exit(0);
                    }
                })
                .show();
    }


    private void modoAutomatico(){
        Intent i = new Intent(MainActivity.this, ModeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(EXTRA_DEVICE_ADDRESS, address);
        i.putExtra(EXTRA_MODE, 1);
        startActivity(i);
        finish();
    }

    private void modoManual(){
        Intent i = new Intent(MainActivity.this, ModeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(EXTRA_DEVICE_ADDRESS, address);
        i.putExtra(EXTRA_MODE, 2);
        startActivity(i);
        finish();
    }

    private void modoEsporadico(){
        Intent i = new Intent(MainActivity.this, ModeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(EXTRA_DEVICE_ADDRESS, address);
        i.putExtra(EXTRA_MODE, 3);
        startActivity(i);
        finish();
    }

    private void configuracion(){
        Intent i = new Intent(MainActivity.this, ConfigurationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(EXTRA_DEVICE_ADDRESS, address);
        i.putExtra(EXTRA_MODE, 4);
        startActivity(i);
        finish();
    }

}
