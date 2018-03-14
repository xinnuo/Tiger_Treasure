package com.ruanmeng;

import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

/**
 * 项目名称：Tiger_Treasure
 * 创建人：小卷毛
 * 创建时间：2017-11-27 16:40
 */
public class CustomExtensionModule extends DefaultExtensionModule {

    /**
     * 返回需要展示的 plugin 列表
     */
    @Override
    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {
        List<IPluginModule> pluginModules = super.getPluginModules(conversationType);
//        pluginModules.add(new DefaultLocationPlugin());
        return pluginModules;
    }

    /**
     * 返回需要展示的 EmoticonTab 列表
     */
    @Override
    public List<IEmoticonTab> getEmoticonTabs() {
        return super.getEmoticonTabs();
    }

}
