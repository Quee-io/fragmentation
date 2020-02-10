package io.quee.fragmentation.demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import io.quee.fragmentation.demo.demo_flow.MainActivity

/**
 * Created by YoKeyword on 16/6/5.
 */
class EnterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter)
        initView()
    }

    private fun initView() {
        val toolBar =
            findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolBar)
        findViewById<View>(R.id.btn_flow).setOnClickListener {
            startActivity(
                Intent(
                    this@EnterActivity,
                    MainActivity::class.java
                )
            )
        }
        findViewById<View>(R.id.btn_wechat).setOnClickListener {
            startActivity(
                Intent(
                    this@EnterActivity,
                    MainActivity::class.java
                )
            )
        }
        findViewById<View>(R.id.btn_zhihu).setOnClickListener {
            startActivity(
                Intent(
                    this@EnterActivity,
                    MainActivity::class.java
                )
            )
        }
    }
}