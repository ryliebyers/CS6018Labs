package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    //Method 1
    private lateinit var mytext:TextView
    //Method 2
    //private var mytext:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        mytext = findViewById<TextView>(R.id.mytextview)

        findViewById<Button>(R.id.submit_button).setOnClickListener {
            //Method 1
            mytext.text = "My cs6018 demo"
            //Method 2
            //mytext!!.text = "My cs6018 demo"
        }

    }
}