package com.naorfarag.exercise1naorjonathan.Search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kc.unsplash.models.Photo;
import com.naorfarag.exercise1naorjonathan.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PhotoRecyclerAdapter extends RecyclerView.Adapter<PhotoRecyclerAdapter.ViewHolder> {

    private List<Photo> photos;
    private OnImageClickListener onImageClickListener;
    public PhotoRecyclerAdapter(OnImageClickListener onImageClickListener) {
        photos = new ArrayList<>();
        this.onImageClickListener = onImageClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(view, onImageClickListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Photo photo = photos.get(position);

        Picasso.get().load(photo.getUrls().getSmall()).into(holder.imageView);
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        public final ImageView imageView;
        public ViewHolder(View view, OnImageClickListener onImageClickListener) {
            super(view);
            imageView = view.findViewById(R.id.imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onImageClickListener != null){
                        onImageClickListener.onImageClick(getAdapterPosition());
                    }
                }
            });
        }

    }

    public interface OnImageClickListener {
        void onImageClick(int position);
    }
}