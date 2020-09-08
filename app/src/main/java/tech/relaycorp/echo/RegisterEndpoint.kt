package tech.relaycorp.echo

import android.content.SharedPreferences
import javax.inject.Inject

class RegisterEndpoint
@Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    private val currentEndpoint get() =
        sharedPreferences.getString("endpoint", null)

    suspend fun registerIfNeeded() {
        if (currentEndpoint != null) return

        // TODO: register
    }
}
