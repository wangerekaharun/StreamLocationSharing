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
package io.stream.locationsharing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.client.api.models.QuerySort
import io.getstream.chat.android.client.channel.ChannelClient
import io.getstream.chat.android.client.models.*
import io.getstream.chat.android.livedata.ChatDomain
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory
import io.stream.locationsharing.databinding.ActivityChannelsBinding
import io.stream.locationsharing.databinding.ActivityLocationSharingBinding
import io.stream.locationsharing.utils.locationFlow
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class LocationSharingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChannelsBinding
    private var sentMessage = Message()
    private lateinit var channelClient: ChannelClient
    private var channelId = ""
    private val mFusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private var currentLocation = LatLng(0.0,0.0)


    @OptIn(InternalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channels)

        binding = ActivityChannelsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mFusedLocationClient.locationFlow().collect {
                    currentLocation = LatLng(it.latitude, it.longitude)

                }
            }
        }

        val client = ChatClient.Builder("b67pax5b2wdq", applicationContext).build()
        ChatDomain.Builder(client, applicationContext).build()

        val user = User(
            id = "tutorial-droid",
            extraData = mutableMapOf(
                "name" to "Stream Location",
                "image" to "https://bit.ly/2TIt8NR",
            ),
        )
        client.connectUser(
            user = user,
            token = BuildConfig.TOKEN
        ).enqueue()

        val filter = Filters.and(
            Filters.eq("type", "messaging"),
            Filters.`in`("members", listOf(user.id)))

        val viewModelFactory = ChannelListViewModelFactory(filter, ChannelListViewModel.DEFAULT_SORT)
        val viewModel: ChannelListViewModel by viewModels { viewModelFactory }
        viewModel.bindView(binding.channelListView, this)
        binding.channelListView.setChannelItemClickListener { channel ->
            val intent = Intent(this, ChannelMessagesActivity::class.java)
            intent.putExtra("channelId", channel.cid)
            Log.d("channel", channel.messages.size.toString())
            startActivity(intent)
        }

        val message = Message(text = "Sample message text")

    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}