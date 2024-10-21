package com.bread_cat.QRchan

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

// Safe Browsing API Request/Response 모델 설정
data class SafeBrowsingRequest(val client: ClientInfo, val threatInfo: ThreatInfo)
data class ClientInfo(val clientId: String, val clientVersion: String)
data class ThreatInfo(val threatTypes: List<String>, val platformTypes: List<String>, val threatEntryTypes: List<String>, val threatEntries: List<ThreatEntry>)
data class ThreatEntry(val url: String)
data class SafeBrowsingResponse(val matches: List<ThreatMatch>?)
data class ThreatMatch(val threatType: String, val platformType: String, val threat: ThreatEntry)

interface SafeBrowsingService {
    @POST("v4/threatMatches:find")
    fun checkUrl(
        @Query("key") apiKey: String, // API 키를 쿼리 매개변수로 받습니다.
        @Body request: SafeBrowsingRequest
    ): Call<SafeBrowsingResponse>
}

