/**
   Copyright (c) 2016 Rory Hool
   
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

package com.roryhool.videoinfoviewer;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Bus;

public class VideoInfoViewerApp extends Application {

   private static Context sContext;

   private static Bus sBus = new Bus();

   public static Context getContext() {
      return sContext;
   }

   public static Bus getBus() {
      return sBus;
   }

   @Override
   public void onCreate() {
      super.onCreate();

      sContext = this;
   }

   private static Tracker mTracker;

   /**
    * Gets the default {@link Tracker} for this {@link Application}.
    * @return tracker
    */
   synchronized public static Tracker getDefaultTracker() {
      if ( mTracker == null ) {
         GoogleAnalytics analytics = GoogleAnalytics.getInstance( getContext() );
         // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
         mTracker = analytics.newTracker( R.xml.global_tracker );
      }
      return mTracker;
   }

}
