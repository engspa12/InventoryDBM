package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by DBM on 6/25/2017.
 */

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
                    //Toast.makeText(mContext, "The sale button was pressed, " + "itemId: " + mProductItemList.get(getAdapterPosition()).getId() + " - " + "position: " + getAdapterPosition(),Toast.LENGTH_SHORT).show();
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

            Picasso.get().load(image_resource).resize(250,250).into(productImageView);
        }

    }

}
