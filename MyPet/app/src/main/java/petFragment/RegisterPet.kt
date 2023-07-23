package petFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import data.FirebaseDBHelper
import it.pdm.app.MainActivity
import it.pdm.app.R
import it.pdm.app.databinding.ActivityRegisterPetBinding
import kotlinx.android.synthetic.main.activity_register_pet.*
import kotlinx.android.synthetic.main.fragment_pop_up_notes.*
import pets.MyPet
import java.text.SimpleDateFormat
import java.util.*

class RegisterPet : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var email: String
    private lateinit var uId: String
    private lateinit var binding : ActivityRegisterPetBinding

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPetBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_register_pet)

        button_sign_pet.setBackgroundColor(Color.BLUE)
        button_sign_pet.background = resources.getDrawable(R.drawable.countor_signuppet)

        val gender = resources.getStringArray(R.array.gender)
        val arrayAdapter = ArrayAdapter(this, R.layout.list_item, gender)
        et_gender.setAdapter(arrayAdapter)

        initializeUI()

        button_sign_pet.setOnClickListener {
            if(everythingOk()){
                writeUser()
                writePet()
                Toast.makeText(this,"Successful!", Toast.LENGTH_LONG)
                    .show()
            }else {
                Toast.makeText(this,"Please check name and birthday", Toast.LENGTH_LONG)
                    .show()
            }
        }

        et_birthday.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            val picker = builder.build()
            picker.show(supportFragmentManager, picker.toString())
            picker.addOnPositiveButtonClickListener {
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = formatter.format(Date(it))
                et_birthday.setText(date)
            }
        }
    }

    //metodo che verifica che sia stato inserito sia il nome sia il compleanno dell'animale
    private fun everythingOk(): Boolean {
        var ok = true
        if (et_name.text?.isEmpty() == true)
            ok = false
        if (et_birthday.text?.isEmpty() == true)
            ok = false
        return ok
    }

    //metodo che crea il nodo "user_uId" nel real time DB
    private fun writeUser(){
        database.child(uId)
    }

    //metodo che scrive l'animale nel real time DB
    private fun writePet() {
        val pet = MyPet(
            et_name.text.toString(),
            et_birthday.text.toString(),
            et_weight.text.toString(),
            et_height.text.toString(),
            et_chipNumber.text.toString(),
            et_gender.text.toString(),
            et_breed.text.toString()
        )
        FirebaseDBHelper.dbRefPets.setValue(pet)
            .addOnSuccessListener {
                Toast.makeText(this, "Successfully", Toast.LENGTH_LONG).show()
            }
        val intent = Intent(this, MainActivity::class.java )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun initializeUI(){
        database = FirebaseDBHelper.dbRefRT
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        email = firebaseUser.email.toString()
        uId = firebaseUser.uid
    }
}