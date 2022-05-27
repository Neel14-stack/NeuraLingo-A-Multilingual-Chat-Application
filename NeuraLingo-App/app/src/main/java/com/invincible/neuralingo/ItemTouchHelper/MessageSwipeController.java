package com.invincible.neuralingo.ItemTouchHelper;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.invincible.neuralingo.ItemTouchHelper.AndroidUtils;
import com.invincible.neuralingo.R;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
        mv = {1, 1, 18},
        bv = {1, 0, 3},
        k = 1,
        d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0018\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u001a\u001a\u00020\u0018H\u0016J\u0010\u0010\u001b\u001a\u00020\u00182\u0006\u0010\u001c\u001a\u00020\u0018H\u0002J\u0010\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 H\u0002J\u0018\u0010!\u001a\u00020\u00182\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\bH\u0016J@\u0010%\u001a\u00020\u001e2\u0006\u0010&\u001a\u00020 2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010'\u001a\u00020\n2\u0006\u0010(\u001a\u00020\u00182\u0006\u0010)\u001a\u00020\u000eH\u0016J \u0010*\u001a\u00020\u000e2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\b2\u0006\u0010+\u001a\u00020\bH\u0016J\u0018\u0010,\u001a\u00020\u001e2\u0006\u0010$\u001a\u00020\b2\u0006\u0010-\u001a\u00020\u0018H\u0016J\u0018\u0010.\u001a\u00020\u001e2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\bH\u0003R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\fX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006/"},
        d2 = {"Lcom/shain/messenger/MessageSwipeController;", "Landroidx/recyclerview/widget/ItemTouchHelper$Callback;", "context", "Landroid/content/Context;", "swipeControllerActions", "Lcom/shain/messenger/SwipeControllerActions;", "(Landroid/content/Context;Lcom/shain/messenger/SwipeControllerActions;)V", "currentItemViewHolder", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "dX", "", "imageDrawable", "Landroid/graphics/drawable/Drawable;", "isVibrate", "", "lastReplyButtonAnimationTime", "", "mView", "Landroid/view/View;", "replyButtonProgress", "shareRound", "startTracking", "swipeBack", "convertToAbsoluteDirection", "", "flags", "layoutDirection", "convertTodp", "pixel", "drawReplyButton", "", "canvas", "Landroid/graphics/Canvas;", "getMovementFlags", "recyclerView", "Landroidx/recyclerview/widget/RecyclerView;", "viewHolder", "onChildDraw", "c", "dY", "actionState", "isCurrentlyActive", "onMove", "target", "onSwiped", "direction", "setTouchListener", "app_debug"}
)
public final class MessageSwipeController extends Callback {
    private Drawable imageDrawable;
    private Drawable shareRound;
    private ViewHolder currentItemViewHolder;
    private View mView;
    private float dX;
    private float replyButtonProgress;
    private long lastReplyButtonAnimationTime;
    private boolean swipeBack;
    private boolean isVibrate;
    private boolean startTracking;
    private final Context context;
    private final SwipeControllerActions swipeControllerActions;

    public int getMovementFlags(@NotNull RecyclerView recyclerView, @NotNull ViewHolder viewHolder) {
        this.mView = viewHolder.itemView;;
        Drawable var3 = this.context.getDrawable(R.drawable.ic_reply_black_24dp);
        if (var3 == null) {
            Intrinsics.throwNpe();
        }

        this.imageDrawable = var3;
        var3 = this.context.getDrawable(R.drawable.ic_round_shape);
        if (var3 == null) {
            Intrinsics.throwNpe();
        }

        this.shareRound = var3;
        return Callback.makeMovementFlags(0, 8);
    }

