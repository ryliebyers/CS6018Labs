package com.example.mvvmwithrecyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mvvmwithrecyclerview.databinding.ActivityMainBinding
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)

        //"DELEGATED PROPERTY".  Access to myViewModel calls viewModels.getValue which manages lifecycle stuff
        val myViewModel : TodoViewModel by viewModels()

        with(binding.recycler){
            layoutManager = LinearLayoutManager(this@MainActivity) //NEW KOTLIN THING
            adapter = TodoListAdapter(listOf()){
                myViewModel.removeItem(it)
            }
        }

        myViewModel.observableList.observe(this) {
            (binding.recycler.adapter as TodoListAdapter).updateList(it)
            Log.e("LIST SIZE", "${it.size}")

            binding.numberOfItems.text = "${it.size} item${if(it.size == 1) "" else "s"}"
        }

        binding.button.setOnClickListener{
            val text = binding.newItemText.text.toString()
            if(text.isEmpty()){
                return@setOnClickListener //NEW KOTLIN THING
            }
            myViewModel.newItem(text)
            binding.newItemText.text.clear()

        }

        setContentView(binding.root)
    }
}