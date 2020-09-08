package tech.relaycorp.echo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var registerEndpoint: RegisterEndpoint

    @Inject
    lateinit var messageRepository: MessageRepository

    @Inject
    lateinit var sendMessage: SendMessage

    private val component by lazy { (applicationContext as App).component }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            registerEndpoint.registerIfNeeded()
        }

        clear.setOnClickListener {
            lifecycleScope.launch {
                messageRepository.clear()
            }
        }

        send.setOnClickListener {
            lifecycleScope.launch {
                sendMessage.send(messageField.text.toString())
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
}
