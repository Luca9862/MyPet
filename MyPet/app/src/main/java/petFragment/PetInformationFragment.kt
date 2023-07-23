package petFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import it.pdm.app.R
import kotlinx.android.synthetic.main.fragment_pet_information.*
import medpet.MedFragment

class PetInformationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pet_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        card_information.setOnClickListener {
            findNavController().navigate(R.id.action_petFragment_to_petIdentityCardFragment)
        }

        card_medical.setOnClickListener {
            val fragment = MedFragment()
            val transaction: FragmentTransaction? = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.rl_pet_fragment, fragment)
            transaction?.commit()
        }

        card_run.setOnClickListener {
            findNavController().navigate(R.id.action_petFragment_to_blankFragment)
        }

        editPetCard.setOnClickListener {
            val intent = Intent(activity, EditPetActivity::class.java)
            FragmentManager.POP_BACK_STACK_INCLUSIVE
            startActivity(intent)
        }
    }
}