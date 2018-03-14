package com.ruanmeng.model

import java.io.Serializable

/**
 * Created by PYM2017 on 2018/1/9.
 */
data class ScreenData(
        var name: String = "",
        var selected: String = "",
        var value: ArrayList<KeyValueData>? = ArrayList()
) : Serializable