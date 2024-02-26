package com.example.aptonia.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.aptonia.R;

import java.util.ArrayList;
import java.util.Timer;

public class LoadingCircle extends View{

    Point[] points;

    Point[] targetPoints;

    int[] targets = {0, 1, 5, 6, 2, 3, 7, 11, 15, 14, 10, 9, 13, 12, 8, 4};

    int move = 1;

    Paint paint;

    TypedArray attrs;

    public LoadingCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.paint = new Paint();
        this.attrs = context.obtainStyledAttributes(attrs, R.styleable.LoadingCircle);

        paint.setColor(this.attrs.getColor(R.styleable.LoadingCircle_loading_color, Color.BLACK));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(20);

        points = null;
    }

    private void loadPoints() {
        points = new Point[16];
        targetPoints = new Point[16];

        int len = (int) (getWidth() / 3 - paint.getStrokeWidth());

        for (int i = 0; i < points.length; i++) {
            points[i] = new Point((int) (- 1.5 * len + (i / 4) * len), (int) (- 1.5 * len + (i % 4) * len));
            targetPoints[i] = new Point((int) (- 1.5 * len + (i / 4) * len), (int) (- 1.5 * len + (i % 4) * len));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (points == null) {
            loadPoints();
        }

        super.onDraw(canvas);

        canvas.translate(getWidth() / 2, getHeight() / 2);

        int skip;

        int x1;
        int x2;
        int y1;
        int y2;

        for (int i = 0; i < points.length; i++) {
            skip = 2;

            x1 = points[targets[i]].x;
            y1 = points[targets[i]].y;

            canvas.drawCircle(x1, y1, paint.getStrokeWidth() / 2, paint);

            x2 = points[targets[(i + skip) % points.length]].x;;
            y2 = points[targets[(i + skip) % points.length]].y;;

            canvas.drawLine(x1, y1, x2, y2, paint);

            skip = 1;

            x1 = points[targets[i]].x;
            y1 = points[targets[i]].y;

            x2 = points[targets[(i + skip) % points.length]].x;
            y2 = points[targets[(i + skip) % points.length]].y;

            //canvas.drawLine(x1, y1, x2, y2, paint);
        }

        update();

        invalidate();
    }

    private void update() {
        for (int i = 0; i < targets.length; i++) {
            Point actual = points[targets[i]];
            Point next = targetPoints[targets[(i + move) % points.length]];

            if (actual.x == next.x && actual.y == next.y) {
                move = (move + 1) % points.length;

                actual = new Point(next);
                next = targetPoints[targets[(i + move) % points.length]];
            }

            if (actual.x == next.x) {
                if (actual.y > next.y) {
                    actual.y--;
                }
                else {
                    actual.y++;
                }
            }
            else if (actual.y == next.y) {
                if (actual.x > next.x) {
                    actual.x--;
                }
                else {
                    actual.x++;
                }
            }
        }
    }

    @Override
    protected void onAnimationStart() {
        super.onAnimationStart();
    }

}
