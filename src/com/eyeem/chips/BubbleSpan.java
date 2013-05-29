package com.eyeem.chips;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.ReplacementSpan;

public class BubbleSpan extends ReplacementSpan {
   public Object data;
   public AwesomeBubble bubble;
   ChipsEditText et;
   int start;
   float baselineDiff;

   public BubbleSpan(AwesomeBubble bubble) {
      this.bubble = bubble;
   }

   public BubbleSpan(AwesomeBubble bubble, ChipsEditText et) {
      this.bubble = bubble;
      this.et = et;
   }

   @Override
   public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
      this.start = start;
      canvas.save();

      //int transY = bottom - d.getBounds().bottom - paint.getFontMetricsInt().descent;

      if (et != null) {
         // we do this because for some Androids the first line's bubbles
         // get cut off by inner scroll boundary - we redraw those
         // bubbles after onDraw passes
         // ...also edittext's layouting is erratic and baselineDiff is something
         // I have trouble calculating
         if (et.getLayout().getLineForOffset(start) == 0) {
            // especially firstline has bigger next line space than others
            et.redrawStack.add(this);
            baselineDiff = ((float)bubble.style.bubblePadding) * -2.0f;
         } else {
            baselineDiff = ((float)bubble.style.bubblePadding) * -1.5f;
         }
      } else {
         // this is for ChipsTextView which base on StaticLayout... much less
         // troubles here
         baselineDiff = ((float)bubble.style.bubblePadding) * 0.6f;
      }
      float transY = top - baselineDiff;

      canvas.translate(x, transY);
      bubble.draw(canvas);
      canvas.restore();
   }

   public void redraw(Canvas canvas) {
      if (et.getScrollY() != 0)
         return;

      int pos = et.getText().getSpanStart(this);
      if (pos == -1)
         return;
      Layout layout = et.getLayout();
      int line = layout.getLineForOffset(pos);
      float x = layout.getPrimaryHorizontal(pos);
      float y = layout.getLineTop(line);
      x += et.getPaddingLeft();
      y += et.getPaddingTop();

      canvas.save();
      canvas.translate(x, y - baselineDiff);
      bubble.draw(canvas);
      canvas.restore();
   }

   @Override
   public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
      return bubble.getWidth();
   }
}
