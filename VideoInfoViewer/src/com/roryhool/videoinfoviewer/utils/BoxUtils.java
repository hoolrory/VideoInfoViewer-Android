package com.roryhool.videoinfoviewer.utils;

import com.coremedia.iso.boxes.Box;
import com.googlecode.mp4parser.AbstractContainerBox;

public class BoxUtils {

   public static Box FindBox( AbstractContainerBox box, String type ) {
      for(Box childBox : box.getBoxes())
      {
         if ( childBox.getType().equals( type ) ) {
            return childBox;
         } else if ( childBox instanceof AbstractContainerBox ) {
            Box foundBox = FindBox( (AbstractContainerBox) childBox, type );
            
            if ( foundBox != null ) {
               return foundBox;
            }
         }
      }

      return null;
   }
}
