package tech.relaycorp.echo

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import tech.relaycorp.echo.di.AppComponent
import tech.relaycorp.echo.di.AppModule
import tech.relaycorp.echo.di.DaggerAppComponent
import tech.relaycorp.sdk.RelaynetClient
import javax.inject.Inject

class App : Application() {

    val component: AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

    @Inject
    lateinit var receiveMessage: ReceiveMessage

    override fun onCreate() {
        super.onCreate()
        component.inject(this)

        RelaynetClient.setup(this)

        CoroutineScope(Dispatchers.IO).launch {
            RelaynetClient.receiveMessages()
                .collect(receiveMessage::receive)
        }
    }
}
