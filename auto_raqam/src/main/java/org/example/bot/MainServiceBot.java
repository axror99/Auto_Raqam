package org.example.bot;

import org.example.jdbc_database.BaseDatabase;
import org.example.model.AutoNumber;
import org.example.model.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class MainServiceBot<R> implements BaseServiceBot {

    private BaseDatabase<R> baseDatabase = null;

    public MainServiceBot(BaseDatabase<R> baseDatabase) {
        this.baseDatabase = baseDatabase;
    }

    @Override
    public InlineKeyboardMarkup getButtons(int divider, int pageIndex) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<R> list = baseDatabase.getPaginationList(pageIndex, 2);
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        inlineKeyboardMarkup.setKeyboard(rows);

        List<InlineKeyboardButton> rowButtons = new ArrayList<>();
        CategoryState categoryState = null;
        for (int i = 0; i < list.size(); i++) {
            R obj = list.get(i);
            InlineKeyboardButton button = null;
            if (obj instanceof AutoNumber) {
                categoryState = CategoryState.AUTONUMBER;
                AutoNumber autoNumber = (AutoNumber) obj;
                button = new InlineKeyboardButton(autoNumber.getNumber());
                button.setCallbackData(categoryState.name() + ":" + autoNumber.getNumber());
            } else if (obj instanceof User) {

            }
            rowButtons.add(button);
            if (i % divider == 0) {
                rows.add(rowButtons);
                rowButtons = new ArrayList<>();
            }
        }
        if (!rowButtons.isEmpty()) {
            rows.add(rowButtons);
        }
        List<InlineKeyboardButton> buttons = getPaginationButtons(pageIndex, categoryState);
        rows.add(buttons);

        return inlineKeyboardMarkup;
    }

    private List<InlineKeyboardButton> getPaginationButtons(int pageIndex, CategoryState state) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setCallbackData(CategoryState.PREV.name() + ":" + state.name() + ":" + (--pageIndex));
        button1.setText(CategoryState.PREV.name());
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        pageIndex += 2;
        button2.setCallbackData(CategoryState.NEXT.name() + ":" + state.name() + ":" + pageIndex);
        button2.setText(CategoryState.NEXT.name());
        buttons.add(button1);
        buttons.add(button2);
        return buttons;
    }



}
