package com.example.lab1

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.lab1.databinding.ActivityDisplayTextBinding

class displayText : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDisplayTextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_text)

        // Get the text from the intent
        val text = intent.getStringExtra("TEXT_KEY")

        // Find the TextView in the layout
        val textView: TextView = findViewById(R.id.textView)

        // Set the text to the TextView
        textView.text = text

        val buttonBack: View? = findViewById(R.id.buttonBack)
        if (buttonBack != null) {
            buttonBack.setOnClickListener {
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_display_text)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}