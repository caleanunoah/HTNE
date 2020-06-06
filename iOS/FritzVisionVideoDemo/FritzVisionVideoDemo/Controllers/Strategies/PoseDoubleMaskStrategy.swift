//
//  PoseDoubleMaskFilter.swift
//  FritzVisionVideoDemo
//
//  Created by Steven Yeung on 11/10/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import Foundation
import Fritz

public class PoseDoubleMaskStrategy: VideoFilterStrategy {

  lazy var poseModel = FritzVisionHumanPoseModelFast()
  lazy var peopleModel = FritzVisionPeopleSegmentationModelFast()
  lazy var hairModel = FritzVisionHairSegmentationModelFast()

  /// Segmentation options to make mask red.
  private var hairOptions: FritzVisionSegmentationMaskOptions {
    let options = FritzVisionSegmentationMaskOptions()
    options.maskColor = .red
    return options
  }

  /// Segmentation options to reduce mask alpha.
  private var maskOptions: FritzVisionSegmentationMaskOptions {
    let options = FritzVisionSegmentationMaskOptions()
    options.maxAlpha = 10
    return options
  }

  public var title = "Pose and Masks"
  public var filters: [FritzVisionImageFilter] {
    return [
      FritzVisionDrawSkeletonCompoundFilter(model: poseModel), // Apply first
      FritzVisionMaskPeopleOverlayFilter(model: peopleModel, options: maskOptions), // Apply second
      FritzVisionMaskHairOverlayFilter(model: hairModel, options: hairOptions) // Apply last
    ]
  }
}
