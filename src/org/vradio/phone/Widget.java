package org.vradio.phone;

import org.vradio.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	Log.v("on Update","ids"+appWidgetIds);
    	
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, Start.class);
            Bundle e=new Bundle();
            e.putString("stop", "all");
            intent.putExtras(e);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.button_w1, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current App Widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
       
        
        
    }
    @Override 
    public void onReceive(Context context, Intent intent) { 
    	Log.v("vradioconfig","onReceive"+intent);
        final String action = intent.getAction(); 
        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) { 
            final int appWidgetId = intent.getExtras().getInt 
    (AppWidgetManager.EXTRA_APPWIDGET_ID, 
                    AppWidgetManager.INVALID_APPWIDGET_ID); 
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) { 
                this.onDeleted(context, new int[] { appWidgetId }); 
            } 
        } else { 
            super.onReceive(context, intent); 
        } 
    } 
}
