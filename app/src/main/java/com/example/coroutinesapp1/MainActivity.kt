package com.example.coroutinesapp1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var textAdvice:TextView
    lateinit var btnAdvice:Button

    val apiURL="https://api.adviceslip.com/advice"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textAdvice=findViewById(R.id.textView)
        btnAdvice=findViewById(R.id.btnAdvice)

        btnAdvice.setOnClickListener(){
            requestAPI()
        }

    }

    fun requestAPI(){

        //step3: Start coroutines by creating its builder
        //write time consuming code inside it
        CoroutineScope(Dispatchers.IO).launch {

            /*async job is: Creates a coroutine and returns its future result
            as an implementation of Deferred. Deferred value is a non-blocking cancellable future */
            val data= async {
                fetchAdvice()
            }.await()
            /* .await() job is:
            Awaits for completion of this value without blocking a thread
            and resumes when deferred computation is complete,
            returning the resulting value or throwing the exception if the deferred was cancelled */

            if (data.isNotEmpty()){
                displayAdvice(data)
            }

        }//end CoroutineScope

    }//end requestAPI()

    fun fetchAdvice():String{
        var response=""
        try {
            response=URL(apiURL).readText(Charsets.UTF_8)
        }catch (e:Exception){
            println("Error $e")
        }
        return response
    }//end fetchAdvice()

    //step : create fun that used and controlled by coroutines to get result
    //suspend defines that this fun can stopped/resumed by the Coroutines
    //and it can only get called inside the Coroutines scope.
    suspend fun displayAdvice(data:String){
        /* in this fun we should use withContext
        because we will access another view within Coroutines
        (this fun can only get called inside CoroutineScope) */
        withContext(Dispatchers.Main){

            val slip= JSONObject(data).getJSONObject("slip")
            val id=slip.getInt("id")
            val advice=slip.getString("advice")
            textAdvice.text=advice

        }

    }//end displayAdvice()


}//end class()