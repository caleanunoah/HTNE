//
//  SplitScreenViewController.swift
//  FritzVisionVideoDemo
//
//  Created by Steven Yeung on 11/11/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import Foundation
import Fritz

public class SplitScreenViewController: UIViewController {

  public var topStrategy: VideoFilterStrategy!
  public var bottomStrategy: VideoFilterStrategy!

  let topScreen = FritzVideoView()
  let bottomScreen = FritzVideoView()

  var videoPicker: VideoPicker?

  public override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    // Show video picker
    videoPicker = VideoPicker(controller: self, delegate: self)
    videoPicker!.present(from: view)
  }

  public override func viewWillDisappear(_ animated: Bool) {
    topScreen.pause()
    bottomScreen.pause()
    view.subviews.forEach({ $0.removeFromSuperview() })
    super.viewWillDisappear(animated)
  }
}

extension SplitScreenViewController: VideoPickerDelegate {

  public func didSelect(url: URL?) {
    guard let url = url else { return }
    let topVideo = FritzVisionVideo(url: url, applyingFilters: topStrategy.filters)
    let bottomVideo = FritzVisionVideo(url: url, applyingFilters: bottomStrategy.filters)

    // Setup both video views
    let boundWidth = view.bounds.width
    let boundHeight = view.bounds.height / 2
    let topBounds = CGRect(x: 0, y: 0, width: boundWidth, height: boundHeight)
    let bottomBounds = CGRect(x: 0, y: boundHeight, width: boundWidth, height: boundHeight)
    topScreen.frame = topBounds
    topScreen.fritzVideo = topVideo
    bottomScreen.frame = bottomBounds
    bottomScreen.fritzVideo = bottomVideo

    // Add both video views
    view.addSubview(topScreen)
    view.bringSubviewToFront(topScreen)
    view.addSubview(bottomScreen)
    view.bringSubviewToFront(bottomScreen)

    // Start playing both videos
    topScreen.play()
    bottomScreen.play()
  }
}
