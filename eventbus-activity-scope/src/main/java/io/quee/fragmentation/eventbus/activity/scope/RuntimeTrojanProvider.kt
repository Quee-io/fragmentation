package io.quee.fragmentation.eventbus.activity.scope

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 * Internal class to initialize EventBusActivityScope.
 *
 * Created by Ibrahim Al-Tamimi on 2020-02-09.
 * Licensed for Quee.io
 */
class RuntimeTrojanProvider constructor() : ContentProvider() {
    public override fun onCreate(): Boolean {
        EventBusActivityScope.init(getContext())
        return true
    }

    public override fun query(
        uri: Uri,
        strings: Array<String>?,
        s: String?,
        strings1: Array<String>?,
        s1: String?
    ): Cursor? {
        return null
    }

    public override fun getType(uri: Uri): String? {
        return null
    }

    public override fun insert(
        uri: Uri,
        contentValues: ContentValues?
    ): Uri? {
        return null
    }

    public override fun delete(
        uri: Uri,
        s: String?,
        strings: Array<String>?
    ): Int {
        return 0
    }

    public override fun update(
        uri: Uri,
        contentValues: ContentValues?,
        s: String?,
        strings: Array<String>?
    ): Int {
        return 0
    }
}