package Notes

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import it.pdm.app.R
import it.pdm.app.databinding.FragmentPopUpNotesBinding
import kotlinx.android.synthetic.main.fragment_pop_up_notes.*
import java.lang.reflect.Modifier
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import java.util.*


class FragmentPopUpNotes : DialogFragment() {

    private lateinit var binding: FragmentPopUpNotesBinding
    private var listener: OnDialogNextBtnClickListener? = null
    @RequiresApi(Build.VERSION_CODES.O)
    val date2 = Date()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    var dataSelezionata = ""
    var dateString = ""
    var date1 : Date = Date()

    fun setListener(listener: OnDialogNextBtnClickListener){
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPopUpNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerEvents()
        data_picker.setOnClickListener{
            openCalendar(data_picker)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerEvents(){
        binding.popUpNext.setOnClickListener{
            val noteTask = binding.tvNote.text.toString()
            val bodyTask = binding.tvNoteBody.text.toString()

            if (date1.before(date2)) {                     //La data Selezionata è prima di quella corrente
                Toast.makeText(context, "Non puoi selezionare una data già passata", Toast.LENGTH_SHORT).show()
            }else if(date1.after(date2)){                  // la data Selezionata è dopo quella corrente
                if(noteTask.isNotEmpty()){
                    listener?.onSaveTask(noteTask, binding.tvNote, dataSelezionata, bodyTask, binding.tvNoteBody) //noteTask è la stringa che viene presa dal tvNote -------------------------------------------------------
                }else{
                    Toast.makeText(context, "Non puoi lasciare il campo vuoto", Toast.LENGTH_SHORT).show()
                }
            }else{
                Log.d("La Data", "Selezionata è la stessa di quella corrente")
                if(noteTask.isNotEmpty()){
                    listener?.onSaveTask(noteTask, binding.tvNote, dataSelezionata, bodyTask, binding.tvNoteBody) //noteTask è la stringa che viene presa dal tvNote -------------------------------------------------------
                }else{
                    Toast.makeText(context, "Non puoi lasciare il campo vuoto", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.popUpClose.setOnClickListener{
            dismiss()
        }
    }

    private fun openCalendar(et: Button) {
        val builder = MaterialDatePicker.Builder.datePicker()
        val picker = builder.build()
        picker.show(childFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dataSelezionata = formatter.format(Date(it))
            et.setText(dataSelezionata)

            date1 = Date(it)
            dateString = dateFormat.format(date1)
        }
    }

    interface OnDialogNextBtnClickListener{
        fun onSaveTask(note : String, tvNote: TextInputEditText, date: String, body : String, tvNoteBody : TextInputEditText)
    }

}

