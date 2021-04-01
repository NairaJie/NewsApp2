package com.nailah.newsapp.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.nailah.newsapp.NewsAdapter
import com.nailah.newsapp.R
import com.nailah.newsapp.model.ResponseNews
import com.nailah.newsapp.service.RetrofitConfig
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    val date = getCurrentDateTime()

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time

    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    companion object {
        fun getLaunchService(from: Context) = Intent(from, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        ib_profile_main.setOnClickListener(this)
        tv_date_main.text = date.toString("dd/MM/yyyy")
        getNews()
    }

    private fun getNews() {
        var country = "id"
        var apiKey = "d4698c0887424ea091a5a0ca726b302a"

        var loading = ProgressDialog.show(this, "Request Data", "Loading...")
        RetrofitConfig.getInstance().getNewsData(country, apiKey).enqueue(
            object : Callback<ResponseNews> {
                override fun onResponse(
                    call: Call<ResponseNews>,
                    response: Response<ResponseNews>
                ) {
                    Log.d("Response", "Success" + response.body()?.articles)
                    loading.dismiss()
                    if (response.isSuccessful) {
                        val status = response.body()?.status
                        if (status.equals("ok")) {
                            Toast.makeText(this@MainActivity, "Data Success !", Toast.LENGTH_SHORT)
                                .show()
                            val newsData = response.body()?.articles
                            val newsDatImage = response.body()!!
                            Glide.with(this@MainActivity)
                                .load(newsDatImage.articles?.component5()?.urlToImage).centerCrop()
                                .into(iv_main)
                            tv_highlight.text =
                                newsDatImage.articles?.component5()?.title.toString()
                            tv_name_author.text =
                                newsDatImage.articles?.component5()?.author.toString()

                            val newsAdapter = NewsAdapter(this@MainActivity, newsData)
                            rv_main.adapter = newsAdapter
                            rv_main.layoutManager = LinearLayoutManager(this@MainActivity)

                        } else {
                            Toast.makeText(this@MainActivity, "Data Failed !", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseNews>, t: Throwable) {
                    Log.d("Response", "Failed : " + t.localizedMessage)
                    loading.dismiss()
                }
            }
        )

    }


    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.ib_profile_main -> startActivity(Intent(ProfileActivity.getLaunchService(this)))
        }
    }
}