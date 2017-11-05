package com.gladysinc.gladys.Adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gladysinc.gladys.Models.Alarm;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.AdapterCallback;
import com.gladysinc.gladys.Utils.DateTimeUtils;

import java.util.List;
import java.util.Objects;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private List<Alarm> results;
    private AdapterCallback.AdapterCallbackAlarm adapter;

    public AlarmAdapter(List<Alarm> alarmList, AdapterCallback.AdapterCallbackAlarm adapterCallback){
        this.results = alarmList;
        adapter = adapterCallback;
    }

    @Override
    public AlarmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Context context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_alarm, parent, false);
        return new AlarmAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlarmAdapter.ViewHolder holder, final int position) {

        final Long id = results.get(position).getAlarm_id();

        holder.alarm_name.setText(results.get(position).getName());

        if (!Objects.equals(results.get(position).getDatetime(), null)){
            holder.alarm_moment.setText(DateTimeUtils.getRelativeTimeSpan(results.get(position).getDatetime()));
        }
        else if (!Objects.equals(results.get(position).getTime(), null)){
            String alarm_moment = DateTimeUtils.getDay(results.get(position).getDayofweek().toString()) + ", " + results.get(position).getTime();
            holder.alarm_moment.setText(alarm_moment);
        }
        else {
            holder.alarm_moment.setText(results.get(position).getCronrule());
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.onClickCallbackAlarm(id);
            }
        });

    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView alarm_name, alarm_moment;
        private ImageButton delete;
        ViewHolder(View view) {
            super(view);

            delete = view.findViewById(R.id.delete_alarm_button);
            alarm_name = view.findViewById(R.id.alarm_name);
            alarm_moment = view.findViewById(R.id.alarm_moment);

        }
    }
}
