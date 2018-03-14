/**
 * created by 小卷毛, 2017/10/26
 * Copyright (c) 2017, 416143467@qq.com All Rights Reserved.
 * #                   *********                            #
 * #                  ************                          #
 * #                  *************                         #
 * #                 **  ***********                        #
 * #                ***  ****** *****                       #
 * #                *** *******   ****                      #
 * #               ***  ********** ****                     #
 * #              ****  *********** ****                    #
 * #            *****   ***********  *****                  #
 * #           ******   *** ********   *****                #
 * #           *****   ***   ********   ******              #
 * #          ******   ***  ***********   ******            #
 * #         ******   **** **************  ******           #
 * #        *******  ********************* *******          #
 * #        *******  ******************************         #
 * #       *******  ****** ***************** *******        #
 * #       *******  ****** ****** *********   ******        #
 * #       *******    **  ******   ******     ******        #
 * #       *******        ******    *****     *****         #
 * #        ******        *****     *****     ****          #
 * #         *****        ****      *****     ***           #
 * #          *****       ***        ***      *             #
 * #            **       ****        ****                   #
 */
package com.ruanmeng.model

import java.io.Serializable

/**
 * 项目名称：Tiger_Treasure
 * 创建人：小卷毛
 * 创建时间：2017-10-26 17:16
 */
data class MomentsData(
        var accountInfoId: String = "",
        var circleId: String = "",
        var createDate: String = "",
        var cricleContent: String = "",
        var cricleImages: String = "",
        var thumbs: String = "",
        var userName: String = "",
        var userhead: String = "",
        var collecon: String = "",
        var compName: String = "",
        var showDate: String = "",

        var cricleReplyId: String = "",
        var replyAccountInfoId: String = "",
        var replyContent: String = "",
        var replyToAccountInfoId: String = "",
        var replyToUserName: String = "",
        var replyUserName: String = "",

        var thumbsId: String = "",

        var circleReplyList: List<MomentsData> ?= ArrayList(),
        var thumbsList: List<MomentsData> ?= ArrayList()
) : Serializable