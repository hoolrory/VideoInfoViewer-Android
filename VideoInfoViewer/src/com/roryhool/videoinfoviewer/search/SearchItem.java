package com.roryhool.videoinfoviewer.search;

import com.coremedia.iso.boxes.Box;

public class SearchItem {

   private enum SearchItemType {
      BOX, FIELD
   }

   private SearchItemType mType;

   private String mTitle;
   private String mDescription;

   public static void CreateFromBox( Box box ) {

   }

   public static void CreateFromField( String field, String value, Box box ) {

   }
}
