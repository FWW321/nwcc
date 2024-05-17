package fww.regular.proxy;

import fww.regular.Interface.FailedFunction;
import fww.regular.Interface.SuccessFunction;

public class ActionProxy {
    private SuccessFunction successFunction;
    private FailedFunction failedFunction;

    public ActionProxy onSuccess(SuccessFunction successFunction) {
        this.successFunction = successFunction;
        return this;
    }

    public ActionProxy onFailed(FailedFunction failedFunction) {
        this.failedFunction = failedFunction;
        return this;
    }

    public void success(String result, int line) {
        if(successFunction != null){
            successFunction.success(result, line);
        }
    }

    public void failed(String s, int line) {
        if(failedFunction != null){
            failedFunction.failed(s, line);
        }
    }
}

