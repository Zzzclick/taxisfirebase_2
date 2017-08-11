package com.example.zzzclcik.taxisfirebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import android.Manifest;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, LocationListener{

    private GoogleMap mMap;
    ImageView bmapa,bhibrido;
    ImageView cancel,telefonoIma;
    String idTaxi,idUsuario;
    String escuchadorUsu,escuchadorUsuP;
    String escuchadorTax,escuchadorTaxP1,escuchadorTaxP2,escuchadorTaxP3,escuchadorTaxP4,escuchadorTaxP5;
    int numP=0;
    AlertDialog alert = null;
    private DatabaseReference mDatabase,mDatabase2,mDatabaseMap;
    private ValueEventListener listenerTaxis, listenerUsuarios, listenerMapa;
    boolean aux=false,auxT=false;
    String latitud2 = "null1";
    String longitud2 = "null1";
    String latitudTaxi,longuitudTaxi;
    boolean auxViaje = false;
    ValidatorUtil validatorUtil = null;
    private int contador=0;
    String telefonoCel;
    ObtenerWebService hiloconexion;
    public boolean auxMapaNormal=true;
    LocationManager locationManager;
    double longitudeBest, latitudeBest;
    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        validatorUtil = new ValidatorUtil(getApplicationContext());

        idTaxi=getIntent().getStringExtra("idTaxi");
        idUsuario=getIntent().getStringExtra("idUsuario");
        if(idTaxi==null||idUsuario==null)
        {recreate();}
        idUsuario=idUsuario.replace("$","");
        System.out.println("QQQ Taxi:"+idTaxi+" Usuario:"+idUsuario);
        Toast.makeText(getApplicationContext(),"QQQ Taxi"+idTaxi+" Usuario"+idUsuario, Toast.LENGTH_SHORT).show();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        GpsActualizacion();
        bmapa = (ImageView)findViewById(R.id.ImaNormal);
        bhibrido = (ImageView)findViewById(R.id.ImaHibrido);
        cancel=(ImageView)findViewById(R.id.CancelImageView);
        telefonoIma=(ImageView)findViewById(R.id.ImaTel);

        ///////////////////////////////////////////////////////////////////////////////////////////
//Obtiene valor de preferencia (la primera ocasión es por default true).
        boolean muestra2 = getValuePreference(getApplicationContext());




        ////////////////////////Inicio_______///////////////////////////////////////////
        //aqui si no es la primera vez que se abre la activity se oculta el showCaseView
        if(!muestra2){

            saveValuePreference(getApplicationContext(), false);
            contador=3;


        }
        /////////////////////////Fin_______/////////////////////////////////////////////
        bmapa.setOnClickListener(this);

        bhibrido.setOnClickListener(this);
        cancel.setOnClickListener(this);
        telefonoIma.setOnClickListener(this);

        auxViaje = false;


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline()) {
                    AlertTerminarServicio();
                }else Toast.makeText(getApplicationContext(),"Revise su conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ImaNormal:
                if(!auxMapaNormal)
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    auxMapaNormal=true;
                }else
                    {
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        auxMapaNormal=false;
                    }


                break;
            case R.id.ImaHibrido:
                if (validatorUtil.isOnline())
                {
                    ConectarServicio();
                }else Toast.makeText(getApplicationContext(),"Revise su conexión", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ImaTel:
                if (validatorUtil.isOnline())
                {
                    HacerLlamada();
                }else Toast.makeText(getApplicationContext(),"Revise su conexión", Toast.LENGTH_SHORT).show();

                break;
            default:
                break;
        }



        contador++;
    }


    @Override
    protected void onStart() {
        super.onStart();
        CargarPeticionesU();
        CargarPeticionesT();
        auxViaje = false;
        aux=true;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();

        try {
            mDatabase.removeEventListener(listenerTaxis);
            mDatabase.removeEventListener(listenerUsuarios);
            mDatabaseMap.removeEventListener(listenerMapa);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if(alert != null)
        {
            alert.dismiss ();
        }
        aux=false;
    }

    @Override
    public void onBackPressed() {
        AlertTerminarServicio();
        if(alert != null)
        {
            alert.dismiss ();
        }
    }

    private void AlertTerminarServicio() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancelar Servicio");
        builder.setMessage("¿Está seguro de cancelar el servicio de taxi?")
                .setCancelable(false)
                .setPositiveButton("Sí, estoy seguro", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        CancelarViaje();
                        auxViaje = true;
                        try {
                            mDatabase.removeEventListener(listenerTaxis);
                            mDatabase.removeEventListener(listenerUsuarios);
                            mDatabaseMap.removeEventListener(listenerMapa);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    private void AlertServicioCancelado(String Mensaje) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("El  servicio a  finalizado");
        builder.setMessage(Mensaje)
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        if (listenerTaxis != null && mDatabaseMap != null && listenerUsuarios != null) {
                            try {
                                mDatabase.removeEventListener(listenerTaxis);
                                mDatabase.removeEventListener(listenerUsuarios);
                                mDatabaseMap.removeEventListener(listenerMapa);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        finish();
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    }
                });
        alert = builder.create();
        alert.show();
    }
    private  void CancelarViaje()
    {

            try {
                    DatabaseReference mDatabaseVia= FirebaseDatabase.getInstance().getReference().child("taxis");
                    DatabaseReference mDatabaseVia2=mDatabaseVia.child(idTaxi);
                    mDatabaseVia2.child("estado").setValue("0");
                    mDatabaseVia2.child("ViajeA").setValue("0#vacio");
                    DatabaseReference mDatabaseVia3= FirebaseDatabase.getInstance().getReference().child("users");
                    DatabaseReference mDatabaseVia4=mDatabaseVia3.child(idUsuario);
                    mDatabaseVia4.child("estado").setValue("0");
                    mDatabaseVia4.child("ViajeA").setValue("0#vacio");
            } catch (Exception e) {
                e.printStackTrace();
            }

    }
    private void CargarPeticionesT() {

        try {
                mDatabase = FirebaseDatabase.getInstance().getReference().child("taxis").child(idTaxi);
                listenerTaxis = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        escuchadorTax=dataSnapshot.child("ViajeA").getValue().toString();

                        try {
                            if(escuchadorTax.equals("0#vacio")&&aux)
                            {
                                AlertServicioCancelado("El Servicio fue finalizado");
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };mDatabase.addValueEventListener(listenerTaxis);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void CargarPeticionesU() {
        try {
            idUsuario=idUsuario.replace("$","");
            System.out.println("QQQ_TRono aqui:"+idUsuario);////AQUI

                mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(idUsuario);
            System.out.println("Trono aqui "+mDatabase);
                listenerUsuarios = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        try {
                            escuchadorUsu=dataSnapshot.child("estado").getValue().toString();
                            escuchadorUsuP=dataSnapshot.child("ViajeA").getValue().toString();
                            latitud2=dataSnapshot.child("latitud").getValue().toString();
                            longitud2=dataSnapshot.child("longitud").getValue().toString();


                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                mDatabase2.addValueEventListener(listenerUsuarios);
            System.out.println("QQQ si pasa");
        } catch (NullPointerException e) {

            e.printStackTrace();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setTrafficEnabled(true);

        try {
            mDatabaseMap = FirebaseDatabase.getInstance().getReference().child("users").child(idUsuario);
            System.out.println("QQQ Paso de 11 aqui  "+mDatabaseMap);
            listenerMapa = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        try {
                            mMap.clear();
                            latitud2=dataSnapshot.child("latitud").getValue().toString();
                            longitud2=dataSnapshot.child("longitud").getValue().toString();
                            String nombre=dataSnapshot.child("name").getValue().toString();
                            telefonoCel=dataSnapshot.child("telefono").getValue().toString();
                            System.out.println("QQQ_pasa aqui 451");
                            if (latitud2!=null || longitud2!=null||!latitud2.equals("desconocida")||!longitud2.equals("desconocida"))
                            { System.out.println("QQQ_pasa aqui 453");
                                final double latA, lonA;
                                try
                                { System.out.println("QQQ_pasa aqui 456");

                                    System.out.println("QQQ_pasa aqui 464");
                                    latA = Double.parseDouble(latitud2);
                                    lonA = Double.parseDouble(longitud2);
                                    System.out.println("QQQ_pasa aqui 467");
                                    mMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcadorpersona2))
                                            .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                            .title(nombre)
                                            .position(new LatLng(latA,lonA)));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latA, lonA), 20));

                                    System.out.println("QQQ_pasa aqui 475");


                                }catch (NumberFormatException ex)
                                {
                                    Toast.makeText(getApplicationContext(), "Latitud y longitud no son números", Toast.LENGTH_SHORT).show();
                                }catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }else{ //Toast.makeText(getApplicationContext(), "No entro", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }; System.out.println("QQQ_pasa aqui 497");
                mDatabaseMap.addValueEventListener(listenerMapa);
            System.out.println("QQQ "+mDatabaseMap);
            System.out.println("QQQ "+listenerMapa);
            System.out.println("QQQ_pasa aqui 499");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }catch (Exception i)
        {
            i.printStackTrace();
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults) to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null) {
                System.out.println("ENTRA &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                DatabaseReference mDatabaseCoord= FirebaseDatabase.getInstance().getReference().child("taxis");
                DatabaseReference mDatabaseCoord2=mDatabaseCoord.child(idTaxi);
                mDatabaseCoord2.child("latitud").setValue(String.valueOf(location.getLatitude()));
                mDatabaseCoord2.child("longitud").setValue(String.valueOf(location.getLongitude()));
                latitudTaxi=String.valueOf(location.getLatitude());
                longuitudTaxi=String.valueOf(location.getLongitude());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ValidatorUtil validatorUtil = new ValidatorUtil(context);
            if (!validatorUtil.isOnline())
            {
                Toast.makeText(context,"No tienes acceso a Internet",Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkStateReceiver);
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();


    }




    ///////////////////////////////////////Para el showCaseView por primera vez__Inicio///////////////////////////////////////////
    private String PREFS_KEY = "mispreferencias";

    public void saveValuePreference(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("MapActivityTaxi", mostrar);
        editor.commit();
    }



    public boolean getValuePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("MapActivityTaxi", true);
    }




    ///////////////////////////////////////Para el showCaseView por primera vez___Fin///////////////////////////////////////////
    public void HacerLlamada()
    {
        try {
            if (telefonoCel!=null) {
                if (!telefonoCel.equalsIgnoreCase("no")&&telefonoCel.matches("[0-9]*")) {
                    Intent i =new Intent(Intent.ACTION_CALL);
                    telefonoCel=telefonoCel.trim();
                    if(!telefonoCel.isEmpty()&&telefonoCel!=null||telefonoCel.equalsIgnoreCase("no"))
                    {
                        i.setData(Uri.parse("tel:"+telefonoCel));
                    }
                    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermission();
                    }else
                    {
                        startActivity(i);
                    }
                }else if(telefonoCel.equalsIgnoreCase("no")){Toast.makeText(getApplicationContext(),"El usuario no a proporcionado su teléfono", Toast.LENGTH_SHORT).show();}
                else if(!telefonoCel.matches("[0-9]*")){Toast.makeText(getApplicationContext(),"El usuario no a proporcionado su teléfono correctamente", Toast.LENGTH_SHORT).show();}
            }else{
                Toast.makeText(getApplicationContext(),"El usuario no a proporcionado su teléfono", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }
    public void requestPermission()
    {
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.CALL_PHONE},1);
    }


    //////////////////////////////////Para el calculo del tiempo
    public class ObtenerWebService extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            String cadena = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";

            //http://maps.googleapis.com/maps/api/geocode/json?latlng=38.404593,-0.529534&sensor=false
            cadena = cadena + params[0];
            cadena = cadena + "&destinations=";
            cadena = cadena + params[1];
            cadena = cadena + "&language=es&key=AIzaSyDldoJ330Eq-cBIZGD9lMs9_FzeucK5Ctc";
            System.out.println(cadena);

            String devuelve = "";

            URL url = null; // Url de donde queremos obtener información
            try {
                url = new URL(cadena);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //Abrir la conexión
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" +
                        " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                //connection.setHeader("content-type", "application/json");

                int respuesta = connection.getResponseCode();
                StringBuilder result = new StringBuilder();

                if (respuesta == HttpURLConnection.HTTP_OK){


                    InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader

                    // El siguiente proceso lo hago porque el JSONOBject necesita un String y tengo
                    // que tranformar el BufferedReader a String. Esto lo hago a traves de un
                    // StringBuilder.

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);        // Paso toda la entrada al StringBuilder
                    }

                    //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                    JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder pasado a cadena
                    //Accedemos al vector de resultados
                    JSONArray resultJSON = respuestaJSON.getJSONArray("rows");   // rows es el nombre del campo en el JSON
                    JSONObject object_rows=resultJSON.getJSONObject(0);

                    JSONArray array_elements=object_rows.getJSONArray("elements");
                    JSONObject  object_elements=array_elements.getJSONObject(0);

                    JSONObject object_duration=object_elements.getJSONObject("duration");
                    JSONObject object_distance=object_elements.getJSONObject("distance");

                    double distanciaAux=Double.parseDouble(object_distance.getString("value"))/1000;


                    String TiempoAux2=object_duration.getString("text");
                    System.out.println("####################################\nDistancia: "+distanciaAux+" Tiempo:"+TiempoAux2);
                    //Vamos obteniendo todos los campos que nos interesen.
                    //En este caso obtenemos la primera dirección de los resultados.
                    String direccion="SIN DATOS PARA ESA LONGITUD Y LATITUD";

                    direccion="Tiempo "+object_duration.getString("text")+" para llegar\n\nDistancia "+object_distance.getString("text");
                    devuelve =direccion;   // variable de salida que mandaré al onPostExecute para que actualice la UI

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return devuelve;
        }

        @Override
        protected void onCancelled(String aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            StringTokenizer st1 = new StringTokenizer(aVoid, "#");
            System.out.println("Aqui???????????????????????????????\n"+aVoid);
            Toast.makeText(getApplicationContext(),aVoid, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
           // resultado.setText("");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
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
          //  Toast.makeText(getApplicationContext(),"Permisos concedidos", Toast.LENGTH_SHORT).show();
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
            locationManager.requestLocationUpdates(provider, 1000*3* 1, 10, locationListenerBest);
           // Toast.makeText(this, "Best Provider is " + provider, Toast.LENGTH_LONG).show();
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void ConectarServicio()
    {
        String Origen=null,Destino=null;

        Origen=latitudeBest+","+longitudeBest;
        Destino=latitud2+","+longitud2;

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\nOrige="+Origen+" Destino="+Destino);
        hiloconexion = new ObtenerWebService();
        if(latitudeBest!=0&&longitudeBest!=0)
        {
            if(latitud2!=null&&longitud2!=null&&!latitud2.equalsIgnoreCase("desconocida")&&!longitud2.equalsIgnoreCase("desconocida"))
            {
                hiloconexion.execute(Origen,Destino);   // Parámetros que recibe doInBackground
            }


        }else if(latitudeBest==0&&longitudeBest==0)
        {
            Toast.makeText(getApplicationContext(),"Esperando ubicacion intentelo en unos segundos mas tarde", Toast.LENGTH_SHORT).show();
        }

    }


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
                    CargarPeticionesU();
                    CargarPeticionesT();
                    Toast.makeText(getApplicationContext(), "longuitid:"+longitudeBest+"\n"+"latitud:"+latitudeBest, Toast.LENGTH_SHORT).show();
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
            if(grantResults[0]!=PackageManager.PERMISSION_DENIED)
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


    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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
    private static boolean esNumero(String cadena){
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();

    }
}

