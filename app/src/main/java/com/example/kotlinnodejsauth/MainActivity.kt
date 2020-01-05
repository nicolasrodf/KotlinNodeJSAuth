package com.example.kotlinnodejsauth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.kotlinnodejsauth.retrofit.INodeJS
import com.example.kotlinnodejsauth.retrofit.RetrofitClient
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var myApi:INodeJS
    var compositeDisposable = CompositeDisposable()

    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Init API
        val retrofit = RetrofitClient.instance
        myApi = retrofit.create(INodeJS::class.java)

        login_button.setOnClickListener {

            if(!TextUtils.isEmpty(email_editText.text.toString()) && !TextUtils.isEmpty(password_editText.text.toString())) {
                //Check email field have correct syntax
                if (checkEmailSyntax(email_editText.text.toString()))
                    login(email_editText.text.toString(), password_editText.text.toString())
                else
                    Toast.makeText(this, "Email syntax is not valid", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "Email or password are empty", Toast.LENGTH_SHORT).show();
        }

        register_button.setOnClickListener {

            if(!TextUtils.isEmpty(email_editText.text.toString()) && !TextUtils.isEmpty(password_editText.text.toString())) {
                //Check email field have correct syntax
                if (checkEmailSyntax(email_editText.text.toString()))
                    register(email_editText.text.toString(), password_editText.text.toString())
                else
                    Toast.makeText(this, "Email syntax is not valid", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "Email or password are empty", Toast.LENGTH_SHORT).show();
        }
    }

    private fun checkEmailSyntax(email: String): Boolean {
        return email.trim().matches(emailPattern.toRegex())
    }

    private fun register(email: String, password: String) {

        //We need name to register user
        val enter_name_view = LayoutInflater.from(this).inflate(R.layout.enter_name_layout,null)

        MaterialStyledDialog.Builder(this)
            .setTitle("Register")
            .setDescription("One more step!")
            .setCustomView(enter_name_view)
            .setIcon(R.drawable.ic_user)
            .setNegativeText("Cancel")
            .onNegative { dialog, which -> dialog.dismiss() }
            .setPositiveText("Register")
            .onPositive { dialog, which ->

                val nameEditText = enter_name_view.findViewById<View>(R.id.name_editText) as EditText

                compositeDisposable.add(myApi.registerUser(email,nameEditText.text.toString(),password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    })

            }.show()
    }

    private fun login(email: String, password: String) {

        compositeDisposable.add(myApi.loginUser(email,password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                if(message.contains("encrypted_password")) //login method in API should return User object(JSON) so should contains "encrypted_password" field
                    Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            })

    }

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
