package com.limfocit.jfixinterface;

import java.util.UUID;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.Session;
import quickfix.fix42.MarketDataIncrementalRefresh;
import quickfix.fix42.MarketDataRequest;
import quickfix.fix42.MarketDataRequestReject;
import quickfix.fix42.MarketDataSnapshotFullRefresh;
import quickfix.fix42.MessageCracker;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.MDEntryType;
import quickfix.field.MDReqID;
import quickfix.field.MDUpdateType;
import quickfix.field.MarketDepth;
import quickfix.field.MsgType;
import quickfix.field.ResetSeqNumFlag;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.field.Username;
import quickfix.field.Password;

public class JFIXApplication extends MessageCracker implements Application {
	
	private final String Username;
	private final String Password;
	
	private SessionID SessionId;
	private Session MySession;
	
	public JFIXApplication(String username, String password) {
		super();		
		Username = username;
		Password = password;
	}
	
	public boolean subscribeData() {
		if (MySession == null) return false;
		MarketDataRequest mes = new MarketDataRequest(
				new MDReqID(UUID.randomUUID().toString()), 
				new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES), 
				new MarketDepth(1));
		mes.set(new MDUpdateType(MDUpdateType.INCREMENTAL_REFRESH));
		MarketDataRequest.NoRelatedSym relatedSymbols = new MarketDataRequest.NoRelatedSym();
		relatedSymbols.set(new Symbol("EUR/USD"));
		mes.addGroup(relatedSymbols);
		MarketDataRequest.NoMDEntryTypes entryTypes = new MarketDataRequest.NoMDEntryTypes();
		entryTypes.set(new MDEntryType(MDEntryType.TRADE));
		mes.addGroup(entryTypes);
		MySession.send(mes);
		return true;
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
	public void onCreate(SessionID sessionID) {
		 Session.lookupSession(sessionID).getLog().onEvent("Valid order types: ");
	}

	@Override
	public void onLogon(final SessionID sessionId) {
		SessionId = sessionId;
		MySession = Session.lookupSession(sessionId);
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
	
	public SessionID getSessionID() {
		return SessionId;
	}	
	
	public void onMessage(MarketDataIncrementalRefresh message, SessionID sessionID) {
		System.out.println("increment");
	}
	
	public void onMessage(MarketDataSnapshotFullRefresh message, SessionID sessionID) {
		System.out.println("full");
	}
	
	public void onMessage(MarketDataRequestReject message, SessionID sessionID) {
		System.out.println("reject");
	}
}

