# Mask Recognition

[![Twitter](https://img.shields.io/badge/twitter-@fritzlabs-blue.svg?style=flat)](http://twitter.com/fritzlabs)

In this app, we use the Fritz AI Studio to train a model and create an app that detects whether a person in an image or video is wearing a face mask.

For the full tutorial, visit [our post on Heartbeat](https://heartbeat.fritz.ai/building-an-on-device-face-mask-detector-with-fritz-ai-studio-874a88ae2702).

<img src="https://thumbs.gfycat.com/MasculineShamefulHippopotamus-size_restricted.gif" width="250"/>

## Fritz AI

Fritz AI is the machine learning platform for iOS and Android developers. Teach your mobile apps to see, hear, sense, and think. Start with our ready-to-use feature APIs or connect and deploy your own custom models.

## Requirements

- Xcode 11.2 or later.
- Xcode project targeting iOS 10 or above. You will only be able to use features in iOS 11+, but you still can include Fritz in apps that target iOS 10+ and selectively enable for users on 11+.
- Swift projects must use Swift 4.1 or later.
- CocoaPods 1.4.0 or later.

## Getting Started

**Step 1: Create a Fritz AI Account**

[Sign up](https://app.fritz.ai/register?utm_source=github&utm_campaign=mask-recogntion) for a free Studio Sandbox account on Fritz AI in order to get started.

**Step 2: Clone / Fork the fritz-examples repository and open FritzMaskRecognitionDemo**

```
git clone https://github.com/fritzlabs/fritz-examples.git
```

**Step 3: Setup the project via Cocoapods**

Install dependencies via Cocoapods by running `pod install` from `fritz-examples/iOS/FritzMaskRecognitionDemo`

```
cd fritz-examples/iOS/FritzMaskRecognitionDemo
pod install
```

- Note you may need to run `pod update` if you already have Fritz installed locally.

**Step 4: Open up a new XCode project**

XCode > Open > FritzMaskRecognitionDemo.xcworkspace

**Step 5: Run the app**

Attach a device or use an emulator to run the app. If you get the error "Please download the Fritz-Info.plist", you'll need to register the app with Fritz (See Step 2).

## Documentation

[Fritz Docs Home](https://docs.fritz.ai/?utm_source=github&utm_campaign=mask-recognition)

[iOS SDK Reference Docs](https://docs.fritz.ai/iOS/latest/index.html?utm_source=github&utm_campaign=mask-recognition)

## Join the community

[Heartbeat](https://heartbeat.fritz.ai/?utm_source=github&utm_campaign=mask-recognition) is a community of developers interested in the intersection of mobile and machine learning. [Chat with us in Slack](https://www.fritz.ai/slack?utm_source=github&utm_campaign=mask-recognition) and stay up to date on the latest mobile ML news with our [Newsletter](https://www.fritz.ai/newsletter?utm_source=github&utm_campaign=mask-recognition).

## Help

For any questions or issues, please open an issue on the [Fritz AI Discourse](https://support.fritz.ai).
