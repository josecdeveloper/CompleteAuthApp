package com.android.completeauthapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.completeauthapp.model.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView homeRecyclerView;
    private DatabaseReference postRef;
    private Button newPostBtn;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        postRef = FirebaseDatabase.getInstance().getReference().child("post");
        homeRecyclerView = (RecyclerView) view.findViewById(R.id.homeRecyclerView);

        newPostBtn = (Button) view.findViewById(R.id.newPost);
        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NewPostActivity.class));

                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeRecyclerView.setHasFixedSize(true);
        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerAdapter<Post, PostViewHolder> adpter =
                new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                        Post.class,
                        R.layout.home_list_item,
                        PostViewHolder.class,
                        postRef) {
                    @Override
                    protected void populateViewHolder(PostViewHolder viewHolder, Post model, int position) {

                        viewHolder.authorView.setText(model.author);
                        viewHolder.bodyView.setText(model.body);
                    }
                };
        homeRecyclerView.setAdapter(adpter);
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView authorView;
        public TextView bodyView;

        public PostViewHolder(View itemView) {
            super(itemView);

            authorView = (TextView) itemView.findViewById(R.id.authorTV);
            bodyView = (TextView) itemView.findViewById(R.id.bodyTV);
        }
    }

}
