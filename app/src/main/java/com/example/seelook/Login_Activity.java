package com.example.seelook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_Activity extends AppCompatActivity {

    private static final String TAG="Login_Activity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText et_user_email, et_user_password;
    private String getUserEmail;
    private String getUserPassword;
    private Button btn_login;
    private CheckBox auto_login;//체크 박
    private boolean saveLoginData;//로그인 저장
    private SharedPreferences appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        appData = getSharedPreferences("appData",MODE_PRIVATE);
        load();

        mAuth=FirebaseAuth.getInstance();
        //레이아웃이랑 연
        et_user_email=(EditText)findViewById(R.id.user_id);
        et_user_password=(EditText)findViewById(R.id.user_password);
        btn_login=(Button)findViewById(R.id.login_btn);
        auto_login=(CheckBox)findViewById(R.id.auto_login);

        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG,"onAuthStateChanged: signed_in: "+user.getUid());
                }
                else if(user==null){
                    Log.d(TAG,"onAuthStateChanged: signd_out: "+user.getUid());
                }
            }
        };
        //아이디 비밀 번호 찾기
        final TextView password_button = (TextView)findViewById(R.id.findpassword);
        password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),FindPassword_Activity.class);
                startActivity(intent);
            }
        });
        //회원 가입 글자
        TextView signin_button= (TextView)findViewById(R.id.signin);
        signin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Signin_Activity.class);
                startActivity(intent);
            }
        });

        if(saveLoginData){
            et_user_email.setText(getUserEmail);
            et_user_password.setText(getUserPassword);
            auto_login.setChecked(saveLoginData);
        }

        //로그인 버튼
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getUserEmail = et_user_email.getText().toString();
                getUserPassword = et_user_password.getText().toString();

                //이메일, 비밀번호가 공백이 아닌 경우
                if(!getUserEmail.equals("")&&!getUserPassword.equals("")){

                    UserLogin(getUserEmail,getUserPassword);
                }
                //공백인 경우
                else{
                    Toast.makeText(Login_Activity.this,"이메일과 비밀번호를 입력하세요",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void UserLogin(String email,String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG,"signInWithEmail:onComplete: "+task.isSuccessful());
                if(!task.isSuccessful()){
                    Log.w(TAG,"signInWithEmail:failed",task.getException());
                    Toast.makeText(Login_Activity.this,"로그인 실패",Toast.LENGTH_SHORT).show();
                }
                else if(task.isSuccessful()){
                    Toast.makeText(Login_Activity.this,"로그인 성공",Toast.LENGTH_SHORT).show();
                    save();
                    Intent intent = new Intent(
                            getApplicationContext(),
                            Home_Activity1.class);
                    startActivity(intent);
                }
            }
        });
    }
    public void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    public void onStop(){
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private  void save(){
        SharedPreferences.Editor editor = appData.edit();
        editor.putBoolean("SAVE_LOGIN_DATA",auto_login.isChecked());
        editor.putString("email",getUserEmail);
        editor.putString("pw",getUserPassword);
        editor.apply();
    }
    private void load(){
        //SharedPreference 객체.get타입(저장된 이름, 기본 값)
        //저장된 이름이 존재하지 않을 시 기본 값
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA",false);
        getUserEmail=appData.getString("email","");
        getUserPassword=appData.getString("pw","");
    }
}