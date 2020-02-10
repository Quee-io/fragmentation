package io.quee.fragmentation.demo.demo_flow.ui.fragment.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import io.quee.fragmentation.demo.R
import io.quee.fragmentation.demo.demo_flow.base.BaseMainFragment
import io.quee.fragmentation.demo.demo_flow.base.MySupportFragment
import java.util.*

/**
 * Created by YoKeyword on 16/2/4.
 */
class ShopFragment : BaseMainFragment() {
    private var mToolbar: Toolbar? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_shop, container, false)
        initView(view, savedInstanceState)
        return view
    }

    private fun initView(view: View, savedInstanceState: Bundle?) {
        mToolbar =
            view.findViewById<View>(R.id.toolbar) as Toolbar
        mToolbar?.setTitle(R.string.shop)
        initToolbarNav(mToolbar!!)
        if (findChildFragment(
                MenuListFragment::class.java
            ) == null
        ) {
            val listMenus = ArrayList(
                Arrays.asList(
                    *resources.getStringArray(R.array.array_menu)
                )
            )
            val menuListFragment: MenuListFragment =
                MenuListFragment.Companion.newInstance(
                    listMenus
                )
            loadRootFragment(R.id.fl_list_container, menuListFragment)
            // false:  不加入回退栈;  false: 不显示动画
            loadRootFragment(
                R.id.fl_content_container,
                ContentFragment.Companion.newInstance(
                    listMenus[0]
                ),
                false,
                false
            )
        }
    }

    override fun onBackPressedSupport(): Boolean { // ContentFragment是ShopFragment的栈顶子Fragment,会先调用ContentFragment的onBackPressedSupport方法
        Toast.makeText(
            _mActivity,
            "onBackPressedSupport-->return false, " + getString(R.string.upper_process),
            Toast.LENGTH_SHORT
        ).show()
        return false
    }

    /**
     * 替换加载 内容Fragment
     *
     * @param fragment
     */
    fun switchContentFragment(fragment: ContentFragment?) {
        val contentFragment: MySupportFragment? =
            findChildFragment(
                ContentFragment::class.java
            )
        contentFragment?.replaceFragment(fragment, false)
    }

    companion object {
        val TAG =
            ShopFragment::class.java.simpleName

        fun newInstance(): ShopFragment {
            val args = Bundle()
            val fragment =
                ShopFragment()
            fragment.arguments = args
            return fragment
        }
    }
}