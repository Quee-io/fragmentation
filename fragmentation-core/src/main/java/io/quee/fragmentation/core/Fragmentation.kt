package io.quee.fragmentation.core

import androidx.annotation.IntDef
import io.quee.fragmentation.core.helper.ExceptionHandler

/**
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */

class Fragmentation internal constructor(builder: FragmentationBuilder) {
    var debug: Boolean
    var mode = BUBBLE
    var handler: ExceptionHandler?
    fun isDebug(): Boolean {
        return debug
    }

    @IntDef(
        NONE,
        SHAKE,
        BUBBLE
    )
    @Retention(AnnotationRetention.SOURCE)
    internal annotation class StackViewMode

    class FragmentationBuilder {
        var debug = false
        var mode = 0
        var handler: ExceptionHandler? = null
        /**
         * @param debug Suppressed Exception("Can not perform this action after onSaveInstanceState!") when debug=false
         */
        fun debug(debug: Boolean): FragmentationBuilder {
            this.debug = debug
            return this
        }

        /**
         * Sets the mode to display the stack view
         *
         *
         * None if debug(false).
         *
         *
         * Default:NONE
         */
        fun stackViewMode(@StackViewMode mode: Int): FragmentationBuilder {
            this.mode = mode
            return this
        }

        /**
         * @param handler Handled Exception("Can not perform this action after onSaveInstanceState!") when debug=false.
         */
        fun handleException(handler: ExceptionHandler): FragmentationBuilder {
            this.handler = handler
            return this
        }

        fun install(): Fragmentation? {
            INSTANCE = Fragmentation(this)
            return INSTANCE
        }
    }

    companion object {
        /**
         * Dont display stack view.
         */
        const val NONE = 0
        /**
         * Shake it to display stack view.
         */
        const val SHAKE = 1
        /**
         * As a bubble display stack view.
         */
        const val BUBBLE = 2
        @Volatile
        var INSTANCE: Fragmentation? = null

        val default: Fragmentation
            get() {
                if (INSTANCE == null) {
                    synchronized(Fragmentation::class.java) {
                        if (INSTANCE == null) {
                            INSTANCE = Fragmentation(FragmentationBuilder())
                        }
                    }
                }
                return INSTANCE!!
            }

        fun builder(): FragmentationBuilder {
            return FragmentationBuilder()
        }
    }

    init {
        debug = builder.debug
        mode = if (debug) {
            builder.mode
        } else {
            NONE
        }
        handler = builder.handler
    }
}