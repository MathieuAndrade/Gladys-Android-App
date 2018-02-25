package com.gladysinc.gladys;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.gladysinc.gladys.Adapters.DevicetypeByRoomAdapterSectioned;
import com.gladysinc.gladys.Models.Devicetype;
import com.gladysinc.gladys.Models.DevicetypeByRoom;
import com.gladysinc.gladys.Utils.AdapterCallback;
import com.gladysinc.gladys.Utils.Connectivity;
import com.gladysinc.gladys.Utils.RetrofitAPI;
import com.gladysinc.gladys.Utils.SelfSigningClientBuilder;
import com.orm.SugarContext;
import com.orm.SugarRecord;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.gladysinc.gladys.Utils.Connectivity.type_of_connection;


public class DashboardFragment extends Fragment implements AdapterCallback.AdapterCallbackDevicestate {

    String url, pref_token;
    Boolean connection;
    RecyclerView recycler_view;
    TextView no_data_dashboard;
    ImageView no_data_dashboard_ic;
    List<Devicetype> data;
    MenuItem get_data_progress;
    SaveData save_data;
    Call<List<DevicetypeByRoom>> call;
    SectionedRecyclerViewAdapter  adapterSectioned;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SugarContext.init(getContext());

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getConnection();
                if(connection){
                    getAllDevicetypeByRoom();
                }
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        recycler_view = view.findViewById(R.id.rv_fragment_dashboard);
        recycler_view.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler_view.setLayoutManager(layoutManager);

        adapterSectioned = new SectionedRecyclerViewAdapter();

        no_data_dashboard = view.findViewById(R.id.no_data_dashboard);
        no_data_dashboard_ic = view.findViewById(R.id.no_data_dashboard_ic);

