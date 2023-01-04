
# Spotify Pro

Is a simple online music player project that gives a similar feel as that of Spotify.
I used exoplayer for the audio player functionality.
And a foreground service which starts a notification channel fully controllable by the user.
The project employs reactive programming using the MVVM architecture.

### Song List
[<img src='https://github.com/lumu-daniel/SpotifyPro/blob/master/app/src/main/res/drawable/song_list.png' alt='Search for Pokemon' height='280'>]
 Song list is fetched from firestore and presented in a recycler view.
When a song is selected it will play in the lower bottom view pager with swipe functionality with right for next and left for right


### Single Song
[<img src='https://github.com/lumu-daniel/SpotifyPro/blob/master/app/src/main/res/drawable/song_display.png' alt='Purchase Result Failed' height='280'>]
 When either the notification or the current element in the view pager is clicked it will navigate user to the song screen.
 This will show the currently playing song with the seek bar to show the progress.


### Notification Channel
[<img src='https://github.com/lumu-daniel/SpotifyPro/blob/master/app/src/main/res/drawable/notification_channel.png' alt='Purchase Result Failed' height='280'>]
 Is used to give the user control when the application is in the background.


## Technologies and Tools
#### UX Design: &nbsp; [<img src='https://github.com/lumu-daniel/lumu-daniel/blob/main/assets/images/jc_icon.png' alt='JetPack Compose' height='18'>](https://www.figma.com/developers) [JetPack Compose](https://developer.android.com/jetpack) &nbsp; | &nbsp; [<img src='https://github.com/lumu-daniel/lumu-daniel/blob/main/assets/images/adobe-xd.gif' alt='Adobe XD' height='18'>](https://www.adobe.com/products/xd.html) [Adobe XD]

#### Languages/Scripts: &nbsp; [<img src='https://github.com/lumu-daniel/lumu-daniel/blob/main/assets/images/java.png' alt='Java' height='18'>](https://www.java.com/en/) [Java] &nbsp; | &nbsp; [<img src='https://github.com/lumu-daniel/lumu-daniel/blob/main/assets/images/kotlin.png' alt='TypeScript' height='18'>](https://kotlinlang.org/) [Kotlin] &nbsp; | &nbsp; [<img src='https://github.com/lumu-daniel/lumu-daniel/blob/main/assets/images/dart.png' alt='HTML5' height='18'>](https://dart.dev/) [Dart] &nbsp; | &nbsp; [<img src='https://github.com/lumu-daniel/lumu-daniel/blob/main/assets/images/javascript.gif' alt='Java Script' height='18'>](https://www.javascript.com/) [Java Script]

#### Frontend: &nbsp; [<img src='https://github.com/lumu-daniel/lumu-daniel/blob/main/assets/images/jc_icon.png' alt='JetPack Compose' height='18'>](https://reactjs.org/docs/getting-started.html) JetPack Compose &nbsp; | &nbsp; [<img src='https://github.com/lumu-daniel/lumu-daniel/blob/main/assets/images/material-ui.png' alt='Material Design' height='18'>](https://material.io/design) [Material Design] &nbsp; | &nbsp; [<img src='https://github.com/lumu-daniel/lumu-daniel/blob/main/assets/images/bootstrap.png' alt='Android Views' height='18'>](https://developer.android.com/reference/android/view/View) [Android Views]
