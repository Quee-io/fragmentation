package io.quee.fragmentation.demo.demo_flow.adapter

import android.content.Context
import android.graphics.Color
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.quee.fragmentation.demo.R
import io.quee.fragmentation.demo.demo_flow.listener.OnItemClickListener
import java.util.*

/**
 * Created by YoKeyword on 16/2/10.
 */
class MenuAdapter(context: Context) :
    RecyclerView.Adapter<MenuAdapter.MyViewHolder>() {
    private val mInflater: LayoutInflater
    private val mContext: Context
    private val mItems: MutableList<String> =
        ArrayList()
    private var mBooleanArray: SparseBooleanArray? = null
    private var mClickListener: OnItemClickListener? =
        null
    private var mLastCheckedPosition = -1
    fun setDatas(items: List<String>) {
        mItems.clear()
        mItems.addAll(items)
        mBooleanArray = SparseBooleanArray(mItems.size)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view: View = mInflater.inflate(R.layout.item_menu, parent, false)
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
        if (!mBooleanArray!![position]) {
            holder.viewLine.visibility = View.INVISIBLE
            holder.itemView.setBackgroundResource(R.color.bg_app)
            holder.tvName.setTextColor(Color.BLACK)
        } else {
            holder.viewLine.visibility = View.VISIBLE
            holder.itemView.setBackgroundColor(Color.WHITE)
            holder.tvName.setTextColor(mContext.resources.getColor(R.color.colorAccent))
        }
        holder.tvName.text = mItems[position]
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun setItemChecked(position: Int) {
        mBooleanArray!!.put(position, true)
        if (mLastCheckedPosition > -1) {
            mBooleanArray!!.put(mLastCheckedPosition, false)
            notifyItemChanged(mLastCheckedPosition)
        }
        notifyDataSetChanged()
        mLastCheckedPosition = position
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener?) {
        mClickListener = itemClickListener
    }

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var viewLine: View
        var tvName: TextView

        init {
            viewLine = itemView.findViewById(R.id.view_line)
            tvName = itemView.findViewById<View>(R.id.tv_name) as TextView
        }
    }

    init {
        mInflater = LayoutInflater.from(context)
        mContext = context
    }
}