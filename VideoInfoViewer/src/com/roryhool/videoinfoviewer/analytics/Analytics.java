/**
   Copyright (c) 2014 Rory Hool
   
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
   
       http://www.apache.org/licenses/LICENSE-2.0
   
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 **/

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
