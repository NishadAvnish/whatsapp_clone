package com.example.avnish.whatsapp_clone.CHAT_RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.avnish.whatsapp_clone.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    Intent i;
    String currentGroupName;
    Toolbar toolbar;
    EditText sendEdit;
    Button send_btn;
    DatabaseReference databaseRef;
    String currentUserId,username,Uid;
    RecyclerView recyclerView;
    HashMap<String,String> map;
    ArrayList<databook> arrayList=null;
    adapter mAdapter;
    Integer flag=0;
    ImageView deletebtn;
    int i_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group__chat);

        Initialize();

        if(i.getExtras()!=null){

            flag=i.getExtras().getInt("CHATACTIVITY");
            if(flag==3)
                {
                Uid = i.getExtras().get("Tag").toString();
                }

            else
                currentGroupName=i.getExtras().get("currentGroupName").toString();
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(currentGroupName);

            retrieve();  //convert the data value from firebase into object

            send_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    writeData();
                }
            });



        }

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-------------------------TO remove friend section from currentUsre---------------------------//
                FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Friend").child(Uid).setValue(null);


                //---------------------------To remove friend  section from friend database--------------------------//

                FirebaseDatabase.getInstance().getReference().child("User").child(Uid)
                        .child("Friend").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(null);


                //----------------------------To remove chat section------------------------------------------------//
                FirebaseDatabase.getInstance().getReference().child("CHAT").child(currentUserId + Uid).setValue(null);
                FirebaseDatabase.getInstance().getReference().child("CHAT").child(Uid+currentUserId).setValue(null);

                FirebaseDatabase.getInstance().getReference().child("User").child(Uid)
                        .child("Friend").setValue(null);


                finish();
            }
        });
    }


    private void retrieve() {


        arrayList.clear();
        if(flag==3){
            FirebaseDatabase.getInstance().getReference().child("CHAT").child(Uid+currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                        {  arrayList.add(dataSnapshot1.getValue(databook.class));
                            mAdapter.notifyDataSetChanged();
                    }}

                    else{
                        FirebaseDatabase.getInstance().getReference().child("CHAT").child(currentUserId+Uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                                    {
                                        arrayList.add(dataSnapshot1.getValue(databook.class));
                                        mAdapter.notifyDataSetChanged();
                                    }

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

      else {
            FirebaseDatabase.getInstance().getReference("Group").child(currentGroupName).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())

                        {
                            arrayList.add(dataSnapshot1.getValue(databook.class));
                            mAdapter.notifyDataSetChanged();

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


}

    private void writeData() {

        arrayList.clear();
        mAdapter.notifyDataSetChanged();



                    if(sendEdit.getText().toString()!=null) {

                        Calendar calender = Calendar.getInstance();
                        String date = new SimpleDateFormat("EEE,MMM d,yyyy").format(calender.getTime());
                        String time = new SimpleDateFormat("h:mm a").format(calender.getTime());
                        map = new HashMap<>();
                        map.put("Msg", sendEdit.getText().toString().trim());
                        map.put("Date", date);
                        map.put("Time", time);
                        map.put("Name", name());
                        map.put("user", currentUserId);


                        //---------------------------------to make make and write the data to one one tone chat activity------------///
                        if (flag == 3) {

                            FirebaseDatabase.getInstance().getReference().child("CHAT").child(currentUserId + Uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        FirebaseDatabase.getInstance().getReference().child("CHAT").child(currentUserId + Uid).push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                sendEdit.setText("");


                                            }
                                        });
                                    } else {
                                        Log.i("TAG", "fhgfhgf");
                                        FirebaseDatabase.getInstance().getReference().child("CHAT").child(Uid + currentUserId).push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                sendEdit.setText("");


                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }


                        ////-------------------------------GROUPCHAT--------------------------------//////////////
                        else {
                            FirebaseDatabase.getInstance().getReference("Group").child(currentGroupName).push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sendEdit.setText("");
                                   // scrollView.fullScroll(View.FOCUS_DOWN);

                                }
                            });
                        }
                    }

        }



        private String name(){

            FirebaseDatabase.getInstance().getReference().child("User").child(currentUserId).child("Name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        username = dataSnapshot.getValue().toString();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return username;
        }



    private void Initialize() {
        i= getIntent();
        toolbar=findViewById(R.id.toolbar);
        sendEdit=findViewById(R.id.hello);
        send_btn=findViewById(R.id.sendbtn);
        deletebtn=findViewById(R.id.deletebtn);

        currentUserId=(FirebaseAuth.getInstance().getCurrentUser().getUid());
        recyclerView=findViewById(R.id.Recyclerview);

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList= new ArrayList<>();
        mAdapter = new adapter(arrayList, name());
        recyclerView.setAdapter(mAdapter);

        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView,new RecyclerView.State(), recyclerView.getAdapter().getItemCount());

        }

}
