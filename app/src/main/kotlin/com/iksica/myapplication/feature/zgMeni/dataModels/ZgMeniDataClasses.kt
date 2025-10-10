package com.iksica.myapplication.feature.zgMeni.dataModels

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuResponse(
    val id: Int,
    val date: String,
    val slug: String,
    val link: String,
    val title: RenderedText,
    val meta: Meta,
    @SerialName("author_meta") val authorMeta: AuthorMeta
)

@Serializable
data class RenderedText(
    val rendered: String
)

@Serializable
data class AuthorMeta(
    val ID: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String
)

@Serializable
data class Meta(
    @SerialName("menu_restaurant_id") val menuRestaurantId: Int,
    @SerialName("menu_date") val menuDate: String,
    @SerialName("menu_products") val menuProducts: MenuProducts
)

@Serializable
data class MenuProducts(
    val rucak: Meal? = null,
    val vecera: Meal? = null
)

@Serializable
data class Meal(
    val menu: List<MenuItem> = emptyList(),
    @SerialName("vege_menu") val vegeMenu: List<MenuItem> = emptyList(),
    val izbor: List<MenuItem> = emptyList(),
    val prilozi: List<MenuItem> = emptyList()
)

@Serializable
data class MenuItem(
    val id: Int,
    val title: String,
    val stock: String,
    val allergens: String,
    val weight: String,
    val price: String
)
