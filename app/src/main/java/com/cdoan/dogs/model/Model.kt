package com.cdoan.dogs.model

import com.google.gson.annotations.SerializedName

data class DogBreed(
    @SerializedName("id")
    val breedId: String?,

    @SerializedName("name")
    val dogBreed: String?,

    @SerializedName("life_span")
    val lifespan: String?,

    @SerializedName("breed_group")
    val breedGroup: String? = null,

    @SerializedName("bred_for")
    val bredFor: String? = null,

    val temperament: String? = null,

    @SerializedName("url")
    val imageUrl: String? = null
)