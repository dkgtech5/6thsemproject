package com.example.safe

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class RecentScanAdapter(private val scans: List<RecentScan>) :
    RecyclerView.Adapter<RecentScanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivScanStatus: ImageView = view.findViewById(R.id.ivScanStatus)
        val tvUrl: TextView = view.findViewById(R.id.tvUrl)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvStatusBadge: TextView = view.findViewById(R.id.tvStatusBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_scan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scan = scans[position]
        holder.tvUrl.text = scan.url
        holder.tvTime.text = scan.time
        
        val context = holder.itemView.context
        val statusColor = if (scan.isSafe) "#10B981" else "#EF4444"
        val bgColor = if (scan.isSafe) "#ECFDF5" else "#FEF2F2"
        val statusIcon = if (scan.isSafe) R.drawable.ic_check_circle else R.drawable.ic_error
        val statusLabel = if (scan.isSafe) "Safe" else "Phishing"

        holder.ivScanStatus.setImageResource(statusIcon)
        holder.ivScanStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(bgColor))
        
        holder.tvStatusBadge.text = statusLabel
        holder.tvStatusBadge.backgroundTintList = ColorStateList.valueOf(Color.parseColor(bgColor))
        holder.tvStatusBadge.setTextColor(Color.parseColor(statusColor))
    }

    override fun getItemCount() = scans.size
}