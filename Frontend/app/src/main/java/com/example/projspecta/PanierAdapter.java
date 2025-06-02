package com.example.projspecta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.projspecta.model.PanierItem;

import java.util.List;

public class PanierAdapter extends RecyclerView.Adapter<PanierAdapter.VH> {

    private static final String BASE_URL = "http://10.0.2.2:9090";

    public interface CartChangeListener { void onCartChanged(); }

    private final Context context;
    private final List<PanierItem> items;
    private final CartChangeListener listener;

    public PanierAdapter(Context context, List<PanierItem> items, CartChangeListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.activity_cart_adapter, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        PanierItem it = items.get(pos);

        h.showName.setText(it.getSpectacleName());
        h.ticketType.setText(it.getCategorieId());
        h.ticketPrice.setText(String.format("%.2f DT", it.getPrix()));
        h.quantity.setText(String.valueOf(it.getQuantite()));

        // Build the full URL and load with Glide exactly like in your Spectacle adapter
        String relative = it.getSpectacleImageUrl();
        if (relative != null && !relative.trim().isEmpty()) {
            String url = relative.startsWith("http") ? relative : BASE_URL + relative;
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .transform(new RoundedCorners(50))
                    .placeholder(R.drawable.c31ca70ebfc129fd520b6ba63b091d35)
                    .into(h.icon);
        } else {
            // fallback if no URL
            Glide.with(context)
                    .load(R.drawable.c31ca70ebfc129fd520b6ba63b091d35)
                    .centerCrop()
                    .transform(new RoundedCorners(50))
                    .into(h.icon);
        }

        h.increase.setOnClickListener(v -> {
            it.setQuantite(it.getQuantite() + 1);
            PanierManager.getInstance(context)
                    .updateItem(
                            it.getSpectacleId(), it.getCategorieId(), it.getQuantite(),
                            it.getSpectacleName(), it.getSpectacleImageUrl(),
                            it.getPrix(), it.getDescription()
                    );
            notifyItemChanged(pos);
            listener.onCartChanged();
        });

        h.decrease.setOnClickListener(v -> {
            if (it.getQuantite() > 1) {
                it.setQuantite(it.getQuantite() - 1);
                PanierManager.getInstance(context)
                        .updateItem(
                                it.getSpectacleId(), it.getCategorieId(), it.getQuantite(),
                                it.getSpectacleName(), it.getSpectacleImageUrl(),
                                it.getPrix(), it.getDescription()
                        );
                notifyItemChanged(pos);
                listener.onCartChanged();
            }
        });

        h.remove.setOnClickListener(v -> {
            PanierManager.getInstance(context)
                    .updateItem(
                            it.getSpectacleId(), it.getCategorieId(), 0,
                            it.getSpectacleName(), it.getSpectacleImageUrl(),
                            it.getPrix(), it.getDescription()
                    );
            items.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, items.size());
            listener.onCartChanged();
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView  icon;
        TextView   showName, ticketType, ticketPrice, quantity;
        ImageButton increase, decrease, remove;

        VH(@NonNull View v) {
            super(v);
            icon        = v.findViewById(R.id.ticketIconImageView);
            showName    = v.findViewById(R.id.showNameTextView);
            ticketType  = v.findViewById(R.id.ticketTypeTextView);
            ticketPrice = v.findViewById(R.id.ticketPriceTextView);
            quantity    = v.findViewById(R.id.quantityTextView);
            increase    = v.findViewById(R.id.increaseButton);
            decrease    = v.findViewById(R.id.decreaseButton);
            remove      = v.findViewById(R.id.removeItemButton);
        }
    }
}
