package com.example.zzzclcik.taxisfirebase;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BandejaPeticiones extends AppCompatActivity {
    public Button aceptarUno,aceptarDos,aceptarTres,aceptarCuatro,aceptarCinco;
    public Button rechazarUno,rechazarDos,rechazarTres,rechazarCuatro,rechazarCinco;
    public TextView mens1,mens2,mens3,mens4,mens5,sinPet;


    private DatabaseReference mDatabase,mDatabaseCordenadas;
    private ValueEventListener listenerTaxis;
    private FirebaseAuth mAuth;
    ValidatorUtil validatorUtil = null;

    private int contador=0;
    View viewLayout;

    LocationManager locationManager;
    double longitudeBest=0, latitudeBest=0;
    String latitudOrigen,longitudOrigen;
    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    String MiId,MiNombre;

    ValueEventListener listener1, listener2,listenerCoordenadas;

    String contenidoPeticiones;
    ArrayList<String> peticionesArray=new ArrayList<>();
    ArrayList<String> igual=new ArrayList<>();
    ArrayList<String> guion=new ArrayList<>();
    ArrayList<String> idArray2=new ArrayList<>();
    ArrayList<Double> latitudArray=new ArrayList<>();
    ArrayList<Double> longuitudArray=new ArrayList<>();
    ArrayList<String> comentarioArray=new ArrayList<>();
    ArrayList<String> tiempoArray=new ArrayList<>();
    ArrayList<String> ignorarArray=new ArrayList<>();
    ArrayList<String> aceptadosArray=new ArrayList<>();
    ArrayList<Double> latitudArray2=new ArrayList<>();
    ArrayList<Double> longuitudArray2=new ArrayList<>();
    ArrayList<String> comentarioArray2=new ArrayList<>();
    ArrayList<String> tiempoArray2=new ArrayList<>();
    MediaPlayer sonido;
    String id1,id2,id3,id4,id5;
    String auxB1,auxB2,auxB3,auxB4,auxB5,ignorados="",escuchadorV,idUsuario;
    boolean auxIgnorar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bandeja_peticiones);
        mAuth=FirebaseAuth.getInstance();
        MiId = mAuth.getCurrentUser().getUid();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        validatorUtil = new ValidatorUtil(getApplicationContext());
        CordenadasInicio();
        GpsActualizacion();
         ignorarArray.clear();
        sonido = MediaPlayer.create(this, R.raw.tono);

        //Toast.makeText(getApplicationContext(),"Nombre"+MiNombre, Toast.LENGTH_SHORT).show();


        getSupportActionBar().setTitle("Bandeja de peticiones");
        validatorUtil = new ValidatorUtil(getApplicationContext());
        String carpetaFuente = "fonts/Graphik-Bold.otf";
        Typeface fuente = Typeface.createFromAsset(getAssets(), carpetaFuente);

        LayoutInflater layoutInflater=getLayoutInflater();
        viewLayout =layoutInflater.inflate(R.layout.custom_toast,(ViewGroup)findViewById(R.id.custom_layout));

        aceptarUno=(Button)findViewById(R.id.buttonUnoAceptar);
        aceptarDos=(Button)findViewById(R.id.buttonDosAceptar);
        aceptarTres=(Button)findViewById(R.id.buttonTresAceptar);
        aceptarCuatro=(Button)findViewById(R.id.buttonCuatroAceptar);
        aceptarCinco=(Button)findViewById(R.id.buttonCincoAceptar);
        ////////////////////////////////////////////////////////
        rechazarUno=(Button)findViewById(R.id.buttonUnoRechazar);
        rechazarDos=(Button)findViewById(R.id.buttonDosRechazar);
        rechazarTres=(Button)findViewById(R.id.buttonTresRechazar);
        rechazarCuatro=(Button)findViewById(R.id.buttonCuatroRechazar);
        rechazarCinco=(Button)findViewById(R.id.buttonCincoRechazar);

//////////////////////////////////////////////////////////////////////////////////7
        mens1=(TextView)findViewById(R.id.textViewUno);
        mens2=(TextView)findViewById(R.id.textViewDos);
        mens3=(TextView)findViewById(R.id.textViewTres);
        mens4=(TextView)findViewById(R.id.textViewCuatro);
        mens5=(TextView)findViewById(R.id.textViewCinco);
        sinPet=(TextView)findViewById(R.id.SinPeticiones_textView);



        mens1.setMovementMethod(new ScrollingMovementMethod());
        mens2.setMovementMethod(new ScrollingMovementMethod());
        mens3.setMovementMethod(new ScrollingMovementMethod());
        mens4.setMovementMethod(new ScrollingMovementMethod());
        mens5.setMovementMethod(new ScrollingMovementMethod());
