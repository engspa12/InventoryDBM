package com.example.android.inventoryapp.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.android.inventoryapp.data.ProductItem;
import com.example.android.inventoryapp.R;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ProductViewHolder> {

    private List<ProductItem> mProductItemList;
    private ItemClickListener mItemClickListener;
    private Context mContext;

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item, parent, false);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mProductItemList == null) {
            return 0;
        }
        return mProductItemList.size();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId, int clickedItemIndex, boolean isSaleButton);
    }

    public InventoryAdapter(Context context, ItemClickListener listener){
        mContext = context;
        mItemClickListener = listener;
    }

    public List<ProductItem> getProducts(){
        return mProductItemList;
    }


    public void setProducts(List<ProductItem> productItemList){
        mProductItemList = productItemList;
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{

        private TextView productName;
        private TextView productQuantity;
        private TextView productPrice;
        private ImageView productImageView;
        private Button saleButton;

        public ProductViewHolder(View itemView){
            super(itemView);

            productName = (TextView) itemView.findViewById(R.id.name_tv);
            productQuantity = (TextView) itemView.findViewById(R.id.quantity_tv);
            productPrice = (TextView) itemView.findViewById(R.id.price_tv);
            productImageView = (ImageView) itemView.findViewById(R.id.image_ref);
            saleButton = (Button) itemView.findViewById(R.id.sale_button);
            saleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int elementId = mProductItemList.get(getAdapterPosition()).getId();
                    int clickedPosition = getAdapterPosition();
                    mItemClickListener.onItemClickListener(elementId,clickedPosition, true);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int elementId = mProductItemList.get(getAdapterPosition()).getId();
                    int clickedPosition = getAdapterPosition();
                    mItemClickListener.onItemClickListener(elementId,clickedPosition, false);
                }
            });

        }

        public void bind(int position){
            ProductItem productItem = mProductItemList.get(position);

            String name = productItem.getName();
            int quantity = productItem.getQuantity();
            double price = productItem.getPrice();
            String image_resource = productItem.getUrlImage();

            productName.setText(mContext.getString(R.string.product_name_detail,name));
            productQuantity.setText(mContext.getString(R.string.product_quantity_detail,quantity));
            productPrice.setText(mContext.getString(R.string.product_price_detail,price));

            if(image_resource != null) {
                Glide.with(productImageView.getContext())
                        .load(image_resource)
                        .apply(new RequestOptions().centerCrop())
                        .into(productImageView);
            } else {
                Glide.with(productImageView.getContext())
                        .load(ResourcesCompat.getDrawable(itemView.getResources(), R.drawable.clothing,null))
                        .apply(new RequestOptions().centerCrop())
                        .into(productImageView);
            }
        }

    }

}
