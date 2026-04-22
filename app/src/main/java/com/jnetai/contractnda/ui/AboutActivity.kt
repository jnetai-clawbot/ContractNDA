package com.jnetai.contractnda.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jnetai.contractnda.BuildConfig
import com.jnetai.contractnda.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "About"

        findViewById<TextView>(R.id.versionText).text = "Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        findViewById<TextView>(R.id.repoText).text = "GitHub: github.com/jnetai-clawbot/ContractNDA"

        findViewById<com.google.android.material.button.MaterialButton>(R.id.checkUpdateButton).setOnClickListener {
            checkForUpdates()
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.shareButton).setOnClickListener {
            shareApp()
        }
    }

    private fun checkForUpdates() {
        val progressBar = findViewById<ProgressBar>(R.id.updateProgressBar)
        val resultText = findViewById<TextView>(R.id.updateResultText)
        progressBar.visibility = ProgressBar.VISIBLE
        resultText.text = "Checking..."

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val url = URL("https://api.github.com/repos/jnetai-clawbot/ContractNDA/releases/latest")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                    conn.connectTimeout = 10000
                    conn.readTimeout = 10000

                    if (conn.responseCode == 200) {
                        val response = conn.inputStream.bufferedReader().readText()
                        val json = JSONObject(response)
                        val tagName = json.optString("tag_name", "unknown")
                        val htmlUrl = json.optString("html_url", "")
                        "Latest release: $tagName\nDownload: $htmlUrl"
                    } else {
                        "No updates found (HTTP ${conn.responseCode})"
                    }
                } catch (e: Exception) {
                    "Error checking updates: ${e.message}"
                }
            }
            progressBar.visibility = ProgressBar.GONE
            resultText.text = result
        }
    }

    private fun shareApp() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "ContractNDA - Contract & NDA Reviewer")
            putExtra(Intent.EXTRA_TEXT, "Check out ContractNDA - a contract and NDA review tool!\nhttps://github.com/jnetai-clawbot/ContractNDA")
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}