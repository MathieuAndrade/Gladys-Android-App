package com.gladysinc.gladys.Fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.gladysinc.gladys.Adapters.EventAdapter;
import com.gladysinc.gladys.Models.Event;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.Connectivity;
import com.gladysinc.gladys.Utils.RetrofitAPI;
import com.gladysinc.gladys.Utils.SelfSigningClientBuilder;
import com.gladysinc.gladys.Utils.SnackbarUtils;

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


public class TimelineFragment extends Fragment {

    String url, pref_token;
    String code, pref_user, pref_house;
    Boolean connection;
    RecyclerView recycler_view;
    TextView no_data_timeline;
    ImageView no_data_timeline_ic;
    EventAdapter adapter;
    MenuItem get_data_progress;
    SaveData save_data;

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
                if(connection){
                    getAllEvents();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_timeline, container, false);

        recycler_view = view.findViewById(R.id.rv_fragment_timeline);
        recycler_view.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler_view.setLayoutManager(layoutManager);

        no_data_timeline = view.findViewById(R.id.no_data_timeline);
        no_data_timeline_ic = view.findViewById(R.id.no_data_timeline_ic);

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

    public void getAllEvents() {

        get_data_progress.setVisible(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);

        Call<List<Event>> call = service.getEvents(pref_token);

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {

                List<Event> TimelineData = response.body();

                if(TimelineData != null){
                    save_data = new SaveData(TimelineFragment.this);
                    save_data.execute(TimelineData);
                } else {
                    onRefreshAdapterView();
                    get_data_progress.setVisible(false);
                    if(getActivity() != null){
                        SnackbarUtils.simpleSnackBar(getContext(), getView(), getContext().getString(R.string.error_code_4));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                if(getActivity() != null){
                    SnackbarUtils.simpleSnackBar(getContext(), getView(), getContext().getString(R.string.error_code_5));
                }
                get_data_progress.setVisible(false);
            }
        });
    }

