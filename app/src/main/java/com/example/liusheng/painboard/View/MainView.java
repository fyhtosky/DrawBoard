//package com.example.liusheng.painboard.View;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.BlurMaskFilter;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.PointF;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuff.Mode;
//import android.graphics.PorterDuffXfermode;
//import android.graphics.RectF;
//import android.graphics.drawable.BitmapDrawable;
//import android.util.AttributeSet;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//
//import android.widget.FrameLayout;
//import android.widget.Toast;
//
//import com.example.liusheng.painboard.Activity.DrawingActivity;
//import com.example.liusheng.painboard.R;
//import com.example.liusheng.painboard.draw.BasicGeometry;
//import com.example.liusheng.painboard.draw.ColorUtils;
//import com.example.liusheng.painboard.draw.DrawAttribute;
//import com.example.liusheng.painboard.draw.StickerBitmap;
//import com.example.liusheng.painboard.draw.StickerBitmapList;
//import com.example.liusheng.painboard.Tools.StorageInSDCard;
//import com.example.liusheng.painboard.draw.UndoAndRedo;
//
//
//public class MainView extends View implements Runnable {
//    private final int VISIBLE_BTN_WIDTH = 60;
//    private final int VISIBLE_BTN_HEIGHT = 40;
//
//    private enum TouchLayer {GEOMETRY_LAYER, PAINT_LAYER, STICKER_BITMAP, STICKER_TOOL, VISIBLE_BTN}
//
//    private Bitmap backgroundBitmap = null;
//    private PointF backgroundBitmapLeftTopP = null;
//    private Bitmap paintBitmap = null;
//    private Bitmap bufferBitmap = null;
//    private Canvas paintCanvas = null;
//    private Canvas bufferCanvas = null;
//    private BasicGeometry basicGeometry = null;
//    private StickerBitmapList stickerBitmapList = null;
//    private GestureDetector brushGestureDetector = null;
//    private BrushGestureListener brushGestureListener = null;
//    public DrawAttribute.DrawStatus drawStatus;
//    public DrawAttribute.Size drawSize;
//    private TouchLayer touchLayer;
//    private UndoAndRedo undoAndRedo;
//    private Context context;
//    private Path strokePath;
//    public Paint whitePaint, strokePaint, normalPaint;
//    private float mLastX;
//    private float mLastY;
//    RectF rect;
//    float downX, downY, x, y;
//
//    public MainView(Context context, AttributeSet attributeSet) {
//        super(context, attributeSet);
//        this.context = context;
//        backgroundBitmap = DrawAttribute.getImageFromAssetsFile(context, "bg_9.png", true);
//        backgroundBitmapLeftTopP = new PointF(0, 0);
//        paintBitmap = Bitmap.createBitmap(DrawAttribute.screenWidth,
//                DrawAttribute.screenHeight, Bitmap.Config.ARGB_8888);
//        bufferBitmap = Bitmap.createBitmap(DrawAttribute.screenWidth,
//                DrawAttribute.screenHeight, Bitmap.Config.ARGB_8888);
//        paintCanvas = new Canvas(paintBitmap);
//        bufferCanvas = new Canvas(bufferBitmap);
//        paintCanvas.drawARGB(0, 255, 255, 255);
//        stickerBitmapList = new StickerBitmapList(this);
//        this.drawStatus = DrawAttribute.DrawStatus.CASUAL_WATER;
//        undoAndRedo = new UndoAndRedo();
//
//        brushGestureListener = new BrushGestureListener
//                (casualStroke(R.drawable.marker, Color.BLACK), 2, null);
//        brushGestureDetector = new GestureDetector(brushGestureListener);
//        new Thread(this).start();
//        initDraw();
//
//    }
//
//    private void initDraw() {
//        drawSize = DrawAttribute.Size.zhong;
//        strokePath = new Path();
//        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
//
//        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
//
//        normalPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
//
//        whitePaint.setStyle(Paint.Style.STROKE);
//        whitePaint.setColor(Color.WHITE);
//        whitePaint.setStrokeWidth(6);
//        whitePaint.setStrokeCap(Paint.Cap.ROUND);
//        whitePaint.setStrokeJoin(Paint.Join.ROUND);
//        whitePaint.setAntiAlias(true);//由于降低密度绘制，所以需要抗锯齿
//        whitePaint.setFilterBitmap(true);
//        whitePaint.setDither(true);
//
//
//        strokePaint.setDither(true);
//        strokePaint.setFilterBitmap(true);
//        strokePaint.setStyle(Paint.Style.STROKE);
//        strokePaint.setStrokeCap(Paint.Cap.ROUND);
//        strokePaint.setStrokeJoin(Paint.Join.ROUND);
//        strokePaint.setAntiAlias(true);//由于降低密度绘制，所以需要抗锯齿
//        strokePaint.setStrokeWidth(18);
////        strokePaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL));
//
//        normalPaint.setStyle(Paint.Style.STROKE);
//        normalPaint.setStrokeWidth(8);
//        normalPaint.setStrokeCap(Paint.Cap.ROUND);
//        normalPaint.setStrokeJoin(Paint.Join.ROUND);
//        normalPaint.setAntiAlias(true);//由于降低密度绘制，所以需要抗锯齿
//        normalPaint.setColor(Color.RED);
//        normalPaint.setDither(true);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        canvas.drawColor(Color.WHITE);
//        canvas.drawBitmap(backgroundBitmap, backgroundBitmapLeftTopP.x,
//                backgroundBitmapLeftTopP.y, null);
//        if (bufferBitmap != null) {
//            canvas.drawBitmap(bufferBitmap, 0, 0, null);
//        }
//        canvas.drawBitmap(paintBitmap, 0, 0, null);
//        if (basicGeometry != null) {
//            basicGeometry.drawGraphic(canvas);
//        }
//        // 绘制所有贴图和工具
//        stickerBitmapList.drawStickerBitmapList(canvas);
//        // 绘制右上角的工具箱
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        x = event.getX();
//        y = event.getY();
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            downX = x;
//            downY = y;
//            rect = new RectF(downX, downY, downX, downY);
//            strokePaint.setColor(ColorUtils.randomColor());
//            // 普通画笔
//            normalPaint.setColor(ColorUtils.randomColor());
//            mLastX = x;
//            mLastY = y;
//            if (strokePath == null) {
//                strokePath = new Path();
//            }
//            strokePath.moveTo(x, y);
//
//            if (isClickOnVisibleBtn(x, y)) {
//                touchLayer = TouchLayer.VISIBLE_BTN;
//                return true;
//            }
//            int touchType = stickerBitmapList.getOnTouchType(x, y);
//            switch (touchType) {
//                case 1:
//                    touchLayer = TouchLayer.STICKER_TOOL;
//                    return true;
//                case 0:
//                    touchLayer = TouchLayer.STICKER_BITMAP;
//                    break;
//                case -1:
//                    if (basicGeometry != null && basicGeometry.isPointInsideGeometry
//                            (event.getX(), event.getY())) {
//                        touchLayer = TouchLayer.GEOMETRY_LAYER;
//                    } else {
//                        touchLayer = TouchLayer.PAINT_LAYER;
//                    }
//            }
//        }
//        // 移动
//        else {
//            if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                rect = new RectF(downX, downY, x, y);
//                if (drawStatus == DrawAttribute.DrawStatus.CIRCLE || drawStatus == DrawAttribute.DrawStatus.JUXING
//                        || drawStatus == DrawAttribute.DrawStatus.XIAN) {
//                    DrawingActivity.trajectoryView.setVisibility(View.VISIBLE);
//                    DrawingActivity.trajectoryView.downX = downX;
//                    DrawingActivity.trajectoryView.downY = downY;
//                    DrawingActivity.trajectoryView.x = x;
//                    DrawingActivity.trajectoryView.y = y;
//                    DrawingActivity.trajectoryView.mPaint = normalPaint;
//                    DrawingActivity.trajectoryView.mode = drawStatus;
//                    DrawingActivity.trajectoryView.invalidate();
//                }
//
//                if (drawStatus == DrawAttribute.DrawStatus.FLUORE) {
//                    //这里终点设为两点的中心点的目的在于使绘制的曲线更平滑，如果终点直接设置为x,y，效果和lineto是一样的,实际是折线效果
//                    // 荧光画笔
//                    strokePath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
//                    paintCanvas.drawPath(strokePath, strokePaint);
//                    paintCanvas.drawPath(strokePath, whitePaint);
//                    invalidate();
//                } else {
//                    if (drawStatus == DrawAttribute.DrawStatus.NORMAL) {
//                        // 普通画笔
//                        strokePath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
//                        paintCanvas.drawPath(strokePath, normalPaint);
//                    }
//                }
//                invalidate();
//
//                mLastX = x;
//                mLastY = y;
//            } else if (event.getAction() == MotionEvent.ACTION_UP) {
//
//                if (drawStatus == DrawAttribute.DrawStatus.CIRCLE || drawStatus == DrawAttribute.DrawStatus.JUXING
//                        || drawStatus == DrawAttribute.DrawStatus.XIAN) {
//                    DrawingActivity.trajectoryView.setVisibility(View.GONE);
//
//                }
//                if (drawStatus == DrawAttribute.DrawStatus.JUXING) {
//                    // 普通画笔
//                    paintCanvas.drawRect(rect, normalPaint);
//                } else if (drawStatus == DrawAttribute.DrawStatus.CIRCLE) {
//                    paintCanvas.drawOval(rect, normalPaint);
//                } else if (drawStatus == DrawAttribute.DrawStatus.XIAN) {
//                    paintCanvas.drawLine(downX, downY, x, y, normalPaint);
//                }
//                strokePath.reset();
//            }
//        }
//        //��ͼ��ļ���
//        if (touchLayer == TouchLayer.PAINT_LAYER) {
//            brushGestureDetector.onTouchEvent(event);
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                undoAndRedo.addBitmap(paintBitmap);
//            }
//        }
//        //����ͼ�εļ���
//        else if (touchLayer == TouchLayer.GEOMETRY_LAYER) {
//            basicGeometry.onTouchEvent(event);
//        }
//        //��ͼ�ļ���
//        else if (touchLayer == TouchLayer.STICKER_BITMAP) {
//            stickerBitmapList.onTouchEvent(event);
//        }
//        return true;
//    }
//
//    private boolean isClickOnVisibleBtn(float x, float y) {
//        if (x > DrawAttribute.screenWidth - VISIBLE_BTN_WIDTH && x < DrawAttribute.screenWidth
//                && y < VISIBLE_BTN_HEIGHT) {
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public void run() {
//        while (!Thread.currentThread().isInterrupted()) {
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//            postInvalidate();
//        }
//    }
//
//    public void setBackgroundBitmap(Bitmap bitmap, boolean isFromSystem) {
//        if (isFromSystem) {
//            backgroundBitmap = bitmap;
//            backgroundBitmapLeftTopP.set(0, 0);
//        } else {
//            float scaleWidth = bitmap.getWidth() * 1.0f / DrawAttribute.screenWidth;
//            float scaleHeight = bitmap.getHeight() * 1.0f / DrawAttribute.screenHeight;
//            float scale = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;
//            if (scale > 1.01)
//                backgroundBitmap = Bitmap.createScaledBitmap(bitmap,
//                        (int) (bitmap.getWidth() / scale), (int) (bitmap.getHeight() / scale), false);
//            else {
//                backgroundBitmap = bitmap;
//            }
//            backgroundBitmapLeftTopP.x = (DrawAttribute.screenWidth - backgroundBitmap.getWidth()) / 2;
//            backgroundBitmapLeftTopP.y = (DrawAttribute.screenHeight - backgroundBitmap.getHeight()) / 2;
//
//        }
//    }
//
//    public void cleanPaintBitmap() {
//        paintCanvas.drawColor(0xffffffff, Mode.DST_OUT);
//    }
//
//    public void recordPaintBitmap(Bitmap bitmap) {
//        undoAndRedo.addBitmap(bitmap);
//    }
//
//    public void undo() {
//        if (!undoAndRedo.currentIsFirst()) {
//            undoAndRedo.undo(paintBitmap);
//        }
//    }
//
//    public void redo() {
//        if (!undoAndRedo.currentIsLast()) {
//            undoAndRedo.redo(paintBitmap);
//        }
//    }
//
//    public void setBasicGeometry(BasicGeometry geometry) {
//        if (basicGeometry != null) {
//            basicGeometry.drawGraphic(paintCanvas);
//        }
//        this.basicGeometry = geometry;
//    }
//
//    public void addStickerBitmap(Bitmap bitmap) {
//        stickerBitmapList.setIsStickerToolsDraw(false, null);
//        if (!stickerBitmapList.addStickerBitmap(new StickerBitmap(this, stickerBitmapList, bitmap))) {
//
//            Toast.makeText(context, "��ͼ̫���ˣ�", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public Canvas getPaintCanvas() {
//        return paintCanvas;
//    }
//
//    private Bitmap casualStroke(int drawableId, int color) {
//        Bitmap mode = ((BitmapDrawable) this.getResources().getDrawable(drawableId)).getBitmap();
//        Bitmap bitmap = mode.copy(Bitmap.Config.ARGB_8888, true);
//        Canvas canvas = new Canvas();
//        canvas.setBitmap(bitmap);
//        Paint paintUnder = new Paint();
//        paintUnder.setColor(color);
//        canvas.drawPaint(paintUnder);
//        Paint paint = new Paint();
//        paint.setFilterBitmap(true);
//        paint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
//        canvas.drawBitmap(mode, 0, 0, paint);
//        return bitmap;
//    }
//
//    public void setBrushBitmap(DrawAttribute.DrawStatus drawStatus, int extraData) {
//        this.drawStatus = drawStatus;
//        Bitmap brushBitmap = null;
//        int brushDistance;
//        Paint brushPaint;
//        //4
//        if (drawStatus == DrawAttribute.DrawStatus.CASUAL_WATER) {
//
//            brushDistance = 1;
//            brushPaint = null;
//
//        }//多种样式---2
//        else if (drawStatus == DrawAttribute.DrawStatus.FLUORE_02) {
////            brushBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.fluore_02);
////            brushBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.fluore_03);
//            brushDistance = 1;
//            brushPaint = null;
//        } else if (drawStatus == DrawAttribute.DrawStatus.FLUORE_STAR) {
//            brushBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.fluore_star);
//            brushDistance = 1;
//            brushPaint = null;
//        }
//        //第二种，蜡笔
//        else if (drawStatus == DrawAttribute.DrawStatus.CASUAL_CRAYON) {
//
//            brushDistance = brushBitmap.getWidth() / 3;
//            brushPaint = null;
//        }
//        //3
//        else if (drawStatus == DrawAttribute.DrawStatus.CASUAL_COLOR_SMALL) {
//
//            brushDistance = 5;
//            brushPaint = null;
//        }
//
//
//        // 橡皮
//        else //if(drawStatus == DrawAttribute.DrawStatus.ERASER)
//        {
//            brushPaint = new Paint();
//            brushPaint.setFilterBitmap(true);
//            brushPaint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
//            switch (extraData) {
//                case 0:
//                    //小
//                    brushBitmap = ((BitmapDrawable) this.getResources().
//                            getDrawable(R.drawable.eraser)).getBitmap();
//                    break;
//                case 1:
//                    //中
//                    brushBitmap = ((BitmapDrawable) this.getResources().
//                            getDrawable(R.drawable.eraser1)).getBitmap();
//                    break;
//                default:
//                    //大
//                    brushBitmap = ((BitmapDrawable) this.getResources().
//                            getDrawable(R.drawable.eraser)).getBitmap();
//                    break;
//            }
//            brushDistance = brushBitmap.getWidth() / 4;
//        }
//        brushGestureListener.setBrush(brushBitmap, brushDistance, brushPaint);
//    }
//
//    class BrushGestureListener extends GestureDetector.SimpleOnGestureListener {
//
//        private Bitmap brushBitmap = null;
//        private int brushDistance;
//        private int halfBrushBitmapWidth;
//        private Paint brushPaint = null;
//        //ӡ����bitmap
//        private Bitmap[] stampBrushBitmaps = null;
//        private boolean isStamp;
//
//
//        public BrushGestureListener(Bitmap brushBitmap, int brushDistance, Paint brushPaint) {
//            super();
//            setBrush(brushBitmap, brushDistance, brushPaint);
//            isStamp = false;
//        }
//
//
//        @Override
//        public boolean onDown(MotionEvent e) {
//
//            switch (drawStatus) {
//                case FLUORE_02:
//                case FLUORE_6:
//                case FLUORE_7:
//                case FLUORE_8:
//                case FLUORE_9:
//
//                case CASUAL_WATER:
//                case CASUAL_CRAYON:
//                case CASUAL_COLOR_SMALL:
//                case CASUAL_COLOR_BIG:
//                case ERASER:
//                    isStamp = false;
//                    setBrushBitmap(drawStatus, ColorUtils.randomColor());
//                    paintCanvas.drawBitmap(brushBitmap, e.getX() - halfBrushBitmapWidth,
//                            e.getY() - halfBrushBitmapWidth, brushPaint);
//                    break;
//                case STAMP_BUBBLE:
//                case STAMP_DOTS:
//                case STAMP_HEART:
//                case STAMP_STAR:
//                    isStamp = true;
//                    paintSingleStamp(e.getX(), e.getY());
//
//                    break;
//                case WORDS:
//
//                    break;
//            }
//
//            return true;
//        }
//
//        /**
//         * @param e1        The first down motion event that started the scrolling.
//         * @param e2        The move motion event that triggered the current onScroll.
//         * @param distanceX The distance along the X axis(轴) that has been scrolled since the last call to onScroll. This is NOT the distance between e1 and e2.
//         * @param distanceY The distance along the Y axis that has been scrolled since the last call to onScroll. This is NOT the distance between e1 and e2.
//         *                  无论是用手拖动view，或者是以抛的动作滚动，都会多次触发 ,这个方法在ACTION_MOVE动作发生时就会触发 参看GestureDetector的onTouchEvent方法源码
//         */
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            float beginX = e2.getX();
//            float beginY = e2.getY();
//
//            switch (drawStatus) {
//                case FLUORE_STAR:
//                    paintCanvas.drawBitmap(brushBitmap, beginX - halfBrushBitmapWidth,
//                            beginY - halfBrushBitmapWidth, brushPaint);
//                    break;
//                case FLUORE_6:
//                case FLUORE_7:
//                case FLUORE_8:
//                case FLUORE_9:
//                case FLUORE_02:
//                case CASUAL_WATER:
//                case CASUAL_CRAYON:
//                case CASUAL_COLOR_SMALL:
//                case CASUAL_COLOR_BIG:
//                case ERASER:
//                    isStamp = false;
//
//                    float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);
//                    float x = distanceX / distance, x_ = 0;
//                    float y = distanceY / distance, y_ = 0;
//                    while (Math.abs(x_) <= Math.abs(distanceX) && Math.abs(y_) <= Math.abs(distanceY)) {
//                        x_ += x * brushDistance;
//                        y_ += y * brushDistance;
//                        paintCanvas.save();
//                        paintCanvas.rotate((float) (Math.random() * 10000), beginX + x_, beginY + y_);
//                        paintCanvas.drawBitmap(brushBitmap, beginX + x_ - halfBrushBitmapWidth,
//                                beginY + y_ - halfBrushBitmapWidth, brushPaint);
//                        paintCanvas.restore();
//                    }
//                    break;
////                case JUXING:
////
////                    Rect rect = new Rect((int)e1.getX(), (int)e1.getY(), (int)e2.getX(),(int) e2.getY());
////                    paintCanvas.drawRect(rect, normalPaint);
////                    break;
//                case STAMP_BUBBLE:
//                case STAMP_DOTS:
//                case STAMP_HEART:
//                case STAMP_STAR:
//                    isStamp = true;
//                    paintSingleStamp(e2.getX(), e2.getY());
//                    break;
//                case WORDS:
//
//
//                    break;
//            }
//
//            if (!isStamp) {
//
//            } else {
//            }
//            return true;
//        }
//
//        public void setBrush(Bitmap brushBitmap, int brushDistance, Paint brushPaint) {
//            this.brushBitmap = brushBitmap;
//            this.brushDistance = brushDistance;
//            halfBrushBitmapWidth = brushBitmap.getWidth() / 2;
//            this.brushPaint = brushPaint;
//        }
//
//        public void setStampBrush(Bitmap[] brushBitmaps) {
//            this.stampBrushBitmaps = brushBitmaps;
//            halfBrushBitmapWidth = brushBitmaps[0].getWidth() / 2;
//        }
//
//        private void paintSingleStamp(float x, float y) {
//            if (Math.random() > 0.1) {
//                paintCanvas.drawBitmap(stampBrushBitmaps[0], x - halfBrushBitmapWidth,
//                        y - halfBrushBitmapWidth, null);
//            }
//            for (int i = 1; i < stampBrushBitmaps.length; i++)
//                if (Math.random() > 0.3) {
//                    paintCanvas.drawBitmap(stampBrushBitmaps[i], x - halfBrushBitmapWidth,
//                            y - halfBrushBitmapWidth, null);
//                }
//        }
//    }
//
//    public void saveBitmap(boolean share) {
//
//        Bitmap bitmap = Bitmap.createBitmap(DrawAttribute.screenWidth,
//                DrawAttribute.screenHeight, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        canvas.drawColor(Color.WHITE);
//        canvas.drawBitmap(backgroundBitmap, backgroundBitmapLeftTopP.x,
//                backgroundBitmapLeftTopP.y, null);
//        canvas.drawBitmap(paintBitmap, 0, 0, null);
//        if (basicGeometry != null) {
//            basicGeometry.drawGraphic(canvas);
//        }
//        stickerBitmapList.drawStickerBitmapList(canvas);
//        StorageInSDCard.saveBitmapInExternalStorage(bitmap, context, share);
//    }
//
//    public void freeBitmaps() {
//        paintCanvas = null;
//        backgroundBitmap.recycle();
//        paintBitmap.recycle();
//        stickerBitmapList.freeBitmaps();
//        undoAndRedo.freeBitmaps();
//    }
//
//
//}
