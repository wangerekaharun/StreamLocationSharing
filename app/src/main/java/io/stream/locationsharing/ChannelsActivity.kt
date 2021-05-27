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

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.livedata.ChatDomain
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory
import io.stream.locationsharing.databinding.ActivityChannelsBinding
import io.stream.locationsharing.utils.createLocationRequest


class ChannelsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChannelsBinding
    lateinit var locationSettingsClient: LocationSettingsRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channels)

        val locationClient: SettingsClient = LocationServices.getSettingsClient(this)
        buildLocationSettingsRequest()
        checkLocationSettings(locationClient)

        if (!isPermissionGranted()){
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        binding = ActivityChannelsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidHV0b3JpYWwtZHJvaWQifQ.NhEr0hP9W9nwqV7ZkdShxvi02C5PR7SJE7Cs4y7kyqg"
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
            startActivity(intent)
        }
    }

    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(createLocationRequest())
        locationSettingsClient = builder.build()
    }

    private fun checkLocationSettings(
        locationClient: SettingsClient
    ) {
        locationClient.checkLocationSettings(locationSettingsClient)
            .addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                        this,
                        201
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.e("SettingsError", sendEx.message.toString())
                }
            }
        }
    }

    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.i("Permission", "permission granted")
            } else {
                Log.i("Permission", "permission denied")
            }
        }
}