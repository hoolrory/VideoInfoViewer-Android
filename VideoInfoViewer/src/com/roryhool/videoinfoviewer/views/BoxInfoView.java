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

package com.roryhool.videoinfoviewer.views;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.boxes.BitRateBox;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.DataEntryUrlBox;
import com.coremedia.iso.boxes.DataInformationBox;
import com.coremedia.iso.boxes.DataReferenceBox;
import com.coremedia.iso.boxes.EditListBox;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.MetaBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleTableBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.SoundMediaHeaderBox;
import com.coremedia.iso.boxes.StaticChunkOffsetBox;
import com.coremedia.iso.boxes.SyncSampleBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.UnknownBox;
import com.coremedia.iso.boxes.UserDataBox;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.coremedia.iso.boxes.apple.AppleItemListBox;
import com.coremedia.iso.boxes.h264.AvcConfigurationBox;
import com.coremedia.iso.boxes.h264.AvcConfigurationBox.AVCDecoderConfigurationRecord;
import com.coremedia.iso.boxes.mdat.MediaDataBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.util.Matrix;
import com.roryhool.videoinfoviewer.R;
import com.roryhool.videoinfoviewer.utils.AtomHelper;

public class BoxInfoView extends FrameLayout {

   TextView mBoxTypeText;
   TextView mBoxDescriptionText;

   LinearLayout mBaseLayout;

   Box mBox;

   public BoxInfoView( Context context ) {
      super( context );

      addView( View.inflate( context, R.layout.box_info, null ) );

      mBoxTypeText = (TextView) findViewById( R.id.box_type );
      mBoxDescriptionText = (TextView) findViewById( R.id.box_description );

      mBaseLayout = (LinearLayout) findViewById( R.id.box_layout );
   }

   public void LoadBox( Box box ) {
      
      mBoxTypeText.setText( box.getType() );
      mBoxDescriptionText.setText( AtomHelper.GetNameForType( box.getType() ) );

      if ( box instanceof AbstractFullBox ) {
         LoadBox( (AbstractFullBox) box );
      }

      if ( box instanceof FileTypeBox ) {
         LoadBox( (FileTypeBox) box );
      } else if ( box instanceof MovieBox ) {
         LoadBox( (MovieBox) box );
      } else if ( box instanceof MovieHeaderBox ) {
         LoadBox( (MovieHeaderBox) box );
      } else if ( box instanceof TrackBox ) {
         LoadBox( (TrackBox) box );
      } else if ( box instanceof TrackHeaderBox ) {
         LoadBox( (TrackHeaderBox) box );
      } else if ( box instanceof MediaBox ) {
         LoadBox( (MediaBox) box );
      } else if ( box instanceof MediaHeaderBox ) {
         LoadBox( (MediaHeaderBox) box );
      } else if ( box instanceof HandlerBox ) {
         LoadBox( (HandlerBox) box );
      } else if ( box instanceof MediaInformationBox ) {
         LoadBox( (MediaInformationBox) box );
      } else if ( box instanceof VideoMediaHeaderBox ) {
         LoadBox( (VideoMediaHeaderBox) box );
      } else if ( box instanceof SoundMediaHeaderBox ) {
         LoadBox( (SoundMediaHeaderBox) box );
      } else if ( box instanceof DataInformationBox ) {
         LoadBox( (DataInformationBox) box );
      } else if ( box instanceof DataReferenceBox ) {
         LoadBox( (DataReferenceBox) box );
      } else if ( box instanceof DataEntryUrlBox ) {
         LoadBox( (DataEntryUrlBox) box );
      } else if ( box instanceof SampleTableBox ) {
         LoadBox( (SampleTableBox) box );
      } else if ( box instanceof SampleDescriptionBox ) {
         LoadBox( (SampleDescriptionBox) box );
      } else if ( box instanceof VisualSampleEntry ) {
         LoadBox( (VisualSampleEntry) box );
      } else if ( box instanceof AudioSampleEntry ) {
         LoadBox( (AudioSampleEntry) box );
      } else if ( box instanceof AvcConfigurationBox ) {
         LoadBox( (AvcConfigurationBox) box );
      } else if ( box instanceof BitRateBox ) {
         LoadBox( (BitRateBox) box );
      } else if ( box instanceof ESDescriptorBox ) {
         LoadBox( (ESDescriptorBox) box );
      } else if ( box instanceof TimeToSampleBox ) {
         LoadBox( (TimeToSampleBox) box );
      } else if ( box instanceof CompositionTimeToSample ) {
         LoadBox( (CompositionTimeToSample) box );
      } else if ( box instanceof SyncSampleBox ) {
         LoadBox( (SyncSampleBox) box );
      } else if ( box instanceof SampleToChunkBox ) {
         LoadBox( (SampleToChunkBox) box );
      } else if ( box instanceof SampleSizeBox ) {
         LoadBox( (SampleSizeBox) box );
      } else if ( box instanceof StaticChunkOffsetBox ) {
         LoadBox( (StaticChunkOffsetBox) box );
      } else if ( box instanceof UnknownBox ) {
         LoadBox( (UnknownBox) box );
      } else if ( box instanceof UserDataBox ) {
         LoadBox( (UserDataBox) box );
      } else if ( box instanceof MetaBox ) {
         LoadBox( (MetaBox) box );
      } else if ( box instanceof AppleItemListBox ) {
         LoadBox( (AppleItemListBox) box );
      } else if ( box instanceof MediaDataBox ) {
         LoadBox( (MediaDataBox) box );
      } else {

      }
   }

