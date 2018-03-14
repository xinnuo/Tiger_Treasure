package com.ruanmeng.model

import java.io.Serializable

/**
 * Created by PYM2017 on 2018/1/16.
 */
data class StewardData(
        var name: String = "",
        var phone: String = "",
        var title: String = ""
) : Serializable