    public boolean onMove(@NotNull RecyclerView recyclerView, @NotNull ViewHolder viewHolder, @NotNull ViewHolder target) {
        return false;
    }

    
    public void onSwiped(@NotNull ViewHolder viewHolder, int direction) {
    }

    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (this.swipeBack) {
            this.swipeBack = false;
            return 0;
        } else {
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }
    }

    public void onChildDraw(@NotNull Canvas c, @NotNull RecyclerView recyclerView, @NotNull ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (actionState == 1) {
            this.setTouchListener(recyclerView, viewHolder);
        }

        View var10000 = this.mView;

        if (var10000.getTranslationX() < (float)this.convertTodp(130) || dX < this.dX) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            this.dX = dX;
            this.startTracking = true;
        }

        this.currentItemViewHolder = viewHolder;
        this.drawReplyButton(c);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    private final void setTouchListener(RecyclerView recyclerView, final ViewHolder viewHolder) {
        recyclerView.setOnTouchListener((OnTouchListener)(new OnTouchListener() {
            public final boolean onTouch(View $noName_0, MotionEvent event) {
                MessageSwipeController var10000 = MessageSwipeController.this;
                var10000.swipeBack = event.getAction() == 3 || event.getAction() == 1;
                if (MessageSwipeController.this.swipeBack && Math.abs(MessageSwipeController.access$getMView$p(MessageSwipeController.this).getTranslationX()) >= (float)MessageSwipeController.this.convertTodp(100)) {
                    MessageSwipeController.this.swipeControllerActions.showReplyUI(viewHolder.getAdapterPosition());
                }
                return false;
            }
        }));
    }

    private final void drawReplyButton(Canvas canvas) {
        if (this.currentItemViewHolder != null) {
            View var10000 = this.mView;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mView");
            }

            float translationX = var10000.getTranslationX();
            long newTime = System.currentTimeMillis();
            long dt = Math.min(17L, newTime - this.lastReplyButtonAnimationTime);
            this.lastReplyButtonAnimationTime = newTime;
            boolean showing = translationX >= (float)this.convertTodp(30);
            if (showing) {
                if (this.replyButtonProgress < 1.0F) {
                    this.replyButtonProgress += (float)dt / 180.0F;
                    if (this.replyButtonProgress > 1.0F) {
                        this.replyButtonProgress = 1.0F;
                    } else {
                        var10000 = this.mView;
                        if (var10000 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("mView");
                        }

                        var10000.invalidate();
                    }
                }
            } else if (translationX <= 0.0F) {
                this.replyButtonProgress = 0.0F;
                this.startTracking = false;
                this.isVibrate = false;
            } else if (this.replyButtonProgress > 0.0F) {
                this.replyButtonProgress -= (float)dt / 180.0F;
                if (this.replyButtonProgress < 0.1F) {
                    this.replyButtonProgress = 0.0F;
                } else {
                    var10000 = this.mView;
                    if (var10000 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("mView");
                    }

                    var10000.invalidate();
                }
            }

            int alpha = 0;
            float scale = 0.0F;
            if (showing) {
                scale = this.replyButtonProgress <= 0.8F ? 1.2F * (this.replyButtonProgress / 0.8F) : 1.2F - 0.2F * ((this.replyButtonProgress - 0.8F) / 0.2F);
                alpha = (int)Math.min(255.0F, (float)255 * (this.replyButtonProgress / 0.8F));
            } else {
                scale = this.replyButtonProgress;
                alpha = (int)Math.min(255.0F, (float)255 * this.replyButtonProgress);
            }

            Drawable var13 = this.shareRound;
            if (var13 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("shareRound");
            }

            var13.setAlpha(alpha);
            var13 = this.imageDrawable;
            if (var13 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("imageDrawable");
            }

            var13.setAlpha(alpha);
            if (this.startTracking && !this.isVibrate) {
                var10000 = this.mView;
                if (var10000 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mView");
                }

                if (var10000.getTranslationX() >= (float)this.convertTodp(100)) {
                    var10000 = this.mView;
                    if (var10000 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("mView");
                    }

                    var10000.performHapticFeedback(3, 2);
                    this.isVibrate = true;
                }
            }

            var10000 = this.mView;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mView");
            }

            int var14;
            if (var10000.getTranslationX() > (float)this.convertTodp(130)) {
                var14 = this.convertTodp(130) / 2;
            } else {
                var10000 = this.mView;
                if (var10000 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mView");
                }

                var14 = (int)(var10000.getTranslationX() / (float)2);
            }

            int x = var14;
            var10000 = this.mView;
            if (var10000 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mView");
            }

            var14 = var10000.getTop();
            View var10001 = this.mView;
            if (var10001 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mView");
            }

            float y = (float)(var14 + var10001.getMeasuredHeight() / 2);
            var13 = this.shareRound;
            if (var13 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("shareRound");
            }

            var13.setColorFilter((ColorFilter)(new PorterDuffColorFilter(ContextCompat.getColor(this.context, R.color.gray_light), Mode.MULTIPLY)));
            var13 = this.shareRound;
            if (var13 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("shareRound");
            }

            var13.setBounds((int)((float)x - (float)this.convertTodp(18) * scale), (int)(y - (float)this.convertTodp(18) * scale), (int)((float)x + (float)this.convertTodp(18) * scale), (int)(y + (float)this.convertTodp(18) * scale));
            var13 = this.shareRound;
            if (var13 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("shareRound");
            }

            var13.draw(canvas);
            var13 = this.imageDrawable;
            if (var13 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("imageDrawable");
            }

            var13.setBounds((int)((float)x - (float)this.convertTodp(12) * scale), (int)(y - (float)this.convertTodp(11) * scale), (int)((float)x + (float)this.convertTodp(12) * scale), (int)(y + (float)this.convertTodp(10) * scale));
            var13 = this.imageDrawable;
            if (var13 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("imageDrawable");
            }

            var13.draw(canvas);
            var13 = this.shareRound;
            if (var13 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("shareRound");
            }

            var13.setAlpha(255);
            var13 = this.imageDrawable;
            if (var13 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("imageDrawable");
            }

            var13.setAlpha(255);
        }
    }

    private final int convertTodp(int pixel) {
        return AndroidUtils.INSTANCE.dp((float)pixel, this.context);
    }

    public MessageSwipeController(@NotNull Context context, @NotNull SwipeControllerActions swipeControllerActions) {
        this.context = context;
        this.swipeControllerActions = swipeControllerActions;
    }

    // $FF: synthetic method
    public static final View access$getMView$p(MessageSwipeController $this) {
        View var10000 = $this.mView;
        if (var10000 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mView");
        }

        return var10000;
    }
}
