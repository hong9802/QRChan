package com.bread_cat.QRchan

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bread_cat.QRchan.databinding.ActivityQrGenerateBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.io.IOException
import java.io.OutputStream

class QRGenerateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrGenerateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrGenerateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioUrl -> {
                    showUrlFields()
                }
                R.id.radioContact -> {
                    showContactFields()
                }
                R.id.radioWifi -> {
                    showWifiFields()
                }
            }
        }

        binding.generateButton.setOnClickListener {
            val qrBitmap = when (binding.radioGroup.checkedRadioButtonId) {
                R.id.radioUrl -> createQRCode(binding.urlInput.text.toString())
                R.id.radioContact -> createQRCode("MECARD:N:${binding.contactNameInput.text};TEL:${binding.contactPhoneInput.text};EMAIL:${binding.contactEmailInput.text};ADR:${binding.contactAddressInput.text};;")
                R.id.radioWifi -> createQRCode("WIFI:S:${binding.wifiSsidInput.text};T:WPA;P:${binding.wifiPasswordInput.text};;")
                else -> null
            }
            if (qrBitmap != null) {
                binding.qrCodeImage.setImageBitmap(qrBitmap)
                Toast.makeText(this, "QR 코드가 생성되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "필수 정보를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.qrCodeImage.setOnLongClickListener {
            val bitmap = (binding.qrCodeImage.drawable as BitmapDrawable).bitmap
            saveQRCodeToGallery(bitmap)
            true
        }
    }

    private fun showUrlFields() {
        binding.urlInputLayout.visibility = View.VISIBLE
        binding.contactLayout.visibility = View.GONE
        binding.wifiLayout.visibility = View.GONE
    }

    private fun showContactFields() {
        binding.urlInputLayout.visibility = View.GONE
        binding.contactLayout.visibility = View.VISIBLE
        binding.wifiLayout.visibility = View.GONE
    }

    private fun showWifiFields() {
        binding.urlInputLayout.visibility = View.GONE
        binding.contactLayout.visibility = View.GONE
        binding.wifiLayout.visibility = View.VISIBLE
    }

    private fun createQRCode(data: String): Bitmap? {
        return try {
            val hints = mapOf(EncodeHintType.CHARACTER_SET to "UTF-8")
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565).apply {
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        setPixel(x, y, if (bitMatrix[x, y]) -0x1000000 else -0x1)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveQRCodeToGallery(bitmap: Bitmap) {
        val filename = "QRCode_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QR Codes")
        }

        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            var outputStream: OutputStream? = null
            try {
                outputStream = resolver.openOutputStream(uri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                Toast.makeText(this, "QR 코드가 저장되었습니다.", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
            } finally {
                outputStream?.close()
            }
        } else {
            Toast.makeText(this, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
