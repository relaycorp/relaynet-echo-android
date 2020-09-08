package tech.relaycorp.echo

import javax.inject.Inject

class SendMessage
@Inject constructor(
    private val messageRepository: MessageRepository
) {

    suspend fun send(message: String) {
        messageRepository.receive(EchoMessage(System.currentTimeMillis(), message))
        // TODO: send
    }
}
