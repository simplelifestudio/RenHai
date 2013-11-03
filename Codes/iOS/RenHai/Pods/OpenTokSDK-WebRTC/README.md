OpenTok 2.0 iOS Client SDK
==========================

The OpenTok 2.0 iOS SDK lets you use OpenTok on WebRTC video sessions in apps you build for iPad, iPhone, and
iPod touch devices.

To get started, [sign up for a developer account](https://dashboard.tokbox.com/signups/new) and get your API Key.

You can use OpenTok video sessions across iOS devices AND web clients that use OpenTok 2.0 (OpenTok on WebRTC).
For details about using this SDK with web clients, see the
[JavaScript documentation](http://tokbox.com/opentok/libraries/client/js/) at the TokBox website.

Using the OpenTok 2.0 iOS SDK
-----------------------------

See the [reference documentation](http://opentok.github.io/opentok-ios-sdk-webrtc/) for details on the API.

See the [release notes](release_notes.md) for information on the latest version of the SDK and for a list of known
issues.

Supported devices
-----------------

The OpenTok 2.0 iOS SDK is supported on the following devices:

* iPhone 4S / 5
* iPad 2 / 3 / 4 / mini

Web browsers supported:

* Google Chrome, Google Chrome Beta
* Mozilla Firefox 22+

The OpenTok 2.0 iOS SDK is supported on wifi connections.

Developer and client requirements
---------------------------------

* The OpenTok 2.0 iOS SDK requires XCode 4.2 or later. XCode 4.2 requires Mac OS 10.6.8 or later.

* You need to test OpenTok apps on an iOS device running iOS 5. The OpenTok iOS SDK supports iPhone 3GS
(subscribing only), iPhone 4 and higher, iPod touch 4th generation and higher, and all iPad versions. 

* To test OpenTok apps on an iOS device, you will need to register as an Apple iOS developer at
[http://developer.apple.com/programs/register/](http://developer.apple.com/programs/register/).

Setting up your development environment
---------------------------------------

Download the [OpenTok iOS SDK](https://github.com/opentok/opentok-iOS-sdk). 

The OpenTokHelloWorld app publishes to a demo session, and subscribes to its own stream
(or any other stream in the session). Included in the OpenTok 2.0 iOS SDK are files you will need to develop
your own apps:

* **Opentok.framework** -- Add this framework to your project. Note that to use the library you must also 
include the opentok.bundle file and the headers included in in the Frameworks directory of the OpenTokHelloWorld
sample app.

* **opentok.bundle** -- Add the opentok.bundle file to your project. The opentok.bundle file is located
in the Opentok.framework/Versions/A/Resources subdirectory of the OpenTok iOS SDK. 

* **Frameworks directory** -- The OpenTok iOS library uses linked frameworks and dynamic libraries provided by iOS.
We cannot pre-link them in the OpenTok framework, so your project must link them. Expand the "Frameworks" directory
of the sample application in XCode project browser. Drag and drop the contents of this directory into your own iOS project.

You can generate a new session ID at this URL:

https://dashboard.tokbox.com/projects

You can also create a web page that connects to the same session as the app.

Using the sample apps
---------------------

The samples directory includes the following apps:

* The OpenTokHello sample app shows the most basic functionality of the OpenTok iOS SDK: connecting to sessions, publishing streams, and subscribing to streams.

* The OpenTokFullTutorial sample app uses more of the OpenTok iOS SDK than the OpenTokHello sample app does.


Creating your own app using the OpenTok 2.0 iOS SDK
---------------------------------------------------

Here are the basic steps in creating your own app:

1. In XCode, create a new project. The simplest application is a Single-View application.

2. Open the project navigator in Xcode.

3. Drag the Opentok.framework directory from the Mac OS Finder to the Frameworks directory for for your project in XCode.

4. Drag the opentok.bundle file from the Mac OS Finder to root directory for your project in XCode.

	The opentok.bundle file is located in the Opentok.framework/Versions/A/Resources subdirectory of the OpenTok iOS SDK.


5. The OpenTok framework requires a few other frameworks and libraries to be added to your project. The easiest way to add them is
to copy them from the OpenTokHello sample app.

	Open the OpenTokHello sample app in XCode. Drag all of the additional frameworks from the frameworks folder (in the project navigator)
	of the OpenTokHello project into the frameworks folder of your project.
	
	The additional frameworks and libraries include the following: AudioToolbox.framework, AVFoundation.framework, CFNetwork.framework,
	CoreAudio.framework, CoreMedia.framework, CoreTelephony.framework, CoreVideo.framework, libz.dylib, libstdc++.dylib, MobileCoreServices.framework,
	OpenGLES.framework, QuartzCore.framework, Security.framework, SystemConfiguration.framework.
	
	**Note:** If you started from a blank project template in Xcode 5, you will need to set the iOS Deployment Target (IPHONEOS_DEPLOYMENT_TARGET) build setting to 6.1.
	Otherwise, you may have linking errors when you compile. This issue will be addressed in a future release.
	
6. When running in the background, the OpenTok SDK requires certain Info.pList settings to continue publishing and subscribing to
audio video streams. In XCode, open Info tab for your app's target and add a Required Background Modes entry (if it does not already
exist). Add the following to the list of required background modes:

	- App plays audio
	- App provides Voice over IP services

Next steps:

1. In the ViewController.h file, add the following line (after `import <UIKit/UIKit.h>`):

		#import <Opentok/Opentok.h>

2. Assuming that your ViewController will implement the OTSessionDelegate, OTPublisherDelegate, and OTSubscriberDelegate protocols,
edit the `@interface` declaration in the ViewController.h file to the following:

		@interface ViewController : UIViewController <OTSessionDelegate, OTSubscriberDelegate, OTPublisherDelegate>

3. You will need to implement the required method in each protocol that you add (OTSessionDelegate, OTSubscriberDelegate, OTPublisherDelegate).

4. Add code to initialize and connect to an OpenTok session. And add code to publish and subscribe to streams.
See the code in the OpenTokHello application.

Connect with TokBox and with other OpenTok developers
-----------------------------------------------------

Your comments and questions are welcome. Come join the conversation at the [OpenTok iOS SDK forum](http://www.tokbox.com/forums/ios).

More information
----------------

See the [reference documentation](docs/reference.md).

For more information on OpenTok, go to <http://www.tokbox.com/>.
