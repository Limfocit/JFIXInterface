package com.limfocit.jfixinterface.app;

import java.util.UUID;

import org.apache.log4j.Logger;

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
import quickfix.field.AggregatedBook;
import quickfix.field.EncryptMethod;
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
	
	private static final Logger log = Logger.getLogger(JFIXApplication.class);
	
	private final String Username;
	private final String Password;
	
	private SessionID SessionId;
	private Session MySession;
	 private Object SessionLock = new Object();
	
	public JFIXApplication(String username, String password) {
		super();		
		Username = username;
		Password = password;
	}
	
	public boolean subscribeData() {
		synchronized (SessionLock) {
			if (MySession == null) return false;
		}		
		log.debug("subscribe data");
		MarketDataRequest mes = new MarketDataRequest(
				new MDReqID(UUID.randomUUID().toString()), 
				new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES), 
				new MarketDepth(1));
		mes.set(new MDUpdateType(MDUpdateType.INCREMENTAL_REFRESH));
		mes.set(new AggregatedBook(true));
		MarketDataRequest.NoRelatedSym relatedSymbols = new MarketDataRequest.NoRelatedSym();
		relatedSymbols.set(new Symbol("EUR/USD"));
		mes.addGroup(relatedSymbols);
		MarketDataRequest.NoMDEntryTypes entryTypes = new MarketDataRequest.NoMDEntryTypes();
		entryTypes.set(new MDEntryType(MDEntryType.BID));
		mes.addGroup(entryTypes);
		entryTypes = new MarketDataRequest.NoMDEntryTypes();
		entryTypes.set(new MDEntryType(MDEntryType.OFFER));
		mes.addGroup(entryTypes);
		MySession.send(mes);
		return true;
	}

	@Override
	public void fromAdmin(Message message, SessionID arg1) throws FieldNotFound,
			IncorrectDataFormat, IncorrectTagValue, RejectLogon {		
		if (message.isSetField(35) && message.getInt(35) != 0) log.debug("fromAdmin " + message);
	}	

	@Override
	public void fromApp(final Message message, final SessionID sessionId) throws FieldNotFound,
			IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		crack(message, sessionId);
	}

	@Override
	public void onCreate(SessionID sessionID) {
		log.debug("create " + sessionID.getSenderCompID());
	}

	@Override
	public void onLogon(final SessionID sessionId) {
		SessionId = sessionId;
		synchronized (SessionLock) {
			MySession = Session.lookupSession(sessionId);			
		}
		log.debug("logon " + SessionId + "  " + MySession + "  is null=" + (MySession == null));
	}

	@Override
	public void onLogout(final SessionID sessionId) {
		log.debug("logoout " + sessionId);
	}

	@Override
	public void toAdmin(final Message message, final SessionID sessionId) {
		log.debug("toAdmin");
		try {
			if (message.getHeader().getField(new MsgType()).getValue().equals(MsgType.LOGON)) {
			    message.setField(new Username(Username));
			    message.setField(new Password(Password));
			    message.setField(new ResetSeqNumFlag(true));
			    message.setField(new EncryptMethod(EncryptMethod.PGP_DES_MD5));
			} 
		} catch (FieldNotFound exFileNotFound) {
			exFileNotFound.printStackTrace();
		}
	}

	@Override
	public void toApp(Message message, SessionID sessionId) throws DoNotSend {
		// TODO Auto-generated method stub
		log.debug("toApp");
	}
	
	public SessionID getSessionID() {
		return SessionId;
	}	
	
	public void onMessage(MarketDataIncrementalRefresh message, SessionID sessionID) {
		log.debug("increment");
	}
	
	public void onMessage(MarketDataSnapshotFullRefresh message, SessionID sessionID) {
		log.debug("full");
	}
	
	public void onMessage(MarketDataRequestReject message, SessionID sessionID) {
		log.debug("reject mdr " + message);
	}
	
	public void onMessage(quickfix.fix42.Reject message, SessionID sessionID) {
		log.debug("reject mess");
	}
	
	public void onMessage(quickfix.fix42.TradingSessionStatus message, SessionID sessionID) {
		try {log.debug("trading session status " + message.getTradSesStatus());} catch (FieldNotFound e) {	log.error("Trying to get trading session status", e);}
	}
}

