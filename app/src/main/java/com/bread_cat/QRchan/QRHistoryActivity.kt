package com.bread_cat.QRchan

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bread_cat.QRchan.databinding.ActivityQrHistoryBinding

class QRHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrHistoryBinding
    private var qrRecords = mutableListOf<QRRecord>()
    private lateinit var adapter: QRRecordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val records = intent.getSerializableExtra("qrRecords") as? ArrayList<QRRecord>
        if (records != null) {
            qrRecords.addAll(records)
        }

        adapter = QRRecordAdapter(this, qrRecords)
        binding.qrRecordRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.qrRecordRecyclerView.adapter = adapter

        binding.clearLogButton.setOnClickListener {
            clearLogs()
        }
    }

    private fun clearLogs() {
        qrRecords.clear()
        adapter.notifyDataSetChanged()
    }
}
