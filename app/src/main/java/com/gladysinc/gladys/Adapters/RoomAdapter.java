package com.gladysinc.gladys.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gladysinc.gladys.Models.DevicetypeByRoom;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.AdapterCallback;

import java.util.List;


public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private List<DevicetypeByRoom> results;
    private Context context;
    private int lastPosition = -1;
    private AdapterCallback.AdapterCallbackRoom callbackRoom;

    public RoomAdapter(List<DevicetypeByRoom> android, AdapterCallback.AdapterCallbackRoom callbackRoom) {
        this.results = android;
        this.callbackRoom = callbackRoom;
        }

    @Override
    public RoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoomAdapter.ViewHolder holder, int position) {

        final Long id = results.get(position).getRommId();
        final String roomName = results.get(position).getRoomName();

        holder.room_name.setText(results.get(position).getRoomName());
        holder.room_house.setText(context.getString(R.string.house) + " " + results.get(position).getHouse());

        holder.deviceButoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbackRoom.onClickCallbackRoom(id, roomName);
            }
        });

        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
            return results.size();
        }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView room_name,room_house;
        private ImageButton deviceButoon;
        ViewHolder(View view) {
            super(view);

            deviceButoon = (ImageButton)view.findViewById(R.id.device_button);
            room_name = (TextView)view.findViewById(R.id.Room_name);
            room_house = (TextView)view.findViewById(R.id.house_id);
        }
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