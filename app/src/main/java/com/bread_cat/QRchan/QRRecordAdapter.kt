package com.bread_cat.QRchan

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bread_cat.QRchan.databinding.ItemQrRecordBinding

class QRRecordAdapter(
    private val context: Context,
    private val qrRecords: List<QRRecord>
) : RecyclerView.Adapter<QRRecordAdapter.QRRecordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QRRecordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemQrRecordBinding.inflate(inflater, parent, false)
        return QRRecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QRRecordViewHolder, position: Int) {
        val qrRecord = qrRecords[position]
        holder.bind(qrRecord)
    }

    override fun getItemCount(): Int = qrRecords.size

    inner class QRRecordViewHolder(private val binding: ItemQrRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(qrRecord: QRRecord) {
            binding.qrContentText.text = qrRecord.content
            binding.qrDateText.text = qrRecord.date.toString()

            // 항목을 길게 누르면 클립보드에 복사
            binding.root.setOnLongClickListener {
                copyToClipboard("QR Content", qrRecord.content)
                Toast.makeText(context, "QR 코드 내용이 복사되었습니다.", Toast.LENGTH_SHORT).show()
                true
            }
        }

        private fun copyToClipboard(label: String, text: String) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
        }
    }
}
