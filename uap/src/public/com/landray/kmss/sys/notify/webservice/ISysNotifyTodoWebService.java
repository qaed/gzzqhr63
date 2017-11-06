/**
 * ISysNotifyTodoWebService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.landray.kmss.sys.notify.webservice;

public interface ISysNotifyTodoWebService extends java.rmi.Remote {
    public com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult updateTodo(com.landray.kmss.sys.notify.webservice.NotifyTodoUpdateContext arg0) throws java.rmi.RemoteException, com.landray.kmss.sys.notify.webservice.Exception;
    public com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult setTodoDone(com.landray.kmss.sys.notify.webservice.NotifyTodoRemoveContext arg0) throws java.rmi.RemoteException, com.landray.kmss.sys.notify.webservice.Exception;
    public com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult getTodo(com.landray.kmss.sys.notify.webservice.NotifyTodoGetContext arg0) throws java.rmi.RemoteException, com.landray.kmss.sys.notify.webservice.Exception;
    public com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult sendTodo(com.landray.kmss.sys.notify.webservice.NotifyTodoSendContext arg0) throws java.rmi.RemoteException, com.landray.kmss.sys.notify.webservice.Exception;
    public com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult deleteTodo(com.landray.kmss.sys.notify.webservice.NotifyTodoRemoveContext arg0) throws java.rmi.RemoteException, com.landray.kmss.sys.notify.webservice.Exception;
}
