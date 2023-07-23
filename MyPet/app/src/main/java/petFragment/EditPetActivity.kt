package petFragment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import data.FirebaseDBHelper
import it.pdm.app.MainActivity
import it.pdm.app.R
import kotlinx.android.synthetic.main.activity_edit_pet.*
import kotlinx.android.synthetic.main.activity_edit_pet.et_birthday
import kotlinx.android.synthetic.main.activity_edit_pet.et_breed
import kotlinx.android.synthetic.main.activity_edit_pet.et_chipNumber
import kotlinx.android.synthetic.main.activity_edit_pet.et_edit1
import kotlinx.android.synthetic.main.activity_edit_pet.et_edit2
import kotlinx.android.synthetic.main.activity_edit_pet.et_edit3
import kotlinx.android.synthetic.main.activity_edit_pet.et_edit4
import kotlinx.android.synthetic.main.activity_edit_pet.et_edit5
import kotlinx.android.synthetic.main.activity_edit_pet.et_edit6
import kotlinx.android.synthetic.main.activity_edit_pet.et_edit7
import kotlinx.android.synthetic.main.activity_edit_pet.et_gender
import kotlinx.android.synthetic.main.activity_edit_pet.et_height
import kotlinx.android.synthetic.main.activity_edit_pet.et_name
import kotlinx.android.synthetic.main.activity_edit_pet.et_weight
import pets.MyPet
import java.text.SimpleDateFormat
import java.util.*

class EditPetActivity : AppCompatActivity() {

    lateinit var name: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pet)

        //prendo la stringa e la metto nella lista per visualizzarla nel dropDown
        val gender = resources.getStringArray(R.array.gender)
        val arrayAdapter = ArrayAdapter(this, R.layout.list_item, gender)
        et_gender.setAdapter(arrayAdapter)

        initView()

        et_birthday.setOnClickListener {
            openCalendar(et_birthday)
        }

        button2.setOnClickListener {
            editPet()
        }
    }

    private fun initView() {
        val petReference = FirebaseDBHelper.dbRefPets
        petReference.keepSynced(true)
        petReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = dataSnapshot.child("name").getValue(String::class.java)
                val birthday = dataSnapshot.child("birthday").getValue(String::class.java)
                val weight = dataSnapshot.child("weight").getValue(String::class.java)
                val height = dataSnapshot.child("height").getValue(String::class.java)
                val chipNumber = dataSnapshot.child("chipNumber").getValue(String::class.java)
                val gender = dataSnapshot.child("gender").getValue(String::class.java)
                val breed = dataSnapshot.child("breed").getValue(String::class.java)

                et_edit1.hint = name
                et_name.hint = name
                et_edit2.hint = birthday
                et_birthday.hint = birthday
                et_edit3.hint = weight + "kg"
                et_weight.hint = weight
                et_edit4.hint = height +"cm"
                et_height.hint = height
                et_edit5.hint = chipNumber
                et_chipNumber.hint = chipNumber
                et_edit6.hint = gender
                et_gender.hint = gender
                et_edit7.hint = breed
                et_breed.hint = breed
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun editPet() {
        val petReference = FirebaseDBHelper.dbRefPets
        petReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val intent = Intent(this@EditPetActivity, MainActivity::class.java)

                val name: String = if(et_name.text?.isEmpty() == true){
                    et_name.hint.toString()
                }else{
                    et_name.text.toString()
                }

                val birthday: String = if(et_birthday.text?.isEmpty() == true){
                    et_birthday.hint.toString()
                }else{
                    et_birthday.text.toString()
                }

                val weight: String = if(et_weight.text?.isEmpty() == true){
                    et_weight.hint.toString()
                }else{
                    et_weight.text.toString()
                }

                val height = if(et_height.text?.isEmpty() == true){
                    et_height.hint.toString()
                }else{
                    et_height.text.toString()
                }

                val chipNumber = if(et_chipNumber.text?.isEmpty() == true){
                    et_chipNumber.hint.toString()
                }else{
                    et_chipNumber.text.toString()
                }

                val gender = if(et_gender.text?.isEmpty() == true){
                    et_gender.hint.toString()
                }else{
                    et_gender.text.toString()
                }

                val breed = if(et_breed.text?.isEmpty() == true){
                    et_breed.hint.toString()
                }else{
                    et_breed.text.toString()
                }

                val pet = MyPet(name, birthday, weight, height, chipNumber, gender, breed)
                petReference.setValue(pet).addOnSuccessListener {
                    finish()
                    startActivity(intent)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "Something went wrong: try again", Toast.LENGTH_LONG).show()
                    }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }


    private fun openCalendar(et: EditText) {
        val builder = MaterialDatePicker.Builder.datePicker()
        val picker = builder.build()
        picker.show(supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = formatter.format(Date(it))
            et.setText(date)
        }
    }
}