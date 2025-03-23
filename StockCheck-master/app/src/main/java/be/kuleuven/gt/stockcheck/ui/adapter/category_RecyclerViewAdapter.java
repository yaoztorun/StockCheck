package be.kuleuven.gt.stockcheck.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import be.kuleuven.gt.stockcheck.R;
import be.kuleuven.gt.stockcheck.data.model.categoryModel;

public class category_RecyclerViewAdapter extends RecyclerView.Adapter<category_RecyclerViewAdapter.MyViewHolder> {

    private final RecyclerViewInterfaceForCategory recyclerViewInterface;
    Context context;
    ArrayList<categoryModel> categoryModels;

    public category_RecyclerViewAdapter(Context context, ArrayList<categoryModel> categoryModels,
                                        RecyclerViewInterfaceForCategory recyclerViewInterface) {
        this.context = context;
        this.categoryModels = categoryModels;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public category_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This is where you inflate the layout (Giving a look to our rows)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new category_RecyclerViewAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull category_RecyclerViewAdapter.MyViewHolder holder, int position) {
        // Assigning values to the views we created in the recycler_view_row_layout file
        // based on the position of the recycler view
        holder.tvName.setText(categoryModels.get(position).getCategoryName());
        holder.tvItemCount.setText("(" + categoryModels.get(position).getItemCount() + ")");
        holder.imageView.setImageResource(categoryModels.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        // The recycler view just wants to know the number of items I want to display
        return categoryModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Grabbing the views from our recycler_view_row_layout file
        // Kinda like in the onCreate method

        ImageView imageView;
        TextView tvName;
        TextView tvItemCount;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterfaceForCategory recyclerViewInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView2);
            tvName = itemView.findViewById(R.id.tvName);
            tvItemCount = itemView.findViewById(R.id.tvItemCount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClickForCategory(pos);
                        }
                    }
                }
            });
        }
    }
}
