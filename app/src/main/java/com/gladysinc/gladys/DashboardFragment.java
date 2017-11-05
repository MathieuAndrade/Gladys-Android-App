package com.gladysinc.gladys;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.TextView;

import com.gladysinc.gladys.Adapters.DevicetypeByRoomAdapter;
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

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
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
    TextView no_data;
    List<Devicetype> data;
    DevicetypeByRoomAdapter adapter;
    MenuItem get_data_progress;
    SaveData save_data;
    Call<List<DevicetypeByRoom>> call;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        recycler_view = view.findViewById(R.id.rv_fragment_dashboard);
        recycler_view.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler_view.setLayoutManager(layoutManager);

        no_data = view.findViewById(R.id.no_data);

        return view;
    }

    public void onCreateAdapterView(){

        long count = SugarRecord.count(Devicetype.class);

        if(count>0) {
            recycler_view.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.INVISIBLE);

            data = SugarRecord.find(Devicetype.class, "display=?", "1");
            adapter = new DevicetypeByRoomAdapter(data, this);
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
            recycler_view.setAdapter(new SlideInLeftAnimationAdapter(alphaAdapter));
            getAllDevicetypeByRoom();
        } else {
            recycler_view.setVisibility(View.INVISIBLE);
            no_data.setVisibility(View.VISIBLE);
            no_data.setText(R.string.no_data);
            getAllDevicetypeByRoom();
        }
    }

    public void onRefreshAdapterView(){

        long count = SugarRecord.count(Devicetype.class);

        if(count>0) {
            recycler_view.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.INVISIBLE);

            data = SugarRecord.find(Devicetype.class, "display=?", "1");
            adapter = new DevicetypeByRoomAdapter(data, this);
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
            recycler_view.setAdapter(new SlideInLeftAnimationAdapter(alphaAdapter));
        } else {
            recycler_view.setVisibility(View.INVISIBLE);
            no_data.setVisibility(View.VISIBLE);
            no_data.setText(R.string.no_data);
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
            Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + type_of_connection, Snackbar.LENGTH_LONG).setAction("Action", null).show();

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
                    Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

            }

            @Override
            public void onFailure(Call<List<DevicetypeByRoom>> call, Throwable t) {
                if(!t.getMessage().equalsIgnoreCase("Socket closed")){
                    Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.command_send), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }else{
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call,Throwable t) {
                    Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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

            try {
                for (int i = 0; i < devicetypeByRomm_list.size(); i++) {
                    new DevicetypeByRoom(devicetypeByRomm_list.get(i).getRoomName()
                            , devicetypeByRomm_list.get(i).getHouse()
                            , devicetypeByRomm_list.get(i).getRommId()
                            , devicetypeByRomm_list.get(i).getDeviceTypes());

                    List<Devicetype> devicetype_list = devicetypeByRomm_list.get(i).getDeviceTypes();

                    for(int l = 0 ; l < devicetype_list.size(); l++) {
                        Devicetype deviceType2 = new Devicetype(devicetype_list.get(l).getDevicetypeName()
                                , devicetype_list.get(l).getDevicetypeId()
                                , devicetype_list.get(l).getType()
                                , devicetype_list.get(l).getCategory()
                                , devicetype_list.get(l).getTag()
                                , devicetype_list.get(l).getUnit()
                                , devicetype_list.get(l).getMin()
                                , devicetype_list.get(l).getMax()
                                , devicetype_list.get(l).getDisplay()
                                , devicetype_list.get(l).getSensor()
                                , devicetype_list.get(l).getLastChanged()
                                , devicetype_list.get(l).getLastValue()
                                , devicetypeByRomm_list.get(i).getRommId()
                                , devicetypeByRomm_list.get(i).getRoomName());

                        Long number = (SugarRecord.count(Devicetype.class, "devicetype_id=?", new String[]{deviceType2.getDevicetypeId().toString()}));

                        if (number == 1){
                            Devicetype devicetype = (SugarRecord.find(Devicetype.class, "devicetype_id=?", deviceType2.getDevicetypeId().toString())).get(0);
                            devicetype.setDevicetypeName(deviceType2.getDevicetypeName());
                            devicetype.setCategory(deviceType2.getCategory());
                            devicetype.setTag(deviceType2.getTag());
                            devicetype.setLastChanged(deviceType2.getLastChanged());
                            devicetype.setLastValue(deviceType2.getLastValue());
                            devicetype.setRoomId(deviceType2.getRoomId());
                            devicetype.setRoomName(deviceType2.getRoomName());
                            SugarRecord.save(devicetype);
                        }else {
                            SugarRecord.save(deviceType2);
                        }

                    }

                    if (isCancelled()) break;
                }
                result = true;

            } catch (Exception e){
                Snackbar.make(dashboard_fragment_weak_reference.get().getActivity().findViewById(R.id.layout), R.string.error + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
                Snackbar.make(dashboardFragment.getActivity().findViewById(R.id.layout), dashboardFragment.getActivity().getString(R.string.error) + " " + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

            dashboardFragment.get_data_progress.setVisible(false);
        }
    }
}


