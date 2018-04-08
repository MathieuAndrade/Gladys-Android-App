package com.gladysinc.gladys.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.gladysinc.gladys.Adapters.DeviceRoomAdapter;
import com.gladysinc.gladys.Models.Devicetype;
import com.gladysinc.gladys.R;
import com.gladysinc.gladys.Utils.AdapterCallback;
import com.orm.SugarRecord;

import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;

public class DeviceActivity extends AppCompatActivity implements AdapterCallback.AdapterCallbackDevicetype {

    private RecyclerView recycler_view;

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
        Toolbar toolbar = findViewById(R.id.toolbar_device);
        setSupportActionBar(toolbar);

        recycler_view = findViewById(R.id.device_rv);
        recycler_view.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view.setLayoutManager(layoutManager);

        final FloatingActionButton fab_scroll_up = findViewById(R.id.fab_scroll_up);
        fab_scroll_up.setVisibility(View.VISIBLE);
        fab_scroll_up.animate().translationY(fab_scroll_up.getHeight() + 400).setInterpolator(new LinearInterpolator()).start();
        fab_scroll_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recycler_view.smoothScrollToPosition(0);
                fab_scroll_up.animate().translationY(fab_scroll_up.getHeight() + 400).setInterpolator(new LinearInterpolator()).start();
            }
        });
    }

    public void adapterView(List<Devicetype> data){

        DeviceRoomAdapter adapter = new DeviceRoomAdapter(data, this);
        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
        recycler_view.setAdapter(new SlideInLeftAnimationAdapter(alphaAdapter));
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