   public void LoadGenericBox( Box box ) {
      mBox = box;

      Log.d( "this", "Printing box type " + mBox.getType() );
      for ( Field field : mBox.getClass().getDeclaredFields() ) {
         field.setAccessible( true );
         try {
            addFieldView( mBox, field );
         } catch ( Exception e ) {
            e.printStackTrace();
            Log.d( "this", e.toString() );
         }

         Log.d( "this", String.format( "------ Printing name %s, type %s", field.getName(), field.getType().getSimpleName() ) );
      }
   }

   public void LoadBox( AbstractFullBox box ) {
      addViewForValue( "Version:", box.getVersion() );
      addViewForValue( "Flag:", box.getFlags() );
   }

   public void LoadBox( FileTypeBox box ) {
      Log.d( "this", "load FileTypeBox" );
      addViewForValue( "Major Brand:", box.getMajorBrand() );
      addViewForValue( "Version:", box.getMinorVersion() );
      addViewForValue( "Compatible Brands:", box.getCompatibleBrands() );
   }

   public void LoadBox( MovieBox box ) {

      addViewForValue( "Track Count:", box.getTrackCount() );
      addViewForValue( "Track Numbers:", box.getTrackNumbers() );
   }

   public void LoadBox( MovieHeaderBox box ) {

      addViewForValue( "Timescale:", box.getTimescale() );
      addViewForValue( "Duration:", box.getDuration() );
      addViewForValue( "Rate:", box.getRate() );
      addViewForValue( "Volume:", box.getVolume() );
      addViewForValue( "Creation Time:", box.getCreationTime() );
      addViewForValue( "Modification Time:", box.getModificationTime() );
      addViewForValue( "Poster Time:", box.getPosterTime() );
      addViewForValue( "Current Time:", box.getCurrentTime() );
      addViewForValue( "Preview Duration:", box.getPreviewDuration() );
      addViewForValue( "Preview Time:", box.getPreviewTime() );
      addViewForValue( "Selection Duration:", box.getSelectionDuration() );
      addViewForValue( "Selection Time:", box.getSelectionTime() );
      addViewForValue( "Next Track Id:", box.getNextTrackId() );

      addMatrixView( "Matrix:", box.getMatrix() );
   }

   public void LoadBox( TrackBox box ) {

   }

