# All About Reactions with the Stream Chat Android SDK



## Introduction

Modern chat apps need something to make the messaging platform lively and increase user engagement. Reactions have emerged as a top feature to help in this.

Stream provides a nice Android SDK for you to add reactions in a matter of minutes in your application. 

In this tutorial you're going to learn how to:

- Add reactions to a message
- Deleting a reaction to a message
- Cumulative Reactions
- Paginating Reactions
- Adding Custom Reactions
- Styling Reactions

**Note:** This tutorial assumes you already know the basic knowledge on the Stream API's. To get started checkout the [Android Chat Messaging Tutorial](https://getstream.io/tutorials/android-chat/#kotlin)

## Adding a Reaction

![Stream Reactions](https://github.com/wangerekaharun/StreamReactions/blob/master/images/reactions.png)

Reactions are on most social media apps and there's many of them for example:

- Like
- Favourite/Love
- Sad/Angry
- Clap

They're used to represent common quick reactions that users can have after seeing a post.  Using the Stream SDK, you can add reactions to your messages in a very easy way.

You'll be seeing how to add a reaction. This is how the app looks like:

![](https://github.com/wangerekaharun/StreamReactions/blob/master/images/first_run.png)


The app has a <code>TextView</code> which has a **Sample message text**. It has a favourite <code>ImageButton</code>> beside it and a <code>VIEW CHANNEL</code> Button.  You'll see the channel functionalities later on in this tutorial.

Tap on the favourite <code>ImageButton</code>. The following <code>BottomSheet</code> modal will appear:

![Add Reaction](https://github.com/wangerekaharun/StreamReactions/blob/master/images/add_reaction.png)

This is the UI for adding your reaction. It has buttons for selecting the reaction type. There are other buttons for adding or reducing the score for your reaction. Once you set everything it should be as follows:

![Reactions Input](https://github.com/wangerekaharun/StreamReactions/blob/master/images/reactions_input.png)



Now that you've seen the UI part, to save your reaction, first, you have to create a <code>Reaction</code> object which has properties of your reaction.

```Kotlin
 val reaction = Reaction(
            messageId = sentMessage.id,
            type = reactionType,
            score = score,
            extraData = mutableMapOf("customField" to 1),
        )
```

In the code above:

- You're adding the <code>messageId</code> for the message that you want to react to.
- You specify the type of the reaction. The type is from the one you selected on the app.
- Here, you're also adding the score for the reaction. The value for the score is the one you set.
- Lastly, you add any extra information.

You have your reaction object ready. Next, you'll be seeing how to send that reaction over to the Stream Client.

To send a message reaction, you need a <code>ChannelClient</code>. A <code>ChannelClient</code> enables you to:

- Create Channels
- Add users to channels
- Add messages to channels
- React to messages in channels.

On the sample app, you declare the client at the top of the file as:

```Kotlin
private lateinit var channelClient: ChannelClient
```

It's initialised in your <code>onCreate</code> method as follows:

```kotlin
channelClient = client.channel(channelType = "messaging", channelId = "general")
```
The logic to save a reaction is on the <code>ReactionsBottomSheet</code> . You'll need to pass the <code>sentMesage</code> and <code>channelCient</code> to the class as shown below:

```Kotlin
val modalbottomSheetFragment = ReactionsBottomSheet(channelClient, sentMessage)
modalbottomSheetFragment.show(supportFragmentManager,modalbottomSheetFragment.tag)
```

Now that you have everything set, to send your reaction object, you need the following code:

```Kotlin
channelClient.sendReaction(reaction).enqueue { result ->
            if (result.isSuccess) {
                val sentReaction = result.data()
                reactionViewModel.setMessageId(reaction.messageId)
                Log.d("Message","Message Reaction score is: ${sentReaction.score}")
                dismiss()
            } else {
                requireContext().toast("Adding reaction Failed")
            }
        }
```

Here, you send your reaction object to Stream Client. You're also handling the success and failure states from the callback response. Depending on the result, you handle the states according to your app's needs. In this scenario you:

- Call the <code>setMessageId()</code> method in the ViewModel. You pass the id of the reaction which has been saved so that you observe it on your activity.

- Show a <code>Toast</code> when saving the reactions fails. 

  

  When the reaction is saved, you'll see the following image:

  

  ![Saved Reactions](https://github.com/wangerekaharun/StreamReactions/blob/master/images/saved_reactions.png)

  

Congratulations! You've learned how to add your first message reaction. With the Stream's Android SDK, it's a very easy and seamless way to do that. Next, you'll be seeing how to remove the reaction.

## Removing a Reaction

To remove a reaction, you only need the <code>messageId</code> and <code>reactionType</code>. The code to remove is as follows:

```Kotlin
 channelClient.deleteReaction(
            messageId = reaction.messageId,
            reactionType = reaction.type,
        ).enqueue { result ->
            if (result.isSuccess) {
                Log.d("Reaction Deleted","Reaction ${reaction.type} has been deleted")
            } else {
                showSnackBar("Delete Failed")
            }
        }
```

This method is like the one for adding reactions. The difference is that you're calling the <code>deleteReaction</code> method to delete the reaction. You're also passing the <code>messageId</code> and <code>reactionType</code> for the reaction that you want to remove. You're also handling the success and error states.

## Cumulative Reactions

Another famous reaction is the clap reaction or enabling a user to react more than once. This is useful in blogs and articles. The Stream's Android SDK allows this functionality out of the box. You do this is by adding the <code>score</code> in your reaction model as shown below:

```Kotlin
val reaction = Reaction(
    messageId = sentMessage.id,
    type = reactionModel.name,
    score = reactionModel.score,
    extraData = mutableMapOf("customField" to 1),
)
```

Here you specify the <code>score</code> for your reaction. The value for the <code>score</code> is the one from the input.  And now you can send the reaction in a similar manner as follows:

```kotlin
channelClient.sendReaction(reaction).enqueue { result ->
            if (result.isSuccess) {
                val sentReaction = result.data()
                reactionViewModel.setMessageId(reaction.messageId)
                Log.d("Message","Message Reaction score is: ${sentReaction.score}")
                dismiss()
            } else {
                requireContext().toast("Adding reaction Failed")
            }
        }
```

## Paginating Reactions

On Social apps, a message or a post can get more than 10 reactions and even at times thousands of reactions. In such cases, you can not display all these reactions in your app. Stream's Android SDK allows you to paginate reactions as you fetch them. In this way, you specify the number that you want to fetch according to the UI of your app.

Here's how an example of to fetch reactions with pagination:

```kotlin
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
```

The <code>getReactions()</code> method takes in three parameters:

- <code>messageId</code> - ID of the message whose reactions you want to fetch.
- <code>offset</code>- The position at which you want to start fetching your reactions. This is useful when at times you want to fetch from let's say the 10th reaction. You set the offset to be 10. For this case, it's 0 since you want to fetch from the first.
- <code>limit</code>- This specifies the number of reactions you want to fetch at a single time. The number is as per your needs.

From the sample project, here's how the reactions are:

![Paginated Reactions](https://github.com/wangerekaharun/StreamReactions/blob/master/images/paginated_reactions.png)

The API offers a lot of flexibility according to your needs. In the next section you'll be looking and the reactions from the API itself and how you can customize them.

## Looking at Stream Reactions

The Stream's Android SDK has UI components for reactions built-in. They're useful in case you don't need custom ones as you've been learning in the sections above. This is how they look:

![Inbuilt Reactions](https://github.com/wangerekaharun/StreamReactions/blob/master/images/inbuilt_reactions.png)

The UI offers a couple of commons reactions for example love, like, thumbs. This makes it easier if you want to adopt them and use them in your app.

However, at times the requirement for your app can be different. Can I remove the default reactions? Can I add custom reactions? Can I be able to apply my app style to the reactions? Are some of the questions you might ask yourself.

And the good news is....drum rolls :-) Yes, you can be able to do all that. From version **4.9** of the Stream's Android SDK, you can be able to customize all this. You'll be learning how to customize next.

## Customizing the Reactions UI

To customize the reactions, you'll be using the <code>SupportedReactions</code> . It allows you to define reactions. It accepts two parameters on its constructor:

- <code>context</code> - context of your class.
- <code>reactions</code> - This is a Map of keys which hold the reaction type and a <code>ReactionDrawable</code>. This is the parameter you use to add your custom reactions. If you don't provide any reactions, by default it'll use the standard reactions.

A <code>ReactionDrawable</code>is an object that has the reaction icon. It handles the states for the icon for example the active and inactive states.

To define your own custom reaction, create a custom <code>ReactionDrawable</code> as follows:

```kotlin
fun clapDrawable(context: Context): SupportedReactions.ReactionDrawable {
    val drawableInactive = ContextCompat.getDrawable(context, R.drawable.ic_clapping)!!
    val drawableActive = ContextCompat.getDrawable(context, R.drawable.ic_clapping)!!.apply {
        setTint(ContextCompat.getColor(context,android.R.color.holo_red_dark ))
    }
    return SupportedReactions.ReactionDrawable(drawableInactive, drawableActive)
}
```

This is a normal function which has <code>context</code> as arguments. It returns a <code>ReactionDrawable</code>. You also set the <code>drawableInactive</code> and <code>drawableActive</code> icons. For the the active icon you're changing the color of the icon.

In the sample project you can check on the <code>ReactionDrawables.kt</code> file. It is under the **utils** package for more custom drawable.

Now, you have the drawables ready for use. Next is to create a map of reactions type and the drawables as follows:

```Kotlin
val reactions: Map<String, SupportedReactions.ReactionDrawable> = mapOf(
    "like" to likeDrawable(applicationContext),
    "clap" to clapDrawable(applicationContext),
    "wondering" to wonderingDrawable(applicationContext),
    "brilliant" to brilliantDrawable(applicationContext),
    "handshake" to handShakeDrawable(applicationContext),
)
```

Here you have all your custom reactions and their icons. This is all you need. After defining this, the final thing to do is to make sure your app uses the custom reactions that you've created.

You do this by adding this:

```Kotlin
ChatUI.supportedReactions = SupportedReactions(applicationContext, reactions)
```

Here you're passing your reactions to the Stream's Android SDK. And now this is how your reactions look like:

![Custom Reactions](https://github.com/wangerekaharun/StreamReactions/blob/master/images/custom_reactions.png)

Woohoo! As you can now see, all the reactions are custom ones and with different colors!. And as you've noted, it's very easy to achieve this in easy steps. 

In the next section, you'll see how to customize the colors of the reaction card and the titles.

## Adding your Custom Styling to Reactions

At times you need the UI for the reactions to adapt to the styling of your app. The Stream's Android SDK also supports this in a simple and straight forwad way. For the title text you add the following in your <code>themes.xml</code> file:

```xml
<item name="streamUiUserReactionsTitleTextColor">@color/white</item>
<item name="streamUiUserReactionsTitleTextSize">18sp</item>
<item name="streamUiUserReactionsTitleTextFont">@font/benton_sans_book</item>
<item name="streamUiUserReactionsTitleTextStyle">italic</item>
<item name="streamUiUserReactionsBackgroundColor">@color/purple_200</item>
```

From the above attributes you can see you can be able to change:

- Color
- Text Size
- Font
- Style
- Card Background.

This is how the final results look like:

![Custom Style Reactions](https://github.com/wangerekaharun/StreamReactions/blob/master/images/reactions_custom_style.png)

Albeit yours will be different depending on your app colors and styling requirements.

## Conclusion

You've learned how to add, remove, paginate and add cumulative reactions in this tutorial. In the process, you've also learned how you can create your custom reactions. You've also seen how to customize reactions in the Stream SDK, and also add your custom reactions.

You can get the full sample project with examples in this tutorial [here](https://github.com/wangerekaharun/StreamReactions).

