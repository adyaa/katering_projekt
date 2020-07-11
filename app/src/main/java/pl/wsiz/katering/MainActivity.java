package pl.wsiz.katering;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.FirebaseAppLifecycleListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.reactivex.disposables.CompositeDisposable;
import pl.wsiz.katering.Common.Common;
import pl.wsiz.katering.Model.UserModel;


public class MainActivity extends AppCompatActivity {

    private static int APP_REQUEST_CODE = 7171;  //any number
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private AlertDialog dialog;
    private CompositeDisposable disposable = new CompositeDisposable();

    private DatabaseReference userRef;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if (listener != null)
            firebaseAuth.removeAuthStateListener(listener);
        disposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Init();
    }

    private void Init() {
        providers= Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        userRef= FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCES);
        firebaseAuth = FirebaseAuth.getInstance();
        //firebaseAuth.setLanguageCode("pl-PL");
        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //Account is already logged in
                    CheckUserFromFirebase(user);
                    // Toast.makeText(MainActivity.this, "Already Logged In", Toast.LENGTH_SHORT).show();
                } else {
                    phoneLogIn();

                }
            }

        };
    }

    private void CheckUserFromFirebase(FirebaseUser user) {
        dialog.show();
        userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    UserModel userModel= dataSnapshot.getValue(UserModel.class);
                    goToHomeActivity(userModel);
                }
                else{
                    showRegisterDialog(user);
                }
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, ""+ databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRegisterDialog(FirebaseUser user)
    {
        androidx.appcompat.app.AlertDialog.Builder builder= new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Rejestracja");
        builder.setMessage("Wypełnij informacje o użytkowniku");

        View itemView= LayoutInflater.from(this).inflate(R.layout.layout_register, null);
        EditText edt_name= (EditText)itemView.findViewById(R.id.edt_name);
        EditText edt_address= (EditText)itemView.findViewById(R.id.edt_address);
        EditText edt_phone= (EditText)itemView.findViewById(R.id.edt_phone);

        //set data
        edt_phone.setText(user.getPhoneNumber());



        builder.setNegativeButton("Anuluj", (dialogInterface, which) -> {
            dialogInterface.dismiss();
        });

        builder.setPositiveButton("Zarejestruj", (dialogInterface, which) -> {
            if(TextUtils.isEmpty(edt_name.getText().toString()))
            {
                Toast.makeText(this, "Podaj imię", Toast.LENGTH_SHORT).show();
                return;
            }
            else  if(TextUtils.isEmpty(edt_address.getText().toString()))
            {
                Toast.makeText(this, "Podaj adres", Toast.LENGTH_SHORT).show();
                return;
            }

            UserModel userModel= new UserModel();
            // userModel.getUid();
            userModel.setUid(user.getUid());
            userModel.setName(edt_name.getText().toString());
            userModel.setAddress(edt_address.getText().toString());
            userModel.setPhone(edt_phone.getText().toString());

            userRef.child(user.getUid()).setValue(userModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                dialogInterface.dismiss();

                                Toast.makeText(MainActivity.this, "Zarejestrowano pomyślnie", Toast.LENGTH_SHORT).show();
                                goToHomeActivity(userModel);
                            }

                        }
                    });

        });

        builder.setView(itemView);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
        startActivity(new Intent(MainActivity.this,HomeActivity.class));
        finish();
    }

    private void goToHomeActivity(UserModel userModel) {

        Common.currentUser = userModel;
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
        finish();
    }

    private void phoneLogIn() {

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),APP_REQUEST_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) {
            IdpResponse response= IdpResponse.fromResultIntent(data);
            if(resultCode== RESULT_OK)
            {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
            }
            else
            {
                Toast.makeText(this, "Nie udało się zalogować", Toast.LENGTH_SHORT).show();
            }
        }
    }



}