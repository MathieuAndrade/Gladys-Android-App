package com.gladysinc.gladys.BrainTabFragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gladysinc.gladys.Adapters.BrainSentencesAdapter;
import com.gladysinc.gladys.Models.BrainSentences;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.AdapterCallback;
import com.gladysinc.gladys.Utils.Connectivity;
import com.gladysinc.gladys.Utils.RetrofitAPI;
import com.gladysinc.gladys.Utils.SelfSigningClientBuilder;
import com.orm.SugarRecord;

import java.util.Arrays;
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


public class PendingSentencesFragment extends Fragment implements AdapterCallback.AdapterCallbackBrainSentences {

    String url, pref_token;
    Boolean connection;
    RecyclerView recycler_view;
    TextView no_data;
    BrainSentencesAdapter adapter;
    Integer index_label;

    public static final String Title = "Phrases en attentes";

    public static PendingSentencesFragment newInstance() {
        return new PendingSentencesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pending_sentences, container, false);

        recycler_view = view.findViewById(R.id.rv_fragment_pending_sentences);
        recycler_view.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler_view.setLayoutManager(layoutManager);

        no_data = view.findViewById(R.id.no_data);

        onCreateAdapterView();

        return view;
    }

    public void onCreateAdapterView(){

        List<BrainSentences> brainSentencesList = SugarRecord.find(BrainSentences.class, "status=?", "pending");

        if (brainSentencesList.size() > 0) {
            recycler_view.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.INVISIBLE);

            adapter = new BrainSentencesAdapter(brainSentencesList, this);
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
            recycler_view.setAdapter(new SlideInLeftAnimationAdapter(alphaAdapter));
        } else {
            recycler_view.setVisibility(View.INVISIBLE);
            no_data.setVisibility(View.VISIBLE);
            no_data.setText(R.string.no_data);
        }
    }

    @Override
    public void onPopupMenuClickBrainSentences(View view, int pos, final Long id, String status, final String label) {

        PopupMenu popup = new PopupMenu(getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.brain_popup_menu, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.approve_button:
                        setStatus(id, "approuved");
                        break;
                    case R.id.reject_button:
                        setStatus(id, "rejected");
                        break;
                    case R.id.set_label_button:
                        setLabelDialog(id, label);
                        break;
                }
                return false;
            }
        });

    }

    public void setStatus(final Long id, final String status){

        getConnection();
        if(connection){

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                    .build();

            RetrofitAPI service = retrofit.create(RetrofitAPI.class);

            Call<Void> call = service.setStatus(id, status, pref_token);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.code() == 200){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.command_send), Snackbar.LENGTH_LONG).setAction("Action", null).show();

                        BrainSentences brainSentences = (SugarRecord.find(BrainSentences.class, "sentencesid = ?", id.toString())).get(0);
                        brainSentences.setStatue(status);
                        SugarRecord.save(brainSentences);
                        adapter.notifyDataSetChanged();
                    }else {
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });
        }
    }

    public void setLabelDialog(final Long id, String label){

        getIndexLabel(label);

        new MaterialDialog.Builder(getActivity())
                .title(R.string.title_dialog_label)
                .items(R.array.label)
                .itemsCallbackSingleChoice(index_label, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        setLabel(id, text.toString());
                        return true;
                    }
                })
                .positiveText(R.string.positve_button)
                .negativeText(R.string.negative_button)
                .show();
    }

    public void setLabel(final Long id, final String label){

        getConnection();
        if(connection){

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                    .build();

            RetrofitAPI service = retrofit.create(RetrofitAPI.class);

            Call<Void> call = service.setLabel(id, label, pref_token);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> all, Response<Void> response) {
                    if (response.code() == 200){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.command_send), Snackbar.LENGTH_LONG).setAction("Action", null).show();

                        BrainSentences brainSentences = (SugarRecord.find(BrainSentences.class, "sentencesid = ?", id.toString())).get(0);
                        brainSentences.setLabel(label);
                        SugarRecord.save(brainSentences);
                        adapter.notifyDataSetChanged();
                    }else {
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> all, Throwable t) {
                    Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });
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

    public void getIndexLabel(String label){
        List<String> labelString = Arrays.asList(getResources().getStringArray(R.array.label));
        index_label = labelString.indexOf(label);
    }
}
