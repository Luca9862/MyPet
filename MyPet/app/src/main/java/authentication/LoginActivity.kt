package authentication

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import it.pdm.app.MainActivity
import it.pdm.app.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val SHARED_PREFS = "sharedPrefs"
    private lateinit var emailTV: TextView
    private lateinit var passwordTV: TextView
    private lateinit var logbutton: Button
    private lateinit var signinTV: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var fpasswordTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        initializeUI()

        checkbox()

        logbutton.setOnClickListener{
            loginUserAccount()
        }
        signinTV.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        fpasswordTV.setOnClickListener{
            resetPassword()
        }
    }

    //metodon che verifica la checkBox per aggiornare la SHARED:PREFS
    private fun checkbox() {
        val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        val check: String? = sharedPreferences.getString("name","")
        if (check.equals("true")){
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun resetPassword() {
        val email = emailTV.text.toString()
        if(TextUtils.isEmpty(email)){
            Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG).show()
            return
        } else{
            mAuth.sendPasswordResetEmail(email).addOnSuccessListener {
                Toast.makeText(applicationContext, "Please, check your email", Toast.LENGTH_LONG).show()
            }
                .addOnFailureListener{
                    Toast.makeText(applicationContext, "ERROR: try again", Toast.LENGTH_LONG).show()
                }
        }
    }

    //metodo che serve per il login effettivo
    private fun loginUserAccount() {
        val email = emailTV.text.toString()
        val password = passwordTV.text.toString()
        progressBarLogin.visibility = View.VISIBLE

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG).show()
            progressBarLogin.visibility = View.INVISIBLE
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(applicationContext, "Please enter password!", Toast.LENGTH_LONG).show()
            progressBarLogin.visibility = View.INVISIBLE
            return
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    if(checkBox.isChecked){
                        val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PREFS,
                            MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("name", "true")
                        editor.apply()
                    }
                    Toast.makeText(applicationContext, "Login successful!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "Login failed! Please try again later", Toast.LENGTH_LONG).show()
                    progressBarLogin.visibility = View.INVISIBLE
                }
            }
    }

    private fun initializeUI() {
        emailTV = findViewById(R.id.et_email)
        passwordTV = findViewById(R.id.et_password)
        signinTV = findViewById(R.id.tv_signup)
        logbutton = findViewById(R.id.button_login)
        fpasswordTV = findViewById(R.id.tv_forgot_password)
    }
}