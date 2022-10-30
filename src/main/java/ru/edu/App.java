package ru.edu;

import ru.edu.service.OnlineSimProvider;

public class App {
    public static void main(String[] args) {

        OnlineSimProvider provider = new OnlineSimProvider();
        provider.getInfo();
        provider.writeFile();

    }
}
