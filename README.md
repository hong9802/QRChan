<<<<<<< HEAD
<<<<<<< HEAD
# Anti_qshing
=======
=======
>>>>>>> c5ddf9e (Add LICENSE PAGE and ETC...)
# QRChan

QRChan은 QR 코드를 안전하게 인식하고 편하게 사용하기 위해 개발된 앱입니다.

## 주요 기능

1. **QR 코드 스캔 및 안전성 확인**  
   - QR 코드 스캔 시, Google Safe Browsing API를 통해 웹사이트의 안전 여부를 확인합니다.  
   - 안전하지 않은 웹사이트인 경우 경고 팝업을 통해 사용자에게 알립니다.  
   - 스캔할 수 있는 정보는 URL, 연락처, Wifi 정보입니다.  

2. **QR 코드 제작**  
   - URL, 연락처, Wi-Fi 정보 등 다양한 정보를 담은 QR 코드를 생성할 수 있습니다.  

3. **QR 코드 인식 내역 관리**  
   - 스캔한 QR 코드 기록을 표시합니다.  
   - 기록은 앱이 종료되면 자동으로 삭제됩니다.  

---

## 라이브러리 및 서비스

- **[ZXing](https://github.com/zxing/zxing)**  
  QR 코드 인식 및 제작을 위해 사용된 라이브러리입니다. ZXing은 Apache License 2.0 하에 제공됩니다.  

- **Google Safe Browsing API**  
  안전하지 않은 웹사이트 탐지를 위해 사용되며, Google의 서비스 정책에 따라 제공됩니다.  
  - **할당량 제한** : Google Safe Browsing API는 할당량 제한(쿼타)이 있을 수 있습니다. 일정량 이상의 요청이 발생하면 API 사용이 일시적으로 제한될 수 있습니다.  
  - **정확성 및 안정성** : Google Safe Browsing API는 탐지 결과의 정확성과 안정성을 보장하지 않습니다.  
  - **이 앱은 API를 통해 제공된 정보를 표시하며, Google과 공식적으로 협력하거나 제휴한 서비스가 아닙니다.**
  

---

## 책임 고지

이 앱은 안전한 QR 코드 사용을 도와주지만, Google Safe Browsing API와 같은 외부 서비스의 결과를 기반으로 작동합니다.  
- **API의 할당량 초과 및 서비스 오류로 인해 일시적으로 기능이 제한될 수 있습니다.**  
- **Google Safe Browsing API를 통해 제공된 경고는 Google에서 제공된 정보에 기반하며, 서비스의 정확성과 안정성을 보장하지 않습니다.**  
- **외부 서비스의 오류나 이 앱의 사용으로 인해 발생한 문제에 대해 개발자는 책임을 지지 않습니다.**  

---

## 라이선스

이 프로젝트는 **Apache License 2.0** 하에 배포됩니다.  
자세한 내용은 [LICENSE 파일](./LICENSE)을 확인하세요.
<<<<<<< HEAD
>>>>>>> c5ddf9e (Add LICENSE PAGE and ETC...)
=======
>>>>>>> c5ddf9e (Add LICENSE PAGE and ETC...)
