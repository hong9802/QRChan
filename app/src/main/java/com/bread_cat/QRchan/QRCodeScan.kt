package com.bread_cat.QRchan

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QRCodeScan(private val act: MainActivity) {

    val qrRecords = mutableListOf<QRRecord>()
    fun startQRscan() {
        val intentIntegrator = IntentIntegrator(act)
        intentIntegrator.setPrompt("안내선 안에 QR코드를 맞춰주세요")
        intentIntegrator.setOrientationLocked(false)
        intentIntegrator.setBeepEnabled(false)
        activityResult.launch(intentIntegrator.createScanIntent())
    }

    private fun addQRRecord(content: String) {
        var currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        qrRecords.add(QRRecord(content, currentTime))
    }

    fun openSafeUrl(url: String) {
        if (url.isNullOrEmpty() || !isValidUrl(url)) {
            Toast.makeText(act, "유효하지 않은 URL입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            act.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(act, "웹 브라우저 설치를 확인해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    fun isValidUrl(url: String): Boolean {
        val urlPattern = "^(http|https)://.*$".toRegex()
        return urlPattern.matches(url)
    }

    private fun handleQRCodeContent(content: String) {
        addQRRecord(content)
        when {
            content.startsWith("WIFI:") -> handleWifiQRCode(content)
            content.startsWith("http") -> checkUrlSafety(content)
            content.startsWith("BEGIN:VCARD") || content.startsWith("MECARD:") -> handleContactQRCode(content)
            else -> {
                Toast.makeText(act, "QR 코드 내용: $content", Toast.LENGTH_SHORT).show()
                // 다른 형식의 QR 코드일 경우 추가 처리
            }
        }
    }

    private fun handleWifiQRCode(content: String) {
        val wifiInfo = content.substringAfter("WIFI:")
        val params = wifiInfo.split(";")
        var ssid = ""
        var password = ""
        var type = ""

        for (param in params) {
            when {
                param.startsWith("S:") -> ssid = param.substringAfter("S:")
                param.startsWith("P:") -> password = param.substringAfter("P:")
                param.startsWith("T:") -> type = param.substringAfter("T:")
            }
        }

        if (ssid.isNotEmpty()) {
            copyToClipboard("WIFI Password", password)
            Toast.makeText(act, "SSID가 $ssid 비밀번호가 복사되었습니다.", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(act, "유효하지 않은 와이파이 QR 코드입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleContactQRCode(content: String) {
        val contactIntent = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE

            if (content.startsWith("BEGIN:VCARD")) {
                val vcard = parseVCard(content)
                putExtra(ContactsContract.Intents.Insert.NAME, vcard["name"])
                putExtra(ContactsContract.Intents.Insert.PHONE, vcard["phone"])
                putExtra(ContactsContract.Intents.Insert.EMAIL, vcard["email"])
            } else if (content.startsWith("MECARD")) {
                val mecard = parseMECARD(content)
                putExtra(ContactsContract.Intents.Insert.NAME, mecard["name"])
                putExtra(ContactsContract.Intents.Insert.PHONE, mecard["phone"])
                putExtra(ContactsContract.Intents.Insert.EMAIL, mecard["email"])
            }
        }
        act.startActivity(contactIntent)
    }

    private fun parseVCard(vcard: String): Map<String, String> {
        val contactInfo = mutableMapOf<String, String>()
        val lines = vcard.split("\n")
        for (line in lines) {
            when {
                line.startsWith("FN:") -> contactInfo["name"] = line.substringAfter("FN:")
                line.startsWith("TEL:") -> contactInfo["phone"] = line.substringAfter("TEL:")
                line.startsWith("EMAIL:") -> contactInfo["email"] = line.substringAfter("EMAIL:")
            }
        }
        return contactInfo
    }

    private fun parseMECARD(mecard: String): Map<String, String> {
        val contactInfo = mutableMapOf<String, String>()
        val params = mecard.substringAfter("MECARD:").split(";")

        for (param in params) {
            when {
                param.startsWith("N:") -> contactInfo["name"] = param.substringAfter("N:")
                param.startsWith("TEL:") -> contactInfo["phone"] = param.substringAfter("TEL:")
                param.startsWith("EMAIL:") -> contactInfo["email"] = param.substringAfter("EMAIL:")
            }
        }

        return contactInfo
    }

    private fun copyToClipboard(label: String, text: String) {
        val clipboard = act.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

    private val activityResult = act.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        val data = result.data
        val intentResult: IntentResult? = IntentIntegrator.parseActivityResult(result.resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents != null) {
                val scannedUrl = intentResult.contents
                // QR 코드에서 읽은 URL을 Safe Browsing API로 전송
                handleQRCodeContent(scannedUrl)
            } else {
                Toast.makeText(act, "QR코드에 문제가 있는 것 같습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(act, "QR코드 스캔에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryResultLauncher.launch(intent)
    }

    private val galleryResultLauncher = act.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data
        if (result.resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                extractQRCodeFromImage(imageUri)
            } else {
                Toast.makeText(act, "이미지를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun extractQRCodeFromImage(imageUri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(act.contentResolver, imageUri)
        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        try {
            val result = MultiFormatReader().decode(binaryBitmap)
            handleQRCodeContent(result.text)
        } catch (e: Exception) {
            Toast.makeText(act, "QR 코드를 인식할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkUrlSafety(scannedUrl: String) {
        val apiKey = "YOUR API KEY" // Safe Browsing API 키

        // 요청 모델 생성
        val request = SafeBrowsingRequest(
            client = ClientInfo("anti_qr_smishing", "1.0"),
            threatInfo = ThreatInfo(
                threatTypes = listOf("MALWARE", "SOCIAL_ENGINEERING"),  // 멀웨어 및 피싱 검사
                platformTypes = listOf("ANY_PLATFORM"),
                threatEntryTypes = listOf("URL"),
                threatEntries = listOf(ThreatEntry(scannedUrl))
            )
        )

        val service = RetrofitClient.getService()
        val call = service.checkUrl(apiKey, request)

        // API 호출
        call.enqueue(object : Callback<SafeBrowsingResponse> {
            override fun onResponse(call: Call<SafeBrowsingResponse>, response: Response<SafeBrowsingResponse>) {
                if (response.isSuccessful && response.body()?.matches != null) {
                    // URL이 악성 사이트로 판명됨
                    Toast.makeText(act, "위험한 사이트입니다: $scannedUrl", Toast.LENGTH_LONG).show()
                } else {
                    // 안전한 사이트
                    Toast.makeText(act, "안전한 사이트입니다: $scannedUrl", Toast.LENGTH_SHORT).show()
                    openSafeUrl(scannedUrl)
                }
            }

            override fun onFailure(call: Call<SafeBrowsingResponse>, t: Throwable) {
                Toast.makeText(act, "안전성 확인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

