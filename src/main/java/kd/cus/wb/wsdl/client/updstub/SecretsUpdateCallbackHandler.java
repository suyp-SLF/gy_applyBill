
/**
 * SecretsUpdateCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.3  Built on : Jun 27, 2015 (11:17:49 BST)
 */

    package kd.cus.wb.wsdl.client.updstub;

    /**
     *  SecretsUpdateCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class SecretsUpdateCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public SecretsUpdateCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public SecretsUpdateCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for getDeptIdByCode method
            * override this method for handling normal response from getDeptIdByCode operation
            */
           public void receiveResultgetDeptIdByCode(
                    kd.cus.wb.wsdl.client.updstub.SecretsUpdateStub.GetDeptIdByCodeResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getDeptIdByCode operation
           */
            public void receiveErrorgetDeptIdByCode(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for updateSecrets method
            * override this method for handling normal response from updateSecrets operation
            */
           public void receiveResultupdateSecrets(
                    kd.cus.wb.wsdl.client.updstub.SecretsUpdateStub.UpdateSecretsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from updateSecrets operation
           */
            public void receiveErrorupdateSecrets(java.lang.Exception e) {
            }
                


    }
    