////////////////////////////Para hacer invisible los componentes_____INICIO/////////////////////////////////////////////////////////////////////        mens1.setVisibility(View.INVISIBLE);
        sinPet.setVisibility(View.VISIBLE);
        mens2.setVisibility(View.INVISIBLE);
        mens3.setVisibility(View.INVISIBLE);
        mens4.setVisibility(View.INVISIBLE);
        mens5.setVisibility(View.INVISIBLE);

        aceptarUno.setVisibility(View.INVISIBLE);
        aceptarDos.setVisibility(View.INVISIBLE);
        aceptarTres.setVisibility(View.INVISIBLE);
        aceptarCuatro.setVisibility(View.INVISIBLE);
        aceptarCinco.setVisibility(View.INVISIBLE);

        rechazarUno.setVisibility(View.INVISIBLE);
        rechazarDos.setVisibility(View.INVISIBLE);
        rechazarTres.setVisibility(View.INVISIBLE);
        rechazarCuatro.setVisibility(View.INVISIBLE);
        rechazarCinco.setVisibility(View.INVISIBLE);


////////////////////////////Para hacer invisible los componentes___FIN/////////////////////////////////////////////////////////////////////

////////////////////////Para configurar el tipo de letra____INICIO///////////////////////////////////////////////////////////////////
        mens1.setTypeface(fuente);
        mens2.setTypeface(fuente);
        mens3.setTypeface(fuente);
        mens4.setTypeface(fuente);
        mens5.setTypeface(fuente);

        aceptarUno.setTypeface(fuente);
        aceptarDos.setTypeface(fuente);
        aceptarTres.setTypeface(fuente);
        aceptarCuatro.setTypeface(fuente);
        aceptarCinco.setTypeface(fuente);

        rechazarUno.setTypeface(fuente);
        rechazarDos.setTypeface(fuente);
        rechazarTres.setTypeface(fuente);
        rechazarCuatro.setTypeface(fuente);
        rechazarCinco.setTypeface(fuente);
////////////////////////Para configurar el tipo de letra___FIN///////////////////////////////////////////////////////////////////



////////////////////////////////////////////////Paa los onclicks de Rechazar____INICIO/////////////////////////////////////////////////////////
        rechazarUno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    auxB1=BorrarPeticion(id1);
                    Toast.makeText(getApplicationContext(),auxB1, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
        rechazarDos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    auxB2=BorrarPeticion(id2);
                    Toast.makeText(getApplicationContext(),auxB2, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
        rechazarTres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    auxB3=BorrarPeticion(id3);
                    Toast.makeText(getApplicationContext(),auxB3, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
        rechazarCuatro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    auxB4=BorrarPeticion(id4);
                    Toast.makeText(getApplicationContext(),auxB4, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
        rechazarCinco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    auxB5=BorrarPeticion(id5);
                    Toast.makeText(getApplicationContext(),auxB5, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
////////////////////////////////////////////////Paa los onclicks de Rechazar_____FIN/////////////////////////////////////////////////////////



///////////////////////////////////////////////Para los Onclicks para Aceptar__INICIO/////////////////////////////////////////////////////////
        aceptarUno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    AceptarPeticion(id1);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
        aceptarDos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    AceptarPeticion(id2);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
        aceptarTres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    AceptarPeticion(id3);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
        aceptarCuatro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    AceptarPeticion(id4);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });
        aceptarCinco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                    AceptarPeticion(id5);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                }

            }
        });

