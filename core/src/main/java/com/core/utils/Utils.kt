package com.core.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import com.core.dto.PersianDate
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class Utils {

    companion object {

        fun dpToPx(context: Context, dp: Float): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
        }

        fun pxToDp(context: Context, px: Int): Int {
            val displayMetrics: DisplayMetrics = context.resources.displayMetrics
            return Math.round((px / (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toDouble()).toInt()
        }

        fun convertMillisecondToTime(value: Long): String {
            return if (value == 0L) {
                "00:00:00"
            } else {
                val time = TimeUnit.MILLISECONDS.toSeconds(value)
                val min  = time / 60
                val sec  = time % 60
                val seconds100      = ((value - TimeUnit.SECONDS.toMillis(time)).toFloat() / 10).roundToInt()
                String.format("%s:%s:%s",
                    if (min < 10) {
                        "0$min"
                    } else {
                        "$min"
                    },
                    if (sec < 10) {
                        "0$sec"
                    } else {
                        "$sec"
                    },
                    if (seconds100 < 10) {
                        "0$seconds100"
                    } else {
                        "$seconds100"
                    }
                )
            }
        }

        fun calculateDateDifference(year:Int, month:Int, dayOfMonth:Int, dayDifference:Int): PersianDate {
            val persianDate = PersianDate(1300,1,1)
            persianDate.year=year
            persianDate.month=month
            persianDate.dayOfMonth=dayOfMonth

            val day=dayDifference
            val countOfDay1=31
            val countOfDay2=30
            if(day>0){

                var dayOfYear=0
                if(persianDate.month>=1 ) {
                    for (i in 1..persianDate.month-1) {
                        if (i <= 6)
                            dayOfYear += 31
                        else
                            dayOfYear += 30
                    }
                    dayOfYear += persianDate.dayOfMonth
                    dayOfYear += day
                }else{
                    dayOfYear=persianDate.dayOfMonth
                }

                if(dayOfYear<=365) {
                    if (dayOfYear < 186) {
                        if (dayOfYear <= 31) {
                            persianDate.month = 1
                            persianDate.dayOfMonth = dayOfYear
                        } else {
                            val monthDivid = dayOfYear / countOfDay1
                            val dayDivid = dayOfYear % countOfDay1
                            persianDate.month = monthDivid
                            if (dayDivid > 0) {
                                persianDate.month++
                                persianDate.dayOfMonth = dayDivid
                            } else
                                persianDate.dayOfMonth = 31
                        }
                    } else {
                        val divid = dayOfYear - 186
                        if (divid > countOfDay2) {
                            val monthDivid = divid / countOfDay2
                            val dayDivid = divid % countOfDay2

                            persianDate.month = monthDivid + 6
                            if (dayDivid > 0) {
                                if (monthDivid > 0)
                                    persianDate.month++
                                persianDate.dayOfMonth = dayDivid
                            } else
                                persianDate.dayOfMonth = countOfDay2
                        } else {
                            if (divid > 0) {
                                persianDate.month = 7
                                persianDate.dayOfMonth = divid
                            } else {
                                persianDate.month = 6
                                persianDate.dayOfMonth = 31
                            }
                        }
                    }
                }else {
                    val dividYear = dayOfYear / 365
                    persianDate.year = persianDate.year + dividYear
                    dayOfYear %= 365

                    if (dayOfYear > 0) {
                        if (dayOfYear < 186) {
                            if (dayOfYear <= 31) {
                                persianDate.month = 1
                                persianDate.dayOfMonth = dayOfYear
                            } else {
                                val monthDivid = dayOfYear / countOfDay1
                                val dayDivid = dayOfYear % countOfDay1
                                persianDate.month = monthDivid
                                if (dayDivid > 0) {
                                    persianDate.month++
                                    persianDate.dayOfMonth = dayDivid
                                } else
                                    persianDate.dayOfMonth = 31
                            }
                        } else {
                            val divid = dayOfYear - 186
                            if (divid > countOfDay2) {
                                val monthDivid = divid / countOfDay2
                                val dayDivid = divid % countOfDay2

                                persianDate.month = monthDivid + 6
                                if (dayDivid > 0) {
                                    if (monthDivid > 0)
                                        persianDate.month++
                                    persianDate.dayOfMonth = dayDivid
                                } else
                                    persianDate.dayOfMonth= countOfDay2
                            } else {
                                if (divid > 0) {
                                    persianDate.month = 7
                                    persianDate.dayOfMonth = divid
                                } else {
                                    persianDate.month = 6
                                    persianDate.dayOfMonth = 31
                                }
                            }
                        }
                    }
                }
            }else{
                var dayOfYear=0
                if(persianDate.month>=1 ) {
                    for (i in 1 until persianDate.month-1) {
                        dayOfYear += if (i <= 6)
                            31
                        else
                            30
                    }
                    dayOfYear += persianDate.dayOfMonth
                    dayOfYear += day
                }else{
                    dayOfYear=persianDate.dayOfMonth
                }

                if(dayOfYear>0) {
                    if (dayOfYear < 186) {
                        if(dayOfYear<=31){
                            persianDate.month = 1
                            persianDate.dayOfMonth = dayOfYear
                        }else {
                            val monthDivid = dayOfYear / countOfDay1
                            val dayDivid = dayOfYear % countOfDay1
                            persianDate.month = monthDivid
                            if (dayDivid > 0) {
                                persianDate.month++
                                persianDate.dayOfMonth = dayDivid
                            } else
                                persianDate.dayOfMonth = 31
                        }
                    } else {
                        val divid = dayOfYear - 186
                        if (divid > countOfDay2) {
                            val monthDivid = divid / countOfDay2
                            val dayDivid = divid % countOfDay2

                            persianDate.month = monthDivid + 6
                            if (dayDivid > 0) {
                                if (monthDivid > 0)
                                    persianDate.month++
                                persianDate.dayOfMonth = dayDivid
                            } else
                                persianDate.dayOfMonth= countOfDay2
                        } else {
                            if (divid > 0) {
                                persianDate.month = 7
                                persianDate.dayOfMonth = divid
                            } else {
                                persianDate.month = 6
                                persianDate.dayOfMonth= 31
                            }
                        }
                    }
                }else{
                    dayOfYear *= -1
                    if(dayOfYear>365){
                        val dividYear=dayOfYear/365
                        persianDate.year=persianDate.year-dividYear-1
                        dayOfYear %= 365
                    }else{
                        persianDate.year--
                    }
                    dayOfYear=365-dayOfYear

                    if(dayOfYear>0) {
                        if (dayOfYear < 186) {
                            if(dayOfYear<=31){
                                persianDate.month = 1
                                persianDate.dayOfMonth = dayOfYear
                            }else {
                                val monthDivid = dayOfYear / countOfDay1
                                val dayDivid = dayOfYear % countOfDay1
                                persianDate.month = monthDivid
                                if (dayDivid > 0) {
                                    persianDate.month++
                                    persianDate.dayOfMonth = dayDivid
                                } else
                                    persianDate.dayOfMonth = 31
                            }
                        } else {
                            val divid = dayOfYear - 186
                            if (divid > countOfDay2) {
                                val monthDivid = divid / countOfDay2
                                val dayDivid = divid % countOfDay2

                                persianDate.month = monthDivid + 6
                                if (dayDivid > 0) {
                                    if (monthDivid > 0)
                                        persianDate.month++
                                    persianDate.dayOfMonth = dayDivid
                                } else
                                    persianDate.dayOfMonth = countOfDay2
                            } else {
                                if (divid > 0) {
                                    persianDate.month = 7
                                    persianDate.dayOfMonth = divid
                                } else {
                                    persianDate.month = 6
                                    persianDate.dayOfMonth = 31
                                }
                            }
                        }
                    }

                }
            }
            return persianDate
        }

        fun calculateDateDifferenceFromToday(selectedDate :PersianDate, today: PersianDate):Int{
            return (getDayOfYear(selectedDate) - getDayOfYear(today) ) + ( selectedDate.year - today.year) * 365
        }

        private fun getDayOfYear(date:PersianDate): Int {
            return if(date.month >6) {
                6*31 + ((date.month - 1) - 6) * 30 + date.dayOfMonth
            } else {
                (date.month-1)*31 + date.dayOfMonth
            }
        }
    }


}