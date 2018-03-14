/**
 * created by 小卷毛, 2017/11/20
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
 * 创建时间：2017-11-20 12:52
 */
data class PersonData(
        val accountInfoId: String = "",
        val address: String = "",
        val age: String = "",
        val birthday: String = "",
        val busCard: String = "",
        val createDate: String = "",
        val email: String = "",
        val expiryDate: String = "",
        val footPrintNum: String = "",
        val hobby: String = "",
        val motto: String = "",
        val msgNum: String = "",
        val nickName: String = "",
        val positionName: String = "",
        val qq: String = "",
        val qrode: String = "",
        val rongToken: String = "",
        val scNum: String = "",
        val sex: String = "",
        val telephone: String = "",
        val telephoneIsOpen: String = "",
        val userName: String = "",
        val userhead: String = "",
        val vipTypeName: String = "",
        val zjToMineNum: String = "",
        val collecon: String = "",

        val compEmail: String = "",
        val compName: String = "",
        val compTel: String = "",
        val compService: String = "",
        val compUrl: String = "",
        val companyId: String = "",
        val copmProfile: String = "",
        val distance: String = "",
        val accountType: String = "",
        val compLogo: String = "",
        val compImage: String = ""
) : Serializable