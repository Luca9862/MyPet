package medpet

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import it.pdm.app.databinding.FragmentVaccinationBinding
import pets.Vaccine

class VaccinationFragment : Fragment(), VaccineAdapter.adapterClickInterface, VaccinePopUp.OnDialogNextBtnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String
    private lateinit var binding: FragmentVaccinationBinding
    private lateinit var database: DatabaseReference
    private lateinit var vacAdapter: VaccineAdapter
    private lateinit var vaccineItemList: MutableList<Vaccine>
    private lateinit var popUpFragment: VaccinePopUp

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVaccinationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        getVaccinesFromFirebase()
        registerEvents()
    }

    //prendo i vaccini dal database
    private fun getVaccinesFromFirebase(){
        database.addValueEventListener(object: ValueEventListener { //ascolto i cambiamenti
            //prendo ogni dato sotto al nodo del database, in questo caso "vaccines"
            override fun onDataChange(snapshot: DataSnapshot) {
                vaccineItemList.clear()
                for (vacSnapshot in snapshot.children){
                    val ns = vacSnapshot.value.toString()
                    val stringArray = ns.split(",")
                    val name = stringArray[0]
                    val date = stringArray[1]
                    val recall = stringArray[2]
                    val toDoVaccine = snapshot.key?.let { Vaccine(it, name, date, recall) } //it indica l'oggetto corrente, e diventa il valore della variabile idVaccine
                    if(toDoVaccine != null){
                        vaccineItemList.add(toDoVaccine) //aggiungo l'oggetto alla lista
                        Log.d("vaccino", name+date+recall)
                    }
                }
                vacAdapter.notifyDataSetChanged()
            }
            //se non è possibile leggere i dati:
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun init() {

        auth = FirebaseAuth.getInstance()
        authId = auth.currentUser!!.uid
        database = Firebase.database.reference.child("users").child(authId).child("vaccines")

        binding.vaccinesList.setHasFixedSize(true)
        binding.vaccinesList.layoutManager = LinearLayoutManager(context)

        vaccineItemList = mutableListOf()
        vacAdapter = VaccineAdapter(vaccineItemList)
        vacAdapter.setListener(this)
        binding.vaccinesList.adapter = vacAdapter

    }

    private fun registerEvents(){
        binding.fab.setOnClickListener{
            popUpFragment = VaccinePopUp()
            popUpFragment.setListener(this)
            popUpFragment.show(childFragmentManager, "VaccinePopUp")
        }
    }

    override fun onSaveVaccine(vaccine: String, tvVaccine: TextInputEditText, date1: String, date2: String) { //tvNote è l'inputEditText nel popUpNotes

        //creo un nodo figlio univoco
        database.push().setValue(vaccine+","+date1+","+date2).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context, "Nota aggiunta", Toast.LENGTH_SHORT).show()
                tvVaccine.text = null
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            popUpFragment.dismiss()
        }
    }

    override fun onDeleteVaccineBtnClicked(vaccine: Vaccine) {
        database.child(vaccine.idVaccine).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context, "cancella", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }



}