package com.core.dto

import androidx.annotation.Keep

@Keep
class ResultDto<T> {
    @Keep
    var code: Int = 0

    @Keep
    var message: String? = null

    @Keep
    var result: T? = null
}