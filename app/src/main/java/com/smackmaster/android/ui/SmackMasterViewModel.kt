package com.smackmaster.android.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smackmaster.android.model.Tone
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class SmackMasterViewModel : ViewModel() {
    var comment by mutableStateOf("")
        private set
    var roast by mutableStateOf("")
        private set
    var selectedTone by mutableStateOf(Tone.NICE)
        private set
    var isProcessing by mutableStateOf(false)
        private set
    var warningMessage by mutableStateOf<String?>(null)
        private set
    var toastMessage by mutableStateOf<String?>(null)
        private set
    var isMinimized by mutableStateOf(false)
        private set
    var isMicGlowing by mutableStateOf(false)
        private set

    private val client: OkHttpClient = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .build()

    private val jsonFormatter = Json { ignoreUnknownKeys = true }

    fun updateComment(value: String) {
        comment = value
        if (value.isNotBlank()) {
            warningMessage = null
        }
    }

    fun setTone(tone: Tone) {
        if (isProcessing) return
        selectedTone = tone
    }

    fun toggleMicGlow() {
        isMicGlowing = !isMicGlowing
    }

    fun clear() {
        if (isProcessing) return
        comment = ""
        roast = ""
        warningMessage = null
        toastMessage = null
    }

    fun copy(callback: (String) -> Unit) {
        val current = roast.trim()
        if (current.isEmpty()) {
            toastMessage = "Nothing to copy"
            return
        }
        callback(current)
        toastMessage = "Copied!"
    }

    fun showToastDone() {
        toastMessage = null
    }

    fun minimize() {
        isMinimized = true
    }

    fun restore() {
        isMinimized = false
    }

    fun requestRoast(baseUrl: String, endpoint: String) {
        val trimmed = comment.trim()
        if (trimmed.isEmpty()) {
            warningMessage = "Paste something spicy to roast first."
            return
        }
        if (isProcessing) return
        isProcessing = true
        warningMessage = null
        toastMessage = null
        roast = ""

        viewModelScope.launch {
            try {
                val requestBody = RoastRequest(tone = selectedTone.name, comment = trimmed)
                val body = jsonFormatter.encodeToString(requestBody)
                    .toRequestBody("application/json; charset=utf-8".toMediaType())
                val request = Request.Builder()
                    .url(baseUrl.trimEnd('/') + endpoint)
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        val message = response.body?.string()
                        throw IOException(message ?: "Failed with code ${response.code}")
                    }
                    val payload = response.body?.string().orEmpty()
                    val roastResponse = jsonFormatter.decodeFromString<RoastResponse>(payload)
                    roast = roastResponse.roast.trim()
                    toastMessage = "Roast ready"
                }
            } catch (io: IOException) {
                warningMessage = io.message ?: "Unable to reach SmackMaster."
            } catch (throwable: Throwable) {
                warningMessage = throwable.message ?: "Something went wrong."
            } finally {
                isProcessing = false
            }
        }
    }

    fun scheduleToastClear(delayMillis: Long = 1200L) {
        if (toastMessage == null) return
        viewModelScope.launch {
            delay(delayMillis)
            toastMessage = null
        }
    }

    @Serializable
    private data class RoastRequest(
        @SerialName("tone") val tone: String,
        @SerialName("comment") val comment: String
    )

    @Serializable
    private data class RoastResponse(
        @SerialName("roast") val roast: String
    )
}
