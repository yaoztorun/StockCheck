package be.kuleuven.gt.stockcheck.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.kuleuven.gt.stockcheck.Endpoints;
import be.kuleuven.gt.stockcheck.R;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogIn;
    private ProgressBar pgBar;
    private CheckBox cbShow;
    private TextView txtBtnRegister;
    private EditText txtInputUsername;
    private EditText txtInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        initializeUI();
        setEventListeners();
    }

    private void initializeUI() {
        cbShow = findViewById(R.id.cbShow);
        pgBar = findViewById(R.id.pgBarLogIn);
        btnLogIn = findViewById(R.id.btnLogIn);
        txtBtnRegister = findViewById(R.id.txtBtnRegister);
        txtInputPassword = findViewById(R.id.txtInputPassword);
        txtInputUsername = findViewById(R.id.txtInputUsername);
    }

    private void setEventListeners() {
        cbShow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                txtInputPassword.setTransformationMethod(null);
            } else {
                txtInputPassword.setTransformationMethod(new PasswordTransformationMethod());
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private boolean checkText() {
        return !txtInputUsername.getText().toString().isEmpty() && !txtInputPassword.getText().toString().isEmpty();
    }

    public void btnLogIn_clicked(View caller) {
        if (!checkText()) {
            showToast("Credentials cannot be empty!");
            toggleProgress(false);
            return;
        }

        toggleProgress(true);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                Endpoints.LOGIN_GET_URL.getUrl(),
                null,
                this::handleLoginResponse,
                this::handleErrorResponse
        );

        requestQueue.add(queueRequest);
    }

    private void handleLoginResponse(JSONArray response) {
        try {
            boolean userExists = false;
            boolean passwordCorrect = false;
            String username = txtInputUsername.getText().toString();
            String password = txtInputPassword.getText().toString();

            for (int i = 0; i < response.length(); i++) {
                JSONObject object = response.getJSONObject(i);
                String nameTemp = object.getString("username");
                String passTemp = object.getString("password");

                if (nameTemp.equals(username)) {
                    userExists = true;
                    if (passTemp.equals(password)) {
                        passwordCorrect = true;
                        break;
                    }
                }
            }

            processLoginResult(userExists, passwordCorrect, username);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleErrorResponse(VolleyError error) {
        showToast("Unable to communicate with the server!" + error);
        toggleProgress(false);
    }

    private void processLoginResult(boolean userExists, boolean passwordCorrect, String username) {
        toggleProgress(false);

        if (!userExists) {
            showToast("You don't have an account, Sign Up!");
            clearInputFields();
        } else if (!passwordCorrect) {
            showToast("Wrong password! Think harder!");
            txtInputPassword.setText("");
        } else {
            showToast("Welcome back, " + username);
            navigateToMainActivity(username);
        }
    }

    private void toggleProgress(boolean show) {
        pgBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogIn.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void clearInputFields() {
        txtInputUsername.setText("");
        txtInputPassword.setText("");
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToMainActivity(String username) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void onTxtBtnRegister_clicked(View caller) {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}
