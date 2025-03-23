package be.kuleuven.gt.stockcheck;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class items_recyclerViewAdapter extends RecyclerView.Adapter<items_recyclerViewAdapter.MyViewHolder> {
    ArrayList<items> itemsArrayList;
    public items_recyclerViewAdapter(ArrayList<items> itemsArrayList){
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public items_recyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //This is where you inflate the layout (Giving a look to our rows)
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_items,parent,false);
        return new items_recyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull items_recyclerViewAdapter.MyViewHolder holder, int position) {
        //assigning values to the views we created in the recycler_view_row_layout file
        //based on the position of the recycler view
        holder.tvName.setText(itemsArrayList.get(position).getName());
        holder.tvQuantity.setText(String.valueOf(itemsArrayList.get(position).getQuantity()));
        holder.tvExp_date.setText(itemsArrayList.get(position).getExp_date());
        holder.tvPurchase_price.setText(String.valueOf(itemsArrayList.get(position).getPurchase_price()));
        holder.tvSale_price.setText(String.valueOf(itemsArrayList.get(position).getSale_price()));


    }

    @Override
    public int getItemCount() {
        //the recycler view just wants to know the number of items I want to display
        //what data to update each view
        return itemsArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        //grabbıng the views from our recycler_view_row_layout file
        //kinda like in the onCreate method

        TextView tvName;
        TextView tvQuantity;
        TextView tvExp_date;
        TextView tvPurchase_price;
        TextView tvSale_price;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvQuantity = itemView.findViewById(R.id.tvQuaVal);
            tvExp_date = itemView.findViewById(R.id.tvDateVal);
            tvPurchase_price = itemView.findViewById(R.id.tvPurchVal);
            tvSale_price = itemView.findViewById(R.id.tvSaleVal);
        }
    }
}
