package home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import it.pdm.app.R
import it.pdm.app.databinding.FragmentHomeBinding


class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.stepCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_blankFragment)//navigazione
        }
        binding.visitsCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_visitsFragment)
        }
        binding.notesCard.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_notesFragment)
        }

        binding.emergencyCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_emergencyNumbersFragment)
        }
    }
}