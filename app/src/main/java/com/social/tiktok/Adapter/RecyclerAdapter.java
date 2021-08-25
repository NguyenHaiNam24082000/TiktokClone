package com.social.tiktok.Adapter;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.database.FirebaseDatabase;
import com.social.tiktok.HomeActivity;
import com.social.tiktok.Models.MediaObject;
import com.social.tiktok.R;
import com.social.tiktok.databinding.ItemVideoBinding;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter {

    List<MediaObject> mediaObjects;
    Context context;
    ImageView imageShare;
    FirebaseDatabase database= FirebaseDatabase.getInstance();
    public RecyclerAdapter(List<MediaObject> mediaObjects, Context context) {
        this.mediaObjects = mediaObjects;
        this.context = context;
    }

    @NonNull
    @Override
    public RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video,parent,false);
        
        return new RecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        MediaObject mediaObject=mediaObjects.get(position);
//        RecycleViewHolder view=(RecycleViewHolder)holder;
//        ExoPlayer exoPlayer;
//        BandwidthMeter bandwidthMeter=new DefaultBandwidthMeter.Builder(context).build();
//        TrackSelection trackSelection= (TrackSelection) new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
//        exoPlayer=(SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(context);
//        Uri video=Uri.parse();
//        DefaultHttpDataSourceFactory df=new DefaultHttpDataSourceFactory("videos");
//        ExtractorsFactory ef=new DefaultExtractorsFactory();
//        MediaSource mediaSource=new ExtractorsFactory(video,df,ef,null,null);
//        ((RecycleViewHolder) holder).binding.playerView.setPlayer(exoPlayer);
//        exoPlayer.prepare(mediaSource);
//        exoPlayer.setPlayWhenReady(true);

    }

    @Override
    public int getItemCount() {
        return mediaObjects.size();
    }


    public class RecycleViewHolder  extends RecyclerView.ViewHolder {
        ItemVideoBinding binding;
        public RecycleViewHolder(@NonNull View view) {
            super(view);
            binding= ItemVideoBinding.bind(view);
        }
    }
}
