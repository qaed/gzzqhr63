package com.landray.kmss.sys.notify.webservice;

public class ISysNotifyTodoWebServiceProxy implements com.landray.kmss.sys.notify.webservice.ISysNotifyTodoWebService {
  private String _endpoint = null;
  private com.landray.kmss.sys.notify.webservice.ISysNotifyTodoWebService iSysNotifyTodoWebService = null;
  
  public ISysNotifyTodoWebServiceProxy() {
    _initISysNotifyTodoWebServiceProxy();
  }
  
  public ISysNotifyTodoWebServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initISysNotifyTodoWebServiceProxy();
  }
  
  private void _initISysNotifyTodoWebServiceProxy() {
    try {
      iSysNotifyTodoWebService = (new com.landray.kmss.sys.notify.webservice.ISysNotifyTodoWebServiceServiceLocator()).getISysNotifyTodoWebServicePort();
      if (iSysNotifyTodoWebService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iSysNotifyTodoWebService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iSysNotifyTodoWebService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iSysNotifyTodoWebService != null)
      ((javax.xml.rpc.Stub)iSysNotifyTodoWebService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.landray.kmss.sys.notify.webservice.ISysNotifyTodoWebService getISysNotifyTodoWebService() {
    if (iSysNotifyTodoWebService == null)
      _initISysNotifyTodoWebServiceProxy();
    return iSysNotifyTodoWebService;
  }
  
  public com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult updateTodo(com.landray.kmss.sys.notify.webservice.NotifyTodoUpdateContext arg0) throws java.rmi.RemoteException, com.landray.kmss.sys.notify.webservice.Exception{
    if (iSysNotifyTodoWebService == null)
      _initISysNotifyTodoWebServiceProxy();
    return iSysNotifyTodoWebService.updateTodo(arg0);
  }
  
  public com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult setTodoDone(com.landray.kmss.sys.notify.webservice.NotifyTodoRemoveContext arg0) throws java.rmi.RemoteException, com.landray.kmss.sys.notify.webservice.Exception{
    if (iSysNotifyTodoWebService == null)
      _initISysNotifyTodoWebServiceProxy();
    return iSysNotifyTodoWebService.setTodoDone(arg0);
  }
  
  public com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult getTodo(com.landray.kmss.sys.notify.webservice.NotifyTodoGetContext arg0) throws java.rmi.RemoteException, com.landray.kmss.sys.notify.webservice.Exception{
    if (iSysNotifyTodoWebService == null)
      _initISysNotifyTodoWebServiceProxy();
    return iSysNotifyTodoWebService.getTodo(arg0);
  }
  
  public com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult sendTodo(com.landray.kmss.sys.notify.webservice.NotifyTodoSendContext arg0) throws java.rmi.RemoteException, com.landray.kmss.sys.notify.webservice.Exception{
    if (iSysNotifyTodoWebService == null)
      _initISysNotifyTodoWebServiceProxy();
    return iSysNotifyTodoWebService.sendTodo(arg0);
  }
  
  public com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult deleteTodo(com.landray.kmss.sys.notify.webservice.NotifyTodoRemoveContext arg0) throws java.rmi.RemoteException, com.landray.kmss.sys.notify.webservice.Exception{
    if (iSysNotifyTodoWebService == null)
      _initISysNotifyTodoWebServiceProxy();
    return iSysNotifyTodoWebService.deleteTodo(arg0);
  }
  
  
}