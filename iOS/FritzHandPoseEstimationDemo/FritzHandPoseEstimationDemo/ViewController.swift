//
//  ViewController.swift
//  FritzStyleTransferDemo
//
//  Created by Christopher Kelly on 9/12/18.
//  Copyright © 2018 Fritz. All rights reserved.
//

import UIKit
import Photos
import Fritz


extension Double {
  func format(f: String) -> String {
    return String(format: "%\(f)f", self)
  }
}


class ViewController: UIViewController, AVCaptureVideoDataOutputSampleBufferDelegate {
  
  // Buttons for editing keypoints and submitting predictions
  @IBOutlet weak var buttonBar: UIToolbar!
  @IBOutlet weak var editButton: UIBarButtonItem!
  
  @IBOutlet weak var fpsLabel: UILabel!
  @IBOutlet weak var modelIdLabel: UILabel!
  @IBOutlet weak var modelVersionLabel: UILabel!

  /// Transmits a captured photo to Fritz.
  @IBAction func keepButtonAction(_ sender: UIBarButtonItem) {
    if let lastPrediction = lastPrediction {
      var poses: [Pose<HandSkeleton>] = []
      var modifiedPoses: [Pose<HandSkeleton>]?
      if let pose = lastPrediction.pose {
        poses.append(pose)
      }
      if let modifiedPose = lastPrediction.modifiedPose,
        lastPrediction.pose != modifiedPose {
        modifiedPoses = [modifiedPose]
      }
      poseModel.record(lastPrediction.image, predicted: poses, modified: modifiedPoses)
    }
    restartCamera()
  }

  /// Edit the keypoints on a captured photo.
  @IBAction func editButtonAction(_ sender: UIBarButtonItem) {
    guard let modifiedPose = lastPrediction?.modifiedPose else { return }
    editButton.isEnabled = false

    // Draw a draggable keypoint at each keypoint position, regardless of confidence
    for keypoint in modifiedPose.keypoints {
      let draggable = DraggableKeypoint(
        position: previewView.convertPoint(fromImagePoint: keypoint.position),
        posePart: keypoint.part
      )
      draggable.delegate = self
      previewView.addSubview(draggable)
      previewView.bringSubviewToFront(draggable)
    }
  }

  /// Discard a captured photo and restart the camera.
  @IBAction func discardButtonAction(_ sender: UIBarButtonItem) {
    restartCamera()
  }

  var previewView: UIImageView!
  
  /// The keypoint and rectangle overlay.
  var shapeLayer: CAShapeLayer!
  
  /// The button used to capture the current frame.
  var captureButton = CameraButton()

  // Model and Predictions
  lazy var poseModel = HandPoseModel()
  
  var minPoseThreshold: Double { return 0.6 }
  var keypointThrehold: Double { return 0.5 }
  
  var lastExecution = Date()
  
  /// The most recent frame and the its associated pose.
  var lastPrediction: (
    image: FritzVisionImage,
    pose: Pose<HandSkeleton>?,
    modifiedPose: Pose<HandSkeleton>?
  )?

  private lazy var captureSession: AVCaptureSession = {
    let session = AVCaptureSession()

    guard
      let backCamera = AVCaptureDevice.default(
          .builtInWideAngleCamera,
        for: .video,
        position: .back),
      let input = try? AVCaptureDeviceInput(device: backCamera)
      else { return session }
    session.addInput(input)

    session.sessionPreset = .photo
    return session
  }()

  override func viewDidLoad() {
    super.viewDidLoad()

    // Add preview View as a subview
    previewView = UIImageView(frame: view.bounds)
    previewView.contentMode = .scaleAspectFill
    previewView.isUserInteractionEnabled = true
    view.addSubview(previewView)
    
    // Setup the capture button
    captureButton.addGestureRecognizer(
      UITapGestureRecognizer(target: self, action: #selector(cameraButtonTapped))
    )
    view.addSubview(captureButton)

    // Setup the layer to plot the keypoints and rectangles.
    shapeLayer = buildRectangleLayer()
    previewView.layer.addSublayer(shapeLayer)
    let videoOutput = AVCaptureVideoDataOutput()
    videoOutput.videoSettings = [kCVPixelBufferPixelFormatTypeKey as String: kCVPixelFormatType_32BGRA as UInt32]
    videoOutput.setSampleBufferDelegate(self, queue: DispatchQueue(label: "MyQueue"))
    self.captureSession.addOutput(videoOutput)
    self.captureSession.startRunning()
    
    view.bringSubviewToFront(captureButton)
    view.layer.addSublayer(shapeLayer)
    view.bringSubviewToFront(captureButton)
    view.bringSubviewToFront(buttonBar)
    view.bringSubviewToFront(modelVersionLabel)
    view.bringSubviewToFront(modelIdLabel)
    view.bringSubviewToFront(fpsLabel)
    previewView.contentMode = .scaleAspectFill
  }
  
  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    captureButton.center(view.frame.midX, view.safeAreaLayoutGuide.layoutFrame.maxY)
  }

