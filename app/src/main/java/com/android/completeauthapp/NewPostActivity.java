package com.android.completeauthapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.completeauthapp.model.Post;
import com.android.completeauthapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    private static final String TAG = NewPostActivity.class.getSimpleName();
    private DatabaseReference mDatabase;
    private EditText fieldBody;
    private Button submitPostBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        fieldBody = (EditText) findViewById(R.id.field_body);
        submitPostBtn = (Button) findViewById(R.id.submitPost);

        submitPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPost();
            }
        });
    }

    private void submitPost() {
        final String body = fieldBody.getText().toString();

        if (TextUtils.isEmpty(body)) {
            fieldBody.setError("Required");
            return;
        }

        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user == null) {
                    // User is null, error out
                    Log.e(TAG, "User " + userId + " is unexpectedly null");
                    Toast.makeText(NewPostActivity.this,
                            "Error: could not fetch user.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Write new post
                    writeNewPost(userId, user.username, body);
                }

                // Finish this Activity, back to the stream
                setEditingEnabled(true);
                finish();
                // [END_EXCLUDE]
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setEditingEnabled(boolean enabled) {
        fieldBody.setEnabled(enabled);
        if (enabled) {
            submitPostBtn.setVisibility(View.VISIBLE);
        } else {
            submitPostBtn.setVisibility(View.GONE);
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // [START write_fan_out]
    private void writeNewPost(String userId, String username, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
    // [END write_fan_out]
}
