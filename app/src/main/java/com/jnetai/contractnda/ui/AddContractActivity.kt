package com.jnetai.contractnda.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jnetai.contractnda.App
import com.jnetai.contractnda.R
import com.jnetai.contractnda.model.Contract
import com.jnetai.contractnda.model.ContractType
import com.jnetai.contractnda.model.RiskLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class AddContractActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contract)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Contract"

        val typeSpinner = findViewById<Spinner>(R.id.typeSpinner)
        val riskSpinner = findViewById<Spinner>(R.id.riskSpinner)

        val types = ContractType.values().map { it.label }
        typeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)

        val risks = RiskLevel.values().map { it.label }
        riskSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, risks)

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            saveContract()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun saveContract() {
        val title = findViewById<EditText>(R.id.titleEdit).text.toString().trim()
        val parties = findViewById<EditText>(R.id.partiesEdit).text.toString().trim()
        val dateStr = findViewById<EditText>(R.id.dateEdit).text.toString().trim()
        val keyTerms = findViewById<EditText>(R.id.keyTermsEdit).text.toString().trim()
        val typePos = findViewById<Spinner>(R.id.typeSpinner).selectedItemPosition
        val riskPos = findViewById<Spinner>(R.id.riskSpinner).selectedItemPosition

        if (title.isEmpty()) {
            findViewById<EditText>(R.id.titleEdit).error = "Title required"
            return
        }
        if (parties.isEmpty()) {
            findViewById<EditText>(R.id.partiesEdit).error = "Parties required"
            return
        }

        val date = try {
            LocalDate.parse(if (dateStr.isEmpty()) LocalDate.now().toString() else dateStr)
        } catch (e: Exception) {
            LocalDate.now()
        }

        val contract = Contract(
            title = title,
            type = ContractType.values()[typePos],
            parties = parties,
            date = date,
            keyTerms = keyTerms,
            riskLevel = RiskLevel.values()[riskPos]
        )

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                App.getInstance(this@AddContractActivity).contractDao().insert(contract)
            }
            finish()
        }
    }
}