   public void LoadBox( TrackHeaderBox box ) {
      addViewForValue( "Creation Time:", box.getCreationTime() );
      addViewForValue( "Duration:", box.getDuration() );
      addViewForValue( "Layer:", box.getLayer() );
      addViewForValue( "Modification Time:", box.getModificationTime() );
      addViewForValue( "Track ID:", box.getTrackId() );
      addViewForValue( "Volume:", box.getVolume() );
      addViewForValue( "Width:", box.getWidth() );
      addViewForValue( "Height:", box.getHeight() );
      addViewForValue( "Alternate Group:", box.getAlternateGroup() );

      addMatrixView( "Matrix:", box.getMatrix() );
   }

   public void LoadBox( MediaBox box ) {
   }

   public void LoadBox( MediaHeaderBox box ) {
      addViewForValue( "Creation Time:", box.getCreationTime() );
      addViewForValue( "Duration:", box.getDuration() );
      addViewForValue( "Language:", box.getLanguage() );
      addViewForValue( "Modification Time:", box.getModificationTime() );
      addViewForValue( "Timescale:", box.getTimescale() );
   }

   public void LoadBox( EditListBox box ) {
      addViewForValue( "Entry Count:", box.getEntries().size() );

      for ( EditListBox.Entry entry : box.getEntries() ) {
         addViewForValue( "Segment Duration:", entry.getSegmentDuration() );
         addViewForValue( "Media Time:", entry.getMediaTime() );
         addViewForValue( "Media Rate:", entry.getMediaRate() );
      }
   }

   public void LoadBox( HandlerBox box ) {
      addViewForValue( "Handler Type:", box.getHandlerType() );
      addViewForValue( "Human Readable Track Type:", box.getHumanReadableTrackType() );
      addViewForValue( "Name:", box.getName() );
   }

   public void LoadBox( MediaInformationBox box ) {

   }

   public void LoadBox( VideoMediaHeaderBox box ) {
      addViewForValue( "Graphics Mode:", box.getGraphicsmode() );
      addViewForValue( "Op Color:", box.getOpcolor() );
   }

   public void LoadBox( SoundMediaHeaderBox box ) {
      addViewForValue( "Balance:", box.getBalance() );
   }

   public void LoadBox( DataInformationBox box ) {

   }

   public void LoadBox( DataReferenceBox box ) {
      addViewForValue( "Version:", box.getVersion() );
      addViewForValue( "Flags:", box.getFlags() );
      addViewForValue( "Size:", box.getSize() );
   }

   public void LoadBox( DataEntryUrlBox box ) {
   }

   public void LoadBox( SampleTableBox box ) {

   }

   public void LoadBox( SampleDescriptionBox box ) {
      addViewForValue( "Version:", box.getVersion() );
      addViewForValue( "Flags:", box.getFlags() );
      addViewForValue( "Size:", box.getSize() );
   }

   public void LoadBox( VisualSampleEntry box ) {
      addViewForValue( "Compressor Name:", box.getCompressorname() );
      addViewForValue( "Depth:", box.getDepth() );
      addViewForValue( "Frame Count:", box.getFrameCount() );
      addViewForValue( "Size:", box.getSize() );
      addViewForValue( "Width:", box.getWidth() );
      addViewForValue( "Height:", box.getHeight() );
      addViewForValue( "Horizontal Resolution:", box.getHorizresolution() );
      addViewForValue( "Vertical Resolution:", box.getVertresolution() );
   }

   public void LoadBox( AudioSampleEntry box ) {
      addViewForValue( "Bytes Per Frame:", box.getBytesPerFrame() );
      addViewForValue( "Bytes Per Packet:", box.getBytesPerPacket() );
      addViewForValue( "Bytes Per Sample:", box.getBytesPerSample() );
      addViewForValue( "Channel Count:", box.getChannelCount() );
      addViewForValue( "Compression Id:", box.getCompressionId() );
      addViewForValue( "Packet Size:", box.getPacketSize() );
      addViewForValue( "Reserved 1:", box.getReserved1() );
      addViewForValue( "Reserved 2:", box.getReserved2() );
      addViewForValue( "Sample Rate:", box.getSampleRate() );
      addViewForValue( "Sample Size:", box.getSampleSize() );
      addViewForValue( "Samples Per Packet:", box.getSamplesPerPacket() );
      addViewForValue( "Size:", box.getSize() );
      addViewForValue( "Sound Version:", box.getSoundVersion() );
      addViewForValue( "Sound Version 2 Data:", box.getSoundVersion2Data() );
   }

