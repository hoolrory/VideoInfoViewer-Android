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

package com.roryhool.videoinfoviewer.data;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.coremedia.iso.IsoFile;
import com.google.gson.annotations.SerializedName;

public class Video implements Comparable<Video> {

   @SerializedName( "FileName" )
   public String FileName;

   @SerializedName( "FilePath" )
   public String FilePath;

   @SerializedName( "Format" )
   public String Format;

   @SerializedName( "FormatProfile" )
   public String FormatProfile;

   @SerializedName( "CodecID" )
   public String CodecID;

   @SerializedName( "FileSize" )
   public String FileSize;

   @SerializedName( "MimeType" )
   public String MimeType;

   @SerializedName( "Duration" )
   public String Duration;

   @SerializedName( "BitRate" )
   public String BitRate;

   @SerializedName( "Date" )
   public String Date;
   
   @SerializedName( "ThumbnailPath" )
   public String ThumbnailPath;

   @SerializedName( "VideoWidth" )
   public int VideoWidth;

   @SerializedName( "VideoHeight" )
   public int VideoHeight;

   public transient int CacheId = -1;

   private transient IsoFile mIsoFile;

   @Override
   public int compareTo( Video video ) {
      Log.d( "this", "Comparing path " + video.FilePath + " to " + FilePath );
      return video.FilePath.equals( FilePath ) ? 0 : -1;
   }

   public String getThumbnailFilePath( Context context ) {
      return context.getFilesDir().getAbsolutePath() + "/" + FileName + ".png";
   }

   public static Video CreateFromFilePath( Context context, String filePath ) {

      File file = new File( filePath );

      if ( !file.exists() ) {
         return null;
      }

      MediaMetadataRetriever retriever = new MediaMetadataRetriever();
      retriever.setDataSource( filePath );

      Video video = new Video();
      video.FilePath = filePath;
      video.FileName = file.getName();
      video.Format = retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_TITLE );
      video.FileSize = Long.toString( file.length() );
      video.MimeType = retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_MIMETYPE );
      video.Duration = retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_DURATION );
      video.BitRate = retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_BITRATE );
      video.Date = retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_DATE );
      video.VideoWidth = Integer.parseInt( retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH ) );
      video.VideoHeight = Integer.parseInt( retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT ) );
      
      Bitmap bitmap = retriever.getFrameAtTime( Long.parseLong( video.Duration ) / 2 );

      try {
         FileOutputStream out = new FileOutputStream( video.getThumbnailFilePath( context ) );
         bitmap.compress( Bitmap.CompressFormat.PNG, 90, out );
         out.close();
      } catch ( Exception e ) {
         e.printStackTrace();
      }

      return video;
   }

   public IsoFile getIsoFile() {
      return mIsoFile;
   }

   public void setIsoFile( IsoFile isoFile ) {
      mIsoFile = isoFile;
   }
}
