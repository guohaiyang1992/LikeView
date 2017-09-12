package com.simon.likeview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.simon.likeview.R;
import com.simon.likeview.anim.ShineAnimator;
import com.simon.likeview.utils.ViewUtils;

import java.util.Random;

/**
 * description: shine view
 * author: Simon
 * created at 2017/9/12 下午1:29
 * <p>
 * 此view 不占用原有布局的位置，临时添加、删除
 */
public class ShineView extends View {
    //--TAG--
    private static final String TAG = "ShineView";
    //--上下文-- 弱
    private Activity activity;
    //--attachView--
    private View attachView;
    //--动画delay--
    private static long FRAME_REFRESH_DELAY = 25;//default 10ms ,change to 25ms for saving cpu.

    private ShineAnimator shineAnimator;


    private Paint paint;
    private Paint paintSmall;

    private int colorCount = 10;
    private static int colorRandom[] = new int[10];

    //--核心属性--
    private int shineCount; //个数
    private float smallOffsetAngle;//间隔角度
    private float turnAngle; //旋转角度
    private long animDuration; //动画时间
    private float shineDistanceMultiple; //距离
    private int smallShineColor; //小的圆环颜色
    private int bigShineColor; //大的圆环颜色
    private int shineSize;
    private boolean allowRandomColor = false; //是否随机色
    private boolean enableFlashing = false; //是否闪光

    //--绘制范围--
    private RectF rectF = new RectF();
    private RectF rectFSmall = new RectF();

    private Random random = new Random();
    private int centerAnimX;
    private int centerAnimY;
    private int attachViewWidth;
    private int attachViewHeight;

    private float value;
    private float distanceOffset = 0.2f;

    private boolean isAutoAnim = false;

    static {
        colorRandom[0] = Color.parseColor("#FFFF99");
        colorRandom[1] = Color.parseColor("#FFCCCC");
        colorRandom[2] = Color.parseColor("#996699");
        colorRandom[3] = Color.parseColor("#FF6666");
        colorRandom[4] = Color.parseColor("#FFFF66");
        colorRandom[5] = Color.parseColor("#F44336");
        colorRandom[6] = Color.parseColor("#666666");
        colorRandom[7] = Color.parseColor("#CCCC00");
        colorRandom[8] = Color.parseColor("#666666");
        colorRandom[9] = Color.parseColor("#999933");
    }

    public ShineView(Context context) {
        this(context, null);
    }

