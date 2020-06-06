//
//  StylizeHairMaskFilter.swift
//  FritzVisionVideoDemo
//
//  Created by Steven Yeung on 11/8/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import UIKit

class DemoTableViewCell: UITableViewCell {

  override func awakeFromNib() {
    super.awakeFromNib()

    let selectionView = UIView()
    selectionView.backgroundColor = UIColor(red: 0.706, green: 0.133, blue: 0.133, alpha: 1)
    selectedBackgroundView = selectionView
  }
}
