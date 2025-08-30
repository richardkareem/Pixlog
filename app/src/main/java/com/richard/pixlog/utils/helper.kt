package com.richard.pixlog.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.richard.pixlog.BuildConfig
import com.richard.pixlog.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
private const val MAXIMAL_SIZE = 1000000
private val timeStamp : String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
val EMAIL_ADDRESS_PATTERN = Pattern.compile(
    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
)

val PASSWORD_PATTERN = Pattern.compile(
    "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\\\S+\$).{4,}\$"
)

val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")

fun isValidStringEmail(str: String): Boolean{
    return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
}
fun isValidPassword(str: String): Boolean = passwordRegex.matches(str)

fun Int.dpToPx(context: Context) : Int = (this * context.resources.displayMetrics.density).toInt()

//helper AppExecutors
class AppExecutors {
    val diskIO: Executor = Executors.newSingleThreadExecutor() //membaca atau menulis ke db
    // menjalankan tugas dengan 3 thread (menjalankan 3 tugas sekaligus bersamaan)
    val networkIO: Executor = Executors.newFixedThreadPool(3)
    // menjalankan tugas di main thread
    val mainThread: Executor = MainThreadExecutor()

    private class MainThreadExecutor : Executor {
        // memastikan tugas di eksekusi di main thread
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

}

fun Context.formatTimeAgo(
    dateString: String,
    inputFormat: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
): String {
    return try {
        // Try multiple date formats to handle different API responses
        val dateFormats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
            
        )
        
        var date: Date? = null
        var usedFormat = ""
        
        for (format in dateFormats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                sdf.timeZone = java.util.TimeZone.getTimeZone("UTC") // Assume UTC if no timezone specified
                date = sdf.parse(dateString)
                if (date != null) {
                    usedFormat = format
                    break
                }
            } catch (e: Exception) {
                // Continue to next format
            }
        }
        
        if (date == null) {
            Log.e("formatTimeAgo", "Could not parse date: $dateString")
            return getString(R.string.invalid_date)
        }
        
        val now = Calendar.getInstance().time
        val diffInMillis = now.time - date.time
        if (diffInMillis < 0) {
            val absDiffInMillis = kotlin.math.abs(diffInMillis)
            val hoursDiff = TimeUnit.MILLISECONDS.toHours(absDiffInMillis)
            
            return when {
                hoursDiff < 1 -> getString(R.string.just_now)
                hoursDiff < 24 -> resources.getQuantityString(R.plurals.hours_ago, hoursDiff.toInt(), hoursDiff)
                else -> getString(R.string.just_now)
            }
        }

        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
        val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
        val weeks = days / 7
        val months = days / 30
        val years = days / 365

        when {
            years > 0 -> resources.getQuantityString(R.plurals.years_ago, years.toInt(), years)
            months > 0 -> resources.getQuantityString(R.plurals.months_ago, months.toInt(), months)
            weeks > 0 -> resources.getQuantityString(R.plurals.weeks_ago, weeks.toInt(), weeks)
            days > 0 -> resources.getQuantityString(R.plurals.days_ago, days.toInt(), days)
            hours > 0 -> resources.getQuantityString(R.plurals.hours_ago, hours.toInt(), hours)
            minutes > 0 -> resources.getQuantityString(R.plurals.minutes_ago, minutes.toInt(), minutes)
            else -> getString(R.string.just_now)
        }
    } catch (e: Exception) {
        Log.e("formatTimeAgo", "Error formatting time for: $dateString", e)
        getString(R.string.invalid_date)
    }
}

fun createCustomTempFile (context: Context) : File{
    val filesDir = context.externalCacheDir
    return File.createTempFile(timeStamp,".jpg", filesDir)
}

fun File.reduceFileImage() : File{
    val file  = this
    val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)

    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > MAXIMAL_SIZE)
    bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

    return file
}

//orientation for upload
fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
    val orientation = ExifInterface(file).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
        ExifInterface.ORIENTATION_NORMAL -> this
        else -> this
    }
}

fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(
        source, 0, 0, source.width, source.height, matrix, true
    )
}

fun getImageUri(context: Context): Uri{
    var uri : Uri? = null
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){ // api 29 keatas
        val contentValue = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
        }
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValue
        )

    }

    return uri ?: getImageUriForPreq(context)
}
private fun getImageUriForPreq(context: Context): Uri{
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
    if(imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
    return  FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.fileprovider",
        imageFile
    )

}

fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = createCustomTempFile(context)
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
    outputStream.close()
    inputStream.close()
    return myFile
}

fun String.withDateFormat(): String {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    val date = format.parse(this) as Date
    return DateFormat.getDateInstance(DateFormat.FULL).format(date)
}