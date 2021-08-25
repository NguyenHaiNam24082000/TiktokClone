package com.social.tiktok;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.social.tiktok.Adapter.RecyclerAdapter;
import com.social.tiktok.Adapter.UserAdapter;
import com.social.tiktok.Models.MediaObject;
import com.social.tiktok.Models.UserObject;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<MediaObject> mediaObjects;
    private RecyclerAdapter mediaAdapter;
    private SocialAutoCompleteTextView searchBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        recyclerView= recyclerView.findViewById(R.id.imageViewFull);
        searchBar=findViewById(R.id.search_bar);

    }
}