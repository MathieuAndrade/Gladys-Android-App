package com.gladysinc.gladys.Models;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Table;

@Table
public class BrainSentences {

    @SerializedName("id")
    private Long sentences_id;

    private String text;
    private String label;
    private String status;
    private String service;

    public BrainSentences(){}

    public BrainSentences(Long sentences_id, String text, String label, String status, String service){
        this.sentences_id = sentences_id;
        this.text = text;
        this.label = label;
        this.status = status;
        this.service = service;
    }

    public Long getSentences_id() {
        return sentences_id;
    }

    public void setSentences_id(Long sentences_id) {
        this.sentences_id = sentences_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStatue() {
        return status;
    }

    public void setStatue(String statue) {
        this.status = statue;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}

