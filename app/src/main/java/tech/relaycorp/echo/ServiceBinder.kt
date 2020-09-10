package tech.relaycorp.echo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun Context.suspendBindService(
    packageName: String,
    componentName: String
): Pair<ServiceConnection, IBinder> =
    suspendCoroutine { cont ->
        val serviceContext = object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, binder: IBinder) {
                cont.resume(Pair(this, binder))
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
            }
        }
        bindService(
            Intent().setComponent(ComponentName(packageName, componentName)),
            serviceContext,
            Context.BIND_AUTO_CREATE
        )
    }
