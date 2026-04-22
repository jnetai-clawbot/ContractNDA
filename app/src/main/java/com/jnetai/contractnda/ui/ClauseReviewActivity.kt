package com.jnetai.contractnda.ui

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jnetai.contractnda.App
import com.jnetai.contractnda.R
import com.jnetai.contractnda.model.Clause
import com.jnetai.contractnda.model.ClauseType
import com.jnetai.contractnda.model.RiskLevel
import com.jnetai.contractnda.util.RedFlagDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClauseReviewActivity : AppCompatActivity() {

    private var contractId: Long = -1
    private var clauses = mutableListOf<Clause>()
    private lateinit var adapter: ClauseListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clause_review)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Clause Review"

        contractId = intent.getLongExtra("contractId", -1)
        if (contractId == -1L) {
            finish()
            return
        }

        adapter = ClauseListAdapter(clauses, ::onClauseClick, ::onClauseChecked)
        findViewById<ListView>(R.id.clauseListView).adapter = adapter

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAddClause).setOnClickListener {
            showAddClauseDialog()
        }

        loadClauses()
    }

    private fun loadClauses() {
        lifecycleScope.launch {
            clauses = withContext(Dispatchers.IO) {
                App.getInstance(this@ClauseReviewActivity).clauseDao().getByContractId(contractId).toMutableList()
            }
            adapter.updateData(clauses)
        }
    }

    private fun onClauseClick(clause: Clause) {
        showEditClauseDialog(clause)
    }

    private fun onClauseChecked(clause: Clause, checked: Boolean) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val updated = clause.copy(reviewed = checked, updatedAt = System.currentTimeMillis())
                App.getInstance(this@ClauseReviewActivity).clauseDao().update(updated)
            }
            loadClauses()
        }
    }

    private fun showAddClauseDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_clause, null)
        val typeSpinner = dialogView.findViewById<Spinner>(R.id.clauseTypeSpinner)
        val types = ClauseType.values().map { it.label }
        typeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)

        val contentEdit = dialogView.findViewById<EditText>(R.id.clauseContentEdit)
        val notesEdit = dialogView.findViewById<EditText>(R.id.clauseNotesEdit)
        val titleEdit = dialogView.findViewById<EditText>(R.id.clauseTitleEdit)
        val redFlagInfo = dialogView.findViewById<TextView>(R.id.redFlagInfo)
        val riskBar = dialogView.findViewById<ProgressBar>(R.id.riskBar)
        val riskLabel = dialogView.findViewById<TextView>(R.id.riskLabel)

        contentEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString() ?: ""
                val flags = RedFlagDetector.detect(text)
                val score = RedFlagDetector.riskScore(text)
                riskBar.progress = score
                riskLabel.text = "Risk: $score%"
                if (flags.isNotEmpty()) {
                    redFlagInfo.text = "🚩 Red flags: ${flags.joinToString(", ")}"
                    redFlagInfo.setTextColor(Color.parseColor("#F44336"))
                    redFlagInfo.visibility = View.VISIBLE
                } else {
                    redFlagInfo.visibility = View.GONE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val order = clauses.size

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Add Clause")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val content = contentEdit.text.toString()
                val notes = notesEdit.text.toString()
                val title = titleEdit.text.toString().ifEmpty { types[typeSpinner.selectedItemPosition] }
                val flags = RedFlagDetector.detect(content)
                val score = RedFlagDetector.riskScore(content)
                val riskLevel = when {
                    score >= 60 -> RiskLevel.HIGH
                    score >= 30 -> RiskLevel.MEDIUM
                    else -> RiskLevel.LOW
                }

                val clause = Clause(
                    contractId = contractId,
                    clauseType = ClauseType.values()[typeSpinner.selectedItemPosition],
                    title = title,
                    content = content,
                    riskScore = score,
                    riskLevel = riskLevel,
                    isRedFlag = flags.isNotEmpty(),
                    notes = notes,
                    order = order
                )

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        App.getInstance(this@ClauseReviewActivity).clauseDao().insert(clause)
                    }
                    loadClauses()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditClauseDialog(clause: Clause) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_clause, null)
        val typeSpinner = dialogView.findViewById<Spinner>(R.id.clauseTypeSpinner)
        val types = ClauseType.values().map { it.label }
        typeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)
        typeSpinner.setSelection(clause.clauseType.ordinal)

        val contentEdit = dialogView.findViewById<EditText>(R.id.clauseContentEdit)
        val notesEdit = dialogView.findViewById<EditText>(R.id.clauseNotesEdit)
        val titleEdit = dialogView.findViewById<EditText>(R.id.clauseTitleEdit)
        val redFlagInfo = dialogView.findViewById<TextView>(R.id.redFlagInfo)
        val riskBar = dialogView.findViewById<ProgressBar>(R.id.riskBar)
        val riskLabel = dialogView.findViewById<TextView>(R.id.riskLabel)

        titleEdit.setText(clause.title)
        contentEdit.setText(clause.content)
        notesEdit.setText(clause.notes)
        riskBar.progress = clause.riskScore
        riskLabel.text = "Risk: ${clause.riskScore}%"
        if (clause.isRedFlag) {
            val flags = RedFlagDetector.detect(clause.content)
            redFlagInfo.text = "🚩 Red flags: ${flags.joinToString(", ")}"
            redFlagInfo.setTextColor(Color.parseColor("#F44336"))
            redFlagInfo.visibility = View.VISIBLE
        }

        contentEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString() ?: ""
                val flags = RedFlagDetector.detect(text)
                val score = RedFlagDetector.riskScore(text)
                riskBar.progress = score
                riskLabel.text = "Risk: $score%"
                if (flags.isNotEmpty()) {
                    redFlagInfo.text = "🚩 Red flags: ${flags.joinToString(", ")}"
                    redFlagInfo.setTextColor(Color.parseColor("#F44336"))
                    redFlagInfo.visibility = View.VISIBLE
                } else {
                    redFlagInfo.visibility = View.GONE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Edit Clause")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val content = contentEdit.text.toString()
                val notes = notesEdit.text.toString()
                val title = titleEdit.text.toString().ifEmpty { types[typeSpinner.selectedItemPosition] }
                val flags = RedFlagDetector.detect(content)
                val score = RedFlagDetector.riskScore(content)
                val riskLevel = when {
                    score >= 60 -> RiskLevel.HIGH
                    score >= 30 -> RiskLevel.MEDIUM
                    else -> RiskLevel.LOW
                }

                val updated = clause.copy(
                    clauseType = ClauseType.values()[typeSpinner.selectedItemPosition],
                    title = title,
                    content = content,
                    riskScore = score,
                    riskLevel = riskLevel,
                    isRedFlag = flags.isNotEmpty(),
                    notes = notes
                )

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        App.getInstance(this@ClauseReviewActivity).clauseDao().update(updated)
                    }
                    loadClauses()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadClauses()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_clause_review, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_default_clauses -> addDefaultClauses()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addDefaultClauses() {
        val defaults = ClauseType.values().mapIndexed { index, type ->
            Clause(
                contractId = contractId,
                clauseType = type,
                title = type.label,
                content = "",
                riskScore = 0,
                riskLevel = RiskLevel.LOW,
                isRedFlag = false,
                notes = "",
                order = index
            )
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                defaults.forEach { clause ->
                    App.getInstance(this@ClauseReviewActivity).clauseDao().insert(clause)
                }
            }
            loadClauses()
        }
    }
}