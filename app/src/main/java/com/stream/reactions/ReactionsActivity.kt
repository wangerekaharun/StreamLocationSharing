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

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.stream.reactions.databinding.ActivityReactionsBinding
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.client.api.models.QuerySort
import io.getstream.chat.android.client.channel.ChannelClient
import io.getstream.chat.android.client.models.*
import io.getstream.chat.android.livedata.ChatDomain

class ReactionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReactionsBinding
    private var sentMessage = Message()
    private lateinit var channelClient: ChannelClient
    private val reactionsAdapter = ReactionsAdapter { reaction ->
        deleteReaction(reaction)
    }
    private val reactionViewModel: ReactionViewModel by viewModels()
    private var channelId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reactions)

        binding = ActivityReactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val client = ChatClient.Builder("b67pax5b2wdq", applicationContext).build()
        ChatDomain.Builder(client, applicationContext).build()

        binding.imgReactOnMessage.setOnClickListener {
            showReactions()
        }

        val user = User(
            id = "tutorial-droid",
            extraData = mutableMapOf(
                "name" to "Stream Reactions",
                "image" to "https://bit.ly/2TIt8NR",
            ),
        )
        client.connectUser(
            user = user,
            token = BuildConfig.TOKEN
        ).enqueue()

        channelClient = client.channel(channelType = "messaging", channelId = "general")

        channelClient.create().enqueue { result ->
            if (result.isSuccess) {
                val newChannel: Channel = result.data()
                channelId = newChannel.cid
                Log.d("Channel", newChannel.name)
            } else {
                showSnackBar("Adding channels Failed")
            }
        }

        val request = QueryChannelsRequest(
            filter = Filters.and(
                Filters.eq("members", listOf("tutorial-droid")),
            ),
            offset = 0,
            limit = 10,
            querySort = QuerySort.desc("last_message_at")
        ).apply {
            watch = true
            state = true
        }

        client.queryChannels(request).enqueue { result ->
            if (result.isSuccess) {
                val channels: List<Channel> = result.data()
                Log.d("Channel", channels.toString())
            } else {
                showSnackBar("Querying channel Failed")
            }
        }

        val message = Message(text = "Sample message text")

        channelClient.sendMessage(message).enqueue { result ->
            if (result.isSuccess) {
                sentMessage = result.data()
                binding.tvMessage.text = sentMessage.text
            } else {
                showSnackBar("Adding message Failed")
            }
        }

        reactionViewModel.messageId.observe(this) { messagId ->
            if (messagId != null) {
                getReactions(messagId)
            }
        }

        binding.btnChannelMessages.setOnClickListener {
            val intent = Intent(this, ChannelMessagesActivity::class.java)
            intent.putExtra("channelId", channelId)
            startActivity(intent)
        }
    }

    private fun showReactions() {
        val modalbottomSheetFragment = ReactionsBottomSheet(channelClient, sentMessage)
        modalbottomSheetFragment.show(supportFragmentManager, modalbottomSheetFragment.tag)
    }

    private fun getReactions(messageId: String) {
        channelClient.getReactions(
            messageId = messageId,
            offset = 0,
            limit = 10,
        ).enqueue { result ->
            if (result.isSuccess) {
                val reactions: List<Reaction> = result.data()
                binding.rvReactions.visibility = View.VISIBLE
                reactionsAdapter.submitList(reactions)
                binding.rvReactions.adapter = reactionsAdapter
            } else {
                showSnackBar("Getting Reactions Failed: ${result.error().message}")
            }
        }
    }

    private fun deleteReaction(reaction: Reaction) {
        channelClient.deleteReaction(
            messageId = reaction.messageId,
            reactionType = reaction.type,
        ).enqueue { result ->
            if (result.isSuccess) {
                Log.d("Reaction Deleted", "Reaction ${reaction.type} has been deleted")
            } else {
                showSnackBar("Delete Failed")
            }
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}