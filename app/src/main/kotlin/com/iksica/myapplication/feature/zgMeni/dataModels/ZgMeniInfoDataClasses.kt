package com.iksica.myapplication.feature.zgMeni.dataModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Title(
    val rendered: String
)


@Serializable
data class RestaurantInfoItem(
    val icon: String,
    val title: String,
    val order: Int
)

@Serializable
data class Meta1(
    @SerialName("_et_pb_use_builder")
    val ponuda: String? = null,
    val radno_vrijeme: String? = null,
    val restaurant_info: List<RestaurantInfoItem>? = null,
    val linije: List<String>? = null
)

@Serializable
data class Post(
    val id: Int,
    val date: String,
    val modified: String,
    val slug: String,
    val status: String,
    val link: String,
    val title: Title,
    val meta: Meta1,
    val image_url: String
)

@Serializable
data class PageProps(
    val posts: List<Post>
)

@Serializable
data class Props(
    val pageProps: PageProps
)

@Serializable
data class Root(
    val props: Props
)