# InventoryDBM

#### InventoryDBM is an Android application which allows users to have their own inventory of products for sale. Users can upload a thumbnail of the image along with details of the product such as name, brand, price, quantity, year of manufacture, and so on. Additionally, they can delete a product at any time by just swiping the product on the main screen or clicking on a button on the detail screen. An explanation message is displayed to the user when the inventory is empty.

#### The database of InventoryDBM was developed using Android Architecture Components such as the Room library, LiveData, and ViewModel.

</br>

<img src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/InventoryApp%2F1.png?alt=media&token=787e0f12-8649-4d78-9c5e-4776021339fa" width="375" height="725"><img height="725" hspace="20"/><img src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/InventoryApp%2F2.png?alt=media&token=683f583f-4a4e-45dd-a46b-ab957fdfdaa3" width="375" height="725">
<img width="770" vspace="20"/>
<img src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/InventoryApp%2F3.png?alt=media&token=2bab629b-399c-43dc-bdaa-4377d031f552" width="375" height="725"><img height="725" hspace="20"/><img src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/InventoryApp%2F4.png?alt=media&token=994029cb-d8eb-4622-8d70-f3c6961a2755" width="375" height="726">
<img width="770" vspace="20"/>
<img src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/InventoryApp%2F6.png?alt=media&token=b6b968b9-468d-4b8b-b1a7-c11a1f49a1a7" width="375" height="725"><img height="725" hspace="20"/><img src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/InventoryApp%2F5.png?alt=media&token=6cdf6fc1-5f9d-4c01-a75e-ff96092c93bd" width="375" height="726">
<img width="770" vspace="20"/>
<img src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/InventoryApp%2F7.png?alt=media&token=f8bd920c-6897-4070-b056-78749d070711" width="375" height="725"><img height="725" hspace="20"/><img src="https://firebasestorage.googleapis.com/v0/b/inventoryapp-c8633.appspot.com/o/InventoryApp%2F8.png?alt=media&token=95d3f105-dbf5-4064-9415-cbe39368e1d1" width="375" height="726">
<img width="770" vspace="20"/>

## Getting Started

#### These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

## Prerequisites

#### InventoryDBM was developed using Android Studio IDE so you must install it on your computer before proceeding:

###### https://developer.android.com/studio/

## Next Steps

#### You can proceed to clone the project to your local machine, but DO NOT enter Android Studio yet. First, you need to set up your Firebase project as indicated in the next paragraph.

#### InventoryDBM requires Firebase Storage for uploading images and Firebase Authentication for security reasons (although the app uses an anonymous sign-in method). Therefore, in order to use InventoryDBM, you need to set up a project in the Firebase console and then add Firebase to your Android app by clicking on the corresponding button in the Project Overview section. This last part involves that you include the required data of your local machine such as the project package name and the SHA-1 fingerprint certificate. For further information, check this link:

###### https://firebase.google.com/docs/android/setup

#### Once you have your project ready, you must add Firebase to your Android app. Remember to download the **_google-services.json_** file and move it to the app directory (into the app module). The Firebase platform will ask you to run the app so it can confirm a successful communication. Therefore, open Android Studio, build the project and run it. DO NOT try to insert items yet. Ignore the Authentication Failed toast message displayed and proceed to the next step.

#### If the communication is successful, uninstall the app and go to the Authentication section in the Firebase console to enable anonymous sign-in method.

#### Lastly, go to the Storage section in the Firebase console and create a folder named: **inventory_photos**.

#### Reinstall the app and start using it on your Android device.

## Compatibility

#### Minimum Android SDK: InventoryDBM requires a minimum API level of 21.
#### Compile Android SDK: InventoryDBM requires you to compile against API 32 or later.

## Getting Help

#### To report a specific problem or feature request, open a new issue on Github. For questions, suggestions, or anything else, email to:

###### arturo.lpc12@gmail.com

## Author

#### Daniel Bedoya - @engspa12 on GitHub

## License

#### See the LICENSE file for details.
