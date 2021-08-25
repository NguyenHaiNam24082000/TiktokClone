package com.social.tiktok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.internal.cache.DiskLruCache;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.social.tiktok.MainRecycleView.VerticalSpacingItemDecorator;
import com.social.tiktok.MainRecycleView.VideoPlayerRecyclerAdapter;
import com.social.tiktok.MainRecycleView.VideoPlayerRecyclerView;
import com.social.tiktok.Models.MediaObject;
import com.social.tiktok.Models.Status;
import com.social.tiktok.Models.UserObject;
import com.social.tiktok.Models.UserStatus;
import com.social.tiktok.Responses.ApiClient;
import com.social.tiktok.Responses.ApiInterface;
import com.social.tiktok.Responses.Users;
import com.social.tiktok.VideoEditorFolder.PortraitCameraActivity;
import com.social.tiktok.databinding.ActivityHomeBinding;
import com.social.tiktok.databinding.ActivitySetupProfileBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.social.tiktok.R.drawable.ic_outline_account_circle_24;
import static com.social.tiktok.R.drawable.ic_user;

public class HomeActivity extends AppCompatActivity {
    private ArrayList<MediaObject> mediaObjects=new ArrayList<>();
    private VideoPlayerRecyclerView recyclerView;
    private static  ApiInterface apiInterface;
    private static final int CAMERA_PERMISSION_REQUEST_CODE=88888;
    private CircleImageView imageUser;
    private ImageView imageMessageChat;
    private int selectedID;
    private FirebaseAuth auth;
    //dùng để xác thực
    private FirebaseStorage storage;
    //lưu trữ
    private ProgressDialog dialogUploadVideo;
    //thanh tiến trình
    private UserObject userObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        init();
        imageMessageChat=findViewById(R.id.imageMessageChat);
        imageUser=findViewById(R.id.imageUser);
        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        if(auth.getCurrentUser()!=null)
        {
            imageMessageChat.setVisibility(View.VISIBLE);
            StorageReference reference=storage.getReference().child("Profiles").child(auth.getUid());
            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(HomeActivity.this).load(uri.toString()).into(imageUser);
                }
            });
            imageUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(HomeActivity.this,UserProfileActivity.class);
                    startActivity(intent);
                }
            });

            //imageUser.setImageURI(reference.getDownloadUrl().getResult());
        }
        else {

            imageUser.setImageDrawable(getResources().getDrawable(ic_outline_account_circle_24));
            imageUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(HomeActivity.this, R.style.BottomSheetDialogTheme);
                    View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                            R.layout.layout_bottom_login,
                            findViewById(R.id.bottomLogin)
                    );
                    bottomSheetView.findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //Toast.makeText(HomeActivity.this,((EditText) bottomSheetView.findViewById(R.id.phoneNumberorEmail)).getText(),Toast.LENGTH_LONG).show();

                            FirebaseDatabase database= FirebaseDatabase.getInstance();
                            DatabaseReference reference=database.getReference();
                            reference.child("users").addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    boolean hasValue = false;
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        Toast.makeText(HomeActivity.this,ds.child("phoneNumber").getValue(String.class),Toast.LENGTH_LONG).show();
                                        if (((((EditText) bottomSheetView.findViewById(R.id.phoneNumberorEmail)).getText()).toString()).equals(ds.child("phoneNumber").getValue(String.class))) {
                                            hasValue = true;
                                            break;
                                        }
                                    }
                                    if (hasValue == true) {
                                        Intent intent = new Intent(HomeActivity.this, VerifyOTPActivity.class);
                                        intent.putExtra("phoneNumberLogin", ((((EditText) bottomSheetView.findViewById(R.id.phoneNumberorEmail)).getText()).toString()));
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        TextView user=bottomSheetView.findViewById(R.id.phoneNumberorEmail);
                                        user.setError("Bạn hãy đăng ký tài khoản");
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }

                            });



                        }
                    });
                    bottomSheetView.findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(HomeActivity.this, RegisterActivity.class);
                            startActivity(intent);
                            finish();
                            bottomSheetDialog.dismiss();
                        }
                    });
                    bottomSheetDialog.setContentView(bottomSheetView);
                    bottomSheetDialog.show();
                }
            });
        }
    }

    private void init() {
        if(Build.VERSION.SDK_INT>=19 && Build.VERSION.SDK_INT<21)
        {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,true);
        }
        if(Build.VERSION.SDK_INT>=19)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if(Build.VERSION.SDK_INT>=21)
        {
            setWindowFlag(this,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        recyclerView=(VideoPlayerRecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
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
//        Call<Users> call=apiInterface.performAllPosts();
//        call.enqueue(new Callback<Users>() {
//            @Override
//            public void onResponse(Call<Users> call, Response<Users> response) {
//                if(response.isSuccessful())
//                {
//                    mediaObjects=(ArrayList<MediaObject>) response.body().getAllPosts();
//                    recyclerView.setMediaObjects(mediaObjects);
//                    VideoPlayerRecyclerAdapter adapter=new VideoPlayerRecyclerAdapter(mediaObjects,initGlide(),HomeActivity.this);
//                    recyclerView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//                    recyclerView.setKeepScreenOn(false);
//                    recyclerView.smoothScrollToPosition(mediaObjects.size()+1);
//                    Toast.makeText(HomeActivity.this,"Connect",Toast.LENGTH_LONG).show();
//                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                        @Override
//                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                            super.onScrollStateChanged(recyclerView, newState);
//                            if (newState == RecyclerView.SCROLL_STATE_IDLE){
//                                selectedID = ((LinearLayoutManager)recyclerView.getLayoutManager())
//                                        .findFirstVisibleItemPosition();;
//                                //Toast.makeText(HomeActivity.this,selectedID+"",Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//
//
//                }
//                else
//                {
//                    Toast.makeText(HomeActivity.this,"Network Error",Toast.LENGTH_LONG).show();
//                }
                    FirebaseDatabase.getInstance().getReference().child("videos").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                //mediaObjects=new ArrayList<>();
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    for(DataSnapshot dsChild: ds.child("media").getChildren()) {
                                        MediaObject mediaObject = dsChild.getValue(MediaObject.class);
                                        mediaObjects.add(mediaObject);
                                    }
                                }
                                recyclerView.setMediaObjects(mediaObjects);
                                VideoPlayerRecyclerAdapter adapter = new VideoPlayerRecyclerAdapter(mediaObjects, initGlide(), HomeActivity.this);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                recyclerView.setKeepScreenOn(false);
                                recyclerView.smoothScrollToPosition(mediaObjects.size() + 1);
                                Toast.makeText(HomeActivity.this, "Connect", Toast.LENGTH_LONG).show();
                                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                            selectedID = ((LinearLayoutManager) recyclerView.getLayoutManager())
                                                    .findFirstVisibleItemPosition();
                                            ;
                                            //Toast.makeText(HomeActivity.this,selectedID+"",Toast.LENGTH_LONG).show();
                                        }
                                    }


                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            }

//            @Override
//            public void onFailure(Call<Users> call, Throwable t) {
//                Toast.makeText(HomeActivity.this,"Network Error",Toast.LENGTH_LONG).show();
//
//            }
//        });


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

    public void followingBtn(View view) {
        Intent intent=new Intent(HomeActivity.this,FollowingActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Animatoo.animateSwipeRight(this);
        finish();
    }

    public void addBtn(View view) {
//        checkPermission();
//        Intent intent=new Intent(HomeActivity.this, PortraitCameraActivity.class);
//        startActivity(intent);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        Animatoo.animateSlideUp(this);
//        finish();
        if(FirebaseAuth.getInstance().getUid()!=null) {
            Intent intent = new Intent(HomeActivity.this, UploadVideoActivity.class);
            startActivity(intent);
        }
    }




    @Override
    protected void onResume() {
        super.onResume();

    }



    public void message(View view) {
        Intent intent=new Intent(this,MessageActivity.class);
        startActivity(intent);
    }

    public void searchBtn(View view) {
        Intent intent=new Intent(this,SearchActivity.class);
        startActivity(intent);
        finishAffinity();
    }

}