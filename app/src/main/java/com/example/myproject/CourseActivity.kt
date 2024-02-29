package com.example.myproject

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.text.Editable
import android.text.TextWatcher
import android.view.View

class CourseActivity : AppCompatActivity() {

    private lateinit var input: EditText
    private lateinit var result: EditText
    private lateinit var selectCours: Spinner
    private lateinit var rubSpinner: Spinner
    private lateinit var spinnerSwap: ImageButton

    private val rates = HashMap<String, Rate>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)

        input = findViewById(R.id.amountFrom)
        result = findViewById(R.id.amountTo)
        selectCours = findViewById(R.id.selectCurrencySpinner)
        rubSpinner = findViewById(R.id.RusSpinner)
        spinnerSwap = findViewById(R.id.spinnerSwap)

        val currenciesArray = resources.getStringArray(R.array.currencies_array_start_usd)
        val currenciesRubArray = resources.getStringArray(R.array.currencies_array_start_rub)

        val rubAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currenciesRubArray)
        rubAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rubSpinner.adapter = rubAdapter

        val selectAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currenciesArray)
        selectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        selectCours.adapter = selectAdapter

        getCurrencies()

        spinnerSwap.setOnClickListener {
            val selectCurrencyText = selectCours.selectedItem.toString()
            val rubCurrencyText = rubSpinner.selectedItem.toString()

            // Находим индексы элементов в обоих списке, которые соответствуют выбранным валютам
            val selectCurrencyIndex = findIndexByCurrencyText(selectCurrencyText, rubSpinner)
            val rubCurrencyIndex = findIndexByCurrencyText(rubCurrencyText, selectCours)

            // Устанавливаем выбранные элементы по найденным индексам
            selectCours.setSelection(rubCurrencyIndex)
            rubSpinner.setSelection(selectCurrencyIndex)
            convertValue()
        }

        input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                convertValue()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }
        })

        selectCours.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                convertValue()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Not used
            }
        }

        rubSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                convertValue()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Not used
            }
        }
    }

    private fun findIndexByCurrencyText(currencyText: String, spinner: Spinner): Int {
        val adapter = spinner.adapter
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).toString() == currencyText) {
                return i
            }
        }
        return 0
    }

    private fun getCurrencies() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.cbr-xml-daily.ru/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call = service.getCurrencies()

        call.enqueue(object : Callback<CurrenciesResponse> {
            override fun onResponse(call: Call<CurrenciesResponse>, response: Response<CurrenciesResponse>) {
                if (response.isSuccessful) {
                    val currenciesResponse = response.body()
                    currenciesResponse?.let {
                        val valute = it.valute
                        valute.keys.forEach { key ->
                            // Используем только первые три буквы валютного кода в качестве ключа
                            val currencyCode = key.take(3)
                            val currencyObject = valute[key]
                            val rate = Rate(
                                currencyObject?.value ?: 0.0,
                                currencyObject?.nominal ?: 0
                            )
                            rates[currencyCode] = rate
                        }
                        updateUI()
                    }
                } else {
                    // Handle error
                    Toast.makeText(this@CourseActivity, "Failed to get currencies", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CurrenciesResponse>, t: Throwable) {
                // Handle failure
                Toast.makeText(this@CourseActivity, "Network request failed", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun Double.roundTo2DecimalPlaces(): String {
        return String.format("%.2f", this)
    }

    private fun updateUI() {
        val usdValueTextView: TextView = findViewById(R.id.textUSD)
        val eurValueTextView: TextView = findViewById(R.id.textEUR)
        val gbpValueTextView: TextView = findViewById(R.id.textGBP)

        rates["USD"]?.let { rate ->
            val currentValue = rate.value
            usdValueTextView.text = currentValue.roundTo2DecimalPlaces() ?: "--"
        }

        rates["EUR"]?.let { rate ->
            val currentValue = rate.value
            eurValueTextView.text = currentValue.roundTo2DecimalPlaces() ?: "--"
        }

        rates["GBP"]?.let { rate ->
            val currentValue = rate.value
            gbpValueTextView.text = currentValue.roundTo2DecimalPlaces() ?: "--"
        }
    }

    private fun convertValue() {
        val amountFrom = input.text.toString().toDoubleOrNull() ?: return
        val selectedCurrency = selectCours.selectedItem.toString().take(3)
        val selectedToCurrency = rubSpinner.selectedItem.toString().take(3)
        val resultToValue: Double
        val res: Double


        if (selectedCurrency == "RUB") {
            val rate = rates[selectedToCurrency] ?: return

            val nominal = rate.nominal
            if (nominal == 0) {
                Toast.makeText(this, "Invalid nominal", Toast.LENGTH_SHORT).show()
                return
            }
            val value = rate.value
            resultToValue = amountFrom * value / nominal
        } else if ((selectedCurrency != "RUB") && (selectedToCurrency != "RUB")) {
                val rate = rates[selectedToCurrency] ?: return
                val rate1 = rates[selectedCurrency] ?: return

                val nominal = rate.nominal
                val nominal1 = rate1.nominal

                if ((nominal == 0) || (nominal1 == 0)) {
                    Toast.makeText(this, "Invalid nominal", Toast.LENGTH_SHORT).show()
                    return
                }

                val value = rate.value
                val value1 = rate1.value

                // Пересчитываем с учетом номинала обеих валют
                resultToValue = amountFrom * value / nominal * nominal1 / value1
        } else {
            val rate = rates[selectedCurrency] ?: return

            val nominal = rate.nominal
            if (nominal == 0) {
                Toast.makeText(this, "Invalid nominal", Toast.LENGTH_SHORT).show()
                return
            }
            val value = rate.value
            resultToValue = amountFrom / value * nominal
        }

        result.setText(resultToValue.toFixed(2).replace(",", "."))
    }

    private fun Double.toFixed(digits: Int): String {
        return "%.${digits}f".format(this)
    }

    data class Rate(val value: Double, val nominal: Int)

    interface ApiService {
        @GET("daily_json.js")
        fun getCurrencies(): Call<CurrenciesResponse>
    }

    data class CurrenciesResponse(
        @SerializedName("Valute")
        val valute: Map<String, CurrencyObject>
    )

    data class CurrencyObject(
        @SerializedName("Value")
        val value: Double,

        @SerializedName("Nominal")
        val nominal: Int
    )
}
