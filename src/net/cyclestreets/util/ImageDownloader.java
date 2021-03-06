package net.cyclestreets.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageDownloader 
{
	public void get(final String url, final ImageView imageView) 
	{
		final BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
		task.execute(url);
	} // get

	//////////////////////////
	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> 
	{
		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(final ImageView imageView) 
		{
			imageViewReference = new WeakReference<ImageView>(imageView);
		} // BitmapDownloaderTask

		@Override
		protected Bitmap doInBackground(String... params) 
		{
			return downloadBitmap(params[0]);
		} // doInBackground

		@Override
		protected void onPostExecute(final Bitmap bitmap) 
		{
			if(isCancelled()) 
				return;
		  
			if(imageViewReference == null)
				return;
			
			final ImageView imageView = imageViewReference.get();
			if (imageView == null) 
				return;
			
			imageView.setImageBitmap(bitmap);
		} // onPostExecute

		private Bitmap downloadBitmap(final String url) 
		{
			final HttpClient client = new DefaultHttpClient();
			final HttpGet getRequest = new HttpGet(url);

			try 
			{
				HttpResponse response = client.execute(getRequest);
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) 
					return null;

				final HttpEntity entity = response.getEntity();
				if (entity == null) 
					return null;
				
				InputStream inputStream = null;
				try 
				{
					inputStream = entity.getContent();
					// return BitmapFactory.decodeStream(inputStream);
					// Bug on slow connections, fixed in future release.
					return BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
		        } // try
				finally 
				{
					if(inputStream != null)
						inputStream.close();
					entity.consumeContent();
				} // finally
			} // try 
			catch (Exception e) 
			{
				getRequest.abort();
			} // catch
			return null;
		} // downloadBitmap
	} // BitmapDownloaderTask 

    static class FlushedInputStream extends FilterInputStream 
    {
        public FlushedInputStream(final InputStream inputStream) 
        {
            super(inputStream);
        } // FlushedInputStream

        @Override
        public long skip(long n) throws IOException 
        {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) 
            {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) 
                {
                    int b = read();
                    if (b < 0) 
                        break;  // we reached EOF
                    else 
                        bytesSkipped = 1; // we read one byte
                } // if ...
                totalBytesSkipped += bytesSkipped;
            } // while ...
            return totalBytesSkipped;
        } // skip
    } // FlushedInputStream
} // ImageDownloader
