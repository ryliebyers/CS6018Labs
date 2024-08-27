package com.example.emailsplitter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var textInput : EditText
    private lateinit var usernameView: TextView
    private lateinit var domainView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textInput = findViewById(R.id.emailInput)
        usernameView = findViewById(R.id.usernameView)
        domainView = findViewById(R.id.domainView)

        findViewById<Button>(R.id.splitButton).setOnClickListener(clickListener)


    }


    private val clickListener = View.OnClickListener(){
        val email = textInput.text.toString()
        val pieces = email.split('@')
        if(pieces.size != 2 || pieces.any (String::isEmpty) ){
            Toast.makeText(this, "Invalid email!", Toast.LENGTH_LONG).show()
        } else {
            usernameView.text = pieces[0]
            domainView.text = pieces[1]
        }
    }


}