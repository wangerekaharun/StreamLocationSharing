/*
 * Copyright 2021 GradleBuildPlugins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stream.reactions.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.stream.reactions.R
import io.getstream.chat.android.ui.SupportedReactions

fun likeDrawable(context: Context): SupportedReactions.ReactionDrawable {
    val drawableInactive = ContextCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24)!!
    val drawableActive = ContextCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24)!!.apply {
        setTint(ContextCompat.getColor(context, android.R.color.holo_red_dark))
    }
    return SupportedReactions.ReactionDrawable(drawableInactive, drawableActive)
}

fun clapDrawable(context: Context): SupportedReactions.ReactionDrawable {
    val drawableInactive = ContextCompat.getDrawable(context, R.drawable.ic_clapping)!!
    val drawableActive = ContextCompat.getDrawable(context, R.drawable.ic_clapping)!!.apply {
        setTint(ContextCompat.getColor(context, android.R.color.holo_red_dark))
    }
    return SupportedReactions.ReactionDrawable(drawableInactive, drawableActive)
}

fun wonderingDrawable(context: Context): SupportedReactions.ReactionDrawable {
    val drawableInactive = ContextCompat.getDrawable(context, R.drawable.ic_wondering)!!
    val drawableActive = ContextCompat.getDrawable(context, R.drawable.ic_wondering)!!.apply {
        setTint(ContextCompat.getColor(context, android.R.color.holo_red_dark))
    }
    return SupportedReactions.ReactionDrawable(drawableInactive, drawableActive)
}

fun brilliantDrawable(context: Context): SupportedReactions.ReactionDrawable {
    val drawableInactive = ContextCompat.getDrawable(context, R.drawable.ic_brilliant)!!
    val drawableActive = ContextCompat.getDrawable(context, R.drawable.ic_brilliant)!!.apply {
        setTint(ContextCompat.getColor(context, android.R.color.holo_red_dark))
    }
    return SupportedReactions.ReactionDrawable(drawableInactive, drawableActive)
}

fun handShakeDrawable(context: Context): SupportedReactions.ReactionDrawable {
    val drawableInactive = ContextCompat.getDrawable(context, R.drawable.ic_hand_shake)!!
    val drawableActive = ContextCompat.getDrawable(context, R.drawable.ic_hand_shake)!!.apply {
        setTint(ContextCompat.getColor(context, android.R.color.holo_red_dark))
    }
    return SupportedReactions.ReactionDrawable(drawableInactive, drawableActive)
}