package com.gladysinc.gladys.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.gladysinc.gladys.Models.Devicetype;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.AdapterCallback;

import java.util.List;

public class DevicetypeByRoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Devicetype> devicetypeList;
    private Context context;
    private  int lastPosition = -1;
    private AdapterCallback.AdapterCallbackDevicestate callbackDevicestate;

    public DevicetypeByRoomAdapter(List<Devicetype> devicetypeList, AdapterCallback.AdapterCallbackDevicestate callbackDevicestate){
        this.devicetypeList = devicetypeList;
        this.callbackDevicestate = callbackDevicestate;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;

        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {

            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_device_binary, parent, false);
                viewHolder = new DevicetypeByRoomAdapter.BinaryViewHolder(view);
                break;

            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_device_multilevel, parent, false);
                viewHolder = new DevicetypeByRoomAdapter.MultilevelViewHolder(view);
                break;

            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_device_sensor, parent, false);
                viewHolder = new DevicetypeByRoomAdapter.SensorViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final int itemType = getItemViewType(position);
        final Long id = devicetypeList.get(position).getDevicetypeId();


        switch (itemType) {
            case 1:
                ((BinaryViewHolder) holder).devicebinary_name.setText(devicetypeList.get(position).getDevicetypeName());
                ((BinaryViewHolder) holder).devicebinary_room.setText(devicetypeList.get(position).getRoomName());

                ((BinaryViewHolder) holder).devicebinary_value.setOnCheckedChangeListener(null);

                if (devicetypeList.get(position).getLastValue() == null){
                    ((BinaryViewHolder) holder).devicebinary_value.setChecked(false);
                }else if (devicetypeList.get(position).getLastValue() == 0) {
                    ((BinaryViewHolder) holder).devicebinary_value.setChecked(false);
                } else {
                    ((BinaryViewHolder) holder).devicebinary_value.setChecked(true);
                }

                ((BinaryViewHolder) holder).devicebinary_value.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Float value;
                        if (isChecked){
                            value = 1f;
                        }else {
                            value = 0f;
                        }
                        callbackDevicestate.onClickCallbackDevicestate(id, value);
                    }
                });

                if(devicetypeList.get(position).getCategory() != null) {
                    ((BinaryViewHolder) holder).devicebinary_logo.setImageResource(getLogo(devicetypeList.get(position).getCategory()));
                }

                break;

            case 2:

                ((MultilevelViewHolder) holder).devicemultilevel_name.setText(devicetypeList.get(position).getDevicetypeName());
                ((MultilevelViewHolder) holder).devicemultilevel_room.setText(devicetypeList.get(position).getRoomName());

                if (devicetypeList.get(position).getLastValue() == null) {
                    ((MultilevelViewHolder) holder).devicemultilevel_value.setText(context.getString(R.string.value) + " " + context.getString(R.string.empty) );
                } else {
                    ((MultilevelViewHolder) holder).devicemultilevel_value.setText(context.getString(R.string.value) + " " + devicetypeList.get(position).getLastValue().toString());
                }


                ((MultilevelViewHolder) holder).seekBar.setOnSeekBarChangeListener(null);
                ((MultilevelViewHolder) holder).seekBar.setMax(devicetypeList.get(position).getMax());

                if(devicetypeList.get(position).getLastValue() == null){
                    ((MultilevelViewHolder) holder).seekBar.setProgress(0);
                } else {
                    ((MultilevelViewHolder) holder).seekBar.setProgress(devicetypeList.get(position).getLastValue().intValue());
                }
                ((MultilevelViewHolder) holder).seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        ((MultilevelViewHolder) holder).devicemultilevel_value.setText(context.getString(R.string.value) + " " + progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        callbackDevicestate.onClickCallbackDevicestate(id, (float) seekBar.getProgress());
                    }
                });

                if(devicetypeList.get(position).getCategory() != null) {
                    ((MultilevelViewHolder) holder).devicemultilevel_logo.setImageResource(getLogo(devicetypeList.get(position).getCategory()));
                }

                break;

            default:
                ((SensorViewHolder) holder).devicesensor_name.setText(devicetypeList.get(position).getDevicetypeName());
                ((SensorViewHolder) holder).devicesensor_room.setText(devicetypeList.get(position).getRoomName());

                if(devicetypeList.get(position).getLastValue() == null) {
                    ((SensorViewHolder) holder).devicesensor_value.setText("0");
                }else {
                    ((SensorViewHolder) holder).devicesensor_value.setText(devicetypeList.get(position).getLastValue().toString() + " " + devicetypeList.get(position).getUnit());
                }

                if(devicetypeList.get(position).getCategory() != null) {
                    ((SensorViewHolder) holder).devicesensor_logo.setImageResource(getLogo(devicetypeList.get(position).getCategory()));
                }

                break;
        }

        setAnimation(holder.itemView, position);

    }

    private class BinaryViewHolder extends RecyclerView.ViewHolder{
        private TextView devicebinary_name, devicebinary_room;
        private ImageView devicebinary_logo;
        private Switch devicebinary_value;
        BinaryViewHolder(View view) {
            super(view);

            devicebinary_logo = (ImageView)view.findViewById(R.id.logo_binary);
            devicebinary_name = (TextView)view.findViewById(R.id.device_binary_name);
            devicebinary_room = (TextView)view.findViewById(R.id.device_binary_room);
            devicebinary_value = (Switch)view.findViewById(R.id.binary_value);
        }
    }

    private class MultilevelViewHolder extends RecyclerView.ViewHolder{
        private TextView devicemultilevel_name, devicemultilevel_room, devicemultilevel_value;
        private ImageView devicemultilevel_logo;
        private SeekBar seekBar;
        MultilevelViewHolder(View view) {
            super(view);

            devicemultilevel_logo = (ImageView)view.findViewById(R.id.logo_multilevel);
            devicemultilevel_name = (TextView)view.findViewById(R.id.device_multilevel_name);
            devicemultilevel_room = (TextView)view.findViewById(R.id.device_multilevel_room);
            devicemultilevel_value = (TextView)view.findViewById(R.id.value_seekBar);
            seekBar = (SeekBar)view.findViewById(R.id.multilevel_value);
        }
    }

    private class SensorViewHolder extends RecyclerView.ViewHolder{
        private TextView devicesensor_name, devicesensor_room, devicesensor_value;
        private ImageView devicesensor_logo;
        SensorViewHolder(View view) {
            super(view);

            devicesensor_logo = (ImageView)view.findViewById(R.id.logo_sensor);
            devicesensor_name = (TextView)view.findViewById(R.id.device_sensor_name);
            devicesensor_room = (TextView)view.findViewById(R.id.device_sensor_room);
            devicesensor_value = (TextView)view.findViewById(R.id.sensor_value);
        }
    }

    @Override
    public int getItemCount() {
        return devicetypeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;

        switch (devicetypeList.get(position).getType()) {
            case "binary":
                viewType = 1;
                break;

            case "multilevel":
                viewType = 2;
                break;

            case "byte":
                viewType = 2;
                break;

            case "brightness":
                viewType = 2;
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

    private void setAnimation(View viewToAnimate, int position)
    {
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
