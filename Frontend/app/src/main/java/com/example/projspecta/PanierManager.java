package com.example.projspecta;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.projspecta.model.PanierItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PanierManager {
    private static final String PREF_NAME = "user_cart";
    private static final String KEY_PANIER = "panier";

    private static PanierManager instance;
    private SharedPreferences prefs;

    private PanierManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static PanierManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new PanierManager(ctx.getApplicationContext());
        }
        return instance;
    }

    public void ajouterAuPanier(long idBillet, long spectacleId, String categorieId, double prix,
                                String description, int quantite,
                                String spectacleName, String imageUrl) {
        try {
            JSONArray old = getPanierArray(), updated = new JSONArray();
            boolean found = false;

            for (int i = 0; i < old.length(); i++) {
                JSONObject o = old.getJSONObject(i);
                if (o.getLong("spectacle_id") == spectacleId &&
                        o.getString("categorie_id").equals(categorieId)) {
                    o.put("quantite", o.getInt("quantite") + quantite);
                    found = true;
                }
                updated.put(o);
            }

            if (!found) {
                JSONObject o = new JSONObject();
                o.put("id_billet", idBillet);
                o.put("spectacle_id", spectacleId);
                o.put("categorie_id", categorieId);
                o.put("quantite", quantite);
                o.put("spectacle_name", spectacleName);
                o.put("image_url", imageUrl);
                o.put("prix", prix);
                o.put("description", description);
                updated.put(o);
            }

            prefs.edit().putString(KEY_PANIER, updated.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateItem(long spectacleId, String categorieId, int quantite,
                           String spectacleName, String imageUrl, double prix, String description) {
        try {
            JSONArray old = getPanierArray(), updated = new JSONArray();
            for (int i = 0; i < old.length(); i++) {
                JSONObject o = old.getJSONObject(i);
                if (o.getLong("spectacle_id") == spectacleId &&
                        o.getString("categorie_id").equals(categorieId)) {
                    if (quantite > 0) {
                        o.put("quantite", quantite);
                        updated.put(o);
                    }
                } else {
                    updated.put(o);
                }
            }
            prefs.edit().putString(KEY_PANIER, updated.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<PanierItem> getPanierList() {
        List<PanierItem> list = new ArrayList<>();
        try {
            JSONArray arr = getPanierArray();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                PanierItem it = new PanierItem();
                it.setBilletId(o.optLong("id_billet", 0));
                it.setSpectacleId(o.getLong("spectacle_id"));
                it.setCategorieId(o.getString("categorie_id"));
                it.setQuantite(o.getInt("quantite"));
                it.setSpectacleName(o.optString("spectacle_name", ""));
                it.setSpectacleImageUrl(o.optString("image_url", ""));
                it.setPrix(o.optDouble("prix", 0.0));
                it.setDescription(o.optString("description", ""));
                list.add(it);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private JSONArray getPanierArray() {
        try {
            return new JSONArray(prefs.getString(KEY_PANIER, "[]"));
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    public void viderPanier() {
        prefs.edit().remove(KEY_PANIER).apply();
    }
}
