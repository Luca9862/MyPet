package medpet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import petFragment.PetInformationFragment
import it.pdm.app.R
import kotlinx.android.synthetic.main.fragment_med.*

class MedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_med, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        card_vaccinations.setOnClickListener {
            findNavController().navigate(R.id.action_petFragment_to_vaccinationFragment)
        }

        card_medicines.setOnClickListener {
            findNavController().navigate(R.id.action_petFragment_to_notesFragment)
        }

        card_visits.setOnClickListener {
            findNavController().navigate(R.id.action_petFragment_to_visitsFragment)
        }

        card_back.setOnClickListener {
            val fragment = PetInformationFragment()
            val transaction: FragmentTransaction? = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.rl_pet_fragment, fragment)
            transaction?.commit()
        }
    }
}