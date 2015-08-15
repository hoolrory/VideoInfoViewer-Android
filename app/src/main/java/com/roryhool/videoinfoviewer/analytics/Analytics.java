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

import com.google.android.gms.analytics.HitBuilders;
import com.roryhool.videoinfoviewer.VideoInfoViewerApp;

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
      VideoInfoViewerApp.getDefaultTracker().send(
              new HitBuilders.EventBuilder()
                      .setCategory( category )
                      .setAction( action )
                      .setLabel( label )
                      .setValue( value )
                      .build() );
   }
}
