package com.limfocit.jfixinterface;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgType;
import quickfix.field.ResetSeqNumFlag;
import quickfix.field.Username;
import quickfix.field.Password;

public class JFIXApplication extends MessageCracker implements Application {
	
	private final String Username;
	private final String Password;
	
	private SessionID SessionId;
	
	public JFIXApplication(String username, String password) {
		super();		
		Username = username;
		Password = password;
	}

	@Override
	public void fromAdmin(Message arg0, SessionID arg1) throws FieldNotFound,
			IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fromApp(final Message message, final SessionID sessionId) throws FieldNotFound,
			IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		crack(message, sessionId);
	}

	@Override
	public void onCreate(SessionID arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLogon(final SessionID sessionId) {
		SessionId = sessionId;
	}

	@Override
	public void onLogout(final SessionID sessionId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toAdmin(final Message message, final SessionID sessionId) {
		try {
			if (message.getHeader().getField(new MsgType()).getValue().equals(MsgType.LOGON)) {
			    message.setField(new Username(Username));
			    message.setField(new Password(Password));
			    message.setField(new ResetSeqNumFlag(true));
			}
		} catch (FieldNotFound exFileNotFound) {
			exFileNotFound.printStackTrace();
		}
	}

	@Override
	public void toApp(Message arg0, SessionID arg1) throws DoNotSend {
		// TODO Auto-generated method stub
		
	}

}
