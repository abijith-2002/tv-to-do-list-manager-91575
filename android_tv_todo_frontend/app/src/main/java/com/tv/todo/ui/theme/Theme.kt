package com.tv.todo.ui.theme

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View
import androidx.core.content.ContextCompat
import com.tv.todo.R

/**
 * Helpers for applying Ocean Professional styling to Views.
 */
object ThemeUtils {
    fun makeCardBackground(context: Context, corner: Float = 20f): StateListDrawable {
        val normal = GradientDrawable().apply {
            setColor(OceanColors.Surface)
            cornerRadius = corner
        }
        val focused = GradientDrawable().apply {
            setColor(OceanColors.Surface)
            setStroke(4, OceanColors.Primary)
            cornerRadius = corner
        }
        return StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_focused), focused)
            addState(intArrayOf(android.R.attr.state_selected), focused)
            addState(intArrayOf(), normal)
        }
    }

    fun applyFocusOutline(view: View) {
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        view.background = makeCardBackground(view.context)
        view.setOnFocusChangeListener { v, hasFocus ->
            v.elevation = if (hasFocus) 12f else 4f
            v.scaleX = if (hasFocus) 1.03f else 1.0f
            v.scaleY = if (hasFocus) 1.03f else 1.0f
        }
    }

    fun colorTextPrimary(context: Context): Int =
        ContextCompat.getColor(context, R.color.textPrimary)
}
