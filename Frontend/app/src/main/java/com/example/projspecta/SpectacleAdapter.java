package com.example.projspecta;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.projspecta.model.Spectacle;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SpectacleAdapter extends RecyclerView.Adapter<SpectacleAdapter.ViewHolder> {

    private final Context context;
    private final List<Spectacle> spectacles;

    public SpectacleAdapter(Context context, List<Spectacle> spectacles) {
        this.context = context;
        this.spectacles = spectacles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Spectacle spectacle = spectacles.get(position);

        // Bind des données aux composants UI
        holder.spectacleName.setText(spectacle.getTitre());

        String imageUrl = "http://10.0.2.2:9090" + spectacle.getImagePath();

        // Chargement de l'image avec Glide
        Glide.with(context)
                .load(imageUrl)
                .centerCrop()
                .transform(new RoundedCorners(50))
                .into(holder.spectacleImage);

        // Ajout du listener sur le bouton pour transmettre l'ID du spectacle
        holder.imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, spectacle_activity.class);
            intent.putExtra("spectacle_id", spectacle.getIdSpec());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return spectacles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView spectacleName, spectacleLocation, spectacleDate;
        ImageView spectacleImage;
        MaterialButton imageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            spectacleName = itemView.findViewById(R.id.event_title);
            spectacleImage = itemView.findViewById(R.id.event_image);
            imageButton = itemView.findViewById(R.id.add_btn);
        }
    }
}
