package com.gladysinc.gladys;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gladysinc.gladys.Adapters.AlarmAdapter;
import com.gladysinc.gladys.Models.Alarm;
import com.gladysinc.gladys.Utils.AdapterCallback;
import com.gladysinc.gladys.Utils.Connectivity;
import com.gladysinc.gladys.Utils.DateTimeUtils;
import com.gladysinc.gladys.Utils.RetrofitAPI;
import com.gladysinc.gladys.Utils.SelfSigningClientBuilder;
import com.orm.SugarRecord;

import java.lang.ref.WeakReference;
import java.util.Calendar;
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


public class AlarmFragment extends Fragment implements AdapterCallback.AdapterCallbackAlarm {

    String url, pref_token;
    Boolean connection;
    RecyclerView recycler_view;
    EditText spe_alarm_name, spe_alarm_time, spe_alarm_date;
    EditText rec_alarm_name, rec_alarm_time;
    String spe_name, rec_name, date_time, day_of_week, id_of_day;
    String time, date;
    EditText cron_name, cron_rule;
    String cron_rule_name, rule;
    TextView no_data_alarm;
    ImageView no_data_alarm_ic;
    AlarmAdapter adapter;
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
                if (connection) {
                    getAllAlarms();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        recycler_view = view.findViewById(R.id.rv_fragment_alarm);
        recycler_view.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recycler_view.setLayoutManager(layoutManager);

        no_data_alarm = view.findViewById(R.id.no_data_alarm);
        no_data_alarm_ic = view.findViewById(R.id.no_data_alarm_ic);

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

    public void getAllAlarms() {

        get_data_progress.setVisible(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);

        Call<List<Alarm>> call = service.getAlarms(pref_token);

        call.enqueue(new Callback<List<Alarm>>() {
            @Override
            public void onResponse(Call<List<Alarm>> call, Response<List<Alarm>> response) {
                List<Alarm> AlarmData = response.body();

                if(AlarmData != null){
                    save_data = new SaveData(AlarmFragment.this);
                    save_data.execute(AlarmData);
                } else {
                    onRefreshAdapterView();
                    get_data_progress.setVisible(false);
                    if(getActivity() != null){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Alarm>> call, Throwable t) {
                if(!Objects.equals(t.getMessage(), "java.net.SocketTimeoutException")){
                    if(getActivity() != null){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
            }
        });

    }

    public void alarmDialog(){
        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.title_dialog_alarm)
                .customView(R.layout.select_alarm_view, true)
                .show();


        Button specific_date_button = dialog.getCustomView().findViewById(R.id.specific_date_button);
        Button recurring = dialog.getCustomView().findViewById(R.id.recurring_button);
        final Button cron = dialog.getCustomView().findViewById(R.id.cron_button);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)  specific_date_button.getLayoutParams();
        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)  recurring.getLayoutParams();
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)  cron.getLayoutParams();

        params.width = ((getResources().getDisplayMetrics().widthPixels)/4);
        params.height = ((getResources().getDisplayMetrics().heightPixels)/13);

        params1.width = ((getResources().getDisplayMetrics().widthPixels)/4);
        params1.height = ((getResources().getDisplayMetrics().heightPixels)/13);

        params2.width = ((getResources().getDisplayMetrics().widthPixels)/4);
        params2.height = ((getResources().getDisplayMetrics().heightPixels)/13);

        specific_date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSpecificDate();
                dialog.dismiss();
            }
        });


        recurring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRecurring();
                dialog.dismiss();
            }
        });

        cron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cronRule();
                dialog.dismiss();
            }
        });
    }

    public void clickRecurring(){
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.recurring)
                .customView(R.layout.recurring_alarm, false)
                .positiveText(R.string.positve_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        getConnection();
                        if (connection) {
                            if (rec_alarm_name.getText().toString().isEmpty() | rec_alarm_time.getText().toString().isEmpty()){
                                if(getActivity() != null){
                                    Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.all_fields), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                }
                            }else {
                                id_of_day = DateTimeUtils.getIdDay(day_of_week);
                                createAlarmRec();
                            }
                        }
                    }
                })
                .negativeText(R.string.negative_button)
                .show();

        rec_alarm_name = dialog.getCustomView().findViewById(R.id.recurring_alarm_name);
        rec_alarm_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rec_name = rec_alarm_name.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        rec_alarm_time = (EditText) dialog.findViewById(R.id.recurring_alarm_time);
        rec_alarm_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker("rec");
            }
        });

        Spinner alarm_rec_day = dialog.getCustomView().findViewById(R.id.recurring_alarm_day);
        alarm_rec_day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                day_of_week = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void clickSpecificDate(){
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.specific_date)
                .customView(R.layout.specific_date_alarm, false)
                .positiveText(R.string.positve_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        getConnection();
                        if (connection) {

                            if (spe_alarm_name.getText().toString().isEmpty() | spe_alarm_time.getText().toString().isEmpty() | spe_alarm_date.getText().toString().isEmpty()){
                                if(getActivity() != null){
                                    Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.all_fields), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                }
                            }else {
                                date_time = DateTimeUtils.parseDateTime(date + time);
                                createAlarmSpe();
                            }
                        }
                    }
                })
                .negativeText(R.string.negative_button)
                .show();

        spe_alarm_name = dialog.getCustomView().findViewById(R.id.specific_alarm_name);
        spe_alarm_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                spe_name = spe_alarm_name.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        spe_alarm_time = (EditText) dialog.findViewById(R.id.specific_alarm_time);
        spe_alarm_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker("spe");
            }
        });

        spe_alarm_time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                time = spe_alarm_time.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        spe_alarm_date = (EditText) dialog.findViewById(R.id.specific_alarm_date);
        spe_alarm_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        spe_alarm_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                date = spe_alarm_date.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    public void cronRule(){
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.cron)
                .customView(R.layout.cron_alarm, false)
                .positiveText(R.string.positve_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        getConnection();
                        if (connection) {

                            if (cron_name.getText().toString().isEmpty() | cron_rule.getText().toString().isEmpty()){
                                if(getActivity() != null){
                                    Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.all_fields), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                                }
                            }else {
                                createCronRule(cron_rule_name, rule);
                            }
                        }
                    }
                })
                .negativeText(R.string.negative_button)
                .show();

        cron_name =(EditText) dialog.findViewById(R.id.cron_alarm_name);
        cron_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cron_rule_name = cron_name.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        cron_rule =(EditText) dialog.findViewById(R.id.cron_alarm_rule);
        cron_rule.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rule = cron_rule.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    public void timePicker(final String type_of_alarm){

        MaterialDialog dialog;

        if( Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            dialog = new MaterialDialog.Builder(getActivity())
                    .title(R.string.set_time)
                    .customView(R.layout.time_picker, false)
                    .positiveText(R.string.positve_button)
                    .show();
        }else {
            dialog = new MaterialDialog.Builder(getActivity())
                    .title(R.string.set_time)
                    .customView(R.layout.time_picker_v21, false)
                    .positiveText(R.string.positve_button)
                    .show();
        }

        TimePicker timePicker = dialog.getCustomView().findViewById(R.id.time_picker);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                String hour;
                String min;

                if (hourOfDay < 10 ){
                    hour = "0" + hourOfDay;
                }else {
                    hour = String.valueOf(hourOfDay);
                }

                if (minute < 10 ){
                    min = "0" + minute;
                }else {
                    min = String.valueOf(minute);
                }

                time = hour + ":" + min;

                if (Objects.equals(type_of_alarm, "rec")){
                    rec_alarm_time.setText(time);
                }else {
                    spe_alarm_time.setText(time);}
            }
        });
    }

    public void datePicker(){

        MaterialDialog dialog;

        if( Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            dialog = new MaterialDialog.Builder(getActivity())
                    .title(R.string.set_date)
                    .customView(R.layout.date_picker, false)
                    .positiveText(R.string.positve_button)
                    .show();
        }else {
            dialog = new MaterialDialog.Builder(getActivity())
                    .title(R.string.set_date)
                    .customView(R.layout.date_picker_v21, false)
                    .positiveText(R.string.positve_button)
                    .show();
        }

        DatePicker datePicker = dialog.getCustomView().findViewById(R.id.date_picker);
        Calendar calendar = Calendar.getInstance();
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int month = monthOfYear + 1;
                date = dayOfMonth + "/" + month + "/" + year;
                spe_alarm_date.setText(date);
            }
        });

    }

    public void createAlarmRec(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);

        Call<Alarm> call = service.createAlarmRec(rec_name, time, id_of_day, true, pref_token);

        call.enqueue(new Callback<Alarm>() {
            @Override
            public void onResponse(Call<Alarm> call, Response<Alarm> response) {

                if (response.code() == 201){
                    getAllAlarms();
                    if(getActivity() != null){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.alarm_created), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }else {
                    if (getActivity() != null){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Alarm> call, Throwable t) {
                if(!Objects.equals(t.getMessage(), "java.net.SocketTimeoutException")){
                    if(getActivity() != null){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
            }
        });
    }

    public void createAlarmSpe(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);

        Call<Alarm> call = service.createAlarmSpe(spe_name, date_time, true, pref_token);

        call.enqueue(new Callback<Alarm>() {
        @Override
            public void onResponse(Call<Alarm> call, Response<Alarm> response) {

                if (response.code() == 201){
                    getAllAlarms();
                    if (getActivity() != null){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.alarm_created), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }else {
                    if (getActivity() != null){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Alarm> call, Throwable t) {
                if(!Objects.equals(t.getMessage(), "java.net.SocketTimeoutException")){
                    if(getActivity() != null){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
            }
        });

    }

    public void createCronRule(String name, String rule){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);

        Call<Alarm> call = service.createCronRule(name, rule, pref_token);

        call.enqueue(new Callback<Alarm>() {
            @Override
            public void onResponse(Call<Alarm> call, Response<Alarm> response) {

                if (response.code() == 201){
                    getAllAlarms();
                    if (getActivity() != null){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.alarm_created), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }else{
                    if (getActivity() != null){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Alarm> call, Throwable t) {
                if(!Objects.equals(t.getMessage(), "java.net.SocketTimeoutException")){
                    if(getActivity() != null){
                        Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                }
            }
        });
    }

    public void getConnection(){
        Connectivity.typeconnection(getContext());

        if (Objects.equals(type_of_connection, "0")
                | Objects.equals(type_of_connection, "1")
                | Objects.equals(type_of_connection, "2")
                | Objects.equals(type_of_connection, "3")
                | Objects.equals(type_of_connection, "4") ){

            connection = false;
            if (getActivity() != null ){
                Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + type_of_connection, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

        }else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            pref_token = prefs.getString("token", "");
            connection = true;
            url = type_of_connection;}

    }

    public void onCreateAdapterView(){

        long count = SugarRecord.count(Alarm.class);

        if(count>0) {
            recycler_view.setVisibility(View.VISIBLE);
            no_data_alarm.setVisibility(View.INVISIBLE);
            no_data_alarm_ic.setVisibility(View.INVISIBLE);

            List<Alarm> data = SugarRecord.listAll(Alarm.class);
            adapter = new AlarmAdapter(data, this);
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
            recycler_view.setAdapter(new SlideInLeftAnimationAdapter(alphaAdapter));
            getAllAlarms();
        } else {
            recycler_view.setVisibility(View.INVISIBLE);
            no_data_alarm.setVisibility(View.VISIBLE);
            no_data_alarm_ic.setVisibility(View.VISIBLE);
            //no_data.setText(R.string.no_data);
            getAllAlarms();
        }
    }

    public void onRefreshAdapterView(){

        long count = SugarRecord.count(Alarm.class);

        if(count>0) {
            recycler_view.setVisibility(View.VISIBLE);
            no_data_alarm.setVisibility(View.INVISIBLE);
            no_data_alarm_ic.setVisibility(View.INVISIBLE);

            List<Alarm> data = SugarRecord.listAll(Alarm.class);
            adapter = new AlarmAdapter(data, this);
            AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
            recycler_view.setAdapter(new SlideInLeftAnimationAdapter(alphaAdapter));
        } else {
            recycler_view.setVisibility(View.INVISIBLE);
            no_data_alarm.setVisibility(View.VISIBLE);
            no_data_alarm_ic.setVisibility(View.VISIBLE);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        MenuItem button = menu.findItem(R.id.add_button);
        button.setVisible(true);
        get_data_progress = menu.findItem(R.id.miActionProgress);

        getConnection();
        if (connection) {
            onCreateAdapterView();
        }else {
            onRefreshAdapterView();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.add_button) {
            alarmDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickCallbackAlarm(final Long alarm_id){

        new MaterialDialog.Builder(getActivity())
                .title(R.string.remove_alarm)
                .positiveText(R.string.positve_button)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteAlarm(alarm_id);
                    }
                })
                .negativeText(R.string.negative_button)
                .show();
    }

    public void deleteAlarm(Long alarm_id){

        getConnection();
        if (connection) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                    .build();

            RetrofitAPI service = retrofit.create(RetrofitAPI.class);

            Call<Void> call = service.deleteAlarm(alarm_id, pref_token);

            call.enqueue(new Callback<Void>() {
            @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    if (response.code() == 200){
                        getAllAlarms();
                        if (getActivity() != null){
                            Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.alarm_removed), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    } else {
                        if (getActivity() != null){
                            Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    if(!Objects.equals(t.getMessage(), "java.net.SocketTimeoutException")){
                        if(getActivity() != null){
                            Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        }
                    }
                }
            });
        }
    }

    public void onStop(){
        super.onStop();
        if (save_data != null && save_data.getStatus() != AsyncTask.Status.FINISHED)
            save_data.cancel(true);
    }

    private static class SaveData extends AsyncTask<List<Alarm>, Void, Boolean>
    {
        private WeakReference<AlarmFragment> alarm_fragment_weak_reference;
        boolean result;

        SaveData(AlarmFragment context){
            alarm_fragment_weak_reference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SafeVarargs
        @Override
        protected final Boolean doInBackground(List<Alarm>... params) {

            long count = SugarRecord.count(Alarm.class);

            if(count>0) {
                SugarRecord.deleteAll(Alarm.class);
            }

            List<Alarm> AlarmData = params[0];

            try {
                for (int i = 0; i < AlarmData.size(); i++) {
                    Alarm alarm = new Alarm(AlarmData.get(i).getAlarm_id()
                            , AlarmData.get(i).getName()
                            , AlarmData.get(i).getDatetime()
                            , AlarmData.get(i).getTime()
                            , AlarmData.get(i).getDayofweek()
                            , AlarmData.get(i).getCronrule()
                            , AlarmData.get(i).getActive());
                    SugarRecord.save(alarm);
                    if (isCancelled()) break;
                }

                result = true;

            } catch (Exception e){
                if(alarm_fragment_weak_reference.get().getActivity() != null){
                    Snackbar.make(alarm_fragment_weak_reference.get().getActivity().findViewById(R.id.layout), R.string.error + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                result = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            AlarmFragment alarmFragment = alarm_fragment_weak_reference.get();
            if (alarmFragment == null) return;

            if(result){
                alarmFragment.onRefreshAdapterView();
            } else {
                if(alarmFragment.getActivity() != null){
                    Snackbar.make(alarmFragment.getActivity().findViewById(R.id.layout), alarmFragment.getActivity().getString(R.string.error) + " " + "5", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            }
            alarmFragment.get_data_progress.setVisible(false);
        }
    }
}