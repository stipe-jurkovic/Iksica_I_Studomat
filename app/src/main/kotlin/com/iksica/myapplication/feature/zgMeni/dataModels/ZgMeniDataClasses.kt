package com.iksica.myapplication.feature.zgMeni.dataModels

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject

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
    @Serializable(with = MenuProductsSerializer::class)
    @SerialName("menu_products") val menuProducts: MenuProducts?
)

object MenuProductsSerializer : KSerializer<MenuProducts?> {
    override val descriptor: SerialDescriptor =
        ListSerializer(MenuProducts.serializer()).descriptor

    override fun serialize(
        encoder: Encoder,
        value: MenuProducts?
    ) {
        value?.let{ encoder.encodeSerializableValue(MenuProducts.serializer(), it) }
    }

    override fun deserialize(decoder: Decoder): MenuProducts? {
        val input = decoder as? JsonDecoder ?: error("Expected JsonDecoder")
        val element = input.decodeJsonElement()
        return when (element) {
            is JsonObject -> {
                Json.decodeFromJsonElement(MenuProducts.serializer(), element)
            }
            else -> null
        }
    }
}

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
