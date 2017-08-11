package com.example.zzzclcik.taxisfirebase;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.StringTokenizer;

public class Usuario extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener , View.OnClickListener{
    private Button btnLogOut,irBandeja;
    private ToggleButton disponibilidad;
    private ImageView imagePerfil,imagenTaxi;
    private TextView txtName,txtperfil;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private int CAMERA_REGUEST_CODE=0;
    private ProgressDialog progressDialog;
    private StorageReference mStorage;
    private DatabaseReference mDatabase,mDataBaseCoord,mDataBaseViaje,mDatabaseViaje;
    private static final String LOGTAG = "android-localizacion";
    AlertDialog alert = null;
    private String idUsusario,getIdUsuario,estado,tipoUser;

    private static final int PETICION_PERMISO_LOCALIZACION = 101;

    private GoogleApiClient apiClient;
    LocationManager locationManager;
    View viewLayout;
    ValidatorUtil validatorUtil = null;


    private int contador=0;

    boolean TipoTaxi=false;
    double lat1,long1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.zoom_back_in,R.anim.zoom_back_out);
        setContentView(R.layout.activity_usuario);
        getSupportActionBar().setTitle("Inicio");
        validatorUtil = new ValidatorUtil(getApplicationContext());
        String carpetaFuente = "fonts/Graphik-Bold.otf";
        Typeface fuente = Typeface.createFromAsset(getAssets(), carpetaFuente);

        LayoutInflater layoutInflater=getLayoutInflater();
        viewLayout =layoutInflater.inflate(R.layout.custom_toast_usuario,(ViewGroup)findViewById(R.id.custom_layout2));

        txtName = (TextView) findViewById(R.id.txtNombre);
        btnLogOut = (Button) findViewById(R.id.singOut);
        imagePerfil = (ImageView) findViewById(R.id.imageView);
        imagenTaxi = (ImageView)findViewById(R.id.imageGps);
        disponibilidad = (ToggleButton)findViewById(R.id.toggleButton);
        txtperfil = (TextView)findViewById(R.id.textView3);
        irBandeja=(Button)findViewById(R.id.IrButton);

        irBandeja.setVisibility(View.INVISIBLE);
        disponibilidad.setVisibility(View.INVISIBLE);




        ///////////////////////////////////////////////////////////////////////////////////////////
