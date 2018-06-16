package com.gladysinc.gladys.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gladysinc.gladys.BuildConfig;
import com.gladysinc.gladys.R;

import java.util.Objects;


public class InfosFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FloatingActionButton fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
        if(fab != null){
            fab.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_infos, container, false);

        TextView app_info = view.findViewById(R.id.app_info);
        app_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + Objects.requireNonNull(getActivity()).getPackageName()));
                startActivity(intent);
            }
        });

        TextView app_version = view.findViewById(R.id.app_version);
        String version = Objects.requireNonNull(getActivity()).getString(R.string.version) + " Beta" + " " + BuildConfig.VERSION_NAME;
        app_version.setText(version);

        TextView open_source_licences = view.findViewById(R.id.open_source_license);
        open_source_licences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.open_source_license)
                        .items(R.array.open_source_licence)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                                String url = "";

                                switch (position){
                                    case 0:
                                        url =  getActivity().getString(R.string.gson);
                                        break;
                                    case 1:
                                        url =  getActivity().getString(R.string.material_dialogs);
                                        break;
                                    case 2:
                                        url =  getActivity().getString(R.string.material_intro_screen);
                                        break;
                                    case 3:
                                        url =  getActivity().getString(R.string.okhttp);
                                        break;
                                    case 4:
                                        url =  getActivity().getString(R.string.recycleview_animator);
                                        break;
                                    case 5:
                                        url =  getActivity().getString(R.string.retrofit);
                                        break;
                                    case 6:
                                        url =  getActivity().getString(R.string.sugarorm);
                                        break;
                                    case 7:
                                        url =  getActivity().getString(R.string.vipulasri);
                                        break;
                                }

                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(browserIntent);
                            }
                        })
                        .show();
            }
        });
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
    }
}
