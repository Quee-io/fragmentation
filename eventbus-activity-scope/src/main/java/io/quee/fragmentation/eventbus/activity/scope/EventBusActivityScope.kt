package io.quee.fragmentation.eventbus.activity.scope

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Activity-scope EventBus.
 *
 *
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */
object EventBusActivityScope {
    private val TAG = EventBusActivityScope::class.java.simpleName
    private val sActivityEventBusScopePool: MutableMap<Activity, LazyEventBusInstance?> =
        ConcurrentHashMap()
    private val sInitialized =
        AtomicBoolean(false)
    @Volatile
    private var sInvalidEventBus: EventBus? = null

    fun init(context: Context?) {
        if (sInitialized.getAndSet(true)) {
            return
        }
        (context!!.applicationContext as Application)
            .registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
                private val mainHandler =
                    Handler(Looper.getMainLooper())

                override fun onActivityCreated(
                    activity: Activity,
                    bundle: Bundle
                ) {
                    sActivityEventBusScopePool[activity] = LazyEventBusInstance()
                }

                override fun onActivityStarted(activity: Activity) {}
                override fun onActivityResumed(activity: Activity) {}
                override fun onActivityPaused(activity: Activity) {}
                override fun onActivityStopped(activity: Activity) {}
                override fun onActivitySaveInstanceState(
                    activity: Activity,
                    bundle: Bundle
                ) {
                }

                override fun onActivityDestroyed(activity: Activity) {
                    if (!sActivityEventBusScopePool.containsKey(activity)) return
                    mainHandler.post(Runnable // Make sure Fragment's onDestroy() has been called.
                    { sActivityEventBusScopePool.remove(activity) })
                }
            })
    }

    /**
     * Get the activity-scope EventBus instance
     */
    fun getDefault(activity: Activity?): EventBus? {
        if (activity == null) {
            Log.e(
                TAG,
                "Can't find the Activity, the Activity is null!"
            )
            return invalidEventBus()
        }
        val lazyEventBusInstance =
            sActivityEventBusScopePool[activity]
        if (lazyEventBusInstance == null) {
            Log.e(
                TAG,
                "Can't find the Activity, it has been removed!"
            )
            return invalidEventBus()
        }
        return lazyEventBusInstance.instance
    }

    private fun invalidEventBus(): EventBus? {
        if (sInvalidEventBus == null) {
            synchronized(EventBusActivityScope::class.java) {
                if (sInvalidEventBus == null) {
                    sInvalidEventBus = EventBus()
                }
            }
        }
        return sInvalidEventBus
    }

    internal class LazyEventBusInstance() {
        @Volatile
        private var eventBus: EventBus? = null

        val instance: EventBus?
            get() {
                if (eventBus == null) {
                    synchronized(this) {
                        if (eventBus == null) {
                            eventBus = EventBus()
                        }
                    }
                }
                return eventBus
            }
    }
}