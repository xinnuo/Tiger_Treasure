/**
 * created by 小卷毛, 2017/10/12
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
@file:Suppress("NOTHING_TO_INLINE")

package com.ruanmeng.base

import android.content.Intent
import android.support.v4.app.Fragment
import android.widget.Toast
import com.maning.mndialoglibrary.MToast
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.PreferencesUtils

/**
 * 项目名称：Tiger_treasure
 * 创建人：小卷毛
 * 创建时间：2017-10-25 16:03
 */
inline fun Fragment.startActivity(activity: Class<*>) = startActivity(Intent(this.activity, activity))

inline fun Fragment.toast(text: CharSequence) = MToast.makeTextShort(this.activity, text).show()

inline fun Fragment.showToast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this.activity, text, duration).show()

inline fun Fragment.getString(key: String): String = PreferencesUtils.getString(this.activity, key, "")

inline fun Fragment.getString(key: String, defaultValue: String): String = PreferencesUtils.getString(this.activity, key, defaultValue)

inline fun Fragment.putString(key: String, vaule: String) = PreferencesUtils.putString(this.activity, key, vaule)

inline fun Fragment.getBoolean(key: String): Boolean = PreferencesUtils.getBoolean(this.activity, key)

inline fun Fragment.putBoolean(key: String, vaule: Boolean) = PreferencesUtils.putBoolean(this.activity, key, vaule)

inline fun Fragment.showLoadingDialog() = DialogHelper.showDialog(this.activity)

inline fun Fragment.cancelLoadingDialog() = DialogHelper.dismissDialog()