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
import android.widget.ImageView;
import android.widget.TextView;

import com.gladysinc.gladys.Adapters.RoomAdapter;
import com.gladysinc.gladys.Models.DevicetypeByRoom;
import com.gladysinc.gladys.Utils.AdapterCallback;
import com.gladysinc.gladys.Utils.Connectivity;
import com.gladysinc.gladys.Utils.RetrofitAPI;
import com.gladysinc.gladys.Utils.SelfSigningClientBuilder;
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


public class RoomsFragment extends Fragment implements AdapterCallback.AdapterCallbackRoom {

    String url, pref_token;
    Boolean connection;
    RecyclerView recycler_view;
    TextView no_data_room;
    ImageView no_data_room_ic;
    RoomAdapter adapter;
    MenuItem get_data_progress;
    SaveData save_data;
    Call<List<DevicetypeByRoom>> call;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getConnection();
                if (connection) {
                    getAllRooms();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rooms, container, false);

        recycler_view = view.findViewById(R.id.rv_fragment_room);
        recycler_view.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler_view.setLayoutManager(layoutManager);

        no_data_room = view.findViewById(R.id.no_data_room);
        no_data_room_ic = view.findViewById(R.id.no_data_room_ic);

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

    public void getAllRooms(){

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
                    save_data = new SaveData(RoomsFragment.this);
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

    public void onCreateAdapterView(){

        long count = SugarRecord.count(DevicetypeByRoom.class);

        if(count>0) {
            recycler_view.setVisibility(View.VISIBLE);
            no_data_room.setVisibility(View.INVISIBLE);
            no_data_room_ic.setVisibility(View.INVISIBLE);

            List<DevicetypeByRoom> data = SugarRecord.listAll(DevicetypeByRoom.class);
            adapter = new RoomAdapter(data, this);
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
            recycler_view.setAdapter(new SlideInLeftAnimationAdapter(alphaAdapter));
            getAllRooms();
        } else {
            recycler_view.setVisibility(View.INVISIBLE);
            no_data_room.setVisibility(View.VISIBLE);
            no_data_room_ic.setVisibility(View.VISIBLE);
            getAllRooms();
        }
    }

    public void onRefreshAdapterView(){

        long count = SugarRecord.count(DevicetypeByRoom.class);

        if(count>0) {
            recycler_view.setVisibility(View.VISIBLE);
            no_data_room.setVisibility(View.INVISIBLE);
            no_data_room_ic.setVisibility(View.INVISIBLE);

            List<DevicetypeByRoom> data = SugarRecord.listAll(DevicetypeByRoom.class);
            adapter = new RoomAdapter(data, this);
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
            recycler_view.setAdapter(new SlideInLeftAnimationAdapter(alphaAdapter));
        } else {
            recycler_view.setVisibility(View.INVISIBLE);
            no_data_room.setVisibility(View.VISIBLE);
            no_data_room_ic.setVisibility(View.VISIBLE);
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
            url = type_of_connection;
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
        if (save_data != null && save_data.getStatus() != AsyncTask.Status.FINISHED)
            save_data.cancel(true);

        if(call != null){
            call.cancel();
        }

    }

    private static class SaveData extends AsyncTask<List<DevicetypeByRoom>, Void, Boolean>
    {
        private WeakReference<RoomsFragment> rooms_fragment_weak_reference;
        boolean result;

        SaveData(RoomsFragment context){
            rooms_fragment_weak_reference = new WeakReference<>(context);
        }
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
                if(rooms_fragment_weak_reference.get().getActivity() != null){
                    Snackbar.make(rooms_fragment_weak_reference.get().getActivity().findViewById(R.id.layout), R.string.error + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

                result = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            RoomsFragment roomsFragment = rooms_fragment_weak_reference.get();
            if (roomsFragment == null) return;

            if(result){
                roomsFragment.onRefreshAdapterView();
            } else {
                if(roomsFragment.getActivity() != null){
                    Snackbar.make(roomsFragment.getActivity().findViewById(R.id.layout), roomsFragment.getActivity().getString(R.string.error) + " " + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }

            roomsFragment.get_data_progress.setVisible(false);
        }
    }
}



