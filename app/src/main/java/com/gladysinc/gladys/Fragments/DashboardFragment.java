package com.gladysinc.gladys.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

import com.gladysinc.gladys.Adapters.DashboardParentAdapter;
import com.gladysinc.gladys.Models.Devicetype;
import com.gladysinc.gladys.Models.DevicetypeByRoom;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.AdapterCallback;
import com.gladysinc.gladys.Utils.Connectivity;
import com.gladysinc.gladys.Utils.RetrofitAPI;
import com.gladysinc.gladys.Utils.SelfSigningClientBuilder;
import com.gladysinc.gladys.Utils.SnackbarUtils;
import com.orm.SugarContext;
import com.orm.SugarRecord;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.gladysinc.gladys.Utils.Connectivity.type_of_connection;


public class DashboardFragment extends Fragment implements AdapterCallback.AdapterCallbackDevicestate {

    String url, pref_token;
    boolean connection;
    RecyclerView recycler_view;
    TextView no_data_dashboard;
    ImageView no_data_dashboard_ic;
    MenuItem get_data_progress;
    SaveData save_data;
    Call<List<DevicetypeByRoom>> call;
    List<DevicetypeByRoom> data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SugarContext.init(Objects.requireNonNull(getContext()));

        FloatingActionButton fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext());
        recycler_view.setLayoutManager(layoutManager);

        no_data_dashboard = view.findViewById(R.id.no_data_dashboard);
        no_data_dashboard_ic = view.findViewById(R.id.no_data_dashboard_ic);

        return view;
    }

    public void onCreateAdapterView(){

        if(SugarRecord.count(Devicetype.class) > 0) {
            recycler_view.setVisibility(View.VISIBLE);
            no_data_dashboard.setVisibility(View.INVISIBLE);
            no_data_dashboard_ic.setVisibility(View.INVISIBLE);

            data = null;
            data = SugarRecord.listAll(DevicetypeByRoom.class);
            for (int i = 0; i < data.size(); i++){
                data.get(i).setDeviceTypes(SugarRecord.findWithQuery(Devicetype.class, "select * from devicetype where display = ? and room_id = ?","1", String.valueOf(data.get(i).getRommId())));
            }

            recycler_view.setAdapter(new DashboardParentAdapter(data, this));
            data = null;

        } else {
            recycler_view.setVisibility(View.INVISIBLE);
            no_data_dashboard.setVisibility(View.VISIBLE);
            no_data_dashboard_ic.setVisibility(View.VISIBLE);
            getAllDevicetypeByRoom();
        }
    }

    public void onRefreshAdapterView(){

        if(SugarRecord.count(Devicetype.class) > 0) {
            recycler_view.setVisibility(View.VISIBLE);
            no_data_dashboard.setVisibility(View.INVISIBLE);
            no_data_dashboard_ic.setVisibility(View.INVISIBLE);

            data = null;
            data = SugarRecord.listAll(DevicetypeByRoom.class);
            for (int i = 0; i < data.size(); i++){
                data.get(i).setDeviceTypes(SugarRecord.findWithQuery(Devicetype.class, "select * from devicetype where display = ? and room_id = ?","1", String.valueOf(data.get(i).getRommId())));
            }

            recycler_view.setAdapter(new DashboardParentAdapter(data, this));
            data = null;

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
                SnackbarUtils.simpleSnackBar(getContext(), getView(), Objects.requireNonNull(getContext()).getString(R.string.error_code_7));
            }

        }else {

            pref_token = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("token", "");
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

        call = retrofit.create(RetrofitAPI.class).getDevicetypeByRoom(pref_token);

        call.enqueue(new Callback<List<DevicetypeByRoom>>() {
            @Override
            public void onResponse(Call<List<DevicetypeByRoom>> call, Response<List<DevicetypeByRoom>> response) {

                if(response.body() != null){
                    save_data = new SaveData(DashboardFragment.this);
                    save_data.execute(response.body());
                } else {
                    onRefreshAdapterView();
                    get_data_progress.setVisible(false);
                    if(getActivity() != null){
                        SnackbarUtils.simpleSnackBar(getContext(), getView(), Objects.requireNonNull(getContext()).getString(R.string.error_code_4));
                    }
                }

            }

            @Override
            public void onFailure(Call<List<DevicetypeByRoom>> call, Throwable t) {

                if(!Objects.equals(t.getMessage(), "java.net.SocketTimeoutException")){
                    if(getActivity() != null){
                        SnackbarUtils.simpleSnackBar(getContext(), getView(), Objects.requireNonNull(getContext()).getString(R.string.error_code_5));
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
                            SnackbarUtils.simpleSnackBar(getContext(), getView(), Objects.requireNonNull(getContext()).getString(R.string.command_send));
                    }
                    }else{
                        if(getActivity() != null){
                            SnackbarUtils.simpleSnackBar(getContext(), getView(), Objects.requireNonNull(getContext()).getString(R.string.error_code_5));
                        }
                    }
                }
                @Override
                public void onFailure(Call<Void> call,Throwable t) {
                    if(!Objects.equals(t.getMessage(), "java.net.SocketTimeoutException")){
                        if(getActivity() != null){
                            SnackbarUtils.simpleSnackBar(getContext(), getView(), Objects.requireNonNull(getContext()).getString(R.string.error_code_5));
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

            List<Devicetype> devicetypesDisplayed = SugarRecord.listAll(Devicetype.class);
            Devicetype devicetype;

            try {

                SugarRecord.deleteAll(Devicetype.class);
                SugarRecord.deleteAll(DevicetypeByRoom.class);

                for (int i = 0; i < params[0].size(); i++) {

                    SugarRecord.save(new DevicetypeByRoom(params[0].get(i).getRoomName()
                            , params[0].get(i).getHouse()
                            , params[0].get(i).getRommId()
                            , params[0].get(i).getDeviceTypes()));

                    for(int l = 0 ; l < params[0].get(i).getDeviceTypes().size(); l++) {
                        SugarRecord.save(new Devicetype(params[0].get(i).getDeviceTypes().get(l).getDevicetypeName()
                                , params[0].get(i).getDeviceTypes().get(l).getDevicetypeId()
                                , params[0].get(i).getDeviceTypes().get(l).getType()
                                , params[0].get(i).getDeviceTypes().get(l).getCategory()
                                , params[0].get(i).getDeviceTypes().get(l).getTag()
                                , params[0].get(i).getDeviceTypes().get(l).getUnit()
                                , params[0].get(i).getDeviceTypes().get(l).getMin()
                                , params[0].get(i).getDeviceTypes().get(l).getMax()
                                , params[0].get(i).getDeviceTypes().get(l).getDisplay()
                                , params[0].get(i).getDeviceTypes().get(l).getSensor()
                                , params[0].get(i).getDeviceTypes().get(l).getLastChanged()
                                , params[0].get(i).getDeviceTypes().get(l).getLastValue()
                                , params[0].get(i).getRommId()
                                , params[0].get(i).getRoomName()));

                        if (isCancelled()) break;
                    }

                    if (isCancelled()) break;
                }

                for(int j = 0; j < devicetypesDisplayed.size(); j++){

                    devicetype = (SugarRecord.find(Devicetype.class, "devicetype_id=?", devicetypesDisplayed.get(j).getDevicetypeId().toString())).get(0);
                    if(devicetype != null){
                        devicetype.setDisplay(devicetypesDisplayed.get(j).getDisplay());
                        SugarRecord.save(devicetype);
                    }

                    if (isCancelled()) break;
                }

                result = true;

            } catch (Exception e){
                if(dashboard_fragment_weak_reference.get().getActivity() != null){
                    SnackbarUtils.simpleSnackBar(dashboard_fragment_weak_reference.get().getActivity(), Objects.requireNonNull(dashboard_fragment_weak_reference.get().getActivity()).findViewById(R.id.layout), Objects.requireNonNull(dashboard_fragment_weak_reference.get().getContext()).getString(R.string.error_code_4));
                }
                result = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (dashboard_fragment_weak_reference.get() == null) return;

            if(result){
                dashboard_fragment_weak_reference.get().onRefreshAdapterView();
            } else {
                if(dashboard_fragment_weak_reference.get().getActivity() != null){
                    SnackbarUtils.simpleSnackBar(dashboard_fragment_weak_reference.get().getActivity(), Objects.requireNonNull(dashboard_fragment_weak_reference.get().getActivity()).findViewById(R.id.layout), Objects.requireNonNull(dashboard_fragment_weak_reference.get().getContext()).getString(R.string.error_code_4));
                }
            }

            dashboard_fragment_weak_reference.get().get_data_progress.setVisible(false);
        }
    }
}


