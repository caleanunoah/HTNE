//
//  ViewController.swift
//  FritzImageLabelingDemo
//
//  Created by Steven Yeung on 10/23/19.
//  Copyright © 2019 Steven Yeung. All rights reserved.
//

import UIKit
import CoreML
import Vision
import AVFoundation
import Accelerate
import Fritz

extension Double {
  func format(f: String) -> String {
    return String(format: "%\(f)f", self)
  }
}


class ViewController: UIViewController, AVCaptureVideoDataOutputSampleBufferDelegate {
  @IBOutlet weak var cameraView: UIView!
  @IBOutlet weak var fpsLabel: UILabel!
  @IBOutlet weak var modelIdLabel: UILabel!
  @IBOutlet weak var modelVersionLabel: UILabel!
  
  @IBOutlet weak var predictionLabel: UILabel! {
      didSet { predictionLabel.text = "Loading... 🚀" }
  }

  @IBOutlet weak var confidenceLabel: UILabel! {
      didSet { confidenceLabel.text = nil }
  }

  var lastExecution = Date()
  var screenHeight: Double?
  var screenWidth: Double?

  // Use a pre-trained image labeling model from Fritz AI.
  lazy var visionModel = FritzVisionLabelModelFast()

  // To use your own custom image labeling model, follow the instructions here:
  // https://docs.fritz.ai/develop/vision/image-labeling/ios.html
  // lazy var visionModel = FritzVisionLabelPredictor(model: YourModelName().fritz())

  // Only show labels above a certain confidence threshold. For new models in development,
  // you may need to lower this to see predictions. As you improve your model, you can
  // increase this reduce false positives.
  let confidenceThreshold = 0.1

  private lazy var cameraLayer: AVCaptureVideoPreviewLayer = {
    let layer = AVCaptureVideoPreviewLayer(session: self.captureSession)
    layer.videoGravity = .resizeAspectFill
    return layer
  }()

  private lazy var captureSession: AVCaptureSession = {
    let session = AVCaptureSession()

    guard
      let backCamera = AVCaptureDevice.default(
        .builtInWideAngleCamera,
        for: .video,
        position: .back
      ),
      let input = try? AVCaptureDeviceInput(device: backCamera)
      else { return session }
    session.addInput(input)
    return session
  }()

  override func viewDidLoad() {
    super.viewDidLoad()
    self.cameraView?.layer.addSublayer(self.cameraLayer)
    // Setup model labels
    self.cameraView?.bringSubviewToFront(self.fpsLabel)
    self.fpsLabel.textAlignment = .center
    self.cameraView?.bringSubviewToFront(self.modelIdLabel)
    self.modelIdLabel.textAlignment = .center
    self.cameraView?.bringSubviewToFront(self.modelVersionLabel)
    self.modelVersionLabel.textAlignment = .center
    
    // Setup prediction labels
    self.cameraView?.bringSubviewToFront(self.predictionLabel)
    self.predictionLabel.textAlignment = .center
    self.cameraView?.bringSubviewToFront(self.confidenceLabel)
    self.confidenceLabel.textAlignment = .center

    // Setup video capture.
    let videoOutput = AVCaptureVideoDataOutput()
    videoOutput.setSampleBufferDelegate(self, queue: DispatchQueue(label: "MyQueue"))
    self.captureSession.addOutput(videoOutput)
    self.captureSession.startRunning()

    screenWidth = Double(view.frame.width)
    screenHeight = Double(view.frame.height)
  }

  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    cameraLayer.frame = cameraView.layer.bounds
  }

  override func viewDidLayoutSubviews() {
    super.viewDidLayoutSubviews()
    self.cameraLayer.frame = self.cameraView?.bounds ?? .zero
  }

  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
    // Dispose of any resources that can be recreated.
  }

  func updateLabels() {
    let thisExecution = Date()
    let executionTime = thisExecution.timeIntervalSince(self.lastExecution)
    let framesPerSecond: Double = 1 / executionTime
    self.lastExecution = thisExecution

    DispatchQueue.main.async {
      self.fpsLabel.text = "FPS: \(framesPerSecond.format(f: ".3"))"
      self.modelIdLabel.text = "Model ID: \(self.visionModel.managedModel.activeModelConfig.identifier)"
      self.modelVersionLabel.text = "Active Version: \(self.visionModel.managedModel.activeModelConfig.version)"
    }
  }

  func captureOutput(
    _ output: AVCaptureOutput,
    didOutput sampleBuffer: CMSampleBuffer,
    from connection: AVCaptureConnection) {
    let image = FritzVisionImage(sampleBuffer: sampleBuffer, connection: connection)
    let options = FritzVisionLabelModelOptions()
    options.threshold = confidenceThreshold


    guard let results = try? visionModel.predict(image, options: options) else { return }
    
    // Display results
    if results.count > 0 {
      let observation = results[0]
      let confidence = Int(observation.confidence * 100)
      self.setResult(text: observation.label, confidence: confidence)
    } else {
      self.setNoResult()
    }
    
    // To record predictions and send data back to Fritz AI via the Data Collection System, use the predictors's record method.
    // In addition to the input image, predicted model results can be collected as well as user-modified annotations.
    // This allows developers to both gather data on model performance and have users collect additional ground truth data for future model retraining.
    // Note, the Data Collection System is only available on paid plans.
    // visionModel.record(image, predicted: results, modified: nil)

    updateLabels()
  }
  
  private func setResult(text: String, confidence: Int) {
      DispatchQueue.main.async {
          self.predictionLabel.text = text.capitalized
          self.confidenceLabel.text = self.confidenceString(confidence)
          self.confidenceLabel.textColor = self.confidenceColor(confidence)
      }
  }

  private func setNoResult() {
      DispatchQueue.main.async {
          self.predictionLabel.text = "?????"
          self.confidenceLabel.text = ""
      }
  }

  private func confidenceString(_ value: Int) -> String {
      return "\(value)%"
  }

  private func confidenceColor(_ value: Int) -> UIColor {
      switch value {
      case ...33: return .red
      case 34...66: return .orange
      default: return .green
      }
  }
}
