package com.gladysinc.gladys.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;
import com.gladysinc.gladys.Models.Event;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.DateTimeUtils;
import com.gladysinc.gladys.Utils.VectorDrawableUtils;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> results;
    private Context context;

    public EventAdapter(List<Event> eventList){this.results = eventList;}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = View.inflate(parent.getContext(), R.layout.card_timeline, null);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){

        holder.event_view.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_event, R.color.colorAccent));
        holder.event_title.setText(results.get(position).getName());
        holder.event_date.setText(DateTimeUtils.getRelativeTimeSpan(results.get(position).getDatetime()));

        switch (results.get(position).getCode()) {
            case "back-at-home":
                holder.event_logo.setImageResource(R.drawable.ic_home);
                holder.event_title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case "left-home":
                holder.event_logo.setImageResource(R.drawable.ic_home);
                holder.event_title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case "left-area":
                holder.event_logo.setImageResource(R.drawable.ic_location_zone);
                holder.event_title.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.event_view.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_event2, R.color.colorPrimary));
                break;
            case "enter-area":
                holder.event_logo.setImageResource(R.drawable.ic_location_zone);
                holder.event_title.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.event_view.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_event2, R.color.colorPrimary));
                break;
            case "alarm":
                holder.event_logo.setImageResource(R.drawable.ic_alarm);
                holder.event_title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case "going-to-sleep":
                holder.event_logo.setImageResource(R.drawable.ic_bed_blue);
                holder.event_title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case "wake-up":
                holder.event_logo.setImageResource(R.drawable.ic_bed_green);
                holder.event_title.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.event_view.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_event2, R.color.colorPrimary));
                break;
            case "gladys-installed":
                holder.event_logo.setImageResource(R.drawable.ic_gladys_install);
                holder.event_title.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.event_view.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_event2, R.color.colorPrimary));
                break;
            case "gladys-updated":
                holder.event_logo.setImageResource(R.drawable.ic_gladys_update);
                holder.event_title.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.event_view.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_event2, R.color.colorPrimary));
                break;
            case "sunrise":
                holder.event_logo.setImageResource(R.drawable.ic_sun);
                holder.event_title.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.event_view.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_event2, R.color.colorPrimary));
                break;
            case "sunset":
                holder.event_logo.setImageResource(R.drawable.ic_sun);
                holder.event_title.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.event_view.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_event2, R.color.colorPrimary));
                break;
            case "devicetype-new-value":
                holder.event_logo.setImageResource(R.drawable.ic_new_devicetype_value);
                holder.event_title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case "user-seen-at-home":
                holder.event_logo.setImageResource(R.drawable.ic_home);
                holder.event_title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case "house-mode-changed":
                holder.event_logo.setImageResource(R.drawable.ic_home);
                holder.event_title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TimelineView event_view;
        private TextView event_title, event_date;
        private ImageView event_logo;

        ViewHolder(View itemView, int viewType) {
            super(itemView);

            event_title = itemView.findViewById(R.id.event_name);
            event_date = itemView.findViewById(R.id.event_date);
            event_logo = itemView.findViewById(R.id.event_logo);
            event_view = itemView.findViewById(R.id.time_marker);
            event_view.initLine(viewType);
        }
    }

}
