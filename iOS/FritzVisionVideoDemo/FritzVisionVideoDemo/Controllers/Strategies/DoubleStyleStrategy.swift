//
//  DoubleStyleStrategy.swift
//  FritzVisionVideoDemo
//
//  Created by Steven Yeung on 11/10/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import Foundation
import Fritz

public class DoubleStyleStrategy: StyleOptionStrategy {

  lazy var kaleidoscopeModel = PatternStyleModel.Style.kaleidoscope.build()
  lazy var horsesModel = PaintingStyleModel.Style.horsesOnSeashore.build()

  public var title = "Double Style"
  public var filters: [FritzVisionImageFilter] {
    return [
      FritzVisionStylizeImageCompoundFilter(model: kaleidoscopeModel, options: styleOptions), // Apply first
      FritzVisionStylizeImageCompoundFilter(model: horsesModel, options: styleOptions) // Apply second
    ]
  }
}
