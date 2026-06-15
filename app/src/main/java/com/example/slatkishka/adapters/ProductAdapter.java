package com.example.slatkishka.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.slatkishka.R;
import com.example.slatkishka.models.ProductModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// адаптер за производи

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<ProductModel> productList; // листа на производи
    private Map<String, Integer> cartQuantities = new HashMap<>(); // хеш мапа со бројот на производи
    private OnCartChangedListener listener;


    public interface OnCartChangedListener {
        void onCartChanged(String productName, int quantity);
    }

    public ProductAdapter(Context context, List<ProductModel> productList, OnCartChangedListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel product = productList.get(position);

        holder.tvName.setText(product.getIme()); // име на производот
        holder.tvPrice.setText(product.getCena() + " ден."); // и цена

        // Glide ни служи за преземање на сликата од Firebase Storage URL
        Glide.with(context)
                .load(product.getSlika())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivImage);

        int currentQty = cartQuantities.getOrDefault(product.getIme(), 0);
        holder.tvQuantity.setText(String.valueOf(currentQty));

        // при клик на копчето за додавање производ
        holder.btnPlus.setOnClickListener(v -> {
            int newQty = cartQuantities.getOrDefault(product.getIme(), 0) + 1;
            cartQuantities.put(product.getIme(), newQty);
            holder.tvQuantity.setText(String.valueOf(newQty));
            if (listener != null) listener.onCartChanged(product.getIme(), newQty);
        });

        // при клик на копчето за одземање производ
        holder.btnMinus.setOnClickListener(v -> {
            int current = cartQuantities.getOrDefault(product.getIme(), 0);
            if (current > 0) {
                int newQty = current - 1;
                if (newQty == 0) {
                    cartQuantities.remove(product.getIme());
                } else {
                    cartQuantities.put(product.getIme(), newQty);
                }
                holder.tvQuantity.setText(String.valueOf(newQty));
                if (listener != null) listener.onCartChanged(product.getIme(), newQty);
            }
        });
    }

    @Override
    public int getItemCount() { return productList.size(); }

    public void clearCart() {
        cartQuantities.clear();
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity;
        ImageView ivImage;
        Button btnPlus, btnMinus;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // се поврзуваат соодветните views
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}