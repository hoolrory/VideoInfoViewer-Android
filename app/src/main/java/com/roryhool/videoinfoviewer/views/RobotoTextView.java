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
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.roryhool.videoinfoviewer.utils.FontManager;

public class RobotoTextView extends TextView {

   public RobotoTextView( Context context ) {
      super( context );
   }

   public RobotoTextView( Context context, AttributeSet attrs ) {
      super( context, attrs );
   }

   public RobotoTextView( Context context, AttributeSet attrs, int defStyle ) {
      super( context, attrs, defStyle );
   }

   @Override
   public void setTypeface( Typeface tf, int style ) {

      Typeface face = tf;

      if ( style == Typeface.BOLD ) {
         face = FontManager.get( getContext() ).getRobotoBold();
      } else if ( style == Typeface.ITALIC ) {
         face = FontManager.get( getContext() ).getRobotoLight();
      } else {
         face = FontManager.get( getContext() ).getRobotoRegular();
      }

      super.setTypeface( face );
   }
}
