package cn.edu.xyc.contactlist.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;
import java.util.List;

import cn.edu.xyc.contactlist.R;

/**
 * 一个快速跳跃分组的侧边栏控件
 *
 * @author sjl
 */
public class WaveSideBar extends View {

    private static final String TAG = "WaveSideBar";

    /**
     * 渲染字母表(*,A,B,C...)
     */
    private List<String> mLetters;

    /**/

    /**
     * *,A,B,C... 这些字母的大小
     */
    private int mTextSize;

    /**
     * #,A,B,C... 这些字母的颜色
     */
    private int mTextColor;

    /**
     * #,A,B,C... 这些字母被选中时的颜色
     */
    private int mTextColorChoose;

    /**
     * 点击*,A,B,C... 这些字母时出现提示字母的大小
     */
    private int mHintTextSize;

    /**
     * 点击*,A,B,C... 这些字母时出现提示圆的颜色
     */
    private int mCircleColor;

    /**
     * 点击*,A,B,C... 这些字母时出现提示圆的半径
     */
    private int mCircleRadius;

    /**
     *
     */
    private int mPadding;

    /**/

    /**
     * WaveSideBar 控件的宽度
     */
    private int mWidth;

    /**
     * WaveSideBar 控件的高度
     */
    private int mHeight;

    /**
     * 每一个Item(*,A,B,C...)的高度
     */
    private int mItemHeight;

    /**/

    /**
     *
     */
    private int mOldPosition;

    /**
     *
     */
    private int mNewPosition;

    /**
     * 当前选中的位置
     */
    private int mChoosePosition = -1;

    /**
     * 选中字体的坐标
     */
    private float mPointX, mPointY;

    /**/

    /**
     * 绘制字母列表画笔
     */
    private Paint mLettersPaint;

    /**
     * 绘制提示圆画笔
     */
    private Paint mCirclePaint;

    /**
     * 绘制提示字母画笔
     */
    private Paint mTextPaint;

    /**
     * 绘制提示圆的圆形路径
     */
    private Path mCirclePath;

    /**/

    /**
     * 圆形中心点X
     */
    private float mCircleCenterX;

    /**
     * 手指滑动的Y点作为中心点
     * <p>
     * 中心点Y
     */
    private int mCenterY;

    /**/

    /**
     *
     */
    private float mAnimatedValue;

    /**
     *
     */
    private ValueAnimator mValueAnimator;

    public WaveSideBar(Context context) {
        this(context, null);
    }

    public WaveSideBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveSideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mCirclePath = new Path();
        mLetters = Arrays.asList(context.getResources().getStringArray(R.array.waveSideBarLetters));

        mTextSize = context.getResources().getDimensionPixelSize(R.dimen.textSize);
        mTextColor = ContextCompat.getColor(context, android.R.color.darker_gray);
        mTextColorChoose = ContextCompat.getColor(context, android.R.color.background_light);
        mHintTextSize = context.getResources().getDimensionPixelSize(R.dimen.hintTextSize);
        mCircleColor = ContextCompat.getColor(context, R.color.colorAccent);
        mPadding = context.getResources().getDimensionPixelSize(R.dimen.padding);

