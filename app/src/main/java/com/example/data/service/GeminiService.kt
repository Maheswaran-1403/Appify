package com.example.data.service

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val API_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    suspend fun generateApp(prompt: String): String? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is placeholder or empty. Falling back to offline generator.")
            return@withContext null
        }

        val systemPrompt = """
            You are the Supervisor Agent of AgentForge, a 40+ multi-agent software factory. Your job is to take a prompt from the user and generate a complete structured application specification.
            You must return a RAW JSON object matching this exact schema. Do NOT wrap the JSON in markdown code blocks like ```json ... ```. Just return the pure JSON text.
            
            Schema:
            {
              "appName": "Name of the generated app",
              "logs": [
                {
                  "agentName": "Supervisor Agent | Planner Agent | Backend Generation Agent | ...",
                  "dept": "Core Management | Data & Analytics | Machine Learning | Software Engineering | Quality Assurance | DevOps & Infra | Technical Docs | User Relations",
                  "msg": "Log message from that agent describing their decision, reasoning, or code implementation detail for this prompt.",
                  "severity": "INFO | SUCCESS | WARN | ERROR"
                }
              ],
              "files": [
                {
                  "path": "path/to/file",
                  "content": "The actual, real, non-placeholder source code or markdown of the file",
                  "language": "kotlin | json | yaml | markdown | xml | dockerfile",
                  "size": "File size estimate (e.g., 2.1 KB)"
                }
              ],
              "emulator": {
                "title": "Title of the running app",
                "theme": "Dark | Light",
                "screens": [
                  {
                    "tabName": "Home | Dashboard | Logs | Settings | etc.",
                    "icon": "home | settings | list | chart | info",
                    "widgets": [
                      {
                        "type": "text | button | input | list | card | chart",
                        "title": "Widget title or label",
                        "subtitle": "Widget value, description, or subtitle text",
                        "items": ["List item 1", "List item 2", "List item 3"],
                        "chartData": [10, 25, 12, 45, 30],
                        "action": "Action description (e.g., 'toast:Action completed successfully!')"
                      }
                    ]
                  }
                ]
              }
            }

            Make sure to include logs for at least 15-20 different agents from the list of 40+ agents. Each log should be realistic, detailed, and relevant to the user prompt.
            The files array should contain real files (at least 4-5 files like MainActivity.kt, Database.kt, Dockerfile, api.yaml, README.md, etc.) with real, complete code that implements the requested app concept.
            The emulator screens should define a fully featured, functional layout of the app so the user can interact with it.
        """.trimIndent()

        val jsonRequest = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "User prompt to build an app: $prompt")
                        })
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", systemPrompt)
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.7f)
            })
        }

        val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val url = "$API_URL?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "Request failed with code: ${response.code}, message: ${response.message}")
                    return@withContext null
                }
                val bodyStr = response.body?.string() ?: return@withContext null
                val responseJson = JSONObject(bodyStr)
                val candidates = responseJson.getJSONArray("candidates")
                if (candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    if (parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).getString("text")
                    }
                }
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calling Gemini API", e)
            null
        }
    }
}
