//
//  VideoFilterStrategy.swift
//  FritzVisionVideoDemo
//
//  Created by Steven Yeung on 11/10/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import Foundation
import Fritz

public protocol VideoFilterStrategy {

  var title: String { get }
  var filters: [FritzVisionImageFilter] { get }
}
