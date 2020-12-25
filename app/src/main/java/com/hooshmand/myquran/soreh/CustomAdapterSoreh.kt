package com.hooshmand.myquran.soreh

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hooshmand.myquran.CustomAdapterEsmeSoreh
import com.hooshmand.myquran.R
import com.hooshmand.myquran.data_esme_soreh
import com.hooshmand.myquran.setting.fontAyeh
import com.hooshmand.myquran.setting.fontTarjomeh

class CustomAdapterSoreh  (val userList: ArrayList<data_Soreh>, private val listener: CustomAdapterSoreh.onItemClickListener) :
    RecyclerView.Adapter<CustomAdapterSoreh.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapterSoreh.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.matn_soreh, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: CustomAdapterSoreh.ViewHolder, position: Int) {
        holder.textViewSoreh.text = userList[position].Ayeh
        holder.textViewSoreh.textSize = fontAyeh

        holder.textViewTarjomeh.text = userList[position].Tarjomeh
        holder.textViewTarjomeh.textSize = fontTarjomeh
        if (position== AyehNo)
            holder.textViewSoreh.setTextColor(Color.BLUE)
        else
            holder.textViewSoreh.setTextColor(Color.BLACK)

    }

    override fun getItemCount(): Int {
        return userList.size
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val textViewSoreh = itemView.findViewById(R.id.text_soreh) as TextView
        val textViewTarjomeh = itemView.findViewById(R.id.text_tarjomeh) as TextView

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
