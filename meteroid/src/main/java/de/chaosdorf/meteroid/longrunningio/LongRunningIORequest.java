/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2016 Chaosdorf e.V.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/

package de.chaosdorf.meteroid.longrunningio;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;

import de.chaosdorf.meteroid.MeteroidNetworkActivity;

public class LongRunningIORequest<T>
{
	private final static String TAG = "LongRunningIO";

	public LongRunningIORequest(final MeteroidNetworkActivity callback, final LongRunningIOTask id, final Call<T> call)
	{
		Log.d(TAG, "Initiating call: " + call.request());
		call.enqueue(newCallback(callback, id));
	}
	
	protected Callback<T> newCallback(final MeteroidNetworkActivity callback, final LongRunningIOTask id)
	{
		return new Callback<T>()
		{
			@Override
			public void onFailure(final Call<T> call, final Throwable t)
			{
				Log.d(TAG, "Handling failure: " + call.request());
				callback.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						callback.displayErrorMessage(id, t.getLocalizedMessage());
					}
				});
			}

			@Override
			public void onResponse(final Call<T> call, final Response<T> resp)
			{
				Log.d(TAG, "Handling response: " + call.request());
				if(resp.isSuccessful())
				{
					try
					{
						final T response = resp.body();
						callback.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								callback.processIOResult(id, response);
							}
						});
					}
					catch(final Throwable t)
					{
						callback.runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								callback.displayErrorMessage(id, t.getLocalizedMessage());
							}
						});
					}
				}
				else
				{
					callback.runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							callback.displayErrorMessage(id, resp.message());
						}
					});
				}
			}
		};
	}
}
