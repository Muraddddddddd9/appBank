package com.example.myproject

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import android.view.View
import android.content.Intent

class BankActivity : AppCompatActivity() {

    private lateinit var bankListLayout: LinearLayout
    private lateinit var addBankButton: Button
    private lateinit var subBankButton: Button
    private lateinit var bankNameEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var sharedPreferences: SharedPreferences
    private val listeners = mutableMapOf<String, SharedPreferences.OnSharedPreferenceChangeListener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank)

        sharedPreferences = getSharedPreferences("Banks", Context.MODE_PRIVATE)

        bankListLayout = findViewById(R.id.bankListLayout)
        addBankButton = findViewById(R.id.addBankButton)
        subBankButton = findViewById(R.id.subBankButton)
        bankNameEditText = findViewById(R.id.bankNameEditText)
        amountEditText = findViewById(R.id.amountEditText)

        addBankButton.setOnClickListener {
            addBank()
        }

        subBankButton.setOnClickListener {
            subBank()
        }

        // Загрузка сохраненных данных, если они есть
        loadSavedBanks()
    }

    private fun addBank() {
        val bankName = bankNameEditText.text.toString().trim()
        val amountText = amountEditText.text.toString().trim()
        val amount = amountText.toDoubleOrNull() ?: return

        // Проверяем, что пользователь ввел название банка
        if (bankName.isEmpty()) {
            // Выводим сообщение об ошибке
            showToast("Please enter bank name")
            return
        }

        // Проверяем, есть ли уже банк с таким названием
        if (sharedPreferences.contains(bankName)) {
            // Если банк существует, прибавляем введенную сумму к текущей
            val currentAmount = sharedPreferences.getFloat(bankName, 0f)
            val newAmount = (currentAmount + amount).toFloat()
            saveBank(bankName, newAmount.toDouble())
            updateBankView(bankName, newAmount)
        } else {
            val bankView = layoutInflater.inflate(R.layout.item_bank, null)
            val bankNameTextView = bankView.findViewById<TextView>(R.id.bankNameTextView)
            val amountTextView = bankView.findViewById<TextView>(R.id.amountTextView)
            val removeButton = bankView.findViewById<Button>(R.id.removeButton)

            bankNameTextView.text = bankName
            amountTextView.text = amount.toString()

            removeButton.setOnClickListener {
                bankListLayout.removeView(bankView)
                removeBank(bankName)
            }

            bankListLayout.addView(bankView)

            // Сохранить новый банк
            saveBank(bankName, amount)
            bankView.tag = bankName

            // Добавляем слушатель изменений для этого банка
            addSharedPreferencesListener(bankName, amountTextView)
        }

        // Обновляем текст в TextView в activity_chart с информацией о банках
        updateBankInfoText()
    }

    private fun updateBankInfoText() {
        val allBanks = sharedPreferences.all
        val bankInfo = StringBuilder()
        for ((bankName, amount) in allBanks) {
            bankInfo.append("$bankName: $amount\n")
        }
    }


    private fun subBank() {
        val bankName = bankNameEditText.text.toString().trim()
        val amountText = amountEditText.text.toString().trim()
        val amount = amountText.toDoubleOrNull() ?: return

        // Проверяем, что пользователь ввел название банка
        if (bankName.isEmpty()) {
            // Выводим сообщение об ошибке
            showToast("Please enter bank name")
            return
        }

        // Проверяем, есть ли уже банк с таким названием
        if (sharedPreferences.contains(bankName)) {
            // Если банк существует, убавляем введенную сумму к текущей
            val currentAmount = sharedPreferences.getFloat(bankName, 0f)
            val newAmount = (currentAmount - amount).toFloat()
            saveBank(bankName, newAmount.toDouble())
            updateBankView(bankName, newAmount)
        } else {
            val bankView = layoutInflater.inflate(R.layout.item_bank, null)
            val bankNameTextView = bankView.findViewById<TextView>(R.id.bankNameTextView)
            val amountTextView = bankView.findViewById<TextView>(R.id.amountTextView)
            val removeButton = bankView.findViewById<Button>(R.id.removeButton)

            bankNameTextView.text = bankName
            amountTextView.text = (-amount).toString() // Умножаем на -1 здесь

            removeButton.setOnClickListener {
                bankListLayout.removeView(bankView)
                removeBank(bankName)
            }

            bankListLayout.addView(bankView)

            // Сохранить новый банк
            saveBank(bankName, (-amount))
            bankView.tag = bankName

            // Добавляем слушатель изменений для этого банка
            addSharedPreferencesListener(bankName, amountTextView)
        }
    }

    private fun addSharedPreferencesListener(bankName: String, amountTextView: TextView) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == bankName) {
                val amount = sharedPreferences.getFloat(key, 0f)
                amountTextView.text = amount.toString()
            }
        }
        listeners[bankName] = listener // Сохраняем слушатель в коллекции
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    private fun removeSharedPreferencesListener(bankName: String) {
        val listener = listeners[bankName]
        if (listener != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
            listeners.remove(bankName) // Удаляем слушатель из коллекции
        }
    }

    private fun saveBank(bankName: String, amount: Double) {
        val editor = sharedPreferences.edit()
        editor.putFloat(bankName, amount.toFloat())
        editor.apply()
    }

    private fun removeBank(bankName: String) {
        val editor = sharedPreferences.edit()
        editor.remove(bankName)
        editor.apply()
        removeSharedPreferencesListener(bankName) // Удаляем слушатель при удалении банка
    }

    private fun loadSavedBanks() {
        val allBanks = sharedPreferences.all
        for ((bankName, amount) in allBanks) {
            val bankView = layoutInflater.inflate(R.layout.item_bank, null)
            val bankNameTextView = bankView.findViewById<TextView>(R.id.bankNameTextView)
            val amountTextView = bankView.findViewById<TextView>(R.id.amountTextView)
            val removeButton = bankView.findViewById<Button>(R.id.removeButton)

            bankNameTextView.text = bankName
            amountTextView.text = amount.toString()

            removeButton.setOnClickListener {
                bankListLayout.removeView(bankView)
                removeBank(bankName)
            }

            bankListLayout.addView(bankView)

            // Добавляем слушатель изменений для этого банка
            addSharedPreferencesListener(bankName, amountTextView)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateBankView(bankName: String, amount: Float) {
        val bankView = bankListLayout.findViewWithTag<View>(bankName) // Находим представление банка по его названию
        if (bankView != null) {
            val amountTextView = bankView.findViewById<TextView>(R.id.amountTextView)
            amountTextView.text = amount.toString() // Обновляем отображение суммы
        }
    }
}