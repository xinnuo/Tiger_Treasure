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
data class CommonData(
        //首页轮播图
        var createDate: String = "",
        var sliderId: String = "",
        var sliderTitle: String = "",
        var content: String = "",
        var sliderImg: String = "",
        var sliderType: String = "",
        var sendStatus: String = "",
        var jumpType: String = "",
        var moduleType: String = "",

        //首页公告
        var coverImg: String = "",
        var messageType: String = "",
        var messageId: String = "",
        var messageInfoId: String = "",
        var readStatus: String = "",
        var recAccountInfoId: String = "",
        var title: String = "",

        //金融圈
        var financialId: String = "",
        var financialStatus: String = "",
        var financialTitle: String = "",
        var financialImage: String = "",

        //行业类别
        var industryId: String = "",
        var industryName: String = "",
        var position: Int = -1,
        var childs: List<CommonData>? = ArrayList(),


        //猜你喜欢
        var compLogo: String = "",
        var compName: String = "",
        var companyId: String = "",
        var showDate: String = "",

        //我的关注+关注我的
        var accountInfoId: String = "",
        var colleconId: String = "",
        var userName: String = "",
        var userhead: String = "",

        //我的收藏
        var address: String = "",
        var productId: String = "",
        var productName: String = "",
        var productPic: String = "",
        var productPrice: String = "",

        //我的产品
        var retailPrice: String = "",
        var supplyDescribe: String = "",
        var supplyId: String = "",
        var supplyPic: String = "",
        var supplyTitle: String = "",

        var isChecked: Boolean = false,
        var managerId: String = "",
        var id: String = "",
        var image: String = "",
        var name: String = "",
        var type: String = "",

        //兴趣爱好
        var chatRoomImage: String = "",
        var chatRoomId: String = "",
        var chatRoomName: String = "",

        //谁浏览过我+我的足迹
        var footprintId: String = "",

        //企业管家列表
        var positionName: String = "",

        //版本跟新
        var versionNo: String = "",
        var force: String = "",
        var url: String = "",
        var versionId: String = ""

) : Serializable