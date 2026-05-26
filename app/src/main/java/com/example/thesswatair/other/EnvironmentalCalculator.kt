package com.example.thesswatair.other

object EnvironmentalCalculator {
    fun calculateFWI(temp: Double, wind: Double, hum: Double): Double {//υπολογισμός κινδύνου πυρκαγιάς πηγή:https://wikifire.wsl.ch/tiki-index27fc.html?page=McArthur+Mark+5+forest+fire+danger+index
        val D = 10.0//τιμή ξηρασίας
        val exponent = -0.450 +
                (0.987 * Math.log(D)) -
                (0.0345 * hum) +
                (0.0338 * temp) +
                (0.0234 * wind)

        val fwiResult = 2.0 * Math.exp(exponent)

        return if (fwiResult < 0) 0.0 else fwiResult//διασφάλιση μη αρνητικού αποτελέσματος
    }
    fun calculateRRI(air: Map<String, Double>?, tempC: Double, hum: Double): Double{//υπολογισμός δείκτη αναπνευστικού κινδύνου πηγή:https://en.wikipedia.org/wiki/Air_Quality_Health_Index_(Canada)
        val o3 = air?.get("o3") ?:0.0
        val no2 = air?.get("no2") ?: 0.0
        val pm25 = air?.get("pm2_5") ?: 0.0

        val riskO3 = Math.exp(0.000537 * o3) - 1.0
        val riskNO2 = Math.exp(0.000871 * no2) - 1.0
        val riskPM25 = Math.exp(0.000487 * pm25) - 1.0

        //rawAqhi: Ο επίσημος δείκτης (συνήθως 1-10+)
        val rawAqhi = (1000.0 / 10.4) * (riskO3 + riskNO2 + riskPM25)

        var modifier = 1.0
        if (hum > 75.0) modifier += 0.10  // +10% λόγω υγρασίας
        if (tempC > 36.0) modifier += 0.15 // +15% λόγω καύσωνα

        //Τελικό RRI
        //πολλαπλασιάζουμε επί 10 για να έχουμε μια κλίμακα 0-100
        val finalRRI = rawAqhi * 10.0 * modifier

        return if (finalRRI < 0) 0.0 else finalRRI
    }
    fun calculateHeatIndex(tempK: Double, hum: Double): Double{//υπολογισμός δείκτη δυσφορίας από την ζέστη πηγή:https://www.wpc.ncep.noaa.gov/html/heatindex_equation.shtml
        val tempC = if (tempK > 100.0) tempK - 273.15 else tempK//μετατροπή kelvin σε celsius

        val tempF = (tempC * 1.8) + 32//μετατροπή σε fahrenait λόγω του τύπου

        var hi = 0.5 * (tempF + 61.0 + ((tempF - 68.0) * 1.2) + (hum * 0.094))

        if (hi >= 80.0) {
            hi = -42.379 +
                    2.04901523 * tempF +
                    10.14333127 * hum -
                    0.22475541 * tempF * hum -
                    0.00683783 * tempF * tempF -
                    0.05481717 * hum * hum +
                    0.00122874 * tempF * tempF * hum +
                    0.00085282 * tempF * hum * hum -
                    0.00000199 * tempF * tempF * hum * hum

            if (hum < 13.0 && tempF in 80.0..112.0) {
                val adj = ((13.0 - hum) / 4.0) * Math.sqrt((17.0 - Math.abs(tempF - 95.0)) / 17.0)
                hi -= adj
            } else if (hum > 85.0 && tempF in 80.0..87.0) {
                val adj = ((hum - 85.0) / 10.0) * ((87.0 - tempF) / 5.0)
                hi += adj
            }
        }

        val finalRRICelsius = (hi - 32.0) / 1.8//μετατροπή του τελικού αποτελέσματος σε celcius

        return if (finalRRICelsius > 65.0 || finalRRICelsius < -50.0) tempC else finalRRICelsius
    }
}