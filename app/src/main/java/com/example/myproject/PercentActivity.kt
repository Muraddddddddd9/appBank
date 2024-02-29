package com.example.myproject

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class PercentActivity : AppCompatActivity() {

    private lateinit var spinnerCalculationType: Spinner
    private lateinit var inputFieldsContainer: View
    private lateinit var calculateButton: Button
    private lateinit var principalInput: EditText
    private lateinit var rateInput: EditText
    private lateinit var timeInput: EditText
    private lateinit var depositInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_percent)

        spinnerCalculationType = findViewById(R.id.spinnerCalculationType)
        inputFieldsContainer = findViewById(R.id.inputFieldsContainer)
        calculateButton = findViewById(R.id.calculateButton)
        principalInput = findViewById(R.id.principalInput)
        rateInput = findViewById(R.id.rateInput)
        timeInput = findViewById(R.id.timeInput)
        depositInput = findViewById(R.id.depositInput)

        val calculationOptions = arrayOf("Простой процент (без пополнения)", "Простой процент (с пополнением)", "Сложной процент (без пополнения)", "Сложной процент (с пополнением)")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, calculationOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCalculationType.adapter = adapter

        spinnerCalculationType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> showInputFields()
                    1 -> showInputFieldsDeposit()
                    2 -> showInputFields()
                    3 -> showInputFieldsDeposit()
                    else -> hideInputFields()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        calculateButton.setOnClickListener {
            calculate()
        }
    }

    private fun showInputFields() {
        inputFieldsContainer.visibility = View.VISIBLE
        depositInput.visibility = View.GONE
    }

    private fun showInputFieldsDeposit() {
        inputFieldsContainer.visibility = View.VISIBLE
        depositInput.visibility = View.VISIBLE
    }

    private fun hideInputFields() {
        inputFieldsContainer.visibility = View.GONE
    }

    private fun Double.roundTo2DecimalPlaces(): Double {
        return (this * 100.0).roundToInt() / 100.0
    }

    private fun calculateSimpleInterest(principal: Double, rate: Double, time: Double): Double {
        return (principal * (rate / 100) * time).roundTo2DecimalPlaces()
    }

    private fun calculateCompoundInterest(principal: Double, rate: Double, time: Double): Double {
        val amount = principal * Math.pow((1 + rate / 100), time)
        return (amount - principal).roundTo2DecimalPlaces()
    }

//
private fun calculateSimpleInterestDeposit(principal: Double, rate: Double, timeInYears: Double, deposit: Double): Double {
    var totalInterest = 0.0
    var remainingPrincipal = principal
    val months = timeInYears * 12

    repeat(months.toInt()) {
        val monthlyInterest = remainingPrincipal * (rate / 100) / 12
        totalInterest += monthlyInterest
        remainingPrincipal += monthlyInterest
        remainingPrincipal += deposit
    }

    return totalInterest.roundTo2DecimalPlaces()
}

    private fun calculateCompoundInterestDeposit(principal: Double, rate: Double, timeInYears: Double, deposit: Double): Double {
        var remainingPrincipal = principal
        val months = timeInYears * 12 // Конвертировать время из годов в месяцы

        repeat(months.toInt()) {
            remainingPrincipal *= (1 + rate / 100 / 12)
            remainingPrincipal += deposit
        }

        return (remainingPrincipal - principal).roundTo2DecimalPlaces()
    }

//

    private fun calculate() {
        val principal = principalInput.text.toString().toDoubleOrNull() ?: return
        val rate = rateInput.text.toString().toDoubleOrNull() ?: return
        val time = timeInput.text.toString().toDoubleOrNull() ?: return
        val deposit = depositInput.text.toString().toDoubleOrNull() ?: return

        val calculationTypePosition = spinnerCalculationType.selectedItemPosition

        val result = when (calculationTypePosition) {
            0 -> calculateSimpleInterest(principal, rate, time)
            1 -> calculateSimpleInterestDeposit(principal, rate, time, deposit)
            2 -> calculateCompoundInterest(principal, rate, time)
            3 -> calculateCompoundInterestDeposit(principal, rate, time, deposit)
            else -> 0.0
        }

        val TotalCash = principal + result;

        val resultIncome = findViewById<TextView>(R.id.resultIncome)
        val resultTotal = findViewById<TextView>(R.id.resultTotal)

        // Преобразуйте результат в строку и установите его в TextView
        resultIncome.text = "Доход : $result"
        resultTotal.text = "Итоговая сумма : $TotalCash"
    }
}
