package com.core.utils


fun String.toEnglishNumber(): String {
    if (this.isEmpty()) return ""
    var out = ""
    val length = this.length
    for (i in 0 until length) {
        val c = this[i]
        if (c in '۰'..'۹') {
            val number = Integer.parseInt(c.toString())
            out += englishNumbers[number]
        } else if (c == '٫' || c == '،')
            out += ','.toString()
        else
            out += c
    }
    return out
}

val persianNumbers = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
val englishNumbers = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")

fun String.toPersianNumber(): String {
    if (this.isEmpty()) return ""
    var out = ""
    val length = this.length
    for (i in 0 until length) {
        when (val c = this[i]) {
            in '0'..'9' -> {
                val number = Integer.parseInt(c.toString())
                out += persianNumbers[number]
            }
            '٫' -> out += '،'.toString()
            else -> out += c
        }
    }
    return out
}

fun String.addThousandSeparator(): String {
    if (this.isNotEmpty()) {
        var normalPrice: String = this
        if (this.contains(".")) {
            normalPrice = this.substringBefore(".")
        }
        val f = StringBuilder()
        val temp = normalPrice.replace("[^0-9۰-۹]+".toRegex(), "")
        for (i in temp.indices) {
            f.append(temp[i])
            if ((temp.length - 1 - i) % 3 == 0 && temp.length - 1 - i != 0) {
                f.append(",")
            }
        }
        return f.toString()
    }else{
        return ""
    }
}

fun String.convertArabicToPersian() =
    this.replace("ي", "ی").replace("ك", "ک")

fun String.embedRTL(): String =
    if (this.isEmpty()) {
        ""
    } else '\u202B'.toString() + this + '\u202B'.toString()

fun String.embedLTR(): String =
    if (this.isEmpty()) "" else '\u202A'.toString() + this + '\u202A'.toString()


fun String?.getParsedPersianDate(): String {
    return if (!this.isNullOrEmpty()){
        val date = this.substringBefore("T")
        val splitDateList = date.split("-")
        val jCal = CalendarDateConverter()
        jCal.GregorianToPersian(
            Integer.valueOf(splitDateList[0]),
            Integer.valueOf(splitDateList[1]),
            Integer.valueOf(splitDateList[2])
        )

        jCal.toString()
    }else{
        ""
    }

}