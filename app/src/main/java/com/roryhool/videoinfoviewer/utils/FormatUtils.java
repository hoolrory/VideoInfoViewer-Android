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

package com.roryhool.videoinfoviewer.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtils {

   public static String ZULU_DATE_FORMAT = "yyyyMMdd'T'HHmmss.SSS'Z'";

   public static String DISPLAY_DATE_FORMAT = "yyyy-MM-dd-yy HH:mm aa";

   public static String FormatZuluDateTimeForDisplay( String zuluTime ) {

      if ( zuluTime == null ) {
          return "N/A";
      }
      
      DateFormat zuluFormat = new SimpleDateFormat( ZULU_DATE_FORMAT, Locale.US );
      zuluFormat.setTimeZone( java.util.TimeZone.getTimeZone( "Zulu" ) );
      
      DateFormat displayFormat = new SimpleDateFormat( DISPLAY_DATE_FORMAT, Locale.US );
      String dateString = zuluTime;
      try {
         Date date = zuluFormat.parse( zuluTime );
         dateString = displayFormat.format( date );
      } catch ( ParseException e ) {
         e.printStackTrace();
      }
      
      return dateString;
   }

   public static String FormatTimeForDisplay( long ms ) {

      long s = ( ms / 1000 ) % 60;
      long m = ms / ( 1000 * 60 ) % 60;
      long h = ms / ( 1000 * 60 * 60 );

      String sString = s < 10 ? "0" + s : "" + s;
      String mString = m < 10 ? "0" + m : "" + m;
      String hString = h < 10 ? "0" + h : "" + h;

      String durationString = String.format( Locale.US, "%s:%s", mString, sString );

      if ( h != 0 ) {
         durationString = String.format( Locale.US, "%s:%s:%s", hString, mString, sString );
      }

      return durationString;
   }

   public static String FormatFileSizeForDisplay( float fileSize ) {

      return String.format( Locale.US, "%.2f MB", fileSize / ( 1024F * 1024F ) );
   }

   public static String FormatBpsForDisplay( long parseLong ) {

      return String.format( Locale.US, "%d kbps", parseLong / 1024 );
   }
}