//Obtiene valor de preferencia (la primera ocasión es por default true).
        boolean muestra2 = getValuePreference(getApplicationContext());




        ////////////////////////////////////Inicio////////////////////////////////////////////////////////

        //Aqui se construye el showCaseView
        ////////////////////////////////////Fin////////////////////////////////////////////////////////////
        ////////////////////////Inicio_______///////////////////////////////////////////
        //aqui si no es la primera vez que se abre la activity se oculta el showCaseView
        if(!muestra2){


        }
        /////////////////////////Fin_______/////////////////////////////////////////////
        try {
            mAuth=FirebaseAuth.getInstance();
            idUsusario=getIntent().getStringExtra("idUsuario");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        txtName.setTypeface(fuente);
        btnLogOut.setTypeface(fuente);
        disponibilidad.setTypeface(fuente);
        txtperfil.setTypeface(fuente);


        System.out.println("Usuario ID DE USUARIO==="+idUsusario);

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        /****Mejora****/
        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            AlertNoGps();
        }
        /********/
        imagePerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validatorUtil.isOnline()) {
                    Intent intent=new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    if(intent.resolveActivity(getPackageManager())!=null)
                    {
                        startActivityForResult(Intent.createChooser(intent,"Seleciona una foto de perfil"),CAMERA_REGUEST_CODE);
                    }
                }
                else
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (validatorUtil.isOnline()) {
                    if(mAuth.getCurrentUser().getUid()!=null)
                    {
                        try {
                            mAuth=FirebaseAuth.getInstance();
                            String user_id = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserBD=mDatabase.child(user_id);
                            currentUserBD.child("estado").setValue("1");
                            mAuth.signOut();
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(intent);
                            overridePendingTransition(R.anim.right_in, R.anim.right_out);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(700);
                        }catch (NullPointerException ex){}
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.exit(0);
                    }
                }
                else
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
            }
        });

        progressDialog=new ProgressDialog(this);

            try {
                    mAuth=FirebaseAuth.getInstance();
                    mAuthListener=new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            if(firebaseAuth.getCurrentUser().getUid()!=null)
                            {
                                mStorage= FirebaseStorage.getInstance().getReference();
                                mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
                                mDatabase.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String imageUrl= null;
                                        try {
                                            txtName.setText(dataSnapshot.child("name").getValue().toString());
                                            imageUrl = dataSnapshot.child("image").getValue().toString();
                                            estado = dataSnapshot.child("estado").getValue().toString();
                                            if(dataSnapshot.child("estado").getValue().toString().trim().equals("0"))
                                            {
                                                disponibilidad.setChecked(true);
                                                disponibilidad.setBackgroundResource(R.drawable.boton_redondo);
                                            }
                                            else {
                                                disponibilidad.setChecked(false);
                                                disponibilidad.setBackgroundResource(R.drawable.boton_redondo2);
                                            }
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }
                                        try
                                        {
                                        if(!imageUrl.equals("default")|| TextUtils.isEmpty(imageUrl))
                                        {
                                            Picasso.with(Usuario.this).load(Uri.parse(dataSnapshot.child("image").getValue().toString())).into(imagePerfil);
                                        }
                                        }
                                        catch(NullPointerException e){}
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }else{
                                Intent i = new Intent(Usuario.this, MainActivity.class);
                                finish();
                                startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                            }
                        }
                    };
                    if(estado!=null)
                    {
                    if(estado.equals("0"))
                    {
                        disponibilidad.setChecked(true);
                    }
                    else if (estado.equals("1"))
                    {
                        disponibilidad.setChecked(false);
                    }
                    }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }



        imagenTaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (validatorUtil.isOnline())
                    {
                        Intent intent = new Intent(getApplicationContext(),BandejaPeticiones.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    } else{ Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();}

            }
        });

        disponibilidad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (validatorUtil.isOnline()) {
                    try {
                        mAuth=FirebaseAuth.getInstance();
                        String user_id = mAuth.getCurrentUser().getUid();
                        if (user_id != null) {
                            DatabaseReference currentUserBD=mDatabase.child(user_id);
                            if(isChecked)
                            {
                                if (estado.equals("1")) {
                                    disponibilidad.setBackgroundResource(R.drawable.boton_redondo);
                                    disponibilidad.setTextOn("Disponible");
                                    currentUserBD.child("estado").setValue("0");
                                }
                            }
                            else
                            {
                                if (estado.equals("0")) {
                                    disponibilidad.setBackgroundResource(R.drawable.boton_redondo2);
                                    disponibilidad.setTextOff("No Disponible");
                                    currentUserBD.child("estado").setValue("1");
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
            }
        });

        irBandeja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatorUtil.isOnline())
                {
                Intent intent = new Intent(getApplicationContext(),BandejaPeticiones.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                } else{ Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();}
            }
        });
        EscuchadoViaje();
    }

    public String getRamdomString()
    {
        SecureRandom random =new SecureRandom();
        return new BigInteger(130,random).toString(32);
    }
    @Override
    protected void onStart()
    {
        super.onStart();

        VerificarTipo();


        Toast toastPersonalizado=Toast.makeText(this,"Toast:Gravity.TOP",Toast.LENGTH_SHORT);
        toastPersonalizado.setGravity(Gravity.TOP,0,0);
        toastPersonalizado.setView(viewLayout);
        //toastPersonalizado.show();

            try {
                    mAuth.addAuthStateListener(mAuthListener);
                    mDatabase=FirebaseDatabase.getInstance().getReference().child("taxis");
                    getIdUsuario=mAuth.getCurrentUser().getUid();
                    final DatabaseReference currentUserBD=mDatabase.child(mAuth.getCurrentUser().getUid());
                    currentUserBD.child("latitud").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String value = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    currentUserBD.child("longitud").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String value = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    currentUserBD.child("estado").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            estado = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            } catch (NullPointerException e) {
                e.printStackTrace();
            }


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    private void AlertNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

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

    @Override
    protected void onDestroy(){
        CerrarSesion();
        super.onDestroy();
        if(alert != null)
        {
            alert.dismiss ();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (validatorUtil.isOnline()) {
            try {
                    if(requestCode==CAMERA_REGUEST_CODE&&resultCode==RESULT_OK)
                    {
                        if(mAuth.getCurrentUser()==null)
                            return;
                        progressDialog.setMessage("Subiendo imagen");
                        progressDialog.show();
                        final Uri uri=data.getData();
                        if(uri==null)
                        {
                            progressDialog.dismiss();
                            return;
                        }
                        if(mAuth.getCurrentUser()==null)
                            return;
                        if(mStorage==null)
                            mStorage=FirebaseStorage.getInstance().getReference();
                        if(mDatabase==null)
                            mDatabase=FirebaseDatabase.getInstance().getReference().child("taxis");
                        final StorageReference filepath=mStorage.child("photoT").child(getRamdomString());
                        final DatabaseReference currentUserBD=mDatabase.child(mAuth.getCurrentUser().getUid());




                        currentUserBD.child("image").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String image=dataSnapshot.getValue().toString();
                                if(!image.equals("default")&&!image.isEmpty())
                                {

                                    Task<Void> task=FirebaseStorage.getInstance().getReferenceFromUrl(image).delete();
                                    task.addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                                Toast.makeText(Usuario.this, "Foto antigua eliminada correctamente", Toast.LENGTH_SHORT).show();
                                            else
                                                Toast.makeText(Usuario.this, "Hubo un error al eliminar la foto antigua", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }


                                currentUserBD.child("image").removeEventListener(this);
                                filepath.putFile(uri).addOnSuccessListener(Usuario.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        progressDialog.dismiss();
                                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                                        Toast.makeText(Usuario.this, "Finalizado", Toast.LENGTH_SHORT).show();
                                        Picasso.with(Usuario.this).load(uri).fit().centerCrop().into(imagePerfil);
                                        DatabaseReference currentUserDB=mDatabase.child(mAuth.getCurrentUser().getUid());
                                        currentUserDB.child("image").setValue(downloadUri.toString());
                                    }
                                }).addOnFailureListener(Usuario.this, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Usuario.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        else
            Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle)
    {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);


                updateUI(lastLocation);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
        Toast.makeText(Usuario.this, "Se ha interrumpido la conexión con Google Play Services", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
        Toast.makeText(Usuario.this, "Error grave al conectar con Google Play Services", Toast.LENGTH_SHORT).show();
    }

    private void updateUI(Location loc) {
        try {
                if (loc != null) {
                    mAuth = FirebaseAuth.getInstance();
                    String user_id = mAuth.getCurrentUser().getUid();
                    if (user_id != null) {
                        lat1=loc.getLatitude();
                        long1=loc.getLongitude();
                        DatabaseReference dataBaseCoord = mDataBaseCoord.child(user_id);
                        dataBaseCoord.child("latitud").setValue(String.valueOf(loc.getLatitude()));
                        dataBaseCoord.child("longitud").setValue(String.valueOf(loc.getLongitude()));
                    }
                }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    public void IniciarSesion()
    {
        try {
            if(getIdUsuario!=null)
            {
                DatabaseReference mDatabase2= FirebaseDatabase.getInstance().getReference().child("taxis");
                DatabaseReference currentUserBD2=mDatabase2.child(getIdUsuario);
                currentUserBD2.child("sesion").setValue("1");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    public void CerrarSesion()
    {
        try {
            if(getIdUsuario!=null)
            {
                DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference().child("taxis");
                DatabaseReference currentUserBD2 = mDatabase2.child(getIdUsuario);
                currentUserBD2.child("sesion").setValue("0");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.item1:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
                if (location != null) {
                    mAuth=FirebaseAuth.getInstance();
                    String user_id = mAuth.getCurrentUser().getUid();
                    if (user_id != null)
                    {
                        lat1=location.getLatitude();
                        long1=location.getLongitude();
                        DatabaseReference dataBaseCoord=mDataBaseCoord.child(user_id);
                        dataBaseCoord.child("latitud").setValue(String.valueOf(location.getLatitude()));
                        dataBaseCoord.child("longitud").setValue(String.valueOf(location.getLongitude()));
                    }

                   double latMax=20.257066,latMin=19.987402,lonMax=98.929459,lonMin=98.160273;
                    if(lat1<latMax&&lat1>latMin)
                    {
                        if((long1*-1)<lonMax&&(long1*-1)>lonMin)
                        {

                        }else
                            {

                            }
                    }else
                        {

                        }
                }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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


    public void VerificarTipo() {

        try {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("tipo");
            mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {
                        tipoUser = (dataSnapshot.getValue().toString());
                       // Toast.makeText(getApplicationContext(), "Tipo Usuario:" + tipoUser, Toast.LENGTH_SHORT).show();

                       if (tipoUser.equals("usuario"))
                       {
                        Toast.makeText(getApplicationContext(), "Esta cuenta ya esta como usuario\n por favor use otra cuenta para usar la aplicacion" , Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(),"Debe usar otra cuenta",Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                       }
                       else if(tipoUser.equals("taxi"))
                       {
                        Toast.makeText(getApplicationContext(), "Usuario valido:" , Toast.LENGTH_SHORT).show();
                       }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        System.out.println("|||||||||||||||||||||Trono aqui En Tipo de usuario");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {
        }


    }
    ///////////////////////////////////////Para el showCaseView por primera vez__Inicio///////////////////////////////////////////
    private String PREFS_KEY = "mispreferencias";

    public void saveValuePreference(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("Usuario1", mostrar);
        editor.commit();
    }



    public boolean getValuePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("Usuario1", true);
    }

    @Override
    public void onClick(View v) {


    }

    /////////////////Volver a mosrtrar el ShowCaseView_______Inicio//////////////////////////////////////////////

    /////////////////Volver a mosrtrar el ShowCaseView_______Final//////////////////////////////////////////////

    ///////////////////////////////////////Para el showCaseView por primera vez___Fin///////////////////////////////////////////
    public void EscuchadoViaje()
    {
        try {
            mDatabaseViaje = FirebaseDatabase.getInstance().getReference().child("taxis");
            mDatabaseViaje= mDatabaseViaje.child(mAuth.getCurrentUser().getUid()).child("ViajeA");
            mDatabaseViaje.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {

                        String idTaxi=mAuth.getCurrentUser().getUid();
                        idTaxi=idTaxi.replace("$","");
                        System.out.println("WWWW2"+mDatabaseViaje);
                        String escuchadorViaje = dataSnapshot.getValue().toString();
                        StringTokenizer token = new StringTokenizer(escuchadorViaje, "#");
                        String idUsuario="nada",estado;
                        estado=token.nextToken();
                        idUsuario=token.nextToken();
                        System.out.println("estado="+estado+"\nid="+idTaxi);
                        if (!escuchadorViaje.equals("0#vacio"))
                        {
                            if (validatorUtil.isOnline()) {
                                Intent intent = new Intent(getApplicationContext(),MapsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.putExtra("idTaxi",idTaxi);
                                intent.putExtra("idUsuario",idUsuario);
                                System.out.println("Cargando datos de viaje22\n"+idTaxi+" \n"+idUsuario);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(),"Cargando datos de viaje", Toast.LENGTH_SHORT).show();
                            }else Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                            System.out.println("QQQ pasa pasa");
                        }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        System.out.println("|||||||||||||||||||||Trono aqui En cargar Viaje");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {
        }
    }
}
