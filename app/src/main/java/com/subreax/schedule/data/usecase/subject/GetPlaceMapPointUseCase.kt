package com.subreax.schedule.data.usecase.subject

sealed class MapPoint {
    class Address(val address: String) : MapPoint()
    class Coordinates(val lat: Double, val lon: Double) : MapPoint()
}

class GetPlaceMapPointUseCase {
    operator fun invoke(place: String): MapPoint? {
        val split = place.split('-')
        return if (split.size >= 2) {
            when (split[0]) {
                "Гл." -> MapPoint.Address("Тула, проспект Ленина, 92")
                "1" -> MapPoint.Address("Тула, проспект Ленина, 95")
                "2" -> MapPoint.Address("Тула, проспект Ленина, 84")
                "3" -> MapPoint.Address("Тула, проспект Ленина, 84к8")
                "4" -> MapPoint.Address("Тула, проспект Ленина, 84к7")
                "5" -> MapPoint.Address("Тула, улица Фридриха Энгельса, 155")
                "6" -> MapPoint.Address("Тула, проспект Ленина, 90к1")
                "6лаб" -> MapPoint.Address("Тула, Смидович, д. 3А")
                "7" -> MapPoint.Address("Тула, проспект Ленина, 93А")
                "8" -> MapPoint.Address("Тула, улица Болдина, 153")
                "9" -> MapPoint.Address("Тула, проспект Ленина, 92")
                "10" -> MapPoint.Address("Тула, улица Болдина, 128")
                "11" -> MapPoint.Address("Тула, улица Болдина, 151")
                "12" -> MapPoint.Address("Тула, улица Агеева, 1Б")
                "13" -> MapPoint.Coordinates(54.172327,37.596777)
                else -> null
            }
        } else {
            null
        }
    }
}
