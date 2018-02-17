package com.helpmeproductions.willus08.welp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.helpmeproductions.willus08.welp.model.Business;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    List<Business> buisnesses = new ArrayList<>();

    public RecyclerAdapter(List<Business> buisnesses) {
        this.buisnesses = buisnesses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.buissnes_information_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Business business = buisnesses.get(position);
        if(business.getImageUrl() != null){
            Glide.with(holder.itemView.getContext())
                    .load(business.getImageUrl())
                    .into(holder.buisnessImage);
        }
        // converts the meeters to miles

        String distanceTo = String.format(" %.2f miles",business.getDistance() * 0.00062137);
        holder.name.setText(business.getName());
        holder.address.setText(business.getLocation().getAddress1());
        holder.distance.setText(distanceTo);
        holder.raiting.setRating(business.getRating());
    }

    @Override
    public int getItemCount() {
        return buisnesses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView buisnessImage;
        TextView name;
        TextView address;
        TextView distance;
        RatingBar raiting;
        public ViewHolder(View itemView) {
            super(itemView);
            buisnessImage = itemView.findViewById(R.id.ivBuisnesImage);
            name = itemView.findViewById(R.id.tvName);
            distance = itemView.findViewById(R.id.tvDistance);
            address  = itemView.findViewById(R.id.tvAddress);
            raiting = itemView.findViewById(R.id.rbRatings);
        }
    }
}
