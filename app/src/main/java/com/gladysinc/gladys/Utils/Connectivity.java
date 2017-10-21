package com.gladysinc.gladys.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import java.util.Objects;

public class Connectivity extends AppCompatActivity{

    public static String typeofconnection;

    public Connectivity() {}


    private static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }


    private static boolean isConnected(Context context){
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }


    private static boolean isConnectedWifi(Context context){
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }


      public static String typeconnection(Context context){

        if (isConnected(context)){

            if (isConnectedWifi(context)){

                chekerprefernceslocal(context);
            } else {chekerpreferncesnat(context); }
        } else{typeofconnection = "0";}

        return typeofconnection;
    }

    private static void chekerprefernceslocal(Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String ip = prefs.getString("ip", "");
        String port = prefs.getString("port", "");
        String token = prefs.getString("token", "");

        if(!Objects.equals(ip, "") & !Objects.equals(port, "") & !Objects.equals(token, "")){
            typeofconnection = "http://" + ip + ":" + port  ;
        } else {typeofconnection = "1";}
    }

    private static void chekerpreferncesnat(Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean nat = prefs.getBoolean("nat", false);
        boolean https = prefs.getBoolean("https", false);
        String dns = prefs.getString("dns", "");
        String portnat = prefs.getString("portnat", "");
        String token = prefs.getString("token", "");


        if (nat) {
            if (!Objects.equals(dns, "") & !Objects.equals(token, "")) {
                if (https){
                    typeofconnection = "https://" + dns;
                }else {
                    if (!Objects.equals(portnat, "")) {
                        typeofconnection = "http://" + dns + ":" + portnat;
                    }else {typeofconnection = "4";}
                }
            } else {typeofconnection = "3";}
        } else {typeofconnection = "2";}
    }

}