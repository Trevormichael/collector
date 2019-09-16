package collector.tmc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import collector.lib.Collection
import collector.lib.Collect
import collector.lib.Collector
import collector.tmc.R
class MainActivity : AppCompatActivity() {

    companion object {
        private const val MESSAGE_LIST = "MESSAGE_LIST"
        private const val MESSAGE_LIST_2 = "MESSAGE_LIST_2"
    }

    @Collection(tag = MESSAGE_LIST)
    lateinit var messagesToDisplay: List<String>

    @Collection(tag = MESSAGE_LIST_2)
    lateinit var messages: List<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Collector.bind(this)
        findViewById<TextView>(R.id.textView).text =  messagesToDisplay.joinToString(separator = ", ")
    }

    @Collect(tag = MESSAGE_LIST) @JvmField
    val theFirstMessage = "The First Message"

    @Collect(tag = MESSAGE_LIST) @JvmField
    val theSecondMessage = "The Second Message"

    @Collect(tag = MESSAGE_LIST) @JvmField
    val theThirdMessage = "The Third Message"

    @Collect(tag = MESSAGE_LIST_2) @JvmField
    val theRealMessage = Message("Title", "Body")

    data class Message(val title: String, val body: String)
}
