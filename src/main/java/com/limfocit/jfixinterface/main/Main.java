package com.limfocit.jfixinterface.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.limfocit.jfixinterface.JFIXApplication;

import quickfix.Acceptor;
import quickfix.Application;
import quickfix.ConfigError;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.LogFactory;
import quickfix.MessageStoreFactory;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.fix44.MessageFactory;


public class Main {
	
	public static void main(String[] args) {		
		try {
		    String fileName = "src\\main\\resources\\settings.ini";
	
		    Application application = new JFIXApplication("", "");
	
		    SessionSettings settings = new SessionSettings(new FileInputStream(fileName));
		    MessageStoreFactory storeFactory = new FileStoreFactory(settings);
		    LogFactory logFactory = new FileLogFactory(settings);
		    MessageFactory messageFactory = new MessageFactory();
		    Acceptor acceptor = new SocketAcceptor(application, storeFactory, settings, logFactory, messageFactory);
		    acceptor.start();
		    acceptor.stop();
		} catch (FileNotFoundException | ConfigError e) {
			e.printStackTrace();
		}
	}

}
