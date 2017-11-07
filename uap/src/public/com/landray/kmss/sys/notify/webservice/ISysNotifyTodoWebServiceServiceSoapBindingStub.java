/**
 * ISysNotifyTodoWebServiceServiceSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.landray.kmss.sys.notify.webservice;

import org.apache.axis.message.SOAPHeaderElement;

import com.gzws.webservice.client.WebServiceConfig;

public class ISysNotifyTodoWebServiceServiceSoapBindingStub extends org.apache.axis.client.Stub implements com.landray.kmss.sys.notify.webservice.ISysNotifyTodoWebService {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[5];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("updateTodo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "notifyTodoUpdateContext"), com.landray.kmss.sys.notify.webservice.NotifyTodoUpdateContext.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "notifyTodoAppResult"));
        oper.setReturnClass(com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "Exception"),
                      "com.landray.kmss.sys.notify.webservice.Exception",
                      new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "Exception"), 
                      true
                     ));
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("setTodoDone");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "notifyTodoRemoveContext"), com.landray.kmss.sys.notify.webservice.NotifyTodoRemoveContext.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "notifyTodoAppResult"));
        oper.setReturnClass(com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "Exception"),
                      "com.landray.kmss.sys.notify.webservice.Exception",
                      new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "Exception"), 
                      true
                     ));
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTodo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "notifyTodoGetContext"), com.landray.kmss.sys.notify.webservice.NotifyTodoGetContext.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "notifyTodoAppResult"));
        oper.setReturnClass(com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "Exception"),
                      "com.landray.kmss.sys.notify.webservice.Exception",
                      new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "Exception"), 
                      true
                     ));
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("sendTodo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "notifyTodoSendContext"), com.landray.kmss.sys.notify.webservice.NotifyTodoSendContext.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "notifyTodoAppResult"));
        oper.setReturnClass(com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "Exception"),
                      "com.landray.kmss.sys.notify.webservice.Exception",
                      new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "Exception"), 
                      true
                     ));
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteTodo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "arg0"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "notifyTodoRemoveContext"), com.landray.kmss.sys.notify.webservice.NotifyTodoRemoveContext.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "notifyTodoAppResult"));
        oper.setReturnClass(com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "Exception"),
                      "com.landray.kmss.sys.notify.webservice.Exception",
                      new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "Exception"), 
                      true
                     ));
        _operations[4] = oper;

    }

    public ISysNotifyTodoWebServiceServiceSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public ISysNotifyTodoWebServiceServiceSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public ISysNotifyTodoWebServiceServiceSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "Exception");
            cachedSerQNames.add(qName);
            cls = com.landray.kmss.sys.notify.webservice.Exception.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "notifyTodoAppResult");
            cachedSerQNames.add(qName);
            cls = com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "notifyTodoGetContext");
            cachedSerQNames.add(qName);
            cls = com.landray.kmss.sys.notify.webservice.NotifyTodoGetContext.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "notifyTodoRemoveContext");
            cachedSerQNames.add(qName);
            cls = com.landray.kmss.sys.notify.webservice.NotifyTodoRemoveContext.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "notifyTodoSendContext");
            cachedSerQNames.add(qName);
            cls = com.landray.kmss.sys.notify.webservice.NotifyTodoSendContext.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "notifyTodoUpdateContext");
            cachedSerQNames.add(qName);
            cls = com.landray.kmss.sys.notify.webservice.NotifyTodoUpdateContext.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            //hezy
            WebServiceConfig cfg = WebServiceConfig.getInstance();
            _call.addHeader(new SOAPHeaderElement("http://sys.webservice.client","tns:user",cfg.getUser()));
            _call.addHeader(new SOAPHeaderElement("http://sys.webservice.client","tns:password",cfg.getPassword()));
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult updateTodo(com.landray.kmss.sys.notify.webservice.NotifyTodoUpdateContext arg0) throws java.rmi.RemoteException, com.landray.kmss.sys.notify.webservice.Exception {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "updateTodo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {arg0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof com.landray.kmss.sys.notify.webservice.Exception) {
              throw (com.landray.kmss.sys.notify.webservice.Exception) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult setTodoDone(com.landray.kmss.sys.notify.webservice.NotifyTodoRemoveContext arg0) throws java.rmi.RemoteException, com.landray.kmss.sys.notify.webservice.Exception {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "setTodoDone"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {arg0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof com.landray.kmss.sys.notify.webservice.Exception) {
              throw (com.landray.kmss.sys.notify.webservice.Exception) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult getTodo(com.landray.kmss.sys.notify.webservice.NotifyTodoGetContext arg0) throws java.rmi.RemoteException, com.landray.kmss.sys.notify.webservice.Exception {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "getTodo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {arg0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof com.landray.kmss.sys.notify.webservice.Exception) {
              throw (com.landray.kmss.sys.notify.webservice.Exception) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult sendTodo(com.landray.kmss.sys.notify.webservice.NotifyTodoSendContext arg0) throws java.rmi.RemoteException, com.landray.kmss.sys.notify.webservice.Exception {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "sendTodo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {arg0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof com.landray.kmss.sys.notify.webservice.Exception) {
              throw (com.landray.kmss.sys.notify.webservice.Exception) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult deleteTodo(com.landray.kmss.sys.notify.webservice.NotifyTodoRemoveContext arg0) throws java.rmi.RemoteException, com.landray.kmss.sys.notify.webservice.Exception {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://webservice.notify.sys.kmss.landray.com/", "deleteTodo"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {arg0});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult) org.apache.axis.utils.JavaUtils.convert(_resp, com.landray.kmss.sys.notify.webservice.NotifyTodoAppResult.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof com.landray.kmss.sys.notify.webservice.Exception) {
              throw (com.landray.kmss.sys.notify.webservice.Exception) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

}
