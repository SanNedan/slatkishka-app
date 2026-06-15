package com.example.slatkishka.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.slatkishka.R;
import com.example.slatkishka.fragments.BusinessDetailsFragment;
import com.example.slatkishka.models.BusinessModel;

import java.util.List;

// адаптер за локалите

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.BusinessViewHolder> {

    private Context context;
    private List<BusinessModel> businessList;

    // конструктор преку кој ги примаме податоците
    public BusinessAdapter(Context context, List<BusinessModel> businessList) {
        this.context = context;
        this.businessList = businessList;
    }

    @NonNull
    @Override
    public BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // го поврзуваме адаптерот со дизајнот на картичката
        View view = LayoutInflater.from(context).inflate(R.layout.item_business, parent, false);
        return new BusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessViewHolder holder, int position) {
        // земаме еден специфичен бизнис од листата според неговата позиција
        BusinessModel business = businessList.get(position);

        // ги пополнуваме TextView елементите
        holder.tvName.setText(business.getIme());
        holder.tvCategory.setText(business.getKategorija());

        // логика за копчето во форма на срце (за Favorites)
        holder.btnFavorite.setOnClickListener(v -> {
            // Засега само прикажуваме порака, но тука би се повикала базата за да се зачува
            Toast.makeText(context, business.getIme() + " е додаден во омилени ❤️", Toast.LENGTH_SHORT).show();
            // TODO: да се поврзе со SQLite!!
        });

        // логика за клик на картичка
        holder.itemView.setOnClickListener(v -> {
            // креираме Bundle и ги пакуваме податоците за локалот
            Bundle bundle = new Bundle();
            bundle.putString("business_name", business.getIme());
            bundle.putString("business_category", business.getKategorija());

            // креираме нов објект од фрагментот за детали и му ги прикачуваме податоците
            BusinessDetailsFragment detailsFragment = BusinessDetailsFragment.newInstance(
                    business.getIme(),
                    business.getKategorija()
            );

            // активноста мора да се повика преку контекст
            androidx.fragment.app.FragmentActivity activity = (androidx.fragment.app.FragmentActivity) context;
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailsFragment)
                    .addToBackStack(null) // овозможува корисникот да се врати назад во листата
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return businessList.size();
    }

    // ViewHolder класата ги чува референците до UI елементите за да не се бараат постојано со findViewById
    public static class BusinessViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory;
        ImageView ivIcon;
        ImageButton btnFavorite;

        public BusinessViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBusinessName);
            tvCategory = itemView.findViewById(R.id.tvBusinessCategory);
            ivIcon = itemView.findViewById(R.id.ivBusinessIcon);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}