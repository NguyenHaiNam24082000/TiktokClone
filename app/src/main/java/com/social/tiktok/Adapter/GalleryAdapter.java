package com.social.tiktok.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.social.tiktok.ImageFullActivity;
import com.social.tiktok.Models.ImageGallery;
import com.social.tiktok.R;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ImageViewHolder>{

    private Context context;
    private ArrayList<ImageGallery> images;

    public GalleryAdapter(Context context, ArrayList<ImageGallery> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        final ImageGallery image=images.get(position);
        Glide.with(context).load(image.getImagePath())
                .apply(new RequestOptions().centerCrop()).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //photoListener.onPhotoClick(image);
                Intent intent=new Intent(context, ImageFullActivity.class);
                intent.putExtra("path",image.getImagePath());
                intent.putExtra("name",image.getImageName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class  ImageViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageGallery);
        }
    }

//    public interface  PhotoListener
//    {
//        void onPhotoClick(String path);
//    }
}
