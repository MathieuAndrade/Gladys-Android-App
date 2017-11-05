package com.gladysinc.gladys.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private List<Devicetype> results;
    private Context context;
    private AdapterCallback.AdapterCallbackDevicestate callback_devicestate;

    public DevicetypeByRoomAdapter(List<Devicetype> devicetypeByRoomList, AdapterCallback.AdapterCallbackDevicestate callbackDevicestate){
        this.results = devicetypeByRoomList;
        this.callback_devicestate = callbackDevicestate;
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
        final Long id = results.get(position).getDevicetypeId();


        switch (itemType) {
            case 1:
                ((BinaryViewHolder) holder).device_binary_name.setText(results.get(position).getDevicetypeName());
                ((BinaryViewHolder) holder).device_binary_room.setText(results.get(position).getRoomName());

                ((BinaryViewHolder) holder).device_binary_value.setOnCheckedChangeListener(null);

                if (results.get(position).getLastValue() == null){
                    ((BinaryViewHolder) holder).device_binary_value.setChecked(false);
                }else if (results.get(position).getLastValue() == 0) {
                    ((BinaryViewHolder) holder).device_binary_value.setChecked(false);
                } else {
                    ((BinaryViewHolder) holder).device_binary_value.setChecked(true);
                }

                ((BinaryViewHolder) holder).device_binary_value.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

                if(results.get(position).getCategory() != null) {
                    ((BinaryViewHolder) holder).device_binary_logo.setImageResource(getLogo(results.get(position).getCategory()));
                }

                break;

            case 2:

                String multilevelValueNull = context.getString(R.string.value) + " " + context.getString(R.string.empty);

                ((MultilevelViewHolder) holder).device_multilevel_name.setText(results.get(position).getDevicetypeName());
                ((MultilevelViewHolder) holder).device_multilevel_room.setText(results.get(position).getRoomName());

                if (results.get(position).getLastValue() == null) {
                    ((MultilevelViewHolder) holder).device_multilevel_value_text.setText(multilevelValueNull);
                } else {
                    String multilevelValue = context.getString(R.string.value) + " " + results.get(position).getLastValue().toString();
                    ((MultilevelViewHolder) holder).device_multilevel_value_text.setText(multilevelValue);
                }


                ((MultilevelViewHolder) holder).device_multilevel_value.setOnSeekBarChangeListener(null);
                ((MultilevelViewHolder) holder).device_multilevel_value.setMax(results.get(position).getMax());

                if(results.get(position).getLastValue() == null){
                    ((MultilevelViewHolder) holder).device_multilevel_value.setProgress(0);
                } else {
                    ((MultilevelViewHolder) holder).device_multilevel_value.setProgress(results.get(position).getLastValue().intValue());
                }
                ((MultilevelViewHolder) holder).device_multilevel_value.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        String progressValue = context.getString(R.string.value) + " " + progress;
                        ((MultilevelViewHolder) holder).device_multilevel_value_text.setText(progressValue);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        callback_devicestate.onClickCallbackDevicestate(id, (float) seekBar.getProgress());
                    }
                });

                if(results.get(position).getCategory() != null) {
                    ((MultilevelViewHolder) holder).device_multilevel_logo.setImageResource(getLogo(results.get(position).getCategory()));
                }

                break;

            default:

                ((SensorViewHolder) holder).device_sensor_name.setText(results.get(position).getDevicetypeName());
                ((SensorViewHolder) holder).device_sensor_room.setText(results.get(position).getRoomName());

                if(results.get(position).getLastValue() == null) {
                    ((SensorViewHolder) holder).device_sensor_value.setText("0");
                }else {
                    String sensorValue = results.get(position).getLastValue().toString() + " " + results.get(position).getUnit();
                    ((SensorViewHolder) holder).device_sensor_value.setText(sensorValue);
                }

                if(results.get(position).getCategory() != null) {
                    ((SensorViewHolder) holder).device_sensor_logo.setImageResource(getLogo(results.get(position).getCategory()));
                }

                break;
        }

    }

    private class BinaryViewHolder extends RecyclerView.ViewHolder{
        private TextView device_binary_name, device_binary_room;
        private ImageView device_binary_logo;
        private Switch device_binary_value;
        BinaryViewHolder(View view) {
            super(view);

            device_binary_logo = view.findViewById(R.id.device_binary_logo);
            device_binary_name = view.findViewById(R.id.device_binary_name);
            device_binary_room = view.findViewById(R.id.device_binary_room);
            device_binary_value = view.findViewById(R.id.device_binary_value);
        }
    }

    private class MultilevelViewHolder extends RecyclerView.ViewHolder{
        private TextView device_multilevel_name, device_multilevel_room, device_multilevel_value_text;
        private ImageView device_multilevel_logo;
        private SeekBar device_multilevel_value;
        MultilevelViewHolder(View view) {
            super(view);

            device_multilevel_logo = view.findViewById(R.id.device_multilevel_logo);
            device_multilevel_name = view.findViewById(R.id.device_multilevel_name);
            device_multilevel_room = view.findViewById(R.id.device_multilevel_room);
            device_multilevel_value_text = view.findViewById(R.id.device_multilevel_velue_text);
            device_multilevel_value = view.findViewById(R.id.device_multilevel_value);
        }
    }

    private class SensorViewHolder extends RecyclerView.ViewHolder{
        private TextView device_sensor_name, device_sensor_room, device_sensor_value;
        private ImageView device_sensor_logo;
        SensorViewHolder(View view) {
            super(view);

            device_sensor_logo = view.findViewById(R.id.device_sensor_logo);
            device_sensor_name = view.findViewById(R.id.device_sensor_name);
            device_sensor_room = view.findViewById(R.id.device_sensor_room);
            device_sensor_value = view.findViewById(R.id.device_sensor_value);
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;

        switch (results.get(position).getType()) {
            case "binary":
                viewType = 1;
                break;

            case "multilevel":
                if(results.get(position).getSensor() == 1){
                    viewType = 3;
                }else {viewType = 2;}
                break;

            case "byte":
                if(results.get(position).getSensor() == 1){
                    viewType = 3;
                }else {viewType = 2;}
                break;

            case "brightness":
                if(results.get(position).getSensor() == 1){
                    viewType = 3;
                }else {viewType = 2;}
                break;

            case "saturation":
                if(results.get(position).getSensor() == 1){
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
