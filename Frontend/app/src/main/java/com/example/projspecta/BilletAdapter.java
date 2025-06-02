package com.example.projspecta;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projspecta.model.Billet;

import java.util.List;

public class BilletAdapter extends RecyclerView.Adapter<BilletAdapter.TypeViewHolder> {

    private final List<Billet> billetTypes;

    public BilletAdapter(List<Billet> billetTypes) {
        this.billetTypes = billetTypes;
    }

    @NonNull @Override
    public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_billet_item, parent, false);
        return new TypeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeViewHolder holder, int position) {
        Billet billet = billetTypes.get(position);
        holder.typeText.setText(billet.getType());
        holder.priceText.setText(billet.getPrice() + " DT");

        // Description by type
        String desc;
        switch (billet.getType().toLowerCase()) {
            case "gold":   desc = "Accès VIP, sièges premium, boissons/snacks inclus"; break;
            case "silver": desc = "Accès prioritaire, sièges confortables"; break;
            case "normale":desc = "Accès standard, sièges classiques"; break;
            default:       desc = "Type de billet inconnu";
        }
        holder.descriptionText.setText(desc);

        Button btn = holder.statusButton;
        if ("Non".equalsIgnoreCase(billet.getVendu())) {
            btn.setText("Ajouter au panier");
            btn.setEnabled(true);
            btn.setOnClickListener(v -> {
                Context ctx = v.getContext();
                long spectacleId   = billet.getSpectacle().getIdSpec();
                String categorieId = billet.getType();
                double prix        = billet.getPrice();
                String description = desc;
                int quantite       = 1;
                String spectacleName = billet.getSpectacle().getTitre();
                String imagePath     = billet.getSpectacle().getImagePath();
                PanierManager.getInstance(ctx).ajouterAuPanier(
                        billet.getIdBillet(),
                        spectacleId,
                        categorieId,
                        prix,
                        description,
                        quantite,
                        spectacleName,
                        imagePath
                );

                Toast.makeText(ctx, "Billet ajouté au panier !", Toast.LENGTH_SHORT).show();
            });
        } else {
            btn.setText("En rupture de stock");
            btn.setEnabled(false);
        }
    }

    @Override public int getItemCount() { return billetTypes.size(); }

    static class TypeViewHolder extends RecyclerView.ViewHolder {
        TextView typeText, priceText, descriptionText;
        Button statusButton;
        TypeViewHolder(@NonNull View v) {
            super(v);
            typeText        = v.findViewById(R.id.billetTypeText);
            priceText       = v.findViewById(R.id.billetPriceText);
            descriptionText = v.findViewById(R.id.billetDescriptionText);
            statusButton    = v.findViewById(R.id.statusButton);
        }
    }
}
