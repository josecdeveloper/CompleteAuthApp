package com.android.completeauthapp.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jose on 12/16/2016.
 */

public class Post {
    public String uid;
    public String author;
    public String body;

    public Post() {
    }

    public Post(String uid, String author, String body) {
        this.uid = uid;
        this.author = author;
        this.body = body;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("body", body);

        return result;
    }
    // [END post_to_map]
}
