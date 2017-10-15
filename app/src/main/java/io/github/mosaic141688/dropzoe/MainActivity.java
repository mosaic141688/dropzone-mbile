package io.github.mosaic141688.dropzoe;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private Location location;
    private LocationManager locationManager;
    private String provider;
    private FirebaseRecyclerAdapter adapter;
    private List<String> enabled = new ArrayList<>();


    private static final int MY_PERMISSIONS_REQUEST_LOCATION=0 ;


    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
        else {
            LocationProvider provider = ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).getProvider(LocationManager.GPS_PROVIDER);
            location = ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // Log.e("LAST KNOW","Lat:"+location.getLatitude()+" LnG"+location.getLongitude());
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, MainActivity.this);


    }

    @Override
    public void onProviderEnabled(String provider) {

    }


    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.stopListening();

    }

    @Override
    public void onLocationChanged(Location location) {
        this.location=location;
        Log.e("UPDATED","Lat:"+location.getLatitude()+" LnG"+location.getLongitude());
        //Toast.makeText(this,"Lat:"+this.location.getLatitude()+" Lng:"+this.location.getLongitude(),Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        final boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("drops");

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);


       // myRef.setValue("Hello, World!");

        RecyclerView rv = (RecyclerView) findViewById(R.id.flyers);
        rv.setLayoutManager(mLayoutManager);

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //DatabaseReference drop = myRef.push();
                if(location==null){
                    builder.setTitle("Please try Again Latter, Location is Not Available");
                    AlertDialog  dialog = builder.create();
                    dialog.show();
                }else {
                    String list = "";
                    for(String s:MainActivity.this.enabled){
                        DatabaseReference drop = myRef.push();
                        drop.setValue(new Drop(s,location.getLatitude(),location.getLongitude(),location.getTime()));
                        list+="\n"+s;
                    }

                    builder.setTitle("Droped"+list);
                    AlertDialog  dialog = builder.create();
                    dialog.show();
                }



            }
        });



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

       // LocationProvider provider = ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).getProvider(LocationManager.GPS_PROVIDER);
       // location = ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(location!=null){
            Log.e("LAST KNOW","Lat:"+location.getLatitude()+" LnG"+location.getLongitude());
        }

        else {
            Log.e("LOCATION","NOT AVAIABLE");
        }


        final Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("flyers");


        FirebaseRecyclerOptions<Flyer> options =
                new FirebaseRecyclerOptions.Builder<Flyer>()
                        .setQuery(query, Flyer.class)
                        .build();

        class FlyerHolder extends RecyclerView.ViewHolder{
            public TextView nameTv;
            public Switch enabledSW;
            public FlyerHolder(View itemView){
                super(itemView);
                nameTv = (TextView) itemView.findViewById(R.id.name);
                enabledSW = (Switch)itemView.findViewById(R.id.enable);
            }
        }

        adapter = new FirebaseRecyclerAdapter<Flyer, FlyerHolder>(options) {
            @Override
            public FlyerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.flyer_item, parent, false);

                return new FlyerHolder(view);
            }

            @Override
            protected void onBindViewHolder(FlyerHolder holder, final int position, final Flyer model) {
                // Bind the Chat object to the ChatHolder
                // ...
                if(position==0){
                    MainActivity.this.enabled.clear();
                }
                if(model.isEnabled()){
                    MainActivity.this.enabled.add(adapter.getRef(position).getKey());
                }
                holder.nameTv.setText(model.getName());
                holder.enabledSW.setChecked(model.isEnabled());
                holder.enabledSW.setEnabled(model.isLocked());

                holder.enabledSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                       // model.setEnabled(!model.isEnabled());
                        adapter.getRef(position).child("enabled").setValue(isChecked);
                    }
                });
            }


        };

        rv.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


