package be.kuleuven.gt.stockcheck;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements RecyclerViewInterface {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private String POST_URL = "https://studev.groept.be/api/a23PT201/sendItemDetails/";
    private String REMOVE_URL = "https://studev.groept.be/api/a23PT201/removeItemDetails/";
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
                } else if (itemId == R.id.navProfile) {
                    loadFragment(new ProfileFragment(), false);
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
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().isEmpty() || etPurch.getText().toString().isEmpty() ||
                        etSale.getText().toString().isEmpty() || etDate.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Details cannot be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                    StringRequest submitRequest = new StringRequest(
                            Request.Method.POST,
                            POST_URL,
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
                            return params;
                        }
                    };
                    requestQueue.add(submitRequest);
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
        EditText etUser = dialogView.findViewById(R.id.etUser);
        EditText etProductName = dialogView.findViewById(R.id.etProductName);
        EditText etProductQuantity = dialogView.findViewById(R.id.etProductQuantity);
        Button btnRemove = dialogView.findViewById(R.id.btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etUser.getText().toString().isEmpty() || etProductName.getText().toString().isEmpty() || etProductQuantity.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "User, Product Name, and Quantity cannot be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                    StringRequest removeRequest = new StringRequest(
                            Request.Method.POST,
                            REMOVE_URL,
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
                                    Log.e("RemoveError", "Error: " + error.toString());
                                    if (error.networkResponse != null) {
                                        Log.e("RemoveError", "Error Response Code: " + error.networkResponse.statusCode);
                                        if (error.networkResponse.data != null) {
                                            Log.e("RemoveError", "Error Response Data: " + new String(error.networkResponse.data));
                                        }
                                    }
                                    Toast.makeText(MainActivity.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("user", etUser.getText().toString());
                            params.put("name", etProductName.getText().toString());
                            params.put("quantity", etProductQuantity.getText().toString());
                            Log.e("RemoveParams", "Params: " + params.toString());
                            return params;
                        }
                    };
                    requestQueue.add(removeRequest);
                }
            }
        });
        removeDialog.show();
    }

    @Override
    public void onItemClick(int position) {
        // Check if the clicked position is the "All Items" category (assuming it is at position 0)
        if (position == 0) {
            loadFragment(new HomeFragment(), false);
        } else {
            loadFragment(CategoryFragment.newInstance(position), false);
        }
    }
}
