package com.jnetai.contractnda.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jnetai.contractnda.App
import com.jnetai.contractnda.R
import com.jnetai.contractnda.model.Clause
import com.jnetai.contractnda.model.Contract
import com.jnetai.contractnda.util.JsonExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ContractDetailActivity : AppCompatActivity() {

    private var contract: Contract? = null
    private var clauses: List<Clause> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contract_detail)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val contractId = intent.getLongExtra("contractId", -1)
        if (contractId == -1L) {
            finish()
            return
        }

        loadContract(contractId)

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabReview).setOnClickListener {
            contract?.let { c ->
                startActivity(Intent(this, ClauseReviewActivity::class.java).apply {
                    putExtra("contractId", c.id)
                })
            }
        }
    }

    private fun loadContract(contractId: Long) {
        lifecycleScope.launch {
            contract = withContext(Dispatchers.IO) {
                (application as com.jnetai.contractnda.App).database.contractDao().getById(contractId)
            }
            clauses = withContext(Dispatchers.IO) {
                (application as com.jnetai.contractnda.App).database.clauseDao().getByContractId(contractId)
            }
            contract?.let { displayContract(it) }
        }
    }

    private fun displayContract(c: Contract) {
        supportActionBar?.title = c.title
        findViewById<TextView>(R.id.detailTitle).text = c.title
        findViewById<TextView>(R.id.detailType).text = "Type: ${c.type.label}"
        findViewById<TextView>(R.id.detailParties).text = "Parties: ${c.parties}"
        findViewById<TextView>(R.id.detailDate).text = "Date: ${c.date}"
        findViewById<TextView>(R.id.detailKeyTerms).text = "Key Terms: ${c.keyTerms}"
        findViewById<TextView>(R.id.detailRisk).text = "Risk Level: ${c.riskLevel.label}"

        val avgRisk = with(lifecycleScope) {
            kotlinx.coroutines.runBlocking {
                (application as com.jnetai.contractnda.App).database.clauseDao().getAverageRiskScore(c.id)
            }
        }
        val riskText = if (clauses.isNotEmpty()) {
            "Overall Risk Score: ${"%.0f".format(avgRisk ?: 0f)}% (${clauses.size} clauses reviewed)"
        } else {
            "No clauses reviewed yet. Tap + to add clause review."
        }
        findViewById<TextView>(R.id.detailRiskScore).text = riskText

        val redFlags = clauses.filter { it.isRedFlag }
        if (redFlags.isNotEmpty()) {
            val flagText = redFlags.joinToString("\n") { "⚠ ${it.title}" }
            findViewById<TextView>(R.id.detailRedFlags).text = "🚩 Red Flags:\n$flagText"
            findViewById<TextView>(R.id.detailRedFlags).visibility = TextView.VISIBLE
        } else {
            findViewById<TextView>(R.id.detailRedFlags).visibility = TextView.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        contract?.let { loadContract(it.id) }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_contract_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        contract?.let { c ->
            when (item.itemId) {
                R.id.action_export -> exportReview(c)
                R.id.action_delete -> deleteContract(c)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun exportReview(contract: Contract) {
        lifecycleScope.launch {
            val allClauses = withContext(Dispatchers.IO) {
                (application as com.jnetai.contractnda.App).database.clauseDao().getByContractId(contract.id)
            }
            val avgRisk = withContext(Dispatchers.IO) {
                (application as com.jnetai.contractnda.App).database.clauseDao().getAverageRiskScore(contract.id)
            }
            val json = JsonExporter.export(contract, allClauses, avgRisk)

            val file = File(getExternalFilesDir(null), "${contract.title.replace(" ", "_")}_review.json")
            withContext(Dispatchers.IO) {
                file.writeText(json)
            }

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, androidx.core.content.FileProvider.getUriForFile(
                    this@ContractDetailActivity,
                    "${packageName}.fileprovider",
                    file
                ))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Export Review"))
        }
    }

    private fun deleteContract(contract: Contract) {
        AlertDialog.Builder(this)
            .setTitle("Delete Contract")
            .setMessage("Delete '${contract.title}' and all its clauses?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        (application as com.jnetai.contractnda.App).database.contractDao().delete(contract)
                    }
                    finish()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}