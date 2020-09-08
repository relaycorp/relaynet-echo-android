package tech.relaycorp.echo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        (context?.applicationContext as? App)?.component?.inject(this) ?: return

        // TODO: Start receiving messages
    }
}
