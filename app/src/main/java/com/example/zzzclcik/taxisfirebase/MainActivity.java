package com.example.zzzclcik.taxisfirebase;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener {
    private EditText textEmail;
    private EditText textPass;
    private TextView resetClave;
    private Button btnRegister,registrar;
    private Boolean aux=false;
    private DatabaseReference mDatabase;
    public String idTaxi,MiId;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static final String TAG = "NOTICIAS";
    ValidatorUtil validatorUtil = null;


    private int contador=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.right_in,R.anim.right_out);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Iniciar Sesión");

        validatorUtil = new ValidatorUtil(getApplicationContext());
        String carpetaFuente = "fonts/Graphik-Bold.otf";
        Typeface fuente = Typeface.createFromAsset(getAssets(), carpetaFuente);
        try {
            mAuth=FirebaseAuth.getInstance();
            MiId=mAuth.getCurrentUser().getUid();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        resetClave = (TextView) findViewById(R.id.resetClave);
        resetClave.setEnabled(false);

        progressDialog=new ProgressDialog(this);
        textEmail=(EditText)findViewById(R.id.editTextEmail);
        textPass=(EditText)findViewById(R.id.editTextClave);
        btnRegister=(Button)findViewById(R.id.buttonEntrar);
        registrar=(Button)findViewById(R.id.buttonRegistarC);

        resetClave.setTypeface(fuente);
        textEmail.setTypeface(fuente);
        textPass.setTypeface(fuente);
        btnRegister.setTypeface(fuente);

        ///////////////////////////////////////////////////////////////////////////////////////////
//Obtiene valor de preferencia (la primera ocasión es por default true).
        boolean muestra2 = getValuePreference(getApplicationContext());




        ////////////////////////////////////Inicio////////////////////////////////////////////////////////

        //Aqui se construye el showCaseView
        ////////////////////////////////////Fin////////////////////////////////////////////////////////////
        ////////////////////////Inicio_______///////////////////////////////////////////
        //aqui si no es la primera vez que se abre la activity se oculta el showCaseView

        /////////////////////////Fin_______/////////////////////////////////////////////
        try {
                mAuth=FirebaseAuth.getInstance();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }



        String token = null;
        try {
            if(validatorUtil.isOnline())
            {
                token = FirebaseInstanceId.getInstance().getToken();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Token: " + token);





        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (validatorUtil.isOnline()) {




                        AceptarTerminos();
                        saveValuePreference4(getApplicationContext(), true);


                }
                else
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();

            }
        });

        resetClave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validatorUtil.isOnline()) {
                    try
                    {
                    mAuth.sendPasswordResetEmail(textEmail.getText().toString().trim());
                    Toast.makeText(MainActivity.this, "Correo enviado\nrevisa tu correo", Toast.LENGTH_SHORT).show();
                    resetClave.setText("");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
            }
        });




                            if(MiId!=null)
                            {
                                System.out.println("QQQ 191");
                                mDatabase= FirebaseDatabase.getInstance().getReference().child("taxis");
                                idTaxi=MiId;
                                System.out.println("QQQ 193");
                                mDatabase.child(idTaxi).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String ValorViaje="";
                                        try {
                                            ValorViaje = (dataSnapshot.child("ViajeA").getValue().toString());

                                            if(ValorViaje!=null||!ValorViaje.equals(""))
                                        {
                                            StringTokenizer token = new StringTokenizer(ValorViaje, "#");
                                            String idUsuario="nada",estado;
                                            estado=token.nextToken();
                                            idUsuario=token.nextToken();

                                        if(!ValorViaje.equals("0#vacio")) {
                                            if (validatorUtil.isOnline())
                                            {
                                            Intent intent = new Intent(getApplicationContext(), MapsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            intent.putExtra("idTaxi", idTaxi);
                                            intent.putExtra("idUsuario", idUsuario);
                                            finish();
                                           // startActivity(intent);
                                            overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                            }else Toast.makeText(getApplicationContext(),"No es posible conectarse ahora",Toast.LENGTH_LONG).show();
                                        }


                                        }
                                        //(5
                                        } catch (NullPointerException e) { e.printStackTrace();
                                            System.out.println("|||||||||||||||||||||TRono aqui "); }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                try
                                {
                                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                Intent i = new Intent(getApplicationContext(),Usuario.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                i.putExtra("idUsuario",MiId);
                                System.out.println("Login ID DE USUARIO==="+MiId);
                                finish();
                                startActivity(i);
                                overridePendingTransition(R.anim.left_in,R.anim.left_out);
                                }
                                //mAuth.signOut();
                                catch(NullPointerException e)
                                {

                                }
                            }




        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Registro.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
    @Override
    protected void onStart()
    {
        super.onStart();

    }

    public void doLogin()
    {
        try {
                String email=textEmail.getText().toString().trim();
                String password=textPass.getText().toString().trim();
                if(!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)) {
                    char[] arrayChar2 = password.toCharArray();
                    if (arrayChar2.length > 5) {
                        aux = ValidatorUtil.validateEmail(email);
                        if (aux) {

                            progressDialog.setMessage("Entrando,espere por favor");
                            progressDialog.show();
                            try
                            {
                            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Intent i= new Intent(getApplicationContext(),Usuario.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        finish();
                                        startActivity(i);
                                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                                    } else {
                                        resetClave.setEnabled(true);
                                        resetClave.setText("Recuperar contraseña aqui");
                                    }
                                }
                            });
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }else {textEmail.setError("Correo inválido");}
                    }else {textPass.setError("La contraseña debe tener mínimo 6 caracteres");}
                }else {Toast toast1 = Toast.makeText(MainActivity.this, "Por favor introduce datos", Toast.LENGTH_SHORT);
                    toast1.setGravity(Gravity.CENTER, 0, 0);
                    toast1.show();}
        } catch (Exception e) {
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



    ///////////////////////////////////////Para el showCaseView por primera vez__Inicio///////////////////////////////////////////
    private String PREFS_KEY = "mispreferencias";

    public void saveValuePreference(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("MainActivity", mostrar);
        editor.commit();
    }



    public boolean getValuePreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("MainActivity", true);
    }

    @Override
    public void onClick(View v) {
        switch (contador) {

        }

        contador++;
    }


    /////////////////Volver a mosrtrar el ShowCaseView_______Final//////////////////////////////////////////////

    ///////////////////////////////////////Para el showCaseView por primera vez___Fin///////////////////////////////////////////


    //////////////////////////////////Metodos para guardar y obtener datos cuando se voltea el cel_______INICIO/////////////////
    @Override
    protected void onSaveInstanceState(Bundle estado) {
        estado.putString("valor1",textEmail.getText().toString());
        estado.putString("valor2",textPass.getText().toString());
        super.onSaveInstanceState(estado);
    }

    @Override
    protected void onRestoreInstanceState(Bundle estado) {
        super.onRestoreInstanceState(estado);
        textEmail.setText(estado.getString("valor1"));
        textPass.setText(estado.getString("valor2"));
    }
//////////////////////////////////Metodos para guardar y obtener datos cuando se voltea el cel_______FIN////////////////////
public void AceptarTerminos()
{
    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
    dialogo1.setTitle("Importante");
    dialogo1.setMessage("Esta aplicación  no es responsable de del mal y de los daños o derivados que puede suceder durante el viaje y uso de la aplicación ");
    dialogo1.setCancelable(false);
    dialogo1.setPositiveButton("Estoy de acuerdo", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialogo1, int id) {
            aceptar();
        }
    });
    dialogo1.setNegativeButton("No acepto", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialogo1, int id) {
            cancelar();
        }
    });
    dialogo1.show();
}
    public void aceptar()
    {
      doLogin();
    }

    public void cancelar() {
        finish();
        Toast t=Toast.makeText(this,"Lo sentimos pero no puedes usar la apliacion ", Toast.LENGTH_SHORT);

    }

    public void saveValuePreference4(Context context, Boolean mostrar) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();
        editor.putBoolean("terminos", mostrar);
        editor.commit();
    }



    public boolean getValuePreference5(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean("terminos", false);
    }
}
