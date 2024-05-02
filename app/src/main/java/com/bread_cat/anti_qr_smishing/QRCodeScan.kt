package com.bread_cat.anti_qr_smishing

import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class QRCodeScan(private val act: MainActivity) {
    fun startQRscan() {
        val intentIntegrator = IntentIntegrator(act)
        intentIntegrator.setPrompt("안내선 안에 QR코드를 맞춰주세요")
        intentIntegrator.setOrientationLocked(false)
        intentIntegrator.setBeepEnabled(false)
        activityResult.launch(intentIntegrator.createScanIntent())
    }

    private val activityResult = act.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        val data = result.data
        val intentResult: IntentResult ? = IntentIntegrator.parseActivityResult(result.resultCode, data)
        if(intentResult != null) {
            if(intentResult.contents != null) {
                Toast.makeText(act, "QR Link : ${intentResult.contents}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(act, "QR코드에 문제가 있는 것 같습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(act, "QR코드 스캔에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
