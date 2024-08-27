package com.example.lab1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.lab1.databinding.ActivityButtonBinding

class ButtonActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityButtonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_button)


        val button1: View? = findViewById(R.id.button1)
        val button2: View? = findViewById(R.id.button2)
        val button3: View? = findViewById(R.id.button3)
        val button4: View? = findViewById(R.id.button4)
        val button5: View? = findViewById(R.id.button5)




        if (button1 != null) {
            button1.setOnClickListener {
                val intent = Intent(this, displayText::class.java)
                intent.putExtra("TEXT_KEY", "who's there?")
                startActivity(intent)
            }
        }
        if (button2 != null) {
            button2.setOnClickListener {
                val intent = Intent(this, displayText::class.java)
                intent.putExtra("TEXT_KEY", "Hello from Name!")
                startActivity(intent)
            }
        }
        if (button3 != null) {
            button3.setOnClickListener {
                val intent = Intent(this, displayText::class.java)
                intent.putExtra("TEXT_KEY", "Hello from Username!")
                startActivity(intent)
            }
        }

        if (button4 != null) {
            button4.setOnClickListener {
                val intent = Intent(this, displayText::class.java)
                intent.putExtra("TEXT_KEY", "Hello from Password!")
                startActivity(intent)
            }
        }


        if (button5 != null) {
            button5.setOnClickListener {
                val intent = Intent(this, displayText::class.java)
                intent.putExtra("TEXT_KEY", "Hello from login!")
                startActivity(intent)
            }
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_button)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}