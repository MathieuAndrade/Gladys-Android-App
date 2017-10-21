package com.gladysinc.gladys.Adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gladysinc.gladys.Models.Tchat;
import com.gladysinc.gladys.R;

import java.util.List;

public class TchatAdapter  extends RecyclerView.Adapter<TchatAdapter.ViewHolder> {

    private List<Tchat> results;

    public TchatAdapter(List<Tchat> android) {
        this.results = android;
    }

    @Override
    public TchatAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_tchat, viewGroup, false);
        return new TchatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TchatAdapter.ViewHolder viewHolder, int i) {
        viewHolder.text_response.setText(results.get(i).getResponse().getText());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView text_response;
        public ViewHolder(View view) {
            super(view);

            text_response = (TextView)view.findViewById(R.id.textmessage);
        }
    }
}
