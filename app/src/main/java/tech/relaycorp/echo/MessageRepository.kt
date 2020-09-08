package tech.relaycorp.echo

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.tfcporciuncula.flow.FlowSharedPreferences
import com.tfcporciuncula.flow.ObjectPreference
import javax.inject.Inject

class MessageRepository
@Inject constructor(
    private val flowSharedPreferences: FlowSharedPreferences,
    private val moshi: Moshi
) {

    private val repo by lazy {
        flowSharedPreferences.getObject(
            "messages",
            object : ObjectPreference.Serializer<List<EchoMessage>> {
                private val adapter = moshi.adapter<List<EchoMessage>>(
                    Types.newParameterizedType(
                        List::class.java,
                        EchoMessage::class.java
                    )
                )

                override fun deserialize(serialized: String) =
                    adapter.fromJson(serialized) ?: emptyList()

                override fun serialize(value: List<EchoMessage>) =
                    adapter.toJson(value)
            },
            emptyList()
        )
    }

    fun observe() = repo.asFlow()

    suspend fun receive(message: EchoMessage) {
        repo.setAndCommit(repo.get() + message)
    }

    suspend fun clear() {
        repo.setAndCommit(emptyList())
    }
}
