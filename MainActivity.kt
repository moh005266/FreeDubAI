package com.freedubai.app

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import java.io.File
import java.util.Locale

/**
 * FreeDub AI starter prototype.
 * Current working features:
 * - Pick a video using Android's document picker.
 * - Choose Hindi or Urdu as the intended output language.
 * - Type/paste a translated script and generate a local TTS audio file.
 *
 * This is NOT yet a full automatic movie-dubbing pipeline.
 * Automatic ASR, machine translation, subtitle/audio timing, and final video muxing
 * must be integrated before it can automatically dub a 1–3 hour movie.
 */
class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private val pickVideoRequest = 1001
    private var selectedVideo: Uri? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private lateinit var status: TextView
    private lateinit var language: Spinner
    private lateinit var script: EditText
    private lateinit var makeAudio: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.TOP
            setPadding(24, 32, 24, 24)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val title = TextView(this).apply {
            text = "FreeDub AI"
            textSize = 28f
            setTextColor(0xFF172554.toInt())
        }
        root.addView(title)

        val subtitle = TextView(this).apply {
            text = "Free starter • English video → Hindi/Urdu workflow"
            textSize = 14f
            setPadding(0, 8, 0, 20)
        }
        root.addView(subtitle)

        val pick = Button(this).apply {
            text = "1. Select video"
            setOnClickListener { openVideoPicker() }
        }
        root.addView(pick)

        status = TextView(this).apply {
            text = "No video selected"
            textSize = 14f
            setPadding(0, 8, 0, 16)
        }
        root.addView(status)

        root.addView(TextView(this).apply {
            text = "2. Choose target language"
            textSize = 16f
        })
        language = Spinner(this)
        val langs = listOf("Hindi", "Urdu")
        language.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, langs
        )
        root.addView(language)

        root.addView(TextView(this).apply {
            text = "3. Paste translated script"
            textSize = 16f
            setPadding(0, 18, 0, 6)
        })
        script = EditText(this).apply {
            hint = "Hindi/Urdu translated text yahan paste karein..."
            minLines = 5
            gravity = Gravity.TOP or Gravity.START
            setPadding(12, 12, 12, 12)
        }
        root.addView(script, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        makeAudio = Button(this).apply {
            text = "4. Generate voice audio"
            isEnabled = false
            setOnClickListener { generateSpeech() }
        }
        root.addView(makeAudio)

        root.addView(TextView(this).apply {
            text = "Note: This prototype makes a voice file from text. It does not yet automatically translate the movie or merge the voice back into the video."
            textSize = 12f
            setPadding(0, 12, 0, 0)
        })

        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun openVideoPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "video/*"
        }
        startActivityForResult(intent, pickVideoRequest)
    }

    @Deprecated("Use Activity Result APIs in a production app")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickVideoRequest && resultCode == RESULT_OK) {
            selectedVideo = data?.data
            status.text = selectedVideo?.lastPathSegment?.let {
                "Video selected: $it"
            } ?: "Video selected"
            makeAudio.isEnabled = true
        }
    }

    override fun onInit(statusCode: Int) {
        ttsReady = statusCode == TextToSpeech.SUCCESS
        if (ttsReady) {
            setTtsLanguage()
        } else {
            runOnUiThread {
                Toast.makeText(this, "Android TTS initialize nahi hua.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setTtsLanguage(): Boolean {
        val locale = if (language.selectedItemPosition == 1) {
            Locale("ur", "PK")
        } else {
            Locale("hi", "IN")
        }
        val result = tts?.setLanguage(locale) ?: TextToSpeech.LANG_MISSING_DATA
        return result != TextToSpeech.LANG_MISSING_DATA &&
               result != TextToSpeech.LANG_NOT_SUPPORTED
    }

    private fun generateSpeech() {
        if (!ttsReady) {
            Toast.makeText(this, "TTS abhi ready nahi hai.", Toast.LENGTH_LONG).show()
            return
        }
        val text = script.text.toString().trim()
        if (text.isEmpty()) {
            script.error = "Pehle translated text paste karein"
            return
        }
        if (!setTtsLanguage()) {
            Toast.makeText(
                this,
                "Is language ki voice phone mein available nahi. TTS voice settings check karein.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val out = File(getExternalFilesDir(null), "freedub_voice.wav")
        val result = tts?.synthesizeToFile(text, Bundle(), out, "freedub_audio")
        if (result == TextToSpeech.SUCCESS) {
            Toast.makeText(
                this,
                "Voice generation request bhej di gayi. File location: ${out.absolutePath}",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(this, "Audio generate nahi ho saka.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
