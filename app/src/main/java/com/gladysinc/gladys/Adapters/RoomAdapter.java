package com.gladysinc.gladys.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gladysinc.gladys.Models.DevicetypeByRoom;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.AdapterCallback;

import java.util.List;


public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private List<DevicetypeByRoom> results;
    private Context context;
    private AdapterCallback.AdapterCallbackRoom callback_room;

    public RoomAdapter(List<DevicetypeByRoom> roomList, AdapterCallback.AdapterCallbackRoom callbackRoom) {
        this.results = roomList;
        this.callback_room = callbackRoom;
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
        String house = context.getString(R.string.house) + " " + results.get(position).getHouse();

        holder.room_name.setText(results.get(position).getRoomName());
        holder.room_house.setText(house);

        holder.device_butoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback_room.onClickCallbackRoom(id, roomName);
            }
        });

    }

    @Override
    public int getItemCount() {
            return results.size();
        }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView room_name,room_house;
        private ImageButton device_butoon;
        ViewHolder(View view) {
            super(view);

            device_butoon = view.findViewById(R.id.device_button);
            room_name = view.findViewById(R.id.room_name);
            room_house = view.findViewById(R.id.house_id);
        }
    }

}