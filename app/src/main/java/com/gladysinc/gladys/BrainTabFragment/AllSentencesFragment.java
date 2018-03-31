package com.gladysinc.gladys.BrainTabFragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gladysinc.gladys.Adapters.BrainSentencesAdapter;
import com.gladysinc.gladys.Models.BrainSentences;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.AdapterCallback;
import com.gladysinc.gladys.Utils.Connectivity;
import com.gladysinc.gladys.Utils.RetrofitAPI;
import com.gladysinc.gladys.Utils.SelfSigningClientBuilder;
import com.gladysinc.gladys.Utils.SnackbarUtils;
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


public class AllSentencesFragment extends Fragment implements AdapterCallback.AdapterCallbackBrainSentences  {

    String url, pref_token;
    Boolean connection;
    RecyclerView recycler_view;
    TextView no_data_all;
    ImageView no_data_all_ic;
    BrainSentencesAdapter adapter;
    Integer index_label;

    public static final String Title = "Toutes les phrases";

    public static AllSentencesFragment newInstance() {
        return new AllSentencesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_sentences, container, false);

        recycler_view = view.findViewById(R.id.rv_fragment_all_sentences);
        recycler_view.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler_view.setLayoutManager(layoutManager);

        no_data_all = view.findViewById(R.id.no_data_all);
        no_data_all_ic = view.findViewById(R.id.no_data_all_ic);

        final FloatingActionButton fab_scroll_up_all = view.findViewById(R.id.fab_scroll_up_all);
        fab_scroll_up_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recycler_view.smoothScrollToPosition(0);
                fab_scroll_up_all.animate().translationY(fab_scroll_up_all.getHeight() + 400).setInterpolator(new LinearInterpolator()).start();
            }
        });

        onCreateAdapterView();

        return view;
    }

    public void onCreateAdapterView(){

        long count = SugarRecord.count(BrainSentences.class);

        if (count > 0) {
            recycler_view.setVisibility(View.VISIBLE);
            no_data_all.setVisibility(View.INVISIBLE);
            no_data_all_ic.setVisibility(View.INVISIBLE);

            List<BrainSentences> data = SugarRecord.listAll(BrainSentences.class);
            adapter = new BrainSentencesAdapter(data, this);
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
            recycler_view.setAdapter(new SlideInLeftAnimationAdapter(alphaAdapter));
        } else {
            recycler_view.setVisibility(View.INVISIBLE);
            no_data_all.setVisibility(View.VISIBLE);
            no_data_all_ic.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPopupMenuClickBrainSentences(View view, int pos, final Long id, String status, final String label) {

        PopupMenu popup = new PopupMenu(getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.brain_popup_menu, popup.getMenu());
        MenuItem approve_button = popup.getMenu().findItem(R.id.approve_button);
        MenuItem reject_button = popup.getMenu().findItem(R.id.reject_button);

        switch (status){
            case "official":
                approve_button.setVisible(false);
                break;
            case "approved":
                approve_button.setVisible(false);
                break;
            case "rejected":
                reject_button.setVisible(false);
                break;
        }
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.approve_button:
                        setStatus(id, "approved");
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
                        if(getActivity() != null){
                            SnackbarUtils.simpleSnackBar(getContext(), getView(), getContext().getString(R.string.command_send));
                        }

                        BrainSentences brainSentences = (SugarRecord.find(BrainSentences.class, "sentencesid = ?", id.toString())).get(0);
                        brainSentences.setStatue(status);
                        SugarRecord.save(brainSentences);
                        onCreateAdapterView();
                    }else {
                        if(getActivity() != null){
                            SnackbarUtils.simpleSnackBar(getContext(), getView(), getContext().getString(R.string.error_code_4));
                        }
                    }

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    if(getActivity() != null){
                        SnackbarUtils.simpleSnackBar(getContext(), getView(), getContext().getString(R.string.error_code_5));
                    }
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

            Call<BrainSentences> call = service.setLabel(id, label, pref_token);
            call.enqueue(new Callback<BrainSentences>() {
                @Override
                public void onResponse(Call<BrainSentences> call ,Response<BrainSentences> response) {

                    if (response.code() == 200){
                        if(getActivity() != null){
                            SnackbarUtils.simpleSnackBar(getContext(), getView(), getContext().getString(R.string.command_send));
                        }

                        BrainSentences brainSentences = (SugarRecord.find(BrainSentences.class, "sentencesid = ?", id.toString())).get(0);
                        brainSentences.setLabel(response.body().getLabel());
                        brainSentences.setService(response.body().getService());
                        SugarRecord.save(brainSentences);
                        onCreateAdapterView();
                    }else {
                        if(getActivity() != null){
                            SnackbarUtils.simpleSnackBar(getContext(), getView(), getContext().getString(R.string.error_code_4));
                        }
                    }
                }

                @Override
                public void onFailure(Call<BrainSentences> call ,Throwable t) {
                    if(getActivity() != null){
                        SnackbarUtils.simpleSnackBar(getContext(), getView(), getContext().getString(R.string.error_code_5));
                    }
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
            if(getActivity() != null){
                SnackbarUtils.simpleSnackBar(getContext(), getView(), getContext().getString(R.string.error_code_7));
            }

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
