package io.shmilyhe.rpc;

public interface AsyncRPCCallback {

    void success(Object result);

    void fail(Exception e);

}
