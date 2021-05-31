package com.open.monitor.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;

/**
 * 创建Spannable的快捷辅助类
 * 代码实例:
 * <pre><code>
 * new SpannableHelper.Builder()
 * .text("AAA").color(Color.WHITE).size(mContext, R.dimen.dp_100).bold(true)
 * .text("BBB").color("#FF0000").size(120).bold(false)
 * .build()
 * </code></pre>
 * 每个文字样式需要以text()方法开头，之后再使用属性方法:color()、size()、bold(),属性方法不分顺序
 */
public class SpannableHelper {

    private SpannableStringBuilder mSpannableStringBuilder;

    private SpannableHelper(Builder builder) {
        mSpannableStringBuilder = builder.mSpannableStringBuilder;
    }

    private SpannableStringBuilder getSpannableStringBuilder() {
        return mSpannableStringBuilder;
    }

    public static final class Builder {
        private int                    index;
        private int                    textLength;
        private SpannableStringBuilder mSpannableStringBuilder;

        public Builder() {
            mSpannableStringBuilder = new SpannableStringBuilder();
        }

        public Builder text(CharSequence text) {
            if (TextUtils.isEmpty(text)) {
                throw new NullPointerException("SpannableHelper.Builder#text(CharSequence text) params can not be empty!");
            }
            index = mSpannableStringBuilder.length();
            textLength = text.length();
            mSpannableStringBuilder.append(text);
            return this;
        }

        public Builder color(int color) {
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
            mSpannableStringBuilder.setSpan(colorSpan, index, index + textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public Builder bgColor(int color) {
            BackgroundColorSpan colorSpan = new BackgroundColorSpan(color);
            mSpannableStringBuilder.setSpan(colorSpan, index, index + textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public Builder color(String color) {
            return color(Color.parseColor(color));
        }

        public Builder size(int size) {
            AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(size);
            mSpannableStringBuilder.setSpan(absoluteSizeSpan, index, index + textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public Builder size(Context context, @DimenRes int dimenRes) {
            return size((int) context.getResources().getDimension(dimenRes));
        }

        public Builder bold(boolean bold) {
            if (bold) {
                StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);//粗体
                mSpannableStringBuilder.setSpan(styleSpan, index, index + textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return this;
        }

        /**
         * 下划线
         */
        public Builder underline() {
            UnderlineSpan span = new UnderlineSpan();
            mSpannableStringBuilder.setSpan(span, index, index + textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }


        /**
         * 点击
         *
         * @apiNote 使用这个属性需要设置 {@link android.widget.TextView#setMovementMethod(android.text.method.LinkMovementMethod)}
         * @apiNote 同时设置颜色 {@link #color(int)} 需要放在此方法之后执行
         */
        public Builder click(final View.OnClickListener onClickListener) {
            ClickableSpan span = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    if (onClickListener != null) {
                        onClickListener.onClick(widget);
                    }
                }
            };
            mSpannableStringBuilder.setSpan(span, index, index + textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        /**
         * 点击（直接设置ClickableSpan）
         *
         * @apiNote 使用这个属性需要设置 {@link android.widget.TextView#setMovementMethod(android.text.method.LinkMovementMethod)}
         * @apiNote 同时设置颜色 {@link #color(int)} 需要放在此方法之后执行
         */
        public Builder click(ClickableSpan span) {
            mSpannableStringBuilder.setSpan(span, index, index + textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public Spannable build() {
            return new SpannableHelper(this).getSpannableStringBuilder();
        }
    }

}
