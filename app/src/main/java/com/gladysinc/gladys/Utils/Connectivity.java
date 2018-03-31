package com.gladysinc.gladys.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.util.Objects;

public class Connectivity{

    public static String type_of_connection;

    public Connectivity() {}

    private static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }


    public static boolean isConnected(Context context){
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }


    public static boolean isConnectedWifi(Context context){
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }


      public static String typeconnection(Context context){

        if (isConnected(context)){

            if (isConnectedWifi(context)){

                chekerprefernceslocal(context);
            } else {chekerpreferncesnat(context); }
        } else{
            type_of_connection = "0";}

        return type_of_connection;
    }

    private static void chekerprefernceslocal(Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String ip = prefs.getString("local_ip", "");
        String port = prefs.getString("local_port", "");
        String token = prefs.getString("token", "");

        if(!Objects.equals(ip, "") & !Objects.equals(port, "") & !Objects.equals(token, "")){
            type_of_connection = "http://" + ip + ":" + port;
        } else {
            type_of_connection = "1";}
    }

    private static void chekerpreferncesnat(Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean nat = prefs.getBoolean("nat", false);
        boolean https = prefs.getBoolean("https", false);
        String dns = prefs.getString("dns", "");
        String portnat = prefs.getString("nat_port", "");
        String token = prefs.getString("token", "");


        if (nat) {
            if (!Objects.equals(dns, "") & !Objects.equals(token, "")) {
                if (https){
                    type_of_connection = "https://" + dns;
                }else {
                    if (!Objects.equals(portnat, "")) {
                        type_of_connection = "http://" + dns + ":" + portnat;
                    }else {
                        type_of_connection = "4";}
                }
            } else {
                type_of_connection = "3";}
        } else {
            type_of_connection = "2";}
    }

}