   public void LoadBox( AvcConfigurationBox box ) {

      AVCDecoderConfigurationRecord record = box.getavcDecoderConfigurationRecord();
      addViewForValue( "Record Content Size:", record.getContentSize() );
      addViewForValue( "Record Picture Parameter Sets:", record.getPictureParameterSetsAsStrings() );
      addViewForValue( "Record PPS:", record.getPPS() );
      addViewForValue( "Record Sequence Parameter Sets Exts:", record.getSequenceParameterSetExtsAsStrings() );
      addViewForValue( "Record Sequence Parameter Sets:", record.getSequenceParameterSetsAsStrings() );
      addViewForValue( "Record SPS:", record.getSPS() );

      addViewForValue( "AVC Level:", box.getAvcLevelIndication() );
      addViewForValue( "AVC Profile:", box.getAvcProfileIndication() );
      addViewForValue( "Bit Depth Chroma Minus 8:", box.getBitDepthChromaMinus8() );
      addViewForValue( "Bit Depth Luma Minus 8:", box.getBitDepthLumaMinus8() );
      addViewForValue( "Chroma Format:", box.getChromaFormat() );
      addViewForValue( "Configuration Version:", box.getConfigurationVersion() );
      addViewForValue( "Content Size:", box.getContentSize() );
      addViewForValue( "Length Size Minus One", box.getLengthSizeMinusOne() );
      addViewForValue( "Picture Parameter Sets:", box.getPictureParameterSets() );
      addViewForValue( "PPS:", box.getPPS() );
      addViewForValue( "Profile Compatibility:", box.getProfileCompatibility() );
      addViewForValue( "Sequence Parameter Set Exts:", box.getSequenceParameterSetExts() );
      addViewForValue( "Sequence Parameter Sets:", box.getSequenceParameterSets() );
      addViewForValue( "SPS:", box.getSPS() );
   }

   public void LoadBox( BitRateBox box ) {
      addViewForValue( "Average BitRate:", box.getAvgBitrate() );
      addViewForValue( "Buffer Size Db:", box.getBufferSizeDb() );
      addViewForValue( "Max Birate:", box.getMaxBitrate() );
   }

   public void LoadBox( ESDescriptorBox box ) {
      // addViewForValue( "", box.getEsDescriptor());
      // addViewForValue( "", box.getDescriptor());
      addViewForValue( "Descriptor As String:", box.getDescriptorAsString() );
   }

   public void LoadBox( TimeToSampleBox box ) {
      addViewForValue( "Entries:", box.getEntries() );
   }

   public void LoadBox( CompositionTimeToSample box ) {
      addViewForValue( "Entries:", box.getEntries() );
   }

   public void LoadBox( SyncSampleBox box ) {
      addViewForValue( "Sample Number:", box.getSampleNumber() );
   }

   public void LoadBox( SampleToChunkBox box ) {
      addViewForValue( "Entries:", box.getEntries() );
   }

   public void LoadBox( SampleSizeBox box ) {
      addViewForValue( "Sample Count:", box.getSampleCount() );
      addViewForValue( "Sample Size:", box.getSampleSize() );
      addViewForValue( "Sample Sizes:", box.getSampleSizes() );
   }

   public void LoadBox( StaticChunkOffsetBox box ) {
      addViewForValue( "Chunk Offsets:", box.getChunkOffsets() );
   }

   public void LoadBox( UnknownBox box ) {
   }

   public void LoadBox( UserDataBox box ) {

   }

