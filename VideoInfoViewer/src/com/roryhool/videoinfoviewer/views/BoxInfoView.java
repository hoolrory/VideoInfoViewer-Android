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

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridLayout.Spec;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
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
import com.coremedia.iso.boxes.EditBox;
import com.coremedia.iso.boxes.EditListBox;
import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MediaInformationBox;
import com.coremedia.iso.boxes.MetaBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox;
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
import com.roryhool.videoinfoviewer.utils.Logg;

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
   
   private GridLayout addTableHeader( String... headers ) {
      
      GridLayout gridLayout = new GridLayout( getContext() );
      gridLayout.setBackgroundResource( R.color.grey_faint );
      int rowNum = 0;
      int columnNum = 0;
      for ( String header : headers ) {

         RobotoTextView headerText = new RobotoTextView( new ContextThemeWrapper( getContext(), R.style.CardKey ) );
         headerText.setText( header );

         Spec rowspecs = GridLayout.spec( rowNum, 1 );
         Spec colspecs = GridLayout.spec( columnNum, 1 );
         GridLayout.LayoutParams params = new GridLayout.LayoutParams( rowspecs, colspecs );
         params.setGravity( Gravity.CENTER_HORIZONTAL );
         params.setMargins( getContext().getResources().getDimensionPixelSize( R.dimen.column_padding ), 0, getContext().getResources().getDimensionPixelSize( R.dimen.column_padding ), 0 );

         gridLayout.addView( headerText, params );

         columnNum++;
      }

      return gridLayout;
   }

   private void addTableRow( GridLayout gridLayout, String... columns ) {
      int rowNum = gridLayout.getRowCount();

      int columnNum = 0;
      for ( String column : columns ) {

         RobotoTextView columnText = new RobotoTextView( new ContextThemeWrapper( getContext(), R.style.CardValue ) );
         columnText.setText( column );

         Spec rowspecs = GridLayout.spec( rowNum, 1 );
         Spec colspecs = GridLayout.spec( columnNum, 1 );
         GridLayout.LayoutParams params = new GridLayout.LayoutParams( rowspecs, colspecs );
         params.setGravity( Gravity.CENTER_HORIZONTAL );

         gridLayout.addView( columnText, params );

         columnNum++;
      }
   }

   private String getDisplayForObject( Object object ) {
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
      } else if ( object instanceof List ) {

         String result = "";
         List<?> list = (List<?>) object;
         for ( int i = 0; i < list.size(); i++ ) {
            if ( i != 0 ) {
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

   public void LoadBox( Box box ) {
      
      mBoxTypeText.setText( box.getType() );
      mBoxDescriptionText.setText( AtomHelper.GetNameForType( box.getType() ) );

      Logg.d( "Trying to load box %s", box.getClass().getName() );

      if ( box instanceof AbstractFullBox ) {
         LoadSpecificBox( (AbstractFullBox) box );
      }

      if ( box instanceof FileTypeBox ) {
         LoadSpecificBox( (FileTypeBox) box );
      } else if ( box instanceof MovieBox ) {
         LoadSpecificBox( (MovieBox) box );
      } else if ( box instanceof MovieHeaderBox ) {
         LoadSpecificBox( (MovieHeaderBox) box );
      } else if ( box instanceof TrackBox ) {
         LoadSpecificBox( (TrackBox) box );
      } else if ( box instanceof TrackHeaderBox ) {
         LoadSpecificBox( (TrackHeaderBox) box );
      } else if ( box instanceof MediaBox ) {
         LoadSpecificBox( (MediaBox) box );
      } else if ( box instanceof MediaHeaderBox ) {
         LoadSpecificBox( (MediaHeaderBox) box );
      } else if ( box instanceof HandlerBox ) {
         LoadSpecificBox( (HandlerBox) box );
      } else if ( box instanceof MediaInformationBox ) {
         LoadSpecificBox( (MediaInformationBox) box );
      } else if ( box instanceof VideoMediaHeaderBox ) {
         LoadSpecificBox( (VideoMediaHeaderBox) box );
      } else if ( box instanceof SoundMediaHeaderBox ) {
         LoadSpecificBox( (SoundMediaHeaderBox) box );
      } else if ( box instanceof DataInformationBox ) {
         LoadSpecificBox( (DataInformationBox) box );
      } else if ( box instanceof DataReferenceBox ) {
         LoadSpecificBox( (DataReferenceBox) box );
      } else if ( box instanceof DataEntryUrlBox ) {
         LoadSpecificBox( (DataEntryUrlBox) box );
      } else if ( box instanceof SampleTableBox ) {
         LoadSpecificBox( (SampleTableBox) box );
      } else if ( box instanceof SampleDescriptionBox ) {
         LoadSpecificBox( (SampleDescriptionBox) box );
      } else if ( box instanceof VisualSampleEntry ) {
         LoadSpecificBox( (VisualSampleEntry) box );
      } else if ( box instanceof AudioSampleEntry ) {
         LoadSpecificBox( (AudioSampleEntry) box );
      } else if ( box instanceof AvcConfigurationBox ) {
         LoadSpecificBox( (AvcConfigurationBox) box );
      } else if ( box instanceof BitRateBox ) {
         LoadSpecificBox( (BitRateBox) box );
      } else if ( box instanceof ESDescriptorBox ) {
         LoadSpecificBox( (ESDescriptorBox) box );
      } else if ( box instanceof TimeToSampleBox ) {
         LoadSpecificBox( (TimeToSampleBox) box );
      } else if ( box instanceof CompositionTimeToSample ) {
         LoadSpecificBox( (CompositionTimeToSample) box );
      } else if ( box instanceof SyncSampleBox ) {
         LoadSpecificBox( (SyncSampleBox) box );
      } else if ( box instanceof SampleToChunkBox ) {
         LoadSpecificBox( (SampleToChunkBox) box );
      } else if ( box instanceof SampleSizeBox ) {
         LoadSpecificBox( (SampleSizeBox) box );
      } else if ( box instanceof StaticChunkOffsetBox ) {
         LoadSpecificBox( (StaticChunkOffsetBox) box );
      } else if ( box instanceof UnknownBox ) {
         LoadSpecificBox( (UnknownBox) box );
      } else if ( box instanceof UserDataBox ) {
         LoadSpecificBox( (UserDataBox) box );
      } else if ( box instanceof MetaBox ) {
         LoadSpecificBox( (MetaBox) box );
      } else if ( box instanceof AppleItemListBox ) {
         LoadSpecificBox( (AppleItemListBox) box );
      } else if ( box instanceof MediaDataBox ) {
         LoadSpecificBox( (MediaDataBox) box );
      } else if ( box instanceof EditBox ) {
         LoadSpecificBox( (EditBox) box );
      } else if ( box instanceof EditListBox ) {
         LoadSpecificBox( (EditListBox) box );
      } else if ( box instanceof SampleDependencyTypeBox ) {
         LoadSpecificBox( (SampleDependencyTypeBox) box );
      } else {
         Logg.d( "Unable to load box of type %s", box.getClass().getName() );
      }
   }

   private void LoadSpecificBox( AbstractFullBox box ) {
      addViewForValue( "Version:", box.getVersion() );
      addViewForValue( "Flag:", box.getFlags() );
   }

   private void LoadSpecificBox( FileTypeBox box ) {
      Log.d( "this", "load FileTypeBox" );
      addViewForValue( "Major Brand:", box.getMajorBrand() );
      addViewForValue( "Version:", box.getMinorVersion() );
      addViewForValue( "Compatible Brands:", box.getCompatibleBrands() );
   }

   private void LoadSpecificBox( MovieBox box ) {

      addViewForValue( "Track Count:", box.getTrackCount() );
      addViewForValue( "Track Numbers:", box.getTrackNumbers() );
   }

   private void LoadSpecificBox( MovieHeaderBox box ) {

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

   private void LoadSpecificBox( TrackBox box ) {

   }

   private void LoadSpecificBox( TrackHeaderBox box ) {
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

   private void LoadSpecificBox( MediaBox box ) {
   }

   private void LoadSpecificBox( MediaHeaderBox box ) {
      addViewForValue( "Creation Time:", box.getCreationTime() );
      addViewForValue( "Duration:", box.getDuration() );
      addViewForValue( "Language:", box.getLanguage() );
      addViewForValue( "Modification Time:", box.getModificationTime() );
      addViewForValue( "Timescale:", box.getTimescale() );
   }

   private void LoadSpecificBox( EditListBox box ) {
      addViewForValue( "Entry Count:", box.getEntries().size() );

      int skippedRows = 0;

      GridLayout gridLayout = addTableHeader( "Segment Duration", "Media Time", "Media Rate" );
      for ( EditListBox.Entry entry : box.getEntries() ) {
         addTableRow( gridLayout, getDisplayForObject( entry.getSegmentDuration() ), getDisplayForObject( entry.getMediaTime() ), getDisplayForObject( entry.getMediaRate() ) );

         if ( gridLayout.getRowCount() > 200 ) {
            skippedRows = box.getEntries().size() - 200;
            break;
         }
      }

      HorizontalScrollView scrollView = new HorizontalScrollView( getContext() );
      scrollView.addView( gridLayout );
      mBaseLayout.addView( scrollView );

      if ( skippedRows > 0 ) {

         RobotoTextView text = new RobotoTextView( new ContextThemeWrapper( getContext(), R.style.CardValue ) );
         text.setText( String.format( Locale.US, "%d entries emmited", skippedRows ) );
         mBaseLayout.addView( text );
      }
   }

   private void LoadSpecificBox( HandlerBox box ) {
      addViewForValue( "Handler Type:", box.getHandlerType() );
      addViewForValue( "Human Readable Track Type:", box.getHumanReadableTrackType() );
      addViewForValue( "Name:", box.getName() );
   }

   private void LoadSpecificBox( MediaInformationBox box ) {

   }

   private void LoadSpecificBox( VideoMediaHeaderBox box ) {
      addViewForValue( "Graphics Mode:", box.getGraphicsmode() );
      addViewForValue( "Op Color:", box.getOpcolor() );
   }

   private void LoadSpecificBox( SoundMediaHeaderBox box ) {
      addViewForValue( "Balance:", box.getBalance() );
   }

   private void LoadSpecificBox( DataInformationBox box ) {

   }

   private void LoadSpecificBox( DataReferenceBox box ) {
      addViewForValue( "Version:", box.getVersion() );
      addViewForValue( "Flags:", box.getFlags() );
      addViewForValue( "Size:", box.getSize() );
   }

   private void LoadSpecificBox( DataEntryUrlBox box ) {
   }

   private void LoadSpecificBox( SampleTableBox box ) {

   }

   private void LoadSpecificBox( SampleDescriptionBox box ) {
      addViewForValue( "Version:", box.getVersion() );
      addViewForValue( "Flags:", box.getFlags() );
      addViewForValue( "Size:", box.getSize() );
   }

   private void LoadSpecificBox( VisualSampleEntry box ) {
      addViewForValue( "Compressor Name:", box.getCompressorname() );
      addViewForValue( "Depth:", box.getDepth() );
      addViewForValue( "Frame Count:", box.getFrameCount() );
      addViewForValue( "Size:", box.getSize() );
      addViewForValue( "Width:", box.getWidth() );
      addViewForValue( "Height:", box.getHeight() );
      addViewForValue( "Horizontal Resolution:", box.getHorizresolution() );
      addViewForValue( "Vertical Resolution:", box.getVertresolution() );
   }

   private void LoadSpecificBox( AudioSampleEntry box ) {
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

   private void LoadSpecificBox( AvcConfigurationBox box ) {

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

   private void LoadSpecificBox( BitRateBox box ) {
      addViewForValue( "Average BitRate:", box.getAvgBitrate() );
      addViewForValue( "Buffer Size Db:", box.getBufferSizeDb() );
      addViewForValue( "Max Birate:", box.getMaxBitrate() );
   }

   private void LoadSpecificBox( ESDescriptorBox box ) {
      // addViewForValue( "", box.getEsDescriptor());
      // addViewForValue( "", box.getDescriptor());
      addViewForValue( "Descriptor As String:", box.getDescriptorAsString() );
   }

   private void LoadSpecificBox( TimeToSampleBox box ) {
      addViewForValue( "Entries:", box.getEntries().size() );

      int skippedRows = 0;

      GridLayout gridLayout = addTableHeader( "Count", "Delta" );
      for ( TimeToSampleBox.Entry entry : box.getEntries() ) {
         addTableRow( gridLayout, getDisplayForObject( entry.getCount() ), getDisplayForObject( entry.getDelta() ) );

         if ( gridLayout.getRowCount() > 200 ) {
            skippedRows = box.getEntries().size() - 200;
            break;
         }
      }

      HorizontalScrollView scrollView = new HorizontalScrollView( getContext() );
      scrollView.addView( gridLayout );
      mBaseLayout.addView( scrollView );

      if ( skippedRows > 0 ) {

         RobotoTextView text = new RobotoTextView( new ContextThemeWrapper( getContext(), R.style.CardValue ) );
         text.setText( String.format( Locale.US, "%d entries emmited", skippedRows ) );
         mBaseLayout.addView( text );
      }
   }

   private void LoadSpecificBox( CompositionTimeToSample box ) {
      addViewForValue( "Entries:", box.getEntries().size() );

      int skippedRows = 0;

      GridLayout gridLayout = addTableHeader( "Count", "Offset" );
      for ( CompositionTimeToSample.Entry entry : box.getEntries() ) {
         addTableRow( gridLayout, getDisplayForObject( entry.getCount() ), getDisplayForObject( entry.getOffset() ) );

         if ( gridLayout.getRowCount() > 200 ) {
            skippedRows = box.getEntries().size() - 200;
            break;
         }
      }

      HorizontalScrollView scrollView = new HorizontalScrollView( getContext() );
      scrollView.addView( gridLayout );
      mBaseLayout.addView( scrollView );

      if ( skippedRows > 0 ) {

         RobotoTextView text = new RobotoTextView( new ContextThemeWrapper( getContext(), R.style.CardValue ) );
         text.setText( String.format( Locale.US, "%d entries emmited", skippedRows ) );
         mBaseLayout.addView( text );
      }
   }

   private void LoadSpecificBox( SyncSampleBox box ) {
      addViewForValue( "Sample Number:", box.getSampleNumber() );
   }

   private void LoadSpecificBox( SampleToChunkBox box ) {
      addViewForValue( "Entries:", box.getEntries().size() );

      int skippedRows = 0;

      GridLayout gridLayout = addTableHeader( "First Chunk", "Sample Description Index", "Samples Per Chunk" );
      for ( SampleToChunkBox.Entry entry : box.getEntries() ) {
         addTableRow( gridLayout, getDisplayForObject( entry.getFirstChunk() ), getDisplayForObject( entry.getSampleDescriptionIndex() ), getDisplayForObject( entry.getSamplesPerChunk() ) );

         if ( gridLayout.getRowCount() > 200 ) {
            skippedRows = box.getEntries().size() - 200;
            break;
         }
      }

      HorizontalScrollView scrollView = new HorizontalScrollView( getContext() );
      scrollView.addView( gridLayout );
      mBaseLayout.addView( scrollView );

      if ( skippedRows > 0 ) {

         RobotoTextView text = new RobotoTextView( new ContextThemeWrapper( getContext(), R.style.CardValue ) );
         text.setText( String.format( Locale.US, "%d entries emmited", skippedRows ) );
         mBaseLayout.addView( text );
      }
   }

   private void LoadSpecificBox( SampleSizeBox box ) {
      addViewForValue( "Sample Count:", box.getSampleCount() );
      addViewForValue( "Sample Size:", box.getSampleSize() );
      addViewForValue( "Sample Sizes:", box.getSampleSizes() );
   }

   private void LoadSpecificBox( StaticChunkOffsetBox box ) {
      addViewForValue( "Chunk Offsets:", box.getChunkOffsets() );
   }

   private void LoadSpecificBox( UnknownBox box ) {
   }

   private void LoadSpecificBox( UserDataBox box ) {

   }

   private void LoadSpecificBox( MetaBox box ) {
      addViewForValue( "Version:", box.getVersion() );
      addViewForValue( "Flags:", box.getFlags() );
      addViewForValue( "Size:", box.getSize() );
   }

   private void LoadSpecificBox( AppleItemListBox box ) {
   }

   private void LoadSpecificBox( MediaDataBox box ) {
      addViewForValue( "Offset:", box.getOffset() );
      addViewForValue( "Size:", box.getSize() );
      addViewForValue( "Type:", box.getType() );
   }

   private void LoadSpecificBox( EditBox box ) {
      addViewForValue( "Offset:", box.getOffset() );
      addViewForValue( "Size:", box.getSize() );
      addViewForValue( "Type:", box.getType() );
   }

   private void LoadSpecificBox( SampleDependencyTypeBox box ) {

      addViewForValue( "Entries:", box.getEntries().size() );

      int skippedRows = 0;

      Logg.d( "there are %d entries", box.getEntries().size() );
      GridLayout gridLayout = addTableHeader( "Reserved", "Sample Depends On", "Sample Has Redundancy", "Sample Is Dependent On" );
      for ( SampleDependencyTypeBox.Entry entry : box.getEntries() ) {
         addTableRow( gridLayout, getDisplayForObject( entry.getReserved() ), getDisplayForObject( entry.getSampleDependsOn() ), getDisplayForObject( entry.getSampleHasRedundancy() ), getDisplayForObject( entry.getSampleIsDependentOn() ) );

         if ( gridLayout.getRowCount() > 200 ) {
            skippedRows = box.getEntries().size() - 200;
            break;
         }
      }

      HorizontalScrollView scrollView = new HorizontalScrollView( getContext() );
      scrollView.addView( gridLayout );
      mBaseLayout.addView( scrollView );

      if ( skippedRows > 0 ) {

         RobotoTextView text = new RobotoTextView( new ContextThemeWrapper( getContext(), R.style.CardValue ) );
         text.setText( String.format( Locale.US, "%d entries emmited", skippedRows ) );
         mBaseLayout.addView( text );
      }
   }
}
