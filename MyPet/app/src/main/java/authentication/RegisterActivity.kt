package authentication

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import data.FirebaseDBHelper
import it.pdm.app.R
import kotlinx.android.synthetic.main.activity_register.*
import java.io.ByteArrayOutputStream

class RegisterActivity : AppCompatActivity() {
    private lateinit var emailTV: TextView
    private lateinit var passwordTV: TextView
    private lateinit var confirmPasswordTV: TextView
    private lateinit var regButton: Button
    private lateinit var loginTV: TextView

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        initializeUI()

        regButton.setOnClickListener{
            registerNewUser()
        }

        loginTV.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    //metodo per la creazione effettiva del nuovo account
    private fun registerNewUser() {

        val email = emailTV.text.toString()
        val password = passwordTV.text.toString()
        val confirmation = confirmPasswordTV.text.toString()
        progressBarSignup.visibility = View.VISIBLE

        if (TextUtils.isEmpty(email)){
            Toast.makeText(applicationContext, "Please, enter email", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(password) || password.length < 6) {
            Toast.makeText(applicationContext, "Invalid password", Toast.LENGTH_LONG).show()
            return
        }
        if(TextUtils.isEmpty(confirmPasswordTV.toString())){
            Toast.makeText(applicationContext, "Please confirm password", Toast.LENGTH_LONG).show()
            return
        }
        if(password != confirmation){
            Toast.makeText(applicationContext, "Password error: try again", Toast.LENGTH_LONG).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    initFirebaseStorage()
                    Toast.makeText(applicationContext, "Registration successful!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, LoginActivity::class.java )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }else{
                    Toast.makeText(applicationContext, "Registration failed!",
                        Toast.LENGTH_LONG).show()
                    progressBarSignup.visibility = View.INVISIBLE
                }
            }
    }

    private fun initFirebaseStorage(){
        val imageRef = FirebaseDBHelper.dbRefST.child("images/pet_picture.jpg")
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.default_picture)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        imageRef.putBytes(data)
    }

    private fun initializeUI() {
        emailTV = findViewById(R.id.et_email)
        passwordTV = findViewById(R.id.et_password)
        confirmPasswordTV = findViewById(R.id.et_confirm_password)
        regButton = findViewById(R.id.button_sign_up)
        loginTV = findViewById(R.id.tv_signup)
    }
}