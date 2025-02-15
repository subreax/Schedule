package com.subreax.schedule.utils

import java.lang.ref.WeakReference

class WeakObjectHolder<T> {
    private var ref = WeakReference<T>(null)

    var value: T?
        get() = ref.get()
        set(value) {
            ref = WeakReference(value)
        }
}
