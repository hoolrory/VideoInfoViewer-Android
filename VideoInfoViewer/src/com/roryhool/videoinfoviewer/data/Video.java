package com.roryhool.videoinfoviewer.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
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

   @SerializedName( "EncodedDate" )
   public String EncodedDate;

   @SerializedName( "TaggedDate" )
   public String TaggedDate;
   
   @SerializedName( "ThumbnailPath" )
   public String ThumbnailPath;

   @SerializedName( "VideoWidth" )
   public int VideoWidth;
   
   @SerializedName( "VideoHeight" )
   public int VideoHeight;

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
      video.EncodedDate = retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_DATE );
      video.TaggedDate = retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_DATE );
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

      /*
      retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_MIMETYPE );
      retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH );
      retriever.extractMetadata( MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT );
      */

      IsoFile isoFile = null;
      try {
         isoFile = new IsoFile( filePath );
      } catch ( IOException e ) {
         e.printStackTrace();
      }

      if ( isoFile != null ) {
         // MovieHeaderBox movieBox = isoFile.getMovieBox().getMovieHeaderBox();
         // double lengthInSeconds = (double) movieBox.getDuration() / movieBox.getTimescale();
         // addKeyValueField( R.id.video_properties_layout, R.string.key_duration, Double.toString( ( lengthInSeconds ) ) );
         Log.d( "This", "Logging boxes" );
         for ( Box box : isoFile.getBoxes() ) {
            Log.d( "This", String.format( "Box - %s with class %s", box.getType(), box.getClass().toString() ) );

         }
         for ( Box box : isoFile.getMovieBox().getBoxes() ) {
            Log.d( "This", String.format( "Movie Box - %s with class %s", box.getType(), box.getClass().toString() ) );

         }
      }

      try {
         isoFile.close();
      } catch ( IOException e ) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return video;
   }
}
