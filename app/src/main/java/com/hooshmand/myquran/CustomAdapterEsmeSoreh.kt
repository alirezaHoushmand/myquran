package com.hooshmand.myquran

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hooshmand.myquran.setting.fontEsmesoreh
import com.hooshmand.myquran.soreh.AyehNo
import com.hooshmand.myquran.soreh.SorehNo

class CustomAdapterEsmeSoreh
    (val userList: ArrayList<data_esme_soreh>, private val listener: onItemClickListener) :
    RecyclerView.Adapter<CustomAdapterEsmeSoreh.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_soreh, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewName.text = userList[position].esme
        holder.textViewName.textSize=fontEsmesoreh
        if (position== SorehNo)
            holder.textViewName.setTextColor(Color.BLUE)
        else
            holder.textViewName.setTextColor(Color.BLACK)
    }

    override fun getItemCount(): Int {
        return userList.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val textViewName = itemView.findViewById(R.id.text_view_esme_soreh) as TextView

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface onItemClickListener {
        fun onItemClick(postion: Int)
    }
}