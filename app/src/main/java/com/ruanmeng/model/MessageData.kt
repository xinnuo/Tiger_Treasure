package com.ruanmeng.model

import java.io.Serializable

/**
 * Created by PYM2017 on 2018/1/26.
 */
data class MessageData(
        val content: String = "",
        val coverImg: String = "",
        val createDate: String = "",
        val expiryDate: String = "",
        val groupId: String = "",
        val groupType: String = "",
        val messageInfoId: String = "",
        val messageType: String = "",
        val sendAccountInfoId: String = "",
        val sendDate: String = "",
        val targetId: String = "",
        val title: String = ""
) : Serializable