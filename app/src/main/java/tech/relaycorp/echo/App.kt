package tech.relaycorp.echo

import android.app.Application
import tech.relaycorp.echo.di.AppComponent
import tech.relaycorp.echo.di.AppModule
import tech.relaycorp.echo.di.DaggerAppComponent

class App : Application() {

    val component: AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
}
