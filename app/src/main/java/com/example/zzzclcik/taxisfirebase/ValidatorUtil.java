package com.example.zzzclcik.taxisfirebase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {

    private CountDownTimer timer = null;
    private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private DatabaseReference mDatabase,mDatabase2;
    private DatabaseReference currentUserBD,currentUserBD2;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private Context context;

    ValidatorUtil(Context context)
    {
        this.context = context;
        connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        networkInfo = connectivityManager.getActiveNetworkInfo();
    }

    public static boolean validateEmail(String email) {

        Pattern pattern = Pattern.compile(PATTERN_EMAIL);

        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    public void Esperar(final String idUsuario, final String numPeticion, final String numIdU)
    {
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                mDatabase = FirebaseDatabase.getInstance().getReference().child("taxis");
                currentUserBD = mDatabase.child(idUsuario);
                currentUserBD.child(numPeticion).setValue("123");
                DatabaseReference mDatabase2= FirebaseDatabase.getInstance().getReference().child("users");
                DatabaseReference currentUserBD2=mDatabase2.child(numIdU);
                currentUserBD2.child("estado").setValue("0");
            }
        };
        timer.start();
    }

    public void CancelTimer()
    {
        if(timer != null)
        {
            timer.cancel();
        }
    }

    public boolean isOnline() {

        RunnableFuture<Boolean> futureRun = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if(networkInfo.isConnectedOrConnecting()) {
                    if ((networkInfo.isAvailable()) && (networkInfo.isConnected())) {
                        try {
                            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                            urlc.setRequestProperty("User-Agent", "Test");
                            urlc.setRequestProperty("Connection", "close");
                            urlc.setConnectTimeout(700);
                            urlc.connect();
                            return (urlc.getResponseCode() == 200);
                        } catch (IOException  |NullPointerException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error al comprobar la conexión a Internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(context,"¡No hay red disponible!",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        new Thread(futureRun).start();

        try {
            return futureRun.get();
        } catch (InterruptedException | NullPointerException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }
}
