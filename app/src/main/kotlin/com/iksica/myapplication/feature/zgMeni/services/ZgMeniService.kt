package com.iksica.myapplication.feature.zgMeni.services

import com.iksica.myapplication.feature.zgMeni.dataModels.MenuResponse
import com.iksica.myapplication.feature.zgMeni.dataModels.Post
import com.iksica.myapplication.feature.zgMeni.dataModels.Root
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.time.Clock
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ZgMeniService(private val client: OkHttpClient) {

    private val json = Json { ignoreUnknownKeys = true }

    fun fetchLocationData(): List<Post>? {
        val url = targetUrl
            .newBuilder()
            .addPathSegment("prehrana")
            .build()
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: ""
        if (!response.isSuccessful) return null
        response.close()

        val doc = Jsoup.parse(body)
        val jsonString = doc.select("script[type=application/json]").first()?.data() ?: ""
        var posts: List<Post>? = null
        try{
            posts = json.decodeFromString<Root>(jsonString).props.pageProps.posts.sortedBy { it.id }
        } catch (e: Exception){

        }
        return posts
    }

    fun fetchMenies(): List<MenuResponse>? {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val today = LocalDate.now().format(dateTimeFormatter)
        val currentInstant = Clock.systemUTC().instant().toEpochMilli()

        val httpUrl: HttpUrl = targetUrl
            .newBuilder()
            .addPathSegment("wp-json")
            .addPathSegment("wp")
            .addPathSegment("v2")
            .addPathSegment("menus")
            .addQueryParameter("menu_date", today)
            .addQueryParameter("timestamp", currentInstant.toString())
            .addQueryParameter("per_page", "100")
            .build()

        val request = Request.Builder()
            .url(httpUrl)
            .get()
            .build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: ""
        if (!response.isSuccessful) return null
        response.close()
        var menies: List<MenuResponse>? = null
        try {
            menies = json.decodeFromString<List<MenuResponse>>(body).map {
                it.copy(id = it.meta.menuRestaurantId)
            }
        } catch (e: Exception){

        }

        return menies?.sortedBy { it.id }
    }
    companion object {
        private const val SCHEME = "https"

        val targetUrl = HttpUrl.Builder()
            .scheme(SCHEME)
            .host("www.sczg.unizg.hr")
            .build()

    }
}