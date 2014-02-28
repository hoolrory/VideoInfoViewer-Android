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
