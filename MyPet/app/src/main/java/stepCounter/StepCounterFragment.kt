package stepCounter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import data.FirebaseDBHelper
import it.pdm.app.MySingleton
import it.pdm.app.R
import it.pdm.app.databinding.FragmentStepCounterBinding
import it.pdm.app.databinding.FragmentStepCounterBinding.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_pet_identity_card.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_step_counter.*
import java.text.SimpleDateFormat
import java.util.*

class StepCounterFragment : Fragment() {

    private lateinit var binding: FragmentStepCounterBinding
    private var passiFatti = 100.00f
    var peso = 0.00f
    private var weight = ""
    private var calPerse = 0.00f
    val mySingleton = MySingleton.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStepCounterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_stop.setBackgroundColor(Color.GRAY)
        btn_stop.isClickable = false

        if (mySingleton.btnStartIsClickable == true){
            binding.btnStart.setBackgroundColor(Color.GRAY)
            binding.btnStop.setBackgroundColor(Color.RED)
        }else if (mySingleton.btnStopIsClickable == true){
            binding.btnStart.setBackgroundColor(resources.getColor(R.color.Green))
            binding.btnStop.setBackgroundColor(Color.GRAY)
        }

        binding.btnStart.setOnClickListener {
            binding.btnStart.setBackgroundColor(Color.GRAY)
            binding.btnStop.setBackgroundColor(Color.RED)
            mySingleton.btnStartIsClickable=true
            mySingleton.btnStopIsClickable=false
            btn_start.isClickable = false
            btn_stop.isClickable = true
            val intentFg = Intent(context, StepService::class.java)
            requireActivity().startService(intentFg)
        }

        binding.btnStop.setOnClickListener {
            binding.btnStart.setBackgroundColor(resources.getColor(R.color.Green))
            binding.btnStop.setBackgroundColor(Color.GRAY)
            mySingleton.btnStartIsClickable=false
            mySingleton.btnStopIsClickable=true
            btn_start.isClickable = true
            btn_stop.isClickable = false
            val intentFg = Intent(context, StepService::class.java)
            requireActivity().stopService(intentFg)
        }
        val mySingleton = MySingleton.getInstance()
        val myValue = mySingleton.myValue
        stepsValue_tv.text = myValue
        passiFatti = myValue.toFloat()
        calPerse = ((peso*0.02)*passiFatti).toFloat()
        Log.d("tag", calPerse.toString())
        Log.d("pesoLetto", peso.toString())
        Log.d("passiLetti", passiFatti.toString())

        //prendo il valore del peso del pet da firebase
        val ref = FirebaseDBHelper.dbRefPets
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    weight = snapshot.child("weight").value.toString()
                    if(weight.isNotEmpty()){
                        peso = weight.toFloat()
                        calPerse = ((peso*0.02)*passiFatti).toFloat()
                        Log.d("peso", peso.toString())
                        Log.d("passiLetti", passiFatti.toString())
                        Log.d("tag", calPerse.toString())
                        val formattedNumber = String.format("%.2f", calPerse)
                        if(calValue_tv.text!=null)
                            calValue_tv.text = formattedNumber
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}


