package com.example.letstalk.common.observer

import android.app.Activity
import android.app.Application
import android.os.Bundle

class AppLifeCycleTracker(
    private val onAppForeground: () -> Unit,
    private val onAppBackground: () -> Unit,
    private val onDestroy:()->Unit
) :
    Application.ActivityLifecycleCallbacks {
    private var activityReference = 0
    private var isActivityConfigChanges = false
    override fun onActivityStarted(activity: Activity) {
        if (++activityReference == 1 && !isActivityConfigChanges) {
            onAppForeground()
        }
    }

    override fun onActivityStopped(activity: Activity) {
        isActivityConfigChanges = activity.isChangingConfigurations
        if (--activityReference == 0 && !isActivityConfigChanges) {
            onAppBackground()
        }
    }

    override fun onActivityResumed(p0: Activity) {
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }
}