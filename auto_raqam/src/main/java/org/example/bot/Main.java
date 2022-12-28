package org.example.bot;

import org.example.jdbc_database.AuksionViewDatabase;
import org.example.jdbc_database.AvtoNumberDataBase;
import org.example.jdbc_database.UserDatabase;
import org.example.model.AutoNumber;
import org.example.model.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main extends TelegramLongPollingBot implements BaseServiceBot {
    private final static String BOT_USERNAME = "http://t.me/avto_auk_bot";
    private final static String BOT_TOKEN = "5959168659:AAFG2TZwVOXPpB4kUsFSU0qvFrQEgsWgIjg";
    static UserDatabase userDatabase = new UserDatabase();
    static Map<Long, Integer> AUTO_NUMBER_MESSAGE_MAP = new ConcurrentHashMap<Long, Integer>();
    static Map<Long, Integer> AUKSION_INFO_MESSAGE_MAP = new ConcurrentHashMap<Long, Integer>();

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                String text = message.getText();
                Long chatId = message.getChatId();
                if (text.equals("/start")) {
                    myExecute(
                            "you will do mistake only one time in your whole life unfortunately you did it",
                            chatId,
                            generateReplyKeyboard(List.of("Settings", "Numbers", "Users"), 2),
                            null
                    );
                } else if (text.equals("Numbers")) {
                    InlineKeyboardMarkup buttons = getInlineKeyboardAutoNumberList(1);
                    Integer messageId = myExecute("auto number list", chatId, null, buttons);
                    AUTO_NUMBER_MESSAGE_MAP.put(chatId, messageId);

                } else if (text.equals("Users")) {
                    MainServiceBot<User> var = new MainServiceBot(new UserDatabase());
                    InlineKeyboardMarkup buttons = var.getButtons(2, 6);
                    myExecute("user list", chatId, null, buttons);
                }
            } else if (message.hasContact()) {
                deleteFirstNumberInlineKeyboard(message.getContact());
                repeatFirstNumberInlineKeyboard(message.getContact());
            }
        } else if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            CallbackQuery callbackQuery = update.getCallbackQuery();
            if (!userDatabase.checkUser(chatId)) {
                ReplyKeyboardMarkup r = shareContact();
                myExecute("PLEASE SHARE YOUR CONTACT", callbackQuery.getMessage().getChatId(), r, null);
            }
            if (isExistPage(data)) {
                String[] split = data.split(":");
                int index = Integer.parseInt(split[2]);
                if (split[1].equals(CategoryState.AUTONUMBER.name())) {
                    InlineKeyboardMarkup buttons = getInlineKeyboardAutoNumberList(index);
                    Integer prevMessageId = AUTO_NUMBER_MESSAGE_MAP.get(chatId);
                    Integer messageId = myExecuteEdit("auto number list", chatId, prevMessageId, buttons);
                    AUTO_NUMBER_MESSAGE_MAP.put(chatId, messageId);
                }
            }
            if (isAuksionInfo(data)) {
                String auksionInfo = new AuksionViewDatabase().getAuksionInfo(
                        data.substring(CategoryState.AUTONUMBER.name().length() + 1)
                );
                Integer auksionInfoId = null;
                if (AUKSION_INFO_MESSAGE_MAP.get(chatId) == null) {
                    auksionInfoId = myExecute(auksionInfo, chatId, null, null);
                }else {
                    auksionInfoId = myExecuteEdit(auksionInfo, chatId, AUKSION_INFO_MESSAGE_MAP.get(chatId), null);
                }
                AUKSION_INFO_MESSAGE_MAP.put(chatId,auksionInfoId);
            }
        }
    }

    private InlineKeyboardMarkup getInlineKeyboardAutoNumberList(int pageIndex) {
        return new MainServiceBot(new AvtoNumberDataBase()).getButtons(3, pageIndex);
    }

    private void getAutoNumberInfo(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
    }

    private ReplyKeyboardMarkup shareContact() {
        ReplyKeyboardMarkup r = new ReplyKeyboardMarkup();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton("SHARE CONTACT");
        keyboardButton.setRequestContact(true);
        keyboardRow.add(keyboardButton);
        r.setKeyboard(List.of(
                keyboardRow
        ));
        r.setResizeKeyboard(true);
        r.setSelective(true);
        r.setOneTimeKeyboard(true);
        return r;
    }

    private Integer myExecute(String text, long chatId, ReplyKeyboardMarkup r, InlineKeyboardMarkup i) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(r == null ? i : r);
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        sendMessage.enableMarkdown(true);
        try {
            return execute(sendMessage).getMessageId();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer myExecuteEdit(String text, long chatId, Integer prevMessageId, InlineKeyboardMarkup i) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(prevMessageId);
        editMessageText.setReplyMarkup(i);
        editMessageText.setText(text);
        editMessageText.enableMarkdown(true);
        try {
            execute(editMessageText);
            return editMessageText.getMessageId();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    private ReplyKeyboardMarkup generateReplyKeyboard(List<String> menuList, int dividerRow) {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        KeyboardRow keyboardButton = new KeyboardRow();
        for (int i = 1; i <= menuList.size(); i++) {
            keyboardButton.add(menuList.get(i - 1));
            if (i % dividerRow == 0) {
                keyboardRowList.add(keyboardButton);
                keyboardButton = new KeyboardRow();
            }
        }
        if (!keyboardButton.isEmpty()) {
            keyboardRowList.add(keyboardButton);
        }
        return replyKeyboardMarkup;
    }

    private void deleteMessage(int deleteMessageId, long chatId) {
        DeleteMessage deleteMessage = new DeleteMessage(String.valueOf(chatId), deleteMessageId);
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void deleteFirstNumberInlineKeyboard(Contact contact) {
        Integer deleteMessageId = AUTO_NUMBER_MESSAGE_MAP.get(contact.getUserId());
        deleteMessage(deleteMessageId, contact.getUserId());
    }

    private void repeatFirstNumberInlineKeyboard(Contact contact) {
        InlineKeyboardMarkup buttons = getInlineKeyboardAutoNumberList(1);
        Integer numberId = myExecute("auto number list", contact.getUserId(), null, buttons);
        userDatabase.addUser(contact.getUserId(), contact.getFirstName() + " " + contact.getLastName(), contact.getPhoneNumber(), contact.getPhoneNumber(), 1);
        AUTO_NUMBER_MESSAGE_MAP.put(contact.getUserId(), numberId);
    }
}
