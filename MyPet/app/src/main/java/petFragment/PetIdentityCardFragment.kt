package petFragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import data.FirebaseDBHelper
import it.pdm.app.R
import kotlinx.android.synthetic.main.fragment_pet_identity_card.*


class PetIdentityCardFragment : Fragment() {

    private lateinit var uId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pet_identity_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setInformation()
    }

    private fun setInformation() {
        val ref = FirebaseDBHelper.dbRefPets
        ref.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val name = snapshot.child("name").value.toString()
                        if(tv_name!=null)
                            tv_name.setText(name)

                        val birthday = snapshot.child("birthday").value.toString()
                        if(tv_birthday!=null)
                            tv_birthday.setText(birthday)

                        val weight = snapshot.child("weight").value.toString()
                        if(tv_weight!=null)
                            tv_weight.setText("")
                            tv_weight.setText("$weight kg")

                        val height = snapshot.child("height").value.toString()
                        if(tv_height!=null)
                            tv_height.setText("")
                            tv_height.setText("$height cm")

                        val chipNumber = snapshot.child("chipNumber").value.toString()
                        if(tv_chipNumber!=null)
                            tv_chipNumber.setText(chipNumber)

                        val gender = snapshot.child("gender").value.toString()
                        if(tv_gender!=null)
                            tv_gender.setText(gender)

                        val breed = snapshot.child("breed").value.toString()
                        if(tv_breed!=null)
                            tv_breed.setText(breed)
                    }
                }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


    private fun init() {
        uId = FirebaseAuth.getInstance().currentUser!!.uid
    }
}