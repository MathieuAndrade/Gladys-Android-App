package com.gladysinc.gladys.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gladysinc.gladys.Models.BrainSentences;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.AdapterCallback;

import java.util.List;

public class BrainSentencesAdapter extends RecyclerView.Adapter<BrainSentencesAdapter.ViewHolder> {

    private List<BrainSentences> results;
    private Context context;
    private AdapterCallback.AdapterCallbackBrainSentences callback_brain_sentences;

    public BrainSentencesAdapter(List<BrainSentences> brainSentences, AdapterCallback.AdapterCallbackBrainSentences callbackBrainSentences){
        this.results = brainSentences;
        this.callback_brain_sentences = callbackBrainSentences;
    }

    @Override
    public BrainSentencesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_brain_sentences, parent, false);
        return new BrainSentencesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BrainSentencesAdapter.ViewHolder holder, int position) {

        final Long id = results.get(position).getSentences_id();
        final String callback_label = results.get(position).getLabel();
        String label = context.getString(R.string.label) + " " + results.get(position).getLabel();
        final String callback_status = results.get(position).getStatue();
        String status = context.getString(R.string.statue) + " " + results.get(position).getStatue();

        holder.text.setText(results.get(position).getText());
        holder.label.setText(label);
        holder.status.setText(status);

        holder.more_button.setOnClickListener(null);
        holder.more_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback_brain_sentences.onPopupMenuClickBrainSentences(holder.more_button, holder.getAdapterPosition(), id, callback_status, callback_label);
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView text, status, label;
        private ImageButton more_button;
        ViewHolder(View view){
            super(view);

            text = view.findViewById(R.id.sentence_text);
            status = view.findViewById(R.id.sentence_status);
            label = view.findViewById(R.id.sentence_label);
            more_button = view.findViewById(R.id.more_button);

        }
    }
}
