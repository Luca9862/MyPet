package medpet

import Notes.FragmentPopUpNotes
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import it.pdm.app.R
import it.pdm.app.databinding.FragmentPopUpNotesBinding
import it.pdm.app.databinding.FragmentVaccinePopUpBinding
import kotlinx.android.synthetic.main.fragment_pop_up_notes.*
import kotlinx.android.synthetic.main.fragment_vaccine_pop_up.*
import java.text.SimpleDateFormat
import java.util.*

class VaccinePopUp : DialogFragment() {

    private lateinit var binding: FragmentVaccinePopUpBinding
    private var listener: VaccinePopUp.OnDialogNextBtnClickListener? = null
    //val date1 = Calendar.getInstance()
    val date2 = Date()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    var dataSelezionata = ""
    var recallSelezionata = ""
    var dateVaccine : Boolean = false
    var recallVaccine : Boolean = false
    var dateFirst : Date = Date()
    var dateRecall : Date = Date()

    fun setListener(listener: VaccinePopUp.OnDialogNextBtnClickListener){
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVaccinePopUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerEvents()
        data_picker1.setOnClickListener{
            openCalendar(data_picker1)
            dateVaccine = true
        }
        data_picker2.setOnClickListener{
            openCalendar(data_picker2)
            recallVaccine = true
        }
    }

    private fun registerEvents(){
        binding.popUpNext.setOnClickListener{
            val vaccineName = binding.nameVaccine.text.toString()

            //La data Selezionata è prima di quella corrente
            if (dateFirst.before(date2)) {
                Toast.makeText(context, "Non puoi selezionare una data già passata", Toast.LENGTH_SHORT).show()
            }
            if(dateRecall.before(dateFirst)){
                Toast.makeText(context, "Non puoi selezionare il richiamo prima della vaccinazione", Toast.LENGTH_SHORT).show()
            }
            // la data Selezionata è dopo quella corrente
            else if(dateFirst.after(date2) || dateRecall.after(dateFirst) ){
                if(vaccineName.isNotEmpty()){
                    listener?.onSaveVaccine(vaccineName, binding.nameVaccine, dataSelezionata, recallSelezionata)
                }else{
                    Toast.makeText(context, "Non puoi lasciare il campo vuoto", Toast.LENGTH_SHORT).show()
                }
            }else{
                Log.d("La Data", "Selezionata è la stessa di quella corrente")
                if(vaccineName.isNotEmpty()){
                    listener?.onSaveVaccine(vaccineName, binding.nameVaccine, dataSelezionata, recallSelezionata)
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
            //il valore è true se viene premuto il pulsante del data_picker in questione
            if(dateVaccine == true){
                dataSelezionata = formatter.format(Date(it))
                et.setText(dataSelezionata)
                dateVaccine = false
                dateFirst = Date(it)
            }
            //il valore è true se viene premuto il pulsante del data_picker in questione
            if (recallVaccine == true){
                recallSelezionata = formatter.format(Date(it))
                et.setText(recallSelezionata)
                recallVaccine = false
                dateRecall = Date(it)
            }
        }
    }

    interface OnDialogNextBtnClickListener{
        fun onSaveVaccine(vaccine: String, tvVaccine: TextInputEditText, date1: String, date2: String)
    }
}
