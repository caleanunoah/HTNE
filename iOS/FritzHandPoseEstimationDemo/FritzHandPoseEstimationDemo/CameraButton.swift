//
//  CameraButton.swift
//  FritzHandPoseEstimationDemo
//
//  Created by Steven Yeung on 11/14/19.
//  Copyright © 2019 Fritz. All rights reserved.
//

import UIKit

final class CameraButton: UIButton {
  private var buttonDimension = 70
  private var buttonOffsetX: CGFloat = 35
  private var buttonOffsetY: CGFloat = 80

  public init() {
    super.init(frame: CGRect.zero)
    self.backgroundColor = UIColor.clear
    if let titleLabel = self.titleLabel {
      titleLabel.textColor = UIColor.white
      titleLabel.font = UIFont.systemFont(ofSize: 20)
    }

    self.backgroundColor = UIColor(white: 1.0, alpha: 0.65)
    let minY = self.safeAreaLayoutGuide.layoutFrame.maxY - buttonOffsetY
    self.frame = CGRect(
      origin: CGPoint(x: UIScreen.main.bounds.midX - buttonOffsetX, y: minY),
      size: CGSize(width: buttonDimension, height: buttonDimension)
    )
    self.layer.cornerRadius = CGFloat(buttonDimension / 2)
    self.layer.borderColor = UIColor.gray.cgColor
    self.layer.borderWidth = 3
  }

  required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
  }

  /// Centers the button.
  ///
  /// - Parameters:
  ///   - x: Location on the x-axis.
  ///   - y: Location on the y-axis.
  func center(_ x: CGFloat, _ y: CGFloat) {
    self.center = CGPoint(x: x, y: y - (buttonOffsetY / 2))
  }
}