        if (null != attrs) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.waveSideBar);

            mTextSize = a.getDimensionPixelSize(R.styleable.waveSideBar_textSize, mTextSize);
            mTextColor = a.getColor(R.styleable.waveSideBar_textColor, mTextColor);
            mTextColorChoose = a.getColor(R.styleable.waveSideBar_chooseTextColor, mTextColorChoose);
            mHintTextSize = a.getDimensionPixelSize(R.styleable.waveSideBar_hintTextSize, mHintTextSize);
            mCircleColor = a.getColor(R.styleable.waveSideBar_backgroundColor, mCircleColor);

            mCircleRadius = a.getDimensionPixelSize(R.styleable.waveSideBar_circleRadius, context.getResources().getDimensionPixelSize(R.dimen.circleRadius));

            a.recycle();
        }

        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        // 字母列表画笔
        mLettersPaint = new Paint();
        mLettersPaint.setAntiAlias(true);
        mLettersPaint.setColor(mTextColor);
        mLettersPaint.setStyle(Paint.Style.STROKE);

        // 画提示圆的画笔
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);

        // 提示字母画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColorChoose);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mHintTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        // 每一个Item(*,A,B,C...)的高度
        mItemHeight = (mHeight - mPadding) / mLetters.size();
        // 10为边框距离屏幕的距离
        mPointX = mWidth - mTextSize - 10;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制字母列表
        drawLetters(canvas);

        // 绘制圆
        drawCirclePath(canvas);

        // 绘制选中的字母
        drawChooseText(canvas);
    }

    /**
     * 绘制字母列表
     *
     * @param canvas 画布对象
     */
    private void drawLetters(Canvas canvas) {

        RectF rectF = new RectF();
        rectF.left = mPointX - mTextSize;
        rectF.right = mPointX + mTextSize;
        rectF.top = mTextSize / 2;
        rectF.bottom = mHeight - mTextSize / 2;

        // 调用画布绘制圆角矩形的方法,绘制字母列表的边框
        canvas.drawRoundRect(rectF, mTextSize, mTextSize, mLettersPaint);

        // 重置字母列表画笔
        mLettersPaint.reset();
        mLettersPaint.setAntiAlias(true);
        mLettersPaint.setColor(getResources().getColor(android.R.color.white));
        mLettersPaint.setStyle(Paint.Style.FILL);
        // 调用画布绘制圆角矩形的方法,绘制字母列表的背景颜色
        canvas.drawRoundRect(rectF, mTextSize, mTextSize, mLettersPaint);

        // 重置字母列表画笔
        mLettersPaint.reset();
        mLettersPaint.setAntiAlias(true);
        mLettersPaint.setColor(mTextColor);
        mLettersPaint.setTextSize(mTextSize);
        mLettersPaint.setTextAlign(Paint.Align.CENTER);

        int size = mLetters.size();
        for (int i = 0; i < size; i++) {
            // 获取FontMetrics实例
            Paint.FontMetrics fontMetrics = mLettersPaint.getFontMetrics();
            float baseline = Math.abs(-fontMetrics.bottom - fontMetrics.top);
            float pointY = mItemHeight * i + baseline / 2 + mPadding;

            if (i == mChoosePosition) {
                mPointY = pointY;
            } else {
                canvas.drawText(mLetters.get(i), mPointX, pointY, mLettersPaint);
            }
        }
    }

    /**
     * 绘制圆
     *
     * @param canvas 画布对象
     */
    private void drawCirclePath(Canvas canvas) {

        // X轴的移动路径,即提示圆距离屏幕右侧的边距
        mCircleCenterX = (mWidth + mCircleRadius) - (mCircleRadius * 6.0f) * mAnimatedValue;
        mCirclePath.reset();
        mCirclePath.addCircle(mCircleCenterX, mCenterY, mCircleRadius, Path.Direction.CW);
        mCirclePath.close();
        canvas.drawPath(mCirclePath, mCirclePaint);
    }

    /**
     * 绘制选中的字母
     *
     * @param canvas 画布对象
     */
    private void drawChooseText(Canvas canvas) {
        if (mChoosePosition != -1) {
            // 重置字母列表画笔
            mLettersPaint.reset();
            mLettersPaint.setAntiAlias(true);
            mLettersPaint.setColor(mTextColorChoose);
            mLettersPaint.setTextSize(mTextSize);
            mLettersPaint.setTextAlign(Paint.Align.CENTER);

            // 调用画布绘制文字的方法,绘制选中的字母
            canvas.drawText(mLetters.get(mChoosePosition), mPointX, mPointY, mLettersPaint);

            // 绘制提示字母
            if (mAnimatedValue >= 0) {
                // 获取到被选中的英文字母
                String target = mLetters.get(mChoosePosition);

                Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
                float baseline = Math.abs(-fontMetrics.bottom - fontMetrics.top);
                float x = mCircleCenterX;
                float y = mCenterY + baseline / 2;
                canvas.drawText(target, x, y, mTextPaint);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // 获取当前Widget相对于自身左上角的x坐标
        final float x = event.getX();
        // 获取当前Widget相对于自身左上角的y坐标
        final float y = event.getY();
        //
        mOldPosition = mChoosePosition;
        //
        mNewPosition = (int) (y / mItemHeight);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 限定手指在X轴触摸范围
                if (mWidth - x > (mCircleRadius)) {
                    return false;
                }
                mCenterY = (int) y;
                startAnimator(1.0f);
                break;
            case MotionEvent.ACTION_MOVE:
                mCenterY = (int) y;
                if (mOldPosition != mNewPosition && mNewPosition >= 0 && mNewPosition < mLetters.size()) {
                    mChoosePosition = mNewPosition;
                    if (null != mListener) {
                        mListener.onLetterChange(mLetters.get(mNewPosition));
                    }
                }
                // 调用postInvalidate()方法,请求重新draw()
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                startAnimator(0f);
                mChoosePosition = -1;
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 产生值的变化为0 - value的随机数
     *
     * @param value 变化的最大值
     */
    private void startAnimator(float value) {
        if (mValueAnimator == null) {
            mValueAnimator = new ValueAnimator();
        }

        mValueAnimator.cancel();
        // 设置值的变化为0 - value
        mValueAnimator.setFloatValues(0, value);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator value) {
                mAnimatedValue = (float) value.getAnimatedValue();
                // 点击的位置变了，即点击的时候显示当前选择位置
                if (mOldPosition != mNewPosition && mNewPosition >= 0 && mNewPosition < mLetters.size()) {
                    mChoosePosition = mNewPosition;
                    if (null != mListener) {
                        mListener.onLetterChange(mLetters.get(mNewPosition));
                    }
                }
                // 调用postInvalidate()方法,请求重新draw()
                postInvalidate();
            }
        });
        mValueAnimator.start();
    }

    /**
     * 对外提供一个获取渲染字母表的方法
     *
     * @return 取渲染字母表
     */
    public List<String> getLetters() {
        return mLetters;
    }

    /**
     * 对外提供一个设置渲染字母表的方法
     *
     * @param letters 要渲染的字母表
     */
    public void setLetters(List<String> letters) {
        this.mLetters = letters;
        // 调用postInvalidate()方法,请求重新draw()
        postInvalidate();
    }

    /**
     * 当新的(*,A,B,C... )字母被选中的时候触发的侦听器的接口对象
     */
    private OnTouchLetterChangeListener mListener;

    /**
     * 当新的(*,A,B,C... )字母被选中的时候触发的侦听器
     */
    public interface OnTouchLetterChangeListener {
        /**
         * 在当新的(*,A,B,C... )字母被选中的时候,获取被选中的字母
         *
         * @param letter 被选中的字母
         */
        void onLetterChange(String letter);
    }

    /**
     * @param listener 当新的(*,A,B,C... )字母被选中的时候触发的侦听器
     */
    public void setOnTouchLetterChangeListener(OnTouchLetterChangeListener listener) {
        this.mListener = listener;
    }
}
