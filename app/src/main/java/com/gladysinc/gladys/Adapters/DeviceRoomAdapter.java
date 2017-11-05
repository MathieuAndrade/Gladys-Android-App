package com.gladysinc.gladys.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gladysinc.gladys.Models.Devicetype;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.AdapterCallback;

import java.util.List;

public class DeviceRoomAdapter extends RecyclerView.Adapter<DeviceRoomAdapter.ViewHolder>  {

    private List<Devicetype> results;
    private Context context;
    private AdapterCallback.AdapterCallbackDevicetype callback_devicetype;

    public DeviceRoomAdapter(List<Devicetype> devicetypeList, AdapterCallback.AdapterCallbackDevicetype callbackDevicetype) {
        this.results = devicetypeList;
        this.callback_devicetype = callbackDevicetype;
    }

    @Override
    public DeviceRoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_device_details, parent, false);
        return new DeviceRoomAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeviceRoomAdapter.ViewHolder holder, int position) {

        final Long id = results.get(position).getDevicetypeId();
        String device_id = context.getString(R.string.id)+ " " + results.get(position).getDevicetypeId();
        String type = results.get(position).getType();
        String device_type = context.getString(R.string.type) + " " + type;
        String device_type_null = context.getString(R.string.type) + " " + context.getString(R.string.empty);
        String category = results.get(position).getCategory();
        String device_category = context.getString(R.string.category) + " " + category;
        String device_category_null = context.getString(R.string.category) + " " + context.getString(R.string.empty);
        String tag = results.get(position).getTag();
        String device_tag = context.getString(R.string.tag) + " " + tag;
        String device_tag_null = context.getString(R.string.tag) + " " + context.getString(R.string.empty);
        Float last_value = results.get(position).getLastValue();
        String device_last_value = context.getString(R.string.last_value) + " " + last_value;

        holder.device_name.setText(results.get(position).getDevicetypeName());
        holder.device_id.setText(device_id);

        if(results.get(position).getCategory() != null){
            holder.device_logo.setImageResource(getLogo(results.get(position).getCategory()));
        }

        if(type != null){
            holder.device_type.setText(device_type);
        } else {holder.device_type.setText(device_type_null);}

        if(category != null){
            holder.device_category.setText(device_category);
        } else {holder.device_category.setText(device_category_null);}

        if(tag != null){
            holder.device_tag.setText(device_tag);
        } else {holder.device_tag.setText(device_tag_null);}

        holder.device_last_value.setText(device_last_value);

        holder.device_active.setOnCheckedChangeListener(null);

        if(results.get(position).getDisplay() == 1){
            holder.device_active.setChecked(true);
        } else {holder.device_active.setChecked(false);}

        holder.device_active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                callback_devicetype.onClickCallbackDevicetype(id, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView device_name, device_id, device_type, device_category, device_tag, device_last_value;
        private ImageView device_logo;
        private CheckBox device_active;
        ViewHolder(View view) {
            super(view);

            device_name = view.findViewById(R.id.device_name);
            device_id = view.findViewById(R.id.device_id);
            device_tag = view.findViewById(R.id.device_tag);
            device_type = view.findViewById(R.id.device_type);
            device_category = view.findViewById(R.id.device_category);
            device_last_value = view.findViewById(R.id.device_last_value);
            device_logo = view.findViewById(R.id.device_logo);
            device_active = view.findViewById(R.id.device_active);
        }
    }

    private int getLogo(String tag){

        int imageView = 0;

        switch (tag){
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
