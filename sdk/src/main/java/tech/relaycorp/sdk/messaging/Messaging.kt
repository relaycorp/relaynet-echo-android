package tech.relaycorp.sdk.messaging

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class Messaging {

    // Send

    suspend fun send(message: Message) {}

    // Receive

    fun receive(): Flow<Message> = emptyFlow()
}
