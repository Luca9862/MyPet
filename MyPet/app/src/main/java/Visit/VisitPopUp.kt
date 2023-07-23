package Visit

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import it.pdm.app.databinding.FragmentVisitPopUpBinding
import kotlinx.android.synthetic.main.fragment_visit_pop_up.data_picker
import pets.Visit
import java.text.SimpleDateFormat
import java.util.*

class VisitPopUp : DialogFragment() {

    private lateinit var binding: FragmentVisitPopUpBinding
    private var listener: OnDialogNextBtnClickListener? = null
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
    ): View {
        binding = FragmentVisitPopUpBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerEvents()
        data_picker.setOnClickListener{
            openCalendar(data_picker)
        }
    }

    private fun openPhoneCalendar(medVisit: Visit){
        val beginTime = Calendar.getInstance()
        //TODO: da finire
        beginTime.set(2022, 4, 19) // set the date and time of the event
        val endTime = Calendar.getInstance()
        endTime.set(2022, 4, 19) // set the date and time of the event
        //TODO
        val calendarIntent = Intent(Intent.ACTION_INSERT)
        calendarIntent.data = CalendarContract.Events.CONTENT_URI
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.timeInMillis)
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
        calendarIntent.putExtra(CalendarContract.Events.DESCRIPTION, medVisit.textVisit)
        startActivity(calendarIntent)
    }

    private fun registerEvents(){
        binding.popUpNext.setOnClickListener{
            val visitName = binding.tvVisit.text.toString()

            //date1.set(data_picker.year, data_picker.month, data_picker.dayOfMonth)

            //val dataSelezionata = dateFormat.format(date1.time) //trasforma in stringa la data con il suo formato dd-MM-yyyy
            if (date1.before(date2)) {                     //La data Selezionata è prima di quella corrente
                Toast.makeText(context, "Non puoi selezionare una data già passata", Toast.LENGTH_SHORT).show()
            }else if(date1.after(date2)){                  // la data Selezionata è dopo quella corrente
                if(visitName.isNotEmpty()){
                    listener?.onSaveVisit(visitName, binding.tvVisit, dataSelezionata) //noteTask è la stringa che viene presa dal tvNote -------------------------------------------------------
                }else{
                    Toast.makeText(context, "Non puoi lasciare il campo vuoto", Toast.LENGTH_SHORT).show()
                }
            }else{
                Log.d("La Data", "Selezionata è la stessa di quella corrente")
                if(visitName.isNotEmpty()){
                    listener?.onSaveVisit(visitName, binding.tvVisit, dataSelezionata) //noteTask è la stringa che viene presa dal tvNote -------------------------------------------------------
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
        fun onSaveVisit(vaccine: String, tvVaccine: TextInputEditText, date1: String)
    }
}