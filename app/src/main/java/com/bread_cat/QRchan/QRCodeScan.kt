package com.bread_cat.QRchan

import android.app.Activity
import android.app.AlertDialog
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

    private fun showWarningDialog(context: Context, scannedUrl: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("경고: 잠재적으로 위험한 사이트")
        builder.setMessage(
            """
            Google Safe Browsing API에 따르면 이 URL이 잠재적으로 위험할 수 있는 사이트로 식별되었습니다.
            URL: $scannedUrl

            이 사이트는 다음과 같은 위험을 포함할 수 있습니다:
            - 소셜 엔지니어링(피싱): 사용자를 속여 개인 정보를 유출하도록 유도할 수 있습니다.
            - 멀웨어: 사용자의 기기에 해를 끼칠 수 있는 악성 코드를 포함할 수 있습니다.
            - 원치 않는 소프트웨어: 브라우저 환경에 영향을 주는 프로그램이 포함될 수 있습니다.
            - 잠재적으로 유해한 애플리케이션(PHA): 사용자 정보를 도용하거나 삭제할 수 있는 위험한 앱을 설치하려 시도할 수 있습니다.

            경고는 Google Safe Browsing 데이터를 기반으로 생성되었습니다. 추가적인 정보는 아래 링크를 참조하세요:
            - Google Safe Browsing API: https://developers.google.com/safe-browsing
            - 소셜 엔지니어링 방지 정보: www.antiphishing.org

            (이 경고는 Google Safe Browsing API 데이터를 기반으로 생성되었습니다.)
    """.trimIndent()
        )

        builder.setCancelable(true)

        builder.setPositiveButton("확인") { dialog, _ ->
            dialog.dismiss()
        }

        builder.setNegativeButton("잠재적 위험을 인지하고 접속") { _, _ ->
            openSafeUrl(context, scannedUrl)
        }

        val dialog = builder.create()
        dialog.show()
    }



    private fun openSafeUrl(context: Context, scannedUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scannedUrl))
        context.startActivity(intent)
    }



    private fun checkUrlSafety(scannedUrl: String) {
        val apiKey = "YOUR API KEY" // Safe Browsing API 키

        // 요청 모델 생성
        val request = SafeBrowsingRequest(
            client = ClientInfo("anti_qr_smishing", "1.0"),
            threatInfo = ThreatInfo(
                threatTypes = listOf("MALWARE", "SOCIAL_ENGINEERING", "THREAT_TYPE_UNSPECIFIED", "UNWANTED_SOFTWARE", "POTENTIALLY_HARMFUL_APPLICATION"),  // 멀웨어 및 피싱 검사
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
                    showWarningDialog(act, scannedUrl)
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

