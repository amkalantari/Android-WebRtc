package com.axon.webrtc.util.bindingAdapter

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

fun NavController.navigateSafe(
    resId: NavDirections,
    navOptions: NavOptions? = null,
    navExtras: Navigator.Extras? = null
) {

    if (currentDestination?.id != resId.actionId){
        navigate(resId.actionId, resId.arguments, navOptions, navExtras)
    }

}