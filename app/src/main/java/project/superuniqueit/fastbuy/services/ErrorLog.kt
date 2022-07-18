package project.superuniqueit.fastbuy.services

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import project.superuniqueit.fastbuy.data.model.Log

import java.util.*

class ErrorLog {

    companion object {
        var database: FirebaseDatabase? = FirebaseDatabase.getInstance()
        var errorLog: DatabaseReference? = database?.getReference("Log")

    }

    fun delete() {

        try {
            errorLog?.removeValue()
                ?.addOnSuccessListener {
                    println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx addOnSuccessListener")
                }?.addOnFailureListener {
                    println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx addOnFailureListener")
                }
        } catch (e: Exception) {

        }
    }



    fun setError(_crash: String) {

        try {
            var logError = Log().apply {
                crash = _crash
            }

            var id = UUID.randomUUID().toString()

            errorLog?.child(id)?.setValue(logError)
                ?.addOnSuccessListener {

                }?.addOnFailureListener {

            }
        } catch (e: Exception) {

        }
    }



}