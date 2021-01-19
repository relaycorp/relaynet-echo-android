package tech.relaycorp.sdk.gateway

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

// Future work:
// - Config automatic bind
class Gateway {

    suspend fun bind() {}

    suspend fun unbind() {}

    suspend fun observerState(): Flow<State> = emptyFlow()

    enum class State { Unbinded, Binded }
}
