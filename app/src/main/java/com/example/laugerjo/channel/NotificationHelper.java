package com.example.laugerjo.channel;

import android.content.Context;
import android.content.ContextWrapper;

public class NotificationHelper  extends ContextWrapper {

        private static final String CHANNEL_ID ="com.example.laugerjo";
        private static final String CHANNEL_NAME ="LAUGERJO";

    public NotificationHelper(Context base) {
        super(base);
    }
}
