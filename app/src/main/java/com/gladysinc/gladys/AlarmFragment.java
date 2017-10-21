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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static com.gladysinc.gladys.Utils.Connectivity.typeofconnection;


public class AlarmFragment extends Fragment implements AdapterCallback.AdapterCallbackAlarm {

    String url, preftoken;
    Boolean connection;
    RecyclerView recyclerView;
    EditText alarm_spe_name, alarm_spe_time, alarm_spe_date;
    EditText alarm_rec_name, alarm_rec_time;
    String spe_name, rec_name, date_time, day_of_week, id_of_day;
    String time, date;
    TextView noData;
    AlarmAdapter adapter;
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
                if (connection) {
                    getAlarms();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_alarm, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.rv_fragment_alarm);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        noData = (TextView) v.findViewById(R.id.no_data);

        return v;
    }

    public void getAlarms() {

        getDataProgress.setVisible(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);

        Call<List<Alarm>> call = service.getAlarms(preftoken);

        call.enqueue(new Callback<List<Alarm>>(){
            @Override
            public void onResponse(Response<List<Alarm>> response, Retrofit retrofit) {

                List<Alarm> AlarmData = response.body();

                if(AlarmData != null){
                    saveData = new SaveData();
                    saveData.execute(AlarmData);
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

    public void alarmDialog(){
        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.title_dialog_alarm)
                .customView(R.layout.select_alarm_view, true)
                .show();


        Button specific_date_button = (Button) dialog.getCustomView().findViewById(R.id.specific_date_button);
        Button recurring = (Button) dialog.getCustomView().findViewById(R.id.recurring_button);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)  specific_date_button.getLayoutParams();
        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams)  recurring.getLayoutParams();

        params.width = ((getResources().getDisplayMetrics().widthPixels)/4);
        params.height = ((getResources().getDisplayMetrics().heightPixels)/13);

        params1.width = ((getResources().getDisplayMetrics().widthPixels)/4);
        params1.height = ((getResources().getDisplayMetrics().heightPixels)/13);

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
                            if (alarm_rec_name.getText().toString().isEmpty() | alarm_rec_time.getText().toString().isEmpty()){
                                Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.all_fields), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }else {
                                id_of_day = DateTimeUtils.getIdDay(day_of_week);
                                createAlarmRec();
                            }
                        }
                    }
                })
                .negativeText(R.string.negative_button)
                .show();

        alarm_rec_name = (EditText) dialog.getCustomView().findViewById(R.id.alarmRecName);
        alarm_rec_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rec_name = alarm_rec_name.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        alarm_rec_time = (EditText) dialog.findViewById(R.id.alarmRecTime);
        alarm_rec_time.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (alarm_rec_time.getRight() - alarm_rec_time.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        timePicker("rec");
                        return true;
                    }
                }
                return false;
            }
        });

        Spinner alarm_rec_day = (Spinner) dialog.getCustomView().findViewById(R.id.alarmRecDay);
        alarm_rec_day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                day_of_week = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
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

                            if (alarm_spe_name.getText().toString().isEmpty() | alarm_spe_time.getText().toString().isEmpty() | alarm_spe_date.getText().toString().isEmpty()){
                                Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.all_fields), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }else {
                                date_time = DateTimeUtils.parseDateTime(date + time, "dd/MM/yyyyHH:mm", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                createAlarmSpe();
                            }
                        }
                    }
                })
                .negativeText(R.string.negative_button)
                .show();

        alarm_spe_name = (EditText) dialog.getCustomView().findViewById(R.id.alarmSpeName);
        alarm_spe_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                spe_name = alarm_spe_name.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        alarm_spe_time = (EditText) dialog.findViewById(R.id.alarmSpeTime);
        alarm_spe_time.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (alarm_spe_time.getRight() - alarm_spe_time.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        timePicker("spe");
                        return true;
                    }
                }
                return false;
            }
        });

        alarm_spe_time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                time = alarm_spe_time.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        alarm_spe_date = (EditText) dialog.findViewById(R.id.alarmSpeDate);
        alarm_spe_date.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (alarm_spe_date.getRight() - alarm_spe_date.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        datePicker();
                        return true;
                    }
                }
                return false;
            }
        });

        alarm_spe_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                date = alarm_spe_date.getText().toString();
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

        TimePicker timePicker = (TimePicker) dialog.getCustomView().findViewById(R.id.timePicker);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                time = hourOfDay + ":" + minute;

                if (Objects.equals(type_of_alarm, "rec")){
                    alarm_rec_time.setText(time);
                }else {alarm_spe_time.setText(time);}
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

        DatePicker datePicker = (DatePicker) dialog.getCustomView().findViewById(R.id.datePicker);
        Calendar calendar = Calendar.getInstance();
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                int month = monthOfYear + 1;
                date = dayOfMonth + "/" + month + "/" + year;
                alarm_spe_date.setText(date);
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

        Call<Alarm> call = service.createAlarmRec(rec_name, time, id_of_day, true, preftoken);

        call.enqueue(new Callback<Alarm>() {
            @Override
            public void onResponse(Response<Alarm> response, Retrofit retrofit) {

                getAlarms();
                Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.alarm_created), Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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

        Call<Alarm> call = service.createAlarmSpe(spe_name, date_time, true, preftoken);

        call.enqueue(new Callback<Alarm>() {
        @Override
            public void onResponse(Response<Alarm> response, Retrofit retrofit) {

                getAlarms();
                Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.alarm_created), Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }

            @Override
            public void onFailure(Throwable t) {
                Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

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

    public void onCreateAdapterView(){

        long count = SugarRecord.count(Alarm.class);

        if(count>0) {
            recyclerView.setVisibility(View.VISIBLE);
            noData.setVisibility(View.INVISIBLE);

            List<Alarm> data = SugarRecord.listAll(Alarm.class);
            adapter = new AlarmAdapter(data, this);
            recyclerView.setAdapter(adapter);
            getAlarms();
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            noData.setVisibility(View.VISIBLE);
            noData.setText(R.string.nodata);
            getAlarms();
        }
    }

    public void refreshAdapterView(){

        long count = SugarRecord.count(Alarm.class);

        if(count>0) {
            recyclerView.setVisibility(View.VISIBLE);
            noData.setVisibility(View.INVISIBLE);

            List<Alarm> data = SugarRecord.listAll(Alarm.class);
            adapter = new AlarmAdapter(data, this);
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
        if (connection) {
            onCreateAdapterView();
        }else {
            refreshAdapterView();
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

    public void onClickCallbackAlarm(Long alarm_id){
        deleteAlarm(alarm_id);
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

            Call<Void> call = service.deleteAlarm(alarm_id, preftoken);

            call.enqueue(new Callback<Void>() {
            @Override
                public void onResponse(Response<Void> response, Retrofit retrofit) {
                    getAlarms();
                    Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.alarm_removed), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

                @Override
                public void onFailure(Throwable t) {
                    Snackbar.make(getActivity().findViewById(R.id.layout), getActivity().getString(R.string.error) + " " + "6", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });
        }
    }

    public void onDestroyView(){
        super.onDestroyView();
        if (saveData != null && saveData.getStatus() != AsyncTask.Status.FINISHED)
            saveData.cancel(true);
    }

    private class SaveData extends AsyncTask<List<Alarm>, Integer, Boolean>
    {
        boolean result;

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