//
//  NavigationController.swift
//  FritzVisionVideoDemo
//
//  Created by Steven Yeung on 11/8/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import UIKit

class NavigationController: UINavigationController {

  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .default
  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
  }

  override func viewDidLoad() {
    super.viewDidLoad()

    navigationBar.tintColor = .white
    navigationBar.shadowImage = UIImage()
    navigationBar.isTranslucent = true
    navigationBar.setBackgroundImage(UIImage(), for: .default)
    let blurView = createBlurView()
    navigationBar.addSubview(blurView)

    let titleFont = UIFont.systemFont(ofSize: 17, weight: .bold)
    navigationBar.titleTextAttributes = [
      .font: titleFont,
      .foregroundColor: UIColor.white
    ]

    let buttonFont = UIFont.systemFont(ofSize: 14, weight: .semibold)
    UIBarButtonItem.appearance(whenContainedInInstancesOf: [NavigationController.self])
      .setTitleTextAttributes([ .font: buttonFont], for: .normal)
    UIBarButtonItem.appearance(whenContainedInInstancesOf: [NavigationController.self])
      .setTitleTextAttributes([ .font: buttonFont], for: .highlighted)
    UIBarButtonItem.appearance(whenContainedInInstancesOf: [NavigationController.self])
      .setTitleTextAttributes([ .font: buttonFont], for: .disabled)
    UIBarButtonItem.appearance(whenContainedInInstancesOf: [NavigationController.self])
      .setTitleTextAttributes([ .font: buttonFont], for: .selected)
  }

  private func createBlurView() -> UIVisualEffectView {
    let visualEffectView   = UIVisualEffectView(effect: UIBlurEffect(style: .dark))

    var bounds = navigationBar.bounds
    let notchHeight: CGFloat = UIDevice.hasNotch ? 30.0 : 0.0
    bounds.size.height += 20 + notchHeight
    bounds.origin.y -= 20 + notchHeight

    visualEffectView.isUserInteractionEnabled = false
    visualEffectView.frame = bounds
    visualEffectView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    visualEffectView.layer.zPosition = -1
    return visualEffectView
  }
}

extension UIDevice {
  enum DevicePlatform {
    case other
    case iPhone6S
    case iPhone6SPlus
    case iPhone7
    case iPhone7Plus
    case iPhone8
    case iPhone8Plus
    case iPhoneX
    case iPhoneXS
    case iPhoneXSMax
    case iPhoneXR
    case simulator
  }

  static var hasNotch: Bool {
    return platform == .iPhoneXSMax ||
      platform == .iPhoneXS ||
      platform == .iPhoneXR ||
      platform == .iPhoneX || platform == .simulator
  }

  static var platform: DevicePlatform {
    var sysinfo = utsname()
    uname(&sysinfo)
    let platform = String(bytes: Data(bytes: &sysinfo.machine, count: Int(_SYS_NAMELEN)), encoding: .ascii)!.trimmingCharacters(in: .controlCharacters)
    switch platform {
    case "x86_64":
      return .simulator
    case "iPhone11,2":
      return .iPhoneXS
    case "iPhone11,4", "iPhone11,6":
      return .iPhoneXSMax
    case "iPhone11,8":
      return .iPhoneXR
    case "iPhone10,1", "iPhone10,4":
      return .iPhone8
    case "iPhone10,2", "iPhone10,5":
      return .iPhone8Plus
    case "iPhone10,3", "iPhone10,6":
      return .iPhoneX
    case "iPhone9,2", "iPhone9,4":
      return .iPhone7Plus
    case "iPhone9,1", "iPhone9,3":
      return .iPhone7
    case "iPhone8,2":
      return .iPhone6SPlus
    case "iPhone8,1":
      return .iPhone6S
    default:
      return .other
    }
  }
}
