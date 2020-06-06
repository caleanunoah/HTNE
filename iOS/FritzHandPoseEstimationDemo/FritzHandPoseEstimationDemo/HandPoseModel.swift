//
//  HandPoseModel.swift
//  FritzHandPoseEstimationDemo
//
//  Created by Jameson Toole on 12/13/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import Foundation
import Fritz

// Extend the model class for use with Fritz.
extension HandPose: SwiftIdentifiedModel {
  static let modelIdentifier = HandPoseModel.modelConfig.identifier
  static let packagedModelVersion = HandPoseModel.modelConfig.version
}

// Define a custom hand pose skeleton. This must match the keypoint configuration you entered in the Dataset Generator.
public enum HandSkeleton: Int, SkeletonType {
  case thumb
  case index
  case middle
  case ring
  case pinky
  
  public static let objectName = "hand"
}

@available(iOS 11.0, *)
public final class HandPoseModel: FritzVisionPosePredictor<HandSkeleton>, DownloadableModel {
  
  @objc public static let modelConfig = FritzModelConfiguration(
    identifier: "4845423b36014942b9fb274fe751e8da",
    version: 6
  )
  
  @objc public static var managedModel: FritzManagedModel {
    return modelConfig.buildManagedModel()
  }
  
  @objc public static var wifiRequiredForModelDownload: Bool = _wifiRequiredForModelDownload

  public static func fetchModel(completionHandler: @escaping (HandPoseModel?, Error?) -> Void) {
    _fetchModel(completionHandler: completionHandler)
  }
  
  public convenience init() {
    let model = HandPose().fritz().model as! FritzMLModel
    self.init(model: model)
  }
}
