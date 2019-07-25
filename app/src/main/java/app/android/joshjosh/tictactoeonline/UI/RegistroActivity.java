package app.android.joshjosh.tictactoeonline.UI;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import app.android.joshjosh.tictactoeonline.R;
import app.android.joshjosh.tictactoeonline.model.User;

public class RegistroActivity extends AppCompatActivity {
    EditText etName,etEmail,etPassword;
    ProgressBar progressBarRegistro;
    Button btnRegistro;
    FirebaseAuth firebaseAuth;
    ScrollView scViewRegistro;
    //TODO
    FirebaseFirestore db;
    String email,password,name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etName = findViewById(R.id.editTextName);
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        btnRegistro = findViewById(R.id.buttonRegistro);
        progressBarRegistro = findViewById(R.id.progressBarRegistro);
        scViewRegistro = findViewById(R.id.formRegistro);
        //ABRIR LA BASE DE DATOS
        firebaseAuth = firebaseAuth.getInstance();
        //Abrimos una instancia de las bases de datos
        db = FirebaseFirestore.getInstance();

        changeRegistroFormVisibility(true);
        eventos();
    }

    private void eventos() {
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etName.getText().toString();
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();

                if(name.isEmpty()){
                    etName.setError("El Nombre es obligatorio");
                }else if(email.isEmpty()){
                    etEmail.setError("El email es obligatorio");
                }else if(password.isEmpty()){
                    etPassword.setError("La contraseña es obligatorio");
                }
                else{
                    //TODO:REALIZAR REGISTRO EN FIREBASE
                    createUser();
                }
            }
        });
    }

    private void createUser() {
        changeRegistroFormVisibility(false);
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);

                        }
                        else{
                            Toast.makeText(RegistroActivity.this,"Error en el registro",Toast.LENGTH_SHORT);
                            updateUI(null);
                        }
                    }
                });


    }

    private void updateUI(FirebaseUser user) {
        if(user != null){
            //Almacenar la informacion del usuario en FireStore
            User nuevoUsuario = new User(name,0,0);

            //ACCEDEMOS A LA COLECCION DE USUARIOS
            db.collection("users")
                    .document(user.getUid())
                    //A ese documento le agregamos los datos que espera la BD
                    .set(nuevoUsuario)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Navegar hacia la siguiente pantalla de la app
                            finish();
                            Intent i = new Intent(RegistroActivity.this,FindGameActivity.class);
                            startActivity(i);
                        }
                    });



        }
        else{
            changeRegistroFormVisibility(true);
            etPassword.setError("Nombre,Email y/o contraseña incorrectos");
            etPassword.requestFocus();
        }

    }
    private void changeRegistroFormVisibility(boolean showForm) {
        progressBarRegistro.setVisibility(showForm ? View.GONE : View.VISIBLE);
        scViewRegistro.setVisibility(showForm ? View.VISIBLE : View.GONE);
    }
}
