package com.roryhool.videoinfoviewer.analytics;

import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class Analytics {

   private static Analytics sInstance;

   public static Analytics Instance( Context context ) {
      if ( sInstance == null ) {
         sInstance = new Analytics( context );
      }

      return sInstance;
   }

   Context mContext;

   public Analytics( Context context ) {
      mContext = context;
   }

   public void LogEvent( String category, String action ) {
      LogEvent( category, action, null, 0 );
   }

   public void LogEvent( String category, String action, String label ) {
      LogEvent( category, action, label, 0 );
   }

   public void LogEvent( String category, String action, String label, long value ) {
      EasyTracker easyTracker = EasyTracker.getInstance( mContext );

      if ( easyTracker == null ) {
         return;
      }

      // MapBuilder.createEvent().build() returns a Map of event fields and values
      // that are set and sent with the hit.
      easyTracker.send(MapBuilder
          .createEvent(category,    // Event category (required)
                       action,      // Event action (required)
                       label,       // Event label
                       value)       // Event value
            .build() );
   }
}
