package com.example.myapplication.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.Tour
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class TourAdapter(private val c: Context, private val mList: MutableList<Tour>) : RecyclerView.Adapter<TourAdapter.ViewHolder>() {

    // Holds the views for adding it to image and text
    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.tourTitle)
        val textViewSub: TextView = itemView.findViewById(R.id.numStops)
        val menu: ImageView = itemView.findViewById(R.id.menus)
    }

    private fun popupMenus(v: View, tour: Tour) {
        //val position = mList[adapterPosition]
        val popupMenus = PopupMenu(c, v)
        popupMenus.inflate(R.menu.tour_menu)
        popupMenus.setOnMenuItemClickListener { it ->
            when (it.itemId) {
                R.id.startTour -> {

                    val inflater = LayoutInflater.from(c)
                    val v = inflater.inflate(R.layout.show_tour,null)
                    val addDialog = AlertDialog.Builder(c)
                    val dR = FirebaseDatabase.getInstance().getReference("tours")
                    var storageReference: StorageReference? = null
                    val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA)
                    val now = Date()
                    val fileName: String = formatter.format(now)
                    storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
                    addDialog.setView(v)
                    addDialog.create()
                    var displayedName = v.findViewById<TextView>(R.id.displaytourname)
                    var displayedAddress = v.findViewById<TextView>(R.id.displaytouraddress)
                    var displayedDescription = v.findViewById<TextView>(R.id.displaytourdescription)
                    var previousStop = v.findViewById<FloatingActionButton>(R.id.previousStop)
                    var nextStop = v.findViewById<FloatingActionButton>(R.id.nextStop)
                    var image = v.findViewById<ImageView>(R.id.imageShow)
                    var counterVar = 0
                    dR.child(tour.name).child("stops").get().addOnSuccessListener { it1 ->
                        dR.child(tour.name).child("images").get().addOnSuccessListener {
                            val first = it.child("0").value
                            val storageRef = FirebaseStorage.getInstance().reference.child("images/$first")
                            val localfile = File.createTempFile("tempImage", "jpg")
                            storageRef.getFile(localfile).addOnSuccessListener {
                                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                                image.setImageBitmap(bitmap)
                            }.addOnFailureListener {
                                Toast.makeText(c, "Failed to get image", Toast.LENGTH_SHORT).show()
                            }
                            //image.setImageURI(Uri.parse(storageReference.getFile(it1.child("0").value).toString()))
                        }.addOnFailureListener {
                            Toast.makeText(c, "Failed",Toast.LENGTH_SHORT).show()
                        }
                        displayedName.text = it1.child("0").child("name").value as CharSequence
                        displayedAddress.text = it1.child("0").child("location").value as CharSequence
                        displayedDescription.text = it1.child("0").child("description").value as CharSequence
                    }.addOnFailureListener {
                        Toast.makeText(c, "Failed",Toast.LENGTH_SHORT).show()
                    }
                    addDialog.show()
                    nextStop.setOnClickListener {
                        counterVar += 1
                        if (counterVar >= tour.stops.size) {
                            Toast.makeText(c, "No more next stops", Toast.LENGTH_SHORT).show()
                            counterVar -= 1
                        } else {
                            dR.child(tour.name).child("images").get().addOnSuccessListener {
                                val index = it.child(counterVar.toString()).value
                                val storageRef =
                                    FirebaseStorage.getInstance().reference.child("images/$index")
                                val localfile = File.createTempFile("tempImage", "jpg")
                                storageRef.getFile(localfile).addOnSuccessListener {
                                    val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                                    image.setImageBitmap(bitmap)
                                }.addOnFailureListener {
                                    Toast.makeText(c, "Failed to get image", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                            displayedName.text = tour.stops[counterVar].name
                            displayedAddress.text = tour.stops[counterVar].location
                            displayedDescription.text = tour.stops[counterVar].description
                        }
                    }
                    previousStop.setOnClickListener {
                        counterVar -= 1
                        if (counterVar < 0) {
                            Toast.makeText(c, "No more previous stops", Toast.LENGTH_SHORT).show()
                            counterVar += 1
                        } else {
                            dR.child(tour.name).child("images").get().addOnSuccessListener {
                                val index = it.child(counterVar.toString()).value
                                val storageRef =
                                    FirebaseStorage.getInstance().reference.child("images/$index")
                                val localfile = File.createTempFile("tempImage", "jpg")
                                storageRef.getFile(localfile).addOnSuccessListener {
                                    val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                                    image.setImageBitmap(bitmap)
                                }.addOnFailureListener {
                                    Toast.makeText(c, "Failed to get image", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                            displayedName.text = tour.stops[counterVar].name
                            displayedAddress.text = tour.stops[counterVar].location
                            displayedDescription.text = tour.stops[counterVar].description
                        }
                    }


                    /*Replace With Tour Start Code
                    Toast.makeText(
                        c,
                        "Going on a Tour",
                        Toast.LENGTH_LONG
                    ).show()*/
                    true
                }
                R.id.delete -> {
                    val builder = AlertDialog.Builder(c)
                    builder.setTitle("Deleting Tour")
                    builder.setMessage("Would you like to delete this tour? (this action cannot be undone)")
                    builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                        deleteTour(tour.name)
                        true
                    }
                    builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                        false
                    }
                    builder.show()
                    true
                }
                else -> true
            }
        }
        popupMenus.show()
        val popup = PopupMenu::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true
        val menu = popup.get(popupMenus)
        menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
            .invoke(menu,true)
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tour_list_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tour = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.textView.text = tour.name
        holder.textViewSub.text = "Stops: " + tour.stops.count()
        holder.menu.setOnClickListener {popupMenus(it, tour)}
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    private fun deleteTour(name: String): Boolean {
        // Getting the specified author reference.
        val dR = FirebaseDatabase.getInstance().getReference("tours").child(name)

        // Removing author.
        dR.removeValue()

        // Getting the titles reference for the specified author.
        val drStops = FirebaseDatabase.getInstance().getReference("stops").child(name)

        // Removing all titles.
        drStops.removeValue()

        Toast.makeText(
            c,
            "Tour Deleted",
            Toast.LENGTH_LONG
        ).show()

        return true
    }
}

private fun StorageReference.getFile(value: Any?) {

}

private fun ImageView.setImageURI(file: FileDownloadTask) {

}
