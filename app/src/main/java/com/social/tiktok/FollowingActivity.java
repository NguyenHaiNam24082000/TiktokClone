package com.social.tiktok;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.social.tiktok.MainRecycleView.VerticalSpacingItemDecorator;
import com.social.tiktok.MainRecycleView.VideoPlayerRecyclerAdapter;
import com.social.tiktok.MainRecycleView.VideoPlayerRecyclerView;
import com.social.tiktok.Models.MediaObject;
import com.social.tiktok.Responses.ApiClient;
import com.social.tiktok.Responses.ApiInterface;
import com.social.tiktok.Responses.Users;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingActivity extends AppCompatActivity {
    private ArrayList<MediaObject> mediaObjects=new ArrayList<>();
    private VideoPlayerRecyclerView recyclerView;
    private static ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        init();
    }

    private void init() {
        if(Build.VERSION.SDK_INT>=19 && Build.VERSION.SDK_INT<21)
        {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,true);
        }
        if(Build.VERSION.SDK_INT>=19)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        if(Build.VERSION.SDK_INT>=21)
        {
            setWindowFlag(this,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        recyclerView=(VideoPlayerRecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        //layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(0);
        recyclerView.addItemDecoration(itemDecorator);

        LoadAllProducts();
        SnapHelper snapHelper=new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);



    }

    private void LoadAllProducts() {
        Call<Users> call=apiInterface.performAllPosts();
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if(response.isSuccessful())
                {
                    mediaObjects=(ArrayList<MediaObject>) response.body().getAllPosts();
                    recyclerView.setMediaObjects(mediaObjects);
                    VideoPlayerRecyclerAdapter adapter=new VideoPlayerRecyclerAdapter(mediaObjects,initGlide(),FollowingActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.setKeepScreenOn(true);
                    recyclerView.smoothScrollToPosition(mediaObjects.size()+1);
                    Toast.makeText(FollowingActivity.this,"Connect",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(FollowingActivity.this,"Network Error",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Toast.makeText(FollowingActivity.this,"Network Error",Toast.LENGTH_LONG).show();

            }
        });
    }

    private RequestManager initGlide() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.color.black)
                .error(R.color.white);

        return Glide.with(this)
                .setDefaultRequestOptions(options);


    }

    private static void setWindowFlag(@NotNull Activity activity, final int bits, boolean b) {
        Window window=activity.getWindow();
        WindowManager.LayoutParams winParams= window.getAttributes();
        if(b)
        {
            winParams.flags |= bits;
        }
        else
        {
            winParams.flags &= ~bits;
        }
        window.setAttributes(winParams);
    }

    @Override
    protected void onDestroy() {
        if(recyclerView!=null)
            recyclerView.releasePlayer();
        super.onDestroy();
    }

    protected void onStop() {
        super.onStop();

        if(recyclerView!=null)
            recyclerView.releasePlayer();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    public void foryouBtn(View view) {
        Intent intent=new Intent(FollowingActivity.this,HomeActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Animatoo.animateSwipeRight(this);
        finish();
    }
}