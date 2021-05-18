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
package com.stream.reactions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stream.reactions.databinding.BottomSheetReactionsBinding
import com.stream.reactions.utils.toast
import io.getstream.chat.android.client.channel.ChannelClient
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.Reaction

class ReactionsBottomSheet(private val channelClient: ChannelClient, private val sentMessage: Message) : BottomSheetDialogFragment() {
    private var score = 0
    private var _binding: BottomSheetReactionsBinding? = null
    private val binding get() = _binding!!
    private var reactionType = ""
    private val reactionViewModel: ReactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_reactions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = BottomSheetReactionsBinding.bind(view)
        binding.layoutReactions.addOnButtonCheckedListener { _, checkedId, _ ->
            reactionType = if (checkedId == R.id.btnClapReaction) {
                "clap"
            } else {
                "like"
            }
        }
        binding.btnIncreaseScore.setOnClickListener {
            increaseScore()
        }
        binding.btnReduceScore.setOnClickListener {
            reduceScore()
        }
        binding.btnSaveReaction.setOnClickListener {
            sendReaction()
        }
    }

    private fun sendReaction() {
        val reaction = Reaction(
            messageId = sentMessage.id,
            type = reactionType,
            score = score,
            extraData = mutableMapOf("customField" to 1),
        )
        channelClient.sendReaction(reaction).enqueue { result ->
            if (result.isSuccess) {
                val sentReaction = result.data()
                reactionViewModel.setMessageId(reaction.messageId)
                Log.d("Message", "Message Reaction score is: ${sentReaction.score}")
                dismiss()
            } else {
                requireContext().toast("Adding reaction Failed")
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun increaseScore() {
        score ++
        binding.tvScoreCount.text = score.toString()
    }

    private fun reduceScore() {
        if (score > 0) {
            score--
        }
        binding.tvScoreCount.text = score.toString()
    }
}