///////////////////////////////////////////////Para los Onclicks para Aceptar____FIN/////////////////////////////////////////////////////////
    }

    @Override
    protected void onStart()
    {
        ignorarArray.clear();

                    ObtenerPeticiones();
        IniciarEscuchadorV();


        super.onStart();
    }

    @Override
    protected void onPostResume() {
      ObtenerPeticiones();
        super.onPostResume();
    }

    @Override
    protected void onRestart() {
        ObtenerPeticiones();
        super.onRestart();
    }

    @Override
    protected void onStop() {
        try {
            mDatabase.removeEventListener(listener2);
            mDatabaseCordenadas.removeEventListener(listenerCoordenadas);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
            mDatabase.removeEventListener(listener2);
            mDatabaseCordenadas.removeEventListener(listenerCoordenadas);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        try {
            mDatabase.removeEventListener(listener2);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        finish();
        System.exit(0);

        super.onBackPressed();
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();

        return isLocationEnabled();

    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void GpsActualizacion()
    {
        checkLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},PETICION_PERMISO_LOCALIZACION);
        }
        else
        {

        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null)
        {
            locationManager.requestLocationUpdates(provider, 1000*60* 1, 10, locationListenerBest);

        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private final LocationListener locationListenerBest = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeBest = location.getLongitude();
            latitudeBest = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // longitudeValueBest.setText(longitudeBest + "");
                    // latitudeValueBest.setText(latitudeBest + "");
                    //Toast.makeText(BandejaPeticiones.this, "longuitid:"+longitudeBest+"\n"+"latitud:"+latitudeBest, Toast.LENGTH_SHORT).show();
                    latitudOrigen=String.valueOf(latitudeBest);
                    longitudOrigen=String.valueOf(longitudeBest);
                    try {
                        if (MiNombre!=null||!MiNombre.equals(""))
                        {
                            mDatabaseCordenadas.removeEventListener(listenerCoordenadas);
                        }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    ObtenerPeticiones();

                    try
                    {
                        if (latitudOrigen != null&&longitudOrigen!=null) {


                            if (MiId != null) {

                                DatabaseReference mDatabaseCoord = FirebaseDatabase.getInstance().getReference().child("taxis");
                                DatabaseReference mDatabaseCoord2 = mDatabaseCoord.child(MiId);
                                mDatabaseCoord2.child("latitud").setValue(latitudOrigen.toString());
                                mDatabaseCoord2.child("longitud").setValue(longitudOrigen.toString());
                            }
                        } else {
                        }
                    }catch (NullPointerException e)
                    {
                        e.printStackTrace();
                    }

                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };
//////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==PETICION_PERMISO_LOCALIZACION)
        {
            if(grantResults[0]!= PackageManager.PERMISSION_DENIED)
            {
                GpsActualizacion();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"permisos denegados", Toast.LENGTH_SHORT).show();
                GpsActualizacion();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
//////////////////////////////////////////////////////////______ObtenerPeticiones____________INICIO/////////////////////
    public void ObtenerPeticiones()
    {
        HacerInvisible();
        sinPet.setVisibility(View.INVISIBLE);
        if (validatorUtil.isOnline()) {
            try {
                mDatabase= FirebaseDatabase.getInstance().getReference().child("peticiones");
                listener2 = new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        HacerInvisible();
                        try {
                            contenidoPeticiones = dataSnapshot.getValue().toString();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        if (contenidoPeticiones != null) {
                            if (latitudeBest != 0 && longitudeBest != 0)
                            {

                                //Toast.makeText(getApplicationContext(),"Con coordenadas", Toast.LENGTH_SHORT).show();
                            peticionesArray.clear();
                            igual.clear();
                            guion.clear();
                            idArray2.clear();
                            latitudArray.clear();
                            longuitudArray.clear();
                            comentarioArray.clear();
                            comentarioArray2.clear();
                            tiempoArray.clear();
                            tiempoArray2.clear();
                            aceptadosArray.clear();


                            contenidoPeticiones = contenidoPeticiones.replace("{", "");
                            contenidoPeticiones = contenidoPeticiones.replace("}", "");
                            StringTokenizer token1 = new StringTokenizer(contenidoPeticiones, ",");
                            StringTokenizer token2;
                            StringTokenizer token3;

                            int num = token1.countTokens();
                            String idArray[];
                            String aux;

                            for (int i = 0; i < num; i++) {

                                peticionesArray.add(token1.nextToken());
                            }

                            for (int i = 0; i < peticionesArray.size(); i++) {
                                aux = peticionesArray.get(i);
                                token2 = new StringTokenizer(aux, "=");
                                num = token2.countTokens();
                                for (int j = 0; j < num; j++) {
                                    igual.add(token2.nextToken());
                                }
                            }

                            for (int i = 0; i < igual.size(); i++) {

                                aux = igual.get(i);
                                token3 = new StringTokenizer(aux, "_");
                                num = token3.countTokens();
                                for (int j = 0; j < num; j++) {

                                    guion.add(token3.nextToken());
                                }
                            }
                            //Mostrar resultados de separacion
                            int contadorAux = 0;//es para que la separacion de cada cinco es uno
                            //para no mostrar los rechazados

                            for (int i = 0; i < guion.size(); i++) {

                                if (i % 5 == 0) {
                                    contadorAux = 0;
                                }


                                switch (contadorAux) {
                                    case 0:
                                        if (!auxIgnorar) {
                                            idArray2.add(guion.get(i).replaceAll(" ", ""));
                                            //System.out.println("FUERA DEL ARRAY" +i);
                                        }
                                        break;
                                    case 1:
                                        if (!auxIgnorar) {
                                            try {
                                                latitudArray.add(Double.parseDouble(guion.get(i)));
                                            } catch (NumberFormatException e) {
                                                e.printStackTrace();
                                                System.out.println("NO SON NUMEROS REVISA LATITUD");
                                            }
                                        }
                                        break;
                                    case 2:
                                        if (!auxIgnorar) {
                                            try {
                                                longuitudArray.add(Double.parseDouble(guion.get(i)));
                                            } catch (NumberFormatException e) {
                                                e.printStackTrace();
                                                System.out.println("NO SON NUMEROS REVISA LONGUITUD");
                                            }
                                        }
                                        break;
                                    case 3:
                                        if (!auxIgnorar) {
                                            comentarioArray.add(guion.get(i));
                                        }
                                        break;
                                    case 4:
                                        if (!auxIgnorar) {
                                            tiempoArray.add(guion.get(i));

                                        }
                                        break;
                                    default:
                                     //   Toast.makeText(getApplicationContext(), "Error en i", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                                auxIgnorar = false;
                                contadorAux++;
                                // System.out.println("Contador: "+contadorAux);
                                //System.out.println("Separacion"+i+": "+guion.get(i)+"\n");
                            }


                            double latitudMenos, latitudMas, longuitudMenos, longuitudMas, distanciaCoordenadas;
                            boolean auxWhile = true;
                            distanciaCoordenadas = 0.010529;


                           //Toast.makeText(getApplicationContext(),"aquiiii\n"+latitudeBest, Toast.LENGTH_SHORT).show();
                            latitudMenos = latitudeBest - distanciaCoordenadas;
                            latitudMas = latitudeBest + distanciaCoordenadas;
                            longuitudMenos = (longitudeBest * -1) - distanciaCoordenadas;//////////
                            longuitudMas = (longitudeBest * -1) + distanciaCoordenadas;/////////////Se  convierten en negativos para poder hacer el filtro de distancia por que si son negativos nose manejan como debera ser
                            int contadorBotones = 0;
                            int numIgnorado;


                            for (int j = 0; j < ignorarArray.size(); j++)
                            {
                                for (int i = 0; i < idArray2.size(); i++)
                                {
                                    if (idArray2.get(i).equals(ignorarArray.get(j)))
                                    {
                                        numIgnorado = i;
                                        System.out.println(idArray2.get(i) + " ANTES");
                                        System.out.println("NUMERO IGNORADO ES " + numIgnorado);
                                        idArray2.remove(numIgnorado);
                                        latitudArray.remove(numIgnorado);
                                        longuitudArray.remove(numIgnorado);
                                        comentarioArray.remove(numIgnorado);
                                        tiempoArray.remove(numIgnorado);
                                        System.out.println(idArray2.size() + " CCC " + latitudArray.size() + " CCC " + longuitudArray.size() + " CCC " + comentarioArray.size() + " CCC " + tiempoArray.size());
                                    }
                                }
                            }


                            for (int i = 0; i < idArray2.size(); i++)
                            {
                                if (latitudArray.get(i) >= latitudMenos && latitudArray.get(i) <= latitudMas)
                                {

                                    if ((longuitudArray.get(i) * -1) >= (longuitudMenos) && (longuitudArray.get(i) * -1) <= (longuitudMas))////////////Aqui tambien se convierten s positivos para que se puedan manejar y hacer el filtro
                                    {
                                        try {
                                            aceptadosArray.add(idArray2.get(i));
                                        } catch (IndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                        }


                                        if (contadorBotones <= 5)
                                        {

                                            switch (contadorBotones)
                                            {
                                                case 0:
                                                    PlayTono();
                                                    sinPet.setVisibility(View.INVISIBLE);
                                                    rechazarUno.setVisibility(View.VISIBLE);
                                                    aceptarUno.setVisibility(View.VISIBLE);

                                                    try {
                                                        mens1.setVisibility(View.VISIBLE);
                                                        mens1.setText("Comentario\n" + comentarioArray.get(i).toString() + "\n\nTiempo aproximado " + tiempoArray.get(i).toString());
                                                        id1 = idArray2.get(i);
                                                    } catch (IndexOutOfBoundsException e) {
                                                        e.printStackTrace();
                                                    }

                                                    break;
                                                case 1:
                                                    PlayTono();
                                                    sinPet.setVisibility(View.INVISIBLE);
                                                    rechazarDos.setVisibility(View.VISIBLE);
                                                    aceptarDos.setVisibility(View.VISIBLE);

                                                    try {
                                                        mens2.setVisibility(View.VISIBLE);
                                                        mens2.setText("Comentario\n" + comentarioArray.get(i).toString() + "\n\nTiempo aproximado " + tiempoArray.get(i).toString());
                                                        id2 = idArray2.get(i);
                                                    } catch (IndexOutOfBoundsException e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;
                                                case 2:
                                                    PlayTono();
                                                    sinPet.setVisibility(View.INVISIBLE);
                                                    rechazarTres.setVisibility(View.VISIBLE);
                                                    aceptarTres.setVisibility(View.VISIBLE);

                                                    try {
                                                        mens3.setVisibility(View.VISIBLE);
                                                        mens3.setText("Comentario\n" + comentarioArray.get(i).toString() + "\n\nTiempo aproximado " + tiempoArray.get(i).toString());
                                                        id3 = idArray2.get(i);
                                                    } catch (IndexOutOfBoundsException e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;
                                                case 3:
                                                    PlayTono();
                                                    sinPet.setVisibility(View.INVISIBLE);
                                                    rechazarCuatro.setVisibility(View.VISIBLE);
                                                    aceptarCuatro.setVisibility(View.VISIBLE);

                                                    try {
                                                        mens4.setVisibility(View.VISIBLE);
                                                        mens4.setText("Comentario\n" + comentarioArray.get(i).toString() + "\n\nTiempo aproximado " + tiempoArray.get(i).toString());
                                                        id4 = idArray2.get(i);
                                                    } catch (IndexOutOfBoundsException e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;
                                                case 4:
                                                    PlayTono();
                                                    sinPet.setVisibility(View.INVISIBLE);
                                                    rechazarCinco.setVisibility(View.VISIBLE);
                                                    aceptarCinco.setVisibility(View.VISIBLE);

                                                    try {
                                                        mens5.setVisibility(View.VISIBLE);
                                                        mens5.setText("Comentario\n" + comentarioArray.get(i).toString() + "\n\nTiempo aproximado " + tiempoArray.get(i).toString());
                                                        id5 = idArray2.get(i);
                                                    } catch (IndexOutOfBoundsException e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;
                                                default:
                                                    Toast.makeText(getApplicationContext(), "Hay más de cinco peticiones", Toast.LENGTH_SHORT).show();
                                                    break;
                                            }
                                            contadorBotones++;
                                        }
                                    }
                                }
                            }

                        }else{}

                        }
                        ///////////
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };mDatabase.addValueEventListener(listener2);

            } catch (NullPointerException e) {
                e.printStackTrace();
            }catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else
            Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();

    }
/////////////////////////////////////////////////////////_ObtenerPeticiones____________FIN////////////////////////////

////////////////////////////////////////////////////TocarTono___INICIO///////////////////////////////////////////////
    public  void PlayTono()
    {


            sonido.start();
            //Toast.makeText(getApplicationContext(),"Duracion del audio="+sonido.getDuration(), Toast.LENGTH_SHORT).show();
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long [] patron = {100,50,50,50};
        v.vibrate(patron,3);



        sinPet.setVisibility(View.INVISIBLE);
    }
////////////////////////////////////////////////////7TocarTono___FIN///////////////////////////////////////////////

    public String BorrarPeticion(String id)
    {

    if(id!=null&&!id.equals(""))
    {

        id=id.replaceAll(" ","");

        ignorarArray.add(id);


        ObtenerPeticiones();
        return "Agregado a lista de ignorados";
    }else return "false__";



    }

    public void HacerInvisible()
    {
        mens1.setVisibility(View.INVISIBLE);
        mens2.setVisibility(View.INVISIBLE);
        mens3.setVisibility(View.INVISIBLE);
        mens4.setVisibility(View.INVISIBLE);
        mens5.setVisibility(View.INVISIBLE);

        aceptarUno.setVisibility(View.INVISIBLE);
        aceptarDos.setVisibility(View.INVISIBLE);
        aceptarTres.setVisibility(View.INVISIBLE);
        aceptarCuatro.setVisibility(View.INVISIBLE);
        aceptarCinco.setVisibility(View.INVISIBLE);

        rechazarUno.setVisibility(View.INVISIBLE);
        rechazarDos.setVisibility(View.INVISIBLE);
        rechazarTres.setVisibility(View.INVISIBLE);
        rechazarCuatro.setVisibility(View.INVISIBLE);
        rechazarCinco.setVisibility(View.INVISIBLE);


    }
    public void AceptarPeticion(String id)
    {
        if(id!=null&&!id.equals(""))
        {
            try {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                DatabaseReference currentUserBD = mDatabase.child(id);
                DatabaseReference currentUserBD2 = currentUserBD.child("peticiones");
                System.out.println(currentUserBD2+" #######");
                currentUserBD2.child(MiId).setValue(MiNombre);
                String retorno= BorrarPeticion(id);
                Toast.makeText(getApplicationContext(),retorno+"\n"+"Peticion enviada", Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }else{}

    }
    public void CordenadasInicio()
    {

        try {
            mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
            mDatabaseCordenadas =mDatabase.child(MiId);
          //  Toast.makeText(getApplicationContext(),"aqui\n"+mDatabaseCordenadas, Toast.LENGTH_SHORT).show();
            System.out.println("AAA aqui\n"+mDatabaseCordenadas);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        listenerCoordenadas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try {
                    latitudeBest=Double.parseDouble(dataSnapshot.child("latitud").getValue().toString());
                    longitudeBest=Double.parseDouble(dataSnapshot.child("longitud").getValue().toString());
                    MiNombre=dataSnapshot.child("name").getValue().toString();

                    //Toast.makeText(getApplicationContext(),"Entro\n "+mDatabaseCordenadas+"\n"+latitudeBest+"\n"+longitudeBest, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(),"NOMBRE\n "+MiNombre, Toast.LENGTH_SHORT).show();
                    } catch (NullPointerException e) { e.printStackTrace();  }
                catch (NumberFormatException e) { e.printStackTrace();  }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        try {
            mDatabaseCordenadas.addValueEventListener(listenerCoordenadas);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    public void IniciarEscuchadorV()
    {
        //0#vacio

        try {
            mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
            mDatabaseCordenadas =mDatabase.child(MiId);
           // Toast.makeText(getApplicationContext(),"aqui\n"+mDatabaseCordenadas, Toast.LENGTH_SHORT).show();
            System.out.println("AAA aqui\n"+mDatabaseCordenadas);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        listenerCoordenadas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                try {
                    escuchadorV=dataSnapshot.child("ViajeA").getValue().toString();
                  // Toast.makeText(getApplicationContext(),"UUU_ "+escuchadorV, Toast.LENGTH_SHORT).show();
                    if(!escuchadorV.equals("0#vacio"))
                    {
                        StringTokenizer st = new StringTokenizer(escuchadorV, "#$");
                        idUsuario=st.nextToken();
                        Intent intent = new Intent(BandejaPeticiones.this,MapsActivity.class);

                        if (idUsuario!=null||!idUsuario.isEmpty()) {
                            intent.putExtra("idUsuario",idUsuario);
                            intent.putExtra("idTaxi",MiId);
                            finish();

                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            startActivity(intent);

                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                System.exit(0);

                        }else
                            {//Toast.makeText(getApplicationContext(),"Id Incorrecto Hay que revisarlo", Toast.LENGTH_SHORT).show();
                            }
                        //#$
                    }
                } catch (NullPointerException e) { e.printStackTrace();  }
                catch (NumberFormatException e) { e.printStackTrace();  }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        try {
            mDatabaseCordenadas.addValueEventListener(listenerCoordenadas);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}

