package be.kuleuven.gt.stockcheck.ui.fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import be.kuleuven.gt.stockcheck.R;
import be.kuleuven.gt.stockcheck.data.items;

public class ItemDetailFragment extends Fragment {

    private static final String ARG_ITEM = "item";
    private items item;
    private String new_date;

    public static ItemDetailFragment newInstance(items item) {
        ItemDetailFragment fragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_detail, container, false);

        if (getArguments() != null) {
            item = (items) getArguments().getSerializable(ARG_ITEM);
        }

        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvQuantity = view.findViewById(R.id.tvQuantity);
        TextView tvExpDate = view.findViewById(R.id.tvExpDate);
        TextView tvPurchasePrice = view.findViewById(R.id.tvPurchasePrice);
        TextView tvSalePrice = view.findViewById(R.id.tvSalePrice);
        TextView tvProfit = view.findViewById(R.id.inputProf);
        TextView tvDaysLeft = view.findViewById(R.id.tvDayLeft);

        if (item != null) {
            tvName.setText(item.getName());
            tvQuantity.setText(String.valueOf(item.getQuantity()));
            if (item.getExp_date().contains(".")) {
                new_date = item.getExp_date().replace(".", "/");
                tvExpDate.setText(new_date);
            } else {
                new_date = item.getExp_date();
                tvExpDate.setText(new_date);
            }
            tvPurchasePrice.setText(String.valueOf(item.getPurchase_price()));
            tvSalePrice.setText(String.valueOf(item.getSale_price()));
            double profit = Math.ceil((item.getSale_price() - item.getPurchase_price()) * 10) / 10.0;
            tvProfit.setText("€" + String.valueOf(profit));
            if (item.getSale_price() - item.getPurchase_price() > 0) {
                tvProfit.setTextColor(Color.parseColor("#028A0F"));
            } else {
                tvProfit.setTextColor(Color.parseColor("#FF0000"));
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate exp_date = LocalDate.parse(new_date, formatter);
            LocalDate today = LocalDate.now();
            long daysLeft = ChronoUnit.DAYS.between(today, exp_date);
            tvDaysLeft.setText(String.valueOf(daysLeft));


        }




        return view;
    }
}