   public void LoadBox( MetaBox box ) {
      addViewForValue( "Version:", box.getVersion() );
      addViewForValue( "Flags:", box.getFlags() );
      addViewForValue( "Size:", box.getSize() );
   }

   public void LoadBox( AppleItemListBox box ) {
   }

   public void LoadBox( MediaDataBox box ) {
      addViewForValue( "Offset:", box.getOffset() );
      addViewForValue( "Parent:", box.getParent() );
      addViewForValue( "Size:", box.getSize() );
      addViewForValue( "Type:", box.getType() );
   }

   private void addViewForValue( String key, Object value ) {

      if ( value instanceof String[] ) {
         addStringArrayView( key, (String[]) value );
      } else if ( value instanceof Matrix ) {
         addMatrixView( key, (Matrix) value );
      } else {
         addTextView( key, value );
      }
   }

   private void addTextView( String key, Object value ) {

      RelativeLayout layout = (RelativeLayout) View.inflate( getContext(), R.layout.fifty_fifty_layout, null );

      LinearLayout leftLayout = (LinearLayout) layout.findViewById( R.id.left_layout );
      LinearLayout rightLayout = (LinearLayout) layout.findViewById( R.id.right_layout );

      Log.d( "this", String.format( Locale.US, "Adding view %s - %s", key, getDisplayForObject( value ) ) );
      RobotoTextView keyText = new RobotoTextView( new ContextThemeWrapper( getContext(), R.style.CardKey ) );
      keyText.setText( key );
      
      RobotoTextView valueText = new RobotoTextView( new ContextThemeWrapper( getContext(), R.style.CardValue ) );
      valueText.setText( getDisplayForObject( value ) );
      
      leftLayout.addView( keyText );
      rightLayout.addView( valueText );

      mBaseLayout.addView( layout );

   }

   private void addStringArrayView( String key, String[] array ) {

      RelativeLayout layout = (RelativeLayout) View.inflate( getContext(), R.layout.string_array_layout, null );

      TextView textView = (TextView) layout.findViewById( R.id.array_title );
      textView.setText( key );

      LinearLayout stringLayout = (LinearLayout) layout.findViewById( R.id.string_layout );

      for ( String string : array ) {
         RobotoTextView valueText = new RobotoTextView( new ContextThemeWrapper( getContext(), R.style.CardValue ) );
         valueText.setText( string );
         stringLayout.addView( valueText );
      }
      mBaseLayout.addView( layout );
   }

   private void addMatrixView( String key, Matrix matrix ) {

      ByteBuffer bb = ByteBuffer.allocate( Double.SIZE * 9 );
      matrix.getContent( bb );
      bb.rewind();

      double a = IsoTypeReader.readFixedPoint1616( bb );
      double b = IsoTypeReader.readFixedPoint1616( bb );
      double u = IsoTypeReader.readFixedPoint0230( bb );
      double c = IsoTypeReader.readFixedPoint1616( bb );
      double d = IsoTypeReader.readFixedPoint1616( bb );
      double v = IsoTypeReader.readFixedPoint0230( bb );
      double tx = IsoTypeReader.readFixedPoint1616( bb );
      double ty = IsoTypeReader.readFixedPoint1616( bb );
      double w = IsoTypeReader.readFixedPoint0230( bb );

      Double[] matrixArray = new Double[9];
      matrixArray[0] = a;
      matrixArray[1] = b;
      matrixArray[2] = u;
      matrixArray[3] = c;
      matrixArray[4] = d;
      matrixArray[5] = v;
      matrixArray[6] = tx;
      matrixArray[7] = ty;
      matrixArray[8] = w;

      RelativeLayout layout = (RelativeLayout) View.inflate( getContext(), R.layout.matrix_layout, null );

      TextView textView = (TextView) layout.findViewById( R.id.matrix_title );
      textView.setText( "Matrix:" );
      
      GridView gridView = (GridView) layout.findViewById( R.id.matrix_grid );

      ArrayAdapter<Double> adapter = new ArrayAdapter<Double>( getContext(), R.layout.matrix_item_layout, matrixArray );

      gridView.setAdapter( adapter );

      mBaseLayout.addView( layout );
   }

