package com.gladysinc.gladys.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private int lastPosition = -1;

    public EventAdapter(List<Event> android){this.results = android;}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = View.inflate(parent.getContext(), R.layout.card_timeline, null);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){

        holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_event, R.color.colorAccent));
        holder.timeline_title.setText(results.get(position).getName());
        holder.timeline_date.setText(DateTimeUtils.getRelativeTimeSpan(results.get(position).getDatetime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        switch (results.get(position).getCode()) {
            case "back-at-home":
                holder.imageCode.setImageResource(R.drawable.ic_home);
                holder.timeline_title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case "left-home":
                holder.imageCode.setImageResource(R.drawable.ic_home);
                holder.timeline_title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case "left-area":
                holder.imageCode.setImageResource(R.drawable.ic_location_zone);
                holder.timeline_title.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_event2, R.color.colorPrimary));
                break;
            case "enter-area":
                holder.imageCode.setImageResource(R.drawable.ic_location_zone);
                holder.timeline_title.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_event2, R.color.colorPrimary));
                break;
            case "alarm":
                holder.imageCode.setImageResource(R.drawable.ic_alarm);
                holder.timeline_title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case "going-to-sleep":
                holder.imageCode.setImageResource(R.drawable.ic_bed_blue);
                holder.timeline_title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case "wake-up":
                holder.imageCode.setImageResource(R.drawable.ic_bed_green);
                holder.timeline_title.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_event2, R.color.colorPrimary));
                break;
            case "gladys-installed":
                holder.imageCode.setImageResource(R.drawable.ic_gladys_install);
                holder.timeline_title.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_event2, R.color.colorPrimary));
                break;
            case "gladys-updated":
                holder.imageCode.setImageResource(R.drawable.ic_gladys_maj);
                holder.timeline_title.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_event2, R.color.colorPrimary));
                break;
            case "sunrise":
                holder.imageCode.setImageResource(R.drawable.ic_sun);
                holder.timeline_title.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_event2, R.color.colorPrimary));
                break;
            case "sunset":
                holder.imageCode.setImageResource(R.drawable.ic_sun);
                holder.timeline_title.setTextColor(context.getResources().getColor(R.color.colorAccent));
                holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(context, R.drawable.ic_marker_event2, R.color.colorPrimary));
                break;
            case "devicetype-new-value":
                holder.imageCode.setImageResource(R.drawable.ic_new_devicetype_value);
                holder.timeline_title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case "user-seen-at-home":
                holder.imageCode.setImageResource(R.drawable.ic_home);
                holder.timeline_title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case "house-mode-changed":
                holder.imageCode.setImageResource(R.drawable.ic_home);
                holder.timeline_title.setTextColor(context.getResources().getColor(R.color.colorPrimary));
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
        TimelineView mTimelineView;
        private TextView timeline_title, timeline_date;
        private ImageView imageCode;

        ViewHolder(View itemView, int viewType) {
            super(itemView);

            timeline_title = (TextView) itemView.findViewById(R.id.timeline_title);
            timeline_date = (TextView) itemView.findViewById(R.id.timeline_date);
            imageCode = (ImageView) itemView.findViewById(R.id.imageView2);
            mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
            mTimelineView.initLine(viewType);
        }
    }

}
