package org.example.bot;

import org.example.jdbc_database.BaseDatabase;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface BaseServiceBot {
    default InlineKeyboardMarkup getButtons(int divider,int pageIndex){
        return null;
    }
    default boolean isExistPage(String callBackDate){
        String[] split = callBackDate.split(":");
        return (split[0].equals(CategoryState.PREV.name())||split[0].equals(CategoryState.NEXT.name()))&&!split[2].equals("0");
    }
    default boolean isAuksionInfo(String data) {
        String[] split = data.split(":");
        return (split[0].equals(CategoryState.AUTONUMBER.name()));
    }

}
