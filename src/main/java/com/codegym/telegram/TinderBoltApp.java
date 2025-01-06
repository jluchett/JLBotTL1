package com.codegym.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TinderBoltApp extends SimpleTelegramBot {

    public static final String TELEGRAM_BOT_TOKEN = "8157762706:AAEAiXN0LF9QzSDCFwA1QsVSmEaQixUIWU0"; //añadir el token del bot entre comillas
    public static final String OPEN_AI_TOKEN = "chat-gpt-token"; //añadir el token de ChatGPT entre comillas

    public TinderBoltApp() {
        super(TELEGRAM_BOT_TOKEN);
    }

    //escribiremos la funcionalidad principal del bot aquí
    public void hello() {
      String text = getMessageText();
    	sendTextMessage("*Hello world*");
      sendTextMessage("_How are you?_");
      sendTextMessage("You wrote: " + text);

      sendPhotoMessage("avatar_main");

      sendTextButtonsMessage("Launch process", "start", "Start", "stop", "Stop");
    }

    public void helloButton(){
      String key = getButtonKey();
      if (key.equals("start")){
        sendTextMessage("The process has been launched again");
      }else {
        sendTextMessage("The process has been stoped");
      }
    }

    public void startCommand(){
      String text = loadMessage("main");
      sendPhotoMessage("main");
      sendTextMessage(text);
    }

    @Override
    public void onInitialize() {
        //y un poco más aquí :)
        addMessageHandler(this::hello);
        addButtonHandler("^.*", this::helloButton);
        addCommandHandler("start", this::startCommand);
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
