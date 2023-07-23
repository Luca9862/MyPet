package it.pdm.app

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import authentication.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import it.pdm.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val SHARED_PREFS = "sharedPrefs"
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(userIsNull()){
            logOut()
        }
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = Navigation.findNavController(this, R.id.activity_main_nav_host_fragment)
        setupWithNavController(binding.bottomNavigationView, navController)

        //quando non siamo nei fragment principali la navbar viene nascosta
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id != R.id.petFragment && destination.id != R.id.homeFragment && destination.id != R.id.settingsFragment) {

                binding.bottomNavigationView.visibility = View.GONE //CON IL .GONE la navBar viene tolta
            } else {

                binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }

        val navController = findNavController(R.id.activity_main_nav_host_fragment)
        val config = AppBarConfiguration(navController.graph)
        //findViewById<Toolbar>(R.id.toolbarTb).setupWithNavController(navController, config)
        //setSupportActionBar(toolbar)
        
    }

    private fun userIsNull(): Boolean{
        var ok = false
        if(FirebaseAuth.getInstance().currentUser == null)
            ok = true

        return ok
    }

    private fun logOut(){

        val sharedPreferences: SharedPreferences? = this.getSharedPreferences(SHARED_PREFS,
            MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor? = sharedPreferences?.edit()
        editor?.putString("name", "")
        editor?.apply()

        val intent = Intent(this, RegisterActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}

