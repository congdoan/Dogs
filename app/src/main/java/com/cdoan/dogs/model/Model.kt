package com.cdoan.dogs.model

data class DogBreed(
    val breedId: String?,
    val dogBreed: String?,
    val lifespan: String?,
    val breedGroup: String? = null,
    val bredFor: String? = null,
    val temperament: String? = null,
    val imageUrl: String? = null
)