//
//  ObjectPoseStrategy.swift
//  FritzVisionVideoDemo
//
//  Created by Steven Yeung on 11/8/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import Foundation
import Fritz

public class ObjectPoseStrategy: VideoFilterStrategy {

  lazy var poseModel = FritzVisionHumanPoseModelFast()
  lazy var objectModel = FritzVisionObjectModelFast()

  public var title = "Objects and Pose"
  public var filters: [FritzVisionImageFilter] {
    return [
      FritzVisionDrawSkeletonCompoundFilter(model: poseModel), // Apply first
      FritzVisionDrawBoxesCompoundFilter(model: objectModel) // Apply second
    ]
  }
}
