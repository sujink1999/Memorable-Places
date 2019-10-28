package com.sujin.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

     static ArrayList<String> array = new ArrayList<String>();
     static ArrayList<LatLng> location = new ArrayList<LatLng>();
     static ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences share = this.getSharedPreferences("com.sujin.memorableplaces", Context.MODE_PRIVATE);
        ArrayList<String> lat = new ArrayList<String>();
        ArrayList<String> lon = new ArrayList<String>();

        try {
            array =(ArrayList<String>) ObjectSerializer.deserialize( share.getString("places",ObjectSerializer.serialize(new ArrayList<String>())));
            lat =(ArrayList<String>) ObjectSerializer.deserialize( share.getString("lat",ObjectSerializer.serialize(new ArrayList<String>())));
            lon =(ArrayList<String>) ObjectSerializer.deserialize( share.getString("lon",ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(array.size()>0 && lat.size()>0 && lon.size()>0)
        {
            if (array.size() == lat.size() && array.size() == lon.size())
            {
                for(int i =0 ; i< array.size(); i++)
                {
                    location.add(new LatLng(Double.parseDouble(lat.get(i)),Double.parseDouble(lon.get(i))));
                }
            }
        }
        else
        {
            array.add("Add a new place");
            location.add(new LatLng(0,0));

        }



        ListView list = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                    intent.putExtra("position", position);
                    startActivity(intent);

            }
        });
    }
}
