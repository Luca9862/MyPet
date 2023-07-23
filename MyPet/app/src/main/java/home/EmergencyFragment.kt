package home

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import it.pdm.app.R
import kotlinx.android.synthetic.main.fragment_emergency.*

class EmergencyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emergency, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        card112.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Are you sure?")
            builder.setPositiveButton("Yes"){
                _,_-> callNumber("112")
            }
            builder.setNegativeButton("Cancel"){_,_->}
            val dialog = builder.create()
            dialog.show()
        }

        enpaCard.setOnClickListener{
            callNumber("02.97064220")
        }

        websiteCard.setOnClickListener{
            val url = "https://www.salute.gov.it/portale/caniGatti/homeCaniGatti.jsp"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        backArrowCard.setOnClickListener{
            findNavController().navigate(R.id.action_emergencyNumbersFragment_to_homeFragment)
        }
    }

    //metodo che apre la app del telefono con un numero già digitato
    private fun callNumber(phoneNumber: String){
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }
}