package tech.relaycorp.echo

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import tech.relaycorp.sdk.FirstPartyEndpoint
import tech.relaycorp.sdk.GatewayClient
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var messageRepository: MessageRepository

    @Inject
    lateinit var sendMessage: SendMessage

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val component by lazy { (applicationContext as App).component }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            send.isEnabled = false
            GatewayClient.bind()
            if (!sharedPreferences.getBoolean("setup", false)) {
                val editor = sharedPreferences.edit()
                editor.putString("sender", FirstPartyEndpoint.register().address)
                editor.putString("receiver", FirstPartyEndpoint.register().address)
                editor.putBoolean("setup", true)
                editor.apply()
            }
            send.isEnabled = true
        }

        send.setOnClickListener {
            lifecycleScope.launch {
                send.isEnabled = false
                val success = sendMessage.send(messageField.text.toString())
                if (success) {
                    messageField.setText("")
                }
                send.isEnabled = true
            }
        }

        clear.setOnClickListener {
            lifecycleScope.launch {
                messageRepository.clear()
            }
        }

        messageRepository
            .observe()
            .onEach {
                messages.text = it.joinToString("\n") { message ->
                    "(${Date(message.time)}) ${message.message}"
                }
            }
            .launchIn(lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        GatewayClient.unbind()
    }
}
