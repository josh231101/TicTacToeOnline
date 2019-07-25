package app.android.joshjosh.tictactoeonline.UI;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.android.joshjosh.tictactoeonline.R;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail,etPassword;
    private Button btnLogin,btnRegistro;
    private ScrollView formLogin;
    private ProgressBar pbLogin;
    private FirebaseAuth firebaseAuth;
    private String email,password;
    boolean tryLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Relacion entre los componentes de la interfaz y las variables
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);
        formLogin = findViewById(R.id.formLogin);
        pbLogin = findViewById(R.id.progressBarLogin);
        btnRegistro = findViewById(R.id.buttonRegistro);

        firebaseAuth = FirebaseAuth.getInstance();

        changeLoginFormVisibility(true);
        eventos();
    }

    private void eventos() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();

                if(email.isEmpty()){
                    etEmail.setError("El email es obligatorio");
                }else if(password.isEmpty()){
                    etPassword.setError("La contraseña es obligatorio");
                }
                else{
                    //TODO:REALIZAR LOGIN EN FIREBASE
                    changeLoginFormVisibility(false);
                    loginUser();
                }


            }
        });
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegistroActivity.class);
                startActivity(intent);

            }
        });

    }

    private void loginUser() {
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        tryLogin = true;
                        //TODO
                        if(task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            upadteUI(user);
                        }
                        else{
                            Log.w("TAG","SIGNINERROR: " + task.getException());
                            upadteUI(null);
                        }
                    }
                });
    }

    private void upadteUI(FirebaseUser user) {
        if(user != null){
            //Almacenar la informacion del usuario en FireStore
            //TODO



            //Navegar hacia la siguiente pantalla de la app
            Intent i = new Intent(LoginActivity.this,FindGameActivity.class);
            startActivity(i);
        }
        else{
            changeLoginFormVisibility(true);
            if(tryLogin){
                etPassword.setError("Email y/o contraseña incorrectos");
                etPassword.requestFocus();
            }

        }
    }

    private void changeLoginFormVisibility(boolean showForm) {
        pbLogin.setVisibility(showForm ? View.GONE : View.VISIBLE);
        formLogin.setVisibility(showForm ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Comprobamos si previamente el usuario ya ha iniciado sesión en el dispositivo
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        upadteUI(currentUser);
    }
}
