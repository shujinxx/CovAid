package com.ganshujin.androidrealtimelocation;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.ganshujin.androidrealtimelocation.Interface.IFirebaseLoadDone;
import com.ganshujin.androidrealtimelocation.Interface.IRecyclerItemClickListener;
import com.ganshujin.androidrealtimelocation.Model.User;
import com.ganshujin.androidrealtimelocation.Service.MyLocationReceiver;
import com.ganshujin.androidrealtimelocation.Utils.Common;
import com.ganshujin.androidrealtimelocation.ViewHolder.UserViewHolder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IFirebaseLoadDone {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    FirebaseRecyclerAdapter<User, UserViewHolder> adapter, searchAdapter;
    RecyclerView recycler_friend_list;
    IFirebaseLoadDone firebaseLoadDone;

    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<>();

    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AllPeopleActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView txt_logged_email = (TextView)headerView.findViewById(R.id.txt_logged_email);
        txt_logged_email.setText(Common.loggedUser.getEmail());

        searchBar = (MaterialSearchBar)findViewById(R.id.material_search_bar);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for(String search:suggestList)
                {
                    if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);
                searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                        if(!enabled)
                        {
                            if(adapter !=null)
                            {
                                recycler_friend_list.setAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {
                        startSearch(text.toString());

                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {

                    }
                });


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recycler_friend_list = (RecyclerView)findViewById(R.id.recycler_friend_list);
        recycler_friend_list.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_friend_list.setLayoutManager(layoutManager);
        recycler_friend_list.addItemDecoration(new DividerItemDecoration(this, ((LinearLayoutManager) layoutManager).getOrientation()));

        //Update LOcation
        updateLocation();

        firebaseLoadDone = this;

        loadFriendList();
        loadSearchData();




    }

    private void loadSearchData() {
        final List<String> UserEmail = new ArrayList<>();
        DatabaseReference userList = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST);

        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapShot:dataSnapshot.getChildren())
                {
                    User user = userSnapShot.getValue(User.class);
                    UserEmail.add(user.getEmail());

                }
                firebaseLoadDone.onFirebaseLoadUserNameDone(UserEmail);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                firebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());

            }
        });
    }



    private void loadFriendList() {
        Query query = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST);


        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int position, @NonNull final User model) {
                userViewHolder.txt_user_email.setText(new StringBuilder(model.getEmail()));

                userViewHolder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View v, int position) {
                        Common.trackingUser = model;
                        startActivity(new Intent(HomeActivity.this,TrackingActivity.class));
                    }
                });

            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_user,viewGroup,false);
                return new UserViewHolder(itemView);
            }
        };
        adapter.startListening();
        recycler_friend_list.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        if (adapter !=null)
            adapter.stopListening();
        if(searchAdapter !=null)
            searchAdapter.stopListening();
        super.onStop();
    }

    @Override
    protected void onResume() {
        if (adapter !=null)
            adapter.startListening();
        if(searchAdapter !=null)
            searchAdapter.startListening();
        super.onResume();
    }

    private void updateLocation() {
        buildLocationRequest();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }


        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());

    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(HomeActivity.this, MyLocationReceiver.class);
        intent.setAction(MyLocationReceiver.ACTION);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setFastestInterval(3000);
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void startSearch(String search_value) {
        List<String> Email = new ArrayList<>();
        Query query = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST)
                .orderByChild("name")
                .startAt(search_value);


        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int position, @NonNull final User model) {
                userViewHolder.txt_user_email.setText(new StringBuilder(model.getEmail()));

                userViewHolder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View v, int position) {
                        Common.trackingUser = model;
                        startActivity(new Intent(HomeActivity.this,TrackingActivity.class));
                    }
                });

            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_user,viewGroup,false);
                return new UserViewHolder(itemView);
            }
        };
        searchAdapter.startListening();
        recycler_friend_list.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();

        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_find_people) {
            Intent myIntent = new Intent(HomeActivity.this, AllPeopleActivity.class);
            HomeActivity.this.startActivity(myIntent);


        }  else if (id == R.id.nav_add_people) {
            Intent myIntent = new Intent(HomeActivity.this, FriendRequestActivity.class);
            HomeActivity.this.startActivity(myIntent);

        }   else if (id == R.id.nav_sign_out) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public void onFirebaseLoadUserNameDone(List<String> Email) {
        searchBar.setLastSuggestions(Email);

    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();

    }
}

