package com.britetodo.turbotrack.data.model

data class Airport(
    val icao: String,
    val iata: String,
    val name: String,
    val city: String,
    val country: String,
    val lat: Double,
    val lon: Double
) {
    val displayName: String get() = "$icao – $city"
    val fullName: String get() = "$name ($iata)"

    companion object {
        val ALL: List<Airport> = listOf(
            Airport("KJFK", "JFK", "John F. Kennedy International", "New York", "US", 40.6413, -73.7781),
            Airport("KLAX", "LAX", "Los Angeles International", "Los Angeles", "US", 33.9425, -118.4081),
            Airport("KORD", "ORD", "O'Hare International", "Chicago", "US", 41.9742, -87.9073),
            Airport("KATL", "ATL", "Hartsfield-Jackson Atlanta", "Atlanta", "US", 33.6407, -84.4277),
            Airport("KDFW", "DFW", "Dallas/Fort Worth International", "Dallas", "US", 32.8998, -97.0403),
            Airport("KSFO", "SFO", "San Francisco International", "San Francisco", "US", 37.6213, -122.3790),
            Airport("KMIA", "MIA", "Miami International", "Miami", "US", 25.7959, -80.2870),
            Airport("KDEN", "DEN", "Denver International", "Denver", "US", 39.8561, -104.6737),
            Airport("KBOS", "BOS", "Logan International", "Boston", "US", 42.3656, -71.0096),
            Airport("KSEA", "SEA", "Seattle-Tacoma International", "Seattle", "US", 47.4502, -122.3088),
            Airport("EGLL", "LHR", "Heathrow Airport", "London", "GB", 51.4775, -0.4614),
            Airport("EGKK", "LGW", "Gatwick Airport", "London", "GB", 51.1537, -0.1821),
            Airport("EDDM", "MUC", "Munich Airport", "Munich", "DE", 48.3537, 11.7861),
            Airport("EDDF", "FRA", "Frankfurt Airport", "Frankfurt", "DE", 50.0379, 8.5622),
            Airport("LFPG", "CDG", "Charles de Gaulle Airport", "Paris", "FR", 49.0097, 2.5478),
            Airport("LEMD", "MAD", "Adolfo Suárez Madrid–Barajas", "Madrid", "ES", 40.4936, -3.5668),
            Airport("LEBL", "BCN", "Barcelona El Prat", "Barcelona", "ES", 41.2971, 2.0785),
            Airport("LIRF", "FCO", "Leonardo da Vinci International", "Rome", "IT", 41.8003, 12.2389),
            Airport("EHAM", "AMS", "Amsterdam Airport Schiphol", "Amsterdam", "NL", 52.3086, 4.7639),
            Airport("ESSA", "ARN", "Stockholm Arlanda Airport", "Stockholm", "SE", 59.6519, 17.9186),
            Airport("RJTT", "HND", "Tokyo Haneda Airport", "Tokyo", "JP", 35.5533, 139.7811),
            Airport("RJAA", "NRT", "Narita International Airport", "Tokyo", "JP", 35.7647, 140.3864),
            Airport("RKSI", "ICN", "Incheon International Airport", "Seoul", "KR", 37.4691, 126.4505),
            Airport("ZBAA", "PEK", "Beijing Capital International", "Beijing", "CN", 40.0799, 116.6031),
            Airport("ZSPD", "PVG", "Shanghai Pudong International", "Shanghai", "CN", 31.1434, 121.8052),
            Airport("VHHH", "HKG", "Hong Kong International Airport", "Hong Kong", "HK", 22.3080, 113.9185),
            Airport("WSSS", "SIN", "Singapore Changi Airport", "Singapore", "SG", 1.3644, 103.9915),
            Airport("OMDB", "DXB", "Dubai International Airport", "Dubai", "AE", 25.2532, 55.3657),
            Airport("OERK", "RUH", "King Khalid International", "Riyadh", "SA", 24.9578, 46.6989),
            Airport("FACT", "CPT", "Cape Town International", "Cape Town", "ZA", -33.9649, 18.6017),
            Airport("HAAB", "ADD", "Bole International Airport", "Addis Ababa", "ET", 8.9779, 38.7993),
            Airport("YSSY", "SYD", "Sydney Kingsford Smith", "Sydney", "AU", -33.9399, 151.1753),
            Airport("YMML", "MEL", "Melbourne Airport", "Melbourne", "AU", -37.6690, 144.8410),
            Airport("SBGR", "GRU", "São Paulo/Guarulhos", "São Paulo", "BR", -23.4356, -46.4731),
            Airport("SBBE", "BEL", "Belém Val de Cans", "Belém", "BR", -1.3792, -48.4763),
            Airport("MMMX", "MEX", "Mexico City International", "Mexico City", "MX", 19.4363, -99.0721),
            Airport("CYYZ", "YYZ", "Toronto Pearson International", "Toronto", "CA", 43.6777, -79.6248),
            Airport("CYVR", "YVR", "Vancouver International", "Vancouver", "CA", 49.1947, -123.1792),
            Airport("UUEE", "SVO", "Sheremetyevo International", "Moscow", "RU", 55.9726, 37.4146),
            Airport("ULLI", "LED", "Pulkovo Airport", "Saint Petersburg", "RU", 59.8003, 30.2625),
            Airport("LTBA", "IST", "Istanbul Atatürk Airport", "Istanbul", "TR", 40.9769, 28.8146),
            Airport("VIDP", "DEL", "Indira Gandhi International", "New Delhi", "IN", 28.5665, 77.1031),
            Airport("VABB", "BOM", "Chhatrapati Shivaji Maharaj", "Mumbai", "IN", 19.0887, 72.8679),
            Airport("WNAW", "NAW", "Narathiwat Airport", "Narathiwat", "TH", 6.5192, 101.7427)
        )

        fun search(query: String): List<Airport> {
            if (query.isBlank()) return ALL.take(20)
            val q = query.uppercase().trim()
            return ALL.filter { airport ->
                airport.icao.contains(q) ||
                airport.iata.contains(q) ||
                airport.city.uppercase().contains(q) ||
                airport.name.uppercase().contains(q)
            }.take(20)
        }
    }
}
