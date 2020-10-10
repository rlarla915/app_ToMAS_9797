package com.team9797.ToMAS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
public class signupactivity extends AppCompatActivity{

    private FirebaseAuth firebaseAuth;
    EditText editTextEmail;
    EditText editTextPassword;
    private String email = "";
    private String password = "";


    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.signup_form);
        editTextEmail = findViewById(R.id.et_email);
        editTextPassword = findViewById(R.id.et_password);

// ...
// Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

    }
    public void signUp(View view) {
        email = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();

        if(isValidEmail() && isValidPasswd()) {
            createUser(email, password);
        }
    }

    private boolean isValidEmail() {
        if (email.isEmpty()) {
            // 이메일 공백
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // 이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        if (password.isEmpty()) {
            // 비밀번호 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    private void createUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            updateuser();
                            Intent intent=new Intent(signupactivity.this,loginactivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            Toast.makeText(signupactivity.this, R.string.success_signup, Toast.LENGTH_SHORT).show();
                        } else {
                            // 회원가입 실패
                            Toast.makeText(signupactivity.this, R.string.failed_signup, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateuser(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        EditText edit_phone = findViewById(R.id.edit_phone);
        EditText edit_name = findViewById(R.id.edit_name);
        EditText et_email = findViewById(R.id.et_email);
        EditText et_password = findViewById(R.id.et_password);
        EditText edit_belong = findViewById(R.id.edit_belong);
        EditText edit_class = findViewById(R.id.edit_class);
        EditText edit_armynumber = findViewById(R.id.edit_armynumber);
        EditText edit_birth = findViewById(R.id.edit_birth);
        String phone = edit_phone.getText().toString();
        String name = edit_name.getText().toString();
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        String belong = edit_belong.getText().toString();
        String armyclass = edit_class.getText().toString();
        String armynumber = edit_armynumber.getText().toString();
        String birth = edit_birth.getText().toString();

        Map<String, Object> upload = new HashMap<>();
       upload.put("이름",name);
        upload.put("소속",belong);
        upload.put("권한","사용자");
        upload.put("군번",armynumber);
        upload.put("계급",armyclass);
        upload.put("point",0);
        upload.put("phonenumber",phone);
        upload.put("password",password);
        upload.put("email",email);
        upload.put("birth",birth);

        //사용자이름, 시간 등등 추가해야 함.

        //test

        db.collection("user").document(firebaseAuth.getCurrentUser().getUid())
                .set(upload)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("AAA", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AAA", "Error writing document", e);
                    }
                });


    }
}
