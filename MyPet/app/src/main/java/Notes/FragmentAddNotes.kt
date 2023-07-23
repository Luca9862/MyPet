package Notes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import data.FirebaseDBHelper
import it.pdm.app.R
import it.pdm.app.databinding.FragmentAddNotesBinding
import kotlinx.android.synthetic.main.fragment_add_notes.*

class FragmentAddNotes : Fragment() {

    private lateinit var binding: FragmentAddNotesBinding

    private lateinit var database: FirebaseDatabase
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var email: String
    private lateinit var uId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializeUI()

        addNoteButton.setOnClickListener{

            val note = noteEditText.text.toString()
            if (note.isNotEmpty()){
                writeUser()
                writeNote()
                Toast.makeText(context,"Registrato!", Toast.LENGTH_LONG)
                    .show()
                findNavController().navigate(R.id.action_fragmentAddNotes_to_notesFragment)
            }else
                Toast.makeText(context, "Impossibile lasciare vuoto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun writeUser(){
        val ref = database.getReference("users")
        ref.child(uId)
    }

    private fun writeNote() {
        val note = noteEditText.text.toString()
        //Firebase.database.reference.child(uId).child("Notes").push().setValue(note)
        FirebaseDBHelper.dbRefRT.child("Notes").push().setValue(note) //con push() si ottiene una chiave univoca del figlio, così da poter creare più note, eliminando l'overwriting.
        //FirebaseRealtimeDBHelper.dbRefNote.setValue(note)
    }

    /*private fun initializeUI(){
        database = Firebase.database
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        email = firebaseUser.email.toString()
        uId = firebaseUser.uid
    }*/

    private fun initUI(){
        uId = firebaseUser.uid
    }

}