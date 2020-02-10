package io.quee.fragmentation.demo.demo_flow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.quee.fragmentation.demo.R
import io.quee.fragmentation.demo.demo_flow.listener.OnItemClickListener
import java.util.*

/**
 * 发现Discover里的子Fragment  Adapter
 * Created by YoKeyword on 16/2/1.
 */
class PagerAdapter(context: Context?) :
    RecyclerView.Adapter<PagerAdapter.MyViewHolder>() {
    private val mItems: MutableList<String> =
        ArrayList()
    private val mInflater: LayoutInflater
    private var mClickListener: OnItemClickListener? =
        null

    fun setDatas(items: List<String>?) {
        mItems.clear()
        mItems.addAll(items!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view: View = mInflater.inflate(R.layout.item_pager, parent, false)
        val holder =
            MyViewHolder(view)
        holder.itemView.setOnClickListener { v ->
            val position = holder.adapterPosition
            if (mClickListener != null) {
                mClickListener!!.onItemClick(position, v)
            }
        }
        return holder
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val item = mItems[position]
        holder.tvTitle.text = item
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener) {
        mClickListener = itemClickListener
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)

    }

    init {
        mInflater = LayoutInflater.from(context)
    }
}