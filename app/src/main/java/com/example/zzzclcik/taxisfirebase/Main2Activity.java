package com.example.zzzclcik.taxisfirebase;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.content.Context;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.location.LocationListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.StringTokenizer;

public class Main2Activity extends AppCompatActivity implements LocationListener,View.OnClickListener{

    public Button aceptarUno,aceptarDos,aceptarTres,aceptarCuatro,aceptarCinco;
    public Button rechazarUno,rechazarDos,rechazarTres,rechazarCuatro,rechazarCinco;
    public TextView mens1,mens2,mens3,mens4,mens5,sinPet;
    public ScrollView sc1,sc2,sc3,sc4,sc5;
    public String nom1,idU1;
    public double lat1,lon1;
    public String nom2,idU2;
    public double lat2,lon2;
    public String nom3,idU3;
    public double lat3,lon3;
    public String nom4,idU4;
    public double lat4,lon4;
    public String nom5,idU5;
    public double lat5,lon5;
    public String dir1,dir2,dir3,dir4,dir5;
    public String idUsusario;
    private DatabaseReference mDatabase;
    private ValueEventListener listenerTaxis;
    private FirebaseAuth mAuth;
    String peticion1,peticion2,peticion3,peticion4,peticion5;
    ValidatorUtil validatorUtil = null;

    private ShowcaseView showcaseView;
    private int contador=0;
    private Target t1,t2,t3,t4,t5;
    View viewLayout;
    MediaPlayer sonido;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.zoom_back_in,R.anim.zoom_back_out);
        setContentView(R.layout.activity_main2);
        sonido = MediaPlayer.create(this, R.raw.tono);
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

        sc1 = (ScrollView)findViewById(R.id.scrollView1);
        sc2 = (ScrollView)findViewById(R.id.scrollView2);
        sc3 = (ScrollView)findViewById(R.id.scrollView3);
        sc4 = (ScrollView)findViewById(R.id.scrollView4);
        sc5 = (ScrollView)findViewById(R.id.scrollView5);

        mens1.setMovementMethod(new ScrollingMovementMethod());
        mens2.setMovementMethod(new ScrollingMovementMethod());
        mens3.setMovementMethod(new ScrollingMovementMethod());
        mens4.setMovementMethod(new ScrollingMovementMethod());
        mens5.setMovementMethod(new ScrollingMovementMethod());

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

        sc1.setVisibility(View.INVISIBLE);
        sc2.setVisibility(View.INVISIBLE);
        sc3.setVisibility(View.INVISIBLE);
        sc4.setVisibility(View.INVISIBLE);
        sc5.setVisibility(View.INVISIBLE);

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

        t1= new ViewTarget(R.id.textViewUno, this);
        t2= new ViewTarget(R.id.buttonUnoRechazar, this);
        t3= new ViewTarget(R.id.buttonUnoAceptar, this);

        ///////////////////////////////////////////////////////////////////////////////////////////
