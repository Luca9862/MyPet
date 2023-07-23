package data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

//questa classe serve per tenere traccia dei riferimenti a firebase e per inizializzare la sincroniz-
//zazione offline
class FirebaseDBHelper {
    companion object{

        val RT = FirebaseDatabase
            .getInstance("https://my-pet-application-default-rtdb.europe-west1.firebasedatabase.app")
            .setPersistenceEnabled(true)

        val ST = FirebaseDatabase.getInstance("gs://my-pet-application.appspot.com")
            .setPersistenceEnabled(true)

        val dbRefRT = FirebaseDatabase
            .getInstance("https://my-pet-application-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

        val dbRefPets = dbRefRT
            .child("pets")

        val dbRefNotes = dbRefRT
            .child("notes")

        val dbRefST = FirebaseStorage
            .getInstance("gs://my-pet-application.appspot.com")
            .getReference("users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)

    }
}