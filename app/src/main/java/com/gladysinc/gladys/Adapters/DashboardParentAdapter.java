package com.gladysinc.gladys.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gladysinc.gladys.Models.DevicetypeByRoom;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.AdapterCallback;

import java.util.List;

public class DashboardParentAdapter extends RecyclerView.Adapter<DashboardParentAdapter.HeaderViewHolder>{

    private List<DevicetypeByRoom> devicetypeByRooms;
    private Context context;
    private AdapterCallback.AdapterCallbackDevicestate callback_devicestate;

    public DashboardParentAdapter(List<DevicetypeByRoom> devicetypeByRooms, AdapterCallback.AdapterCallbackDevicestate callback_devicestate) {
        this.devicetypeByRooms = devicetypeByRooms;
        this.callback_devicestate = callback_devicestate;
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_dashboard_device, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        holder.room.setText(devicetypeByRooms.get(position).getRoomName());

        if(devicetypeByRooms.get(position).getDeviceTypes().size() == 0){
            holder.dashboardCard.setLayoutParams(new CardView.LayoutParams(0, 0));
        }else{
            holder.recyclerView.setAdapter(new DashboardChildAdapter(devicetypeByRooms.get(position).getDeviceTypes(), callback_devicestate));
        }
    }

    @Override
    public int getItemCount() {
        return devicetypeByRooms.size();
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView room;
        private final RecyclerView recyclerView;
        private final CardView dashboardCard;

        HeaderViewHolder(View view) {
            super(view);

            room = view.findViewById(R.id.device_room_name);
            recyclerView = view.findViewById(R.id.rv_card_dashboard_device);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            dashboardCard = view.findViewById(R.id.dashboard_card);
        }
    }
}
