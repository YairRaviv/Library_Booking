package com.example.floorview;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class ChangeCredentialsActivity extends AppCompatActivity
{

    DatabaseReference RealTimeDB;
    FirebaseAuth AuthDB;
    String userId , email , password1 , password2 , id;
    EditText ID , Password_1 , Password_2 , Email;
    Button Change , Cancel;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        RealTimeDB = FirebaseDatabase.getInstance().getReference();
        AuthDB = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_credentials);
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("userId");
        ID = (EditText)findViewById(R.id.C_ID);
        Password_1 = (EditText)findViewById(R.id.C_Password1);
        Password_2 = (EditText)findViewById(R.id.C_Password2);
        Email = (EditText)findViewById(R.id.C_Email);
        Change = (Button)findViewById(R.id.Change);
        Cancel = (Button)findViewById(R.id.buttoncancel);
        Change.setOnClickListener(view ->
        {

            email = Email.getText().toString();
            password1 = Password_1.getText().toString();
            password2 = Password_2.getText().toString();
            id = ID.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password1))
            {
                Toast.makeText(ChangeCredentialsActivity.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
            }
            else if (password1.length() < 4)
            {
                Toast.makeText(ChangeCredentialsActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
            }
            else if (!password1.equals(password2))
            {
                Toast.makeText(ChangeCredentialsActivity.this, "Differente Passwords!", Toast.LENGTH_SHORT).show();
            }
            else
                {
                ChangeCredentials(email , password1 , id);
            }
        });
        Cancel.setOnClickListener(view ->
        {
            final User[] current_user = new User[1];
            RealTimeDB.child("Users").child(AuthDB.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
            {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task)
                {
                    if (!task.isSuccessful())
                    {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else
                    {
                        current_user[0] =  task.getResult().getValue(User.class);
                        assert current_user[0] != null;
                        navigateToNextScreen(current_user[0]._librarianCode);
                    }
                }
            });
        });
    }
    public void ChangeCredentials(String email, String password, String id)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //Update Autentication DB

        assert user != null;
        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(ChangeCredentialsActivity.this, "User email address updated.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(ChangeCredentialsActivity.this, "Exeption!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(ChangeCredentialsActivity.this, "User email address updated.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(ChangeCredentialsActivity.this, "Exeption!!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //Update RealTime DB
        //final String [] lc = new String[3];
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
                    String lc = current_user._librarianCode;
                    String tmp_id = current_user._id;
                    String tmp_mail = current_user._mail;
                    User newuser = new User(id,email,lc);
                    RealTimeDB.child("Users").child(userId).setValue(newuser).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(ChangeCredentialsActivity.this, "Done Successfully!", Toast.LENGTH_SHORT).show();
                                navigateToNextScreen(newuser._librarianCode);
                            }
                        }
                    });
                }
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void navigateToNextScreen(String userLc){
        String [] acceptableLibrarianCodes = getResources().getStringArray(R.array.librarianCodes);
        if(Arrays.stream(acceptableLibrarianCodes).anyMatch(code -> code.equals(userLc)))
        {
            //move to main screen - librarian
            Intent intent = new Intent(ChangeCredentialsActivity.this, LibrarianMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("userId",AuthDB.getCurrentUser().getUid());
            startActivity(intent);
            finish();
        }
        else
        {
            //move to main screen - student
            Intent intent = new Intent(ChangeCredentialsActivity.this, StudentMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            String i = AuthDB.getCurrentUser().getUid();
            intent.putExtra("userId",AuthDB.getCurrentUser().getUid());
            startActivity(intent);
            finish();
        }
    }
}