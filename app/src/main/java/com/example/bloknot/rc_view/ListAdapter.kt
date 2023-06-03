package com.example.bloknot.rc_view

import android.content.ClipData.Item
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bloknot.EditActivity
import com.example.bloknot.MyIntenConstants
import com.example.bloknot.R
import com.example.bloknot.db.MyDbManager

class ListAdapter(val listTitle: ArrayList<ListItem>, var context: Context) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    class ViewHolder(view: View, var context: Context) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvTime: TextView = view.findViewById(R.id.tvTime)

        fun bind(item: ListItem) {
            tvTitle.text = item.title
            tvTime.text = item.time
            itemView.setOnClickListener {
                val intent = Intent(context, EditActivity::class.java).apply {
                    putExtra(MyIntenConstants.TITLE_KEY, item.title)
                    putExtra(MyIntenConstants.CONTENT_KEY, item.desc)
                    putExtra(MyIntenConstants.URI_KEY, item.uri)
                    putExtra(MyIntenConstants.ID_KEY, item.id)

                }
                context.startActivity(intent)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rc_item, parent, false)
        return ViewHolder(view, context)
    }

    override fun getItemCount(): Int {
        return listTitle.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listTitle[position])
    }

    fun updateAdapter(listItems: List<ListItem>) {
        listTitle.clear()
        listTitle.addAll(listItems)
        notifyDataSetChanged()
    }

    fun removeItem(pos: Int,dbManager: MyDbManager) {
        dbManager.removeItemFromDb(listTitle[pos].id.toString())
        listTitle.removeAt(pos)
        notifyItemRangeChanged(0,listTitle.size)
        notifyDataSetChanged()
    }
}