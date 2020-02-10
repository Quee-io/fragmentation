package io.quee.fragmentation.demo.demo_flow.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.quee.fragmentation.core.ISupportFragment
import io.quee.fragmentation.demo.R
import io.quee.fragmentation.demo.demo_flow.base.BaseBackFragment

/**
 * Created by YoKeyword on 16/2/3.
 */
class DetailFragment : BaseBackFragment() {
    private var mToolbar: Toolbar? = null
    private var mTvContent: TextView? = null
    private var mFab: FloatingActionButton? = null
    private var mTitle: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            mTitle =
                bundle.getString(ARG_TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_detail, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        mToolbar =
            view.findViewById<View>(R.id.toolbar) as Toolbar
        mFab = view.findViewById<View>(R.id.fab) as FloatingActionButton
        mTvContent = view.findViewById<View>(R.id.tv_content) as TextView
        mToolbar!!.title = mTitle
        initToolbarNav(mToolbar!!)
    }

    /**
     * 这里演示:
     * 比较复杂的Fragment页面会在第一次start时,导致动画卡顿
     * Fragmentation提供了onEnterAnimationEnd()方法,该方法会在 入栈动画 结束时回调
     * 所以在onCreateView进行一些简单的View初始化(比如 toolbar设置标题,返回按钮; 显示加载数据的进度条等),
     * 然后在onEnterAnimationEnd()方法里进行 复杂的耗时的初始化 (比如FragmentPagerAdapter的初始化 加载数据等)
     */
    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        initDelayView()
    }

    private fun initDelayView() {
        mTvContent?.setText(R.string.large_text)
        mFab!!.setOnClickListener {
            startForResult(
                ModifyDetailFragment.Companion.newInstance(
                    mTitle
                ),
                REQ_MODIFY_FRAGMENT
            )
        }
    }

    override fun onFragmentResult(
        requestCode: Int,
        resultCode: Int,
        data: Bundle?
    ) {
        super.onFragmentResult(requestCode, resultCode, data)
        if (requestCode == REQ_MODIFY_FRAGMENT && resultCode == ISupportFragment.RESULT_OK && data != null) {
            mTitle =
                data.getString(KEY_RESULT_TITLE)
            mToolbar!!.title = mTitle
            // 保存被改变的 title
            arguments!!.putString(
                ARG_TITLE,
                mTitle
            )
            Toast.makeText(_mActivity, R.string.modify_title, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        val TAG =
            DetailFragment::class.java.simpleName
        const val KEY_RESULT_TITLE = "title"
        private const val REQ_MODIFY_FRAGMENT = 100
        private const val ARG_TITLE = "arg_title"
        fun newInstance(title: String?): DetailFragment {
            val fragment =
                DetailFragment()
            val bundle = Bundle()
            bundle.putString(
                ARG_TITLE,
                title
            )
            fragment.arguments = bundle
            return fragment
        }
    }
}