    public void eventDialog(){
        new MaterialDialog.Builder(getActivity())
                .title(R.string.title_dialog_event)
                .items(R.array.events)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        String event = text.toString();
                        createEvent(event);

                        return true;
                    }
                })
                .positiveText(R.string.positve_button)
                .negativeText(R.string.negative_button)
                .show();
    }

    public void createEvent( final String name){

        SharedPreferences pref1 = PreferenceManager.getDefaultSharedPreferences(getContext());

        pref_house = pref1.getString("idhouse", "1");
        pref_user = pref1.getString("iduser", "1");

        getConnection();

        if(connection){

            getCode(name);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                    .build();

            RetrofitAPI service = retrofit.create(RetrofitAPI.class);

            Call<Event> call = service.createEvents(code, pref_house, pref_user, pref_token);

            call.enqueue(new Callback<Event>() {
                @Override
                public void onResponse(Call<Event> call, Response<Event> response) {
                    if(response.code() == 201){
                        getAllEvents();
                        if(getActivity() != null){
                            SnackbarUtils.simpleSnackBar(getContext(), getView(), getContext().getString(R.string.event_created));
                        }
                    }else{
                        if(getActivity() != null){
                            SnackbarUtils.simpleSnackBar(getContext(), getView(), getContext().getString(R.string.error_code_4));
                        }
                    }
                }

                @Override
                public void onFailure(Call<Event> call, Throwable t) {
                    if(getActivity() != null){
                        SnackbarUtils.simpleSnackBar(getContext(), getView(), getContext().getString(R.string.error_code_4));
                    }
                }
            });
        }
    }

    public void onCreateAdapterView(){

        long count = Event.count(Event.class);

        if (count > 0) {
            recycler_view.setVisibility(View.VISIBLE);
            no_data_timeline.setVisibility(View.INVISIBLE);
            no_data_timeline_ic.setVisibility(View.INVISIBLE);

            List<Event> data = Event.listAll(Event.class);
            adapter = new EventAdapter(data);
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
            recycler_view.setAdapter(new SlideInLeftAnimationAdapter(alphaAdapter));
            getAllEvents();
        } else {
            recycler_view.setVisibility(View.INVISIBLE);
            no_data_timeline.setVisibility(View.VISIBLE);
            no_data_timeline_ic.setVisibility(View.VISIBLE);
            getAllEvents();
        }
    }

    public void onRefreshAdapterView(){

        long count = Event.count(Event.class);

        if (count > 0) {
            recycler_view.setVisibility(View.VISIBLE);
            no_data_timeline.setVisibility(View.INVISIBLE);
            no_data_timeline_ic.setVisibility(View.INVISIBLE);

            List<Event> data = Event.listAll(Event.class);
            adapter = new EventAdapter(data);
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
            recycler_view.setAdapter(new SlideInLeftAnimationAdapter(alphaAdapter));
        } else {
            recycler_view.setVisibility(View.INVISIBLE);
            no_data_timeline.setVisibility(View.VISIBLE);
            no_data_timeline_ic.setVisibility(View.VISIBLE);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        MenuItem button = menu.findItem(R.id.add_button);
        button.setVisible(true);
        get_data_progress = menu.findItem(R.id.miActionProgress);

        getConnection();
        if(connection){
            onCreateAdapterView();
        }else {
            onRefreshAdapterView();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.add_button) {
            eventDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                SnackbarUtils.simpleSnackBar(getContext(), getView(), getContext().getString(R.string.error_code_7));
            }

        }else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            pref_token = prefs.getString("token", "");
            connection = true;
            url = type_of_connection;}

    }

    public void getCode(String text){

        switch (text) {
            case "Alarme":
                code = "alarm";
                break;
            case "Changement de mode":
                code = "house-mode-changed";
                break;
            case "Coucher de soleil":
                code = "sunset";
                break;
            case "Départ de la maison":
                code = "left-home";
                break;
            case "Départ d'une zone":
                code = "left-area";
                break;
            case "Entrée dans une zone":
                code = "enter-area";
                break;
            case "Gladys installée":
                code = "gladys-installed";
                break;
            case "Gladys mise à jour":
                code = "gladys-updated";
                break;
            case "Lever de soleil":
                code = "sunrise";
                break;
            case "Nouvelle valeur deviceType":
                code = "devicetype-new-value";
                break;
            case "Retour à la maison":
                code = "back-at-home";
                break;
            case "Réveil":
                code = "wake-up";
                break;
            case "Utilisateur vu à la maison":
                code = "user-seen-at-home";
                break;
            case "Va se coucher":
                code = "going-to-sleep";
                break;
        }

    }

    public void onStop(){
        super.onStop();
        if (save_data != null && save_data.getStatus() != AsyncTask.Status.FINISHED)
            save_data.cancel(true);
    }

    private static class SaveData extends AsyncTask<List<Event>, Void, Boolean>
    {
        private WeakReference<TimelineFragment> timeline_fragment_weak_reference;
        boolean result;

        SaveData(TimelineFragment context){
            timeline_fragment_weak_reference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SafeVarargs
        @Override
        protected final Boolean doInBackground(List<Event>... params) {

            long count = Event.count(Event.class);

            if (count > 0) {
                Event.deleteAll(Event.class);
            }

            List<Event> TimelineData = params[0];

            try {
                for (int i = 0; i < TimelineData.size(); i++) {
                    Event event = new Event(TimelineData.get(i).getName()
                            , TimelineData.get(i).getCode()
                            , TimelineData.get(i).getDatetime()
                            , TimelineData.get(i).getUser());
                    event.save();
                    if (isCancelled()) break;
                }

                result = true;

            } catch (Exception e){
                if(timeline_fragment_weak_reference.get().getActivity() != null){
                    SnackbarUtils.simpleSnackBar(timeline_fragment_weak_reference.get().getActivity(), timeline_fragment_weak_reference.get().getActivity().findViewById(R.id.layout), timeline_fragment_weak_reference.get().getContext().getString(R.string.error_code_4));
            }
                result = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (timeline_fragment_weak_reference.get() == null) return;

            if(result){
                timeline_fragment_weak_reference.get().onRefreshAdapterView();
            } else {
                if(timeline_fragment_weak_reference.get().getActivity() != null){
                    SnackbarUtils.simpleSnackBar(timeline_fragment_weak_reference.get().getActivity(), timeline_fragment_weak_reference.get().getActivity().findViewById(R.id.layout), timeline_fragment_weak_reference.get().getContext().getString(R.string.error_code_4));
                }
            }

            timeline_fragment_weak_reference.get().get_data_progress.setVisible(false);
        }
    }
}