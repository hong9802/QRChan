package com.bread_cat.QRchan

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Safe Browsing API Request/Response 모델 설정
data class SafeBrowsingRequest(val client: ClientInfo, val threatInfo: ThreatInfo)
data class ClientInfo(val clientId: String, val clientVersion: String)
data class ThreatInfo(val threatTypes: List<String>, val platformTypes: List<String>, val threatEntryTypes: List<String>, val threatEntries: List<ThreatEntry>)
data class ThreatEntry(val url: String)
data class SafeBrowsingResponse(val matches: List<ThreatMatch>?)
data class ThreatMatch(val threatType: String, val platformType: String, val threat: ThreatEntry)

interface SafeBrowsingService {
    @Headers("Content-Type: application/json")
    @POST("/v4/threatMatches:find")
    fun checkUrl(@Body request: SafeBrowsingRequest): Call<SafeBrowsingResponse>
}
