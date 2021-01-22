package tech.relaycorp.echo.di

import dagger.Component
import tech.relaycorp.echo.App
import tech.relaycorp.echo.MainActivity
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class
    ]
)
interface AppComponent {
    fun inject(app: App)
    fun inject(activity: MainActivity)
}
