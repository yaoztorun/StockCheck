package be.kuleuven.gt.stockcheck.ui.adapter;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import be.kuleuven.gt.stockcheck.R;
import be.kuleuven.gt.stockcheck.data.items;

public class items_recyclerViewAdapter extends RecyclerView.Adapter<items_recyclerViewAdapter.MyViewHolder> {
    ArrayList<items> itemsArrayList;
    private final RecyclerViewInterface recyclerViewInterface;
    private final RecyclerViewInterfaceRemove recyclerViewInterfaceRemove;

    public items_recyclerViewAdapter(ArrayList<items> itemsArrayList, RecyclerViewInterface recyclerViewInterface, RecyclerViewInterfaceRemove recyclerViewInterfaceRemove) {
        this.itemsArrayList = itemsArrayList;
        this.recyclerViewInterface = recyclerViewInterface;
        this.recyclerViewInterfaceRemove = recyclerViewInterfaceRemove;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_items, parent, false);
        return new MyViewHolder(view, recyclerViewInterface, recyclerViewInterfaceRemove);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvName.setText(itemsArrayList.get(position).getName());
        holder.tvQuantity.setText(String.valueOf(itemsArrayList.get(position).getQuantity()));
        holder.tvExp_date.setText(itemsArrayList.get(position).getExp_date());
        holder.tvPurchase_price.setText(String.valueOf(itemsArrayList.get(position).getPurchase_price()));
        holder.tvSale_price.setText(String.valueOf(itemsArrayList.get(position).getSale_price()));
        if (itemsArrayList.get(position).getQuantity() < itemsArrayList.get(position).getThreshold()) {
            holder.imgTH.setVisibility(View.VISIBLE);
        }

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recyclerViewInterfaceRemove != null){
                    int pos = holder.getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        recyclerViewInterfaceRemove.onItemRemove(itemsArrayList.get(pos));
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvQuantity;
        TextView tvExp_date;
        TextView tvPurchase_price;
        TextView tvSale_price;
        ImageView imgTH;
        ImageView btnDelete;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface, RecyclerViewInterfaceRemove recyclerViewInterfaceRemove) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvQuantity = itemView.findViewById(R.id.tvQuaVal);
            tvExp_date = itemView.findViewById(R.id.tvDateVal);
            tvPurchase_price = itemView.findViewById(R.id.tvPurchVal);
            tvSale_price = itemView.findViewById(R.id.tvSaleVal);
            imgTH = itemView.findViewById(R.id.imgTH);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(itemsArrayList.get(pos));

                        }
                    }
                }
            });
        }
    }
}
