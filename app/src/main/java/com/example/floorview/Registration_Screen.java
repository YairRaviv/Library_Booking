package com.example.floorview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Registration_Screen extends AppCompatActivity
{
    private DatabaseReference RealTimeDB;
    private FirebaseAuth AuthDB;
    EditText ID , Password , Email , Lecturercode;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        RealTimeDB = FirebaseDatabase.getInstance().getReference();
        AuthDB = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_screen);
        ID = (EditText)findViewById(R.id.ID);
        Password = (EditText)findViewById(R.id.Password);
        Email = (EditText)findViewById(R.id.Email);
        Lecturercode = (EditText)findViewById(R.id.Lecturer_code);

    }

    public void OnClickRegister(View view)
    {
        String id,mail,pass;
        int lecturercode;
        id = ID.getText().toString();
        mail = Email.getText().toString();
        pass = Password.getText().toString();
        // lecturer code should be a known one , so this condition will be : if(LecturerCodes.contains(Lecturercode.getText().toString()))
        if (Lecturercode.getText().toString() != null || Lecturercode.getText().toString() != "")
        {
            lecturercode = Integer.parseInt(Lecturercode.getText().toString());
        }
        else
        {
            lecturercode = 0;
        }


        //validation for credentials
        //if bla bla bla...
        register(id,mail,pass,lecturercode);


        //upload to fire base ..
    }

    public void register(final String Id ,final String mail ,String Pass ,final int L_C)
    {
        AuthDB.createUserWithEmailAndPassword(mail , Pass).addOnSuccessListener(new OnSuccessListener<AuthResult>()
        {

            @Override
            public void onSuccess(AuthResult authResult)
            {
                User user = new User(Id,mail,L_C);
                RealTimeDB.child("Users").child(AuthDB.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(Registration_Screen.this, "Update the profile ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Registration_Screen.this , Login_Registration_Screen.class);
                            Bundle b = new Bundle();
                            b.putInt("LC",L_C);
                            intent.putExtras(b);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(Registration_Screen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
