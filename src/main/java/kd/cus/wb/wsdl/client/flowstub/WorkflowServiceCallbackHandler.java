
/**
 * WorkflowServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.3  Built on : Jun 27, 2015 (11:17:49 BST)
 */

    package kd.cus.wb.wsdl.client.flowstub;

    /**
     *  WorkflowServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class WorkflowServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public WorkflowServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public WorkflowServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for getWorkflowNewFlag method
            * override this method for handling normal response from getWorkflowNewFlag operation
            */
           public void receiveResultgetWorkflowNewFlag(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetWorkflowNewFlagResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getWorkflowNewFlag operation
           */
            public void receiveErrorgetWorkflowNewFlag(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for writeWorkflowReadFlag method
            * override this method for handling normal response from writeWorkflowReadFlag operation
            */
           public void receiveResultwriteWorkflowReadFlag(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.WriteWorkflowReadFlagResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from writeWorkflowReadFlag operation
           */
            public void receiveErrorwriteWorkflowReadFlag(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getMyWorkflowRequestCount method
            * override this method for handling normal response from getMyWorkflowRequestCount operation
            */
           public void receiveResultgetMyWorkflowRequestCount(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetMyWorkflowRequestCountResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getMyWorkflowRequestCount operation
           */
            public void receiveErrorgetMyWorkflowRequestCount(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getCreateWorkflowTypeList method
            * override this method for handling normal response from getCreateWorkflowTypeList operation
            */
           public void receiveResultgetCreateWorkflowTypeList(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetCreateWorkflowTypeListResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCreateWorkflowTypeList operation
           */
            public void receiveErrorgetCreateWorkflowTypeList(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for deleteRequest method
            * override this method for handling normal response from deleteRequest operation
            */
           public void receiveResultdeleteRequest(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.DeleteRequestResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deleteRequest operation
           */
            public void receiveErrordeleteRequest(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getCCWorkflowRequestList method
            * override this method for handling normal response from getCCWorkflowRequestList operation
            */
           public void receiveResultgetCCWorkflowRequestList(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetCCWorkflowRequestListResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCCWorkflowRequestList operation
           */
            public void receiveErrorgetCCWorkflowRequestList(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getUserId method
            * override this method for handling normal response from getUserId operation
            */
           public void receiveResultgetUserId(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetUserIdResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getUserId operation
           */
            public void receiveErrorgetUserId(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getCreateWorkflowTypeCount method
            * override this method for handling normal response from getCreateWorkflowTypeCount operation
            */
           public void receiveResultgetCreateWorkflowTypeCount(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetCreateWorkflowTypeCountResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCreateWorkflowTypeCount operation
           */
            public void receiveErrorgetCreateWorkflowTypeCount(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getWorkflowRequestLogs method
            * override this method for handling normal response from getWorkflowRequestLogs operation
            */
           public void receiveResultgetWorkflowRequestLogs(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetWorkflowRequestLogsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getWorkflowRequestLogs operation
           */
            public void receiveErrorgetWorkflowRequestLogs(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for givingOpinions method
            * override this method for handling normal response from givingOpinions operation
            */
           public void receiveResultgivingOpinions(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GivingOpinionsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from givingOpinions operation
           */
            public void receiveErrorgivingOpinions(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getToDoWorkflowRequestCount method
            * override this method for handling normal response from getToDoWorkflowRequestCount operation
            */
           public void receiveResultgetToDoWorkflowRequestCount(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetToDoWorkflowRequestCountResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getToDoWorkflowRequestCount operation
           */
            public void receiveErrorgetToDoWorkflowRequestCount(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getProcessedWorkflowRequestList method
            * override this method for handling normal response from getProcessedWorkflowRequestList operation
            */
           public void receiveResultgetProcessedWorkflowRequestList(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetProcessedWorkflowRequestListResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getProcessedWorkflowRequestList operation
           */
            public void receiveErrorgetProcessedWorkflowRequestList(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getCCWorkflowRequestCount method
            * override this method for handling normal response from getCCWorkflowRequestCount operation
            */
           public void receiveResultgetCCWorkflowRequestCount(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetCCWorkflowRequestCountResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCCWorkflowRequestCount operation
           */
            public void receiveErrorgetCCWorkflowRequestCount(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for doForceOver method
            * override this method for handling normal response from doForceOver operation
            */
           public void receiveResultdoForceOver(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.DoForceOverResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from doForceOver operation
           */
            public void receiveErrordoForceOver(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for doCreateWorkflowRequest method
            * override this method for handling normal response from doCreateWorkflowRequest operation
            */
           public void receiveResultdoCreateWorkflowRequest(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.DoCreateWorkflowRequestResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from doCreateWorkflowRequest operation
           */
            public void receiveErrordoCreateWorkflowRequest(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getMyWorkflowRequestList method
            * override this method for handling normal response from getMyWorkflowRequestList operation
            */
           public void receiveResultgetMyWorkflowRequestList(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetMyWorkflowRequestListResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getMyWorkflowRequestList operation
           */
            public void receiveErrorgetMyWorkflowRequestList(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getCreateWorkflowRequestInfo method
            * override this method for handling normal response from getCreateWorkflowRequestInfo operation
            */
           public void receiveResultgetCreateWorkflowRequestInfo(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetCreateWorkflowRequestInfoResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCreateWorkflowRequestInfo operation
           */
            public void receiveErrorgetCreateWorkflowRequestInfo(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAllWorkflowRequestCount method
            * override this method for handling normal response from getAllWorkflowRequestCount operation
            */
           public void receiveResultgetAllWorkflowRequestCount(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetAllWorkflowRequestCountResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAllWorkflowRequestCount operation
           */
            public void receiveErrorgetAllWorkflowRequestCount(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getLeaveDays method
            * override this method for handling normal response from getLeaveDays operation
            */
           public void receiveResultgetLeaveDays(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetLeaveDaysResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getLeaveDays operation
           */
            public void receiveErrorgetLeaveDays(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getHendledWorkflowRequestCount method
            * override this method for handling normal response from getHendledWorkflowRequestCount operation
            */
           public void receiveResultgetHendledWorkflowRequestCount(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetHendledWorkflowRequestCountResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getHendledWorkflowRequestCount operation
           */
            public void receiveErrorgetHendledWorkflowRequestCount(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for forwardWorkflowRequest method
            * override this method for handling normal response from forwardWorkflowRequest operation
            */
           public void receiveResultforwardWorkflowRequest(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.ForwardWorkflowRequestResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from forwardWorkflowRequest operation
           */
            public void receiveErrorforwardWorkflowRequest(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getCreateWorkflowCount method
            * override this method for handling normal response from getCreateWorkflowCount operation
            */
           public void receiveResultgetCreateWorkflowCount(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetCreateWorkflowCountResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCreateWorkflowCount operation
           */
            public void receiveErrorgetCreateWorkflowCount(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getProcessedWorkflowRequestCount method
            * override this method for handling normal response from getProcessedWorkflowRequestCount operation
            */
           public void receiveResultgetProcessedWorkflowRequestCount(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetProcessedWorkflowRequestCountResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getProcessedWorkflowRequestCount operation
           */
            public void receiveErrorgetProcessedWorkflowRequestCount(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getCreateWorkflowList method
            * override this method for handling normal response from getCreateWorkflowList operation
            */
           public void receiveResultgetCreateWorkflowList(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetCreateWorkflowListResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCreateWorkflowList operation
           */
            public void receiveErrorgetCreateWorkflowList(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getWorkflowRequest method
            * override this method for handling normal response from getWorkflowRequest operation
            */
           public void receiveResultgetWorkflowRequest(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetWorkflowRequestResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getWorkflowRequest operation
           */
            public void receiveErrorgetWorkflowRequest(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAllWorkflowRequestList method
            * override this method for handling normal response from getAllWorkflowRequestList operation
            */
           public void receiveResultgetAllWorkflowRequestList(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetAllWorkflowRequestListResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAllWorkflowRequestList operation
           */
            public void receiveErrorgetAllWorkflowRequestList(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for forward2WorkflowRequest method
            * override this method for handling normal response from forward2WorkflowRequest operation
            */
           public void receiveResultforward2WorkflowRequest(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.Forward2WorkflowRequestResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from forward2WorkflowRequest operation
           */
            public void receiveErrorforward2WorkflowRequest(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for submitWorkflowRequest method
            * override this method for handling normal response from submitWorkflowRequest operation
            */
           public void receiveResultsubmitWorkflowRequest(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.SubmitWorkflowRequestResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from submitWorkflowRequest operation
           */
            public void receiveErrorsubmitWorkflowRequest(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getWorkflowRequest4Split method
            * override this method for handling normal response from getWorkflowRequest4Split operation
            */
           public void receiveResultgetWorkflowRequest4Split(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetWorkflowRequest4SplitResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getWorkflowRequest4Split operation
           */
            public void receiveErrorgetWorkflowRequest4Split(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getToDoWorkflowRequestList method
            * override this method for handling normal response from getToDoWorkflowRequestList operation
            */
           public void receiveResultgetToDoWorkflowRequestList(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetToDoWorkflowRequestListResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getToDoWorkflowRequestList operation
           */
            public void receiveErrorgetToDoWorkflowRequestList(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getHendledWorkflowRequestList method
            * override this method for handling normal response from getHendledWorkflowRequestList operation
            */
           public void receiveResultgetHendledWorkflowRequestList(
                    kd.cus.wb.wsdl.client.flowstub.WorkflowServiceStub.GetHendledWorkflowRequestListResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getHendledWorkflowRequestList operation
           */
            public void receiveErrorgetHendledWorkflowRequestList(java.lang.Exception e) {
            }
                


    }
    