package com.example.thesswatair.other

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
//πηγή για emojis:https://getemoji.com/
object EnvironmentInfo {
    object AqiInfo{
        fun getAQILevel(aqi:Int):String{
            return when{
                aqi <= 50 -> "Καλό"
                aqi <= 100 -> "Μέτριο"
                aqi <= 150 -> "Ανθυγιεινό για τις ευπαθείς ομάδες"
                aqi <= 200 -> "Ανθυγιεινό"
                aqi<=300 -> "Πολύ ανθυγιεινό"
                else ->"Τοξικό"
            }
        }
        @Composable
        fun getAQIColor(aqi:Int): Color{
            val isDark = isSystemInDarkTheme()
            return when{
                aqi <= 50 -> if (isDark) Color(0xFF81C784) else Color(0xFF4CAF50)      // Πράσινο
                aqi <= 100 -> if (isDark) Color(0xFFFFF176) else Color(0xFFFBC02D)    // Κίτρινο/Μέτριο
                aqi <= 150 -> if (isDark) Color(0xFFFFB74D) else Color(0xFFFB8C00)    // Πορτοκαλί
                aqi <= 200 -> if (isDark) Color(0xFFE57373) else Color(0xFFF44336)    // Κόκκινο
                aqi <= 300 -> if (isDark) Color(0xFFBA68C8) else Color(0xFF9C27B0)    // Μοβ
                else -> if (isDark) Color(0xFF9575CD) else Color(0xFF673AB7)
            }
        }
        fun getHealthAdvice(aqi:Int): HealthAdvice{
            return when{
                aqi <= 50 -> HealthAdvice(
                    title = "Ιδανικές Συνθήκες",
                    description = "Η ποιότητα του αέρα θεωρείται ικανοποιητική και η ατμοσφαιρική ρύπανση εγκυμονεί ελάχιστο έως καθόλου κίνδυνο.",
                    icon = "\uD83C\uDFC3\u200D♂\uFE0F",
                )
                aqi <= 100 -> HealthAdvice(
                    title = "Μέτρια Ποιότητα",
                    description = "Ο αέρας είναι αποδεκτός. Τα ευαίσθητα άτομα ενδέχεται να χρειαστεί να μειώσουν την έντονη σωματική άσκηση σε εξωτερικούς χώρους.",
                    icon = "\uD83D\uDEB6\u200D♀\uFE0F",
                )
                aqi <= 150 -> HealthAdvice(
                    title="Ανθυγιεινή για Ευαίσθητες Ομάδες",
                    description = "Τα μέλη των ευαίσθητων ομάδων ενδέχεται να εμφανίσουν επιπτώσεις στην υγεία τους. Το ευρύ κοινό δεν είναι πιθανό να επηρεαστεί.",
                    icon = "\uD83D\uDE10",
                )
                aqi <= 200 -> HealthAdvice(
                    title = "Ανθυγιεινή",
                    description = "Όλος ο πληθυσμός ενδέχεται να αρχίσει να εμφανίζει επιπτώσεις στην υγεία. Τα μέλη των ευαίσθητων ομάδων μπορεί να εμφανίσουν πιο σοβαρά προβλήματα.",
                    icon = "\uD83D\uDE10",
                )
                aqi <= 300 -> HealthAdvice(
                    title = "Πολύ Ανθυγιεινή",
                    description = "Προειδοποίηση υγείας για καταστάσεις έκτακτης ανάγκης. Είναι εξαιρετικά πιθανό να επηρεαστεί ολόκληρος ο πληθυσμός.",
                    icon = "\uD83D\uDE37",
                )
                else -> HealthAdvice(
                    title = "Επικίνδυνες Συνθήκες",
                    description = "Συναγερμός υγείας: όλος ο κόσμος ενδέχεται να εμφανίσει σοβαρές επιπτώσεις. Όλοι θα πρέπει να αποφεύγουν κάθε σωματική καταπόνηση σε εξωτερικούς χώρους.",
                    icon = "☠\uFE0F",
                )
            }
        }
    }
    object fireInfo{
        fun getFireRiskLevel(calcFWI:Double):String{
            return when{
                calcFWI < 5.2 -> "Πολύ Χαμηλή Πιθανότητα"
                calcFWI < 11.2 -> "Χαμηλή Πιθανότητα"
                calcFWI < 21.3 -> "Μέτρια Πιθανότητα"
                calcFWI < 38.0 -> "Υψηλή Πιθανότητα"
                calcFWI < 50.0 -> "Πολύ Υψηλή Πιθανότητα"
                calcFWI >= 50.0 -> "Ακραία Πιθανότητα"
                else -> "Άγνωστο"
            }
        }
        fun getFireRiskLevelEmoji(calcFWI: Double):String{
            return when{
                calcFWI < 5.2 -> "\uD83D\uDCA7"
                calcFWI < 11.2 -> "\uD83C\uDF3F"
                calcFWI < 21.3 -> "⚠\uFE0F"
                calcFWI < 38.0 -> "\uD83D\uDD25"
                calcFWI < 50.0 -> "\uD83D\uDE92"
                calcFWI >= 50.0 -> "\uD83C\uDF0B"
                else -> "?"
            }
        }
        @Composable
        fun getFireRiskLevelColor(calcFWI: Double): Color {
            val isDark = isSystemInDarkTheme()
            return when {
                calcFWI < 5.2 -> if (isDark) Color(0xFF81D4FA) else Color(0xFF2196F3)
                calcFWI < 11.2 -> if (isDark) Color(0xFFA5D6A7) else Color(0xFF4CAF50)
                calcFWI < 21.3 -> if (isDark) Color(0xFFFFF59D) else Color(0xFFFFEB3B)
                calcFWI < 38.0 -> if (isDark) Color(0xFFFFCC80) else Color(0xFFFF9800)
                calcFWI < 50.0 -> if (isDark) Color(0xFFE57373) else Color(0xFFF44336)
                else -> if (isDark) Color(0xFFCE93D8) else Color(0xFF9C27B0)
            }
        }
    }

    object AdvancedIndices{
        @Composable
        fun getHeatIndexColor(hi: Double):Color{
            val isDark = isSystemInDarkTheme()
            return when{
                hi < 27.0 -> if(isDark) Color(0xFF90CAF9) else Color(0xFF64B5F6)
                hi < 33.0 -> if (isDark) Color(0xFFFFF59D) else Color(0xFFFFEB3B)
                hi < 41.0 -> if (isDark) Color(0xFFFFCC80) else Color(0xFFFFA000)
                else -> if (isDark) Color(0xFFEF9A9A) else Color(0xFFD32F2F)
            }
        }
        fun getRRILevel(rri: Double): String {
            return when {
                rri <= 30.0 -> "Χαμηλός Κίνδυνος"
                rri <= 60.0 -> "Μέτριος Κίνδυνος"
                rri <= 90.0 -> "Υψηλός Κίνδυνος"
                else -> "Πολύ Υψηλός Κίνδυνος"
            }
        }
    }
}