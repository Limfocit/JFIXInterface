package com.limfocit.jfixinterface.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.limfocit.jfixinterface.JFIXApplication;

import quickfix.Acceptor;
import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.LogFactory;
import quickfix.MessageStoreFactory;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.MessageFactory;


public class Main {
	
	public static void main(String[] args) {		
		try {
		    String fileName = "src\\main\\resources\\settings.ini";
	
		    JFIXApplication application = new JFIXApplication("fxddi23728", "fxdd1234");
	
		    SessionSettings settings = new SessionSettings(new FileInputStream(fileName));
		    MessageStoreFactory storeFactory = new FileStoreFactory(settings);
		    LogFactory logFactory = new FileLogFactory(settings);
		    MessageFactory messageFactory = new DefaultMessageFactory();
		    Acceptor acceptor = new SocketAcceptor(application, storeFactory, settings, logFactory, messageFactory);
		    acceptor.start();
		    while (application.subscribeData()) {}
		    System.out.println("sub data");
		    try {
				System.in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    acceptor.stop();
		} catch (FileNotFoundException | ConfigError e) {
			e.printStackTrace();
		}
	}

}
