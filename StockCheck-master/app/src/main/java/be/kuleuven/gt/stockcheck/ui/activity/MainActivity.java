package be.kuleuven.gt.stockcheck.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import be.kuleuven.gt.stockcheck.Endpoints;
import be.kuleuven.gt.stockcheck.R;
import be.kuleuven.gt.stockcheck.data.items;
import be.kuleuven.gt.stockcheck.ui.adapter.RecyclerViewInterface;
import be.kuleuven.gt.stockcheck.ui.adapter.RecyclerViewInterfaceForCategory;
import be.kuleuven.gt.stockcheck.ui.adapter.RecyclerViewInterfaceRemove;
import be.kuleuven.gt.stockcheck.ui.fragment.CategoryFragment;
import be.kuleuven.gt.stockcheck.ui.fragment.HomeFragment;
import be.kuleuven.gt.stockcheck.ui.fragment.ItemDetailFragment;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface, RecyclerViewInterfaceForCategory, RecyclerViewInterfaceRemove {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private String username;
    private int savedCategoryPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        frameLayout = findViewById(R.id.frameLayout);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navHome) {
                    loadFragment(new HomeFragment(), false);  // Load HomeFragment for Home navigation
                } else if (itemId == R.id.navAdd) {
                    onAddClick(null);
                } else if (itemId == R.id.navMinus) {
                    onRemoveClick(null);
                }

                return true;
            }
        });

        // Initial fragment load
        loadFragment(new HomeFragment(), true);
        username = getIntent().getStringExtra("username");
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frameLayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }
        fragmentTransaction.commit();
    }

    public void onAddClick(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.item_add_dialog, null);
        dialogBuilder.setView(dialogView);
        AlertDialog addDialog = dialogBuilder.create();
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etPurch = dialogView.findViewById(R.id.etPurch);
        EditText etSale = dialogView.findViewById(R.id.etSale);
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etQuan = dialogView.findViewById(R.id.etQuan);
        Spinner spType = dialogView.findViewById(R.id.spType);
        String username = getIntent().getStringExtra("username");
        Button btnSend = dialogView.findViewById(R.id.btnSend);
        EditText etThresh = dialogView.findViewById(R.id.etThresh);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().isEmpty() || etPurch.getText().toString().isEmpty() ||
                        etSale.getText().toString().isEmpty() || etDate.getText().toString().isEmpty() || etThresh.getText().toString().isEmpty()
                        || etQuan.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Details cannot be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

                    JsonArrayRequest queueRequest = new JsonArrayRequest(
                            Request.Method.GET,
                            Endpoints.CHECK_EXISTING_ITEMS.getUrl()+ username,
                            null,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    boolean exists = false;
                                    String nameTemp;
                                    for (int i = 0; i < response.length(); i++) {
                                        try {
                                            JSONObject object = response.getJSONObject(i);
                                            nameTemp = object.getString("name");

                                            if (nameTemp.equals(etName.getText().toString())) {
                                                exists = true;
                                                break;
                                            }
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }

                                    if (exists) {
                                        Toast.makeText(MainActivity.this, "Item already exists!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        StringRequest submitRequest = new StringRequest(
                                                Request.Method.POST,
                                                Endpoints.MAIN_POST_URL.getUrl(),
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        Toast.makeText(MainActivity.this, "Your data has been sent!", Toast.LENGTH_SHORT).show();
                                                        addDialog.dismiss();
                                                        loadFragment(new HomeFragment(), false);
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Toast.makeText(MainActivity.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }) {
                                            @Override
                                            protected Map<String, String> getParams() {
                                                Map<String, String> params = new HashMap<>();
                                                params.put("user", username);
                                                params.put("typeamk", spType.getSelectedItem().toString());
                                                params.put("name", etName.getText().toString());
                                                params.put("quantity", etQuan.getText().toString());
                                                params.put("expdate", etDate.getText().toString());
                                                params.put("purchaseprice", etPurch.getText().toString());
                                                params.put("saleprice", etSale.getText().toString());
                                                params.put("threshold", etThresh.getText().toString());
                                                return params;
                                            }
                                        };
                                        requestQueue.add(submitRequest);
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    Toast.makeText(MainActivity.this, "Unable to communicate with the server!" + volleyError, Toast.LENGTH_LONG).show();
                                }
                            }
                    );
                    requestQueue.add(queueRequest);
                }
            }
        });
        addDialog.show();
    }


    public void onRemoveClick(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.item_remove_dialog, null);
        dialogBuilder.setView(dialogView);
        AlertDialog removeDialog = dialogBuilder.create();
        EditText etProductName = dialogView.findViewById(R.id.etProductName);
        EditText etProductQuantity = dialogView.findViewById(R.id.etProductQuantity);
        Button btnDecrement = dialogView.findViewById(R.id.btnDecrement);
        Button btnIncrement = dialogView.findViewById(R.id.btnIncrement);
        Button btnRemove = dialogView.findViewById(R.id.btnRemove);
        Switch swAddRemove = dialogView.findViewById(R.id.swAddRemove);


        btnDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(etProductQuantity.getText().toString());
                if (quantity > 1) {
                    quantity--;
                    etProductQuantity.setText(String.valueOf(quantity));
                }
            }
        });
        swAddRemove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    btnRemove.setText("Add");
                    swAddRemove.setText("Click to set Remove");
                } else {
                    btnRemove.setText("Remove");
                    swAddRemove.setText("Click to set Add");
                }
            }
        });
        btnIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(etProductQuantity.getText().toString());
                quantity++;
                etProductQuantity.setText(String.valueOf(quantity));
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etProductName.getText().toString().isEmpty() || etProductQuantity.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Product Name and Quantity cannot be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);


                    JsonArrayRequest queueRequest = new JsonArrayRequest(
                            Request.Method.GET,
                            Endpoints.CHECK_QUANTITY_BELOW_ZERO.getUrl()+ username + "/" + etProductName.getText().toString(),
                            null,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        Log.d("url", Endpoints.CHECK_QUANTITY_BELOW_ZERO.getUrl()+ username + "/" + etProductName.getText().toString());
                                        JSONObject object = response.getJSONObject(0);
                                        int currentQuantity = object.getInt("quantity");
                                        int inputQuantity = Integer.parseInt(etProductQuantity.getText().toString());

                                        if (btnRemove.getText().equals("Remove") && currentQuantity < inputQuantity) {
                                            Toast.makeText(MainActivity.this, "Existing quantity cannot be set below zero!", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            StringRequest removeRequest = new StringRequest(
                                                    Request.Method.POST,
                                                    Endpoints.MAIN_REMOVE_URL.getUrl(),
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            Toast.makeText(MainActivity.this, "Quantity has been updated!", Toast.LENGTH_SHORT).show();
                                                            removeDialog.dismiss();
                                                            loadFragment(new HomeFragment(), false);
                                                        }
                                                    },
                                                    new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            // Log the error response for debugging
                                                            Toast.makeText(MainActivity.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }) {
                                                @Override
                                                protected Map<String, String> getParams() {
                                                    Map<String, String> params = new HashMap<>();
                                                    if( btnRemove.getText().equals("Add")){
                                                        params.put("quantity",etProductQuantity.getText().toString());
                                                    }
                                                    else{
                                                        params.put("quantity","-" + etProductQuantity.getText().toString());
                                                    }
                                                    params.put("user", username);
                                                    params.put("name", etProductName.getText().toString());
                                                    Log.e("RemoveParams", "Params: " + params.toString());
                                                    return params;
                                                }
                                            };
                                            requestQueue.add(removeRequest);
                                        }
                                    } catch (JSONException | NumberFormatException e) {
                                        Toast.makeText(MainActivity.this, "Error parsing response!", Toast.LENGTH_SHORT).show();
                                        Log.e("QueueError", "Error: " + e.toString());
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Log the error response for debugging
                                    Toast.makeText(MainActivity.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    requestQueue.add(queueRequest);
                }
            }
        });
        removeDialog.show();
    }
    @Override
    public void onItemClickForCategory(int position) {
        // Check if the clicked position is the "All Items" category (assuming it is at position 0)
        if (position == 0) {
            loadFragment(new HomeFragment(), false);
        } else {
            loadFragment(CategoryFragment.newInstance(position), false);
        }
    }

    private void removeFromList(items item) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest submitRequest = new StringRequest(
                Request.Method.POST,
                Endpoints.REMOVE_ITEM_URL.getUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(
                                MainActivity.this,
                                "Deleting the item...",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                MainActivity.this,
                                "Cannot delete the item!" + error,
                                Toast.LENGTH_LONG).show();
                    }
                }
        ) { //NOTE THIS PART: here we are passing the POST parameters to the webservice
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user", username);
                params.put("name",String.valueOf(item.getName()));
                return params;
            }
        };
        requestQueue.add(submitRequest);
    }
    @Override
    public void onItemClick(items item) {
        // Load the ItemDetailFragment when a RecyclerView item is clicked
        Fragment itemDetailFragment = ItemDetailFragment.newInstance(item);
        loadFragment(itemDetailFragment, false);
    }



    @Override
    public void onItemRemove(items item) {
        removeFromList(item);
        loadFragment(new HomeFragment(), false);
    }
}
