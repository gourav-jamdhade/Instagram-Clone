package com.example.instagramclone.Utils

import android.app.ProgressDialog
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

fun uploadImage(uri: Uri, folderName: String, callback: (String) -> Unit) {

    var imageUrl: String? = null
    FirebaseStorage.getInstance().getReference(folderName).child(UUID.randomUUID().toString())
        .putFile(uri)
        .addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {

                imageUrl = it.toString()

                callback(imageUrl!!)
            }
        }

}

fun uploadVideo(
    uri: Uri,
    folderName: String,
    progressDialog: ProgressDialog,
    callback: (String) -> Unit
) {

    var imageUrl: String? = null
    progressDialog.setTitle("Uploading . . .")
    progressDialog.show()
    FirebaseStorage.getInstance().getReference(folderName).child(UUID.randomUUID().toString())
        .putFile(uri)
        .addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {

                imageUrl = it.toString()

                callback(imageUrl!!)
            }
        }
        .addOnProgressListener {uploadTask ->
            val progress = (100.0 * uploadTask.bytesTransferred / uploadTask.totalByteCount).toInt()
            progressDialog.setMessage("Uploaded $progress%")

            if (progress >= 100) {
                progressDialog.dismiss()
            }
        }

}