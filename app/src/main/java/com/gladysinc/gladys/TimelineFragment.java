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

import com.afollestad.materialdialogs.MaterialDialog;
import com.gladysinc.gladys.Adapters.EventAdapter;
import com.gladysinc.gladys.Models.Event;
import com.gladysinc.gladys.Utils.Connectivity;
import com.gladysinc.gladys.Utils.RetrofitAPI;
import com.gladysinc.gladys.Utils.SelfSigningClientBuilder;

import java.util.List;
import java.util.Objects;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.gladysinc.gladys.Utils.Connectivity.typeofconnection;


public class TimelineFragment extends Fragment {

    String url, preftoken;
    String code, prefuser, prefhouse;
    Boolean connection;
    RecyclerView recyclerView;
    TextView noData;
    EventAdapter adapter;
    MenuItem getDataProgress;
    SaveData saveData;

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
                if(connection){
                    getEvents();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_timeline, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.rv_fragment_timeline);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        noData = (TextView) v.findViewById(R.id.no_data);

        return v;
    }

    public void getEvents() {

        getDataProgress.setVisible(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);

        Call<List<Event>> call = service.getEvents(preftoken);

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Response<List<Event>> response, Retrofit retrofit) {

                List<Event> TimelineData = response.body();

                if(TimelineData != null){
                    saveData = new SaveData();
                    saveData.execute(TimelineData);
                } else {
                    refreshAdapterView();
                    getDataProgress.setVisible(false);
                    Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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

        prefhouse = pref1.getString("idhouse", "1");
        prefuser = pref1.getString("iduser", "1");

        getConnection();

        if(connection){

            getCode(name);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                    .build();

            RetrofitAPI service = retrofit.create(RetrofitAPI.class);

            Call<Event> call = service.createEvents(code, prefhouse, prefuser, preftoken);

            call.enqueue(new Callback<Event>() {
                @Override
                public void onResponse(Response<Event> response, Retrofit retrofit) {

                    getEvents();
                    Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.event_created), Snackbar.LENGTH_LONG).setAction("Action", null).show();

                }

                @Override
                public void onFailure(Throwable t) {
                    Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });
        }
    }

    public void onCreateAdapterView(){

        long count = Event.count(Event.class);

        if (count > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noData.setVisibility(View.INVISIBLE);

            List<Event> data = Event.listAll(Event.class);
            adapter = new EventAdapter(data);
            recyclerView.setAdapter(adapter);
            getEvents();
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            noData.setVisibility(View.VISIBLE);
            noData.setText(R.string.nodata);
            getEvents();
        }
    }

    public void refreshAdapterView(){

        long count = Event.count(Event.class);

        if (count > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noData.setVisibility(View.INVISIBLE);

            List<Event> data = Event.listAll(Event.class);
            adapter = new EventAdapter(data);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            noData.setVisibility(View.VISIBLE);
            noData.setText(R.string.nodata);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        MenuItem button = menu.findItem(R.id.add_button);
        button.setVisible(true);
        getDataProgress = menu.findItem(R.id.miActionProgress);

        getConnection();
        if(connection){
            onCreateAdapterView();
        }else {
            refreshAdapterView();
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
            url = typeofconnection;}

        return (connection);
    }

    public String getCode(String text){

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

        return(code);
    }

    public void onDestroyView(){
        super.onDestroyView();
        if (saveData != null && saveData.getStatus() != AsyncTask.Status.FINISHED)
            saveData.cancel(true);
    }

    private class SaveData extends AsyncTask<List<Event>, Integer, Boolean>
    {
        boolean result;

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