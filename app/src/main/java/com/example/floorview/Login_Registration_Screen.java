package com.example.floorview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Login_Registration_Screen extends AppCompatActivity
{
    private Button OnClickLogin;
    private Button OnClickRegister;

    private DatabaseReference RealTimeDB;
    private FirebaseAuth AuthDB;
    EditText TxtUserName;
    EditText TxtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OnClickLogin = (Button)findViewById(R.id.button_login);
        OnClickRegister = (Button)findViewById(R.id.button3_register);
        TxtUserName = (EditText)findViewById(R.id.TxtUserName);
        TxtPassword = (EditText)findViewById(R.id.TxtPassword);
        RealTimeDB = FirebaseDatabase.getInstance().getReference();
        AuthDB = FirebaseAuth.getInstance();

        OnClickLogin.setOnClickListener(view ->
        {

            String email = TxtUserName.getText().toString();
            String password = TxtPassword.getText().toString();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(Login_Registration_Screen.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
            }else if (password.length() < 4){
                Toast.makeText(Login_Registration_Screen.this, "Password too short!", Toast.LENGTH_SHORT).show();
            }
            else {
                LoginUser(email , password);
            }

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || !(email.contains("@")))
            {
                Toast.makeText(Login_Registration_Screen.this, "Wrong Credentials!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                LoginUser(email , password);
            }

        });
        OnClickRegister.setOnClickListener(view ->
        {
            Intent intent = new Intent(Login_Registration_Screen.this , Registration_Screen.class);
            startActivity(intent);
        });
    }

//    public void OnClickLogin(View view)
//    {
//        String mail,pass;
//        mail = TxtUserName.getText().toString();
//        pass = TxtPassword.getText().toString();
//        System.out.println("mail is : "+mail+"\npass is :"+pass);
//        //validation // if bla bla bla ...
//        LoginUser(mail,pass);
//    }
//
//    public void OnClickRegister(View view)
//    {
//        Intent intent = new Intent(Login_Registration_Screen.this , Registration_Screen.class);
//        startActivity(intent);
//    }

    public void LoginUser( String mail ,  String pass)
    {
        AuthDB.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    Toast.makeText(Login_Registration_Screen.this, "successfully log in " , Toast.LENGTH_SHORT).show();

                    final int[] lcs = new int[1];
                    RealTimeDB.child("Users").child(AuthDB.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task)
                        {
                            if (!task.isSuccessful())
                            {
                                Log.e("firebase", "Error getting data", task.getException());
                            }
                            else
                            {
                                User current_user =  task.getResult().getValue(User.class);
                                assert current_user != null;
                                lcs[0] = current_user._librarianCode;
                            }
                        }
                    });
                    int lc = lcs[0];
                    if(lc!=0)
                    {
                        //move to main screen - librarian
                        Intent intent = new Intent(Login_Registration_Screen.this, LibrarianMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("userId",AuthDB.getCurrentUser().getUid());
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        //move to main screen - student
                        Intent intent = new Intent(Login_Registration_Screen.this, StudentMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("userId",AuthDB.getCurrentUser().getUid());
                        startActivity(intent);
                        finish();
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(Login_Registration_Screen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}