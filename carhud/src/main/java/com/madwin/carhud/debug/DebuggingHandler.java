package com.madwin.carhud.debug;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Andrew on 5/27/2014.
 */
public class DebuggingHandler {

    public static void dumpIntent(Intent i) {
        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            Log.e("carhud", "******Dumping Intent start********");
            while (it.hasNext()) {
                String key = it.next();
                Log.e("carhud", "[" + key + " = " + bundle.get(key) + "]");
            }
            Log.e("carhud", "******Dumping intent ended*******");
        }
    }

}
