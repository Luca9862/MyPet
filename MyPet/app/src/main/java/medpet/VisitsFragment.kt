package medpet

import pets.Visit
import Visit.VisitAdapter
import Visit.VisitPopUp
import android.os.Bundle
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
import data.FirebaseDBHelper
import it.pdm.app.databinding.FragmentVisitsBinding

class VisitsFragment : Fragment(), VisitPopUp.OnDialogNextBtnClickListener,
    VisitAdapter.adapterClickInterface {

    private lateinit var binding: FragmentVisitsBinding

    /*variabili primo codice*/
    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String
    private lateinit var database: DatabaseReference
    private lateinit var visitAdapter: VisitAdapter
    private lateinit var visitItemList: MutableList<Visit>
    /*--------------VARIABILI-----------------*/
    private lateinit var popUpFragment: VisitPopUp

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVisitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        getNoteFromFirebase()
        registerEvents()
    }

    private fun getNoteFromFirebase(){
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                visitItemList.clear()
                for (noteSnapshot in snapshot.children){
                    val ns = noteSnapshot.value.toString()
                    val stringArray = ns.split(",")
                    val title = stringArray[0]
                    val visitDate = stringArray[1]
                    val toDoNote = noteSnapshot.key?.let { Visit(it, title, visitDate) } //con it, passo nel primo parametro(idNote)
                    if(toDoNote != null){
                        visitItemList.add(toDoNote)
                    }
                }
                visitAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun init() {

        auth = FirebaseAuth.getInstance()
        authId = auth.currentUser!!.uid
        database = FirebaseDBHelper.dbRefRT.child("Visit")

        binding.visitList.setHasFixedSize(true)
        binding.visitList.layoutManager = LinearLayoutManager(context)

        visitItemList = mutableListOf()
        visitAdapter = VisitAdapter(visitItemList)
        visitAdapter.setListener(this)
        binding.visitList.adapter = visitAdapter
    }

    private fun registerEvents(){
        binding.fab.setOnClickListener{
            popUpFragment = VisitPopUp()
            popUpFragment.setListener(this)
            popUpFragment.show(childFragmentManager, "FragmentPopUpNotes")
        }
    }

    override fun onDeleteNoteBtnClicked(visit: Visit) {
        database.child(visit.idVisit).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context, "cancella", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSaveVisit(visit: String, tvVisit: TextInputEditText, date1: String)
    {
        database.push().setValue(visit+","+date1).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context, "Visita aggiunta", Toast.LENGTH_SHORT).show()
                tvVisit.text = null
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            popUpFragment.dismiss()
        }
    }
}