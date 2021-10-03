package com.core.dto

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize


@Parcelize
@Keep
data class LoginRequest(
    @Keep var AgentName : String
): Parcelable

@Parcelize
@Keep
data class LoginResponse(
    @Keep var users : List<User>
): Parcelable

@Parcelize
@Keep
data class User(
    @Keep var userName:String,
    @Keep var id:String
):Parcelable