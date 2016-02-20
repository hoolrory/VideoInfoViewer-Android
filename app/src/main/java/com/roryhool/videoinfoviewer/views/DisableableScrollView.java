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

package com.roryhool.videoinfoviewer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class DisableableScrollView extends ScrollView {

   boolean mEnabled = true;

   public DisableableScrollView( Context context ) {
      super( context );

   }

   public DisableableScrollView( Context context, AttributeSet attrs ) {
      super( context, attrs );

   }

   public DisableableScrollView( Context context, AttributeSet attrs, int defStyle ) {
      super( context, attrs, defStyle );

   }

   public void setEnabled( boolean enabled ) {
      mEnabled = enabled;
   }

   @Override
   public boolean onTouchEvent( MotionEvent event ) {
      if ( mEnabled ) {
         return super.onTouchEvent( event );
      } else {
         return false;
      }
   }

   @Override
   public boolean onInterceptTouchEvent( MotionEvent event ) {
      if ( mEnabled ) {
         return super.onInterceptTouchEvent( event );
      } else {
         return false;
      }
   }

}
