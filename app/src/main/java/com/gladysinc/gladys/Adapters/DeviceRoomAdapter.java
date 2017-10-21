package com.gladysinc.gladys.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.gladysinc.gladys.Models.Devicetype;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.AdapterCallback;

import java.util.List;

public class DeviceRoomAdapter extends RecyclerView.Adapter<DeviceRoomAdapter.ViewHolder>  {

    private List<Devicetype> devicetypeList;
    private Context context;
    private int lastPosition = -1;
    private AdapterCallback.AdapterCallbackDevicetype callbackDevicetype;

    public DeviceRoomAdapter(List<Devicetype> android, AdapterCallback.AdapterCallbackDevicetype callbackDevicetype) {
        this.devicetypeList = android;
        this.callbackDevicetype = callbackDevicetype;
    }

    @Override
    public DeviceRoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_device_details, parent, false);
        return new DeviceRoomAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeviceRoomAdapter.ViewHolder holder, int position) {

        final Long id = devicetypeList.get(position).getDevicetypeId();
        String type = devicetypeList.get(position).getType();
        String category = devicetypeList.get(position).getCategory();
        String tag = devicetypeList.get(position).getTag();
        Float last_value = devicetypeList.get(position).getLastValue();

        holder.device_name.setText(devicetypeList.get(position).getDevicetypeName());
        holder.device_id.setText(context.getString(R.string.id)+ " " + devicetypeList.get(position).getDevicetypeId());

        if(devicetypeList.get(position).getCategory() != null){
            holder.logo.setImageResource(getLogo(devicetypeList.get(position).getCategory()));
        }

        if(type != null){
            holder.device_type.setText(context.getString(R.string.type) + " " + type);
        } else {holder.device_type.setText(context.getString(R.string.type) + " " + context.getString(R.string.empty));}

        if(category != null){
            holder.device_category.setText(context.getString(R.string.category) + " " + category);
        } else {holder.device_category.setText(context.getString(R.string.category) + " " + context.getString(R.string.empty));}

        if(tag != null){
            holder.device_tag.setText(context.getString(R.string.tag) + " " + tag);
        } else {holder.device_tag.setText(context.getString(R.string.tag) + " " + context.getString(R.string.empty));}

        holder.device_last_value.setText(context.getString(R.string.last_value) + " " + last_value);

        holder.active.setOnCheckedChangeListener(null);

        if(devicetypeList.get(position).getDisplay() == 1){
            holder.active.setChecked(true);
        } else {holder.active.setChecked(false);}

        holder.active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                callbackDevicetype.onClickCallbackDevicetype(id, isChecked);
            }
        });

        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return devicetypeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView device_name, device_id, device_type, device_category, device_tag, device_last_value;
        private ImageView logo;
        private CheckBox active;
        ViewHolder(View view) {
            super(view);

            device_name = (TextView) view.findViewById(R.id.device_name);
            device_id = (TextView) view.findViewById(R.id.device_id);
            device_tag = (TextView) view.findViewById(R.id.device_tag);
            device_type = (TextView) view.findViewById(R.id.device_type);
            device_category =(TextView) view.findViewById(R.id.device_category);
            device_last_value = (TextView) view.findViewById(R.id.device_last_value);
            logo = (ImageView) view.findViewById(R.id.logo);
            active = (CheckBox) view.findViewById(R.id.active);
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
