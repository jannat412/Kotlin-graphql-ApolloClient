package com.example.graphqlkotlin

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory
import com.apollographql.apollo.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.example.graphqlkotlin.adapter.UserAdapter
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val BASE_URL = "http://192.168.0.249:4000/graphql"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Logger.addLogAdapter(AndroidLogAdapter())

        val sqlNormalizedCacheFactory = SqlNormalizedCacheFactory(this, "apollo.db")
        val cacheFactory = LruNormalizedCacheFactory(EvictionPolicy.builder().maxSizeBytes(10 * 1024 * 1024).build()).chain(sqlNormalizedCacheFactory)

        val httpClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addNetworkInterceptor(NetworkInterceptor())
                .build()
        }

        val apolloClient: ApolloClient by lazy {
            ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(httpClient)
                .normalizedCache(cacheFactory)
                .build()
        }

        apolloClient.query(FeedResultQuery.Builder().build()).responseFetcher(ApolloResponseFetchers.CACHE_AND_NETWORK)
            .enqueue(object: ApolloCall.Callback<FeedResultQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    Logger.d(e.localizedMessage ?: "Error")
                }

                override fun onResponse(response: Response<FeedResultQuery.Data>) {
                    Logger.d(response.data?.peoples().toString())
                    runOnUiThread {
                        response.data?.peoples()?.let { bindToRecycleview(it) }
                    }

                }
            })
    }

    private class NetworkInterceptor: Interceptor {

        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            return chain!!.proceed(chain.request().newBuilder().header("Authorization", "Bearer <TOKEN>").build())
        }
    }

    @SuppressLint("WrongConstant")
    fun bindToRecycleview(userList: MutableList<FeedResultQuery.People>)
    {
        val recyclerView = findViewById<RecyclerView>(R.id.rv_users_list)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        var recyclerViewAdapter = UserAdapter(userList)
        recyclerView.adapter = recyclerViewAdapter
        recyclerViewAdapter.notifyDataSetChanged()
    }
}