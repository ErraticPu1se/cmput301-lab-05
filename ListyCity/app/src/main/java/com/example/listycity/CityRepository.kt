package com.example.listycity

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CityRepository {
    private val db = Firebase.firestore
    private val citiesRef = db.collection("cities")
    private val _cities = mutableStateListOf<City>()

    val cities: List<City>
        get() = _cities

    init {
        citiesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            _cities.clear()
            snapshot?.documents?.forEach { document ->
                val city = document.toObject(City::class.java)
                if (city != null) {
                    _cities.add(city)
                }
            }
        }
    }

    fun addCity(city: City) {
        citiesRef.document(city.name).set(city)
    }

    fun updateCity(oldCity: City, updatedCity: City) {
        val oldDocument = citiesRef.document(oldCity.name)
        val updatedDocument = citiesRef.document(updatedCity.name)

        db.runBatch { batch ->
            if (oldCity.name != updatedCity.name) {
                batch.delete(oldDocument)
            }
            batch.set(updatedDocument, updatedCity)
        }
    }

    fun deleteCity(city: City) {
        citiesRef.document(city.name).delete()
    }
}
