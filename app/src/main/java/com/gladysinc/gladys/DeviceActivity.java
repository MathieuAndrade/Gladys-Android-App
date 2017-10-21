package com.gladysinc.gladys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.gladysinc.gladys.Adapters.DeviceRoomAdapter;
import com.gladysinc.gladys.Models.Devicetype;
import com.gladysinc.gladys.Utils.AdapterCallback;
import com.orm.SugarRecord;

import java.util.List;

public class DeviceActivity extends AppCompatActivity implements AdapterCallback.AdapterCallbackDevicetype {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        initialdeclarations();

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Long id;
        String room_name;

        Intent intent = getIntent();
        id = intent.getLongExtra("room_id", 0);
        room_name = intent.getExtras().getString("room_name", "");

        List<Devicetype> devicetypes = SugarRecord.find(Devicetype.class, "room_id=?", id.toString());
        adapterView(devicetypes);

        getSupportActionBar().setTitle(room_name);
    }

    public void initialdeclarations(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_device);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.rv_device);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    public void adapterView(List<Devicetype> data){

        DeviceRoomAdapter adapter = new DeviceRoomAdapter(data, this);
        recyclerView.setAdapter(adapter);
    }

    public void onClickCallbackDevicetype(Long id, boolean active){

        Long display = (active) ? 1L : 0L;
        Devicetype devicetype = (SugarRecord.find(Devicetype.class, "devicetype_id=?", id.toString())).get(0);
        devicetype.setDisplay(display);
        SugarRecord.save(devicetype);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
