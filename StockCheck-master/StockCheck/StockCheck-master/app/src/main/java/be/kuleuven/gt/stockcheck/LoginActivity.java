package be.kuleuven.gt.stockcheck;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogIn;
    private ProgressBar pgBar;
    private CheckBox cbShow;
    private TextView txtBtnRegister;
    private EditText txtInputUsername;
    private EditText txtInputPassword;
    private static final String  GET_URL = "https://studev.groept.be/api/a23PT201/getCredentials";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        cbShow = (CheckBox) findViewById(R.id.cbShow);
        pgBar = (ProgressBar) findViewById(R.id.pgBarLogIn);
        btnLogIn = (Button) findViewById(R.id.btnLogIn);
        txtBtnRegister = (TextView) findViewById(R.id.txtBtnRegister);
        txtInputPassword = (EditText) findViewById(R.id.txtInputPassword);
        txtInputUsername = (EditText) findViewById(R.id.txtInputUsername);

        cbShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    txtInputPassword.setTransformationMethod(null);
                }
                else{
                    txtInputPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        }
    public boolean checkText() {
        return !txtInputUsername.getText().toString().isEmpty() && !txtInputPassword.getText().toString().isEmpty();
    }


    public void btnLogIn_clicked(View Caller){
        pgBar.setVisibility(View.VISIBLE);
        btnLogIn.setVisibility(View.GONE);
        if(checkText()){
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonArrayRequest queueRequest;
            queueRequest = new JsonArrayRequest(Request.Method.GET,
                    GET_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    boolean check = false;
                    boolean passwordCheck = false;
                    String nameTemp = "";
                    String passTemp;
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);
                            nameTemp = object.getString("username");
                            passTemp = object.getString("password");
                            if (nameTemp.equals(String.valueOf(txtInputUsername.getText())) && passTemp.equals(String.valueOf(txtInputPassword.getText()))) {
                                check = true;
                                passwordCheck = true;
                                break;
                            }
                            else if(nameTemp.equals(String.valueOf(txtInputUsername.getText()))){
                                check = true;
                                passwordCheck = false;
                                break;
                            }


                            } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    if(!check && !passwordCheck)
                    {
                        txtInputPassword.setText("");
                        txtInputUsername.setText("");
                        pgBar.setVisibility(View.GONE);
                        btnLogIn.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this,"You don't have an account, Sign Up!", Toast.LENGTH_SHORT).show();
                    }
                    else if(!passwordCheck){
                        txtInputPassword.setText("");
                        pgBar.setVisibility(View.GONE);
                        btnLogIn.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this,"Wrong password! Think harder!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(LoginActivity.this,"Welcome back, "+nameTemp,Toast.LENGTH_SHORT).show();
                        pgBar.setVisibility(View.GONE);
                        btnLogIn.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", String.valueOf(txtInputUsername.getText()));
                        startActivity(intent);
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse (VolleyError volleyError){
                            Toast.makeText(LoginActivity.this, "Unable to communicate with the server!" + volleyError, Toast.LENGTH_LONG).show();
                            pgBar.setVisibility(View.GONE);
                            btnLogIn.setVisibility(View.VISIBLE);
                        }
                    });
            requestQueue.add(queueRequest);
            }
        else{
            Toast.makeText(LoginActivity.this, "Credentials cannot be empty!", Toast.LENGTH_SHORT).show();
            pgBar.setVisibility(View.GONE);
            btnLogIn.setVisibility(View.VISIBLE);
        }
        }

public void onTxtBtnRegister_clicked(View Caller){
    Intent intent = new Intent(this, SignUpActivity.class);
    startActivity(intent);
}

}

