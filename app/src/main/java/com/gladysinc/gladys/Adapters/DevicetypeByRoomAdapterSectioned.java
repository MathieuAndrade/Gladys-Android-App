package com.gladysinc.gladys.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.gladysinc.gladys.Models.Devicetype;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.AdapterCallback;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class DevicetypeByRoomAdapterSectioned extends StatelessSection {

    private String room;
    private List<Devicetype> devicetypes;
    private Context context;
    private AdapterCallback.AdapterCallbackDevicestate callback_devicestate;

    public DevicetypeByRoomAdapterSectioned(String room, List<Devicetype> devicetypeList, AdapterCallback.AdapterCallbackDevicestate callbackDevicestate, Context context) {
        super(new SectionParameters.Builder(R.layout.card_device)
                .headerResourceId(R.layout.card_device_room_header)
                .build());

        this.room = room;
        this.devicetypes = devicetypeList;
        this.callback_devicestate = callbackDevicestate;
        this.context = context;
    }

    @Override
    public int getContentItemsTotal() {
        return devicetypes.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;
        final int itemType = getViewType(position);
        final Long id = devicetypes.get(position).getDevicetypeId();

        switch (itemType) {

            case 1:

                itemHolder.device_sensor_rv.setVisibility(View.INVISIBLE);
                itemHolder.device_binary_rv.setVisibility(View.VISIBLE);
                itemHolder.device_multilevel_rv.setVisibility(View.INVISIBLE);
                itemHolder.device_binary_rv.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                itemHolder.device_multilevel_rv.getLayoutParams().height = 0;
                itemHolder.device_sensor_rv.getLayoutParams().height = 0;

                itemHolder.device_binary_name.setText(devicetypes.get(position).getDevicetypeName());

                if(devicetypes.get(position).getTag() == null){
                    itemHolder.device_binary_tag.setText(context.getString(R.string.empty));
                }else {
                    itemHolder.device_binary_tag.setText(devicetypes.get(position).getTag());
                }

                itemHolder.device_binary_value.setOnCheckedChangeListener(null);

                if (devicetypes.get(position).getLastValue() == null){
                    itemHolder.device_binary_value.setChecked(false);
                }else if (devicetypes.get(position).getLastValue() == 0) {
                    itemHolder.device_binary_value.setChecked(false);
                } else {
                    itemHolder.device_binary_value.setChecked(true);
                }

                itemHolder.device_binary_value.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Float value;
                        if (isChecked){
                            value = 1f;
                        }else {
                            value = 0f;
                        }
                        callback_devicestate.onClickCallbackDevicestate(id, value);
                    }
                });

                if(devicetypes.get(position).getCategory() != null) {
                    itemHolder.device_binary_logo.setImageResource(getLogo(devicetypes.get(position).getCategory()));
                }

                break;

            case 2:

                itemHolder.device_sensor_rv.setVisibility(View.INVISIBLE);
                itemHolder.device_binary_rv.setVisibility(View.INVISIBLE);
                itemHolder.device_multilevel_rv.setVisibility(View.VISIBLE);
                itemHolder.device_binary_rv.getLayoutParams().height = 0;
                itemHolder.device_multilevel_rv.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                itemHolder.device_sensor_rv.getLayoutParams().height = 0;

                String multilevelValueNull = context.getString(R.string.value) + " " + context.getString(R.string.empty);

                itemHolder.device_multilevel_name.setText(devicetypes.get(position).getDevicetypeName());

                if(devicetypes.get(position).getTag() == null){
                    itemHolder.device_multilevel_tag.setText(context.getString(R.string.empty));
                }else {
                    itemHolder.device_multilevel_tag.setText(devicetypes.get(position).getTag());
                }

                if (devicetypes.get(position).getLastValue() == null) {
                    itemHolder.device_multilevel_value_text.setText(multilevelValueNull);
                } else {
                    String multilevelValue = context.getString(R.string.value) + " " + devicetypes.get(position).getLastValue().toString();
                    itemHolder.device_multilevel_value_text.setText(multilevelValue);
                }


                itemHolder.device_multilevel_value.setOnSeekBarChangeListener(null);
                itemHolder.device_multilevel_value.setMax(devicetypes.get(position).getMax());

                if(devicetypes.get(position).getLastValue() == null){
                    itemHolder.device_multilevel_value.setProgress(0);
                } else {
                    itemHolder.device_multilevel_value.setProgress(devicetypes.get(position).getLastValue().intValue());
                }
                itemHolder.device_multilevel_value.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        String progressValue = context.getString(R.string.value) + " " + progress;
                        itemHolder.device_multilevel_value_text.setText(progressValue);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        callback_devicestate.onClickCallbackDevicestate(id, (float) seekBar.getProgress());
                    }
                });

                if(devicetypes.get(position).getCategory() != null) {
                    itemHolder.device_multilevel_logo.setImageResource(getLogo(devicetypes.get(position).getCategory()));
                }

                break;

            default:

                itemHolder.device_sensor_rv.setVisibility(View.VISIBLE);
                itemHolder.device_binary_rv.setVisibility(View.INVISIBLE);
                itemHolder.device_multilevel_rv.setVisibility(View.INVISIBLE);
                itemHolder.device_binary_rv.getLayoutParams().height = 0;
                itemHolder.device_multilevel_rv.getLayoutParams().height = 0;
                itemHolder.device_sensor_rv.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;

                itemHolder.device_sensor_name.setText(devicetypes.get(position).getDevicetypeName());

                if(devicetypes.get(position).getTag() == null){
                    itemHolder.device_sensor_tag.setText(context.getString(R.string.empty));
                }else {
                    itemHolder.device_sensor_tag.setText(devicetypes.get(position).getTag());
                }

                if(devicetypes.get(position).getLastValue() == null) {
                    itemHolder.device_sensor_value.setText("0");
                }else {
                    String sensorValue = devicetypes.get(position).getLastValue().toString() + " " + devicetypes.get(position).getUnit();
                    itemHolder.device_sensor_value.setText(sensorValue);
                }

                if(devicetypes.get(position).getCategory() != null) {
                    itemHolder.device_sensor_logo.setImageResource(getLogo(devicetypes.get(position).getCategory()));
                }

                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

        headerHolder.room.setText(room);
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView room;

        HeaderViewHolder(View view) {
            super(view);

            room = view.findViewById(R.id.device_room_name);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout device_binary_rv
                , device_multilevel_rv
                , device_sensor_rv;

        private TextView device_binary_name, device_binary_tag
                , device_multilevel_name, device_multilevel_tag, device_multilevel_value_text
                , device_sensor_name, device_sensor_tag, device_sensor_value;

        private ImageView device_binary_logo
                , device_multilevel_logo
                , device_sensor_logo;

        private Switch device_binary_value;
        private SeekBar device_multilevel_value;

        ItemViewHolder(View view) {
            super(view);

            device_binary_rv = view.findViewById(R.id.device_binary_rv);
            device_binary_logo = view.findViewById(R.id.device_binary_logo);
            device_binary_name = view.findViewById(R.id.device_binary_name);
            device_binary_tag = view.findViewById(R.id.device_binary_tag);
            device_binary_value = view.findViewById(R.id.device_binary_value);

            device_multilevel_rv = view.findViewById(R.id.device_multilevel_rv);
            device_multilevel_logo = view.findViewById(R.id.device_multilevel_logo);
            device_multilevel_name = view.findViewById(R.id.device_multilevel_name);
            device_multilevel_tag = view.findViewById(R.id.device_multilevel_tag);
            device_multilevel_value_text = view.findViewById(R.id.device_multilevel_velue_text);
            device_multilevel_value = view.findViewById(R.id.device_multilevel_value);

            device_sensor_rv = view.findViewById(R.id.device_sensor_rv);
            device_sensor_logo = view.findViewById(R.id.device_sensor_logo);
            device_sensor_name = view.findViewById(R.id.device_sensor_name);
            device_sensor_tag = view.findViewById(R.id.device_sensor_tag);
            device_sensor_value = view.findViewById(R.id.device_sensor_value);
        }
    }

    private int getViewType(int position) {
        int viewType;

        switch (devicetypes.get(position).getType()) {

            case "binary":
                viewType = 1;
                break;

            case "multilevel":
                if(devicetypes.get(position).getSensor() == 1){
                    viewType = 3;
                }else {viewType = 2;}
                break;

            case "byte":
                if(devicetypes.get(position).getSensor() == 1){
                    viewType = 3;
                }else {viewType = 2;}
                break;

            case "brightness":
                if(devicetypes.get(position).getSensor() == 1){
                    viewType = 3;
                }else {viewType = 2;}
                break;

            case "saturation":
                if(devicetypes.get(position).getSensor() == 1){
                    viewType = 3;
                }else {viewType = 2;}
                break;

            default:
                viewType = 3;
                break;
        }

        return viewType;
    }

    private int getLogo(String category){

        int imageView = 0;

        switch (category){
            case "light":
                imageView = R.drawable.ic_light;
                break;

            case "outlet":
                imageView = R.drawable.ic_outlet;
                break;

            case "music":
                imageView = R.drawable.ic_music;
                break;

            case "tv":
                imageView = R.drawable.ic_tv;
                break;

            case "phone":
                imageView = R.drawable.ic_phone;
                break;

            case "computer":
                imageView = R.drawable.ic_computeur;
                break;

            default:
                break;
        }

        return imageView;
    }
}

