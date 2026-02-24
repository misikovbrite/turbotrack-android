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

            // ── USA (40) ──────────────────────────────────────────────────────
            Airport("KJFK", "JFK", "John F. Kennedy International", "New York", "US", 40.6413, -73.7781),
            Airport("KLAX", "LAX", "Los Angeles International", "Los Angeles", "US", 33.9425, -118.4081),
            Airport("KORD", "ORD", "O'Hare International", "Chicago", "US", 41.9742, -87.9073),
            Airport("KATL", "ATL", "Hartsfield-Jackson Atlanta International", "Atlanta", "US", 33.6407, -84.4277),
            Airport("KDFW", "DFW", "Dallas/Fort Worth International", "Dallas", "US", 32.8998, -97.0403),
            Airport("KDEN", "DEN", "Denver International", "Denver", "US", 39.8561, -104.6737),
            Airport("KSFO", "SFO", "San Francisco International", "San Francisco", "US", 37.6213, -122.3790),
            Airport("KLAS", "LAS", "Harry Reid International", "Las Vegas", "US", 36.0840, -115.1537),
            Airport("KSEA", "SEA", "Seattle-Tacoma International", "Seattle", "US", 47.4502, -122.3088),
            Airport("KPHX", "PHX", "Phoenix Sky Harbor International", "Phoenix", "US", 33.4373, -112.0078),
            Airport("KMIA", "MIA", "Miami International", "Miami", "US", 25.7959, -80.2870),
            Airport("KEWR", "EWR", "Newark Liberty International", "Newark", "US", 40.6895, -74.1745),
            Airport("KBOS", "BOS", "Logan International", "Boston", "US", 42.3656, -71.0096),
            Airport("KDTW", "DTW", "Detroit Metropolitan Wayne County", "Detroit", "US", 42.2124, -83.3534),
            Airport("KMSN", "MSN", "Dane County Regional", "Madison", "US", 43.1399, -89.3375),
            Airport("KIAH", "IAH", "George Bush Intercontinental", "Houston", "US", 29.9902, -95.3368),
            Airport("KMSP", "MSP", "Minneapolis-Saint Paul International", "Minneapolis", "US", 44.8820, -93.2218),
            Airport("KFLL", "FLL", "Fort Lauderdale-Hollywood International", "Fort Lauderdale", "US", 26.0726, -80.1527),
            Airport("KBWI", "BWI", "Baltimore/Washington International", "Baltimore", "US", 39.1754, -76.6683),
            Airport("KSTL", "STL", "St. Louis Lambert International", "St. Louis", "US", 38.7487, -90.3700),
            Airport("KTPA", "TPA", "Tampa International", "Tampa", "US", 27.9755, -82.5332),
            Airport("KSAN", "SAN", "San Diego International", "San Diego", "US", 32.7336, -117.1897),
            Airport("KAUS", "AUS", "Austin-Bergstrom International", "Austin", "US", 30.1975, -97.6664),
            Airport("KPDX", "PDX", "Portland International", "Portland", "US", 45.5887, -122.5975),
            Airport("KSLC", "SLC", "Salt Lake City International", "Salt Lake City", "US", 40.7884, -111.9778),
            Airport("KPIT", "PIT", "Pittsburgh International", "Pittsburgh", "US", 40.4915, -80.2329),
            Airport("KCLT", "CLT", "Charlotte Douglas International", "Charlotte", "US", 35.2140, -80.9431),
            Airport("KMCO", "MCO", "Orlando International", "Orlando", "US", 28.4294, -81.3089),
            Airport("KDAL", "DAL", "Dallas Love Field", "Dallas", "US", 32.8471, -96.8518),
            Airport("KHOU", "HOU", "William P. Hobby Airport", "Houston", "US", 29.6454, -95.2789),
            Airport("KABQ", "ABQ", "Albuquerque International Sunport", "Albuquerque", "US", 35.0402, -106.6091),
            Airport("KMEM", "MEM", "Memphis International", "Memphis", "US", 35.0424, -89.9767),
            Airport("KBUF", "BUF", "Buffalo Niagara International", "Buffalo", "US", 42.9405, -78.7322),
            Airport("KRDU", "RDU", "Raleigh-Durham International", "Raleigh", "US", 35.8776, -78.7875),
            Airport("KBNA", "BNA", "Nashville International", "Nashville", "US", 36.1245, -86.6782),
            Airport("KJAX", "JAX", "Jacksonville International", "Jacksonville", "US", 30.4941, -81.6879),
            Airport("KRSW", "RSW", "Southwest Florida International", "Fort Myers", "US", 26.5362, -81.7552),
            Airport("KMKE", "MKE", "Milwaukee Mitchell International", "Milwaukee", "US", 42.9472, -87.8966),
            Airport("KIND", "IND", "Indianapolis International", "Indianapolis", "US", 39.7173, -86.2944),
            Airport("KCMH", "CMH", "John Glenn Columbus International", "Columbus", "US", 39.9980, -82.8919),

            // ── Canada (10) ───────────────────────────────────────────────────
            Airport("CYYZ", "YYZ", "Toronto Pearson International", "Toronto", "CA", 43.6777, -79.6248),
            Airport("CYVR", "YVR", "Vancouver International", "Vancouver", "CA", 49.1947, -123.1792),
            Airport("CYMX", "YMX", "Montréal-Mirabel International", "Montreal", "CA", 45.6795, -74.0387),
            Airport("CYUL", "YUL", "Montréal-Trudeau International", "Montreal", "CA", 45.4706, -73.7408),
            Airport("CYOW", "YOW", "Ottawa Macdonald-Cartier International", "Ottawa", "CA", 45.3225, -75.6692),
            Airport("CYEG", "YEG", "Edmonton International", "Edmonton", "CA", 53.3097, -113.5797),
            Airport("CYYC", "YYC", "Calgary International", "Calgary", "CA", 51.1313, -114.0100),
            Airport("CYWG", "YWG", "Winnipeg James Armstrong Richardson", "Winnipeg", "CA", 49.9100, -97.2399),
            Airport("CYQR", "YQR", "Regina International Airport", "Regina", "CA", 50.4320, -104.6659),
            Airport("CYFC", "YFC", "Fredericton International Airport", "Fredericton", "CA", 45.8689, -66.5372),

            // ── Mexico / Caribbean (10) ───────────────────────────────────────
            Airport("MMMX", "MEX", "Mexico City International", "Mexico City", "MX", 19.4363, -99.0721),
            Airport("MMCUN", "CUN", "Cancún International", "Cancún", "MX", 21.0365, -86.8771),
            Airport("MMGL", "GDL", "Miguel Hidalgo y Costilla International", "Guadalajara", "MX", 20.5218, -103.3107),
            Airport("MMMTY", "MTY", "General Mariano Escobedo International", "Monterrey", "MX", 25.7749, -100.1069),
            Airport("MMTJ", "TIJ", "General Abelardo L. Rodríguez International", "Tijuana", "MX", 32.5411, -116.9700),
            Airport("MMMY", "MID", "Manuel Crescencio Rejón International", "Mérida", "MX", 20.9370, -89.6577),
            Airport("MROC", "SJO", "Juan Santamaría International", "San José", "CR", 9.9939, -84.2088),
            Airport("MDSD", "SDQ", "Las Américas International", "Santo Domingo", "DO", 18.4297, -69.6689),
            Airport("MKJP", "KIN", "Norman Manley International Airport", "Kingston", "JM", 17.9357, -76.7875),
            Airport("TNCA", "AUA", "Queen Beatrix International Airport", "Oranjestad", "AW", 12.5014, -70.0152),

            // ── South America (12) ────────────────────────────────────────────
            Airport("SBGR", "GRU", "São Paulo/Guarulhos International", "São Paulo", "BR", -23.4356, -46.4731),
            Airport("SBBR", "BSB", "Presidente Juscelino Kubitschek International", "Brasília", "BR", -15.8711, -47.9186),
            Airport("SBGL", "GIG", "Rio de Janeiro/Galeão International", "Rio de Janeiro", "BR", -22.8099, -43.2505),
            Airport("SCEL", "SCL", "Arturo Merino Benítez International", "Santiago", "CL", -33.3930, -70.7858),
            Airport("SAEZ", "EZE", "Ministro Pistarini International", "Buenos Aires", "AR", -34.8222, -58.5358),
            Airport("SKBO", "BOG", "El Dorado International", "Bogotá", "CO", 4.7016, -74.1469),
            Airport("SEQM", "UIO", "Mariscal Sucre International", "Quito", "EC", -0.1292, -78.3575),
            Airport("SPJC", "LIM", "Jorge Chávez International", "Lima", "PE", -12.0219, -77.1143),
            Airport("SUAA", "MVD", "Carrasco International", "Montevideo", "UY", -34.8384, -56.0308),
            Airport("SVMI", "CCS", "Simón Bolívar International", "Caracas", "VE", 10.6012, -66.9913),
            Airport("SBPA", "POA", "Salgado Filho International Airport", "Porto Alegre", "BR", -29.9944, -51.1713),
            Airport("SPZO", "CUZ", "Alejandro Velasco Astete International", "Cusco", "PE", -13.5357, -71.9387),

            // ── UK / Ireland (10) ─────────────────────────────────────────────
            Airport("EGLL", "LHR", "Heathrow Airport", "London", "GB", 51.4775, -0.4614),
            Airport("EGKK", "LGW", "Gatwick Airport", "London", "GB", 51.1537, -0.1821),
            Airport("EGGW", "LTN", "London Luton Airport", "London", "GB", 51.8747, -0.3683),
            Airport("EGSS", "STN", "London Stansted Airport", "London", "GB", 51.8850, 0.2350),
            Airport("EGLC", "LCY", "London City Airport", "London", "GB", 51.5053, 0.0553),
            Airport("EGCC", "MAN", "Manchester Airport", "Manchester", "GB", 53.3537, -2.2750),
            Airport("EGPH", "EDI", "Edinburgh Airport", "Edinburgh", "GB", 55.9500, -3.3725),
            Airport("EGPF", "GLA", "Glasgow Airport", "Glasgow", "GB", 55.8719, -4.4331),
            Airport("EGBB", "BHX", "Birmingham Airport", "Birmingham", "GB", 52.4539, -1.7480),
            Airport("EIDW", "DUB", "Dublin Airport", "Dublin", "IE", 53.4213, -6.2700),

            // ── France (8) ────────────────────────────────────────────────────
            Airport("LFPG", "CDG", "Charles de Gaulle Airport", "Paris", "FR", 49.0097, 2.5478),
            Airport("LFPO", "ORY", "Paris Orly Airport", "Paris", "FR", 48.7233, 2.3794),
            Airport("LFMN", "NCE", "Nice Côte d'Azur Airport", "Nice", "FR", 43.6584, 7.2158),
            Airport("LFLL", "LYS", "Lyon-Saint-Exupéry Airport", "Lyon", "FR", 45.7256, 5.0811),
            Airport("LFBD", "BOD", "Bordeaux-Mérignac Airport", "Bordeaux", "FR", 44.8283, -0.7156),
            Airport("LFRS", "NTE", "Nantes Atlantique Airport", "Nantes", "FR", 47.1532, -1.6111),
            Airport("LFML", "MRS", "Marseille Provence Airport", "Marseille", "FR", 43.4393, 5.2214),
            Airport("LFTW", "FNI", "Nîmes-Alès-Camargue-Cévennes Airport", "Nîmes", "FR", 43.7574, 4.4163),

            // ── Germany (8) ───────────────────────────────────────────────────
            Airport("EDDF", "FRA", "Frankfurt Airport", "Frankfurt", "DE", 50.0379, 8.5622),
            Airport("EDDM", "MUC", "Munich Airport", "Munich", "DE", 48.3537, 11.7861),
            Airport("EDDB", "BER", "Berlin Brandenburg Airport", "Berlin", "DE", 52.3667, 13.5033),
            Airport("EDDH", "HAM", "Hamburg Airport", "Hamburg", "DE", 53.6304, 9.9882),
            Airport("EDDK", "CGN", "Cologne Bonn Airport", "Cologne", "DE", 50.8659, 7.1427),
            Airport("EDDS", "STR", "Stuttgart Airport", "Stuttgart", "DE", 48.6900, 9.2216),
            Airport("EDDL", "DUS", "Düsseldorf Airport", "Düsseldorf", "DE", 51.2895, 6.7668),
            Airport("EDDN", "NUE", "Nuremberg Airport", "Nuremberg", "DE", 49.4987, 11.0669),

            // ── Spain (8) ─────────────────────────────────────────────────────
            Airport("LEMD", "MAD", "Adolfo Suárez Madrid-Barajas Airport", "Madrid", "ES", 40.4936, -3.5668),
            Airport("LEBL", "BCN", "Barcelona El Prat Airport", "Barcelona", "ES", 41.2971, 2.0785),
            Airport("LEMG", "AGP", "Málaga-Costa del Sol Airport", "Málaga", "ES", 36.6749, -4.4991),
            Airport("LEAL", "ALC", "Alicante-Elche Airport", "Alicante", "ES", 38.2822, -0.5582),
            Airport("GCFV", "FUE", "Fuerteventura Airport", "Fuerteventura", "ES", 28.4527, -13.8638),
            Airport("GCLP", "LPA", "Gran Canaria Airport", "Las Palmas", "ES", 27.9319, -15.3866),
            Airport("LEPA", "PMI", "Palma de Mallorca Airport", "Palma", "ES", 39.5517, 2.7388),
            Airport("LEZL", "SVQ", "Seville Airport", "Seville", "ES", 37.4180, -5.8931),

            // ── Italy (8) ─────────────────────────────────────────────────────
            Airport("LIRF", "FCO", "Leonardo da Vinci International", "Rome", "IT", 41.8003, 12.2389),
            Airport("LIMC", "MXP", "Milan Malpensa Airport", "Milan", "IT", 45.6306, 8.7281),
            Airport("LIML", "LIN", "Milan Linate Airport", "Milan", "IT", 45.4453, 9.2767),
            Airport("LIPZ", "VCE", "Venice Marco Polo Airport", "Venice", "IT", 45.5053, 12.3519),
            Airport("LIRN", "NAP", "Naples International Airport", "Naples", "IT", 40.8860, 14.2908),
            Airport("LICC", "CTA", "Catania-Fontanarossa Airport", "Catania", "IT", 37.4668, 15.0664),
            Airport("LIPB", "BZO", "Bolzano Airport", "Bolzano", "IT", 46.4603, 11.3264),
            Airport("LIBP", "PSR", "Pescara International Airport", "Pescara", "IT", 42.4317, 14.1811),

            // ── Benelux (6) ───────────────────────────────────────────────────
            Airport("EHAM", "AMS", "Amsterdam Airport Schiphol", "Amsterdam", "NL", 52.3086, 4.7639),
            Airport("EBBR", "BRU", "Brussels Airport", "Brussels", "BE", 50.9014, 4.4844),
            Airport("EBCI", "CRL", "Brussels South Charleroi Airport", "Charleroi", "BE", 50.4592, 4.4538),
            Airport("ELLX", "LUX", "Luxembourg Airport", "Luxembourg", "LU", 49.6233, 6.2044),
            Airport("EHRD", "RTM", "Rotterdam The Hague Airport", "Rotterdam", "NL", 51.9569, 4.4372),
            Airport("EHEH", "EIN", "Eindhoven Airport", "Eindhoven", "NL", 51.4501, 5.3746),

            // ── Switzerland / Austria (5) ─────────────────────────────────────
            Airport("LSZH", "ZRH", "Zurich Airport", "Zurich", "CH", 47.4647, 8.5492),
            Airport("LOWW", "VIE", "Vienna International Airport", "Vienna", "AT", 48.1103, 16.5697),
            Airport("LSGG", "GVA", "Geneva Airport", "Geneva", "CH", 46.2380, 6.1089),
            Airport("LOWI", "INN", "Innsbruck Airport", "Innsbruck", "AT", 47.2602, 11.3440),
            Airport("LSZB", "BRN", "Bern Airport", "Bern", "CH", 46.9141, 7.4971),

            // ── Scandinavia (9) ───────────────────────────────────────────────
            Airport("EKCH", "CPH", "Copenhagen Airport", "Copenhagen", "DK", 55.6180, 12.6561),
            Airport("ESSA", "ARN", "Stockholm Arlanda Airport", "Stockholm", "SE", 59.6519, 17.9186),
            Airport("ESGG", "GOT", "Gothenburg Landvetter Airport", "Gothenburg", "SE", 57.6628, 12.2798),
            Airport("ENGM", "OSL", "Oslo Gardermoen Airport", "Oslo", "NO", 60.1939, 11.1004),
            Airport("ENZV", "SVG", "Stavanger Airport Sola", "Stavanger", "NO", 58.8768, 5.6378),
            Airport("EFHK", "HEL", "Helsinki-Vantaa Airport", "Helsinki", "FI", 60.3172, 24.9633),
            Airport("BIKF", "KEF", "Keflavík International Airport", "Reykjavík", "IS", 63.9850, -22.6056),
            Airport("BGGH", "GOH", "Nuuk Airport", "Nuuk", "GL", 64.1909, -51.6781),
            Airport("EKBI", "BLL", "Billund Airport", "Billund", "DK", 55.7403, 9.1518),

            // ── Eastern Europe (10) ───────────────────────────────────────────
            Airport("EPWA", "WAW", "Warsaw Chopin Airport", "Warsaw", "PL", 52.1657, 20.9671),
            Airport("EPKK", "KRK", "Kraków John Paul II International", "Kraków", "PL", 50.0778, 19.7848),
            Airport("LKPR", "PRG", "Václav Havel Airport Prague", "Prague", "CZ", 50.1008, 14.2600),
            Airport("LHBP", "BUD", "Budapest Ferenc Liszt International", "Budapest", "HU", 47.4298, 19.2611),
            Airport("LROP", "OTP", "Henri Coandă International", "Bucharest", "RO", 44.5711, 26.0850),
            Airport("UKBB", "KBP", "Boryspil International Airport", "Kyiv", "UA", 50.3450, 30.8947),
            Airport("LJLJ", "LJU", "Ljubljana Jože Pučnik Airport", "Ljubljana", "SI", 46.2237, 14.4576),
            Airport("LZIB", "BTS", "Bratislava Airport", "Bratislava", "SK", 48.1702, 17.2127),
            Airport("LDZA", "ZAG", "Zagreb Airport", "Zagreb", "HR", 45.7429, 16.0688),
            Airport("LWSK", "SKP", "Skopje International Airport", "Skopje", "MK", 41.9616, 21.6214),

            // ── Portugal / Greece / Cyprus / Israel (8) ───────────────────────
            Airport("LPPT", "LIS", "Lisbon Humberto Delgado Airport", "Lisbon", "PT", 38.7813, -9.1359),
            Airport("LPPR", "OPO", "Francisco de Sá Carneiro Airport", "Porto", "PT", 41.2481, -8.6814),
            Airport("LGAV", "ATH", "Athens Eleftherios Venizelos International", "Athens", "GR", 37.9364, 23.9445),
            Airport("LGTS", "SKG", "Thessaloniki Macedonia International", "Thessaloniki", "GR", 40.5197, 22.9709),
            Airport("LCLK", "LCA", "Larnaca International Airport", "Larnaca", "CY", 34.8751, 33.6249),
            Airport("LCPH", "PFO", "Paphos International Airport", "Paphos", "CY", 34.7180, 32.4857),
            Airport("LLBG", "TLV", "Ben Gurion International Airport", "Tel Aviv", "IL", 32.0114, 34.8867),
            Airport("LLHA", "HFA", "Haifa Airport", "Haifa", "IL", 32.8094, 35.0431),

            // ── Turkey (5) ────────────────────────────────────────────────────
            Airport("LTFM", "IST", "Istanbul Airport", "Istanbul", "TR", 41.2753, 28.7519),
            Airport("LTAI", "AYT", "Antalya Airport", "Antalya", "TR", 36.8987, 30.7992),
            Airport("LTBA", "SAW", "Sabiha Gökçen International Airport", "Istanbul", "TR", 40.8986, 29.3092),
            Airport("LTAC", "ESB", "Ankara Esenboğa Airport", "Ankara", "TR", 40.1281, 32.9951),
            Airport("LTBJ", "ADB", "Adnan Menderes Airport", "Izmir", "TR", 38.2924, 27.1570),

            // ── Russia / CIS (6) ──────────────────────────────────────────────
            Airport("UUEE", "SVO", "Sheremetyevo International Airport", "Moscow", "RU", 55.9726, 37.4146),
            Airport("UUDD", "DME", "Domodedovo International Airport", "Moscow", "RU", 55.4088, 37.9063),
            Airport("UUWW", "VKO", "Vnukovo International Airport", "Moscow", "RU", 55.5915, 37.2615),
            Airport("ULLI", "LED", "Pulkovo Airport", "Saint Petersburg", "RU", 59.8003, 30.2625),
            Airport("URSS", "AER", "Sochi International Airport", "Sochi", "RU", 43.4499, 39.9566),
            Airport("UACC", "NQZ", "Nursultan Nazarbayev International", "Nur-Sultan", "KZ", 51.0223, 71.4669),

            // ── Middle East (10) ──────────────────────────────────────────────
            Airport("OMDB", "DXB", "Dubai International Airport", "Dubai", "AE", 25.2532, 55.3657),
            Airport("OMDW", "DWC", "Al Maktoum International Airport", "Dubai", "AE", 24.8963, 55.1614),
            Airport("OERK", "RUH", "King Khalid International Airport", "Riyadh", "SA", 24.9578, 46.6989),
            Airport("OEJN", "JED", "King Abdulaziz International Airport", "Jeddah", "SA", 21.6796, 39.1565),
            Airport("OTHH", "DOH", "Hamad International Airport", "Doha", "QA", 25.2731, 51.6081),
            Airport("OMAA", "AUH", "Abu Dhabi International Airport", "Abu Dhabi", "AE", 24.4330, 54.6511),
            Airport("OLBA", "BEY", "Beirut Rafic Hariri International", "Beirut", "LB", 33.8209, 35.4884),
            Airport("OIIE", "IKA", "Imam Khomeini International Airport", "Tehran", "IR", 35.4161, 51.1522),
            Airport("OKBK", "KWI", "Kuwait International Airport", "Kuwait City", "KW", 29.2267, 47.9689),
            Airport("OEDF", "DMM", "King Fahd International Airport", "Dammam", "SA", 26.4712, 49.7979),

            // ── East Asia (12) ────────────────────────────────────────────────
            Airport("RJTT", "HND", "Tokyo Haneda Airport", "Tokyo", "JP", 35.5533, 139.7811),
            Airport("RJAA", "NRT", "Narita International Airport", "Tokyo", "JP", 35.7647, 140.3864),
            Airport("RKSI", "ICN", "Incheon International Airport", "Seoul", "KR", 37.4691, 126.4505),
            Airport("RKSS", "GMP", "Gimpo International Airport", "Seoul", "KR", 37.5583, 126.7906),
            Airport("ZBAA", "PEK", "Beijing Capital International Airport", "Beijing", "CN", 40.0799, 116.6031),
            Airport("ZBAD", "PKX", "Beijing Daxing International Airport", "Beijing", "CN", 39.5097, 116.4105),
            Airport("ZSPD", "PVG", "Shanghai Pudong International Airport", "Shanghai", "CN", 31.1434, 121.8052),
            Airport("ZSSS", "SHA", "Shanghai Hongqiao International Airport", "Shanghai", "CN", 31.1979, 121.3363),
            Airport("VHHH", "HKG", "Hong Kong International Airport", "Hong Kong", "HK", 22.3080, 113.9185),
            Airport("RCTP", "TPE", "Taiwan Taoyuan International Airport", "Taipei", "TW", 25.0777, 121.2328),
            Airport("RJBB", "KIX", "Kansai International Airport", "Osaka", "JP", 34.4272, 135.2440),
            Airport("RJOO", "ITM", "Osaka Itami Airport", "Osaka", "JP", 34.7855, 135.4380),

            // ── Southeast Asia (9) ────────────────────────────────────────────
            Airport("WSSS", "SIN", "Singapore Changi Airport", "Singapore", "SG", 1.3644, 103.9915),
            Airport("VTBS", "BKK", "Suvarnabhumi Airport", "Bangkok", "TH", 13.6811, 100.7475),
            Airport("VTBD", "DMK", "Don Mueang International Airport", "Bangkok", "TH", 13.9126, 100.6067),
            Airport("WMKK", "KUL", "Kuala Lumpur International Airport", "Kuala Lumpur", "MY", 2.7456, 101.7099),
            Airport("RPLL", "MNL", "Ninoy Aquino International Airport", "Manila", "PH", 14.5086, 121.0197),
            Airport("WIII", "CGK", "Soekarno-Hatta International Airport", "Jakarta", "ID", -6.1256, 106.6559),
            Airport("VVTS", "SGN", "Tan Son Nhat International Airport", "Ho Chi Minh City", "VN", 10.8188, 106.6520),
            Airport("VVNB", "HAN", "Noi Bai International Airport", "Hanoi", "VN", 21.2212, 105.8072),
            Airport("VNKT", "KTM", "Tribhuvan International Airport", "Kathmandu", "NP", 27.6966, 85.3591),

            // ── South Asia (8) ────────────────────────────────────────────────
            Airport("VIDP", "DEL", "Indira Gandhi International Airport", "New Delhi", "IN", 28.5665, 77.1031),
            Airport("VABB", "BOM", "Chhatrapati Shivaji Maharaj International", "Mumbai", "IN", 19.0887, 72.8679),
            Airport("VOMM", "MAA", "Chennai International Airport", "Chennai", "IN", 12.9900, 80.1693),
            Airport("VOBL", "BLR", "Kempegowda International Airport", "Bengaluru", "IN", 13.1979, 77.7063),
            Airport("VECC", "CCU", "Netaji Subhas Chandra Bose International", "Kolkata", "IN", 22.6547, 88.4467),
            Airport("VCBI", "CMB", "Bandaranaike International Airport", "Colombo", "LK", 7.1808, 79.8841),
            Airport("VGHS", "DAC", "Hazrat Shahjalal International Airport", "Dhaka", "BD", 23.8433, 90.3978),
            Airport("OPKC", "KHI", "Jinnah International Airport", "Karachi", "PK", 24.9065, 67.1608),

            // ── Oceania (8) ───────────────────────────────────────────────────
            Airport("YSSY", "SYD", "Sydney Kingsford Smith Airport", "Sydney", "AU", -33.9399, 151.1753),
            Airport("YMML", "MEL", "Melbourne Airport", "Melbourne", "AU", -37.6690, 144.8410),
            Airport("YBBN", "BNE", "Brisbane Airport", "Brisbane", "AU", -27.3842, 153.1175),
            Airport("YPER", "PER", "Perth Airport", "Perth", "AU", -31.9403, 115.9669),
            Airport("YAPH", "ADL", "Adelaide Airport", "Adelaide", "AU", -34.9450, 138.5308),
            Airport("NZAA", "AKL", "Auckland Airport", "Auckland", "NZ", -37.0082, 174.7850),
            Airport("NZCH", "CHC", "Christchurch Airport", "Christchurch", "NZ", -43.4894, 172.5322),
            Airport("NZWN", "WLG", "Wellington International Airport", "Wellington", "NZ", -41.3272, 174.8052),

            // ── Africa (12) ───────────────────────────────────────────────────
            Airport("FACT", "CPT", "Cape Town International Airport", "Cape Town", "ZA", -33.9649, 18.6017),
            Airport("FAOR", "JNB", "O.R. Tambo International Airport", "Johannesburg", "ZA", -26.1392, 28.2460),
            Airport("HECA", "CAI", "Cairo International Airport", "Cairo", "EG", 30.1219, 31.4056),
            Airport("DNMM", "LOS", "Murtala Muhammed International Airport", "Lagos", "NG", 6.5774, 3.3212),
            Airport("HAAB", "ADD", "Bole International Airport", "Addis Ababa", "ET", 8.9779, 38.7993),
            Airport("DTTA", "TUN", "Tunis-Carthage International Airport", "Tunis", "TN", 36.8510, 10.2272),
            Airport("GMMN", "CMN", "Mohammed V International Airport", "Casablanca", "MA", 33.3675, -7.5900),
            Airport("FMMI", "TNR", "Ivato International Airport", "Antananarivo", "MG", -18.7969, 47.4788),
            Airport("HTDA", "DAR", "Julius Nyerere International Airport", "Dar es Salaam", "TZ", -6.8781, 39.2026),
            Airport("HKNA", "NBO", "Jomo Kenyatta International Airport", "Nairobi", "KE", -1.3192, 36.9275),
            Airport("FGSL", "SSG", "Santa Isabel Airport", "Malabo", "GQ", 3.7553, 8.7087),
            Airport("DIAP", "ABJ", "Félix-Houphouët-Boigny International", "Abidjan", "CI", 5.2614, -3.9263),
            // ── Additional Americas ───────────────────────────────────────────
            Airport("SBBE", "BEL", "Belém Val de Cans International", "Belém", "BR", -1.3792, -48.4763),
            Airport("SBSV", "SSA", "Deputado Luís Eduardo Magalhães International", "Salvador", "BR", -12.9086, -38.3225),
            Airport("SPIM", "IQT", "Coronel FAP Francisco Secada Vignetta International", "Iquitos", "PE", -3.7847, -73.3088),
            Airport("SGAS", "ASU", "Silvio Pettirossi International Airport", "Asunción", "PY", -25.2399, -57.5198),
            Airport("SLVR", "VVI", "Viru Viru International Airport", "Santa Cruz", "BO", -17.6448, -63.1354),
            Airport("MMPO", "PXM", "Puerto Escondido International Airport", "Puerto Escondido", "MX", 15.8769, -97.0891),

            // ── Additional Europe ─────────────────────────────────────────────
            Airport("LYBE", "BEG", "Belgrade Nikola Tesla Airport", "Belgrade", "RS", 44.8184, 20.3091),
            Airport("LBSF", "SOF", "Sofia Airport", "Sofia", "BG", 42.6952, 23.4114),
            Airport("LUKK", "KIV", "Chișinău International Airport", "Chișinău", "MD", 46.9277, 28.9310),
            Airport("EETN", "TLL", "Tallinn Airport", "Tallinn", "EE", 59.4133, 24.8328),
            Airport("EVRA", "RIX", "Riga International Airport", "Riga", "LV", 56.9236, 23.9711),
            Airport("EYVI", "VNO", "Vilnius Airport", "Vilnius", "LT", 54.6341, 25.2858),
            Airport("LMML", "MLA", "Malta International Airport", "Valletta", "MT", 35.8574, 14.4775),
            Airport("LGKR", "CFU", "Corfu International Airport", "Corfu", "GR", 39.6019, 19.9117),
            Airport("LPFR", "FAO", "Faro Airport", "Faro", "PT", 37.0144, -7.9659),

            // ── Central / South Asia ──────────────────────────────────────────
            Airport("OAKB", "KBL", "Hamid Karzai International Airport", "Kabul", "AF", 34.5659, 69.2123),
            Airport("OPLA", "LHE", "Allama Iqbal International Airport", "Lahore", "PK", 31.5216, 74.4036),
            Airport("UTTT", "TAS", "Tashkent International Airport", "Tashkent", "UZ", 41.2579, 69.2812),
            Airport("UAAA", "ALA", "Almaty International Airport", "Almaty", "KZ", 43.3521, 77.0405),

            // ── Additional East / Southeast Asia ──────────────────────────────
            Airport("ZUCK", "CKG", "Chongqing Jiangbei International Airport", "Chongqing", "CN", 29.7192, 106.6417),
            Airport("ZGGG", "CAN", "Guangzhou Baiyun International Airport", "Guangzhou", "CN", 23.3924, 113.2990),
            Airport("ZUUU", "CTU", "Chengdu Tianfu International Airport", "Chengdu", "CN", 30.3124, 104.4440),
            Airport("RJCH", "CTS", "New Chitose Airport", "Sapporo", "JP", 42.7752, 141.6922),
            Airport("RCSS", "TSA", "Taipei Songshan Airport", "Taipei", "TW", 25.0694, 121.5522),
            Airport("VDPP", "PNH", "Phnom Penh International Airport", "Phnom Penh", "KH", 11.5466, 104.8441),
            Airport("VLVT", "VTE", "Wattay International Airport", "Vientiane", "LA", 17.9883, 102.5633),
            Airport("VYYY", "RGN", "Yangon International Airport", "Yangon", "MM", 16.9073, 96.1332),

            // ── Additional Middle East / Africa ───────────────────────────────
            Airport("OOMS", "MCT", "Muscat International Airport", "Muscat", "OM", 23.5933, 58.2844),
            Airport("OBKH", "BAH", "Bahrain International Airport", "Manama", "BH", 26.2708, 50.6336),
            Airport("HSPN", "SSH", "Sharm el-Sheikh International Airport", "Sharm el-Sheikh", "EG", 27.9773, 34.3950),
            Airport("FTTJ", "NDJ", "N'Djamena International Airport", "N'Djamena", "TD", 12.1337, 15.0340),
            Airport("DXXX", "LFW", "Lomé-Tokoin International Airport", "Lomé", "TG", 6.1656, 1.2545),
            Airport("HDAM", "JIB", "Djibouti-Ambouli International Airport", "Djibouti", "DJ", 11.5473, 43.1595)
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
