package com.cp.plugin.event;

public interface SubEvent {

    void onMessage(String tag, String content);

    void onError(Throwable throwable);


}
