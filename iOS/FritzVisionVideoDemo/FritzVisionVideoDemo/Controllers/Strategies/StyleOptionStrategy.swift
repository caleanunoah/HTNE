//
//  StyleOptionStrategy.swift
//  FritzVisionVideoDemo
//
//  Created by Steven Yeung on 11/12/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import Foundation
import Fritz

protocol StyleOptionStrategy: VideoFilterStrategy {}

extension StyleOptionStrategy {

  /// Style model options to reduce the size of the image to process and scale it back up to the input dimensions
  var styleOptions: FritzVisionStyleModelOptions {
    let options = FritzVisionStyleModelOptions()
    options.flexibleModelDimensions = FlexibleModelDimensions(width: 240, height: 426)
    options.resizeOutputToInputDimensions = true
    return options
  }
}
