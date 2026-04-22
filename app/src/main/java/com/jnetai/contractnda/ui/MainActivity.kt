package com.jnetai.contractnda.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jnetai.contractnda.App
import com.jnetai.contractnda.BuildConfig
import com.jnetai.contractnda.R
import com.jnetai.contractnda.model.Contract
import com.jnetai.contractnda.model.RiskLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var adapter: ContractAdapter
    private var contracts = mutableListOf<Contract>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "ContractNDA"

        recyclerView = findViewById(R.id.recyclerView)
        emptyView = findViewById(R.id.emptyView)
        adapter = ContractAdapter(contracts) { contract ->
            startActivity(Intent(this, ContractDetailActivity::class.java).apply {
                putExtra("contractId", contract.id)
            })
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            startActivity(Intent(this, AddContractActivity::class.java))
        }

        loadContracts()
    }

    override fun onResume() {
        super.onResume()
        loadContracts()
    }

    private fun loadContracts() {
        lifecycleScope.launch {
            contracts = withContext(Dispatchers.IO) {
                App.getInstance(this@MainActivity).contractDao().getAll().toMutableList()
            }
            adapter.updateData(contracts)
            emptyView.visibility = if (contracts.isEmpty()) View.VISIBLE else View.GONE
            recyclerView.visibility = if (contracts.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}