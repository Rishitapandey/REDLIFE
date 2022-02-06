package com.example.redlife;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioRouting;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.core.GeoHash;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.lang.System.in;

public class UserMapsActivity extends FragmentActivity implements OnMapReadyCallback, RoutingListener {
    private GoogleMap mMap;
    TextView NameBtn,addressBtn,phonebtn;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};


    Location currentLocation;
    private FirebaseAuth mAuth;
    FusedLocationProviderClient fusedLocationClient;
    SupportMapFragment mapFragment;
    String bloodGroup;
    String customerId;
    Marker HospitalMarker;
    private DatabaseReference userRef;
    GeoFire geoFire,geoFire1,geoFire2,geoFire3,geoFire4,geoFire5;
    List<String> list,myList1, myList, myList2, myList3, myList4;
    Boolean Found,Found1,Found2,Found3,Found4;
    DatabaseReference database, databaseReference,databaseReference1,databaseReference2,databaseReference3,databaseReference4;
    Boolean HospitalFound=false,isFound=false;
    private String HospitalFoundId;
    private Button PressBtn,infoBtn;
    DatabaseReference HospitalRef;
    private LatLng UserLocation;
    String RetreiveName = null,RetreiveAddress = null;
    Long Phone;
    LatLng HospitalLatLng;
    long  Phonenumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_maps);
        Toast.makeText(this, "Showing your location", Toast.LENGTH_SHORT).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Intent intent = getIntent();
        polylines = new ArrayList<>();
        bloodGroup = intent.getStringExtra(FirstActivity.BLOOD);
        PressBtn=(Button)findViewById(R.id.Press);
        infoBtn=findViewById(R.id.InfoButton);
        NameBtn=findViewById(R.id.HospitalName);
        phonebtn=findViewById(R.id.PhoneNumber);
        addressBtn=findViewById(R.id.Address);


        customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
         database = FirebaseDatabase.getInstance().getReference().child("HospitalAvailable");
         databaseReference=FirebaseDatabase.getInstance().getReference().child("HospitalAvailable").child("Hospital1");
        databaseReference1=FirebaseDatabase.getInstance().getReference().child("HospitalAvailable").child("Hospital2");
        databaseReference2=FirebaseDatabase.getInstance().getReference().child("HospitalAvailable").child("Hospital3");
        databaseReference3=FirebaseDatabase.getInstance().getReference().child("HospitalAvailable").child("Hospital4");
        databaseReference4=FirebaseDatabase.getInstance().getReference().child("HospitalAvailable").child("Hospital5");
         String hospitalKey="";

        mAuth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //check permission
        if (ActivityCompat.checkSelfPermission(UserMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(UserMapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44

            );
        }
        PressBtn.setVisibility(View.VISIBLE);
        infoBtn.setVisibility(View.GONE);



        list=new ArrayList<String>();
        myList = new ArrayList<String>();
        myList1 = new ArrayList<String>();
        myList2 = new ArrayList<String>();
        myList3 = new ArrayList<String>();
        myList4 = new ArrayList<String>();
        myList.add("A+");
        myList.add("A-");
        myList.add("B+");
        myList.add("O+");
        Found=CompareBloodGroup(bloodGroup,myList);
        if(Found){
            list.add("Hospital1");
        }
        myList1.add("AB+");
        myList1.add("AB-");
        myList1.add("B+");
        myList1.add("O+");
        Found1=CompareBloodGroup(bloodGroup,myList1);
        if(Found1){
            list.add("Hospital2");
        }

        myList2.add("A+");
        myList2.add("O-");
        myList2.add("B+");
        myList2.add("O+");
        Found2=CompareBloodGroup(bloodGroup,myList2);
        if(Found2){
            list.add("Hospital3");
        }

        myList3.add("A+");
        myList3.add("A-");
        myList3.add("B+");
        myList3.add("O+");
        Found3=CompareBloodGroup(bloodGroup,myList3);
        if(Found3){
            list.add("Hospital4");
        }

        myList4.add("A+");
        myList4.add("AB+");
        myList4.add("B+");
        myList4.add("B-");
        Found4=CompareBloodGroup(bloodGroup,myList4);
        if(Found4){
            list.add("Hospital5");
        }
        Log.d("list", list.toString());
        LatLng hospital1Location=new LatLng(28.6383, 77.2386);
        LatLng hospitalLocation1=new LatLng(28.4855, 77.1049 );
        LatLng hospitalLocation2=new LatLng(28.7096, 77.1699);
        LatLng hospitalLocation3=new LatLng(28.5659, 77.2111);
        LatLng hospitalLocation4=new LatLng(28.5406, 77.2834);
        geoFire1=new GeoFire(databaseReference);
        geoFire1.setLocation(hospitalKey, new GeoLocation(28.6383, 77.2386), new GeoFire.CompletionListener() {
            @Override
           public void onComplete(String key, DatabaseError error) {
                Map<String, Object> updates = new HashMap<>();
               updates.put("Name","Lok Nayak Hospital");
                updates.put("Address","Jawaharlal Nehru Marg, Delhi Gate-110002");
                updates.put("Phone", 9953111111L);
                updates.put("Blood group available",myList);
                databaseReference.updateChildren(updates);

           }
        });
        geoFire2=new GeoFire(databaseReference1);
        geoFire2.setLocation(hospitalKey, new GeoLocation(28.6668, 77.2146), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("Name","ST Stephen's Hospital");
                updates.put("Address","tis hazari,Delhi-110054");
                updates.put("Phone", 9953564797L);
                updates.put("Blood group available",myList1);
                databaseReference1.updateChildren(updates);

            }
        });
        geoFire3=new GeoFire(databaseReference2);
        geoFire3.setLocation(hospitalKey, new GeoLocation(28.7096, 77.1699), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("Name","Fortis Hospital");
                updates.put("Address","Shalimar Bagh, Delhi,110088");
                updates.put("Phone", 9953482156L);
                updates.put("Blood group available",myList2);
                databaseReference2.updateChildren(updates);

            }
        });
        geoFire4=new GeoFire(databaseReference3);
        geoFire4.setLocation(hospitalKey, new GeoLocation(28.5659, 77.2111), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("Name","AIMMS Hospital");
                updates.put("Address","Gautam Nagar, Ansari Nagar East,110029");
                updates.put("Phone", 9953484556L);
                updates.put("Blood group available",myList3);
                databaseReference3.updateChildren(updates);

            }
        });
        geoFire5=new GeoFire(databaseReference4);
        geoFire5.setLocation(hospitalKey, new GeoLocation(28.5406, 77.2834), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("Name","Indraprashta apollo hospital");
                updates.put("Address","Mathura Rd, Sarita Vihar-110076");
                updates.put("Phone", 9953411116L);
                updates.put("Blood group available",myList4);
                databaseReference4.updateChildren(updates);

            }
        });

        PressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getClosestHospital();
                PressBtn.setVisibility(View.GONE);
                infoBtn.setVisibility(View.VISIBLE);




            }
        });
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(
                        UserMapsActivity.this,R.style.BottomSheetDialogTheme
                );
                View bottomsheetView= LayoutInflater.from(getApplicationContext()).
                        inflate(R.layout.layout_bottom_sheet,(LinearLayout)findViewById(R.id.bottom_sheet_container));
                NameBtn=bottomsheetView.findViewById(R.id.HospitalName);
                phonebtn=bottomsheetView.findViewById(R.id.PhoneNumber);
                addressBtn=bottomsheetView.findViewById(R.id.Address);
                NameBtn.setText(RetreiveName);
                String number = Long.toString(Phone);
                phonebtn.setText(number);
                addressBtn.setText(RetreiveAddress);

                bottomsheetView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(bottomsheetView);
                bottomSheetDialog.show();

            }
        });



    }

    private void getCurrentLocation() {
        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null) {
                    currentLocation = location;
                    mapFragment.getMapAsync(UserMapsActivity.this);
                    geoFire = new GeoFire(userRef);
                    geoFire.setLocation(customerId, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {

                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            GeoHash geoHash = new GeoHash(new GeoLocation(location.getLatitude(), location.getLongitude()));
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("BloodGroup", bloodGroup);
                            userRef.updateChildren(updates);
                            UserLocation=new LatLng(location.getLatitude(),location.getLongitude());


                        }
                    });


                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions Options = new MarkerOptions().position(latLng).title("i m here");
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        mMap.addMarker(Options);


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


    }

    public boolean CompareBloodGroup(String s, List<String> list) {
        for (String Blood : list) {
            if (Blood.equals(s)) {
                return true;

            }
        }
        return false;
    }
    int radius=1;

    public void getClosestHospital(){

        GeoFire geoFire6=new GeoFire(database);
        GeoQuery geoQuery=geoFire6.queryAtLocation(new GeoLocation(UserLocation.latitude,UserLocation.longitude),radius);

        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                for (String no:list) {
                    if (no.equals(key)) {
                        if (!HospitalFound) {
                            HospitalFound = true;
                            HospitalFoundId = key;





                        }
                    }
                }
                GettinghospitalLocation();
                GettingHospitalInfo();


                }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!HospitalFound){
                    radius=radius+1;
                    getClosestHospital();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void GettinghospitalLocation() {
        database.child(HospitalFoundId).child("l").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    List<Object>HospitalLocationMap=(List<Object>)dataSnapshot.getValue();
                    double LocationLat=0;
                    double LocationLang=0;
                    if(HospitalLocationMap.get(0)!=null){
                        LocationLat=Double.parseDouble(HospitalLocationMap.get(0).toString());

                    }
                    if(HospitalLocationMap.get(1)!=null){
                        LocationLang=Double.parseDouble(HospitalLocationMap.get(1).toString());
                    }

                    HospitalLatLng=new LatLng(LocationLat,LocationLang);
                    Location location1=new Location("");
                    location1.setLatitude(UserLocation.latitude);
                    location1.setLongitude(UserLocation.longitude);
                    Location location2=new Location("");
                    location2.setLatitude(HospitalLatLng.latitude);
                    location2.setLongitude(HospitalLatLng.longitude);
                    float Distance=location1.distanceTo(location2);
                    HospitalMarker=mMap.addMarker(new MarkerOptions().position(HospitalLatLng).title("Hospital "));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(HospitalLatLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(HospitalLatLng, 13));
                    getRoutertoMarker(HospitalLatLng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getRoutertoMarker(LatLng hospitalLatLng) {

        Routing routing = new Routing.Builder()
                .key("AIzaSyDQkvUlkOQnji5nmuOcYB4SYKFGAOgWaA4")
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),hospitalLatLng)
                .build();
        routing.execute();
    }

    public void GettingHospitalInfo(){
        database.child(HospitalFoundId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    RetreiveName = (String) dataSnapshot.child("Name").getValue();
                    RetreiveAddress = (String) dataSnapshot.child("Address").getValue();
                    Phone = (long) dataSnapshot.child("Phone").getValue();

                    Log.d("KEYY", RetreiveName + RetreiveAddress);


                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }


    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("ERoR:",e.getMessage());
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {


        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {

    }

    private void erasePolyLines() {
        for (Polyline line:polylines){
            line.remove();
        }
        polylines.clear();
    }
}