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
      addTextView( "Version:", box.getVersion() );
      addTextView( "Flag:", box.getFlags() );
   }

   public void LoadBox( FileTypeBox box ) {
      Log.d( "this", "load FileTypeBox" );
      addTextView( "Major Brand:", box.getMajorBrand() );
      addTextView( "Version:", box.getMinorVersion() );
      addTextView( "Compatible Brands:", box.getCompatibleBrands() );
   }

   public void LoadBox( MovieBox box ) {

      addTextView( "Track Count:", box.getTrackCount() );
      addTextView( "Track Numbers:", box.getTrackNumbers() );
   }

   public void LoadBox( MovieHeaderBox box ) {

      addTextView( "Timescale:", box.getTimescale() );
      addTextView( "Duration:", box.getDuration() );
      addTextView( "Rate:", box.getRate() );
      addTextView( "Volume:", box.getVolume() );
      addTextView( "Creation Time:", box.getCreationTime() );
      addTextView( "Modification Time:", box.getModificationTime() );
      addTextView( "Poster Time:", box.getPosterTime() );
      addTextView( "Current Time:", box.getCurrentTime() );
      addTextView( "Preview Duration:", box.getPreviewDuration() );
      addTextView( "Preview Time:", box.getPreviewTime() );
      addTextView( "Selection Duration:", box.getSelectionDuration() );
      addTextView( "Selection Time:", box.getSelectionTime() );
      addTextView( "Next Track Id:", box.getNextTrackId() );

      addMatrixView( "Matrix:", box.getMatrix() );
   }

   public void LoadBox( TrackBox box ) {

   }

   public void LoadBox( TrackHeaderBox box ) {
      addTextView( "Creation Time:", box.getCreationTime() );
      addTextView( "Duration:", box.getDuration() );
      addTextView( "Layer:", box.getLayer() );
      addTextView( "Modification Time:", box.getModificationTime() );
      addTextView( "Track ID:", box.getTrackId() );
      addTextView( "Volume:", box.getVolume() );
      addTextView( "Width:", box.getWidth() );
      addTextView( "Height:", box.getHeight() );
      addTextView( "Alternate Group:", box.getAlternateGroup() );

      addMatrixView( "Matrix:", box.getMatrix() );
   }

   public void LoadBox( MediaBox box ) {
   }

   public void LoadBox( MediaHeaderBox box ) {
      addTextView( "Creation Time:", box.getCreationTime() );
      addTextView( "Duration:", box.getDuration() );
      addTextView( "Language:", box.getLanguage() );
      addTextView( "Modification Time:", box.getModificationTime() );
      addTextView( "Timescale:", box.getTimescale() );
   }

   public void LoadBox( EditListBox box ) {
      addTextView( "Entry Count", box.getEntries().size() );

      for ( EditListBox.Entry entry : box.getEntries() ) {
         addTextView( "Segment Duration:", entry.getSegmentDuration() );
         addTextView( "Media Time:", entry.getMediaTime() );
         addTextView( "Media Rate:", entry.getMediaRate() );
      }
   }

   public void LoadBox( HandlerBox box ) {
      addTextView( "Handler Type:", box.getHandlerType() );
      addTextView( "Human Readable Track Type:", box.getHumanReadableTrackType() );
      addTextView( "Name:", box.getName() );
   }

   public void LoadBox( MediaInformationBox box ) {

   }

   public void LoadBox( VideoMediaHeaderBox box ) {
      addTextView( "Graphics Mode:", box.getGraphicsmode() );
      addTextView( "Op Color:", box.getOpcolor() );
   }

   public void LoadBox( SoundMediaHeaderBox box ) {
      addTextView( "Balance:", box.getBalance() );
   }

   public void LoadBox( DataInformationBox box ) {

   }

   public void LoadBox( DataReferenceBox box ) {
      addTextView( "Version:", box.getVersion() );
      addTextView( "Flags:", box.getFlags() );
      addTextView( "Size:", box.getSize() );
   }

   public void LoadBox( DataEntryUrlBox box ) {
   }

   public void LoadBox( SampleTableBox box ) {

   }

   public void LoadBox( SampleDescriptionBox box ) {
      addTextView( "Version:", box.getVersion() );
      addTextView( "Flags:", box.getFlags() );
      addTextView( "Size:", box.getSize() );
   }

   public void LoadBox( VisualSampleEntry box ) {
      addTextView( "Compressor Name:", box.getCompressorname() );
      addTextView( "Depth:", box.getDepth() );
      addTextView( "Frame Count:", box.getFrameCount() );
      addTextView( "Size:", box.getSize() );
      addTextView( "Width:", box.getWidth() );
      addTextView( "Height:", box.getHeight() );
      addTextView( "Horizontal Resolution:", box.getHorizresolution() );
      addTextView( "Vertical Resolution:", box.getVertresolution() );
   }

   public void LoadBox( AudioSampleEntry box ) {
      addTextView( "Bytes Per Frame:", box.getBytesPerFrame() );
      addTextView( "Bytes Per Packet:", box.getBytesPerPacket() );
      addTextView( "Bytes Per Sample:", box.getBytesPerSample() );
      addTextView( "Channel Count:", box.getChannelCount() );
      addTextView( "Compression Id:", box.getCompressionId() );
      addTextView( "Packet Size:", box.getPacketSize() );
      addTextView( "Reserved 1:", box.getReserved1() );
      addTextView( "Reserved 2:", box.getReserved2() );
      addTextView( "Sample Rate:", box.getSampleRate() );
      addTextView( "Sample Size:", box.getSampleSize() );
      addTextView( "Samples Per Packet:", box.getSamplesPerPacket() );
      addTextView( "Size:", box.getSize() );
      addTextView( "Sound Version:", box.getSoundVersion() );
      addTextView( "Sound Version 2 Data:", box.getSoundVersion2Data() );
   }

   public void LoadBox( AvcConfigurationBox box ) {

      AVCDecoderConfigurationRecord record = box.getavcDecoderConfigurationRecord();
      addTextView( "Record Content Size:", record.getContentSize() );
      addTextView( "Record Picture Parameter Sets:", record.getPictureParameterSetsAsStrings() );
      addTextView( "Record PPS:", record.getPPS() );
      addTextView( "Record Sequence Parameter Sets Exts:", record.getSequenceParameterSetExtsAsStrings() );
      addTextView( "Record Sequence Parameter Sets:", record.getSequenceParameterSetsAsStrings() );
      addTextView( "Record SPS:", record.getSPS() );

      addTextView( "AVC Level:", box.getAvcLevelIndication() );
      addTextView( "AVC Profile:", box.getAvcProfileIndication() );
      addTextView( "Bit Depth Chroma Minus 8:", box.getBitDepthChromaMinus8() );
      addTextView( "Bit Depth Luma Minus 8:", box.getBitDepthLumaMinus8() );
      addTextView( "Chroma Format:", box.getChromaFormat() );
      addTextView( "Configuration Version:", box.getConfigurationVersion() );
      addTextView( "Content Size:", box.getContentSize() );
      addTextView( "Length Size Minus One", box.getLengthSizeMinusOne() );
      addTextView( "Picture Parameter Sets:", box.getPictureParameterSets() );
      addTextView( "PPS:", box.getPPS() );
      addTextView( "Profile Compatibility:", box.getProfileCompatibility() );
      addTextView( "Sequence Parameter Set Exts:", box.getSequenceParameterSetExts() );
      addTextView( "Sequence Parameter Sets:", box.getSequenceParameterSets() );
      addTextView( "SPS:", box.getSPS() );
   }

   public void LoadBox( BitRateBox box ) {
      addTextView( "Average BitRate:", box.getAvgBitrate() );
      addTextView( "Buffer Size Db:", box.getBufferSizeDb() );
      addTextView( "Max Birate:", box.getMaxBitrate() );
   }

   public void LoadBox( ESDescriptorBox box ) {
      // addTextView( "", box.getEsDescriptor());
      // addTextView( "", box.getDescriptor());
      addTextView( "Descriptor As String:", box.getDescriptorAsString() );
   }

   public void LoadBox( TimeToSampleBox box ) {
      addTextView( "Entries:", box.getEntries() );
   }

   public void LoadBox( CompositionTimeToSample box ) {
      addTextView( "Entries:", box.getEntries() );
   }

   public void LoadBox( SyncSampleBox box ) {
      addTextView( "Sample Number:", box.getSampleNumber() );
   }

   public void LoadBox( SampleToChunkBox box ) {
      addTextView( "Entries:", box.getEntries() );
   }

   public void LoadBox( SampleSizeBox box ) {
      addTextView( "Sample Count:", box.getSampleCount() );
      addTextView( "Sample Size:", box.getSampleSize() );
      addTextView( "Sample Sizes:", box.getSampleSizes() );
   }

   public void LoadBox( StaticChunkOffsetBox box ) {
      addTextView( "Chunk Offsets:", box.getChunkOffsets() );
   }

   public void LoadBox( UnknownBox box ) {
   }

   public void LoadBox( UserDataBox box ) {

   }

   public void LoadBox( MetaBox box ) {
      addTextView( "Version:", box.getVersion() );
      addTextView( "Flags:", box.getFlags() );
      addTextView( "Size:", box.getSize() );
   }

   public void LoadBox( AppleItemListBox box ) {
   }

   public void LoadBox( MediaDataBox box ) {
      addTextView( "Offset:", box.getOffset() );
      addTextView( "Parent:", box.getParent() );
      addTextView( "Size:", box.getSize() );
      addTextView( "Type:", box.getType() );
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
      TextView view = new TextView( getContext() );

      Object object = field.get( box );
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
      }


      return String.format( Locale.US, "Unknown type: '%s'", object.getClass().getSimpleName() );
   }
}
