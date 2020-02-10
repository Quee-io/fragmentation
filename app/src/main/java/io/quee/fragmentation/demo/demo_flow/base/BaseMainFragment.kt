package io.quee.fragmentation.demo.demo_flow.base

import android.content.Context
import androidx.appcompat.widget.Toolbar
import io.quee.fragmentation.demo.R

/**
 * Created by YoKeyword on 16/2/3.
 */
open class BaseMainFragment : MySupportFragment() {
    protected var mOpenDraweListener: OnFragmentOpenDrawerListener? = null
    protected fun initToolbarNav(
        toolbar: Toolbar,
        isHome: Boolean = false
    ) {
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp)
        toolbar.setNavigationOnClickListener {
            if (mOpenDraweListener != null) {
                mOpenDraweListener!!.onOpenDrawer()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentOpenDrawerListener) {
            mOpenDraweListener = context
        } else { //            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentOpenDrawerListener");
        }
    }

    override fun onDetach() {
        super.onDetach()
        mOpenDraweListener = null
    }

    interface OnFragmentOpenDrawerListener {
        fun onOpenDrawer()
    }
}