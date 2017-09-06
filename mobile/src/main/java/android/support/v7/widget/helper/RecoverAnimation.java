package android.support.v7.widget.helper;

import android.support.v4.animation.AnimatorCompatHelper;
import android.support.v4.animation.AnimatorListenerCompat;
import android.support.v4.animation.AnimatorUpdateListenerCompat;
import android.support.v4.animation.ValueAnimatorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;

@SuppressWarnings("RestrictedApi")
class RecoverAnimation implements AnimatorListenerCompat {

    final float mStartDx;

    final float mStartDy;

    final float mTargetX;

    final float mTargetY;

    final RecyclerView.ViewHolder mViewHolder;

    final int mActionState;

    private final ValueAnimatorCompat mValueAnimator;

    final int mAnimationType;

    public boolean mIsPendingCleanup;

    float mX;

    float mY;

    // if user starts touching a recovering view, we put it into interaction mode again,
    // instantly.
    boolean mOverridden = false;

    boolean mEnded = false;

    private float mFraction;

    public RecoverAnimation(RecyclerView.ViewHolder viewHolder, int animationType,
                            int actionState, float startDx, float startDy, float targetX, float targetY) {
        mActionState = actionState;
        mAnimationType = animationType;
        mViewHolder = viewHolder;
        mStartDx = startDx;
        mStartDy = startDy;
        mTargetX = targetX;
        mTargetY = targetY;
        mValueAnimator = AnimatorCompatHelper.emptyValueAnimator();
        mValueAnimator.addUpdateListener(
                new AnimatorUpdateListenerCompat() {
                    @Override
                    public void onAnimationUpdate(ValueAnimatorCompat animation) {
                        setFraction(animation.getAnimatedFraction());
                    }
                });
        mValueAnimator.setTarget(viewHolder.itemView);
        mValueAnimator.addListener(this);
        setFraction(0f);
    }

    public void setDuration(long duration) {
        mValueAnimator.setDuration(duration);
    }

    public void start() {
        mViewHolder.setIsRecyclable(false);
        mValueAnimator.start();
    }

    public void cancel() {
        mValueAnimator.cancel();
    }

    public void setFraction(float fraction) {
        mFraction = fraction;
    }

    /**
     * We run updates on onDraw method but use the fraction from animator callback.
     * This way, we can sync translate x/y values w/ the animators to avoid one-off frames.
     */
    public void update() {
        if (mStartDx == mTargetX) {
            mX = ViewCompat.getTranslationX(mViewHolder.itemView);
        } else {
            mX = mStartDx + mFraction * (mTargetX - mStartDx);
        }
        if (mStartDy == mTargetY) {
            mY = ViewCompat.getTranslationY(mViewHolder.itemView);
        } else {
            mY = mStartDy + mFraction * (mTargetY - mStartDy);
        }
    }

    @Override
    public void onAnimationStart(ValueAnimatorCompat animation) {

    }

    @Override
    public void onAnimationEnd(ValueAnimatorCompat animation) {
        if (!mEnded) {
            mViewHolder.setIsRecyclable(true);
        }
        mEnded = true;
    }

    @Override
    public void onAnimationCancel(ValueAnimatorCompat animation) {
        setFraction(1f); //make sure we recover the view's state.
    }

    @Override
    public void onAnimationRepeat(ValueAnimatorCompat animation) {

    }
}