   private void addFieldView( Box box, Field field ) throws IllegalAccessException, IllegalArgumentException, InstantiationException {
      
      Object object = field.get( box );

      TextView view = new TextView( getContext() );
      view.setText( String.format( Locale.US, "%s - %s", field.getName(), getDisplayForObject( object ) ) );

      mBaseLayout.addView( view );
   }

   private String getDisplayForObject(Object object) {
      if ( object instanceof String ) {
         return (String) object;
      } else if ( object instanceof Long ) {
         return String.format( Locale.US, "%d", (Long) object );
      } else if ( object instanceof Integer ) {
         return String.format( Locale.US, "%d", (Integer) object );
      } else if ( object instanceof Float ) {
         return String.format( Locale.US, "%.2f", (Float) object );
      } else if ( object instanceof Double ) {
         return String.format( Locale.US, "%.2f", (Double) object );
      } else if ( object instanceof Byte ) {
         return String.format( Locale.US, "%02X", (Byte) object );
      } else if (object instanceof List ) {
         
         String result = "";
         List<?> list = (List<?>) object;
         for ( int i = 0; i < list.size(); i++ ) {
            if(i != 0) {
               result += ", ";
            }
            result += getDisplayForObject( list.get( i ) );
         }
         return result;
      } else if ( object instanceof Matrix ) {
         Matrix matrix = (Matrix) object;
         ByteBuffer bb = ByteBuffer.allocate( Double.SIZE * 9 );
         matrix.getContent( bb );
         bb.rewind();

         double a = IsoTypeReader.readFixedPoint1616( bb );
         double b = IsoTypeReader.readFixedPoint1616( bb );
         double u = IsoTypeReader.readFixedPoint0230( bb );
         double c = IsoTypeReader.readFixedPoint1616( bb );
         double d = IsoTypeReader.readFixedPoint1616( bb );
         double v = IsoTypeReader.readFixedPoint0230( bb );
         double tx = IsoTypeReader.readFixedPoint1616( bb );
         double ty = IsoTypeReader.readFixedPoint1616( bb );
         double w = IsoTypeReader.readFixedPoint0230( bb );
         
         return String.format( Locale.US, "%s - [%.2f, %.2f, %.2f, \r\n%.2f, %.2f, %.2f, %.2f, %.2f, %.2f]", matrix.toString(), a, b, u, c, d, v, tx, ty, w );
      } else if ( object instanceof int[] ) {

         String result = "";
         int[] array = (int[]) object;
         for ( int i = 0; i < array.length; i++ ) {
            if ( i != 0 ) {
               result += ", ";
            }
            result += getDisplayForObject( array[i] );
         }
         return result;
      } else if ( object instanceof long[] ) {

         String result = "";
         long[] array = (long[]) object;
         for ( int i = 0; i < array.length; i++ ) {
            if ( i != 0 ) {
               result += ", ";
            }
            result += getDisplayForObject( array[i] );
         }
         return result;
      } else if ( object instanceof Date ) {

         Date date = (Date) object;
         return new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss", Locale.US ).format( date );
      } else if ( object instanceof Object[] ) {

         String result = "";
         Object[] array = (Object[]) object;
         for ( int i = 0; i < array.length; i++ ) {
            if ( i != 0 ) {
               result += ", ";
            }
            result += getDisplayForObject( array[i] );
         }
         return result;
      } else if ( object instanceof byte[] ) {

         String result = "";
         byte[] array = (byte[]) object;
         for ( int i = 0; i < array.length; i++ ) {
            if ( i != 0 ) {
               result += ", ";
            }
            result += getDisplayForObject( array[i] );
         }
         return result;
      }


      return String.format( Locale.US, "Unknown type: '%s'", object.getClass().getSimpleName() );
   }
}
