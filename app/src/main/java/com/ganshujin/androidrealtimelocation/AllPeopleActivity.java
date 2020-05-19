package com.ganshujin.androidrealtimelocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.ganshujin.androidrealtimelocation.Interface.IFirebaseLoadDone;
import com.ganshujin.androidrealtimelocation.Interface.IRecyclerItemClickListener;
import com.ganshujin.androidrealtimelocation.Model.MyResponse;
import com.ganshujin.androidrealtimelocation.Model.Request;
import com.ganshujin.androidrealtimelocation.Model.User;
import com.ganshujin.androidrealtimelocation.Remote.IFCMService;
import com.ganshujin.androidrealtimelocation.Utils.Common;
import com.ganshujin.androidrealtimelocation.ViewHolder.UserViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AllPeopleActivity extends AppCompatActivity implements IFirebaseLoadDone {

    FirebaseRecyclerAdapter<User, UserViewHolder> adapter, searchAdapter;
    RecyclerView recycler_all_user;
    IFirebaseLoadDone firebaseLoadDone;

    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<>();

    IFCMService ifcmService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_people);

        ifcmService = Common.getFCMService();


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
                                recycler_all_user.setAdapter(adapter);
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

        recycler_all_user = (RecyclerView)findViewById(R.id.recycler_all_people);
        recycler_all_user.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_all_user.setLayoutManager(layoutManager);
        recycler_all_user.addItemDecoration(new DividerItemDecoration(this, ((LinearLayoutManager) layoutManager).getOrientation()));


        firebaseLoadDone = this;

        loadUserList();
        loadSearchData();
    }

    private void loadSearchData() {
        final List<String> UserEmail = new ArrayList<>();
        DatabaseReference userList = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION);
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

    private void loadUserList() {
        Query query = FirebaseDatabase.getInstance().getReference().child(Common.USER_INFORMATION);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int position, @NonNull final User model) {
                if (model.getEmail().equals(Common.loggedUser.getEmail()))
                {
                    userViewHolder.txt_user_email.setText(new StringBuilder(model.getEmail()).append("  (me)"));
                    userViewHolder.txt_user_email.setTypeface(userViewHolder.txt_user_email.getTypeface(), Typeface.ITALIC);
                }
                else
                {
                    userViewHolder.txt_user_email.setText(new StringBuilder(model.getEmail()));
                }

                //
                userViewHolder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View v, int position) {
                        showDialogRequest(model);

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

        //avoid all blank in load user

        adapter.startListening();
        recycler_all_user.setAdapter(adapter);


    }

    private void showDialogRequest(final User model) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this,R.style.MyRequestDialog);
        alertDialog.setTitle("Request Friend");
        alertDialog.setMessage("Do you want to send request to "+model.getEmail() );
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            };
        });
        alertDialog.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //add to accept list
                DatabaseReference acceptList = FirebaseDatabase.getInstance()
                        .getReference(Common.USER_INFORMATION)
                        .child(Common.loggedUser.getUid())
                        .child(Common.ACCEPT_LIST);

                acceptList.orderByKey().equalTo(model.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()==null)
                                    sendFriendRequest(model);
                                else
                                    Toast.makeText(AllPeopleActivity.this, "You and "+model.getEmail()+" are already friends.", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }
        });

        alertDialog.show();

    }

    private void sendFriendRequest(final User model) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.TOKENS);

        tokens.orderByKey().equalTo(model.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null)
                            Toast.makeText(AllPeopleActivity.this, "Token error.", Toast.LENGTH_SHORT);
                        else
                        {
                            Request request = new Request();

                            Map<String,String> dataSend = new HashMap<>();
                            dataSend.put(Common.FROM_UID,Common.loggedUser.getUid());
                            dataSend.put(Common.FROM_NAME,Common.loggedUser.getEmail());
                            dataSend.put(Common.TO_UID,model.getUid());
                            dataSend.put(Common.TO_NAME,model.getEmail());

                            request.setTo(dataSnapshot.child(model.getUid()).getValue(String.class));
                            request.setData(dataSend);

                            //Send
                            compositeDisposable.add(ifcmService.sendFriendRequestToUser(request)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<MyResponse>() {
                                @Override
                                public void accept(MyResponse myResponse) throws Exception {
                                    if(myResponse.success==1)
                                        Toast.makeText(AllPeopleActivity.this, "Request sent", Toast.LENGTH_SHORT).show();

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept (Throwable throwable) throws Exception {
                                    Toast.makeText(AllPeopleActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }));


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onStop() {
        if(adapter !=null)
            adapter.stopListening();
        if(searchAdapter !=null)
            searchAdapter.stopListening();
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter !=null)
            adapter.startListening();
        if(searchAdapter !=null)
            searchAdapter.startListening();

    }

    private void startSearch(String text_search) {
        Query query = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .orderByChild("name")
                .startAt(text_search);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int position, @NonNull final User model) {
                if (model.getEmail().equals(Common.loggedUser.getEmail()))
                {
                    userViewHolder.txt_user_email.setText(new StringBuilder(model.getEmail()).append("  (me)"));
                    userViewHolder.txt_user_email.setTypeface(userViewHolder.txt_user_email.getTypeface(), Typeface.ITALIC);
                }
                else
                {
                    userViewHolder.txt_user_email.setText(new StringBuilder(model.getEmail()));
                }

                //
                userViewHolder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View v, int position) {
                        showDialogRequest(model);
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
        recycler_all_user.setAdapter(searchAdapter);

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
