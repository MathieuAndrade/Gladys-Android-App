package com.gladysinc.gladys.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
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

public class DashboardChildAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Devicetype> devicetypes;
    private Context context;
    private AdapterCallback.AdapterCallbackDevicestate callback_devicestate;

    DashboardChildAdapter(List<Devicetype> devicetypes, AdapterCallback.AdapterCallbackDevicestate callbackDevicestate){
        this.devicetypes = devicetypes;
        this.callback_devicestate = callbackDevicestate;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {

            case 1:
                viewHolder = new DashboardChildAdapter.BinaryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_device_binary, parent, false));
                break;

            case 2:
                viewHolder = new DashboardChildAdapter.MultilevelViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_device_multilevel, parent, false));
                break;

            default:
                viewHolder = new DashboardChildAdapter.SensorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_device_sensor, parent, false));
                break;
        }

        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        final int itemType = getItemViewType(position);
        final long id = devicetypes.get(position).getDevicetypeId();

        switch (itemType) {
            case 1:
                ((BinaryViewHolder) holder).device_binary_name.setText(devicetypes.get(position).getDevicetypeName());
                ((BinaryViewHolder) holder).device_binary_tag.setText(devicetypes.get(position).getTag());

                ((BinaryViewHolder) holder).device_binary_value.setOnCheckedChangeListener(null);

                if (devicetypes.get(position).getLastValue() == null){
                    ((BinaryViewHolder) holder).device_binary_value.setChecked(false);
                }else if (devicetypes.get(position).getLastValue() == 0) {
                    ((BinaryViewHolder) holder).device_binary_value.setChecked(false);
                } else {
                    ((BinaryViewHolder) holder).device_binary_value.setChecked(true);
                }

                ((BinaryViewHolder) holder).device_binary_value.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            callback_devicestate.onClickCallbackDevicestate(id, 1f);
                        }else {
                            callback_devicestate.onClickCallbackDevicestate(id, 0f);
                        }
                    }
                });

                if(devicetypes.get(position).getCategory() != null) {
                    ((BinaryViewHolder) holder).device_binary_logo.setImageResource(getLogo(devicetypes.get(position).getCategory()));
                }

                break;

            case 2:
                ((MultilevelViewHolder) holder).device_multilevel_name.setText(devicetypes.get(position).getDevicetypeName());
                ((MultilevelViewHolder) holder).device_multilevel_tag.setText(devicetypes.get(position).getTag());

                if (devicetypes.get(position).getLastValue() == null) {
                    ((MultilevelViewHolder) holder).device_multilevel_value_text.setText(context.getString(R.string.value) + " " + context.getString(R.string.empty));
                } else {
                    ((MultilevelViewHolder) holder).device_multilevel_value_text.setText(context.getString(R.string.value) + " " + devicetypes.get(position).getLastValue().toString());
                }


                ((MultilevelViewHolder) holder).device_multilevel_value.setOnSeekBarChangeListener(null);
                ((MultilevelViewHolder) holder).device_multilevel_value.setMax(devicetypes.get(position).getMax());

                if(devicetypes.get(position).getLastValue() == null){
                    ((MultilevelViewHolder) holder).device_multilevel_value.setProgress(0);
                } else {
                    ((MultilevelViewHolder) holder).device_multilevel_value.setProgress(devicetypes.get(position).getLastValue().intValue());
                }
                ((MultilevelViewHolder) holder).device_multilevel_value.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        ((MultilevelViewHolder) holder).device_multilevel_value_text.setText(context.getString(R.string.value) + " " + progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        callback_devicestate.onClickCallbackDevicestate(id, (float) seekBar.getProgress());
                    }
                });

                if(devicetypes.get(position).getCategory() != null) {
                    ((MultilevelViewHolder) holder).device_multilevel_logo.setImageResource(getLogo(devicetypes.get(position).getCategory()));
                }

                break;

            default:

                ((SensorViewHolder) holder).device_sensor_name.setText(devicetypes.get(position).getDevicetypeName());
                ((SensorViewHolder) holder).device_sensor_tag.setText(devicetypes.get(position).getTag());

                String value = "0";
                String unit = "";

                if(devicetypes.get(position).getLastValue() != null) value = devicetypes.get(position).getLastValue().toString();
                if(devicetypes.get(position).getUnit() != null) unit = devicetypes.get(position).getUnit();

                String sensroValue = value + " " + unit;
                ((SensorViewHolder) holder).device_sensor_value.setText(sensroValue);

                if(devicetypes.get(position).getCategory() != null) {
                    ((SensorViewHolder) holder).device_sensor_logo.setImageResource(getLogo(devicetypes.get(position).getCategory()));
                }

                break;
        }

    }

    private class BinaryViewHolder extends RecyclerView.ViewHolder{
        private TextView device_binary_name, device_binary_tag;
        private ImageView device_binary_logo;
        private Switch device_binary_value;

        BinaryViewHolder(View view) {
            super(view);

            device_binary_logo = view.findViewById(R.id.device_binary_logo);
            device_binary_name = view.findViewById(R.id.device_binary_name);
            device_binary_tag = view.findViewById(R.id.device_binary_tag);
            device_binary_value = view.findViewById(R.id.device_binary_value);
        }
    }

    private class MultilevelViewHolder extends RecyclerView.ViewHolder{
        private TextView device_multilevel_name, device_multilevel_tag, device_multilevel_value_text;
        private ImageView device_multilevel_logo;
        private SeekBar device_multilevel_value;

        MultilevelViewHolder(View view) {
            super(view);

            device_multilevel_logo = view.findViewById(R.id.device_multilevel_logo);
            device_multilevel_name = view.findViewById(R.id.device_multilevel_name);
            device_multilevel_tag = view.findViewById(R.id.device_multilevel_tag);
            device_multilevel_value_text = view.findViewById(R.id.device_multilevel_value_text);
            device_multilevel_value = view.findViewById(R.id.device_multilevel_value);
        }
    }

    private class SensorViewHolder extends RecyclerView.ViewHolder{
        private TextView device_sensor_name, device_sensor_tag, device_sensor_value;
        private ImageView device_sensor_logo;

        SensorViewHolder(View view) {
            super(view);
            device_sensor_logo = view.findViewById(R.id.device_sensor_logo);
            device_sensor_name = view.findViewById(R.id.device_sensor_name);
            device_sensor_tag = view.findViewById(R.id.device_sensor_tag);
            device_sensor_value = view.findViewById(R.id.device_sensor_value);
        }
    }

    @Override
    public int getItemCount() {
        return devicetypes.size();
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;

        switch (devicetypes.get(position).getType()) {

            case "binary":
                if(devicetypes.get(position).getSensor() == 1){
                    viewType = 3;
                }else {viewType = 1;}
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