  override func viewWillLayoutSubviews() {
    super.viewWillLayoutSubviews()

    previewView.frame = view.bounds
  }

  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
    // Dispose of any resources that can be recreated.
  }
  
  func buildRectangleLayer() -> CAShapeLayer {
    let shape = CAShapeLayer()
    shape.opacity = 0.5
    shape.lineWidth = 2
    shape.lineJoin = .miter
    shape.strokeColor =  UIColor(hue: 0.786, saturation: 0.79, brightness: 0.53, alpha: 1.0).cgColor
    return shape
  }

  func displayInputImage(_ image: FritzVisionImage) {
    guard let image = image.rotated() else { return }

    DispatchQueue.main.async {
      self.previewView.image = image
    }
  }
  
  func captureOutput(_ output: AVCaptureOutput, didOutput sampleBuffer: CMSampleBuffer, from connection: AVCaptureConnection) {
    let image = FritzVisionImage(sampleBuffer: sampleBuffer, connection: connection)
    predict(image)
  }
}

/// Methods for handling model predictions
extension ViewController {
  
  func predict(_ image: FritzVisionImage) {
    let options = FritzVisionPoseModelOptions()
    options.minPoseThreshold = minPoseThreshold
    options.minPartThreshold = keypointThrehold

    guard let result = try? poseModel.predict(image, options: options),
      let pose = result.pose() else {
      // No pose was found, but store the the input image.
      lastPrediction = (image: image, pose: nil, modifiedPose: nil)
      // If there was no pose, display original image
      displayInputImage(image)
      return
    }
    
    updateLabels()
    
    // A pose was found, store it. There are no modifications yet so store
    // the predicted pose as the modified pose as well.
    lastPrediction = (image: image, pose: pose, modifiedPose: pose.scaled(to: image.size))

    guard let poseResult = image.draw(pose: pose) else {
      displayInputImage(image)
      return
    }

    DispatchQueue.main.async {
      self.previewView.image = poseResult
    }
  }
  
  func updateLayer(pose: Pose<HandSkeleton>) {
    DispatchQueue.main.async {
      let path = UIBezierPath()
      for keypoint in pose.keypoints {
        path.move(to: self.previewView.convertPoint(fromImagePoint: keypoint.position))
      }
      path.close()

      self.shapeLayer.path = path.cgPath
    }
  }
  
  func updateLabels() {
    let thisExecution = Date()
    let executionTime = thisExecution.timeIntervalSince(self.lastExecution)
    let framesPerSecond: Double = 1 / executionTime
    self.lastExecution = thisExecution
    
    DispatchQueue.main.async {
      self.fpsLabel.text = "FPS: \(framesPerSecond.format(f: ".3"))"
      self.modelIdLabel.text = "Model ID: \(self.poseModel.managedModel.activeModelConfig.identifier)"
      self.modelVersionLabel.text = "Active Version: \(self.poseModel.managedModel.activeModelConfig.version)"
    }
  }
}


/// Methods for changing capture state
extension ViewController {
  
  @objc func cameraButtonTapped() {
    captureSession.stopRunning()
    DispatchQueue.main.async {
      self.editButton.isEnabled = true
      self.toggleCaptureUI()
    }
  }
  
  /// Restarts the capturing state.
  func restartCamera() {
    DispatchQueue.main.async {
      self.previewView.subviews.forEach { $0.removeFromSuperview() }
      self.shapeLayer.path = nil
      self.captureSession.startRunning()
      self.toggleCaptureUI()
    }
  }
  
  /// Swaps visibility of UI elements
  func toggleCaptureUI() {
    buttonBar.isHidden = !buttonBar.isHidden
    captureButton.isHidden = !captureButton.isHidden
    fpsLabel.isHidden = !fpsLabel.isHidden
    modelVersionLabel.isHidden = !modelVersionLabel.isHidden
    modelIdLabel.isHidden = !modelIdLabel.isHidden
  }
}


/// For updating keypoint locations.
extension ViewController: DragDelegate {

  func didDrag(_ posePart: HandSkeleton, to position: CGPoint) {
    guard let prediction = lastPrediction,
      let modifiedPose = prediction.modifiedPose,
      let startingPoint = modifiedPose.getKeypoint(for: posePart)
      else { return }

    // Create a new keypoint at the new position
    let movedPoint = Keypoint<HandSkeleton>(
      index: startingPoint.index,
      position: previewView.convertPoint(fromViewPoint: position),
      score: startingPoint.score,
      part: posePart
    )

    // Replace the matching keypoint with the new keypoint
    var updatedPoints: [Keypoint<HandSkeleton>] = []
    for keypoint in modifiedPose.keypoints {
      if keypoint.part ~= posePart {
        updatedPoints.append(movedPoint)
      }
      else {
        updatedPoints.append(keypoint)
      }
    }

    // Store the new pose and redraw the view
    let updatedPose = Pose(
      keypoints: updatedPoints,
      score: modifiedPose.score,
      bounds: modifiedPose.bounds
    )
    let drawnImage = prediction.image.draw(pose: updatedPose, keypointsMeeting: keypointThrehold)
    previewView.image = drawnImage
    updateLayer(pose: updatedPose)
    lastPrediction = (image: prediction.image, pose: prediction.pose, modifiedPose: updatedPose)
  }
}
