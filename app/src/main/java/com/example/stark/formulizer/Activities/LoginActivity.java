package com.example.stark.formulizer.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.stark.formulizer.R;
import com.example.stark.formulizer.Controllers.FormulizerClient;
import com.example.stark.formulizer.Models.GeneralResponseModel;
import com.example.stark.formulizer.Services.UserService;
import com.example.stark.formulizer.Utilities.PreferenceReader;
import com.example.stark.formulizer.Utilities.Validators;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private TextView signup;
    public  Button btnSignIn;
    private TextView account;
    private EditText email;
    private EditText password;
    private PreferenceReader prf;
    private FormulizerClient formulizerClient;
    MaterialDialog.Builder progress ;
    MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prf = new PreferenceReader(LoginActivity.this);
        if(!prf.getTOKEN().isEmpty()){
            Intent it = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(it);
        }
        formulizerClient = new FormulizerClient(LoginActivity.this);
        progress = new MaterialDialog.Builder(this)
                .title(R.string.login_dialog_title)
                .content(R.string.login_dialog_content)
                .progress(true,0);
        dialog = progress.build();
        setContentView(R.layout.activity_login);
        signup = (TextView)findViewById(R.id.signup);
       btnSignIn = (Button) findViewById(R.id.buttonsignin);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);


        if(!Validators.isConnected()){
            Toast.makeText(LoginActivity.this,"Your Not Connected to Intenet!!!! Please Check Connection!",Toast.LENGTH_LONG).show();
        }
       btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if(Validators.isConnected()){
                if(email.getText().toString().isEmpty()){
                    email.requestFocus();
                    email.setError(getResources().getString(R.string.error_email_required));
                }
                if(password.getText().toString().isEmpty()){
                    password.requestFocus();
                    password.setError(getResources().getString(R.string.error_password_required));
                }
                else if(!Validators.isValidEmail(email.getText().toString())){
                    email.setText("");
                    email.requestFocus();
                    email.setError(getResources().getString(R.string.error_invalid_email));
                }
                else {
                    dialog.show();
                    UserService userService = formulizerClient.getClient().create(UserService.class);
                    Call<GeneralResponseModel<String>> call = userService.login(email.getText().toString(),password.getText().toString());
                    call.enqueue(new Callback<GeneralResponseModel<String>>() {

                        @Override
                        public void onResponse(Call<GeneralResponseModel<String>> call, Response<GeneralResponseModel<String>> response) {
                            System.out.println(response.body().isSuccess());
                            System.out.println(response.body().getMsg());
                            if (response.body().isSuccess()) {
                                String token = response.body().getData().get(0);
                                System.out.println(token);
                                if (!token.equals("")) {
                                    prf.setTOKEN(token);
                                    showToast(getResources().getString(R.string.prompt_success_signin));
                                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(it);
                                    dialog.dismiss();
                                    finish();
                                }
                                else{
                                    showToast(getResources().getString(R.string.error_incorrect_login));
                                }
                            }
                            else{
                                showToast(getResources().getString(R.string.error_incorrect_login));
                            }
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<GeneralResponseModel<String>> call, Throwable t) {
                            showToast(getResources().getString(R.string.error_server_offline));
                            dialog.dismiss();
                        }
                    });
                }
                }
                else{
                    showToast(getResources().getString(R.string.error_internet_required));
                }
            }
        });
       /* signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(it);
            }
        });
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(it);
            }
        });*/

    }

    private void showToast(String message) {
       Toast.makeText(LoginActivity.this,message,Toast.LENGTH_LONG).show();
    }

}
