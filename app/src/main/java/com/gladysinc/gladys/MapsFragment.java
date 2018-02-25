package com.gladysinc.gladys;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gladysinc.gladys.Utils.Connectivity;
import com.gladysinc.gladys.Utils.RetrofitAPI;
import com.gladysinc.gladys.Utils.SelfSigningClientBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.gladysinc.gladys.Utils.Connectivity.type_of_connection;


public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    String url, pref_token;
    Boolean connection;
    GoogleMap map;
    LatLng loc;
    ConstraintLayout mapContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapContainer = view.findViewById(R.id.map_container);
        mapContainer.setVisibility(View.INVISIBLE);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.map_container, mapFragment).commit();
        mapFragment.getMapAsync(this);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        MenuItem location_button = menu.findItem(R.id.location_button);
        MenuItem report_button = menu.findItem(R.id.report_button);
        location_button.setVisible(true);
        report_button.setVisible(true);

        /*getConnection();
        if (connection) {
            onCreateAdapterView();
        }else {
            onRefreshAdapterView();
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.report_button) {
            createLocation();
        } else if (id == R.id.location_button){
            if(map != null){
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setOnMyLocationChangeListener(myLocationChangeListener);
        map.setBuildingsEnabled(false);
        enableLocation();
    }

    public void enableLocation(){

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mapContainer.setVisibility(View.INVISIBLE);
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 111);
        }else if (map != null){
            mapContainer.setVisibility(View.VISIBLE);
            map.setMyLocationEnabled(true);
            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    if (loc != null){
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
                    }
                }
            });
        }
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            loc = new LatLng(location.getLatitude(), location.getLongitude());
        }
    };

    public void createLocation(){

        getConnection();
        if(connection){

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                    .build();

            RetrofitAPI service = retrofit.create(RetrofitAPI.class);

            Call<Void> call = service.createLocation((float) loc.latitude, (float) loc.longitude, pref_token);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call,Response<Void> response) {
                    if(response.code() == 201){
                        if(getActivity() != null){
                            Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.command_send), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }else{
                        if(getActivity() != null){
                            Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }
                }
                @Override
                public void onFailure(Call<Void> call,Throwable t) {
                    if(!Objects.equals(t.getMessage(), "java.net.SocketTimeoutException")){
                        if(getActivity() != null){
                            Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableLocation();
        } else {
            Snackbar.make(getActivity().findViewById(R.id.layout), "Permision refusée", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    public void getConnection(){
        Connectivity.typeconnection(getContext());

        if (Objects.equals(type_of_connection, "0")
                | Objects.equals(type_of_connection, "1")
                | Objects.equals(type_of_connection, "2")
                | Objects.equals(type_of_connection, "3")
                | Objects.equals(type_of_connection, "4") ){

            connection = false;
            if(getActivity() != null){
                Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + type_of_connection, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

        }else {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            pref_token = prefs.getString("token", "");
            connection = true;
            url = type_of_connection;}

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
