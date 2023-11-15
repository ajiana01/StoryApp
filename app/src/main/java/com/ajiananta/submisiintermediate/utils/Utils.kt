package com.ajiananta.submisiintermediate.utils

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Geocoder
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.ajiananta.submisiintermediate.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private const val FILENAME_FORMAT = "dd-MMM-yyyy"
private const val MAX_IMAGE_SIZE = 1024 * 1024
val timeStamp: String by lazy {
    SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
}

fun String.withDateFormat(): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val date = inputFormat.parse(this) as Date
    val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
    return outputFormat.format(date)
}

fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
    val matrix = Matrix()
    return if (isBackCamera) {
        matrix.postRotate(90f)
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width ,bitmap.height, matrix,  true)
    } else {
        matrix.postRotate(-90f)
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val photoFile = createCustomTempFile(context)

    contentResolver.openInputStream(selectedImg)?.use { inputStream ->
        FileOutputStream(photoFile).use { outputStream ->
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        }
    }
    return photoFile
}

fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpeg", storageDir)
}

fun reduceImg(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 3
    } while (streamLength > MAX_IMAGE_SIZE)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun createFiles(application: Application): File {
    val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
        File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    val outputDirectory = if (
        mediaDir != null && mediaDir.exists()
    ) mediaDir else application.filesDir
    return File(outputDirectory, "$timeStamp.jpg")
}

@Suppress("DEPRECATION")
fun getAddName(context: Context, lat: Double, lon: Double): String? {
    var addName: String? = null
    val geocoder = Geocoder(context, Locale.getDefault())
    try {
        val list = geocoder.getFromLocation(lat, lon, 1)
        if (list != null && list.size != 0) {
            addName = list[0].getAddressLine(0)
            Log.d(ContentValues.TAG, "Addres Name: $addName")
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return addName
}
