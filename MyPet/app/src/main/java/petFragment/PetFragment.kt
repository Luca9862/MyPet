package it.pdm.app

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import data.FirebaseDBHelper
import kotlinx.android.synthetic.main.fragment_pet.*
import petFragment.PetInformationFragment
import petFragment.RegisterPet
import java.io.*

class Pet : Fragment() {

    private lateinit var user: FirebaseUser
    private lateinit var uId: String

    //companion object per tenere in riferimento i codici per accedere alla fotocamera/memoria
    //tramite il metodo StartActivityForResult
    companion object{
        const val CAMERA_PERMISSION_CODE = 1
        const val CAMERA_REQUEST_CODE = 2
        const val GALLERY_REQUEST_CODE = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        setPet()

        fab.setOnClickListener {
            //findNavController().navigate(R.id.action_petFragment_to_signupPetFragment)
            val intent = Intent(context, RegisterPet::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        fab_camera.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setCancelable(true)
            builder.setTitle("How?")
            builder.setMessage("Open camera or select picture from local storage?")
            builder.setPositiveButton("Camera"
            ) { _, _ -> takePicture()}
            builder.setNeutralButton("Gallery"
            ) { _, _ -> selectPictureFromDevice()}
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    //metodo per aprire la galleria
    private fun selectPictureFromDevice(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    //metodo per aprire la fotocamera
    private fun takePicture(){
        if (requireContext().let { it1 ->
                ContextCompat.checkSelfPermission(
                    it1,
                    Manifest.permission.CAMERA
                )
            } == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }
        else{
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    //override del metodo che verifica l'autorizzazione/permessi per la fotocamera
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== CAMERA_PERMISSION_CODE)
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(requireActivity(), permissions, requestCode)
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }else{
                Toast.makeText(context, "DENIED ACCESS, check device settings", Toast.LENGTH_LONG).show()
            }
    }

    //override che gestisce entrambe le opzioni (galleria/fotocamera) mediante dei controlli if;
    //in entrambe le situazioni l'immagine viene presa come bitmap. Per essere caricata sullo storage
    //bisogna comprimere il bitmap in un jpeg. Dopo, con l'ausilio di un oggetto ByteArrayOutputStream
    // in cui viene caricata la picture,
    //l'immagine viene effettivamente caricata sul firebase storage
    //EDIT: nell'opzione della galleria, se la foto viene caricata come bitmap l'app crasha.
    //è stato risolto castando l'immagine in un oggetto URI per poi convertirla in bitmap
    //tramite un apposito metodo. METODO FINITO
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imageRef = FirebaseDBHelper.dbRefST.child("images/pet_picture.jpg")
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CAMERA_REQUEST_CODE){
                val bitmap: Bitmap = data!!.extras!!.get("data") as Bitmap
                pet_picture.setImageBitmap(bitmap)
                saveBitmapToInternalStorage(bitmap)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                imageRef.putBytes(data)
                pet_picture.setImageBitmap(bitmap)
            }else if(requestCode == GALLERY_REQUEST_CODE){
                val uri: Uri = data!!.data as Uri
                val bitmap = convertUriToBitmap(uri)
                pet_picture.setImageBitmap(bitmap)
                saveBitmapToInternalStorage(bitmap)
                val baos = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                pet_picture.setImageBitmap(bitmap)
                imageRef.putBytes(data)
            }
        }
    }

    //questo metodo viene eseguito appena il fragment viene lanciato. Esso scarica dal DB l'immagine
    //e il nome dell'animale (ancora da correggere la lettura dal DB della foto)
    private fun setPet(){
        //progressBarPetFragment.visibility = View.VISIBLE
        val refRT = FirebaseDBHelper.dbRefRT.child("pets").child("name")
        refRT
            .addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    setPicturePet()
                    val fragment = PetInformationFragment()
                    val transaction: FragmentTransaction? = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.rl_pet_fragment, fragment)
                    transaction?.commit()

                    val data = snapshot.getValue(String::class.java)
                    if(tv_pet_name!=null){
                        tv_pet_name.text = data
                    }
                    setPetVisibility()
                } else {
                    if(fab!=null)
                        fab.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled",error.toException())            }
        })
    }

    //metodo che scarica la foto dal DB Storage oppure, se presente sul dispositivo, viene caricata da esso
    @SuppressLint("SuspiciousIndentation")
    private fun setPicturePet(){
        val directory = context?.getDir("imageDir", Context.MODE_PRIVATE)
        val file = File(directory, "your-image.jpg")
        if(file.exists()){
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            if(pet_picture!=null)
                pet_picture.setImageBitmap(bitmap)
        }else{
            val refPicture = FirebaseDBHelper.dbRefST.child("images/pet_picture.jpg")
            refPicture.getBytes(1000000000000000000).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeByteArray(it,0,it.size)
                    //saveBitmapToInternalStorage(bitmap)
                    if(pet_picture!=null)
                        pet_picture.setImageBitmap(bitmap)
            }
        }
    }

    //metodo che prende in input un URI e lo converte in BITMAP
    private fun convertUriToBitmap(uri: Uri): Bitmap? {
        val inputStream: InputStream?
        return try {
            inputStream = context?.contentResolver?.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
            null
        }
    }

    //metodo che prende in input un bitmap e lo salva in una cartella della app
    private fun saveBitmapToInternalStorage(bitmap: Bitmap?) {
        val directory = context?.getDir("imageDir", Context.MODE_PRIVATE)
        val file = File(directory, "your-image.jpg")
        val stream = FileOutputStream(file)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
    }

    //metodo che imposta la visibilità degli elementi del fragment che fanno riferimento all'animale
    private fun setPetVisibility(){
        if(pet_picture!=null)
            pet_picture.visibility = View.VISIBLE
        if(tv_pet_name!=null)
            tv_pet_name.visibility = View.VISIBLE
        if(rl_pet_fragment!=null)
            rl_pet_fragment.visibility = View.VISIBLE
        if(fab_camera!=null)
            fab_camera.visibility = View.VISIBLE
    }

    private fun init(){
        user = FirebaseAuth.getInstance().currentUser!!
        uId = user.uid
    }
}






