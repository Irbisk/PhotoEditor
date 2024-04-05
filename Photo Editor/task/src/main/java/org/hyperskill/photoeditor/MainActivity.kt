package org.hyperskill.photoeditor

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.alpha
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.pow


class MainActivity : AppCompatActivity() {

    private val activityResultLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val photoUri = result.data?.data ?: return@registerForActivityResult
                currentImage.setImageURI(photoUri)
                defaultImage = currentImage.drawable.toBitmap()
            }
        }

    private lateinit var defaultImage: Bitmap
    private lateinit var currentImage: ImageView
    private lateinit var galleryBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var brightnessSlider: Slider
    private lateinit var contrastSlider: Slider
    private lateinit var saturationSlider: Slider
    private lateinit var gammaSlider: Slider
    private var lastJob: Job? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()

        //do not change this line
        currentImage.setImageBitmap(createBitmap())

        defaultImage = currentImage.drawable.toBitmap()

        galleryBtn.setOnClickListener {
            activityResultLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }


/*        brightnessSlider.addOnChangeListener { _, value, _ ->
            var bitmap = defaultImage.copy(Bitmap.Config.ARGB_8888, true)
            bitmap = adjustBrightness(bitmap, value)
            bitmap = adjustContrast(bitmap, contrastSlider.value)
            bitmap = adjustSaturation(bitmap, saturationSlider.value)
            bitmap = adjustGamma(bitmap, gammaSlider.value)
            currentImage.setImageBitmap(bitmap)

        }

        contrastSlider.addOnChangeListener { _, value, _ ->
            var bitmap = defaultImage.copy(Bitmap.Config.ARGB_8888, true)
            bitmap = adjustBrightness(bitmap, brightnessSlider.value)
            bitmap = adjustContrast(bitmap, value)
            bitmap = adjustSaturation(bitmap, saturationSlider.value)
            bitmap = adjustGamma(bitmap, gammaSlider.value)
            currentImage.setImageBitmap(bitmap)

        }

        saturationSlider.addOnChangeListener { _, value, _ ->
            var bitmap = defaultImage.copy(Bitmap.Config.ARGB_8888, true)
            bitmap = adjustBrightness(bitmap, brightnessSlider.value)
            bitmap = adjustContrast(bitmap, contrastSlider.value)
            bitmap = adjustSaturation(bitmap, value)
            bitmap = adjustGamma(bitmap, gammaSlider.value)
            currentImage.setImageBitmap(bitmap)

        }

        gammaSlider.addOnChangeListener { _, value, _ ->
            var bitmap = defaultImage.copy(Bitmap.Config.ARGB_8888, true)
            bitmap = adjustBrightness(bitmap, brightnessSlider.value)
            bitmap = adjustContrast(bitmap, contrastSlider.value)
            bitmap = adjustSaturation(bitmap, saturationSlider.value)
            bitmap = adjustGamma(bitmap, value)
            currentImage.setImageBitmap(bitmap)

        }*/

        brightnessSlider.addOnChangeListener { slider, value, fromUser ->
            onSliderChanges(slider, value, fromUser)
        }

        contrastSlider.addOnChangeListener { slider, value, fromUser ->
            onSliderChanges(slider, value, fromUser)
        }

        saturationSlider.addOnChangeListener { slider, value, fromUser ->
            onSliderChanges(slider, value, fromUser)
        }

        gammaSlider.addOnChangeListener { slider, value, fromUser ->
            onSliderChanges(slider, value, fromUser)
        }



        saveBtn.setOnClickListener { _ ->
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    println("11")
                    val bitmap: Bitmap = currentImage.drawable.toBitmap()
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    values.put(MediaStore.Images.ImageColumns.WIDTH, bitmap.width)
                    values.put(MediaStore.Images.ImageColumns.HEIGHT, bitmap.height)

                    val uri = this@MainActivity.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                    ) ?: return@setOnClickListener

                    contentResolver.openOutputStream(uri).use {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    }
                }

                ActivityCompat
                    .shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) -> {
                    println("12")
                    AlertDialog.Builder(this)
                        .setTitle("Permission required")
                        .setMessage("This app needs permission to access this feature.")
                        .setPositiveButton("Grant") { _, _ ->
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                0
                            )

                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }

                else -> {
                    println("13")
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        0
                    )
                }
            }
        }
    }

    private fun onSliderChanges(slider: Slider, sliderValue: Float, fromUser: Boolean) {

        lastJob?.cancel()

        lastJob = GlobalScope.launch(Dispatchers.Default) {

            val bitmap = defaultImage.copy(Bitmap.Config.ARGB_8888, true) ?: return@launch

            val brightenCopyDeferred: Deferred<Bitmap> = this.async {
                if (slider == brightnessSlider) {
                    adjustBrightness(bitmap, sliderValue)
                } else {
                    adjustBrightness(bitmap, brightnessSlider.value)
                }

            }
            val brightenCopy: Bitmap = brightenCopyDeferred.await()

            val contrastedCopy = if (slider == contrastSlider) {
                adjustContrast(brightenCopy, sliderValue)
            } else {
                adjustContrast(brightenCopy, contrastSlider.value)
            }

            val saturatedCopy = if (slider == saturationSlider) {
                adjustSaturation(contrastedCopy, sliderValue)
            } else {
                adjustSaturation(contrastedCopy, saturationSlider.value)
            }

            val gammaCopy = if (slider == gammaSlider) {
                adjustGamma(saturatedCopy, sliderValue)
            } else {
                adjustGamma(saturatedCopy, gammaSlider.value)
            }

            ensureActive()

            runOnUiThread {
                currentImage.setImageBitmap(gammaCopy)
            }
        }
    }

    private fun adjustBrightness(bitmap: Bitmap, value: Float): Bitmap {
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)

                val alpha = Color.alpha(pixel)
                val red = getRightColor(Color.red(pixel) + value.toInt())
                val green = getRightColor(Color.green(pixel) + value.toInt())
                val blue = getRightColor(Color.blue(pixel) + value.toInt())

                val colorNew = Color.argb(alpha, red, green, blue)
                bitmap.setPixel(x, y, colorNew)
            }
        }
        return bitmap
    }

    private fun adjustContrast(bitmap: Bitmap, value: Float): Bitmap {
        val avgBright = getAverageBrightness(bitmap)
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                val alpha = (255.0 + value) / (255.0 - value)
                val red = getRightColor((alpha * (Color.red(pixel) - avgBright) + avgBright).toInt())
                val green = getRightColor((alpha * (Color.green(pixel) - avgBright) + avgBright).toInt())
                val blue = getRightColor((alpha * (Color.blue(pixel) - avgBright) + avgBright).toInt())
                val colorNew = Color.argb(pixel.alpha, red, green, blue)
                bitmap.setPixel(x, y, colorNew)
            }
        }
        return bitmap
    }

    private fun adjustSaturation(bitmap: Bitmap, value: Float): Bitmap {
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                val alpha = (255.0 + value) / (255.0 - value)
                val rgbAvg = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                val red = getRightColor((alpha * (Color.red(pixel) - rgbAvg) + rgbAvg).toInt())
                val green = getRightColor((alpha * (Color.green(pixel) - rgbAvg) + rgbAvg).toInt())
                val blue = getRightColor((alpha * (Color.blue(pixel) - rgbAvg) + rgbAvg).toInt())
                val colorNew = Color.argb(pixel.alpha, red, green, blue)
                bitmap.setPixel(x, y, colorNew)
            }
        }
        return bitmap

    }

    private  fun adjustGamma(bitmap: Bitmap, value: Float): Bitmap {
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                val red = getRightColor(getGammaColor(Color.red(pixel), value))
                val green = getRightColor(getGammaColor(Color.green(pixel), value))
                val blue = getRightColor(getGammaColor(Color.blue(pixel), value))
                val colorNew = Color.argb(pixel.alpha, red, green, blue)
                bitmap.setPixel(x, y, colorNew)
            }
        }
        return bitmap

    }

    fun getGammaColor(color: Int, value: Float): Int {

        return (255 * ((color / 255.0).pow(value.toDouble()))).toInt()
    }

    private fun getAverageBrightness(bitmap: Bitmap): Int {
        var red = 0L
        var green = 0L
        var blue = 0L
        val pixelsNumber = bitmap.height.toLong() * bitmap.width.toLong()
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                red += Color.red(pixel)
                green += Color.green(pixel)
                blue += Color.blue(pixel)
            }
        }
        return ((red + green + blue) / (pixelsNumber * 3)).toInt()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted, use the restricted features
                    println("1")
                    saveBtn.callOnClick()
                } else {
                    // no permission, block access to this feature
                    println("2")
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                println("3")
            }
        }
    }

    private fun getRightColor(color: Int): Int {
        return if (color > 255) 255
        else if (color < 0) 0
        else color
    }


    private fun bindViews() {
        currentImage = findViewById(R.id.ivPhoto)
        galleryBtn = findViewById(R.id.btnGallery)
        saveBtn = findViewById(R.id.btnSave)
        brightnessSlider = findViewById(R.id.slBrightness)
        contrastSlider = findViewById(R.id.slContrast)
        saturationSlider = findViewById(R.id.slSaturation)
        gammaSlider = findViewById(R.id.slGamma)
    }

    // do not change this function
    fun createBitmap(): Bitmap {
        val width = 200
        val height = 100
        val pixels = IntArray(width * height)
        // get pixel array from source

        var R: Int
        var G: Int
        var B: Int
        var index: Int

        for (y in 0 until height) {
            for (x in 0 until width) {
                // get current index in 2D-matrix
                index = y * width + x
                // get color
                R = x % 100 + 40
                G = y % 100 + 80
                B = (x+y) % 100 + 120

                pixels[index] = Color.rgb(R,G,B)

            }
        }
        // output bitmap
        val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmapOut
    }
}