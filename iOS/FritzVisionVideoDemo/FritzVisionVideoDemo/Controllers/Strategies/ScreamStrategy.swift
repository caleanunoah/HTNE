//
//  ScreamStrategy'.swift
//  FritzVisionVideoDemo
//
//  Created by Steven Yeung on 11/11/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import Foundation
import Fritz

public class ScreamStrategy: StyleOptionStrategy {

  lazy var screamModel = PaintingStyleModel.Style.theScream.build()

  public var title = "The Scream"
  public var filters: [FritzVisionImageFilter] {
    return [FritzVisionStylizeImageCompoundFilter(model: screamModel, options: styleOptions)]
  }
}
