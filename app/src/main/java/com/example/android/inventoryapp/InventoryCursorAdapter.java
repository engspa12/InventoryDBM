package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;
import com.squareup.picasso.Picasso;

/**
 * Created by DBM on 6/25/2017.
 */

public class InventoryCursorAdapter extends CursorAdapter {


    public InventoryCursorAdapter(Context context, Cursor c){
        super(context,c,0 /* flags */);
    }

    @Override
    public View newView(Context context, final Cursor cursor, final ViewGroup viewGroup) {
        View newView = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);

       newView.findViewById(R.id.sale_button).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               //The method performItemClick will call the OnItemClickListener that is defined in the Main Activity
               //And it will send the view(of the SALE button), the position, and we set id=0 because we don't use the id in our code
              ((ListView) viewGroup).performItemClick(view, ((ListView) viewGroup).getPositionForView(view), 0);
       }
      });
        return newView;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        TextView productName = (TextView) view.findViewById(R.id.name_tv);
        TextView productQuantity = (TextView) view.findViewById(R.id.quantity_tv);
        TextView productPrice = (TextView) view.findViewById(R.id.price_tv);
        ImageView productImageView = (ImageView) view.findViewById(R.id.image_ref);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_NAME));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_QUANTITY));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_PRICE));

        String image_resource = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_IMAGE_URL));

        productName.setText(context.getString(R.string.product_name_detail,name));
        productQuantity.setText(context.getString(R.string.product_quantity_detail,quantity));
        productPrice.setText(context.getString(R.string.product_price_detail,price));

        Picasso.get().load(image_resource).resize(250,250).into(productImageView);

      //  view.findViewById(R.id.sale_button).setOnClickListener(new View.OnClickListener() {
      //  @Override
      //  public void onClick(View v) {
       //     ListView parent = ((ListView) (v.getParent()).getParent());
       //    parent.performItemClick(v, parent.getPositionForView(v), 0);
       //     System.out.println("The id inside bindView is: " + v.getId());
        //    System.out.println("The position inside bindView is: " + parent.getPositionForView(v));
        //  }
      //});
    }

}