        final FloatingActionButton fab_scroll_up = getActivity().findViewById(R.id.fab_scroll_up);
        fab_scroll_up.setVisibility(View.VISIBLE);
        fab_scroll_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recycler_view.smoothScrollToPosition(0);
                fab_scroll_up.animate().translationY(fab_scroll_up.getHeight() + 400).setInterpolator(new LinearInterpolator()).start();
            }
        });

        return view;
    }

    public void onCreateAdapterView(){

        long count = SugarRecord.count(Devicetype.class);

        if(count>0) {
            recycler_view.setVisibility(View.VISIBLE);
            no_data_dashboard.setVisibility(View.INVISIBLE);
            no_data_dashboard_ic.setVisibility(View.INVISIBLE);

            for (int i = 0; i < 50; i++){
                data = null;
                data = SugarRecord.findWithQuery(Devicetype.class, "select * from devicetype where display = ? and room_id = ?","1", String.valueOf(i));
                if(data.size() > 0){
                    adapterSectioned.addSection(new DevicetypeByRoomAdapterSectioned(data.get(0).getRoomName(), data, this, getContext()));
                }
            }

            recycler_view.setAdapter(adapterSectioned);
            getAllDevicetypeByRoom();
        } else {
            recycler_view.setVisibility(View.INVISIBLE);
            no_data_dashboard.setVisibility(View.VISIBLE);
            no_data_dashboard_ic.setVisibility(View.VISIBLE);
            getAllDevicetypeByRoom();
        }
    }

    public void onRefreshAdapterView(){

        long count = SugarRecord.count(Devicetype.class);
        adapterSectioned.removeAllSections();

        if(count>0) {
            recycler_view.setVisibility(View.VISIBLE);
            no_data_dashboard.setVisibility(View.INVISIBLE);
            no_data_dashboard_ic.setVisibility(View.INVISIBLE);

            for (int i = 0; i < 50; i++){
                data = null;
                data = SugarRecord.findWithQuery(Devicetype.class, "select * from devicetype where display = ? and room_id = ?","1", String.valueOf(i));
                if(data.size() > 0){
                    adapterSectioned.addSection(new DevicetypeByRoomAdapterSectioned(data.get(0).getRoomName(), data, this, getContext()));
                }
            }

            recycler_view.setAdapter(adapterSectioned);
        } else {
            recycler_view.setVisibility(View.INVISIBLE);
            no_data_dashboard.setVisibility(View.VISIBLE);
            no_data_dashboard_ic.setVisibility(View.VISIBLE);
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

    public void getAllDevicetypeByRoom(){

        get_data_progress.setVisible(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);

        call = service.getDevicetypeByRoom(pref_token);

        call.enqueue(new Callback<List<DevicetypeByRoom>>() {
            @Override
            public void onResponse(Call<List<DevicetypeByRoom>> call, Response<List<DevicetypeByRoom>> response) {

                List<DevicetypeByRoom> devicetypeData = response.body();

                if(devicetypeData != null){
                    save_data = new SaveData(DashboardFragment.this);
                    save_data.execute(devicetypeData);
                } else {
                    onRefreshAdapterView();
                    get_data_progress.setVisible(false);
                    if(getActivity() != null){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<List<DevicetypeByRoom>> call, Throwable t) {

                if(!Objects.equals(t.getMessage(), "java.net.SocketTimeoutException")){
                    if(getActivity() != null){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
                get_data_progress.setVisible(false);

            }
        });
    }

    @Override
    public void onClickCallbackDevicestate(Long id, Float value) {

        if(call != null){
            call.cancel();
        }
        changeDevicestate(id, value);
    }

    public void changeDevicestate(Long id, Float value){

        getConnection();
        if(connection){

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                    .build();

            RetrofitAPI service = retrofit.create(RetrofitAPI.class);

            Call<Void> call = service.changeDevicestate(id, value, pref_token);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call,Response<Void> response) {
                    if(response.code() == 200){
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

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        get_data_progress = menu.findItem(R.id.miActionProgress);

        getConnection();
        if (connection) {
            onCreateAdapterView();
        }else {
            onRefreshAdapterView();
        }
    }

    public void onStop(){
        super.onStop();
        if (save_data != null && save_data.getStatus() != AsyncTask.Status.FINISHED){
            save_data.cancel(true);
        }
        if(call != null){
            call.cancel();
        }
    }

    private static class SaveData extends AsyncTask<List<DevicetypeByRoom>, Void, Boolean>
    {
        private WeakReference<DashboardFragment> dashboard_fragment_weak_reference;
        boolean result;

        SaveData(DashboardFragment context){
            dashboard_fragment_weak_reference = new WeakReference<>(context);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SafeVarargs
        @Override
        protected final Boolean doInBackground(List<DevicetypeByRoom>... params) {

            List<DevicetypeByRoom> devicetypeByRomm_list = params[0];
            List<Devicetype> devicetypesDisplayed = SugarRecord.find(Devicetype.class, "display=?", "1");
            SugarRecord.deleteAll(Devicetype.class);

            try {
                for (int i = 0; i < devicetypeByRomm_list.size(); i++) {

                    for(int l = 0 ; l < devicetypeByRomm_list.get(i).getDeviceTypes().size(); l++) {
                        Devicetype deviceType2 = new Devicetype(devicetypeByRomm_list.get(i).getDeviceTypes().get(l).getDevicetypeName()
                                , devicetypeByRomm_list.get(i).getDeviceTypes().get(l).getDevicetypeId()
                                , devicetypeByRomm_list.get(i).getDeviceTypes().get(l).getType()
                                , devicetypeByRomm_list.get(i).getDeviceTypes().get(l).getCategory()
                                , devicetypeByRomm_list.get(i).getDeviceTypes().get(l).getTag()
                                , devicetypeByRomm_list.get(i).getDeviceTypes().get(l).getUnit()
                                , devicetypeByRomm_list.get(i).getDeviceTypes().get(l).getMin()
                                , devicetypeByRomm_list.get(i).getDeviceTypes().get(l).getMax()
                                , devicetypeByRomm_list.get(i).getDeviceTypes().get(l).getDisplay()
                                , devicetypeByRomm_list.get(i).getDeviceTypes().get(l).getSensor()
                                , devicetypeByRomm_list.get(i).getDeviceTypes().get(l).getLastChanged()
                                , devicetypeByRomm_list.get(i).getDeviceTypes().get(l).getLastValue()
                                , devicetypeByRomm_list.get(i).getRommId()
                                , devicetypeByRomm_list.get(i).getRoomName());

                        SugarRecord.save(deviceType2);

                        if (isCancelled()) break;
                    }

                    if (isCancelled()) break;
                }

                for(int j = 0; j < devicetypesDisplayed.size(); j++){

                    Devicetype devicetype = (SugarRecord.find(Devicetype.class, "devicetype_id=?", devicetypesDisplayed.get(j).getDevicetypeId().toString())).get(0);
                    if(devicetype != null){
                        devicetype.setDisplay(devicetypesDisplayed.get(j).getDisplay());
                        SugarRecord.save(devicetype);
                    }

                    if (isCancelled()) break;
                }

                result = true;

            } catch (Exception e){
                if(dashboard_fragment_weak_reference.get().getActivity() != null){
                    Snackbar.make(dashboard_fragment_weak_reference.get().getActivity().findViewById(R.id.layout), R.string.error + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                result = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            DashboardFragment dashboardFragment = dashboard_fragment_weak_reference.get();
            if (dashboardFragment == null) return;

            if(result){
                dashboardFragment.onRefreshAdapterView();
            } else {
                if(dashboardFragment.getActivity() != null){
                    Snackbar.make(dashboardFragment.getActivity().findViewById(R.id.layout), dashboardFragment.getActivity().getString(R.string.error) + " " + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }

            dashboardFragment.get_data_progress.setVisible(false);
        }
    }
}


