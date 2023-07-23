package setting

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import authentication.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import data.FirebaseDBHelper
import it.pdm.app.MainActivity
import it.pdm.app.MySingleton
import it.pdm.app.R
import java.io.ByteArrayOutputStream
import java.io.File


class Settings : PreferenceFragmentCompat() {
    private val SHARED_PREFS = "sharedPrefs"
    private lateinit var mAuth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var prefUser: Preference
    private lateinit var prefPassword: Preference
    private lateinit var prefLogout: Preference
    private lateinit var prefClearShared: Preference
    private lateinit var prefDeletePet : Preference
    private lateinit var prefFeedback: Preference
    private lateinit var prefInfo: Preference
    val mySingleton = MySingleton.getInstance()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        initializeUI()

        setEmail()

        prefPassword.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setCancelable(true)
            builder.setTitle("Reset password?")
            builder.setMessage("You will receive an email with a link")
            builder.setPositiveButton("Confirm"
                ) { _, _ ->
                resetPassword()}
            builder.setNegativeButton(android.R.string.cancel
                ) { _, _ -> }

            val dialog: AlertDialog = builder.create()
            dialog.show()
            true
        }

        prefLogout.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            logout()
            true
        }
        prefClearShared.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mySingleton.resetShared = true
            Toast.makeText(context, "Step reset", Toast.LENGTH_SHORT).show()
            true
        }

        prefInfo.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setCancelable(true)
            builder.setTitle("Info")
            builder.setMessage(
                "MyPet is a project developed by Luca Canali and Eros Marsichina, " +
                        "University Insubria's students "
            )
            builder.setPositiveButton("Ok"
            ) {_,_ ->}

            val dialog: AlertDialog = builder.create()
            dialog.show()
            true
        }

        prefDeletePet.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setCancelable(true)
            builder.setTitle("DELETE PET")
            builder.setMessage("Are you sure? Any progress will be lost")
            builder.setPositiveButton("Ok"
            ) {_,_ -> deletePet() }
            builder.setNegativeButton("Cancel"
            ){_,_ ->}

            val dialog: AlertDialog = builder.create()
            dialog.show()
            true
        }

        prefFeedback.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "plain/text"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("mypet.mc@gmail.com"))
            startActivity(emailIntent)
            true
        }
    }

    private fun deletePet(){
        FirebaseDBHelper.dbRefRT.addValueEventListener(object: ValueEventListener
            {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        snapshot.ref.removeValue()
                        deleteImageFromInternalStorage()
                        deleteImageFromFirebase()
                        if(context!=null){
                            val intent = Intent(context, MainActivity::class.java )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            Toast.makeText(context, "Pet deleted", Toast.LENGTH_LONG).show()
                        }

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled", error.toException())
                }
            })
    }

    private fun deleteImageFromInternalStorage(){
        val directory = context?.getDir("imageDir", Context.MODE_PRIVATE)
        val file = File(directory, "your-image.jpg")
        if(file.exists()){
            file.delete()
        }
    }

    private fun deleteImageFromFirebase(){
        if(isAdded){
            val imageRef = FirebaseDBHelper.dbRefST.child("images/pet_picture.jpg")
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.default_picture)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            imageRef.putBytes(data)
        }
    }

    private fun setEmail(){
            prefUser.summary = user.email.toString()
    }

    private fun resetPassword() {
        mAuth.sendPasswordResetEmail(user.email.toString()).addOnSuccessListener {
            Toast.makeText(context, "Please, check your email", Toast.LENGTH_LONG).show()
            logout()
        }
            .addOnFailureListener{
                Toast.makeText(context, "ERROR: try again", Toast.LENGTH_LONG).show()
            }
    }


    private fun logout() {
        Toast.makeText(context, "LOG OUT", Toast.LENGTH_LONG).show()

        deleteImageFromInternalStorage()
        val sharedPreferences: SharedPreferences? = context?.getSharedPreferences(SHARED_PREFS,
            AppCompatActivity.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor? = sharedPreferences?.edit()
        editor?.putString("name", "")
        editor?.apply()

        val intent = Intent(context, RegisterActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)

    }

    private fun initializeUI(){
        mAuth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!
        prefUser = findPreference("user_email")!!
        prefPassword = findPreference("reset_password")!!
        prefLogout = findPreference("logout")!!
        prefClearShared = findPreference("Clear_shared")!!
        prefDeletePet = findPreference("delete_pet")!!
        prefInfo = findPreference("info")!!
        prefFeedback = findPreference("feedback")!!
    }
}

