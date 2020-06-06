//
//  StylizeBackgroundStrategy.swift
//  FritzVisionVideoDemo
//
//  Created by Steven Yeung on 11/19/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import Foundation
import Fritz

public class StylizeBackgroundStrategy: StyleOptionStrategy {

  lazy var styleModel = PaintingStyleModel.Style.ritmoPlastico.build()
  lazy var peopleModel = FritzVisionPeopleSegmentationModelFast()

  public var title = "Stylize Hair"
  public var filters: [FritzVisionImageFilter] {
    return [
      FritzVisionStylizeImageCompoundFilter(model: styleModel, options: styleOptions), // Apply first
      FritzVisionCutOutPeopleOverlayFilter(model: peopleModel) // Apply second
    ]
  }
}
