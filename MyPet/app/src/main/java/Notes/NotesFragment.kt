package Notes

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
import it.pdm.app.databinding.FragmentNotesBinding


class NotesFragment : Fragment(), FragmentPopUpNotes.OnDialogNextBtnClickListener,
    toDoAdapter.adapterClickInterface {

    private lateinit var binding: FragmentNotesBinding

    /*variabili primo codice*/
    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String
    private lateinit var database: DatabaseReference
    private lateinit var taskAdapter: toDoAdapter
    private lateinit var toDoItemList: MutableList<Note>
    /*--------------VARIABILI-----------------*/
    private lateinit var popUpFragment: FragmentPopUpNotes
    /*--------VARIABILI PER IL POPUP-------*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        getNoteFromFirebase()
        registerEvents()
    }

    private fun getNoteFromFirebase(){
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                toDoItemList.clear()
                for (noteSnapshot in snapshot.children){
                    val ns = noteSnapshot.value.toString()
                    val stringArray = ns.split(",")
                    val title = stringArray[0]
                    val noteDate = stringArray[1]
                    val noteBody = stringArray[2]
                    val toDoNote = noteSnapshot.key?.let { Note(it, title, noteDate, noteBody) } //con it, passo nel primo parametro(idNote)
                    if(toDoNote != null){
                        toDoItemList.add(toDoNote)
                    }
                }
                taskAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun init() {

        auth = FirebaseAuth.getInstance()
        authId = auth.currentUser!!.uid
        database = FirebaseDBHelper.dbRefRT.child("Notes")

        binding.noteList.setHasFixedSize(true)
        binding.noteList.layoutManager = LinearLayoutManager(context)

        toDoItemList = mutableListOf()
        taskAdapter = toDoAdapter(toDoItemList)
        taskAdapter.setListener(this)
        binding.noteList.adapter = taskAdapter

    }

    private fun registerEvents(){
        binding.fab.setOnClickListener{
            popUpFragment = FragmentPopUpNotes()
            popUpFragment.setListener(this)
            popUpFragment.show(childFragmentManager, "FragmentPopUpNotes")
        }
    }

    override fun onSaveTask(note: String, tvNote: TextInputEditText, date: String, body : String, tvNoteBody : TextInputEditText) { //tvNote è l'inputEditText nel popUpNotes

        database.push().setValue(note+","+date+","+body).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context, "Nota aggiunta", Toast.LENGTH_SHORT).show()
                tvNote.text = null
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            popUpFragment.dismiss()
        }
    }

    override fun onDeleteNoteBtnClicked(note: Note) {
        database.child(note.idNote).removeValue().addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context, "cancella", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}