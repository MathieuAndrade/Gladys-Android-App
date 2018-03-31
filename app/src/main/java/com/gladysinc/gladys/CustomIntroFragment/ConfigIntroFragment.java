package com.gladysinc.gladys.CustomIntroFragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gladysinc.gladys.Models.User;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.RetrofitAPI;
import com.gladysinc.gladys.Utils.SelfSigningClientBuilder;

import agency.tango.materialintroscreen.SlideFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.gladysinc.gladys.Utils.Connectivity.isConnected;
import static com.gladysinc.gladys.Utils.Connectivity.isConnectedWifi;


public class ConfigIntroFragment extends SlideFragment {

    String ip, port, token, dns, nat_port, url;
    TextView ip_input, port_input, token_input, dns_input, nat_port_input, nat_token_input;
    Button test_button;
    Boolean connection_passed = false, https;
    Switch https_switch;
    SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_config_intro, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        ip = prefs.getString("local_ip", "");
        port = prefs.getString("local_port", "");
        token = prefs.getString("token", "");
        dns = prefs.getString("dns","");
        nat_port = prefs.getString("nat_port", "");
        https = prefs.getBoolean("https", false);

        ip_input = view.findViewById(R.id.ip_input);
        port_input = view.findViewById(R.id.port_input);
        token_input = view.findViewById(R.id.token_input);

        ip_input.setText(ip);
        port_input.setText(port);
        token_input.setText(token);

        TextView help_token = view.findViewById(R.id.help_token);
        help_token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getActivity())
                        .titleColor(Color.BLACK)
                        .contentColor(Color.GRAY)
                        .backgroundColorRes(R.color.white)
                        .positiveColorRes(R.color.colorAccent)
                        .title(R.string.create_token)
                        .content(R.string.create_token_guide)
                        .positiveText(R.string.positve_button)
                        .show();
            }
        });

        test_button = view.findViewById(R.id.test_button);
        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isConnected(getContext())){
                    snackBarMessage(getString(R.string.error_code_0));
                    return;
                }

                if(!isConnectedWifi(getContext())){
                    new MaterialDialog.Builder(getActivity())
                            .titleColor(Color.BLACK)
                            .contentColor(Color.GRAY)
                            .backgroundColorRes(R.color.white)
                            .positiveColorRes(R.color.colorAccent)
                            .negativeColorRes(R.color.colorAccent)
                            .content(R.string.no_wifi_message)
                            .positiveText(R.string.yes)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    natParamsDialog();
                                }
                            })
                            .negativeText(R.string.no)
                            .show();
                    return;
                }

                if(testInputs()){
                    savePrefs("local_ip", ip);
                    savePrefs("local_port", port);
                    savePrefs("token", token);
                    url = "http://" + ip + ":" + port;
                    connection();
                }else {
                    snackBarMessage(getString(R.string.error_code_1));
                }

            }
        });

        return view;
    }

    @Override
    public int backgroundColor() {
        return R.color.colorPrimary;
    }

    @Override
    public int buttonsColor() {
        return R.color.colorAccent;
    }

    @Override
    public boolean canMoveFurther() {
        return connection_passed;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getString(R.string.error_code_6);
    }

    public void snackBarMessage(String message){
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public boolean testInputs(){

        ip = String.valueOf(ip_input.getText());
        port = String.valueOf(port_input.getText());
        token = String.valueOf(token_input.getText());

        return !ip.isEmpty() & !port.isEmpty() & !token.isEmpty();
    }

    public void savePrefs(String valueKey, String value) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(valueKey, value);
        edit.apply();
    }

    public void connection(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(SelfSigningClientBuilder.getUnsafeOkHttpClient())
                .build();

        RetrofitAPI service = retrofit.create(RetrofitAPI.class);

        Call<User> call = service.whoAmI(token);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.code() == 200){

                    final String firstname = response.body().getFirstname();
                    final String lastname = response.body().getLastname();
                    final Long user_id = response.body().getUserId();

                    new MaterialDialog.Builder(getActivity())
                            .contentColor(Color.GRAY)
                            .backgroundColorRes(R.color.white)
                            .positiveColorRes(R.color.colorAccent)
                            .negativeColorRes(R.color.colorAccent)
                            .content(getString(R.string.are_you) + " " + firstname + " " + lastname + " ?")
                            .positiveText(R.string.is_me)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    connection_passed = true;
                                    snackBarMessage(getString(R.string.you_are_ready));
                                    savePrefs("name", lastname);
                                    savePrefs("first_name", firstname);
                                    savePrefs("user_id", user_id.toString());
                                }
                            })
                            .negativeText(R.string.is_not_me)
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    new MaterialDialog.Builder(getActivity())
                                            .titleColor(Color.BLACK)
                                            .contentColor(Color.GRAY)
                                            .backgroundColorRes(R.color.white)
                                            .positiveColorRes(R.color.colorAccent)
                                            .title(R.string.it_is_not_you)
                                            .content(R.string.it_is_not_you_message)
                                            .positiveText(R.string.positve_button)
                                            .show();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                snackBarMessage(getString(R.string.error));
            }
        });
    }

    public void natParamsDialog(){

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .titleColor(Color.BLACK)
                .contentColor(Color.GRAY)
                .backgroundColorRes(R.color.white)
                .positiveColorRes(R.color.colorAccent)
                .positiveText(R.string.connection)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dns = String.valueOf(dns_input.getText());
                        nat_port = String.valueOf(nat_port_input.getText());
                        token = String.valueOf(nat_token_input.getText());
                        https = https_switch.isChecked();

                        if(!dns.isEmpty() & !nat_port.isEmpty() & !token.isEmpty()){

                            savePrefs("dns", dns);
                            savePrefs("nat_port", nat_port);
                            savePrefs("token", token);

                            if(https){

                                SharedPreferences.Editor edit = prefs.edit();
                                edit.putBoolean("https", https);
                                edit.apply();

                                url = "https://" + dns;
                                connection();
                            }else{
                                url = "http://" + dns + ":" + nat_port;
                                connection();
                            }

                        }else{
                            snackBarMessage(getString(R.string.error_code_1));
                        }
                    }
                })
                .title(R.string.settings)
                .customView(R.layout.dialog_settings_intro, true)
                .show();

        dns_input = dialog.getCustomView().findViewById(R.id.dns_input);
        nat_port_input = dialog.getCustomView().findViewById(R.id.nat_port_input);
        nat_token_input = dialog.getCustomView().findViewById(R.id.nat_token_input);
        https_switch = dialog.getCustomView().findViewById(R.id.https);

        dns_input.setText(dns);
        nat_token_input.setText(token);
        nat_port_input.setText(nat_port);
        nat_port_input.setEnabled(!https);
        https_switch.setChecked(https);

        https_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                nat_port_input.setEnabled(!b);
            }
        });
    }
}