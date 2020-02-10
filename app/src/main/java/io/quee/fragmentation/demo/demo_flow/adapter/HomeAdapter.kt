package io.quee.fragmentation.demo.demo_flow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.quee.fragmentation.demo.R
import io.quee.fragmentation.demo.demo_flow.entity.Article
import io.quee.fragmentation.demo.demo_flow.listener.OnItemClickListener
import java.util.*

/**
 * 主页HomeFragment  Adapter
 * Created by YoKeyword on 16/2/1.
 */
class HomeAdapter(context: Context?) :
    RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {
    private val mItems: MutableList<Article> =
        ArrayList()
    private val mInflater: LayoutInflater
    private var mClickListener: OnItemClickListener? =
        null

    fun setDatas(items: List<Article>?) {
        mItems.clear()
        mItems.addAll(items!!)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view: View = mInflater.inflate(R.layout.item_home, parent, false)
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
        holder.tvTitle.text = item.title
        holder.tvContent.text = item.content
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun getItem(position: Int): Article {
        return mItems[position]
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener?) {
        mClickListener = itemClickListener
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView
        val tvContent: TextView

        init {
            tvTitle = itemView.findViewById<View>(R.id.tv_title) as TextView
            tvContent = itemView.findViewById<View>(R.id.tv_content) as TextView
        }
    }

    init {
        mInflater = LayoutInflater.from(context)
    }
}