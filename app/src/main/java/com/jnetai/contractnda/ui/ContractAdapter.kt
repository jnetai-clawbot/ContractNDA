package com.jnetai.contractnda.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jnetai.contractnda.R
import com.jnetai.contractnda.model.Contract
import com.jnetai.contractnda.model.RiskLevel

class ContractAdapter(
    private var contracts: MutableList<Contract>,
    private val onClick: (Contract) -> Unit
) : RecyclerView.Adapter<ContractAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.contractTitle)
        val typeText: TextView = view.findViewById(R.id.contractType)
        val partiesText: TextView = view.findViewById(R.id.contractParties)
        val dateText: TextView = view.findViewById(R.id.contractDate)
        val riskBadge: TextView = view.findViewById(R.id.riskBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contract, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contract = contracts[position]
        holder.titleText.text = contract.title
        holder.typeText.text = contract.type.label
        holder.partiesText.text = contract.parties
        holder.dateText.text = contract.date.toString()
        holder.riskBadge.text = contract.riskLevel.label

        val color = when (contract.riskLevel) {
            RiskLevel.LOW -> Color.parseColor("#4CAF50")
            RiskLevel.MEDIUM -> Color.parseColor("#FF9800")
            RiskLevel.HIGH -> Color.parseColor("#F44336")
        }
        holder.riskBadge.setBackgroundColor(color)

        holder.itemView.setOnClickListener { onClick(contract) }
    }

    override fun getItemCount() = contracts.size

    fun updateData(newContracts: MutableList<Contract>) {
        contracts = newContracts
        notifyDataSetChanged()
    }
}