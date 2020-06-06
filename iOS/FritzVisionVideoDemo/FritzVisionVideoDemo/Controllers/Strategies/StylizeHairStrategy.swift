//
//  StylizeHairStrategy.swift
//  FritzVisionVideoDemo
//
//  Created by Steven Yeung on 11/8/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import Foundation
import UIKit
import Fritz

public class StylizeHairStrategy: StyleOptionStrategy {

  lazy var hairModel = FritzVisionHairSegmentationModelFast()
  lazy var styleModel = PaintingStyleModel.Style.starryNight.build()

  public var title = "Stylize Hair"
  public var filters: [FritzVisionImageFilter] {
    return [
      StylizeHairMaskFilter(
        hairModel: hairModel,
        styleModel: styleModel,
        styleOptions: styleOptions
      )
    ]
  }
}
