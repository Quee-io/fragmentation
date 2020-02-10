package io.quee.fragmentation.core.debug

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentationMagician
import io.quee.fragmentation.core.Fragmentation
import io.quee.fragmentation.core.ISupportFragment
import io.quee.fragmentation.core.R
import java.util.*

/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */

class DebugStackDelegate(activity: FragmentActivity) :
    SensorEventListener {
    private val mActivity: FragmentActivity
    private var mSensorManager: SensorManager? = null
    private var mStackDialog: AlertDialog? = null
    fun onCreate(mode: Int) {
        if (mode != Fragmentation.SHAKE) return
        mSensorManager =
            mActivity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensorManager!!.registerListener(
            this,
            mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun onPostCreate(mode: Int) {
        if (mode != Fragmentation.BUBBLE) return
        val root =
            mActivity.findViewById<View>(R.id.content)
        if (root is FrameLayout) {
            val stackView = ImageView(mActivity)
            stackView.setImageResource(R.drawable.fragmentation_ic_stack)
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.END
            val dp18 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                18f,
                mActivity.resources.displayMetrics
            ).toInt()
            params.topMargin = dp18 * 7
            params.rightMargin = dp18
            stackView.layoutParams = params
            root.addView(stackView)
            stackView.setOnTouchListener(
                StackViewTouchListener(
                    stackView,
                    dp18 / 4
                )
            )
            stackView.setOnClickListener { showFragmentStackHierarchyView() }
        }
    }

    fun onDestroy() {
        if (mSensorManager != null) {
            mSensorManager!!.unregisterListener(this)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val sensorType = event.sensor.type
        val values = event.values
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            val value = 12
            if (Math.abs(values[0]) >= value || Math.abs(values[1]) >= value || Math.abs(
                    values[2]
                ) >= value
            ) {
                showFragmentStackHierarchyView()
            }
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }

    /**
     * 调试相关:以dialog形式 显示 栈视图
     */
    fun showFragmentStackHierarchyView() {
        if (mStackDialog != null && mStackDialog!!.isShowing) return
        val container = DebugHierarchyViewContainer(mActivity)
        container.bindFragmentRecords(getFragmentRecords())
        container.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mStackDialog = AlertDialog.Builder(mActivity)
            .setView(container)
            .setPositiveButton(R.string.cancel, null)
            .setCancelable(true)
            .create()
        mStackDialog!!.show()
    }

    /**
     * 调试相关:以log形式 打印 栈视图
     */
    fun logFragmentRecords(tag: String?) {
        val fragmentRecordList = getFragmentRecords() ?: return
        val sb = StringBuilder()
        for (i in fragmentRecordList.indices.reversed()) {
            val fragmentRecord = fragmentRecordList[i]
            if (i == fragmentRecordList.size - 1) {
                sb.append("═══════════════════════════════════════════════════════════════════════════════════\n")
                if (i == 0) {
                    sb.append("\t栈顶\t\t\t").append(fragmentRecord.fragmentName).append("\n")
                    sb.append("═══════════════════════════════════════════════════════════════════════════════════")
                } else {
                    sb.append("\t栈顶\t\t\t").append(fragmentRecord.fragmentName).append("\n\n")
                }
            } else if (i == 0) {
                sb.append("\t栈底\t\t\t").append(fragmentRecord.fragmentName).append("\n\n")
                processChildLog(fragmentRecord.childFragmentRecord, sb, 1)
                sb.append("═══════════════════════════════════════════════════════════════════════════════════")
                Log.i(tag, sb.toString())
                return
            } else {
                sb.append("\t↓\t\t\t").append(fragmentRecord.fragmentName).append("\n\n")
            }
            processChildLog(fragmentRecord.childFragmentRecord, sb, 1)
        }
    }

    private fun getFragmentRecords(): List<DebugFragmentRecord>? {
        val fragmentRecordList: MutableList<DebugFragmentRecord> =
            ArrayList()
        val fragmentList: List<Fragment?> =
            FragmentationMagician.getActiveFragments(mActivity.supportFragmentManager)
        if (fragmentList.isEmpty()) return null
        for (fragment in fragmentList) {
            addDebugFragmentRecord(fragmentRecordList, fragment)
        }
        return fragmentRecordList
    }

    private fun processChildLog(
        fragmentRecordList: List<DebugFragmentRecord>?,
        sb: StringBuilder,
        childHierarchy: Int
    ) {
        var childHierarchy = childHierarchy
        if (fragmentRecordList == null || fragmentRecordList.size == 0) return
        for (j in fragmentRecordList.indices) {
            val childFragmentRecord = fragmentRecordList[j]
            for (k in 0 until childHierarchy) {
                sb.append("\t\t\t")
            }
            if (j == 0) {
                sb.append("\tSubstack top\t\t").append(childFragmentRecord.fragmentName)
                    .append("\n\n")
            } else if (j == fragmentRecordList.size - 1) {
                sb.append("\tBottom of substack\t\t").append(childFragmentRecord.fragmentName)
                    .append("\n\n")
                processChildLog(childFragmentRecord.childFragmentRecord, sb, ++childHierarchy)
                return
            } else {
                sb.append("\t↓\t\t\t").append(childFragmentRecord.fragmentName).append("\n\n")
            }
            processChildLog(childFragmentRecord.childFragmentRecord, sb, childHierarchy)
        }
    }

    private fun getChildFragmentRecords(parentFragment: Fragment): List<DebugFragmentRecord>? {
        val fragmentRecords: MutableList<DebugFragmentRecord> =
            ArrayList()
        val fragmentList: List<Fragment?> =
            FragmentationMagician.getActiveFragments(parentFragment.childFragmentManager)
        if (fragmentList.isEmpty()) return null
        for (i in fragmentList.indices.reversed()) {
            val fragment = fragmentList[i]
            addDebugFragmentRecord(fragmentRecords, fragment)
        }
        return fragmentRecords
    }

    private fun addDebugFragmentRecord(
        fragmentRecords: MutableList<DebugFragmentRecord>,
        fragment: Fragment?
    ) {
        if (fragment != null) {
            val backStackCount = fragment.fragmentManager!!.backStackEntryCount
            var name: CharSequence = fragment.javaClass.simpleName
            if (backStackCount == 0) {
                name = span(name, " *")
            } else {
                for (j in 0 until backStackCount) {
                    val entry =
                        fragment.fragmentManager!!.getBackStackEntryAt(j)
                    if (entry.name != null && entry.name == fragment.tag
                        || entry.name == null && fragment.tag == null
                    ) {
                        break
                    }
                    if (j == backStackCount - 1) {
                        name = span(name, " *")
                    }
                }
            }
            if (fragment is ISupportFragment && (fragment as ISupportFragment).isSupportVisible()) {
                name = span(name, " ☀")
            }
            fragmentRecords.add(DebugFragmentRecord(name, getChildFragmentRecords(fragment)!!))
        }
    }

    private fun span(name: CharSequence, str: String): CharSequence {
        var name = name
        name = name.toString() + str
        return name
    }

    private inner class StackViewTouchListener internal constructor(
        private val stackView: View,
        private val clickLimitValue: Int
    ) :
        OnTouchListener {
        private var dX = 0f
        private var dY = 0f
        private var downX = 0f
        private var downY = 0f
        private var isClickState = false
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val X = event.rawX
            val Y = event.rawY
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isClickState = true
                    downX = X
                    downY = Y
                    dX = stackView.x - event.rawX
                    dY = stackView.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> if (Math.abs(X - downX) < clickLimitValue && Math.abs(
                        Y - downY
                    ) < clickLimitValue && isClickState
                ) {
                    isClickState = true
                } else {
                    isClickState = false
                    stackView.x = event.rawX + dX
                    stackView.y = event.rawY + dY
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> if (X - downX < clickLimitValue && isClickState) {
                    stackView.performClick()
                }
                else -> return false
            }
            return true
        }

    }

    init {
        mActivity = activity
    }
}