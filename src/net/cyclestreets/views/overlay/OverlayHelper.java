package net.cyclestreets.views.overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class OverlayHelper 
{
	static int offset(final Context context)
	{
		return (int)(8 * context.getResources().getDisplayMetrics().density);
 	} // offset
	
	static float cornerRadius(final Context context)
	{
		return 4.0f * context.getResources().getDisplayMetrics().density;
	} // cornerRadius
	
	static void drawRoundRect(final Canvas canvas, 
							  final Rect rect, 
							  final float cornerRadius, 
							  final Paint brush)
	{
		canvas.drawRoundRect(new RectF(rect), cornerRadius, cornerRadius, brush);
	} // drawRoundRect
} // class OverlayHelper
