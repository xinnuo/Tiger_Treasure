package com.ruanmeng;

import android.net.Uri;

import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.model.UserInfo;

@ConversationProviderTag(
        conversationType = "discussion",
        portraitPosition = 1
)
public class DiscussionConversationProvider extends io.rong.imkit.widget.provider.DiscussionConversationProvider {

    @Override
    public String getTitle(String id) {
        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(id);
        return userInfo == null ? id : userInfo.getName();
    }

    @Override
    public Uri getPortraitUri(String id) {
        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(id);
        return userInfo == null ? null : userInfo.getPortraitUri();
    }

}
