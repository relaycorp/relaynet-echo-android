package tech.relaycorp.echo.di

import dagger.Component
import tech.relaycorp.echo.MainActivity
import tech.relaycorp.echo.NotificationBroadcastReceiver
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class
    ]
)
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(receiver: NotificationBroadcastReceiver)
}