//Obtiene valor de preferencia (la primera ocasión es por default true).
        boolean muestra2 = getValuePreference(getApplicationContext());




        ////////////////////////////////////Inicio////////////////////////////////////////////////////////
        showcaseView=new ShowcaseView.Builder(this)
                .setTarget(Target.NONE)
                .setOnClickListener(this)
                .setContentTitle("Bienvenido")
                .setContentText("Vamos a comenzar")
                .setStyle(R.style.Transparencia)
                .build();
        showcaseView.setButtonText("Siguiente");
        //Aqui se construye el showCaseView
        ////////////////////////////////////Fin////////////////////////////////////////////////////////////
        ////////////////////////Inicio_______///////////////////////////////////////////
        //aqui si no es la primera vez que se abre la activity se oculta el showCaseView
        if(!muestra2){

            saveValuePreference(getApplicationContext(), false);
            contador=5;
            showcaseView.hide();

        }
        /////////////////////////Fin_______/////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        try {
                mAuth=FirebaseAuth.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        aceptarUno.setEnabled(false);
        aceptarDos.setEnabled(false);
        aceptarTres.setEnabled(false);
        aceptarCuatro.setEnabled(false);
        aceptarCinco.setEnabled(false);
        ////////////////////////////////////////////////////////
        rechazarUno.setEnabled(false);
        rechazarDos.setEnabled(false);
        rechazarTres.setEnabled(false);
        rechazarCuatro.setEnabled(false);
        rechazarCinco.setEnabled(false);
        try {
            idUsusario = getIntent().getStringExtra("idUsuario");
            System.out.println("PETICIONES ID DE USUARIO===" + idUsusario);
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }
            //////////////////////////////////////////BOTONES DEL ONCLICK --------Aceptar///////////////////////////////////////////////////////////////////////////
   aceptarUno.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View view) {
           AceptarViaje("peticion2","peticion3","peticion4","peticion5","1",idU1);
       }
   });

    aceptarDos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AceptarViaje("peticion1","peticion3","peticion4","peticion5","2",idU2);
            }
        });

    aceptarTres.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AceptarViaje("peticion1","peticion2","peticion4","peticion5","3",idU3);
        }
    });

    aceptarCuatro.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AceptarViaje("peticion1","peticion2","peticion3","peticion5","4",idU4);
        }
    });

    aceptarCinco.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AceptarViaje("peticion1","peticion2","peticion3","peticion4","5",idU5);
        }
    });

    ///////////////////////////////BOTON ONCLICK RECHAZAR
    rechazarUno.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RechazarViaje("peticion1",idU1);
        }
    });

     rechazarDos.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             RechazarViaje("peticion2",idU2);
         }
     });

    rechazarTres.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RechazarViaje("peticion3",idU3);
        }
    });

    rechazarCuatro.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RechazarViaje("peticion4",idU4);
        }
    });

    rechazarCinco.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RechazarViaje("peticion5",idU5);
        }
    });

    }//

    @Override
    protected void onStart() {
        super.onStart();
TocarTono();
        Toast toastPersonalizado=Toast.makeText(this,"Toast:Gravity.TOP",Toast.LENGTH_LONG);
        toastPersonalizado.setGravity(Gravity.CENTER,0,0);
        toastPersonalizado.setView(viewLayout);
        toastPersonalizado.show();

        CargarValores();
    }

    private void CargarValores()
    {
        final String[] numPeticion = new String[5];
        if (validatorUtil.isOnline()) {
            try {
                    mAuth= FirebaseAuth.getInstance();

                    mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis").child(idUsusario);
                    listenerTaxis = mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            peticion1=dataSnapshot.child("peticion1").getValue().toString();
                            peticion2=dataSnapshot.child("peticion2").getValue().toString();
                            peticion3=dataSnapshot.child("peticion3").getValue().toString();
                            peticion4=dataSnapshot.child("peticion4").getValue().toString();
                            peticion5=dataSnapshot.child("peticion5").getValue().toString();

                            if(peticion1.equals("123"))
                            {
                                mens1.setVisibility(View.INVISIBLE);
                                aceptarUno.setVisibility(View.INVISIBLE);
                                rechazarUno.setVisibility(View.INVISIBLE);
                                sc1.setVisibility(View.INVISIBLE);
                                aceptarUno.setEnabled(false);
                                rechazarUno.setEnabled(false);
                                mens1.setText("Sin Petición");
                            }
                            else
                            {
                                if(peticion1!=null||!peticion1.equals(""))
                                {
                                    mens1.setVisibility(View.VISIBLE);
                                    aceptarUno.setVisibility(View.VISIBLE);
                                    rechazarUno.setVisibility(View.VISIBLE);
                                    sc1.setVisibility(View.VISIBLE);
                                    StringTokenizer st = new StringTokenizer(peticion1, "¥");
                                    numPeticion[0] = st.nextToken();
                                    nom1 = st.nextToken();
                                    lat1 = Double.parseDouble(st.nextToken());
                                    lon1 = Double.parseDouble(st.nextToken());
                                    idU1 = st.nextToken();
                                    dir1 = st.nextToken();
                                    mens1.setText(nom1 + dir1);
                                    aceptarUno.setEnabled(true);
                                    rechazarUno.setEnabled(true);
                                    System.out.println(dir1 + "SASFGDGHHDFGDHJHKAsdafghjklkasdasfghfgjkssdfghjklsdsfsssSDFGHJK");
                                    ///////////////////////
                                    TocarTono();
                                    //////////////////////
                                }
                            }
                            if(peticion2.equals("123"))
                            {
                                mens2.setVisibility(View.INVISIBLE);
                                aceptarDos.setVisibility(View.INVISIBLE);
                                rechazarDos.setVisibility(View.INVISIBLE);
                                sc2.setVisibility(View.INVISIBLE);
                                aceptarDos.setEnabled(false);
                                rechazarDos.setEnabled(false);
                                mens2.setText("Sin Petición");
                            }
                            else
                            {
                                if(peticion2!=null||!peticion2.equals(""))
                                {
                                    mens2.setVisibility(View.VISIBLE);
                                    aceptarDos.setVisibility(View.VISIBLE);
                                    rechazarDos.setVisibility(View.VISIBLE);
                                    sc2.setVisibility(View.VISIBLE);
                                    StringTokenizer st = new StringTokenizer(peticion2, "¥");
                                    numPeticion[1] = st.nextToken();
                                    nom2 = st.nextToken();
                                    lat2 = Double.parseDouble(st.nextToken());
                                    lon2 = Double.parseDouble(st.nextToken());
                                    idU2 = st.nextToken();
                                    dir2 = st.nextToken();
                                    mens2.setText(nom2 + dir2);
                                    aceptarDos.setEnabled(true);
                                    rechazarDos.setEnabled(true);
                                    ///////////////////////
                                    TocarTono();
                                    //////////////////////
                                }
                            }
                            if(peticion3.equals("123"))
                            {
                                mens3.setVisibility(View.INVISIBLE);
                                aceptarTres.setVisibility(View.INVISIBLE);
                                rechazarTres.setVisibility(View.INVISIBLE);
                                sc3.setVisibility(View.INVISIBLE);
                                aceptarTres.setEnabled(false);
                                rechazarTres.setEnabled(false);
                                mens3.setText("Sin Petición");
                            }
                            else
                            {
                                if(peticion3!=null||!peticion3.equals(""))
                                {
                                    mens3.setVisibility(View.VISIBLE);
                                    aceptarTres.setVisibility(View.VISIBLE);
                                    rechazarTres.setVisibility(View.VISIBLE);
                                    sc3.setVisibility(View.VISIBLE);
                                    StringTokenizer st = new StringTokenizer(peticion3, "¥");
                                    numPeticion[2] = st.nextToken();
                                    nom3 = st.nextToken();
                                    lat3 = Double.parseDouble(st.nextToken());
                                    lon3 = Double.parseDouble(st.nextToken());
                                    idU3 = st.nextToken();
                                    dir3 = st.nextToken();
                                    mens3.setText(nom3 + dir3);
                                    aceptarTres.setEnabled(true);
                                    rechazarTres.setEnabled(true);
                                    ///////////////////////
                                    TocarTono();
                                    //////////////////////
                                }
                            }
                            if(peticion4.equals("123"))
                            {
                                mens4.setVisibility(View.INVISIBLE);
                                aceptarCuatro.setVisibility(View.INVISIBLE);
                                rechazarCuatro.setVisibility(View.INVISIBLE);
                                sc4.setVisibility(View.INVISIBLE);
                                aceptarCuatro.setEnabled(false);
                                rechazarCuatro.setEnabled(false);
                                mens4.setText("Sin Petición");
                            }
                            else
                            {
                                if(peticion4!=null||!peticion4.equals(""))
                                {
                                    mens4.setVisibility(View.VISIBLE);
                                    aceptarCuatro.setVisibility(View.VISIBLE);
                                    rechazarCuatro.setVisibility(View.VISIBLE);
                                    sc4.setVisibility(View.VISIBLE);
                                    StringTokenizer st = new StringTokenizer(peticion4, "¥");
                                    numPeticion[3] = st.nextToken();
                                    nom4 = st.nextToken();
                                    lat4 = Double.parseDouble(st.nextToken());
                                    lon4 = Double.parseDouble(st.nextToken());
                                    idU4 = st.nextToken();
                                    dir4 = st.nextToken();
                                    mens4.setText(nom4 + dir4);
                                    aceptarCuatro.setEnabled(true);
                                    rechazarCuatro.setEnabled(true);
                                    ///////////////////////
                                    TocarTono();
                                    //////////////////////
                                }
                            }
                            if(peticion5.equals("123"))
                            {
                                mens5.setVisibility(View.INVISIBLE);
                                aceptarCinco.setVisibility(View.INVISIBLE);
                                rechazarCinco.setVisibility(View.INVISIBLE);
                                sc5.setVisibility(View.INVISIBLE);
                                aceptarCinco.setEnabled(false);
                                rechazarCinco.setEnabled(false);
                                mens5.setText("Sin Petición");

                            }
                            else
                            {
                                if(peticion5!=null||!peticion5.equals(""))
                                {
                                    mens5.setVisibility(View.VISIBLE);
                                    aceptarCinco.setVisibility(View.VISIBLE);
                                    rechazarCinco.setVisibility(View.VISIBLE);
                                    sc5.setVisibility(View.VISIBLE);
                                    StringTokenizer st = new StringTokenizer(peticion5, "¥");
                                    numPeticion[4] = st.nextToken();
                                    nom5 = st.nextToken();
                                    lat5 = Double.parseDouble(st.nextToken());
                                    lon5 = Double.parseDouble(st.nextToken());
                                    idU5 = st.nextToken();
                                    dir5 = st.nextToken();
                                    mens5.setText(nom5 + dir5);
                                    aceptarCinco.setEnabled(true);
                                    rechazarCinco.setEnabled(true);
                                    ///////////////////////
                                    TocarTono();
                                    //////////////////////
                                }
                            }
                            ////
                            if(peticion5.equals("123")&&peticion4.equals("123")&&peticion3.equals("123")&&peticion2.equals("123")&&peticion1.equals("123"))
                            {
                                sinPet.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }catch (Exception i)
            {
                i.printStackTrace();
            }
        }
        else
            Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
    }

    public  void TocarTono()
    {



        sinPet.setVisibility(View.INVISIBLE);
    }

    public void AceptarViaje(String nomPeticion1, String nomPeticion2, String nomPeticion3, String nomPeticion4, String numeroPeticion, String numIdU)
    {
        if (validatorUtil.isOnline()) {
            try {
                DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
                DatabaseReference currentUserBD=mDatabase.child(idUsusario);
                currentUserBD.child("estado").setValue("2");
                currentUserBD.child(nomPeticion1).setValue("123");
                currentUserBD.child(nomPeticion2).setValue("123");
                currentUserBD.child(nomPeticion3).setValue("123");
                currentUserBD.child(nomPeticion4).setValue("123");
                currentUserBD.child("ViajeA").setValue("1#$"+numIdU);
                DatabaseReference mDatabase2= FirebaseDatabase.getInstance().getReference().child("users");
                DatabaseReference currentUserBD2=mDatabase2.child(numIdU);
                currentUserBD2.child("estado").setValue("2");
                currentUserBD2.child("ViajeA").setValue("1"+"#"+idUsusario);

                Intent intent = new Intent(Main2Activity.this,MapsActivity.class);

                intent.putExtra("idUsuario",numIdU);
                intent.putExtra("idTaxi",idUsusario);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
    }

    public void RechazarViaje(String numPeticion, String numIdU)
    {
        if (validatorUtil.isOnline()) {
            try {
                    DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
                    DatabaseReference currentUserBD=mDatabase.child(idUsusario);
                    currentUserBD.child(numPeticion).setValue("123");
                    DatabaseReference mDatabase2= FirebaseDatabase.getInstance().getReference().child("users");
                    DatabaseReference currentUserBD2=mDatabase2.child(numIdU);
                    currentUserBD2.child("estado").setValue("0");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        try
        {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("taxis");
                DatabaseReference currentUserBD = mDatabase.child(idUsusario);
                currentUserBD.child("latitud").setValue(String.valueOf(location.getLatitude()));
                currentUserBD.child("longitud").setValue(String.valueOf(location.getLongitude()));
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerTaxis != null) {
            mDatabase.removeEventListener(listenerTaxis);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.zoom_back_in,R.anim.zoom_back_out);
        if (listenerTaxis != null) {
            mDatabase.removeEventListener(listenerTaxis);
        }
        Intent i = new Intent(getApplicationContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
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
        if (listenerTaxis != null) {
            mDatabase.removeEventListener(listenerTaxis);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listenerTaxis != null) {
            mDatabase.removeEventListener(listenerTaxis);
        }
    }



    ///////////////////////////////////////Para el showCaseView por primera vez__Inicio///////////////////////////////////////////
    private String PREFS_KEY = "mispreferencias";

    public void saveValuePreference(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("MainActivity2", mostrar);
        editor.commit();
    }



    public boolean getValuePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("MainActivity2", true);
    }

    @Override
    public void onClick(View v) {
        switch (contador) {
            case 0:
                showcaseView.setShowcase(t1, true);
                showcaseView.setContentTitle("peticion");
                showcaseView.setContentText("Aqui podras Visualizar la peticion que te enviaron ");
                break;
            case 1:

                showcaseView.setShowcase(t2, true);
                showcaseView.setContentTitle("Rechazar");
                showcaseView.setContentText("presiona para rechazar");
                break;
            case 2:
                showcaseView.setShowcase(t3, true);
                showcaseView.setContentTitle("Aceptar");
                showcaseView.setContentText("presiona para aceptar");
                showcaseView.setButtonText("Finalizar");
                break;

            case 3:
                showcaseView.hide();
                boolean muestra2 = getValuePreference(getApplicationContext());
                if(muestra2)
                {
                    saveValuePreference(getApplicationContext(), false);
                    //  Toast.makeText(getApplicationContext(),"Primera vez:"+muestra, Toast.LENGTH_LONG).show();
                }
                break;
            default:
                showcaseView.hide();
                boolean muestra21 = getValuePreference(getApplicationContext());
                if(muestra21)
                {
                    saveValuePreference(getApplicationContext(), false);
                    //  Toast.makeText(getApplicationContext(),"Primera vez:"+muestra, Toast.LENGTH_LONG).show();
                }
                break;
        }

        contador++;
    }

    /////////////////Volver a mosrtrar el ShowCaseView_______Inicio//////////////////////////////////////////////
    public  void Ayuda()
    {
        contador=0;
        showcaseView.show();
        showcaseView.setTarget(Target.NONE);
        showcaseView.setContentTitle("Bienvenido");
        showcaseView  .setContentText("Vamos a comenzar");
        showcaseView.setButtonText("Siguiente");
    }
    /////////////////Volver a mosrtrar el ShowCaseView_______Final//////////////////////////////////////////////

    ///////////////////////////////////////Para el showCaseView por primera vez___Fin///////////////////////////////////////////

}
