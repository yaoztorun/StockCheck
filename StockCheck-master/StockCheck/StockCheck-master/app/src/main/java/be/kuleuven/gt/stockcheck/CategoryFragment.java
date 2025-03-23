package be.kuleuven.gt.stockcheck;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoryFragment extends Fragment {
    private static final String ARG_CATEGORY_INDEX = "category_index";
    private int categoryIndex;
    private ArrayList<items> itemsArrayList = new ArrayList<>();
    private ArrayList<categoryModel> categoryModels = new ArrayList<>();
    private RecyclerView itemRecyclerView;
    private RecyclerView categoryRecyclerView;
    private TextView tvNoItem;
    private String username;
    private boolean created;
    private int[] categoryImages = {
            R.drawable.allitems,
            R.drawable.beverages,
            R.drawable.fruits,
            R.drawable.meat,
            R.drawable.snacks,
            R.drawable.milk,
            R.drawable.icecream,
            R.drawable.food
    };

    public static CategoryFragment newInstance(int categoryIndex) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_INDEX, categoryIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryIndex = getArguments().getInt(ARG_CATEGORY_INDEX);
        }
        username = getActivity().getIntent().getStringExtra("username");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        itemRecyclerView = view.findViewById(R.id.itemRecyclerView);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        tvNoItem = view.findViewById(R.id.tvNoItem);
        setupCategoryRecyclerView();
        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        String category = getCategoryName(categoryIndex);
        String url = "https://studev.groept.be/api/a23PT201/getItemsByCategory/" + category + "/" + username;
        Log.d("CategoryFragment", "Fetching items for category: " + category + " with URL: " + url);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("CategoryFragment", "Response received for category: " + category + " -> " + response.toString());
                        if (response.length() == 0) {
                            created = false;
                            tvNoItem.setVisibility(View.VISIBLE);
                        } else {
                            created = true;
                            tvNoItem.setVisibility(View.GONE);
                            processJSONResponse(response);
                            items_recyclerViewAdapter adapterItem = new items_recyclerViewAdapter(itemsArrayList);
                            itemRecyclerView.setAdapter(adapterItem);
                            itemRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("CategoryFragment", "Error fetching items for category: " + category + " -> " + error.toString());
                        if (error.networkResponse != null) {
                            Log.e("CategoryFragment", "Error response code: " + error.networkResponse.statusCode);
                            Log.e("CategoryFragment", "Error response data: " + new String(error.networkResponse.data));
                        }
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void fetchCategoryItemCounts(final VolleyCallback callback) {
        String url = "https://studev.groept.be/api/a23PT201/getCategoryItemCounts/" + username;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Map<String, Integer> itemCounts = new HashMap<>();
                        ArrayList<Integer> countList = new ArrayList<>();
                        String categoryName = "";
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject category = response.getJSONObject(i);
                                categoryName = category.getString("category");
                                countList.add(category.getInt("count"));
                            }

                                int count = 0;
                                for(int a = 0; a<countList.size();a++){
                                    count += countList.get(a);
                                }

                                itemCounts.put(categoryName, count);

                            callback.onSuccess(itemCounts);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void setUpCategoryModels(Map<String, Integer> itemCounts) {
        String[] categoryNames = getResources().getStringArray(R.array.categories_txt);
        for (int i = 0; i < categoryNames.length; i++) {

                int itemCount = itemCounts.getOrDefault(categoryNames[i], 0);
                categoryModels.add(new categoryModel(categoryNames[i], categoryImages[i], itemCount));

        }
    }

    private void setupCategoryRecyclerView() {
        fetchCategoryItemCounts(new VolleyCallback() {
            @Override
            public void onSuccess(Map<String, Integer> itemCounts) {
                setUpCategoryModels(itemCounts);
                category_RecyclerViewAdapter adapter = new category_RecyclerViewAdapter(getActivity(), categoryModels, new RecyclerViewInterface() {
                    @Override
                    public void onItemClick(int position) {
                        ((MainActivity) getActivity()).onItemClick(position);
                    }
                });
                categoryRecyclerView.setAdapter(adapter);
                categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            }
        });
    }

    private void processJSONResponse(JSONArray response) {
        itemsArrayList.clear();
        try {
            for (int i = 0; i < response.length(); i++) {
                items item = new items(response.getJSONObject(i));
                itemsArrayList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCategoryName(int index) {
        String[] categories = getResources().getStringArray(R.array.categories_txt);
        return categories[index];
    }

    public interface VolleyCallback {
        void onSuccess(Map<String, Integer> itemCounts);
    }
}