    public ShineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniDefaultConfig();
        //--初始化通过xml配置的属性--
        initConfig(context, attrs);
        //--初始化画布--
        initDrawConfig();
        //--初始化动画--
        initAnim();
    }

    private void initAnim() {
        this.shineAnimator = new ShineAnimator(animDuration, shineDistanceMultiple, 200);
        ValueAnimator.setFrameDelay(FRAME_REFRESH_DELAY);
        shineAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ViewUtils.removeView(activity, ShineView.this);
            }
        });
    }

    private void initDrawConfig() {
        //--大圆形--
        paint = new Paint();
        paint.setColor(bigShineColor);
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        //--小圆形--
        paintSmall = new Paint();
        paintSmall.setColor(smallShineColor);
        paintSmall.setStrokeWidth(10);
        paintSmall.setStyle(Paint.Style.STROKE);
        paintSmall.setStrokeCap(Paint.Cap.ROUND);
    }

    //--初始化默认值--
    private void iniDefaultConfig() {
        allowRandomColor = false;
        animDuration = 1500;
        bigShineColor = colorRandom[0];
        enableFlashing = false;
        shineCount = 7;
        turnAngle = 20;
        shineDistanceMultiple = 1.5f;
        smallOffsetAngle = 20;
        smallShineColor = colorRandom[1];
        shineSize = 0;
        isAutoAnim = false;
    }

    //--初始化配置--
    private void initConfig(Context context, AttributeSet attrs) {
        //--初始化上下文--
        activity = (Activity) context;
        //--获取配置参数 | 获得我们所定义的自定义样式属性--
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ShineView, 0, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.ShineView_shine_count) {
                setShineCount(a.getInteger(attr, 7));
            } else if (attr == R.styleable.ShineView_shine_turn_angle) {
                setTurnAngle(a.getFloat(attr, 20));
            } else if (attr == R.styleable.ShineView_small_shine_offset_angle) {
                setSmallOffsetAngle(a.getFloat(attr, 20));
            } else if (attr == R.styleable.ShineView_enable_flashing) {
                setEnableFlashing(a.getBoolean(attr, false));
            } else if (attr == R.styleable.ShineView_small_shine_color) {
                setSmallShineColor(a.getColor(attr, 0));
            } else if (attr == R.styleable.ShineView_big_shine_color) {
                setBigShineColor(a.getColor(attr, 0));
            } else if (attr == R.styleable.ShineView_allow_random_color) {
                setAllowRandomColor(a.getBoolean(attr, false));
            } else if (attr == R.styleable.ShineView_shine_animation_duration) {
                setAnimDuration(a.getInt(attr, 1500));
            } else if (attr == R.styleable.ShineView_shine_distance_multiple) {
                setShineDistanceMultiple(a.getFloat(attr, 1.5f));
            } else if (attr == R.styleable.ShineView_shine_size) {
                setShineSize(a.getInt(attr, 0));
            } else if (attr == R.styleable.ShineView_auto_anim) {
                setAutoAnim(a.getBoolean(attr, false));
            }

        }
        a.recycle();
    }

    //-------------------对外的方法，目前开放------------------------

    /**
     * 是否自动执行动画
     *
     * @param autoAnim true:自动执行  false：不自动执行
     */
    public void setAutoAnim(boolean autoAnim) {
        isAutoAnim = autoAnim;
    }

    public void setShineSize(int shineSize) {
        this.shineSize = shineSize;
    }

    /**
     * 设置距离变化倍率
     *
     * @param shineDistanceMultiple 倍率 float
     */
    public void setShineDistanceMultiple(float shineDistanceMultiple) {
        if (shineDistanceMultiple <= 1f) {
            shineDistanceMultiple = 1.5f;
        }
        this.shineDistanceMultiple = shineDistanceMultiple;
        //--set and check anim--
        if (shineAnimator == null) {
            return;
        }
        if (shineAnimator.isRunning()) {
            shineAnimator.end();
        }
        shineAnimator.setFloatValues(1f, shineDistanceMultiple);

    }

    /**
     * 设置动画时间
     *
     * @param animDuration 单位ms
     */
    public void setAnimDuration(long animDuration) {
        //--check anim--
        if (animDuration <= 0) {
            animDuration = 1500;
        }
        this.animDuration = animDuration;
        //--set and check anim--
        if (shineAnimator == null) {
            return;
        }
        if (shineAnimator.isRunning()) {
            shineAnimator.end();
        }
        shineAnimator.setDuration(animDuration);
    }

    /**
     * 设置是否闪光
     *
     * @param enableFlashing true:闪光 false 不闪光
     */
    public void setEnableFlashing(boolean enableFlashing) {
        this.enableFlashing = enableFlashing;
    }

    /**
     * 小圆环间隔角度
     *
     * @param smallOffsetAngle 间隔角度
     */
    public void setSmallOffsetAngle(float smallOffsetAngle) {
        this.smallOffsetAngle = smallOffsetAngle;
    }

    /**
     * 是否随机切换颜色
     *
     * @param allowRandomColor true：随机切换 false:不随机切换
     */
    public void setAllowRandomColor(boolean allowRandomColor) {
        this.allowRandomColor = allowRandomColor;
    }

    /**
     * 设置大圆环颜色值
     *
     * @param bigShineColor 颜色值
     */
    public void setBigShineColor(int bigShineColor) {
        if (bigShineColor == 0) {
            bigShineColor = colorRandom[0];
        }
        this.bigShineColor = bigShineColor;
        //--set paint--
        if (paint == null) {
            return;
        }
        paint.setColor(bigShineColor);
    }

    /**
     * 设置小圆环颜色
     *
     * @param smallShineColor 颜色值
     */
    public void setSmallShineColor(int smallShineColor) {
        if (smallShineColor == 0) {
            smallShineColor = colorRandom[1];
        }
        this.smallShineColor = smallShineColor;
        //--set paint--
        if (paintSmall == null) {
            return;
        }
        paintSmall.setColor(smallShineColor);
    }

    /**
     * 设置圆环个数
     *
     * @param shineCount 个数
     */
    public void setShineCount(int shineCount) {
        //--check value--
        if (shineCount <= 0) {
            shineCount = 7;
        }
        this.shineCount = shineCount;
    }

    /**
     * 设置旋转角
     *
     * @param turnAngle 旋转角度
     */
    public void setTurnAngle(float turnAngle) {
        //--check value--
        if (turnAngle <= 0) {
            turnAngle = 20;
        }
        this.turnAngle = turnAngle;
    }


    /**
     * 必须执行此方法，绑定宿主view
     *
     * @param attachView 宿主view（用于确定位置）
     */
    public void attachView(View attachView) {
        if (attachView != null) {
            this.attachView = attachView;
        }
        if (isAutoAnim) {
            startAnim();
        }
    }

    /**
     * 开启动画
     */
    public void startAnim() {
        //--check attachView--
        if (attachView == null) {
            throw new NullPointerException("You must init attachView!=>attachView(View attachView)");
        }
        //--check anim--
        if (shineAnimator != null && shineAnimator.isRunning()) {
            shineAnimator.end();
        }
        //--init anim config--
        if (attachView.getWidth() == 0 || attachView.getHeight() == 0) {
            int[] viewInfo = ViewUtils.getViewInfo(attachView);
            attachViewWidth = viewInfo[0];
            attachViewHeight = viewInfo[1];
            Log.v("test", "" + attachViewWidth);
        } else {
            attachViewWidth = attachView.getWidth();
            attachViewHeight = attachView.getHeight();
        }
        int[] location = ViewUtils.getViewLocation(attachView);
        //--frame--
        Rect visibleFrame = ViewUtils.getViewFrame(activity);
        // --If navigation bar is not displayed on left, visibleFrame.left is 0.--
        centerAnimX = location[0] + attachViewWidth / 2 - visibleFrame.left;
        if (isTranslucentNavigation(activity)) {
            if (isFullScreen(activity)) {
                centerAnimY = visibleFrame.height() - ViewUtils.getBottomHeight(attachView, activity, false) + attachViewHeight / 2;
            } else {
                centerAnimY = visibleFrame.height() - ViewUtils.getBottomHeight(attachView, activity, true) + attachViewHeight / 2;
            }
        } else {
            centerAnimY = getMeasuredHeight() - ViewUtils.getBottomHeight(attachView, activity, false) + attachViewHeight / 2;
        }
        //--动画--
        shineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                value = (float) valueAnimator.getAnimatedValue();
                if (shineSize != 0 && shineSize > 0) {
                    paint.setStrokeWidth((shineSize) * (shineDistanceMultiple - value));
                    paintSmall.setStrokeWidth(((float) shineSize / 3 * 2) * (shineDistanceMultiple - value));
                } else {
                    paint.setStrokeWidth((attachViewWidth / 2) * (shineDistanceMultiple - value));
                    paintSmall.setStrokeWidth((attachViewWidth / 3) * (shineDistanceMultiple - value));
                }
                rectF.set(centerAnimX - (attachViewWidth / (3 - shineDistanceMultiple) * value), centerAnimY - (attachViewHeight / (3 - shineDistanceMultiple) * value), centerAnimX + (attachViewWidth / (3 - shineDistanceMultiple) * value), centerAnimY + (attachViewHeight / (3 - shineDistanceMultiple) * value));
                rectFSmall.set(centerAnimX - (attachViewWidth / ((3 - shineDistanceMultiple) + distanceOffset) * value), centerAnimY - (attachViewHeight / ((3 - shineDistanceMultiple) + distanceOffset) * value), centerAnimX + (attachViewWidth / ((3 - shineDistanceMultiple) + distanceOffset) * value), centerAnimY + (attachViewHeight / ((3 - shineDistanceMultiple) + distanceOffset) * value));
                invalidate();
            }
        });
        //--attach view--
        ViewUtils.attachView(activity, this);
        //--start anim--
        shineAnimator.startAnim(this, centerAnimX, centerAnimY);
    }

    //------------------私有方法-------------------------

    //--只是为解决自动执行出错的问题--
    boolean isRun = false;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < shineCount; i++) {
            if (allowRandomColor) {
                paint.setColor(colorRandom[Math.abs(colorCount / 2 - i) >= colorCount ? colorCount - 1 : Math.abs(colorCount / 2 - i)]);
            }
            canvas.drawArc(rectF, 360f / shineCount * i + 1 + ((value - 1) * turnAngle), 0.1f, false, getConfigPaint(paint));
            canvas.drawArc(rectFSmall, 360f / shineCount * i + 1 - smallOffsetAngle + ((value - 1) * turnAngle), 0.1f, false, getConfigPaint(paintSmall));
        }

        //--此处是防止自动执行动画时无法绘制的问题（执行onDraw但是无效）--
        if (shineAnimator != null && !isRun) {
            isRun = true;
            startAnim();
        }
    }

    /**
     * 为paint 配置颜色
     *
     * @param paint 目标paint
     */
    private Paint getConfigPaint(Paint paint) {
        if (enableFlashing) {
            paint.setColor(colorRandom[random.nextInt(colorCount - 1)]);
        }
        return paint;
    }

    /**
     * @param activity
     * @return 判断当前手机是否是全屏
     */
    public static boolean isFullScreen(Activity activity) {
        int flag = activity.getWindow().getAttributes().flags;
        if ((flag & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param activity
     * @return 判断当前手机是否透明虚拟按键
     */
    public static boolean isTranslucentNavigation(Activity activity) {
        int flag = activity.getWindow().getAttributes().flags;
        if ((flag & WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                == WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION) {
            return true;
        } else {
            return false;
        }
    }
}

