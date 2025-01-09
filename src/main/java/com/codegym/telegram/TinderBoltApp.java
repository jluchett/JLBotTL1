package com.codegym.telegram;

import java.util.ArrayList;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TinderBoltApp extends SimpleTelegramBot {

    public static final String TELEGRAM_BOT_TOKEN = "token-telg"; //añadir el token del bot entre comillas
    public static final String OPEN_AI_TOKEN = "token-ai"; //añadir el token de ChatGPT entre comillas

    private DialogMode mode;
    private ChatGPTService chatGPT = new ChatGPTService(OPEN_AI_TOKEN);
    private ArrayList<String> messages = new ArrayList<>();

    public TinderBoltApp() {
        super(TELEGRAM_BOT_TOKEN);
    }

    //escribiremos la funcionalidad principal del bot aquí
    public void hello() {
      if(mode == DialogMode.GPT){
        gptDialog();
        return;
      }else if (mode == DialogMode.DATE){
        dateDialog();
        return;
      }else if (mode == DialogMode.MESSAGE){
        messageDialog();
        return;
      }else if (mode == DialogMode.PROFILE){
        profileDialog();
        return;
      }else if (mode == DialogMode.OPENER){
        openerDialog();
        return;
      } else {
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

    public void dateCommand(){
      mode = DialogMode.DATE;
      String text = loadMessage("date");
      sendPhotoMessage("date");

      sendTextButtonsMessage(text, "date_grande", "Ariana Grande",
                                              "date_robbie", "Margot Robbie",
                                              "date_zendaya", "Zendaya",
                                              "date_gosling", "Ryan Gosling",
                                              "date_hardy", "Tom Hardy");
    }

    public void dateDialog(){
      String text = getMessageText();
      var myMessage = sendTextMessage("user is typing...");
      String answer = chatGPT.addMessage(text);
      //sendTextMessage(answer);
      updateTextMessage(myMessage, answer);
    }

    public void dateButton(){
      String key = getButtonKey();
      sendPhotoMessage(key);
      sendHtmlMessage(key);
      
      String prompt = loadPrompt(key);
      chatGPT.setPrompt(prompt);
    }

    public void messageCommand(){
      mode = DialogMode.MESSAGE;
      String text = loadMessage("message");
      sendPhotoMessage("message");
      sendTextButtonsMessage(text, "message_next", "write next message",
                                      "message_date", "Ask for a date");
      messages.clear();
    }

    public void messageButton(){
      String key = getButtonKey(); 
      String prompt = loadPrompt(key);
      String history = String.join("\n\n", messages);

      var myMessage = sendTextMessage("chatGPT is typing...");
      String answer = chatGPT.sendMessage(prompt, history);
      updateTextMessage(myMessage, answer);
    }

    public void messageDialog(){
      String text = getMessageText();
      messages.add(text);
    }

    public void profileCommand(){
      mode = DialogMode.PROFILE;
      String text = loadMessage("profile");
      sendPhotoMessage("profile");
      sendTextMessage(text);
      sendTextMessage("Introduce tu nombre");
      user = new UserInfo();
      questionIndex = 0;
    }

    private UserInfo user = new UserInfo();
    private int questionIndex = 0;

    public void profileDialog(){
      String text = getMessageText();
      questionIndex++;
      switch (questionIndex) {
        case 1:
          user.name = text;
          sendTextMessage(user.name + ", cual es tu edad?");
          break;
        case 2:
          user.age = text;
          sendTextMessage(user.name + ", cual es tu hobby?");
          break;
        case 3:
          user.hobby = text;
          sendTextMessage(user.name + ", cual es tu objetivo del perfil ?");
          break;
        default:
          user.goals = text;
          String prompt = loadPrompt("profile");
          String userInfo = user.toString();
          var myMessage = sendTextMessage("chatGPT is typing...");
          String answer = chatGPT.sendMessage(prompt, userInfo);
          updateTextMessage(myMessage, answer);
      } 
    }

    public void openerCommand(){
      mode = DialogMode.OPENER;
      String text = loadMessage("opener");
      sendPhotoMessage("opener");
      sendTextMessage(text);

      sendTextMessage("Introduce el nombre de la persona");
      user = new UserInfo();
      questionIndex = 0;
    }

    public void openerDialog(){
      String text = getMessageText();
      questionIndex++;
      switch (questionIndex) {
        case 1:
          user.name = text;
          sendTextMessage("Cual es la edad de " + user.name + "?");
          break;
        case 2:
          user.age = text;
          sendTextMessage("En que trabaja " + user.name + "?");
          break;
        case 3:
          user.occupation = text;
          sendTextMessage("Cual es el hobby de " + user.name + "?");
          break;
        default:
          user.hobby = text;
          String prompt = loadPrompt("opener");
          String userInfo = user.toString();
          var myMessage = sendTextMessage("chatGPT is typing...");
          String answer = chatGPT.sendMessage(prompt, userInfo);
          updateTextMessage(myMessage, answer);
      }
    }

    @Override
    public void onInitialize() {
        addMessageHandler(this::hello);
        //addButtonHandler("^.*", this::helloButton);
        addCommandHandler("start", this::startCommand);
        addCommandHandler("gpt", this::gptCommand);
        addCommandHandler("date", this::dateCommand);
        addButtonHandler("^date_.*", this::dateButton);
        addCommandHandler("message", this::messageCommand);
        addButtonHandler("^message_.*", this::messageButton);
        addCommandHandler("profile", this::profileCommand);
        addCommandHandler("opener", this::openerCommand);
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
