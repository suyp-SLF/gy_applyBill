
/**
 * DocServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.3  Built on : Jun 27, 2015 (11:17:49 BST)
 */

    package kd.cus.wb.wsdl.client.stub;

    /**
     *  DocServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class DocServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public DocServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public DocServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for getDoc method
            * override this method for handling normal response from getDoc operation
            */
           public void receiveResultgetDoc(
                    kd.cus.wb.wsdl.client.stub.DocServiceStub.GetDocResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getDoc operation
           */
            public void receiveErrorgetDoc(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getDocCount method
            * override this method for handling normal response from getDocCount operation
            */
           public void receiveResultgetDocCount(
                    kd.cus.wb.wsdl.client.stub.DocServiceStub.GetDocCountResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getDocCount operation
           */
            public void receiveErrorgetDocCount(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for login method
            * override this method for handling normal response from login operation
            */
           public void receiveResultlogin(
                    kd.cus.wb.wsdl.client.stub.DocServiceStub.LoginResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from login operation
           */
            public void receiveErrorlogin(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for updateDoc method
            * override this method for handling normal response from updateDoc operation
            */
           public void receiveResultupdateDoc(
                    kd.cus.wb.wsdl.client.stub.DocServiceStub.UpdateDocResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from updateDoc operation
           */
            public void receiveErrorupdateDoc(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for deleteDoc method
            * override this method for handling normal response from deleteDoc operation
            */
           public void receiveResultdeleteDoc(
                    kd.cus.wb.wsdl.client.stub.DocServiceStub.DeleteDocResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deleteDoc operation
           */
            public void receiveErrordeleteDoc(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for createDoc method
            * override this method for handling normal response from createDoc operation
            */
           public void receiveResultcreateDoc(
                    kd.cus.wb.wsdl.client.stub.DocServiceStub.CreateDocResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from createDoc operation
           */
            public void receiveErrorcreateDoc(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getList method
            * override this method for handling normal response from getList operation
            */
           public void receiveResultgetList(
                    kd.cus.wb.wsdl.client.stub.DocServiceStub.GetListResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getList operation
           */
            public void receiveErrorgetList(java.lang.Exception e) {
            }
                


    }
    