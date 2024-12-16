package com.bread_cat.QRchan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bread_cat.QRchan.databinding.ActivityLicenseBinding

class LicenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLicenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLicenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 고지사항
        binding.licenseTextView.text = """
            고지 정보

            QRChan 앱은 다음과 같은 라이브러리 및 서비스를 사용합니다:

            1. ZXing 라이브러리
            이 앱은 QR 코드 인식 및 생성을 위해 ZXing 라이브러리를 사용합니다. ZXing은 Apache License 2.0 하에 배포됩니다.
            Apache License 2.0 전문: https://www.apache.org/licenses/LICENSE-2.0

            --------------------------------------------------------------------------------
            ZXing 관련 고지:
            --------------------------------------------------------------------------------

            2. Google Safe Browsing API
            이 앱은 Google Safe Browsing API를 사용하여 QR 코드로 연결된 URL의 안전성을 확인합니다. 
            Google Safe Browsing API는 다음과 같은 조건 하에 제공됩니다:
              - 서비스의 정확성과 안정성을 보장하지 않습니다.
              - 상업적 목적으로 사용할 수 없습니다.

            Google API 서비스 약관: https://developers.google.com/terms

            --------------------------------------------------------------------------------
            책임 고지:
            --------------------------------------------------------------------------------
            - QRChan은 Google Safe Browsing API를 통해 제공된 정보의 정확성 및 안정성을 보장하지 않습니다.
            - Google Safe Browsing API의 할당량 제한 또는 외부 서비스 오류로 인해 일시적으로 기능이 제한될 수 있습니다.
            - QRChan은 외부 서비스의 결과로 인해 발생한 문제에 대해 책임지지 않습니다.
            - QRChan은 Google과 공식적으로 협력하거나 제휴한 서비스가 아닙니다.

            --------------------------------------------------------------------------------
            추가 라이브러리 고지:
            --------------------------------------------------------------------------------

            Barcode4J:
            Copyright 2002-2010 Jeremias Märki
            Copyright 2005-2006 Dietmar Bürkle

            Barcode4J는 Apache License 하에 배포되며, 자세한 정보는 다음 링크에서 확인할 수 있습니다:
            http://barcode4j.sourceforge.net/contributors.html

            JCommander:
            Copyright 2010 Cedric Beust cedric@beust.com
        """.trimIndent()
    }
}
