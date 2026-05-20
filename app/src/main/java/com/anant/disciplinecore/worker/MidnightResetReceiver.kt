package com.anant.disciplinecore.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

// Placeholder for future midnight reset via AlarmManager
class MidnightResetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // The ViewModel already handles stale reset on every app open.
        // Future: schedule daily alarm here for background reset.
    }
}
