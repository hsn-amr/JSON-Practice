package com.example.jsonpractice

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import org.json.JSONArray
import java.lang.Exception
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var tv: TextView
    lateinit var et: EditText
    lateinit var get: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv = findViewById(R.id.textView)
        et = findViewById(R.id.et)
        get = findViewById(R.id.button)

        get.setOnClickListener {
            if(et.text.isNotEmpty() && et.text.toString().toInt() <= 13){
                hideSoftKeyboard()
                tv.text=""
                requestApi()
            }else{
                Toast.makeText(this, "Type a number or type less than 14", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun requestApi(){
        var data = ""
        CoroutineScope(Dispatchers.IO).launch{
            data = async {
                fetchData()
            }.await()
            if(data.isNotEmpty()){
                val array = JSONArray(data)
                withContext(Dispatchers.Main){
                    for (i in 0 until et.text.toString().toInt()){
                        tv.text = "${tv.text}\n${array.getJSONObject(i).getString("name")}"
                    }
                    et.text.clear()
                }
            }
        }
    }

    private fun fetchData(): String {
        var response = ""
        try {
            response = URL("https://dojo-recipes.herokuapp.com/people/").readText(Charsets.UTF_8)
        }catch (e: Exception){
            Log.e("TAG", "$e")
        }
        return response
    }

    // extension function to hide soft keyboard programmatically
    //https://android--code.blogspot.com/2020/08/android-kotlin-edittext-hide-keyboard.html
    fun Activity.hideSoftKeyboard(){
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}