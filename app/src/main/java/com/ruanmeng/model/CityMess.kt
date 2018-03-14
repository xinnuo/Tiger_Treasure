package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Tiger_Treasure
 * 创建人：小卷毛
 * 创建时间：2017-11-20 12:52
 */
data class CityMess(

        var areaId: String = "",
        var areaName: String = "",
        var firstLetter: String = "",
        var lat: String = "",
        var lng: String = "",
        var location: String = "",

        var enterpriserId: String = "",
        var letter: String = "",
        var userName: String = "",
        var isChecked: Boolean = false,
        var userhead: String = ""
) : Serializable