package com.kominfo.praunper;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    public static final String MESSAGES_CHILD = "messages";
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder> mFirebaseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();


        mMessageRecyclerView =  findViewById(R.id.recycler);
        mLinearLayoutManager = new LinearLayoutManager(this);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = findViewById(R.id.input);

                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("messages")
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName())
                        );

                // Clear the input
                input.setText("");
            }
        });


        DatabaseReference messagesRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD);

        SnapshotParser<ChatMessage> parser = new SnapshotParser<ChatMessage>() {
            @Override
            public ChatMessage parseSnapshot(DataSnapshot dataSnapshot) {
                ChatMessage friendlyMessage = dataSnapshot.getValue(ChatMessage.class);
                if (friendlyMessage != null) {
                    friendlyMessage.setId(dataSnapshot.getKey());
                }
                return friendlyMessage;
            }
        };

        FirebaseRecyclerOptions<ChatMessage> options =
                new FirebaseRecyclerOptions.Builder<ChatMessage>()
                        .setQuery(messagesRef, parser)
                        .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull ChatMessage model) {

                holder.messageTExt.setText(model.getMessageText());
                holder.messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
                holder.messageUser.setText(model.getMessageUser()+"");
            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.message, viewGroup, false));
            }
        };


        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAdapter.startListening();
    }
}
