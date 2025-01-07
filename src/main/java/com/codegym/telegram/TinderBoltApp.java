package com.codegym.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TinderBoltApp extends SimpleTelegramBot {

    public static final String TELEGRAM_BOT_TOKEN = "token-tel"; //añadir el token del bot entre comillas
    public static final String OPEN_AI_TOKEN = "token-opa"; //añadir el token de ChatGPT entre comillas

    private DialogMode mode;
    private ChatGPTService chatGPT = new ChatGPTService(OPEN_AI_TOKEN);

    public TinderBoltApp() {
        super(TELEGRAM_BOT_TOKEN);
    }

    //escribiremos la funcionalidad principal del bot aquí
    public void hello() {
      if(mode == DialogMode.GPT){
        gptDialog();
        return;
      }else {
        String text = getMessageText();
        sendTextMessage("*Hello world*");
        sendTextMessage("_How are you?_");
        sendTextMessage("You wrote: " + text);

        sendPhotoMessage("avatar_main");

        sendTextButtonsMessage("Launch process", "start", "Start", "stop", "Stop");
      }
      
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
      mode = DialogMode.MAIN;
      String text = loadMessage("main");
      sendPhotoMessage("main");
      sendTextMessage(text);

      showMainMenu("start", "menu principal", 
                               "profile", "generación de perfil de Tinder 😎",
                               "opener", "mensaje para iniciar conversación 🥰",
                               "message", "correspondencia en su nombre 😈",
                               "date", "correspondencia con celebridades 🔥",
                               "gpt", "hacer una pregunta a chat GPT 🧠");
    }

    public void gptCommand(){
      mode = DialogMode.GPT;
      String text = loadMessage("gpt");
      sendPhotoMessage("gpt");
      sendTextMessage(text);
    }

    public void gptDialog(){
      String text = getMessageText();
      String prompt = loadPrompt("gpt");
      String answer = chatGPT.sendMessage(prompt , text);
      sendTextMessage(answer);
    }

    @Override
    public void onInitialize() {
        //y un poco más aquí :)
        addMessageHandler(this::hello);
        addButtonHandler("^.*", this::helloButton);
        addCommandHandler("start", this::startCommand);
        addCommandHandler("gpt", this::gptCommand);
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
