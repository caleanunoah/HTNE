//
//  FemmesStrategy.swift
//  FritzVisionVideoDemo
//
//  Created by Steven Yeung on 11/11/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import Foundation
import Fritz

public class FemmesStrategy: StyleOptionStrategy {

  lazy var femmesModel = PaintingStyleModel.Style.femmes.build()

  public var title = "Femmes"
  public var filters: [FritzVisionImageFilter] {
    return [FritzVisionStylizeImageCompoundFilter(model: femmesModel, options: styleOptions)]
  }
}
