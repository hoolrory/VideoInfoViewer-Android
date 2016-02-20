/**
 * Copyright (c) 2016 Rory Hool
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package com.roryhool.videoinfoviewer.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.google.gson.annotations.SerializedName;
import com.roryhool.videoinfoviewer.VideoInfoViewerApp;
import com.roryhool.videoinfoviewer.utils.BoxUtils;

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

   @SerializedName( "FrameRate" )
   public String FrameRate;

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

   public String getThumbnailFilePath() {
      return VideoInfoViewerApp.getContext().getFilesDir().getAbsolutePath() + "/" + FileName + ".png";
   }

   public static Video CreateFromFilePath( String filePath ) {

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
      try {
         video.VideoWidth = Integer.parseInt( retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH ) );
      } catch ( NumberFormatException e ) {

      }
      try {
         video.VideoHeight = Integer.parseInt( retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT ) );
      } catch ( NumberFormatException e ) {

      }

      Bitmap bitmap = retriever.getFrameAtTime( Long.parseLong( video.Duration ) / 2 );

      double currentWidth = (double) bitmap.getWidth();
      double currentHeight = (double) bitmap.getHeight();

      double thumbWidth = currentWidth;
      double thumbHeight = currentHeight;

      double maxDimension = 640;
      boolean widthGreater = bitmap.getWidth() > bitmap.getHeight();
      if ( widthGreater && currentWidth > maxDimension ) {
         double ratio = maxDimension / currentWidth;
         thumbWidth = currentWidth * ratio;
         thumbHeight = currentHeight * ratio;
      } else if ( !widthGreater && currentHeight > maxDimension ) {
         double ratio = maxDimension / currentHeight;
         thumbWidth = currentWidth * ratio;
         thumbHeight = currentHeight * ratio;
      }

      try {
         FileOutputStream out = new FileOutputStream( video.getThumbnailFilePath() );
         Bitmap smaller = Bitmap.createScaledBitmap( bitmap, (int) thumbWidth, (int) thumbHeight, true );
         bitmap.recycle();
         smaller.compress( Bitmap.CompressFormat.PNG, 90, out );
         out.close();
         smaller.recycle();
      } catch ( Exception e ) {
         e.printStackTrace();
      }

      IsoFile isoFile;
      try {
         isoFile = new IsoFile( video.FilePath );

         Box box = BoxUtils.FindBox( isoFile.getMovieBox(), "stsz" );

         if ( box instanceof SampleSizeBox ) {
            SampleSizeBox sampleSizeBox = (SampleSizeBox) box;
            float durationMS = Float.parseFloat( video.Duration );
            float durationS = durationMS / 1000.0f;
            float sampleCount = (float) sampleSizeBox.getSampleCount();
            float fps = sampleCount / durationS;
            video.FrameRate = String.format( "%.2f", fps );
         }
         isoFile.close();
      } catch ( IOException e ) {
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
