//
//  SingleScreenViewController.swift
//  FritzVisionVideoDemo
//
//  Created by Steven Yeung on 11/8/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import Foundation
import UIKit
import Fritz

public class SingleScreenViewController: UIViewController {

  @IBOutlet weak var progressBar: UIProgressView!
  @IBOutlet weak var exportLabel: UILabel!

  public var videoStrategy: VideoFilterStrategy!
  var videoPicker: VideoPicker?
  var videoPlayer: FritzVisionVideo?
  var videoView = FritzVideoView()

  var exporter: AVAssetExportSession?
  var progressBarTimer: Timer?

  public override func viewDidLoad() {
    super.viewDidLoad()
    updateNavBar()
  }

  public override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    // Show video picker
    videoPicker = VideoPicker(controller: self, delegate: self)
    videoPicker!.present(from: view)
  }

  public override func viewWillDisappear(_ animated: Bool) {
    progressBarTimer?.invalidate()
    exporter?.cancelExport()
    videoView.pause()
    view.subviews.forEach({ $0.removeFromSuperview() })
    super.viewWillDisappear(animated)
  }
}

/// For selecting a video from the Camera Roll
extension SingleScreenViewController: VideoPickerDelegate {
  
  @objc public func didSelect(url: URL?) {
    guard let url = url else { return }
    videoPlayer = FritzVisionVideo(url: url, applyingFilters: videoStrategy.filters)

    // Add the video view
    videoView.frame = view.bounds
    view.addSubview(videoView)
    view.bringSubviewToFront(videoView)

    // Set the video and start playing it
    videoView.fritzVideo = videoPlayer
    videoView.play()
  }
}

/// For navigation bar functionality
extension SingleScreenViewController {

  /// Add the export button to the navigation bar and set the title
  func updateNavBar() {
    let exportButton = UIImage(named: "ExportIcon")
    let exportItem = UIBarButtonItem(
      image: exportButton,
      style: .plain,
      target: self,
      action: #selector(exportButtonTapped)
    )

    self.navigationItem.rightBarButtonItem = exportItem
    self.navigationItem.title = videoStrategy.title
  }

  /// Start the export process
  @objc func exportButtonTapped(_ button: UIBarButtonItem) {
    guard exporter == nil, let videoPlayer = videoPlayer else { return }
    videoView.pause()

    // Create a file to export to
    let fileName = videoStrategy.title.replacingOccurrences(of: " ", with: "_")
    var exportUrl = FileManager.default.urls(
      for: .documentDirectory,
      in: .userDomainMask
      )[0].appendingPathComponent("\(fileName).mov", isDirectory: false)
    exportUrl = URL(fileURLWithPath: exportUrl.path)

    // Delete the contents of the file if it already exists
    try? FileManager.default.removeItem(at: exportUrl)

    exporter = videoPlayer.export(to: exportUrl, as: AVFileType.mov) { result in
      self.progressBarTimer?.invalidate()
      switch result {
      case .success:
        // Notify the user of a successful export
        DispatchQueue.main.async() {
          self.progressBar?.setProgress(1, animated: true)
          self.exportLabel?.text = "Saved to Camera Roll"
        }
      case .failure:
        // Notify the user of a failed export
        DispatchQueue.main.async() {
          self.exportLabel?.text = "Export failed"
        }
      }
    }
    showExportView()
  }
}

/// For viewing and updating export progress
extension SingleScreenViewController {

  /// Blur the background and show the export progress
  func showExportView() {
    let blurEffect = UIBlurEffect(style: UIBlurEffect.Style.regular)
    let blurEffectView = UIVisualEffectView(effect: blurEffect)
    blurEffectView.frame = view.bounds
    blurEffectView.autoresizingMask = [.flexibleWidth, .flexibleHeight]

    // Show the progress bar and label
    progressBar.isHidden = false
    exportLabel.isHidden = false

    // Start timer to update export progress every second
    progressBarTimer = Timer.scheduledTimer(
      timeInterval: 1,
      target: self,
      selector: #selector(updateProgress),
      userInfo: nil,
      repeats: true
    )

    view.addSubview(blurEffectView)
    view.bringSubviewToFront(progressBar)
    view.bringSubviewToFront(exportLabel)
  }

  /// Update the export progress
  @objc func updateProgress(_ timer: Timer) {
    guard let exportJob = exporter else { return }
    progressBar.setProgress(exportJob.progress, animated: true)
  }
}
