package com.example.polyclynic_kot

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class AIFragment : Fragment() {
    private lateinit var medicationApiService: MistralApiService
    private lateinit var scrollView: ScrollView
    private lateinit var medicationNameView: TextView
    private lateinit var compositionView: TextView
    private lateinit var usageView: TextView
    private lateinit var contraindicationsView: TextView
    private lateinit var constraintProgressBar: ConstraintLayout
    private lateinit var refreshButton: Button
    private lateinit var searchButton: Button
    private lateinit var medicationInput: EditText

    private lateinit var clearHistoryButton: Button
    private lateinit var historyAdapter: ArrayAdapter<String>

    private var lastQuery: String = ""
    private lateinit var sharedPreferences: SharedPreferences
    private val searchHistory = mutableListOf<String>()
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private lateinit var listViewHistory: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiKey = "ejl3O9z39jUcB70jLgNyobzqORZNJjNH"

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer $apiKey")
                val request = requestBuilder.build()
                Log.d("AIFragment", "Request: $request")
                chain.proceed(request)
            }
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.mistral.ai/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        medicationApiService = retrofit.create(MistralApiService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_a_i, container, false)

        scrollView = view.findViewById(R.id.scroll_medication)
        medicationNameView = view.findViewById(R.id.medication_name_text)
        compositionView = view.findViewById(R.id.composition_text)
        usageView = view.findViewById(R.id.usage_text)
        contraindicationsView = view.findViewById(R.id.contraindications_text)
        constraintProgressBar = view.findViewById(R.id.constraint_progress)
        refreshButton = view.findViewById(R.id.buttonRefresh)
        searchButton = view.findViewById(R.id.buttonSearch)
        medicationInput = view.findViewById(R.id.editTextMedication)
        clearHistoryButton = view.findViewById(R.id.buttonClearHistory)
        listViewHistory = view.findViewById(R.id.listViewHistory)

        sharedPreferences = requireContext().getSharedPreferences("search_prefs", Context.MODE_PRIVATE)

        historyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, searchHistory)
        listViewHistory.adapter = historyAdapter

        loadSearchHistory()

        medicationInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                loadSearchHistory()
                listViewHistory.visibility = if (hasFocus && searchHistory.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }

        listViewHistory.setOnItemClickListener { _, _, position, _ ->
            val selectedQuery = searchHistory[position]
            medicationInput.setText(selectedQuery)
            fetchData(selectedQuery)
            listViewHistory.visibility = View.GONE
        }

        searchButton.setOnClickListener {
            val query = medicationInput.text?.toString()?.trim() ?: ""
            if (query.isNotEmpty()) {
                addToHistory(query)
                fetchData(query)
                listViewHistory.visibility = View.GONE
            } else {
                Toast.makeText(requireContext(), "Введите название лекарства", Toast.LENGTH_SHORT).show()
            }
        }

        refreshButton.setOnClickListener {
            if (lastQuery.isNotEmpty()) {
                fetchData(lastQuery)
            }
        }

        clearHistoryButton.setOnClickListener {
            clearSearchHistory()
        }

        medicationInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { handler.removeCallbacks(it) }
                searchRunnable = kotlinx.coroutines.Runnable {
                    val query = medicationInput.text.toString().trim()
                    if (query.isNotEmpty()) {
                        fetchData(query)
                    }
                }
                handler.postDelayed(searchRunnable!!, 2000)
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        return view
    }

    private fun fetchData(request: String) {
        lastQuery = request
        constraintProgressBar.isVisible = true
        scrollView.isVisible = false
        refreshButton.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val medicationResponse = fetchMedicationInfo(request)
                Log.d("DEBUG", "Ответ от API: $medicationResponse")

                if (medicationResponse == null ||
                    medicationResponse.choices.isEmpty() ||
                    medicationResponse.choices[0].message.content.isNullOrEmpty()
                ) {
                    medicationNameView.text = "Информация не найдена."
                    refreshButton.visibility = View.VISIBLE
                } else {
                    val medicationInfo = parseMedicationInfo(medicationResponse.choices[0].message.content.toString())
                    Log.d("DEBUG", "Parsed Medication Info: $medicationInfo")
                    displayMedicationInfo(medicationInfo)
                }
                constraintProgressBar.isVisible = false
                scrollView.isVisible = true
            } catch (e: Exception) {
                Log.e("AIFragment", "Ошибка извлечения данных", e)
                medicationNameView.text = "Информация не найдена."
                constraintProgressBar.isVisible = false
                refreshButton.visibility = View.VISIBLE
            }
        }
    }

    private fun loadSearchHistory() {
        val historySet = sharedPreferences.getString("search_history", "[]")
        val type = object : TypeToken<List<String>>() {}.type

        Log.d("history", "Загруженная история: $historySet")
        searchHistory.clear()
        searchHistory.addAll(Gson().fromJson(historySet, type))
        //searchHistory.sortDescending()

        historyAdapter.notifyDataSetChanged()
    }

    private fun clearSearchHistory() {
        searchHistory.clear()
        saveSearchHistory()
        //sharedPreferences.edit().putStringSet("search_history", searchHistory.toSet()).apply()
        historyAdapter.notifyDataSetChanged()
    }

    private fun saveSearchHistory() {
        val json = Gson().toJson(searchHistory)
        sharedPreferences.edit().putString("search_history", json).apply()
    }

    private fun addToHistory(query: String) {
        if (!searchHistory.contains(query)) {
            if (searchHistory.size >= 10) {
                searchHistory.removeAt(0)
            }
            searchHistory.add(0, query)
            saveSearchHistory()

            /*
            sharedPreferences.edit()
                .putStringSet("search_history", searchHistory.toSet()) // Преобразуем в Set
                .apply()
             */

            historyAdapter.notifyDataSetChanged()
        }
    }

    private suspend fun fetchMedicationInfo(medicationName: String): MedicationResponse? {
        val gson = Gson()
        val messages = listOf(
            Message(
                "user",
                "Расскажи о лекарстве $medicationName, но не добавляй никаких дополнительных слов или объяснений, только JSON. Дай ответ на русском языке в формате:" +
                        "medicationName:$medicationName" +
                        "composition:" +
                        "usage:" +
                        "contraindications:"
            )
        )

        val request = MedicationRequest("mistral-large-latest", messages, 1.0)
        val json = gson.toJson(request)
        Log.d("AIFragment", "Отправляемый JSON: $json")
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)

        return try {
            // Выполняем запрос через Retrofit, используя suspend функцию
            val response = medicationApiService.completeChat(requestBody).awaitResponse()

            if (response.isSuccessful) {
                Log.d("DEBUG", "Ответ от API: ${response.body()}")
                response.body()
            } else {
                Log.e("DEBUG", "Ошибка от сервера: ${response.code()} ${response.message()}")
                Log.e("DEBUG", "Тело ответа: ${response.errorBody()?.string()}")
                null
            }

        } catch (e: HttpException) {
            // Логируем ошибку в случае HTTP ошибки
            Log.e("AIFragment", "HTTP error: ${e.code()} ${e.message()}")
            null
        } catch (e: Exception) {
            // Логируем ошибку в случае любой другой ошибки
            Log.e("AIFragment", "Ошибка при запросе: ${e.localizedMessage}", e)
            null
        }
    }


    private fun displayMedicationInfo(medicationInfo: Map<String, Any>) {
        val medicationName = medicationInfo["medicationName"] as? String ?: "Название не найдено"
        val composition = medicationInfo["composition"] as? String ?: "Состав не найден"
        val usage = when (val usageData = medicationInfo["usage"]) {
            is String -> usageData
            is Map<*, *> -> usageData.keys.joinToString(", ") { it.toString() }
            else -> "Способ применения не найден"
        }

        // Проверяем, является ли contraindications строкой или Map и обрабатываем
        val contraindications = when (val contraindicationsData = medicationInfo["contraindications"]) {
            is String -> contraindicationsData
            is Map<*, *> -> contraindicationsData.keys.joinToString(", ") { it.toString() }
            else -> "Противопоказания не найдены"
        }

        Log.d("DEBUG", "Отображаемое название: $medicationName")
        Log.d("DEBUG", "Отображаемый состав: $composition")
        Log.d("DEBUG", "Отображаемый способ применения: $usage")
        Log.d("DEBUG", "Отображаемые противопоказания: $contraindications")

        medicationNameView.text = medicationName
        compositionView.text = composition
        usageView.text = usage
        contraindicationsView.text = contraindications
    }

    private fun parseMedicationInfo(content: String): Map<String, Any> {
        /*
        val jsonContent = content.substringAfter("json").substringBeforeLast("")
        val type = object : TypeToken<Map<String, Any>>() {}.type

        return Gson().fromJson(jsonContent, type)
         */

        val jsonContent = content.substringAfter("json", "").trim()
        if (jsonContent.isEmpty()) {
            Log.e("AIFragment", "Ошибка парсинга JSON: пустой контент")
            return emptyMap()
        }

        return try {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            Gson().fromJson(jsonContent, type)
        } catch (e: Exception) {
            Log.e("AIFragment", "Ошибка парсинга JSON", e)
            emptyMap()
        }

    }
}