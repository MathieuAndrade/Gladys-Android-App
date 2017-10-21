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

import com.gladysinc.gladys.Models.Alarm;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.AdapterCallback;
import com.gladysinc.gladys.Utils.DateTimeUtils;

import java.util.List;
import java.util.Objects;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private List<Alarm> results;
    private Context context;
    private int lastPosition = -1;
    private AdapterCallback.AdapterCallbackAlarm adapter;

    public AlarmAdapter(List<Alarm> android, AdapterCallback.AdapterCallbackAlarm adapterCallback){
        this.results = android;
        adapter = adapterCallback;
    }

    @Override
    public AlarmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_alarm, parent, false);
        return new AlarmAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlarmAdapter.ViewHolder holder, final int position) {

        final Long id = results.get(position).getAlarm_id();

        holder.alarm_name.setText(results.get(position).getName());

        if (!Objects.equals(results.get(position).getDatetime(), null)){
            holder.alarm_moment.setText(DateTimeUtils.getRelativeTimeSpan(results.get(position).getDatetime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        }
        else if (!Objects.equals(results.get(position).getTime(), null)){
            holder.alarm_moment.setText(DateTimeUtils.getDay(results.get(position).getDayofweek().toString()) + ", " + results.get(position).getTime());
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

        setAnimation(holder.itemView, position);

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

            delete = (ImageButton) view.findViewById(R.id.deleteButton);
            alarm_name = (TextView) view.findViewById(R.id.alarmname);
            alarm_moment = (TextView) view.findViewById(R.id.alarmmoment);

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
