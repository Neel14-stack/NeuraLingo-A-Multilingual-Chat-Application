package com.invincible.neuralingo.ItemTouchHelper;

import android.content.Context;
import android.content.res.Resources;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
        mv = {1, 1, 18},
        bv = {1, 0, 3},
        k = 1,
        d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0002J\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\f"},
        d2 = {"Lcom/shain/messenger/utils/AndroidUtils;", "", "()V", "density", "", "checkDisplaySize", "", "context", "Landroid/content/Context;", "dp", "", "value", "app_debug"}
)
public final class AndroidUtils {
    private static float density;
    @NotNull
    public static final AndroidUtils INSTANCE;

    public final int dp(float value, @NotNull Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        if (density == 1.0F) {
            this.checkDisplaySize(context);
        }

        return value == 0.0F ? 0 : (int)Math.ceil((double)(density * value));
    }

    private final void checkDisplaySize(Context context) {
        try {
            Resources var10000 = context.getResources();
            Intrinsics.checkExpressionValueIsNotNull(var10000, "context.resources");
            density = var10000.getDisplayMetrics().density;
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    private AndroidUtils() {
    }

    static {
        AndroidUtils var0 = new AndroidUtils();
        INSTANCE = var0;
        density = 1.0F;
    }
}
