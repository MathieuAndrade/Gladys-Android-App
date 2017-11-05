package com.gladysinc.gladys;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.gladysinc.gladys.Adapters.ViewPagerAdapter;
import com.gladysinc.gladys.Models.BrainSentences;
import com.gladysinc.gladys.Utils.Connectivity;
import com.gladysinc.gladys.Utils.RetrofitAPI;
import com.gladysinc.gladys.Utils.SelfSigningClientBuilder;
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

public class BrainFragment extends Fragment {

    String url, pref_token;
    Boolean connection;
    MenuItem get_data_progress;
    SaveData save_data;
    Call<List<BrainSentences>> call;
    String take = "300";
    ViewPager view_pager;

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
                if (connection) {
                    getAllBrainSentences();
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_brain, container, false);

        view_pager = view.findViewById(R.id.pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        view_pager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab);
        tabLayout.setupWithViewPager(view_pager);

        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        get_data_progress = menu.findItem(R.id.miActionProgress);

        getConnection();
        if (connection) {
            getAllBrainSentences();
        }
    }

    public void getAllBrainSentences() {

        get_data_progress.setVisible(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);

        call = service.getBrainSentences(take, pref_token);

        call.enqueue(new Callback<List<BrainSentences>>() {
            @Override
            public void onResponse(Call<List<BrainSentences>> call, Response<List<BrainSentences>> response) {

                List<BrainSentences> brainSentencesList = response.body();

                if (brainSentencesList != null) {
                    save_data = new SaveData(BrainFragment.this);
                    save_data.execute(brainSentencesList);
                } else {
                    get_data_progress.setVisible(false);
                    if(getActivity() != null){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<BrainSentences>> call, Throwable t) {
                if(!Objects.equals(t.getMessage(), "java.net.SocketTimeoutException")){
                    if(getActivity() != null){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
                get_data_progress.setVisible(false);
            }
        });
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

    private static class SaveData extends AsyncTask<List<BrainSentences>, Void, Boolean>
    {
        private WeakReference<BrainFragment> brain_fragment_weak_reference;
        boolean result;

        SaveData(BrainFragment context){
            brain_fragment_weak_reference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SafeVarargs
        @Override
        protected final Boolean doInBackground(List<BrainSentences>... params) {

            long count = SugarRecord.count(BrainSentences.class);

            if(count>0) {
                SugarRecord.deleteAll(BrainSentences.class);
            }

            List<BrainSentences> brainSentencesList = params[0];

            try {
                for (int i = 0; i < brainSentencesList.size(); i++) {
                    BrainSentences brainSentences = new BrainSentences(brainSentencesList.get(i).getSentences_id()
                            , brainSentencesList.get(i).getText()
                            , brainSentencesList.get(i).getLabel()
                            , brainSentencesList.get(i).getStatue());
                    SugarRecord.save(brainSentences);
                    if (isCancelled()) break;
                    }

                result = true;

            } catch (Exception e){
                if(brain_fragment_weak_reference.get().getActivity() != null){
                    Snackbar.make(brain_fragment_weak_reference.get().getActivity().findViewById(R.id.layout), R.string.error + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                result = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            BrainFragment brainFragment = brain_fragment_weak_reference.get();
            if (brainFragment == null) return;

            if(!result){
                if(brainFragment.getActivity() != null){
                    Snackbar.make(brainFragment.getActivity().findViewById(R.id.layout), brainFragment.getActivity().getString(R.string.error) + " " + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
            brainFragment.get_data_progress.setVisible(false);
            brainFragment.view_pager.getAdapter().notifyDataSetChanged();
        }
    }
}




