package be.kuleuven.gt.stockcheck;

import android.app.MediaRouteActionProvider;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private Button btnSignUp;
    private ProgressBar pgBar;
    private TextView txtBtnLogIn;
    private EditText txtInputUserSignUp;
    private EditText txtInputPasswSignUp1;
    private EditText txtInputPasswSignUp2;
    private static final String GET_URL = "https://studev.groept.be/api/a23PT201/getCredentials";
    private static final String POST_URL = "https://studev.groept.be/api/a23PT201/setCredentials/";
    private CheckBox cbShow2;
    private boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        cbShow2 = (CheckBox) findViewById(R.id.cbShow2);
        pgBar = (ProgressBar) findViewById(R.id.pgBarSign);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        txtBtnLogIn = (TextView) findViewById(R.id.txtBtnLogIn);
        txtInputPasswSignUp1 = (EditText) findViewById(R.id.txtInputPswdsgnup1);
        txtInputUserSignUp = (EditText) findViewById(R.id.txtInputUsrsgnup);
        txtInputPasswSignUp2 = (EditText) findViewById(R.id.txtInputPswdsgnup2);

        cbShow2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    txtInputPasswSignUp1.setTransformationMethod(null);
                    txtInputPasswSignUp2.setTransformationMethod(null);
                }
                else{
                    txtInputPasswSignUp1.setTransformationMethod(new PasswordTransformationMethod());
                    txtInputPasswSignUp2.setTransformationMethod(new PasswordTransformationMethod());

                }
            }


    });
    }


    public boolean checkText() {
        if (String.valueOf(txtInputUserSignUp.getText()).isEmpty() || String.valueOf(txtInputPasswSignUp1.getText()).isEmpty() || String.valueOf(txtInputPasswSignUp2.getText()).isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public void btnSignUp_clicked(View Caller) {
        pgBar.setVisibility(View.VISIBLE);
        btnSignUp.setVisibility(View.GONE);
        if (checkText()) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonArrayRequest queueRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    GET_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            boolean check = false;
                            String nameTemp;
                            String passTemp;
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = response.getJSONObject(i);
                                    nameTemp = object.getString("username");
                                    passTemp = object.getString("password");
                                    if (nameTemp.equals(String.valueOf(txtInputUserSignUp.getText()))) {
                                        check = true;
                                        break;
                                    }

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if (!check) {
                                if (String.valueOf(txtInputPasswSignUp1.getText()).equals(String.valueOf(txtInputPasswSignUp2.getText()))) {
                                    Toast.makeText(SignUpActivity.this, "Creating your Account!", Toast.LENGTH_SHORT).show();

                                    RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);
                                    StringRequest submitRequest = new StringRequest(
                                            Request.Method.POST,
                                            POST_URL,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    pgBar.setVisibility(View.GONE);
                                                    btnSignUp.setVisibility(View.VISIBLE);
                                                    Toast.makeText(SignUpActivity.this, "Your account is created!",
                                                            Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                    intent.putExtra("username", String.valueOf(txtInputUserSignUp.getText()));
                                                    startActivity(intent);
                                                }

                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    pgBar.setVisibility(View.GONE);
                                                    btnSignUp.setVisibility(View.VISIBLE);
                                                    Toast.makeText(SignUpActivity.this, "Unable to create your account :(", Toast.LENGTH_SHORT).show();

                                                }
                                            }) {
                                        @Override
                                        protected Map<String, String> getParams() {
                                            Map<String, String> params = new HashMap<>();
                                            params.put("username", String.valueOf(txtInputUserSignUp.getText()));
                                            params.put("password", String.valueOf(txtInputPasswSignUp1.getText()));
                                            return params;
                                        }
                                    };
                                    requestQueue.add(submitRequest);
                                }
                                else {
                                    Toast.makeText(SignUpActivity.this, "Passwords does not match!", Toast.LENGTH_SHORT).show();
                                    pgBar.setVisibility(View.GONE);
                                    btnSignUp.setVisibility(View.VISIBLE);

                                }
                                }
                            else {
                                Toast.makeText(SignUpActivity.this,"This username exists! Be creative!", Toast.LENGTH_SHORT).show();
                                pgBar.setVisibility(View.GONE);
                                btnSignUp.setVisibility(View.VISIBLE);




                            }
                        };
                    },
                    new Response.ErrorListener() {
                @Override
                public void onErrorResponse (VolleyError volleyError){
                    Toast.makeText(SignUpActivity.this, "Unable to communicate with the server!" + volleyError, Toast.LENGTH_LONG).show();
                    pgBar.setVisibility(View.GONE);
                    btnSignUp.setVisibility(View.VISIBLE);
                }
            });
            requestQueue.add(queueRequest);

        }else {
            Toast.makeText(SignUpActivity.this, "Credentials cannot be empty!", Toast.LENGTH_SHORT).show();
            pgBar.setVisibility(View.GONE);
            btnSignUp.setVisibility(View.VISIBLE);

        }

        }

        public void onTxtBtnLogIn_clicked (View Caller){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    }