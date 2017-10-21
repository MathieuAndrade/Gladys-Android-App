package com.gladysinc.gladys;

import android.content.Intent;
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

import com.gladysinc.gladys.Adapters.RoomAdapter;
import com.gladysinc.gladys.Models.DevicetypeByRoom;
import com.gladysinc.gladys.Utils.AdapterCallback;
import com.gladysinc.gladys.Utils.Connectivity;
import com.gladysinc.gladys.Utils.RetrofitAPI;
import com.gladysinc.gladys.Utils.SelfSigningClientBuilder;
import com.orm.SugarRecord;

import java.util.List;
import java.util.Objects;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.gladysinc.gladys.Utils.Connectivity.typeofconnection;


public class RoomsFragment extends Fragment implements AdapterCallback.AdapterCallbackRoom {

    String url, preftoken;
    Boolean connection;
    RecyclerView recyclerView;
    TextView noData;
    RoomAdapter adapter;
    MenuItem getDataProgress;
    SaveData saveData;
    Call<List<DevicetypeByRoom>> call;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getConnection();
                if (connection) {
                    getRooms();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_rooms, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.rv_fragment_room);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        noData = (TextView) v.findViewById(R.id.no_data);

        return v;
    }

    public void getRooms(){

        getDataProgress.setVisible(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);

        call = service.getDevicetypeByRoom(preftoken);

        call.enqueue(new Callback<List<DevicetypeByRoom>>() {
            @Override
            public void onResponse(Response<List<DevicetypeByRoom>> response, Retrofit retrofit) {

                List<DevicetypeByRoom> devicetypeData = response.body();

                if(devicetypeData != null){
                    saveData = new SaveData();
                    saveData.execute(devicetypeData);
                } else {
                    refreshAdapterView();
                    getDataProgress.setVisible(false);
                    Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                if(!t.getMessage().equalsIgnoreCase("Socket closed")){
                    Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                getDataProgress.setVisible(false);
            }
        });
    }

    public void onCreateAdapterView(){

        long count = SugarRecord.count(DevicetypeByRoom.class);

        if(count>0) {
            recyclerView.setVisibility(View.VISIBLE);
            noData.setVisibility(View.INVISIBLE);

            List<DevicetypeByRoom> data = SugarRecord.listAll(DevicetypeByRoom.class);
            adapter = new RoomAdapter(data, this);
            recyclerView.setAdapter(adapter);
            getRooms();
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            noData.setVisibility(View.VISIBLE);
            noData.setText(R.string.nodata);
            getRooms();
        }
    }

    public void refreshAdapterView(){

        long count = SugarRecord.count(DevicetypeByRoom.class);

        if(count>0) {
            recyclerView.setVisibility(View.VISIBLE);
            noData.setVisibility(View.INVISIBLE);

            List<DevicetypeByRoom> data = SugarRecord.listAll(DevicetypeByRoom.class);
            adapter = new RoomAdapter(data, this);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            noData.setVisibility(View.VISIBLE);
            noData.setText(R.string.nodata);
        }
    }

    public void onClickCallbackRoom(Long room_id, String room_name) {

        if(call != null){
            call.cancel();
        }

        Intent intent = new Intent(getActivity(), DeviceActivity.class);
        intent.putExtra("room_id", room_id);
        intent.putExtra("room_name", room_name);
        startActivity(intent);

    }

    public boolean getConnection(){
        Connectivity.typeconnection(getContext());

        if (Objects.equals(typeofconnection, "0")
                | Objects.equals(typeofconnection, "1")
                | Objects.equals(typeofconnection, "2")
                | Objects.equals(typeofconnection, "3")
                | Objects.equals(typeofconnection, "4") ){

            connection = false;
            Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + typeofconnection, Snackbar.LENGTH_LONG).setAction("Action", null).show();

        }else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            preftoken = prefs.getString("token", "");
            connection = true;
            url = typeofconnection;
        }

        return (connection);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        getDataProgress = menu.findItem(R.id.miActionProgress);

        getConnection();
        if (connection) {
            onCreateAdapterView();
        }else {
            refreshAdapterView();
        }
    }

    public void onDestroyView(){
        super.onDestroyView();
        if (saveData != null && saveData.getStatus() != AsyncTask.Status.FINISHED)
            saveData.cancel(true);

        if(call != null){
            call.cancel();
        }

    }

    private class SaveData extends AsyncTask<List<DevicetypeByRoom>, Integer, Boolean>
    {
        boolean result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SafeVarargs
        @Override
        protected final Boolean doInBackground(List<DevicetypeByRoom>... params) {

            long count = SugarRecord.count(DevicetypeByRoom.class);

            if (count > 0) {
                SugarRecord.deleteAll(DevicetypeByRoom.class);
            }

            List<DevicetypeByRoom> DevicetypeData = params[0];

            try {

                for (int i = 0; i < DevicetypeData.size(); i++) {
                    DevicetypeByRoom devicetypeByRoom = new DevicetypeByRoom(DevicetypeData.get(i).getRoomName()
                            , DevicetypeData.get(i).getHouse()
                            , DevicetypeData.get(i).getRommId()
                            , DevicetypeData.get(i).getDeviceTypes());
                    SugarRecord.save(devicetypeByRoom);
                    if (isCancelled()) break;
                }
                result = true;

            } catch (Exception e){
                Snackbar.make(getActivity().findViewById(R.id.layout), R.string.error + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                result = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result){
                refreshAdapterView();
            } else {
                Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

            getDataProgress.setVisible(false);
        }
    }
}



