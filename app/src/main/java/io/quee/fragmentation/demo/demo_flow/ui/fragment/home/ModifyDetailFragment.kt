package io.quee.fragmentation.demo.demo_flow.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import io.quee.fragmentation.core.ISupportFragment
import io.quee.fragmentation.demo.R
import io.quee.fragmentation.demo.demo_flow.base.BaseBackFragment
import io.quee.fragmentation.demo.demo_flow.ui.fragment.CycleFragment

/**
 * Created by YoKeyword on 16/2/7.
 */
class ModifyDetailFragment :
    BaseBackFragment() {
    private var mToolbar: Toolbar? = null
    private var mEtModiyTitle: EditText? = null
    private var mBtnModify: Button? = null
    private var mBtnNext: Button? = null
    private var mTitle: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args != null) {
            mTitle =
                args.getString(ARG_TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View =
            inflater.inflate(R.layout.fragment_modify_detail, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        mToolbar =
            view.findViewById<View>(R.id.toolbar) as Toolbar
        mEtModiyTitle = view.findViewById<View>(R.id.et_modify_title) as EditText
        mBtnModify = view.findViewById<View>(R.id.btn_modify) as Button
        mBtnNext = view.findViewById<View>(R.id.btn_next) as Button
        mToolbar?.setTitle(R.string.start_result_test)
        initToolbarNav(mToolbar!!)
        mEtModiyTitle!!.setText(mTitle)
        // 显示 软键盘
//        showSoftInput(mEtModiyTitle);
        mBtnModify!!.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(
                DetailFragment.Companion.KEY_RESULT_TITLE,
                mEtModiyTitle!!.text.toString()
            )
            setFragmentResult(ISupportFragment.RESULT_OK, bundle)
            Toast.makeText(_mActivity, R.string.modify_success, Toast.LENGTH_SHORT).show()
        }
        mBtnNext!!.setOnClickListener {
            start(
                CycleFragment.Companion.newInstance(
                    1
                )
            )
        }
    }

    override fun onSupportInvisible() {
        super.onSupportInvisible()
        hideSoftInput()
    }

    companion object {
        private const val ARG_TITLE = "arg_title"
        fun newInstance(title: String?): ModifyDetailFragment {
            val args = Bundle()
            val fragment =
                ModifyDetailFragment()
            args.putString(
                ARG_TITLE,
                title
            )
            fragment.arguments = args
            return fragment
        }
    }
}