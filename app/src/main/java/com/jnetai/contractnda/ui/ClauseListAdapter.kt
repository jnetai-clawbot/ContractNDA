package com.jnetai.contractnda.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jnetai.contractnda.R
import com.jnetai.contractnda.model.Clause
import com.jnetai.contractnda.model.RiskLevel

class ClauseListAdapter(
    private var clauses: MutableList<Clause>,
    private val onClick: (Clause) -> Unit,
    private val onChecked: (Clause, Boolean) -> Unit
) : BaseAdapter() {

    override fun getCount() = clauses.size
    override fun getItem(position: Int) = clauses[position]
    override fun getItemId(position: Int) = clauses[position].id
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.item_clause, parent, false)
        val clause = clauses[position]

        val titleText = view.findViewById<TextView>(R.id.clauseTitle)
        val typeText = view.findViewById<TextView>(R.id.clauseType)
        val riskText = view.findViewById<TextView>(R.id.clauseRisk)
        val flagText = view.findViewById<TextView>(R.id.clauseRedFlag)
        val reviewedCheck = view.findViewById<CheckBox>(R.id.clauseReviewed)

        titleText.text = clause.title.ifEmpty { clause.clauseType.label }
        typeText.text = clause.clauseType.label
        riskText.text = "Risk: ${clause.riskScore}%"

        val riskColor = when (clause.riskLevel) {
            RiskLevel.LOW -> Color.parseColor("#4CAF50")
            RiskLevel.MEDIUM -> Color.parseColor("#FF9800")
            RiskLevel.HIGH -> Color.parseColor("#F44336")
        }
        riskText.setTextColor(riskColor)

        if (clause.isRedFlag) {
            flagText.visibility = View.VISIBLE
            flagText.text = "🚩 Red Flag"
        } else {
            flagText.visibility = View.GONE
        }

        reviewedCheck.setOnCheckedChangeListener(null)
        reviewedCheck.isChecked = clause.reviewed
        reviewedCheck.setOnCheckedChangeListener { _, isChecked ->
            onChecked(clause, isChecked)
        }

        view.setOnClickListener { onClick(clause) }
        return view
    }

    fun updateData(newClauses: MutableList<Clause>) {
        clauses = newClauses
        notifyDataSetChanged()
    }
}