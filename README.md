JFIXInterface
=============

Java class for interecting with broker's server via FIX protocol(using QuickFIXJ - Java FIX engine implemetation).<br/>
Future methods:

- void **auth**(String URI, String pathToSertificate )
- void **closePosition**(String marginPositionId)
- void **removeOrder**(String EPSOrderId)
- EPSOrder **addOrder**(String orderType, double price, double volume)
